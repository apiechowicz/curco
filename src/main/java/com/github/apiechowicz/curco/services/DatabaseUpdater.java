package com.github.apiechowicz.curco.services;

import com.github.apiechowicz.curco.model.api.ApiTableResponse;
import com.github.apiechowicz.curco.model.daos.ApiTable;
import com.github.apiechowicz.curco.model.daos.CurrencyDao;
import com.github.apiechowicz.curco.model.daos.ExchangeRateDao;
import com.github.apiechowicz.curco.repositories.CurrencyRepository;
import com.github.apiechowicz.curco.repositories.ExchangeRateRepository;
import com.github.apiechowicz.curco.services.providers.ApiDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class DatabaseUpdater {

    private final ApiDataProvider apiDataProvider;
    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;

    @Autowired
    public DatabaseUpdater(ApiDataProvider apiDataProvider, CurrencyRepository currencyRepository,
                           ExchangeRateRepository exchangeRateRepository) {
        this.apiDataProvider = apiDataProvider;
        this.currencyRepository = currencyRepository;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public void updateCurrenciesAndExchangeRates() {
        Arrays.stream(ApiTable.values())
                .forEach(this::requestTableDataAndUpdateDb);
    }

    private void requestTableDataAndUpdateDb(ApiTable apiTable) {
        apiDataProvider.requestTable(apiTable).ifPresent(apiTableResponse -> {
            final List<ApiTableResponse.Rate> rates = apiTableResponse.getRates();
            if (rates != null) {
                updateAvailableCurrencies(rates, apiTable);
            }
        });
    }

    private void updateAvailableCurrencies(List<ApiTableResponse.Rate> rates, ApiTable apiTable) {
        final List<CurrencyDao> currencies = new ArrayList<>();
        final List<ExchangeRateDao> exchangeRates = new ArrayList<>();
        for (ApiTableResponse.Rate rate : rates) {
            if (rate != null) {
                rateToCurrencyOptional(rate, apiTable).ifPresent(currency -> {
                    currencies.add(currency);
                    if (rate.getMid() != null) {
                        exchangeRates.add(new ExchangeRateDao(currency, rate.getMid()));
                    }
                });
            }
        }
        currencyRepository.saveAll(currencies);
        exchangeRateRepository.saveAll(exchangeRates);
    }

    private Optional<CurrencyDao> rateToCurrencyOptional(ApiTableResponse.Rate rate, ApiTable apiTable) {
        if (rate.getCode() == null || rate.getCurrency() == null) {
            return Optional.empty();
        }
        return Optional.of(new CurrencyDao(rate.getCode(), rate.getCurrency(), apiTable));
    }
}
