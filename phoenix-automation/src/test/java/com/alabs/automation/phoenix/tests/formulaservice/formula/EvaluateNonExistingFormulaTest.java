package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.phoenix.data.formula.NonExistingFormulaEvaluatorExpressionGenerator;
import com.alabs.automation.framework.steps.api.ResponseSteps;
import com.alabs.automation.phoenix.data.common.InvalidAuthHeadersDataProvider;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.models.formula.formula.NonExistingFormulaEvaluatorResponse;
import com.alabs.automation.phoenix.steps.formulaservice.formula.EvaluateNonExistingFormulaSteps;
import com.alabs.automation.phoenix.tests.formulaservice.BaseFormulasTest;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.apache.http.HttpStatus.*;

@TmsLink("9030391")
public class EvaluateNonExistingFormulaTest extends BaseFormulasTest {
    private final EvaluateNonExistingFormulaSteps evaluateNonExistingFormulaSteps = new EvaluateNonExistingFormulaSteps();

    @BeforeTest(groups = {"suite.smoke"})
    public void beforeTest() {
        prepareUserInfo();
    }

    @Test(description = "Formula - Admin: Evaluate formula that doesn't exist yet, with expressions as parameter",
            priority = Integer.MAX_VALUE, groups = {"suite.smoke"})
    public void evaluateNonExistingFormulaTest() {
        Response response = evaluateNonExistingFormulaSteps.evaluateFormula(
                NonExistingFormulaEvaluatorExpressionGenerator.prepareEvaluatorWithValidExpressions(gameAccountId));
        ResponseSteps.checkStatusCode(response, SC_OK);
        evaluateNonExistingFormulaSteps.checkResultInResponse(response);
    }

    @Test(description = "Send POST /admin/v1/formulas/evaluate call with one expression")
    public void evaluateFormulaWithOneExpression() {
        Response response = evaluateNonExistingFormulaSteps.evaluateFormula(
                NonExistingFormulaEvaluatorExpressionGenerator.prepareEvaluatorWithOneValidExpression(gameAccountId));
        ResponseSteps.checkStatusCode(response, SC_OK);
        evaluateNonExistingFormulaSteps.checkResultInResponse(response);
    }

    @Test(description = "Send POST /admin/v1/formulas/evaluate call with invalid expression")
    public void evaluateFormulaWithInvalidExpression() {
        Response response = evaluateNonExistingFormulaSteps.evaluateFormula(
                NonExistingFormulaEvaluatorExpressionGenerator.prepareEvaluatorWithInvalidExpression(gameAccountId));
        ResponseSteps.checkStatusCode(response, SC_OK);
        evaluateNonExistingFormulaSteps.checkErrorMessageInResponse(
                response.as(NonExistingFormulaEvaluatorResponse[].class), NonExistingFormulaEvaluatorExpressionGenerator.INVALID_EXPRESSION);
    }

    @Test(description = "Send POST /admin/v1/formulas/evaluate call with invalid auth header",
            dataProviderClass = InvalidAuthHeadersDataProvider.class, dataProvider = "invalidAuthRequestSpecifications")
    public void evaluateFormulaWithInvalidHeader(InvalidAuthRequestSpecification invalidAuthRequestSpecification) {
        Response response = evaluateNonExistingFormulaSteps.evaluateFormulaWithInvalidHeader(
                invalidAuthRequestSpecification,
                NonExistingFormulaEvaluatorExpressionGenerator.prepareEvaluatorWithValidExpressions(gameAccountId));
        ResponseSteps.checkStatusCode(response, SC_UNAUTHORIZED);
    }
}
