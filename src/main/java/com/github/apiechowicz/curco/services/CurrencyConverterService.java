package com.github.apiechowicz.curco.services;

import com.github.apiechowicz.curco.model.daos.ConversionDao;
import com.github.apiechowicz.curco.model.daos.ExchangeRateDao;
import com.github.apiechowicz.curco.repositories.ConversionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CurrencyConverterService {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final int CALCULATION_PRECISION = 10;
    private static final int RESULT_PRECISION = 2;

    private final ConversionRepository conversionRepository;

    @Autowired
    public CurrencyConverterService(ConversionRepository conversionRepository) {
        this.conversionRepository = conversionRepository;
    }

    public BigDecimal convertCurrencies(BigDecimal amount, ExchangeRateDao from, ExchangeRateDao to) {
        if (from == to || BigDecimal.ZERO.equals(amount)) {
            return amount;
        }
        final BigDecimal conversionResult = calculateAmount(amount, from.getExchangeRate(), to.getExchangeRate());
        saveResult(amount, from, to, conversionResult);
        return conversionResult;
    }

    private BigDecimal calculateAmount(BigDecimal amount, BigDecimal firstCurrencyExchangeRate,
                                       BigDecimal secondCurrencyExchangeRate) {
        return amount.setScale(CALCULATION_PRECISION, ROUNDING_MODE)
                .multiply(firstCurrencyExchangeRate)
                .divide(secondCurrencyExchangeRate, ROUNDING_MODE)
                .setScale(RESULT_PRECISION, ROUNDING_MODE);
    }

    private ConversionDao saveResult(BigDecimal amount, ExchangeRateDao exchangeRateFrom,
                                     ExchangeRateDao exchangeRateTo, BigDecimal conversionResult) {
        return conversionRepository.save(new ConversionDao(amount, exchangeRateFrom, exchangeRateTo, conversionResult));
    }
}
