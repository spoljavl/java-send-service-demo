package com.endava.javacommunity.sendservice.services;

import com.endava.javacommunity.sendservice.annotations.SimulateBatchConfirmationFailure;
import com.endava.javacommunity.sendservice.data.model.Batch;
import com.endava.javacommunity.sendservice.data.response.SendConfirmationResponseDto;
import com.endava.javacommunity.sendservice.mappers.CustomMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class SendConfirmationService {

  private static final int FEE_AMOUNT = 5;

  private final CustomMapper customMapper;

  public SendConfirmationService(CustomMapper customMapper) {
    this.customMapper = customMapper;
  }

  @SimulateBatchConfirmationFailure(amount = 8.88, resends = 0)
  public Mono<SendConfirmationResponseDto> confirmBatchedSend(String batchString) {
    return Mono.just(customMapper.deserializeBatch(batchString))
        .flatMap(this::confirmBatch);
  }

  private Mono<SendConfirmationResponseDto> confirmBatch(Batch batch) {
    return Mono.fromRunnable(() -> log.info("Batch confirmation for batch {}", batch.getId()))
        .then(Mono.fromRunnable(() -> log.info("Successful confirmation for batch {}", batch.getId())))
        .thenReturn(createSendConfirmationResponseDto(batch.size()));
  }

  private static SendConfirmationResponseDto createSendConfirmationResponseDto(int batchSize) {
    return SendConfirmationResponseDto.builder()
        .confirmed(true)
        .numberOfConfirmedSends(batchSize)
        .feesAmount(new BigDecimal(FEE_AMOUNT).divide(new BigDecimal(batchSize), 2, RoundingMode.HALF_UP))
        .build();
  }

}
