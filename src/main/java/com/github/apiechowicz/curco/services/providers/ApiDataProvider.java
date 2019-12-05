package com.github.apiechowicz.curco.services.providers;

import com.github.apiechowicz.curco.model.daos.ApiTable;
import com.github.apiechowicz.curco.model.daos.CurrencyDao;
import com.github.apiechowicz.curco.model.api.ApiExchangeRateResponse;
import com.github.apiechowicz.curco.model.api.ApiTableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ApiDataProvider {

    private static final String REQUEST_EXCHANGE_RATE_TEMPLATE = "/rates/%s/%s";
    private static final String REQUEST_TABLE_TEMPLATE = "/tables/%s";
    private static final String RESPONSE_FORMAT = "/?format=json";

    private final RestTemplate restTemplate;

    @Value("${api.url}")
    private String apiUrl;

    @Autowired
    public ApiDataProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<ApiTableResponse> requestTable(ApiTable table) {
        final String url = createTableRequestUrl(table);
        return Optional.ofNullable(restTemplate.getForObject(url, ApiTableResponse[].class))
                .map(tables -> {
                    if (tables.length == 1) {
                        return tables[0];
                    }
                    return null;
                });
    }

    public Optional<ApiExchangeRateResponse> requestExchangeRate(CurrencyDao currencyDao) {
        final String url = createExchangeRateRequestUrl(currencyDao);
        return Optional.ofNullable(restTemplate.getForObject(url, ApiExchangeRateResponse.class));
    }

    private String createTableRequestUrl(ApiTable table) {
        final String requestPath = String.format(REQUEST_TABLE_TEMPLATE, table.name().toLowerCase());
        return String.format("%s%s%s", apiUrl, requestPath, RESPONSE_FORMAT);
    }

    private String createExchangeRateRequestUrl(CurrencyDao currencyDao) {
        final String requestPath = String.format(REQUEST_EXCHANGE_RATE_TEMPLATE,
                currencyDao.getApiTable().name().toLowerCase(), currencyDao.getCodeName().toLowerCase());
        return String.format("%s%s%s", apiUrl, requestPath, RESPONSE_FORMAT);
    }
}
