package com.alabs.automation.phoenix.models.formula.formula;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserAttributeFunctionFormula {
    private String evaluationId;
    private String formulaId;
    private String snapshotId;
    private Map<String, Integer> additionalAttributes;
}
