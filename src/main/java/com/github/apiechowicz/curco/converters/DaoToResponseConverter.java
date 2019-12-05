package com.github.apiechowicz.curco.converters;

import com.github.apiechowicz.curco.model.daos.Dao;
import com.github.apiechowicz.curco.model.responses.Response;

import java.util.List;
import java.util.stream.Collectors;

public interface DaoToResponseConverter<D extends Dao, R extends Response> {

    R convertDaoToResponse(D dao);

    default List<R> convertDaosToResponses(List<D> daos) {
        return daos.stream().map(this::convertDaoToResponse).collect(Collectors.toList());
    }
}
