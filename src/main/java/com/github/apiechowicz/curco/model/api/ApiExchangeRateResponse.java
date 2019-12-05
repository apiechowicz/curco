package com.github.apiechowicz.curco.model.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApiExchangeRateResponse {
    private String table;
    private String currency;
    private String code;
    private List<Rate> rates;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Rate {
        private String no;
        private String effectiveDate;
        private BigDecimal mid;
    }
}
