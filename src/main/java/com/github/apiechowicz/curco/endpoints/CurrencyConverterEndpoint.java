package com.github.apiechowicz.curco.endpoints;

import com.github.apiechowicz.curco.model.Currency;
import com.github.apiechowicz.curco.services.CurrencyConverterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/convert")
public class CurrencyConverterEndpoint {

    private final CurrencyConverterService currencyConverterService;

    @Autowired
    public CurrencyConverterEndpoint(CurrencyConverterService currencyConverterService) {
        this.currencyConverterService = currencyConverterService;
    }

    @GetMapping
    public BigDecimal convertCurrencies(@RequestParam BigDecimal amount, @RequestParam Currency from,
                                        @RequestParam Currency to) {
        return currencyConverterService.convertCurrencies(amount, from, to);
    }
}
