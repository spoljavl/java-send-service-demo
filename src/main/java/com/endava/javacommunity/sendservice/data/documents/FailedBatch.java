package com.endava.javacommunity.sendservice.data.documents;

import static com.endava.javacommunity.sendservice.data.documents.FailedBatch.COLLECTION_NAME;

import com.endava.javacommunity.sendservice.data.model.Batch;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@Builder
@Document(COLLECTION_NAME)
public class FailedBatch {

  public static final String COLLECTION_NAME = "failed-batches";

  @Indexed
  String batchId;
  @Indexed
  String currencySymbol;
  @Indexed
  long createdAt;
  List<FailedSend> failedSends;
  @Indexed
  long failedAt;

  @Value
  @Builder
  public static class FailedSend {
    String transactionId;
    String senderIban;
    String recipientIban;
    BigDecimal amount;
    String currencySymbol;
  }

  public static FailedBatch fromBatch(Batch batch) {
    return FailedBatch.builder()
        .batchId(batch.getId())
        .currencySymbol(batch.getCurrencySymbol())
        .createdAt(batch.getCreatedAt())
        .failedSends(batch.getSends().stream()
            .map(send -> FailedSend.builder()
                .transactionId(send.getTransactionId())
                .senderIban(send.getSenderIban())
                .recipientIban(send.getRecipientIban())
                .amount(send.getAmount())
                .currencySymbol(send.getCurrencySymbol())
                .build())
            .collect(Collectors.toList()))
        .failedAt(Instant.now().getEpochSecond())
        .build();
  }

}
