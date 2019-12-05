package com.github.apiechowicz.curco.model.daos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = ExchangeRateDao.TABLE_NAME)
public class ExchangeRateDao implements Dao {

    public static final String TABLE_NAME = "exchange_rates";

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private CurrencyDao currency;

    @Column(nullable = false)
    private BigDecimal exchangeRate;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    public ExchangeRateDao(CurrencyDao currency, BigDecimal exchangeRate) {
        this.currency = currency;
        this.exchangeRate = exchangeRate;
        dateTime = LocalDateTime.now();
    }
}
