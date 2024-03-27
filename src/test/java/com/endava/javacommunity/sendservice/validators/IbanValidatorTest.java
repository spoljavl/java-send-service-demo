package com.endava.javacommunity.sendservice.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class IbanValidatorTest {

  @Test
  public void shouldValidateDEIban() {
    final String iban = "HR1723600001101234565";

    assertTrue(IbanValidator.isSupportedIban(iban));
  }

  @Test
  public void shouldNotValidateFakeIban() {
    final String iban = "Fake";

    assertFalse(IbanValidator.isSupportedIban(iban));
  }

}
