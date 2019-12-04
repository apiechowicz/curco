package com.github.apiechowicz.curco.endpoints

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.apiechowicz.curco.model.Currency
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@AutoConfigureMockMvc
@WebMvcTest(controllers = CurrencyListEndpoint)
class CurrencyListEndpointSpec extends Specification {

    @Autowired
    private MockMvc mvc

    @Autowired
    private ObjectMapper objectMapper

    def "returns list of available currencies"() {
        when: 'request for list of currencies is performed'
        MockHttpServletResponse response = mvc.perform(get('/list'))
                .andReturn()
                .response

        then: 'response will not be null'
        response != null

        and: 'ok status will be returned'
        response.status == HttpStatus.OK.value()

        and: 'number of returned currencies will be equal to number of available currencies'
        List<Currency> returnedCurrencies = objectMapper.readValue(response.contentAsString, List)
        Currency[] actualAvailableCurrencies = Currency.values() as List
        returnedCurrencies.size() == actualAvailableCurrencies.length

        and: 'every available currency will be in returned list'
        actualAvailableCurrencies.each {
            assert returnedCurrencies.contains(it.name())
        }
    }
}
