package com.github.apiechowicz.curco


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import spock.lang.Specification

@SpringBootTest
class CurcoApplicationSpec extends Specification {

    @Autowired
    private ApplicationContext context

    def "loads application context properly"() {
        expect: 'that context is not null'
        context != null
    }
}
