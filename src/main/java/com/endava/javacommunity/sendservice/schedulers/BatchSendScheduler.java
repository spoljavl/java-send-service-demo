package com.endava.javacommunity.sendservice.schedulers;

import com.endava.javacommunity.sendservice.clients.SendConfirmationWebClient;
import com.endava.javacommunity.sendservice.data.model.Currencies;
import com.endava.javacommunity.sendservice.data.response.SendConfirmationResponseDto;
import com.endava.javacommunity.sendservice.exceptions.ServiceUnavailableException;
import com.endava.javacommunity.sendservice.mappers.CustomMapper;
import com.endava.javacommunity.sendservice.services.CacheService;
import com.endava.javacommunity.sendservice.services.FailedBatchService;
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
  private final CustomMapper customMapper;
  private final SendConfirmationWebClient sendConfirmationWebClient;
  private final FailedBatchService failedBatchService;

  public BatchSendScheduler(CacheService cacheService, CustomMapper customMapper, SendConfirmationWebClient sendConfirmationWebClient,
      FailedBatchService failedBatchService) {
    this.cacheService = cacheService;
    this.customMapper = customMapper;
    this.sendConfirmationWebClient = sendConfirmationWebClient;
    this.failedBatchService = failedBatchService;
  }

  @Scheduled(cron = "${client.send.batch.sendSchedulerCron}")
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
        .map(customMapper::deserializeBatch)
        .doOnNext(batch -> log.info("Preparing to send {} transactions for {} for batch {}", batch.size(), batch.getCurrencySymbol(), batch.getId()))
        .flatMap(batch -> sendConfirmationWebClient.batchedSend(batch)
            .doOnError(throwable -> log.error("Error sending {} transactions for {} for batch {}", batch.size(), batch.getCurrencySymbol(),
                batch.getId()))
            .onErrorResume(ServiceUnavailableException.class, throwable -> failedBatchService.addBatchDeadQueue(batch)
                .thenReturn(SendConfirmationResponseDto.builder().confirmed(false).build()))
            .doOnNext(response -> log.info("Results for {} for batch {}: [confirmed: {}; numberOfConfirmedSends: {}; fees: {}]",
                batch.getCurrencySymbol(), batch.getId(), response.isConfirmed(), response.getNumberOfConfirmedSends(), response.getFeesAmount())))
        .then();
  }
}
