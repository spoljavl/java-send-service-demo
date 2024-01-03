package com.endava.javacommunity.sendservice.data.model;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Send implements Serializable {

  private String accountId;
  private String transactionId;
  private String iban;
  private BigDecimal amount;
  private String currencySymbol;

}
