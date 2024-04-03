package com.endava.javacommunity.sendservice.schedulers;

import com.endava.javacommunity.sendservice.data.model.Currencies;
import com.endava.javacommunity.sendservice.services.CacheService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class BatchSendScheduler {

  private final CacheService cacheService;

  public BatchSendScheduler(CacheService cacheService) {
    this.cacheService = cacheService;
  }

  @Scheduled(cron = "0/10 * * * * *")
  public void execute() {
    log.debug("triggering periodic job for batch send...");
    sendAllBatchesFromSendQueue().block();
    log.debug("...periodic job for batch send done");
  }

  private Mono<Void> sendAllBatchesFromSendQueue() {
    return Mono.just(List.of(Currencies.values()))
        .flatMapMany(Flux::fromIterable)
        .flatMap(this::sendBatch)
        .then();
  }

  private Mono<Void> sendBatch(Currencies currencies) {
    return cacheService.removeFromSendQueue(currencies.getSymbol())
        .doOnNext(batch -> log.info("Preparing to send {} transactions for {} for batch {}", batch.size(), batch.getCurrencySymbol(), batch.getId()))
        .flatMap(batch -> Mono.empty()); // TODO send client
  }
}
