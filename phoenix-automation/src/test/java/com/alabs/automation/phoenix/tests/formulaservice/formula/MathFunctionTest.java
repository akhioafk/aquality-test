package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.framework.steps.api.ResponseSteps;
import com.alabs.automation.phoenix.data.formula.FormulaType;
import com.alabs.automation.phoenix.tests.formulaservice.BaseEvaluationFunctionTest;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaValueOption.FIRST_PRIOR_DEFAULT_TRUE;
import static com.alabs.automation.phoenix.constants.formulaservice.FormulaValueOption.SECOND_PRIOR_DEFAULT_FALSE;
import static com.alabs.automation.phoenix.data.formula.FormulaGenerator.prepareFormula;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;

@TmsLink("9030392")
public class MathFunctionTest extends BaseEvaluationFunctionTest {

    @Test(description = "Formula - Internal: evaluate math functions",
            priority = Integer.MAX_VALUE, groups = {"suite.smoke"})
    public void mathFunctionTest() {
        FormulaType formula = FormulaType.ADD_ONE_HUNDRED;
        createFormulaAndEvaluateFunction(formula);
    }

    @Test(description = "Send POST /internal/v1/formulas/process",
            dataProvider = "formulaResponseProviderForAdditionalChecks")
    public void mathFunctionAdditionalChecks(FormulaType formula) {
        createFormulaAndEvaluateFunction(formula);
    }

    @Test(description = "Send POST /internal/v1/formulas/process",
            dataProvider = "formulaResponseProviderForFractionalPartChecks")
    public void mathFunctionFractionalPartChecks(String formulaExpression, FormulaType formula, String value) {
        createFormulaAndEvaluateFunction(formulaExpression, formula, value);
    }

    @Test(description = "Send POST /internal/v1/formulas/process - with invalid expressions",
            dataProvider = "invalidExpressionsProvider")
    public void mathFunctionWithInvalidExpressions(String formulaExpression) {
        Response response = createFormulaSteps.createFormula(prepareFormula(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression(validRuleIds[0], formulaExpression),
                SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[1])));
        ResponseSteps.checkStatusCode(response, SC_BAD_REQUEST);
    }

    @DataProvider
    private Object[][] formulaResponseProviderForAdditionalChecks() {
        return new Object[][]{
                {FormulaType.SUBTRACT_ONE_HUNDRED},
                {FormulaType.MULTIPLY_BY_TWO},
                {FormulaType.DIVIDE_BY_TWO},
                {FormulaType.MODULO_TEN},
                {FormulaType.LOGARITHM_BASE_3},
                {FormulaType.LOGARITHM_BASE_2},
                {FormulaType.LOGARITHM_BASE_10}
        };
    }

    @DataProvider
    private Object[][] formulaResponseProviderForFractionalPartChecks() {
        return new Object[][]{
                {FormulaType.DIVIDE_BY_TEN.getValue(EVALUATE_FORMULA_VALUE_ONE), FormulaType.DIVIDE_BY_TEN, EVALUATE_FORMULA_VALUE_ONE},
                {FormulaType.DIVIDE_BY_TEN.getValue(EVALUATE_FORMULA_VALUE_MINUS_ONE), FormulaType.DIVIDE_BY_TEN, EVALUATE_FORMULA_VALUE_MINUS_ONE},
                {FormulaType.DIVIDE_BY_THREE.getValue(EVALUATE_FORMULA_VALUE_ONE), FormulaType.DIVIDE_BY_THREE, EVALUATE_FORMULA_VALUE_ONE},
                {FormulaType.DIVIDE_BY_TEN.getValue(EVALUATE_FORMULA_VALUE_ONE_POINT_ZERO), FormulaType.DIVIDE_BY_TEN, EVALUATE_FORMULA_VALUE_ONE_POINT_ZERO},
                {FormulaType.ADD_FOUR_OVER_TEN.getValue(EVALUATE_FORMULA_VALUE_ONE), FormulaType.ADD_FOUR_OVER_TEN, EVALUATE_FORMULA_VALUE_ONE}
        };
    }

    @DataProvider
    private Object[][] invalidExpressionsProvider() {
        return new Object[][]{
                {FORMULA_EXPRESSION_WITH_ZERO},
                {FORMULA_EXPRESSION_WITH_LETTER}
        };
    }
}
