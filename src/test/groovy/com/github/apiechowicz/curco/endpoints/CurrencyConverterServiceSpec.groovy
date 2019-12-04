package com.github.apiechowicz.curco.endpoints

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.apiechowicz.curco.model.Currency
import com.github.apiechowicz.curco.services.CurrencyConverterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@AutoConfigureMockMvc
@WebMvcTest(controllers = CurrencyConverterEndpoint)
class CurrencyConverterServiceSpec extends Specification {

    @Autowired
    private MockMvc mvc

    @Autowired
    private CurrencyConverterService converterService

    @Autowired
    private ObjectMapper objectMapper

    def "returns result of currency conversion for valid request"() {
        given: 'valid input data'
        BigDecimal amount = 1
        Currency from = Currency.USD
        Currency to = Currency.USD

        and: 'the fact that currency converter service returns valid conversion result'
        BigDecimal conversionResult = 2
        converterService.convertCurrencies(amount, from, to) >> conversionResult

        when: 'valid request for currency conversion is performed'
        MockHttpServletResponse response = mvc.perform(get('/convert')
                .param('amount', amount.toString())
                .param('from', from.name())
                .param('to', to.name())
        ).andReturn().response

        then: 'response will not be null'
        response != null

        and: 'ok status will be returned'
        response.status == HttpStatus.OK.value()

        and: 'returned result will be equal to result of currency conversion'
        objectMapper.readValue(response.contentAsString, BigDecimal) == conversionResult
    }

    @TestConfiguration
    static class StubConfig {
        DetachedMockFactory detachedMockFactory = new DetachedMockFactory()

        @Bean
        CurrencyConverterService registrationService() {
            return detachedMockFactory.Stub(CurrencyConverterService)
        }
    }
}
