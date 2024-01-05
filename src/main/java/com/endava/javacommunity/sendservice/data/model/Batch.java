package com.endava.javacommunity.sendservice.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Batch implements Serializable {

  private String id;
  private String currencySymbol;
  private long createdAt;
  private List<Send> sends;

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
