package com.endava.javacommunity.sendservice.validators;

import java.util.Currency;

public class CurrencyValidator {

  public static boolean isSupportedCurrency(String currencySymbol) {
    try {
      return Currency.getAvailableCurrencies().contains(Currency.getInstance(currencySymbol));
    } catch (Exception e) {
      return false;
    }
  }

}
