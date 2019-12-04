package com.github.apiechowicz.curco.endpoints;

import com.github.apiechowicz.curco.model.Currency;
import com.github.apiechowicz.curco.services.CurrencyConverterService;
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

    private final CurrencyConverterService currencyConverterService;

    @Autowired
    public CurrencyConverterEndpoint(CurrencyConverterService currencyConverterService) {
        this.currencyConverterService = currencyConverterService;
    }

    @GetMapping
    public ResponseEntity<BigDecimal> convertCurrencies(@RequestParam BigDecimal amount, @RequestParam Currency from,
                                                        @RequestParam Currency to) {
        if (isAnyParameterNull(amount, from, to)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final Optional<BigDecimal> conversionResult = currencyConverterService.convertCurrencies(amount, from, to);
        return conversionResult.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));
    }

    private boolean isAnyParameterNull(BigDecimal amount, Currency from, Currency to) {
        return amount == null || from == null || to == null;
    }
}
