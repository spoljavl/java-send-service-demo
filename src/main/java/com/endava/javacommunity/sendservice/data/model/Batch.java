package com.endava.javacommunity.sendservice.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class Batch implements Serializable {

  String id;
  String currencySymbol;
  long createdAt;
  List<Send> sends;
  int resends;

  public static Batch create(String currencySymbol) {
    return Batch.builder()
        .id(UUID.randomUUID().toString())
        .currencySymbol(currencySymbol)
        .createdAt(Instant.now().toEpochMilli())
        .sends(new ArrayList<>())
        .resends(0)
        .build();
  }

  @JsonIgnore
  public boolean isElapsed(int batchSendCycle) {
    return Duration.between(Instant.ofEpochMilli(createdAt), Instant.now()).abs().getSeconds() > batchSendCycle;
  }

  @JsonIgnore
  public void addSend(String accountId, String transactionId, String iban, BigDecimal amount, String currencySymbol) {
    this.sends.add(Send.builder()
        .accountId(accountId)
        .transactionId(transactionId)
        .iban(iban)
        .amount(amount)
        .currencySymbol(currencySymbol)
        .build());
  }

  @JsonIgnore
  public int size() {
    return this.sends.size();
  }

}
