package com.endava.javacommunity.sendservice.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CurrencyValidatorTest {

  private static CurrencyValidator currencyValidator;

  @BeforeAll
  static void init() {
    currencyValidator = Mockito.mock(CurrencyValidator.class);
  }

  @Test
  public void shouldValidateEURCurrency() {
    final String currencySymbol = "EUR";

    Mockito.when(currencyValidator.isSupportedCurrency(currencySymbol)).thenReturn(true);

    assertTrue(currencyValidator.isSupportedCurrency(currencySymbol));
  }

  @Test
  public void shouldNotValidateFakeCurrency() {
    final String currencySymbol = "Fake";

    Mockito.when(currencyValidator.isSupportedCurrency(currencySymbol)).thenReturn(false);

    assertFalse(currencyValidator.isSupportedCurrency(currencySymbol));
  }

}
