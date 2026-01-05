package com.alabs.automation.phoenix.models.formula.formula;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.alabs.automation.phoenix.constants.formulaservice.FormulaValuePriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FormulaExpression {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("rule_id")
    @JsonAlias("ruleId")
    private String ruleId;
    private FormulaValuePriority priority;
    @JsonProperty("default")
    @JsonAlias("isDefault")
    private Boolean isDefault;
    private String expression;
    private String normalizedExpression;
}
