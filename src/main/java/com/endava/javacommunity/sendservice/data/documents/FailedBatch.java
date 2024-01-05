package com.endava.javacommunity.sendservice.data.documents;

import static com.endava.javacommunity.sendservice.data.documents.FailedBatch.COLLECTION_NAME;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@Jacksonized
@AllArgsConstructor
@Builder
@Document(COLLECTION_NAME)
public class FailedBatch {

  public static final String COLLECTION_NAME = "failed-batches";

  @Id
  ObjectId id;
  @Indexed
  String batchId;
  @Indexed
  String currencySymbol;
  String createdAt;
  List<FailedSend> failedSends;
  String timestamp;

  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @Getter
  public static class FailedSend implements Serializable {
    private String accountId;
    private String transactionId;
    private String iban;
    private BigDecimal amount;
    private String currencySymbol;
  }

}
