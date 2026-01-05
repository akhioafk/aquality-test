package com.alabs.automation.phoenix.models.formula.formula;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NonExistingFormulaEvaluatorResponse {
    private String expression;
    private String reason;
    private Number result;
}
