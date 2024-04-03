package com.endava.javacommunity.sendservice.data.model;

import lombok.Getter;

@Getter
public enum Currencies {

  EUR("EUR", "euro", "978"),
  CHF("CHF", "swiss franc", "756"),
  GBP("GBP", "pound sterling", "826"),
  USD("USD", "american dollar", "840");

  private final String symbol;
  private final String name;
  private final String code;

  Currencies(String symbol, String name, String code) {
    this.symbol = symbol;
    this.name = name;
    this.code = code;
  }

}
