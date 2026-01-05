package com.alabs.automation.phoenix.data.formula;

import com.alabs.automation.framework.utilities.Randomizer;
import com.alabs.automation.phoenix.constants.formulaservice.FormulaGenerationConstants;
import com.alabs.automation.phoenix.models.formula.formula.NonExistingFormulaEvaluatorExpressionRequest;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class NonExistingFormulaEvaluatorExpressionGenerator {
    public static final String VALUE = String.valueOf(Randomizer.randomPositiveInt(FormulaGenerationConstants.MAX_VALUE));
    public static final String INVALID_EXPRESSION = "attribute(\"total_spen\")";
    public static final Map<String, FormulaType> expressionToFormulaMap = new HashMap() {{
        put(FormulaType.ADD_ONE_HUNDRED.getValue(VALUE), FormulaType.ADD_ONE_HUNDRED);
        put(FormulaType.SUBTRACT_ONE_HUNDRED.getValue(VALUE), FormulaType.SUBTRACT_ONE_HUNDRED);
        put(FormulaType.COMPLEX.getValue(VALUE), FormulaType.COMPLEX);
    }};

    public static NonExistingFormulaEvaluatorExpressionRequest prepareEvaluatorWithValidExpressions(String gameAccountId) {
        String[] validExpressions = Arrays.stream(FormulaType.values())
                .map(formula -> formula.getValue(VALUE))
                .filter(expressionToFormulaMap::containsKey)
                .toArray(String[]::new);
        return prepareEvaluatorExpression(gameAccountId, validExpressions);
    }

    public static NonExistingFormulaEvaluatorExpressionRequest prepareEvaluatorWithOneValidExpression(String gameAccountId) {
        return prepareEvaluatorExpression(gameAccountId, FormulaType.ADD_ONE_HUNDRED.getValue(VALUE));
    }

    public static NonExistingFormulaEvaluatorExpressionRequest prepareEvaluatorWithInvalidExpression(String gameAccountId) {
        return prepareEvaluatorExpression(gameAccountId, INVALID_EXPRESSION, FormulaType.ADD_ONE_HUNDRED.getValue(VALUE));
    }

    public static NonExistingFormulaEvaluatorExpressionRequest prepareEvaluatorExpression(String gameAccountId, String... expressions) {
        return NonExistingFormulaEvaluatorExpressionRequest.builder().gameAccountId(gameAccountId).expressions(expressions).build();
    }
}
