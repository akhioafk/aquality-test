package com.alabs.automation.phoenix.models.formula.formula;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.alabs.automation.phoenix.models.common.search.Metrics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FormulaResponse {
    private String id;
    private String description;
    private FormulaExpression[] expressions;
    private Boolean archived;
    private Date updatedAt;
    private String updatedBy;
    private String code;
    private Metrics metrics;
}
