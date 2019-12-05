package com.github.apiechowicz.curco.model.responses;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Currency implements Response {

    private final String codeName;
    private final String fullName;
}
