package com.alabs.automation.phoenix.models.formula.formula;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExistingFormulaEvaluatorResponse {
    private String id;
    private Number result;
    private String reason;
}
