package com.github.apiechowicz.curco.converters;

import com.github.apiechowicz.curco.model.daos.CurrencyDao;
import com.github.apiechowicz.curco.model.responses.Currency;
import org.springframework.stereotype.Service;

@Service
public class CurrencyConverter implements DaoToResponseConverter<CurrencyDao, Currency> {

    @Override
    public Currency convertDaoToResponse(CurrencyDao dao) {
        return new Currency(dao.getCodeName(), dao.getFullName());
    }
}
