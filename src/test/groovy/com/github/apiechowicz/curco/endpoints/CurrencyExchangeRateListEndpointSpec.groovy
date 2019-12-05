package com.github.apiechowicz.curco.endpoints

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.apiechowicz.curco.model.Currency
import com.github.apiechowicz.curco.model.ExchangeRate
import com.github.apiechowicz.curco.services.exchangerate.ExchangeRateProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.lang.Unroll
import spock.mock.DetachedMockFactory

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@AutoConfigureMockMvc
@WebMvcTest(controllers = CurrencyExchangeRateListEndpoint)
class CurrencyExchangeRateListEndpointSpec extends Specification {

    private static final String URL = '/exchangeRates'
    private static final TypeReference TYPE = new TypeReference<List<ExchangeRate>>() {}

    @Autowired
    private MockMvc mvc

    @Autowired
    private ExchangeRateProvider exchangeRateProvider

    @Autowired
    private ObjectMapper objectMapper

    @Unroll
    def "returns list of exchange rates for #currencies"(List<Currency> currencies) {
        when: 'request for list of exchange rates is performed with valid argument'
        MockHttpServletResponse response = mvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(currencies))
        ).andReturn()
                .response

        then: 'exchange rate provider will be queried for every currency in request'
        currencies.size() * exchangeRateProvider.provideExchangeRate(_) >> { arguments ->
            createExchangeRate(arguments[0] as Currency)
        }
        and: 'response will not be null'
        response != null
        and: 'ok status will be returned'
        response.status == HttpStatus.OK.value()
        and: 'every currency from the request will be present in response'
        List<ExchangeRate> exchangeRates = objectMapper.readValue(response.contentAsString, TYPE)
        exchangeRates.size() == currencies.size()
        exchangeRates.every { currencies.contains(it.currency) }

        where:
        currencies                   | _
        []                           | _
        [Currency.CHF]               | _
        [Currency.CHF, Currency.USD] | _
    }

    def "returns bad request when no currency list is provided"() {
        when: 'request for list of exchange rates is performed without valid argument'
        MockHttpServletResponse response = mvc.perform(post(URL))
                .andReturn()
                .response

        then: 'response will not be null'
        response != null
        and: 'bad request status will be returned'
        response.status == HttpStatus.BAD_REQUEST.value()
    }

    @Unroll
    def "supports #caseName case currency codes"(String caseName, List<String> currencies) {
        when: 'request for list of exchange rates is performed with valid argument'
        MockHttpServletResponse response = mvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(currencies))
        ).andReturn()
                .response

        then: 'exchange rate provider will be queried for every currency in request'
        currencies.size() * exchangeRateProvider.provideExchangeRate(_) >> { arguments ->
            createExchangeRate(arguments[0] as Currency)
        }
        and: 'response will not be null'
        response != null
        and: 'ok status will be returned'
        response.status == HttpStatus.OK.value()
        and: 'every currency from the request will be present in response'
        List<ExchangeRate> exchangeRates = objectMapper.readValue(response.contentAsString, TYPE)
        exchangeRates.size() == currencies.size()
        [exchangeRates, currencies].transpose().each { rate, currency ->
            assert rate.currency.name() == currency.toUpperCase()
        }

        where:
        caseName               | currencies
        'lower'                | ['usd', 'chf']
        'upper'                | ['USD', 'CHF']
        'mixed (lower, upper)' | ['usd', 'CHF']
        'mixed (upper, lower)' | ['USD', 'chf']
    }

    private static Optional<ExchangeRate> createExchangeRate(Currency currency) {
        Optional.of(new ExchangeRate(currency, BigDecimal.ONE))
    }

    @TestConfiguration
    static class StubConfig {
        DetachedMockFactory detachedMockFactory = new DetachedMockFactory()

        @Bean
        ExchangeRateProvider registrationService() {
            return detachedMockFactory.Mock(ExchangeRateProvider)
        }
    }
}
