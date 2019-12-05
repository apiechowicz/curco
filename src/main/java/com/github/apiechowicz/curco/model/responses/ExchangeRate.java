package com.github.apiechowicz.curco.model.responses;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class ExchangeRate implements Response {

    private final Currency currencyResponse;
    private final BigDecimal exchangeRate;
}
