package com.endava.javacommunity.sendservice.validators;

import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;

public class IbanValidator {

  public static boolean isSupportedIban(String iban) {
    return IBANCheckDigit.IBAN_CHECK_DIGIT.isValid(iban);
  }

}
