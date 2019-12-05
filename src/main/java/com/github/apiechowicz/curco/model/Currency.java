package com.github.apiechowicz.curco.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.io.Serializable;

public enum Currency implements Serializable {
    THB,
    USD,
    AUD,
    HKD,
    CAD,
    NZD,
    SGD,
    EUR,
    HUF,
    CHF,
    GBP,
    UAH,
    JPY,
    CZK,
    DKK,
    ISK,
    NOK,
    SEK,
    HRK,
    RON,
    BGN,
    TRY,
    ILS,
    CLP,
    PHP,
    MXN,
    ZAR,
    BRL,
    MYR,
    RUB,
    IDR,
    INR,
    KRW,
    CNY,
    XDR;

    @JsonCreator
    public static Currency fromText(String text) {
        return Currency.valueOf(text.toUpperCase());
    }
}
