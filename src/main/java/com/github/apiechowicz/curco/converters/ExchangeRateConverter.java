package com.github.apiechowicz.curco.converters;

import com.github.apiechowicz.curco.model.daos.ExchangeRateDao;
import com.github.apiechowicz.curco.model.responses.Currency;
import com.github.apiechowicz.curco.model.responses.ExchangeRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRateConverter implements DaoToResponseConverter<ExchangeRateDao, ExchangeRate> {

    private final CurrencyConverter currencyConverter;

    @Autowired
    public ExchangeRateConverter(CurrencyConverter currencyConverter) {
        this.currencyConverter = currencyConverter;
    }

    @Override
    public ExchangeRate convertDaoToResponse(ExchangeRateDao exchangeRateDao) {
        final Currency currencyResponse = currencyConverter.convertDaoToResponse(exchangeRateDao.getCurrency());
        return new ExchangeRate(currencyResponse, exchangeRateDao.getExchangeRate());
    }
}
