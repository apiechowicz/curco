package com.github.apiechowicz.curco.services.exchangerate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
final class ApiExchangeRateResponse implements Serializable {
    private String table;
    private String currency;
    private String code;
    private List<Rate> rates;

    @Getter
    @Setter
    @NoArgsConstructor
    static final class Rate {
        private String no;
        private String effectiveDate;
        private BigDecimal mid;
    }
}
