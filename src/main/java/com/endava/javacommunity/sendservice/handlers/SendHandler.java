package com.endava.javacommunity.sendservice.handlers;

import com.endava.javacommunity.sendservice.data.model.Batch;
import com.endava.javacommunity.sendservice.services.CacheService;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SendHandler {

  private final CacheService cacheService;
  private final int maxBatchSize;

  public SendHandler(CacheService cacheService, @Value("${client.send.batch.maxSize}") int maxBatchSize) {
    this.cacheService = cacheService;
    this.maxBatchSize = maxBatchSize;
  }

  public Mono<Void> addSend(String accountId, String transactionId, String iban, BigDecimal amount, String currencySymbol) {
    return Mono.fromRunnable(() -> log.info("Adding send transaction {} to batch", transactionId))
        .then(Mono.defer(() -> cacheService.getAndDeleteCurrentBatch(currencySymbol)))
        .doOnNext(currentBatch -> log.info("Current batch found with id {}", currentBatch.getId()))
        .switchIfEmpty(Mono.just(Batch.create(currencySymbol))
            .doOnNext(newCurrentBatch -> log.info("New batch created with id {}", newCurrentBatch.getId())))
        .doOnNext(batch -> batch.addSend(accountId, transactionId, iban, amount, currencySymbol))
        .doOnNext(batch -> log.info("Added new send transaction {} to batch {}", transactionId, batch.getId()))
        .flatMap(batch -> {
          if (batch.size() >= maxBatchSize) {
            log.info("Max batch size {} reached. Adding current batch with id {} to the send queue.", maxBatchSize, batch.getId());
            return cacheService.addToSendQueue(batch);
          } else {
            return cacheService.addCurrentBatch(batch);
          }
        });
  }

}
