package com.endava.javacommunity.sendservice.validators;

import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;
import org.springframework.stereotype.Component;

@Component
public class IbanValidator {

  public boolean isSupportedIban(String iban) {
    return IBANCheckDigit.IBAN_CHECK_DIGIT.isValid(iban);
  }

}
