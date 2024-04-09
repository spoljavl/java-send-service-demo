package com.endava.javacommunity.sendservice.data.response;

import com.endava.javacommunity.sendservice.data.documents.FailedBatch;
import com.endava.javacommunity.sendservice.data.documents.FailedBatch.FailedSend;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FailedBatchResponseDto implements Serializable {

  private String batchId;
  private String currencySymbol;
  private long createdAt;
  private List<FailedSendResponseDto> failedSends;
  private int resends;
  private long failedAt;

  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @Data
  public static class FailedSendResponseDto implements Serializable {
    String transactionId;
    String senderIban;
    String recipientIban;
    BigDecimal amount;
    String currencySymbol;
  }

  public static FailedBatchResponseDto fromFailedBatch(FailedBatch failedBatch) {
    return FailedBatchResponseDto.builder()
        .batchId(failedBatch.getBatchId())
        .currencySymbol(failedBatch.getCurrencySymbol())
        .createdAt(failedBatch.getCreatedAt())
        .failedSends(failedBatch.getFailedSends().stream()
            .map(failedSend -> FailedSendResponseDto.builder()
                .transactionId(failedSend.getTransactionId())
                .senderIban(failedSend.getSenderIban())
                .recipientIban(failedSend.getRecipientIban())
                .amount(failedSend.getAmount())
                .currencySymbol(failedSend.getCurrencySymbol())
                .build())
            .collect(Collectors.toList()))
        .resends(failedBatch.getResends())
        .failedAt(Instant.now().getEpochSecond())
        .build();
  }

}
