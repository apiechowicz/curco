package com.github.apiechowicz.curco.services.exchangerate;

import com.github.apiechowicz.curco.model.Currency;
import com.github.apiechowicz.curco.model.ExchangeRate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ExchangeRateProvider {

    private static final String URL = "http://api.nbp.pl/api/exchangerates/rates/a/%s/?format=json";

    private final RestTemplate restTemplate = new RestTemplate();

    public Optional<ExchangeRate> provideExchangeRate(Currency currency) {
        final ApiExchangeRateResponse response = restTemplate.getForObject(getUrl(currency), ApiExchangeRateResponse.class);
        return extractExchangeRate(response, currency);
    }

    private String getUrl(Currency currency) {
        return String.format(URL, currency.name().toLowerCase());
    }

    private Optional<ExchangeRate> extractExchangeRate(ApiExchangeRateResponse response, Currency currency) {
        if (response == null || response.getRates() == null || response.getRates().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(response.getRates().get(0).getMid())
                .map(rate -> new ExchangeRate(currency, rate));
    }
}
