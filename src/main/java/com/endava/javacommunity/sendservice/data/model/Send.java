package com.endava.javacommunity.sendservice.data.model;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class Send implements Serializable {

  String transactionId;
  String senderIban;
  String recipientIban;
  BigDecimal amount;
  String currencySymbol;

}
