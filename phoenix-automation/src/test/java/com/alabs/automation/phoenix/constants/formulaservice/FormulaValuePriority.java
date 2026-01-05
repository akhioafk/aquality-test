package com.alabs.automation.phoenix.constants.formulaservice;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum FormulaValuePriority {
    FIRST_PRIORITY(1),
    SECOND_PRIORITY(2),
    THIRD_PRIORITY(3),
    FOURTH_PRIORITY(4),
    FIFTH_PRIORITY(5),
    SIXTH_PRIORITY(6),
    SEVENTH_PRIORITY(7),
    EIGHTH_PRIORITY(8),
    NINTH_PRIORITY(9),
    TENTH_PRIORITY(10);

    @JsonValue
    private final int priority;

    FormulaValuePriority(int priority) {
        this.priority = priority;
    }
}
