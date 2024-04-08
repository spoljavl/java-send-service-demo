package com.endava.javacommunity.sendservice.services;

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

  public Mono<String> getCurrentBatch(String currencySymbol) {
    final RBucketReactive<String> currentBatch = client.getBucket(String.format(CURRENT_BATCH, currencySymbol));
    return currentBatch.get();
  }

  public Mono<String> getAndDeleteCurrentBatch(String currencySymbol) {
    final RBucketReactive<String> currentBatch = client.getBucket(String.format(CURRENT_BATCH, currencySymbol));
    return currentBatch.getAndDelete();
  }

  public Mono<Void> addCurrentBatch(String batch, String currencySymbol) {
    final RBucketReactive<String> currentBatch = client.getBucket(String.format(CURRENT_BATCH, currencySymbol));
    return currentBatch.set(batch);
  }

  public Mono<Void> addToSendQueue(String batch, String currencySymbol) {
    final RQueueReactive<String> sendQueue = client.getQueue(String.format(BATCH_SEND_QUEUE, currencySymbol));
    return sendQueue.offer(batch)
        .switchIfEmpty(Mono.error(new RuntimeException("Cannot add batch to send queue")))
        .then();
  }

  public Mono<String> removeFromSendQueue(String currencySymbol) {
    final RQueueReactive<String> sendQueue = client.getQueue(String.format(BATCH_SEND_QUEUE, currencySymbol));
    return sendQueue.poll();
  }

}
