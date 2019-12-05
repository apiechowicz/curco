package com.github.apiechowicz.curco.repositories;

import com.github.apiechowicz.curco.model.daos.CurrencyDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<CurrencyDao, Integer> {

}
