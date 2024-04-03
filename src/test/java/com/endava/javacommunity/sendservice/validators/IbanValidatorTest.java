package com.endava.javacommunity.sendservice.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class IbanValidatorTest {

  private static IbanValidator ibanValidator;

  @BeforeAll
  static void init() {
    ibanValidator = Mockito.mock(IbanValidator.class);
  }

  @Test
  public void shouldValidateDEIban() {
    final String iban = "DE75512108001245126199";

    Mockito.when(ibanValidator.isSupportedIban(iban)).thenReturn(true);

    assertTrue(ibanValidator.isSupportedIban(iban));
  }

  @Test
  public void shouldNotValidateFakeIban() {
    final String iban = "Fake";

    Mockito.when(ibanValidator.isSupportedIban(iban)).thenReturn(false);

    assertFalse(ibanValidator.isSupportedIban(iban));
  }

}
