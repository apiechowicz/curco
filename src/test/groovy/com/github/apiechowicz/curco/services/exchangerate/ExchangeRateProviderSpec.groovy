package com.github.apiechowicz.curco.services.exchangerate

import com.github.apiechowicz.curco.model.Currency
import com.github.apiechowicz.curco.model.ExchangeRate
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class ExchangeRateProviderSpec extends Specification {

    def "returns exchange rate returned by the api"() {
        given: 'a currency'
        Currency currency = Currency.CHF
        and: "it's exchange rate"
        BigDecimal exchangeRate = BigDecimal.ONE
        and: 'a stubbed rest template'
        RestTemplate restTemplate = Stub(RestTemplate) {
            getForObject(_, _) >> Stub(ApiExchangeRateResponse) {
                getRates() >> [Stub(ApiExchangeRateResponse.Rate) {
                    getMid() >> exchangeRate
                }]
            }
        }
        and: 'exchange rate provider using prepared rest template'
        ExchangeRateProvider exchangeRateProvider = new ExchangeRateProvider(restTemplate)

        when: 'exchange rate is requested'
        Optional<ExchangeRate> result = exchangeRateProvider.provideExchangeRate(currency)

        then: 'result will not be empty'
        !result.isEmpty()
        and: 'it will contain proper values'
        with(result.get()) {
            it.currency == currency
            it.exchangeRate == exchangeRate
        }
    }

    def "returns no exchange rate if api call failed"() {
        given: 'a currency'
        Currency currency = Currency.CHF
        and: 'a stubbed rest template'
        RestTemplate restTemplate = Stub(RestTemplate) {
            getForObject(_, _) >> null
        }
        and: 'exchange rate provider using prepared rest template'
        ExchangeRateProvider exchangeRateProvider = new ExchangeRateProvider(restTemplate)

        when: 'exchange rate is requested'
        Optional<ExchangeRate> result = exchangeRateProvider.provideExchangeRate(currency)

        then: 'result will be empty'
        result.isEmpty()
    }

    def "caches results returned by the api"() {
        given: 'a currency'
        Currency currency = Currency.CHF
        and: "it's exchange rate"
        BigDecimal exchangeRate = BigDecimal.ONE
        and: 'a stubbed rest template'
        RestTemplate restTemplate = Mock(RestTemplate)
        and: 'exchange rate provider using prepared rest template'
        ExchangeRateProvider exchangeRateProvider = new ExchangeRateProvider(restTemplate)

        when: 'exchange rate is requested'
        Optional<ExchangeRate> result = exchangeRateProvider.provideExchangeRate(currency)

        then: 'api will be requested once '
        1 * restTemplate.getForObject(_, _) >> Stub(ApiExchangeRateResponse) {
            getRates() >> [Stub(ApiExchangeRateResponse.Rate) {
                getMid() >> exchangeRate
            }]
        }
        and: 'the result will not be empty'
        !result.isEmpty()
        and: 'it will contain proper values'
        with(result.get()) {
            it.currency == currency
            it.exchangeRate == exchangeRate
        }

        when: 'exchange rate is requested for the same currency'
        Optional<ExchangeRate> secondResult = exchangeRateProvider.provideExchangeRate(currency)

        then: 'api will not be called again'
        0 * restTemplate._
        and: 'the result will be exactly the same as first time'
        secondResult == result
    }
}
