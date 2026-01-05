package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.phoenix.data.formula.FormulaGenerator;
import com.alabs.automation.framework.steps.api.ResponseSteps;
import com.alabs.automation.framework.utilities.StringUtils;
import com.alabs.automation.phoenix.data.common.InvalidAuthHeadersDataProvider;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.models.formula.formula.FormulaExpression;
import com.alabs.automation.phoenix.models.formula.formula.FormulaRequest;
import com.alabs.automation.phoenix.models.formula.formula.FormulaResponse;
import com.alabs.automation.phoenix.steps.formulaservice.formula.FormulaResponseSteps;
import com.alabs.automation.phoenix.steps.formulaservice.formula.UpdateFormulaSteps;
import com.alabs.automation.phoenix.tests.formulaservice.BaseFormulasTest;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaGenerationConstants.MAX_ID_LENGTH;
import static com.alabs.automation.phoenix.constants.formulaservice.FormulaValueOption.*;
import static org.apache.http.HttpStatus.*;

@TmsLink("9030388")
public class UpdateFormulaTest extends BaseFormulasTest {
    private final UpdateFormulaSteps updateFormulaSteps = new UpdateFormulaSteps();
    private FormulaResponse formulaResponse;

    @BeforeTest(groups = {"suite.smoke"})
    public void beforeTest() {
        prepareUserInfo();
        prepareValidDeviceModelRules(userDeviceModel, VALID_RULES_AMOUNT);
        formulaResponse = prepareFormulaWithTwoExpressions();
    }

    @Test(description = "Formula - Admin: modify formula group", priority = Integer.MAX_VALUE, groups = {"suite.smoke"})
    public void updateFormulaTest() {
        updateFormulaAndCheckResponse(FormulaGenerator.prepareFormulaWithoutId(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression(),
                SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0])));
    }

    @Test(description = "Send PUT /admin/v1/formulas/{formula_id}", dataProvider = "formulaExpressionOptionProviderForUpdate")
    public void updateValidFormulaParameters(String name, FormulaRequest formulaRequest) {
        updateFormulaAndCheckResponse(formulaRequest);
    }

    @Test(description = "Send PUT /admin/v1/formulas/{formula_id}", dataProvider = "invalidFormulaExpressionOptionProviderForUpdate")
    public void updateInvalidFormulaParameters(String name, FormulaRequest formulaRequest) {
        updateFormulaAndCheckStatus(formulaRequest, SC_BAD_REQUEST);
    }

    @Test(description = "Send PUT /admin/v1/formulas/{formula_id} with a non-existing id")
    public void updateFormulaForNonExistingId() {
        Response response = updateFormulaSteps.updateFormula(StringUtils.randomAlphanumeric(MAX_ID_LENGTH),
                FormulaGenerator.prepareFormulaWithoutId(FIRST_PRIOR_DEFAULT_TRUE.getExpression(),
                        SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0])));
        ResponseSteps.checkStatusCode(response, SC_NOT_FOUND);
    }

    @Test(description = "Send PUT /admin/v1/formulas/{formula_id} call with invalid auth header",
            dataProviderClass = InvalidAuthHeadersDataProvider.class, dataProvider = "invalidAuthRequestSpecifications")
    public void updateFormulaWithInvalidHeader(InvalidAuthRequestSpecification invalidAuthRequestSpecification) {
        FormulaRequest request = FormulaGenerator.prepareFormulaWithoutId(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression(),
                SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]));
        Response response = updateFormulaSteps.updateFormulaWithInvalidHeader(invalidAuthRequestSpecification,
                formulaResponse.getId(), request);
        ResponseSteps.checkStatusCode(response, SC_UNAUTHORIZED);
    }

    private Response updateFormulaAndCheckStatus(FormulaRequest request, int expectedStatus) {
        Response response = updateFormulaSteps.updateFormula(formulaResponse.getId(), request);
        ResponseSteps.checkStatusCode(response, expectedStatus);
        return response;
    }

    private void updateFormulaAndCheckResponse(FormulaRequest request) {
        Response response = updateFormulaAndCheckStatus(request, SC_OK);
        FormulaResponse formulaResponse = response.as(FormulaResponse.class);
        FormulaResponseSteps.checkFormulaResponseCorrespondToRequestAfterUpdate(formulaResponse, request);
        formulaTableSteps.checkFormulaConfigMatchesWithDatabase(formulaResponse);
        FormulaResponseSteps.checkUpdatedAtParameterChanged(formulaResponse, this.formulaResponse.getUpdatedAt());
    }

    @DataProvider
    private Object[][] formulaExpressionOptionProviderForUpdate() {
        FormulaRequest formulaWithValidBody = FormulaGenerator.prepareFormulaWithoutId(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression(validRuleIds[0]),
                SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[1]));
        FormulaRequest formulaWithoutDescription = FormulaGenerator.prepareFormulaWithExpressionOnly(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression(),
                SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]));
        return new Object[][]{
                {"to update several/all parameters", formulaWithValidBody},
                {"without optional body parameters", formulaWithoutDescription}
        };
    }

    @DataProvider
    private Object[][] invalidFormulaExpressionOptionProviderForUpdate() {
        FormulaRequest formulaWithValidBodyToUpdateId = FormulaGenerator.prepareFormula(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression(),
                SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]));
        FormulaRequest formulaWithInvalidDescription = FormulaGenerator.prepareFormulaWithInvalidDescription(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression(),
                SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]));
        FormulaRequest formulaWithTwoExpressionOfDefaultTrueParam = FormulaGenerator.prepareFormulaWithoutId(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression(),
                SECOND_PRIOR_DEFAULT_TRUE.getExpression());
        FormulaRequest formulaWithSamePriorityExpression = FormulaGenerator.prepareFormulaWithoutId(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression(),
                FIRST_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]));
        FormulaRequest formulaWithoutExpressionWithDefaultTrue = FormulaGenerator.prepareFormulaWithoutId(
                SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]),
                THIRD_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[1]));
        FormulaRequest formulaWithoutExpression = FormulaGenerator.prepareFormulaWithoutId(
                new FormulaExpression(),
                SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]));
        FormulaRequest formulaWithoutRuleIdForExpressionWithDefaultFalse = FormulaGenerator.prepareFormulaWithoutId(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression(validRuleIds[0]),
                SECOND_PRIOR_DEFAULT_FALSE.getExpression());
        return new Object[][]{
                {"to update the 'id' value", formulaWithValidBodyToUpdateId},
                {"with 'description' exceeding 1000 characters", formulaWithInvalidDescription},
                {"with two expressions having 'default':'true' parameter", formulaWithTwoExpressionOfDefaultTrueParam},
                {"with the same 'priority' expression", formulaWithSamePriorityExpression},
                {"without expression with a 'default':'true' parameter", formulaWithoutExpressionWithDefaultTrue},
                {"without expression", formulaWithoutExpression},
                {"without 'rule_id' for expression with 'default':'false'", formulaWithoutRuleIdForExpressionWithDefaultFalse}
        };
    }
}
