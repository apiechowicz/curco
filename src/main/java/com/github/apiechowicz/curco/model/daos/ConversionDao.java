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
@Table(name = ConversionDao.TABLE_NAME)
public class ConversionDao implements Dao {

    public static final String TABLE_NAME = "conversions";

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private ExchangeRateDao currencyFrom;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private ExchangeRateDao currencyTo;

    @Column(nullable = false)
    private BigDecimal result;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    public ConversionDao(BigDecimal amount, ExchangeRateDao currencyFrom, ExchangeRateDao currencyTo, BigDecimal result) {
        this.amount = amount;
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
        this.result = result;
        dateTime = LocalDateTime.now();
    }
}
