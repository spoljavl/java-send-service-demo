package com.endava.javacommunity.sendservice.schedulers;

import com.endava.javacommunity.sendservice.data.model.Currencies;
import com.endava.javacommunity.sendservice.mappers.CustomMapper;
import com.endava.javacommunity.sendservice.services.CacheService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class BatchTimeoutScheduler {

  private final CacheService cacheService;
  private final CustomMapper customMapper;
  private final int timeoutSeconds;

  public BatchTimeoutScheduler(CacheService cacheService, CustomMapper customMapper, @Value("${client.send.batch.timeoutSeconds}") int timeoutSeconds) {
    this.cacheService = cacheService;
    this.customMapper = customMapper;
    this.timeoutSeconds = timeoutSeconds;
  }

  @Scheduled(cron = "0/5 * * * * *")
  public void execute() {
    log.debug("triggering periodic job for current batch timeout check...");
    checkAllCurrentBatchesForTimeout().block();
    log.debug("...periodic job for current batch timeout check done");
  }

  private Mono<Void> checkAllCurrentBatchesForTimeout() {
    return Mono.just(List.of(Currencies.values()))
        .flatMapMany(Flux::fromIterable)
        .flatMap(this::checkCurrentBatchForTimeout)
        .then();
  }

  private Mono<Void> checkCurrentBatchForTimeout(Currencies currencies) {
    return cacheService.getCurrentBatch(currencies.getSymbol())
        .map(customMapper::deserializeBatch)
        .filter(batch -> batch.isElapsed(timeoutSeconds))
        .doOnNext(batch -> log.info("Batch cycle time {} elapsed. Adding current batch {} to the send queue.", timeoutSeconds, batch.getId()))
        .flatMap(batch -> cacheService.addToSendQueue(customMapper.serializeToJson(batch), batch.getCurrencySymbol())
            .then(cacheService.getAndDeleteCurrentBatch(batch.getCurrencySymbol())))
        .then();
  }

}
