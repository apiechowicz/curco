package com.github.apiechowicz.curco.endpoints;

import com.github.apiechowicz.curco.model.Currency;
import com.github.apiechowicz.curco.model.ExchangeRate;
import com.github.apiechowicz.curco.services.exchangerate.ExchangeRateProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/exchangeRates")
public class CurrencyExchangeRateListEndpoint {

    private final ExchangeRateProvider exchangeRateProvider;

    @Autowired
    public CurrencyExchangeRateListEndpoint(ExchangeRateProvider exchangeRateProvider) {
        this.exchangeRateProvider = exchangeRateProvider;
    }

    @PostMapping
    public ResponseEntity<List<ExchangeRate>> getExchangeRates(@RequestBody List<Currency> currencies) {
        if (currencies == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(mapToExchangeRates(currencies), HttpStatus.OK);
    }

    private List<ExchangeRate> mapToExchangeRates(List<Currency> currencies) {
        return currencies.stream()
                .map(exchangeRateProvider::provideExchangeRate)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
