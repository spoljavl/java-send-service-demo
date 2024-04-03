package com.endava.javacommunity.sendservice.data.request;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SendRequestDto implements Serializable {

  private String accountId;
  private String transactionId;
  private String iban;
  private BigDecimal amount;
  private String currencySymbol;

}
