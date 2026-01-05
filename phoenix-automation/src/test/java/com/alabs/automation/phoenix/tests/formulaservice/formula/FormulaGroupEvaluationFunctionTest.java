package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.phoenix.data.formula.ExistingFormulaEvaluatorRequestGenerator;
import com.alabs.automation.framework.steps.api.ResponseSteps;
import com.alabs.automation.phoenix.constants.formulaservice.ValueCombinationDescription;
import com.alabs.automation.phoenix.models.formula.formula.ExistingFormulaEvaluatorRequest;
import com.alabs.automation.phoenix.models.formula.formula.FormulaExpression;
import com.alabs.automation.phoenix.models.formula.formula.FormulaResponse;
import com.alabs.automation.phoenix.steps.formulaservice.formula.FormulaPreconditionSteps;
import com.alabs.automation.phoenix.tests.formulaservice.BaseEvaluationFunctionTest;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaValueOption.*;
import static org.apache.http.HttpStatus.SC_OK;

@TmsLink("9030395")
public class FormulaGroupEvaluationFunctionTest extends BaseEvaluationFunctionTest {

    @BeforeTest(groups = {"suite.smoke"})
    public void beforeTest() {
        prepareInvalidDeviceModelRules(userDeviceModel, INVALID_RULES_AMOUNT);
    }

    @Test(description = "Formula - Internal: Formula Group evaluation function",
            priority = Integer.MAX_VALUE, groups = {"suite.smoke"})
    public void formulaGroupEvaluationTest() {
        FormulaResponse initialFormulaResponse = FormulaPreconditionSteps.createFormula(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression(validRuleIds[0]),
                SECOND_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[0]),
                THIRD_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[1]));
        evaluationFunction(initialFormulaResponse);
    }

    @Test(description = "Send POST /internal/v1/formulas/process to check formula group with values :",
            dataProvider = "formulaExpressionsProviderForAdditionalChecks")
    public void formulaGroupEvaluationAdditionalChecks(String name, FormulaExpression... formulaExpressions) {
        evaluationFunction(FormulaPreconditionSteps.createFormula(formulaExpressions));
    }

    private void evaluationFunction(FormulaResponse formulaResponse) {
        ExistingFormulaEvaluatorRequest formulaEvaluatorRequest = ExistingFormulaEvaluatorRequestGenerator.prepareFormulaEvaluatorRequestWithoutSnapshotId(
                gameAccountId, formulaResponse.getId());
        Response response = evaluateExistingFormulaSteps.evaluateFormula(formulaEvaluatorRequest);
        ResponseSteps.checkStatusCode(response, SC_OK);
        evaluateExistingFormulaSteps.checkResultInResponse(formulaEvaluatorRequest, response, validRuleIds, formulaResponse);
    }

    @DataProvider
    private Object[][] formulaExpressionsProviderForAdditionalChecks() {
        return new Object[][]{
                {ValueCombinationDescription.DESCRIPTION_1,
                        FIRST_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]),
                        SECOND_PRIOR_DEFAULT_TRUE.getExpression(invalidRuleIds[0]),
                        THIRD_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[1])},
                {ValueCombinationDescription.DESCRIPTION_2,
                        FIRST_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]),
                        SECOND_PRIOR_DEFAULT_TRUE.getExpression(),
                        THIRD_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[0])},
                {ValueCombinationDescription.DESCRIPTION_3,
                        FIRST_PRIOR_DEFAULT_TRUE.getExpression(),
                        SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]),
                        THIRD_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[0])},
                {ValueCombinationDescription.DESCRIPTION_4,
                        FIRST_PRIOR_DEFAULT_TRUE.getExpression(invalidRuleIds[0]),
                        SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]),
                        THIRD_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[0])},
                {ValueCombinationDescription.DESCRIPTION_5,
                        FIRST_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[0]),
                        SECOND_PRIOR_DEFAULT_TRUE.getExpression(validRuleIds[0]),
                        THIRD_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[1])},
                {ValueCombinationDescription.DESCRIPTION_6,
                        FIRST_PRIOR_DEFAULT_TRUE.getExpression(),
                        SECOND_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[0]),
                        THIRD_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0])},
                {ValueCombinationDescription.DESCRIPTION_7,
                        FIRST_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[0]),
                        SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]),
                        THIRD_PRIOR_DEFAULT_TRUE.getExpression()},
                {ValueCombinationDescription.DESCRIPTION_8,
                        FIRST_PRIOR_DEFAULT_TRUE.getExpression(validRuleIds[0]),
                        SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[1]),
                        THIRD_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[0])},
                {ValueCombinationDescription.DESCRIPTION_9,
                        FIRST_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]),
                        SECOND_PRIOR_DEFAULT_TRUE.getExpression(validRuleIds[1]),
                        THIRD_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[0])},
                {ValueCombinationDescription.DESCRIPTION_10,
                        FIRST_PRIOR_DEFAULT_TRUE.getExpression(),
                        SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]),
                        THIRD_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[1])},
                {ValueCombinationDescription.DESCRIPTION_11,
                        FIRST_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]),
                        SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[1]),
                        THIRD_PRIOR_DEFAULT_TRUE.getExpression()},
                {ValueCombinationDescription.DESCRIPTION_12,
                        FIRST_PRIOR_DEFAULT_TRUE.getExpression(invalidRuleIds[0]),
                        SECOND_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[1]),
                        THIRD_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[2])},
                {ValueCombinationDescription.DESCRIPTION_13,
                        FIRST_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[0]),
                        SECOND_PRIOR_DEFAULT_TRUE.getExpression(invalidRuleIds[1]),
                        THIRD_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[2])},
                {ValueCombinationDescription.DESCRIPTION_14,
                        FIRST_PRIOR_DEFAULT_TRUE.getExpression(),
                        SECOND_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[0]),
                        THIRD_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[1])},
                {ValueCombinationDescription.DESCRIPTION_15,
                        FIRST_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[0]),
                        SECOND_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[1]),
                        THIRD_PRIOR_DEFAULT_TRUE.getExpression()}
        };
    }
}
