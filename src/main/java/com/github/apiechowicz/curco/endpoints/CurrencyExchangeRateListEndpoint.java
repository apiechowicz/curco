package com.github.apiechowicz.curco.endpoints;

import com.github.apiechowicz.curco.converters.ExchangeRateConverter;
import com.github.apiechowicz.curco.model.daos.CurrencyDao;
import com.github.apiechowicz.curco.model.daos.ExchangeRateDao;
import com.github.apiechowicz.curco.model.responses.ExchangeRate;
import com.github.apiechowicz.curco.services.providers.CurrencyProvider;
import com.github.apiechowicz.curco.services.providers.ExchangeRateProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/exchangeRates")
public class CurrencyExchangeRateListEndpoint {

    private static final ResponseEntity<List<ExchangeRate>> BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    private static final ResponseEntity<List<ExchangeRate>> SERVICE_UNAVAILABLE =
            new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);

    private final CurrencyProvider currencyProvider;
    private final ExchangeRateProvider exchangeRateProvider;
    private final ExchangeRateConverter exchangeRateConverter;

    @Autowired
    public CurrencyExchangeRateListEndpoint(CurrencyProvider currencyProvider,
                                            ExchangeRateProvider exchangeRateProvider,
                                            ExchangeRateConverter exchangeRateConverter) {
        this.currencyProvider = currencyProvider;
        this.exchangeRateProvider = exchangeRateProvider;
        this.exchangeRateConverter = exchangeRateConverter;
    }

    @PostMapping
    public ResponseEntity<List<ExchangeRate>> getExchangeRates(@RequestBody List<String> currencyCodeNames) {
        if (currencyCodeNames == null) {
            return BAD_REQUEST;
        }
        final Optional<List<CurrencyDao>> currencies = currencyProvider.getAllCurrencies();
        if (!currencies.isPresent()) {
            return SERVICE_UNAVAILABLE;
        }
        final Optional<List<CurrencyDao>> requestedCurrencies = findCorrespondingCurrencies(currencyCodeNames,
                currencies.get());
        if (!requestedCurrencies.isPresent()) {
            return BAD_REQUEST;
        }
        final List<ExchangeRateDao> currentExchangeRates = findCurrentExchangeRates(requestedCurrencies.get());
        if (currentExchangeRates.size() == currencyCodeNames.size()) {
            final List<ExchangeRate> exchangeRates = exchangeRateConverter.convertDaosToResponses(currentExchangeRates);
            return new ResponseEntity<>(exchangeRates, HttpStatus.OK);
        }
        return SERVICE_UNAVAILABLE;
    }

    private List<ExchangeRateDao> findCurrentExchangeRates(List<CurrencyDao> requestedCurrencies) {
        return requestedCurrencies.stream()
                .map(exchangeRateProvider::findCurrentExchangeRate)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<List<CurrencyDao>> findCorrespondingCurrencies(List<String> currencyCodeNames,
                                                                    List<CurrencyDao> currencyDaos) {
        final Map<String, CurrencyDao> codeNameToCurrency = currencyDaos.stream()
                .collect(Collectors.toMap(currency -> currency.getCodeName().toLowerCase(), currency -> currency));
        final List<CurrencyDao> foundCurrencies = currencyCodeNames.stream()
                .map(codeName -> codeNameToCurrency.get(codeName.toLowerCase()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return currencyCodeNames.size() == foundCurrencies.size() ? Optional.of(foundCurrencies) : Optional.empty();
    }
}
