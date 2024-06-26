package com.endava.javacommunity.sendservice.data.request;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SendConfirmationRequestDto implements Serializable {

  private String batchString;

}
