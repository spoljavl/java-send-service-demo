package com.endava.javacommunity.sendservice.data.response;

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
public class SendConfirmationResponseDto implements Serializable {

  private boolean confirmed;
  private int numberOfConfirmedSends;
  private BigDecimal feesAmount;

}
