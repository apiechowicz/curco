package com.github.apiechowicz.curco.services;

import com.github.apiechowicz.curco.model.Currency;
import com.github.apiechowicz.curco.model.ExchangeRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class CurrencyConverterService {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final int CALCULATION_PRECISION = 10;
    private static final int RESULT_PRECISION = 2;

    private final ExchangeRateProvider exchangeRateProvider;

    @Autowired
    public CurrencyConverterService(ExchangeRateProvider exchangeRateProvider) {
        this.exchangeRateProvider = exchangeRateProvider;
    }

    public Optional<BigDecimal> convertCurrencies(BigDecimal amount, Currency from, Currency to) {
        if (from == to || BigDecimal.ZERO.equals(amount)) {
            return Optional.of(amount);
        }
        final Optional<ExchangeRate> firstCurrencyExchangeRate = exchangeRateProvider.provideExchangeRate(from);
        if (!firstCurrencyExchangeRate.isPresent()) {
            return Optional.empty();
        }
        final Optional<ExchangeRate> secondCurrencyExchangeRate = exchangeRateProvider.provideExchangeRate(to);
        return secondCurrencyExchangeRate
                .map(exchangeRate -> calculateAmount(amount, firstCurrencyExchangeRate.get(), exchangeRate));
    }

    private BigDecimal calculateAmount(BigDecimal amount, ExchangeRate firstCurrencyExchangeRate,
                                       ExchangeRate secondCurrencyExchangeRate) {
        return amount.setScale(CALCULATION_PRECISION, ROUNDING_MODE)
                .multiply(firstCurrencyExchangeRate.getValue())
                .divide(secondCurrencyExchangeRate.getValue(), ROUNDING_MODE)
                .setScale(RESULT_PRECISION, ROUNDING_MODE);
    }
}
