package com.github.apiechowicz.curco.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.io.Serializable;

public enum Currency implements Serializable {
    USD,
    CHF;

    @JsonCreator
    public static Currency fromText(String text) {
        return Currency.valueOf(text.toUpperCase());
    }
}
