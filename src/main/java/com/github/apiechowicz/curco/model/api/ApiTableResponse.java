package com.github.apiechowicz.curco.model.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApiTableResponse {

    private String table;
    private String no;
    private String effectiveDate;
    private List<Rate> rates;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Rate {
        private String currency;
        private String code;
        private BigDecimal mid;
    }
}
