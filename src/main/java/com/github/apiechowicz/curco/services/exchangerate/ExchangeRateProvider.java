package com.github.apiechowicz.curco.services.exchangerate;

import com.github.apiechowicz.curco.model.Currency;
import com.github.apiechowicz.curco.model.ExchangeRate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Optional;

@Service
public class ExchangeRateProvider {

    private static final Duration REFRESH_INTERVAL = Duration.ofHours(1);
    private static final String REQUEST_PATH_TEMPLATE = "/rates/a/%s";
    private static final String RESPONSE_FORMAT = "/?format=json";

    private final RestTemplate restTemplate;
    private final CacheLoader<Currency, Optional<ExchangeRate>> cacheLoader =
            new CacheLoader<Currency, Optional<ExchangeRate>>() {
                @Override
                public Optional<ExchangeRate> load(Currency currency) {
                    return requestCurrencyExchangeRate(currency);
                }
            };
    private final LoadingCache<Currency, Optional<ExchangeRate>> cache = CacheBuilder.newBuilder()
            .refreshAfterWrite(REFRESH_INTERVAL)
            .build(cacheLoader);

    @Value("${api.url}")
    private String apiUrl;

    @Autowired
    public ExchangeRateProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<ExchangeRate> provideExchangeRate(Currency currency) {
        final Optional<ExchangeRate> exchangeRate = cache.getUnchecked(currency);
        if (!exchangeRate.isPresent()) {
            cache.refresh(currency);
        }
        return cache.getUnchecked(currency);
    }

    private Optional<ExchangeRate> requestCurrencyExchangeRate(Currency currency) {
        final String url = createRequestUrl(currency);
        final ApiExchangeRateResponse response = restTemplate.getForObject(url, ApiExchangeRateResponse.class);
        return extractExchangeRate(response, currency);
    }

    private String createRequestUrl(Currency currency) {
        final String requestPath = String.format(REQUEST_PATH_TEMPLATE, currency.name().toLowerCase());
        return String.format("%s%s%s", apiUrl, requestPath, RESPONSE_FORMAT);
    }

    private Optional<ExchangeRate> extractExchangeRate(ApiExchangeRateResponse response, Currency currency) {
        if (response == null || response.getRates() == null || response.getRates().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(response.getRates().get(0).getMid())
                .map(rate -> new ExchangeRate(currency, rate));
    }
}
