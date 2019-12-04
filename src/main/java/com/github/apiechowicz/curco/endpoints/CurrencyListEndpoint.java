package com.github.apiechowicz.curco.endpoints;

import com.github.apiechowicz.curco.model.Currency;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/list")
public class CurrencyListEndpoint {

    private static final List<Currency> AVAILABLE_CURRENCIES = Arrays.asList(Currency.values());

    @GetMapping
    public List<Currency> getAvailableCurrencies() {
        return AVAILABLE_CURRENCIES;
    }
}
