package com.endava.javacommunity.sendservice.services;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.endava.javacommunity.sendservice.data.documents.FailedBatch;
import com.endava.javacommunity.sendservice.data.model.Batch;
import com.endava.javacommunity.sendservice.data.model.Send;
import com.endava.javacommunity.sendservice.data.response.FailedBatchResponseDto;
import com.endava.javacommunity.sendservice.mappers.CustomMapper;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class FailedBatchService {

  private final ReactiveMongoTemplate mongoTemplate;
  private final CacheService cacheService;
  private final CustomMapper customMapper;

  public FailedBatchService(ReactiveMongoTemplate mongoTemplate, CacheService cacheService, CustomMapper customMapper) {
    this.mongoTemplate = mongoTemplate;
    this.cacheService = cacheService;
    this.customMapper = customMapper;
  }

  public Mono<Void> addBatchDeadQueue(Batch batch) {
    return Mono.fromRunnable(() -> log.warn("Sending batch {} to dead queue", batch.getId()))
        .then(mongoTemplate.save(FailedBatch.fromBatch(batch)))
        .then();
  }

  public Flux<FailedBatchResponseDto> findFailedBatches(Optional<String> batchId, Optional<String> currencySymbol) {
    final Set<Criteria> criteriaSet = new HashSet<>();

    batchId.ifPresent(thisBatchId -> criteriaSet.add(new Criteria().and("batchId").is(thisBatchId)));
    currencySymbol.ifPresent(thisCurrencySymbol -> criteriaSet.add(new Criteria().and("currencySymbol").is(thisCurrencySymbol)));

    final Criteria criteria = criteriaSet.isEmpty() ? new Criteria() : new Criteria().andOperator(criteriaSet);

    return mongoTemplate.find(new Query(criteria), FailedBatch.class)
        .map(FailedBatchResponseDto::fromFailedBatch);
  }

  public Mono<Void> resendBatch(String batchId) {
    return findAndRemoveBatch(batchId)
        .flatMap(batch -> cacheService.addToSendQueue(customMapper.serializeToJson(batch), batch.getCurrencySymbol()))
        .then(Mono.fromRunnable(() -> log.info("Attempting to resend batch {}", batchId)));
  }

  private Mono<Batch> findAndRemoveBatch(String batchId) {
    final Query query = Query.query(where("batchId").is(batchId));

    return mongoTemplate.findAndRemove(query, FailedBatch.class)
        .switchIfEmpty(Mono.error(new RuntimeException(String.format("Batch with id %s not found", batchId))))
        .map(failedBatch -> Batch.builder()
            .id(failedBatch.getBatchId())
            .currencySymbol(failedBatch.getCurrencySymbol())
            .createdAt(failedBatch.getCreatedAt())
            .sends(failedBatch.getFailedSends().stream()
                .map(failedSend -> Send.builder()
                    .transactionId(failedSend.getTransactionId())
                    .senderIban(failedSend.getSenderIban())
                    .recipientIban(failedSend.getRecipientIban())
                    .amount(failedSend.getAmount())
                    .currencySymbol(failedSend.getCurrencySymbol())
                    .build())
                .collect(Collectors.toList()))
            .resends(failedBatch.getResends() + 1)
            .build());
  }

}
