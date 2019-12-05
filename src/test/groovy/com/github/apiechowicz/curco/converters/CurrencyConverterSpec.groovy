package com.github.apiechowicz.curco.converters

import com.github.apiechowicz.curco.model.daos.ApiTable
import com.github.apiechowicz.curco.model.daos.CurrencyDao
import com.github.apiechowicz.curco.model.responses.Currency
import spock.lang.Specification

class CurrencyConverterSpec extends Specification {

    def "converts dao to response correctly"() {
        given: 'dao'
        CurrencyDao currencyDao = new CurrencyDao('codeName', 'fullName', ApiTable.A)
        and: 'converter'
        CurrencyConverter currencyConverter = new CurrencyConverter()

        when: 'conversion is performed'
        Currency result = currencyConverter.convertDaoToResponse(currencyDao)

        then: 'object will be converted properly'
        result.codeName == currencyDao.codeName
        result.fullName == currencyDao.fullName
    }

    def "converts list of daos to response correctly"() {
        given: 'list of daos'
        List<CurrencyDao> currencyDaos = [
                new CurrencyDao('codeName1', 'fullName1', ApiTable.A),
                new CurrencyDao('codeName2', 'fullName2', ApiTable.B),
        ]
        and: 'converter'
        CurrencyConverter currencyConverter = new CurrencyConverter()

        when: 'conversion is performed'
        List<Currency> results = currencyConverter.convertDaosToResponses(currencyDaos)

        then: 'daos will be converted properly'
        currencyDaos.size() == results.size()
        [currencyDaos, results].transpose().each { dao, response ->
            assert dao.codeName == response.codeName
            assert dao.fullName == response.fullName
        }
    }
}
