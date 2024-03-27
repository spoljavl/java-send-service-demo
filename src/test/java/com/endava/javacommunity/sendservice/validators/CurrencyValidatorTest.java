package com.endava.javacommunity.sendservice.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CurrencyValidatorTest {

  @Test
  public void shouldValidateEURCurrency() {
    final String currencySymbol = "EUR";

    assertTrue(CurrencyValidator.isSupportedCurrency(currencySymbol));
  }

  @Test
  public void shouldNotValidateFakeCurrency() {
    final String currencySymbol = "Fake";

    assertFalse(CurrencyValidator.isSupportedCurrency(currencySymbol));
  }

}
