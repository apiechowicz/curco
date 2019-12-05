package com.github.apiechowicz.curco.services.providers;

import com.github.apiechowicz.curco.model.daos.CurrencyDao;
import com.github.apiechowicz.curco.repositories.CurrencyRepository;
import com.github.apiechowicz.curco.services.DatabaseUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CurrencyProvider {

    private final CurrencyRepository currencyRepository;
    private final DatabaseUpdater databaseUpdater;

    @Autowired
    public CurrencyProvider(CurrencyRepository currencyRepository, DatabaseUpdater databaseUpdater) {
        this.currencyRepository = currencyRepository;
        this.databaseUpdater = databaseUpdater;
    }

    public Optional<List<CurrencyDao>> getAllCurrencies() {
        final List<CurrencyDao> availableCurrencies = getCurrencies();
        if (availableCurrencies.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(availableCurrencies);
    }

    public Optional<CurrencyDao> getByCodeName(String codeName) {
        final List<CurrencyDao> availableCurrencies = getCurrencies();
        return availableCurrencies.stream()
                .filter(currency -> codeNameMatches(codeName, currency))
                .findAny();
    }

    private List<CurrencyDao> getCurrencies() {
        final List<CurrencyDao> availableCurrencies = currencyRepository.findAll();
        if (availableCurrencies.isEmpty()) {
            databaseUpdater.updateCurrenciesAndExchangeRates();
            return currencyRepository.findAll();
        }
        return availableCurrencies;
    }

    private boolean codeNameMatches(String codeName, CurrencyDao currency) {
        return currency.getCodeName().toLowerCase().equals(codeName.toLowerCase());
    }
}
