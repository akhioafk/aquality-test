package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.phoenix.data.formula.FormulaGenerator;
import com.alabs.automation.framework.steps.api.ResponseSteps;
import com.alabs.automation.phoenix.data.common.InvalidAuthHeadersDataProvider;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.data.common.InvalidIdsProvider;
import com.alabs.automation.phoenix.models.common.error.ErrorResponse;
import com.alabs.automation.phoenix.models.formula.formula.FormulaExpression;
import com.alabs.automation.phoenix.models.formula.formula.FormulaRequest;
import com.alabs.automation.phoenix.models.formula.formula.FormulaResponse;
import com.alabs.automation.phoenix.steps.common.ErrorResponseSteps;
import com.alabs.automation.phoenix.steps.formulaservice.formula.CreateFormulaSteps;
import com.alabs.automation.phoenix.steps.formulaservice.formula.FormulaResponseSteps;
import com.alabs.automation.phoenix.tests.formulaservice.BaseFormulasTest;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaValueOption.*;
import static org.apache.http.HttpStatus.*;

@TmsLink("9030388")
public class CreateFormulaTest extends BaseFormulasTest {
    private final CreateFormulaSteps createFormulaSteps = new CreateFormulaSteps();

    @BeforeTest(groups = {"suite.smoke"})
    public void beforeTest() {
        prepareUserInfo();
        prepareValidDeviceModelRules(userDeviceModel, VALID_RULES_AMOUNT);
    }

    @Test(description = "Formula - Admin: create formula group", priority = Integer.MAX_VALUE, groups = {"suite.smoke"})
    public void createFormulaTest() {
        createFormulaAndCheckResponse(FormulaGenerator.prepareFormula(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression(),
                SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]),
                THIRD_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[1])));
    }

    @Test(description = "Send POST /admin/v1/formulas", dataProvider = "formulaExpressionOptionProviderForCreate")
    public void createValidFormula(String name, FormulaRequest formulaRequest) {
        createFormulaAndCheckResponse(formulaRequest);
    }

    @Test(description = "Send POST /admin/v1/formulas with an existing 'id' value")
    public void createFormulaWithExistingId() {
        FormulaResponse formulaResponse = createFormulaAndCheckResponse(FormulaGenerator.prepareFormula(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression()));
        createFormulaAndCheckStatus(FormulaGenerator.prepareFormula(formulaResponse.getId(),
                FIRST_PRIOR_DEFAULT_TRUE.getExpression()), SC_CONFLICT);
    }

    @Test(description = "Send POST /admin/v1/formulas", dataProvider = "invalidFormulaExpressionOptionProviderForCreate")
    public void createInvalidFormula(String name, FormulaRequest formulaRequest) {
        createFormulaAndCheckStatus(formulaRequest, SC_BAD_REQUEST);
    }

    @Test(description = "Send POST /admin/v1/formulas with an invalid 'id'",
            dataProviderClass = InvalidIdsProvider.class, dataProvider = "invalidIdsProvider")
    public void createFormulaWithInvalidId(String invalidIdName, String invalidId, String idValidationMessage) {
        FormulaRequest request = FormulaGenerator.prepareFormula(invalidId,
                FIRST_PRIOR_DEFAULT_TRUE.getExpression());
        Response response = createFormulaAndCheckStatus(request, SC_BAD_REQUEST);
        ErrorResponseSteps.checkIdValidationMessage(response.as(ErrorResponse.class), idValidationMessage);
    }

    @Test(description = "Send POST /admin/v1/formulas call with invalid auth header",
            dataProviderClass = InvalidAuthHeadersDataProvider.class, dataProvider = "invalidAuthRequestSpecifications")
    public void createFormulaWithInvalidHeader(InvalidAuthRequestSpecification invalidAuthRequestSpecification) {
        FormulaRequest request = FormulaGenerator.prepareFormula(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression(),
                SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]),
                THIRD_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[1]));
        Response response = createFormulaSteps.createFormulaWithInvalidHeader(invalidAuthRequestSpecification, request);
        ResponseSteps.checkStatusCode(response, SC_UNAUTHORIZED);
    }

    private Response createFormulaAndCheckStatus(FormulaRequest request, int expectedStatus) {
        Response response = createFormulaSteps.createFormula(request);
        ResponseSteps.checkStatusCode(response, expectedStatus);
        return response;
    }

    private FormulaResponse createFormulaAndCheckResponse(FormulaRequest request) {
        Response response = createFormulaAndCheckStatus(request, SC_CREATED);
        FormulaResponse formulaResponse = response.as(FormulaResponse.class);
        FormulaResponseSteps.checkFormulaResponseCorrespondToRequestAfterCreate(formulaResponse, request);
        formulaTableSteps.checkFormulaConfigMatchesWithDatabase(formulaResponse);
        return formulaResponse;
    }

    @DataProvider
    private Object[][] formulaExpressionOptionProviderForCreate() {
        FormulaRequest formulaWithOnlyOneExpression = FormulaGenerator.prepareFormula(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression());
        FormulaRequest formulaWithoutDescription = FormulaGenerator.prepareFormulaWithoutDescription(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression(),
                SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]),
                THIRD_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[1]));
        return new Object[][]{
                {"with only one expression object", formulaWithOnlyOneExpression},
                {"without optional body parameters", formulaWithoutDescription}
        };
    }

    @DataProvider
    private Object[][] invalidFormulaExpressionOptionProviderForCreate() {
        FormulaRequest formulaWithInvalidDescription = FormulaGenerator.prepareFormulaWithInvalidDescription(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression());
        FormulaRequest formulaWithTwoExpressionOfDefaultTrueParam = FormulaGenerator.prepareFormula(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression(),
                SECOND_PRIOR_DEFAULT_TRUE.getExpression());
        FormulaRequest formulaWithSamePriorityExpression = FormulaGenerator.prepareFormula(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression(),
                FIRST_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]));
        FormulaRequest formulaWithoutExpressionWithDefaultTrue = FormulaGenerator.prepareFormula(
                SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]),
                THIRD_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[1]));
        FormulaRequest formulaWithoutExpression = FormulaGenerator.prepareFormula(
                new FormulaExpression());
        FormulaRequest formulaWithoutRuleIdForExpressionWithDefaultFalse = FormulaGenerator.prepareFormula(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression(validRuleIds[0]),
                SECOND_PRIOR_DEFAULT_FALSE.getExpression());
        return new Object[][]{
                {"with a 'description' exceeding 1000 characters", formulaWithInvalidDescription},
                {"with two expressions having 'default':'true' parameter", formulaWithTwoExpressionOfDefaultTrueParam},
                {"with same 'priority' expression", formulaWithSamePriorityExpression},
                {"without expression with a 'default':'true' parameter", formulaWithoutExpressionWithDefaultTrue},
                {"without expression", formulaWithoutExpression},
                {"without 'rule_id' for expression with 'default':'false'", formulaWithoutRuleIdForExpressionWithDefaultFalse}
        };
    }
}
