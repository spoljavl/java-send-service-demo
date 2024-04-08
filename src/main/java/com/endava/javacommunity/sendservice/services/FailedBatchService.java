package com.endava.javacommunity.sendservice.services;

import  com.endava.javacommunity.sendservice.data.documents.FailedBatch;
import com.endava.javacommunity.sendservice.data.model.Batch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class FailedBatchService {

  private final ReactiveMongoTemplate mongoTemplate;

  public FailedBatchService(ReactiveMongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  public Mono<Void> addBatchDeadQueue(Batch batch) {
    return Mono.fromRunnable(() -> log.warn("Sending batch {} to dead queue", batch.getId()))
        .then(mongoTemplate.save(FailedBatch.fromBatch(batch)))
        .then();
  }

}
