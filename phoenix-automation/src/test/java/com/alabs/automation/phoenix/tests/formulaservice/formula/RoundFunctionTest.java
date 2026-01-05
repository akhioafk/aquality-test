package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.phoenix.data.formula.FormulaType;
import com.alabs.automation.framework.utilities.MathUtils;
import com.alabs.automation.phoenix.tests.formulaservice.BaseEvaluationFunctionTest;
import io.qameta.allure.TmsLink;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@TmsLink("9030393")
public class RoundFunctionTest extends BaseEvaluationFunctionTest {
    private static final String VALUE_ROUND_FIRST = "4.55555";
    private static final String VALUE_ROUND_SECOND = "4.44444";
    private static final String VALUE_ROUND_DOWN = "1000/15";
    private static final String VALUE_ROUND_UP = "10000/43";
    private static final String VALUE_ROUND_THIRD = "11/2";
    private static final String VALUE_ROUND_WITH_TWO_SIGNIFICANT_FIGURES = "1/3";
    private static final String VALUE_ROUND_FOURTH = "-7/4";

    @Test(description = "Formula - Internal: evaluate round functions",
            priority = Integer.MAX_VALUE, groups = {"suite.smoke"})
    public void roundFunctionTest() {
        FormulaType formula = FormulaType.ROUND;
        createFormulaAndEvaluateFunction(formula.getValue(VALUE_ROUND_FIRST), formula, VALUE_ROUND_FIRST);
    }

    @Test(description = "Send POST /internal/v1/formulas/process",
            dataProvider = "formulaResponseProviderForAdditionalChecks")
    public void roundFunctionAdditionalChecks(String formulaExpression, FormulaType formula, String value) {
        createFormulaAndEvaluateFunction(formulaExpression, formula, value);
    }

    @DataProvider
    private Object[][] formulaResponseProviderForAdditionalChecks() {
        return new Object[][]{
                {FormulaType.ROUND.getValue(VALUE_ROUND_SECOND), FormulaType.ROUND, VALUE_ROUND_SECOND},
                {FormulaType.ROUND_DOWN.getValue(VALUE_ROUND_FIRST), FormulaType.ROUND_DOWN, VALUE_ROUND_FIRST},
                {FormulaType.ROUND_UP.getValue(VALUE_ROUND_SECOND), FormulaType.ROUND_UP, VALUE_ROUND_SECOND},
                {FormulaType.ROUND_DOWN_BY_10.getValue(VALUE_ROUND_DOWN), FormulaType.ROUND_DOWN_BY_10,
                        String.valueOf(MathUtils.calculateFraction(VALUE_ROUND_DOWN))},
                {FormulaType.ROUND_UP_BY_100.getValue(VALUE_ROUND_UP), FormulaType.ROUND_UP_BY_100,
                        String.valueOf(MathUtils.calculateFraction(VALUE_ROUND_UP))},
                {FormulaType.ROUND.getValue(VALUE_ROUND_THIRD), FormulaType.ROUND,
                        String.valueOf(MathUtils.calculateFraction(VALUE_ROUND_THIRD))},
                {FormulaType.ROUND_DOWN.getValue(VALUE_ROUND_THIRD), FormulaType.ROUND_DOWN,
                        String.valueOf(MathUtils.calculateFraction(VALUE_ROUND_THIRD))},
                {FormulaType.ROUND_UP.getValue(VALUE_ROUND_THIRD), FormulaType.ROUND_UP,
                        String.valueOf(MathUtils.calculateFraction(VALUE_ROUND_THIRD))},
                {FormulaType.ROUND_WITH_TWO_SIGNIFICANT_FIGURES.getValue(VALUE_ROUND_WITH_TWO_SIGNIFICANT_FIGURES),
                        FormulaType.ROUND_WITH_TWO_SIGNIFICANT_FIGURES, String.valueOf(MathUtils.calculateFraction(VALUE_ROUND_WITH_TWO_SIGNIFICANT_FIGURES))},
                {FormulaType.ROUND.getValue(VALUE_ROUND_FOURTH), FormulaType.ROUND, String.valueOf(MathUtils.calculateFraction(VALUE_ROUND_FOURTH))}
        };
    }
}
