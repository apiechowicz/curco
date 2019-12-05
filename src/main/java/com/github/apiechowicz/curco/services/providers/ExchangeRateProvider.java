package com.github.apiechowicz.curco.services.providers;

import com.github.apiechowicz.curco.model.api.ApiExchangeRateResponse;
import com.github.apiechowicz.curco.model.daos.CurrencyDao;
import com.github.apiechowicz.curco.model.daos.ExchangeRateDao;
import com.github.apiechowicz.curco.repositories.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExchangeRateProvider {

    private static final int REFRESH_INTERVAL_IN_HOURS = 12;

    private final ApiDataProvider apiDataProvider;
    private final ExchangeRateRepository exchangeRateRepository;

    @Autowired
    public ExchangeRateProvider(ApiDataProvider apiDataProvider, ExchangeRateRepository exchangeRateRepository) {
        this.apiDataProvider = apiDataProvider;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public Optional<ExchangeRateDao> findCurrentExchangeRate(CurrencyDao currencyFrom) {
        final List<ExchangeRateDao> allExchangeRates = findAllExchangeRatesSortedDescending(currencyFrom);
        if (allExchangeRates.isEmpty()) {
            return requestNewExchangeRate(currencyFrom);
        }
        final ExchangeRateDao mostUpToDateExchangeRate = allExchangeRates.get(0);
        if (isExchangeRateTooOld(mostUpToDateExchangeRate)) {
            return requestNewExchangeRate(currencyFrom);
        }
        return Optional.of(mostUpToDateExchangeRate);
    }

    private List<ExchangeRateDao> findAllExchangeRatesSortedDescending(CurrencyDao currency) {
        return exchangeRateRepository.findAllByCurrency(currency)
                .stream()
                .sorted(Comparator.comparing(ExchangeRateDao::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    private Optional<ExchangeRateDao> requestNewExchangeRate(CurrencyDao currency) {
        final Optional<ApiExchangeRateResponse> apiExchangeRateResponse = apiDataProvider.requestExchangeRate(currency);
        if (!apiExchangeRateResponse.isPresent()) {
            return Optional.empty();
        }
        final List<ApiExchangeRateResponse.Rate> receivedRates = apiExchangeRateResponse.get().getRates();
        if (receivedRates == null || receivedRates.isEmpty()) {
            return Optional.empty();
        }
        final ApiExchangeRateResponse.Rate rate = receivedRates.get(0);
        if (rate == null || rate.getMid() == null) {
            return Optional.empty();
        }
        final ExchangeRateDao newExchangeRate = exchangeRateRepository.save(new ExchangeRateDao(currency, rate.getMid()));
        return Optional.of(newExchangeRate);
    }

    private boolean isExchangeRateTooOld(ExchangeRateDao mostUpToDateExchangeRate) {
        final LocalDateTime now = LocalDateTime.now();
        final Duration duration = Duration.between(mostUpToDateExchangeRate.getDateTime(), now);
        return duration.toHours() >= REFRESH_INTERVAL_IN_HOURS;
    }
}
