package com.cosmos.meta.model;

import lombok.Getter;

@Getter
public enum FlowStep {
    NAME(1),
    DATE_OF_BIRTH(2),
    TIME_OF_BIRTH(3),
    CITY(4);

    private final Integer order;

    FlowStep(Integer order) {
        this.order = order;
    }


}
