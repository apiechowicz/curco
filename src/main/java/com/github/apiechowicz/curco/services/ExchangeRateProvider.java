package com.github.apiechowicz.curco.services;

import com.github.apiechowicz.curco.model.Currency;
import com.github.apiechowicz.curco.model.ExchangeRate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExchangeRateProvider {

    public Optional<ExchangeRate> provideExchangeRate(Currency currency) {
        throw new RuntimeException("Not implemented");
    }
}
