package com.github.apiechowicz.curco.services;

import com.github.apiechowicz.curco.model.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CurrencyConverterService {

    public BigDecimal convertCurrencies(BigDecimal amount, Currency from, Currency to) {
        if (from == to) {
            return amount;
        }
        throw new RuntimeException("Not implemented");
    }
}
