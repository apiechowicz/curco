package com.github.apiechowicz.curco.endpoints;

import com.github.apiechowicz.curco.converters.CurrencyConverter;
import com.github.apiechowicz.curco.model.responses.Currency;
import com.github.apiechowicz.curco.services.providers.CurrencyProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/list")
public class CurrencyListEndpoint {

    private final CurrencyProvider currencyProvider;
    private final CurrencyConverter currencyConverter;

    @Autowired
    public CurrencyListEndpoint(CurrencyProvider currencyProvider, CurrencyConverter currencyConverter) {
        this.currencyProvider = currencyProvider;
        this.currencyConverter = currencyConverter;
    }

    @GetMapping
    public ResponseEntity<List<Currency>> getAvailableCurrencies() {
        return currencyProvider.getAllCurrencies()
                .map(daos -> new ResponseEntity<>(currencyConverter.convertDaosToResponses(daos), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));
    }
}
