package com.alabs.automation.phoenix.models.formula.formula;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FormulaExportResponse {
    private String id;
    private String description;
    private FormulaExpression[] expressions;
    private Boolean archived;
    private Date updatedAt;
    private String updatedBy;
    private JsonNode dependencyExpressions;
}
