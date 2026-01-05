package com.alabs.automation.phoenix.data.formula;

import com.alabs.automation.framework.utilities.Randomizer;
import com.alabs.automation.phoenix.models.formula.formula.ExistingEvaluatorFormula;
import com.alabs.automation.phoenix.models.formula.formula.ExistingFormulaEvaluatorRequest;
import com.alabs.automation.phoenix.models.formula.snapshot.FormulaSnapshotAttributes;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.UUID;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaGenerationConstants.MAX_VALUE;

@UtilityClass
public class ExistingFormulaEvaluatorRequestGenerator {

    public static ExistingFormulaEvaluatorRequest prepareFormulaEvaluatorRequest(String gameAccountId,
                                                                                 String snapshotId,
                                                                                 String... formulaIds) {
        return ExistingFormulaEvaluatorRequest.builder()
                .gameAccountId(gameAccountId)
                .formulas(prepareEvaluatorFormulas(snapshotId, formulaIds))
                .build();
    }

    public static ExistingFormulaEvaluatorRequest prepareFormulaEvaluatorRequestWithoutSnapshotId(String gameAccountId,
                                                                                                  String... formulaIds) {
        return ExistingFormulaEvaluatorRequest.builder()
                .gameAccountId(gameAccountId)
                .formulas(prepareEvaluatorFormulas(formulaIds))
                .build();
    }

    public static ExistingFormulaEvaluatorRequest prepareFormulaEvaluatorRequest(String gameAccountId,
                                                                                 String snapshotId,
                                                                                 int attribute,
                                                                                 String... formulaIds) {
        ExistingFormulaEvaluatorRequest existingFormulaEvaluatorRequest = prepareFormulaEvaluatorRequest(
                gameAccountId, snapshotId, formulaIds);
        Arrays.stream(existingFormulaEvaluatorRequest.getFormulas())
                .forEach(formula -> formula.getAdditionalAttributes().setTestAttribute(attribute));
        return existingFormulaEvaluatorRequest;
    }

    private static ExistingEvaluatorFormula[] prepareEvaluatorFormulas(String snapshotId, String... formulaIds) {
        return Arrays.stream(formulaIds)
                .map(formulaId -> ExistingEvaluatorFormula.builder()
                        .evaluationId(UUID.randomUUID().toString())
                        .formulaId(formulaId)
                        .snapshotId(snapshotId)
                        .additionalAttributes(prepareAttributes())
                        .build())
                .toArray(ExistingEvaluatorFormula[]::new);
    }

    private static ExistingEvaluatorFormula[] prepareEvaluatorFormulas(String... formulaIds) {
        return Arrays.stream(formulaIds)
                .map(formulaId -> ExistingEvaluatorFormula.builder()
                        .evaluationId(UUID.randomUUID().toString())
                        .formulaId(formulaId)
                        .additionalAttributes(prepareAttributes())
                        .build())
                .toArray(ExistingEvaluatorFormula[]::new);
    }

    private static FormulaSnapshotAttributes prepareAttributes() {
        return FormulaSnapshotAttributes.builder().testAttribute(Randomizer.randomPositiveInt(MAX_VALUE)).build();
    }
}
