package com.endava.javacommunity.sendservice.services;

import com.endava.javacommunity.sendservice.data.model.Batch;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RQueueReactive;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CacheService {

  private static final String CURRENT_BATCH = "%s_CURRENT_BATCH";
  private static final String BATCH_SEND_QUEUE = "%s_BATCH_SEND_QUEUE";

  private final RedissonReactiveClient client;

  public CacheService(RedissonReactiveClient client) {
    this.client = client;
  }

  public Mono<Batch> getCurrentBatch(String currencySymbol) {
    final RBucketReactive<Batch> currentBatch = client.getBucket(String.format(CURRENT_BATCH, currencySymbol));
    return currentBatch.get();
  }

  public Mono<Batch> getAndDeleteCurrentBatch(String currencySymbol) {
    final RBucketReactive<Batch> currentBatch = client.getBucket(String.format(CURRENT_BATCH, currencySymbol));
    return currentBatch.getAndDelete();
  }

  public Mono<Void> addCurrentBatch(Batch batch) {
    final RBucketReactive<Batch> currentBatch = client.getBucket(String.format(CURRENT_BATCH, batch.getCurrencySymbol()));
    return currentBatch.set(batch);
  }

  public Mono<Void> addToSendQueue(Batch batch) {
    final RQueueReactive<Batch> sendQueue = client.getQueue(String.format(BATCH_SEND_QUEUE, batch.getCurrencySymbol()));
    return sendQueue.offer(batch)
        .switchIfEmpty(Mono.error(new RuntimeException("Cannot add batch to send queue")))
        .then();
  }

  public Mono<Batch> removeFromSendQueue(String currencySymbol) {
    final RQueueReactive<Batch> sendQueue = client.getQueue(String.format(BATCH_SEND_QUEUE, currencySymbol));
    return sendQueue.poll();
  }

}
