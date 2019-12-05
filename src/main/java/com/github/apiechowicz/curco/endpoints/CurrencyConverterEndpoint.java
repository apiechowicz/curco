package com.github.apiechowicz.curco.endpoints;

import com.github.apiechowicz.curco.model.daos.CurrencyDao;
import com.github.apiechowicz.curco.model.daos.ExchangeRateDao;
import com.github.apiechowicz.curco.services.CurrencyConverterService;
import com.github.apiechowicz.curco.services.providers.CurrencyProvider;
import com.github.apiechowicz.curco.services.providers.ExchangeRateProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/convert")
public class CurrencyConverterEndpoint {

    private static final ResponseEntity<BigDecimal> BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    private static final ResponseEntity<BigDecimal> SERVICE_UNAVAILABLE =
            new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);

    private final CurrencyProvider currencyProvider;
    private final ExchangeRateProvider exchangeRateProvider;
    private final CurrencyConverterService currencyConverterService;

    @Autowired
    public CurrencyConverterEndpoint(CurrencyProvider currencyProvider, ExchangeRateProvider exchangeRateProvider,
                                     CurrencyConverterService currencyConverterService) {
        this.currencyProvider = currencyProvider;
        this.exchangeRateProvider = exchangeRateProvider;
        this.currencyConverterService = currencyConverterService;
    }

    @GetMapping
    public ResponseEntity<BigDecimal> convertCurrencies(@RequestParam BigDecimal amount, @RequestParam String from,
                                                        @RequestParam String to) {
        if (isAnyParameterNull(amount, from, to)) {
            return BAD_REQUEST;
        }
        final Optional<CurrencyDao> currencyFrom = currencyProvider.getByCodeName(from);
        if (!currencyFrom.isPresent()) {
            return BAD_REQUEST;
        }
        final Optional<CurrencyDao> currencyTo = currencyProvider.getByCodeName(to);
        if (!currencyTo.isPresent()) {
            return BAD_REQUEST;
        }
        final Optional<ExchangeRateDao> exchangeRateFrom = exchangeRateProvider
                .findCurrentExchangeRate(currencyFrom.get());
        if (!exchangeRateFrom.isPresent()) {
            return SERVICE_UNAVAILABLE;
        }
        return exchangeRateProvider.findCurrentExchangeRate(currencyTo.get())
                .map(exchangeRateTo -> createResponse(amount, exchangeRateFrom.get(), exchangeRateTo))
                .orElse(SERVICE_UNAVAILABLE);
    }

    private boolean isAnyParameterNull(BigDecimal amount, String from, String to) {
        return amount == null || from == null || to == null;
    }

    private ResponseEntity<BigDecimal> createResponse(BigDecimal amount, ExchangeRateDao exchangeRateFrom,
                                                      ExchangeRateDao exchangeRateDao) {
        return new ResponseEntity<>(currencyConverterService.convertCurrencies(amount, exchangeRateFrom,
                exchangeRateDao), HttpStatus.OK);
    }
}
