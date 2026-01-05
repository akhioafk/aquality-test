package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.phoenix.data.formula.FormulaType;
import com.alabs.automation.phoenix.tests.formulaservice.BaseEvaluationFunctionTest;
import io.qameta.allure.TmsLink;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@TmsLink("9030394")
public class ComparisonFunctionTest extends BaseEvaluationFunctionTest {

    @Test(description = "Formula - Internal: evaluate comparison functions",
            priority = Integer.MAX_VALUE, groups = {"suite.smoke"})
    public void comparisonFunctionTest() {
        FormulaType formula = FormulaType.IF_GREATER_THAN_1000;
        createFormulaAndEvaluateFunction(formula);
    }

    @Test(description = "Send POST /internal/v1/formulas/process",
            dataProvider = "formulaResponseProviderForAdditionalChecks")
    public void comparisonFunctionAdditionalChecks(FormulaType formula) {
        createFormulaAndEvaluateFunction(formula);
    }

    @DataProvider
    private Object[][] formulaResponseProviderForAdditionalChecks() {
        return new Object[][]{
                {FormulaType.IF_NOT_EQUAL_TO_1500},
                {FormulaType.MIN_COMPARING_TO_100},
                {FormulaType.MAX_COMPARING_TO_100},
                {FormulaType.IF_GREATER_THAN_1000},
                {FormulaType.IF_LESS_THAN_1000},
                {FormulaType.IF_GREATER_OR_EQUAL_TO_1500},
                {FormulaType.IF_LESS_OR_EQUAL_TO_1000},
                {FormulaType.IF_EQUAL_TO_1500},
                {FormulaType.IF_GREATER_THAN_1000_AND_LESS_THAN_2000}
        };
    }
}
