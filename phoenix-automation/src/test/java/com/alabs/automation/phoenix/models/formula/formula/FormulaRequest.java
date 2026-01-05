package com.alabs.automation.phoenix.models.formula.formula;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FormulaRequest {
    private String id;
    private String description;
    private FormulaExpression[] expressions;
}
