package com.github.apiechowicz.curco.services

import com.github.apiechowicz.curco.model.Currency
import com.github.apiechowicz.curco.model.ExchangeRate
import spock.lang.Specification
import spock.lang.Unroll

class CurrencyConverterServiceSpec extends Specification {

    def "returns same value when converting to the same currency"() {
        given: 'amount of money to convert'
        BigDecimal amount = 1
        and: 'the same currency to convert to'
        Currency currency = Currency.USD
        and: 'currency converter service'
        CurrencyConverterService currencyConverter = new CurrencyConverterService(Stub(ExchangeRateProvider))

        when: 'currencies are converted'
        Optional<BigDecimal> result = currencyConverter.convertCurrencies(amount, currency, currency)

        then: 'result will not be empty'
        !result.isEmpty()

        and: 'it will be equal to original amount'
        result.get() == amount
    }

    def "returns zero when converting zero amount"() {
        given: 'zero amount of money to convert'
        BigDecimal amount = 0
        and: 'different currencies'
        Currency currencyFrom = Currency.USD
        Currency currencyTo = Currency.CHF
        and: 'currency converter service'
        CurrencyConverterService currencyConverter = new CurrencyConverterService(Stub(ExchangeRateProvider))

        when: 'currencies are converted'
        Optional<BigDecimal> result = currencyConverter.convertCurrencies(amount, currencyFrom, currencyTo)

        then: 'result will not be empty'
        !result.isEmpty()

        and: 'it will be equal to zero'
        result.get() == BigDecimal.ZERO
    }

    @Unroll
    def "returns no result if exchange rate cannot be obtained"(Optional<BigDecimal> firstRate, Optional<BigDecimal> secondRate) {
        given: 'non zero amount'
        BigDecimal amount = 1
        and: 'different currencies'
        Currency currencyFrom = Currency.USD
        Currency currencyTo = Currency.CHF
        and: 'the fact that exchange rate provider will not return at least one exchange rate'
        ExchangeRateProvider exchangeRateProvider = Stub(ExchangeRateProvider) {
            provideExchangeRate(currencyFrom) >> firstRate.map({ rate -> new ExchangeRate(currencyFrom, rate) })
            provideExchangeRate(currencyTo) >> secondRate.map({ rate -> new ExchangeRate(currencyTo, rate) })
        }
        and: 'currency converter service'
        CurrencyConverterService currencyConverter = new CurrencyConverterService(exchangeRateProvider)

        when: 'currencies are converted'
        Optional<BigDecimal> result = currencyConverter.convertCurrencies(amount, currencyFrom, currencyTo)

        then: 'result will be empty'
        result.isEmpty()

        where:
        firstRate        | secondRate
        Optional.of(1)   | Optional.empty()
        Optional.empty() | Optional.of(1)
        Optional.empty() | Optional.empty()
    }

    @Unroll
    def "calculates value correctly for #amount with given exchange rates: #firstRate, #secondRate"(BigDecimal amount, BigDecimal firstRate, BigDecimal secondRate, BigDecimal expectedResult) {
        given: 'different currencies'
        Currency currencyFrom = Currency.USD
        Currency currencyTo = Currency.CHF
        and: 'the fact that exchange rate provider will return predefined exchange rates'
        ExchangeRateProvider exchangeRateProvider = Stub(ExchangeRateProvider) {
            provideExchangeRate(currencyFrom) >> Optional.of(new ExchangeRate(currencyFrom, firstRate))
            provideExchangeRate(currencyTo) >> Optional.of(new ExchangeRate(currencyTo, secondRate))
        }
        and: 'currency converter service'
        CurrencyConverterService currencyConverter = new CurrencyConverterService(exchangeRateProvider)

        when: 'currencies are converted'
        Optional<BigDecimal> result = currencyConverter.convertCurrencies(amount, currencyFrom, currencyTo)

        then: 'result will not be empty'
        !result.isEmpty()

        and: 'it will be equal to expected result'
        result.get() == expectedResult

        where:
        amount | firstRate | secondRate | expectedResult
        1      | 1         | 1          | 1
        10     | 3.85      | 3.9        | 9.87
    }
}
