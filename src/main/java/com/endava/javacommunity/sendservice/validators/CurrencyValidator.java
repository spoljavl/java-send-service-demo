package com.endava.javacommunity.sendservice.validators;

import com.endava.javacommunity.sendservice.data.model.Currencies;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CurrencyValidator {

  private final Set<Currencies> supportedCurrencies;

  public CurrencyValidator(@Value("${client.supportedCurrencies}") String[] supportedCurrencies) {
    this.supportedCurrencies = Arrays.stream(supportedCurrencies)
        .map(String::toUpperCase)
        .map(Currencies::valueOf)
        .collect(Collectors.toSet());
  }

  public boolean isSupportedCurrency(String currencySymbol) {
    try {
      return supportedCurrencies.contains(Currencies.valueOf(currencySymbol));
    } catch (Exception e) {
      return false;
    }
  }

}
