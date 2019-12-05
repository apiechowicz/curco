package com.github.apiechowicz.curco.converters


import com.github.apiechowicz.curco.model.daos.CurrencyDao
import com.github.apiechowicz.curco.model.daos.ExchangeRateDao
import com.github.apiechowicz.curco.model.responses.Currency
import com.github.apiechowicz.curco.model.responses.ExchangeRate
import spock.lang.Specification

class ExchangeRateConverterSpec extends Specification {

    def "converts dao to response correctly"() {
        given: 'dao'
        ExchangeRateDao exchangeRateDao = new ExchangeRateDao(Stub(CurrencyDao), BigDecimal.ONE)
        and: 'mocked currency converter'
        CurrencyConverter currencyConverter = Mock(CurrencyConverter)
        and: 'converter'
        ExchangeRateConverter exchangeRateConverter = new ExchangeRateConverter(currencyConverter)

        when: 'conversion is performed'
        ExchangeRate result = exchangeRateConverter.convertDaoToResponse(exchangeRateDao)

        then: 'currency conversion will be triggered once'
        1 * currencyConverter.convertDaoToResponse(_) >> Stub(Currency)
        and: 'exchange rates will be equal'
        result.exchangeRate == exchangeRateDao.exchangeRate
    }

    def "converts list of daos to response correctly"() {
        given: 'list of daos'
        List<ExchangeRateDao> exchangeRateDaos = [
                new ExchangeRateDao(Stub(CurrencyDao), BigDecimal.ONE),
                new ExchangeRateDao(Stub(CurrencyDao), BigDecimal.TEN),
        ]
        and : 'mocked currency converter'
        CurrencyConverter currencyConverter = Mock(CurrencyConverter)
        and: 'converter'
        ExchangeRateConverter exchangeRateConverter = new ExchangeRateConverter(currencyConverter)

        when: 'conversion is performed'
        List<ExchangeRate> results = exchangeRateConverter.convertDaosToResponses(exchangeRateDaos)

        then: 'currency conversion will be triggered for every dao'
        exchangeRateDaos.size() * currencyConverter.convertDaoToResponse(_) >> Stub(Currency)
        and: 'daos will be converted properly'
        exchangeRateDaos.size() == results.size()
        [exchangeRateDaos, results].transpose().each { dao, response ->
            assert dao.exchangeRate == response.exchangeRate
        }
    }
}
