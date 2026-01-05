package com.alabs.automation.phoenix.models.formula.formula;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.alabs.automation.phoenix.models.formula.snapshot.FormulaSnapshotAttributes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ExistingEvaluatorFormula {
    private String evaluationId;
    private String formulaId;
    private String snapshotId;
    private FormulaSnapshotAttributes additionalAttributes;
}
