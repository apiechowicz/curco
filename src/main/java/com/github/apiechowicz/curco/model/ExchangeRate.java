package com.github.apiechowicz.curco.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public final class ExchangeRate {

    private final Currency currency;
    private final BigDecimal value;
}
