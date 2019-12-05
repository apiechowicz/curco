package com.github.apiechowicz.curco.repositories;

import com.github.apiechowicz.curco.model.daos.CurrencyDao;
import com.github.apiechowicz.curco.model.daos.ExchangeRateDao;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExchangeRateRepository extends CrudRepository<ExchangeRateDao, Integer> {

    List<ExchangeRateDao> findAllByCurrency(CurrencyDao currency);
}
