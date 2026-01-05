package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.phoenix.data.formula.ExistingFormulaEvaluatorRequestGenerator;
import com.alabs.automation.phoenix.data.formula.FormulaGenerator;
import com.alabs.automation.phoenix.data.formula.FormulaSnapshotGenerator;
import com.alabs.automation.framework.steps.api.ResponseSteps;
import com.alabs.automation.framework.utilities.MathUtils;
import com.alabs.automation.phoenix.models.formula.formula.ExistingFormulaEvaluatorRequest;
import com.alabs.automation.phoenix.models.formula.formula.FormulaResponse;
import com.alabs.automation.phoenix.models.formula.snapshot.FormulaSnapshotResponse;
import com.alabs.automation.phoenix.steps.formulaservice.formula.EvaluateExistingFormulaSteps;
import com.alabs.automation.phoenix.steps.formulaservice.formula.FormulaPreconditionSteps;
import com.alabs.automation.phoenix.steps.formulaservice.snapshot.CreateSnapshotSteps;
import com.alabs.automation.phoenix.tests.formulaservice.BaseFormulasTest;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaValueOption.*;
import static org.apache.http.HttpStatus.SC_OK;

@TmsLink("9030389")
public class EvaluateExistingFormulaTest extends BaseFormulasTest {
    private final EvaluateExistingFormulaSteps evaluateExistingFormulaSteps = new EvaluateExistingFormulaSteps();
    private final CreateSnapshotSteps createSnapshotSteps = new CreateSnapshotSteps();
    private FormulaResponse firstFormula, secondFormula;
    private String snapshotId;

    @BeforeTest(groups = {"suite.smoke"})
    public void beforeTest() {
        prepareUserInfo();
        prepareValidDeviceModelRules(userDeviceModel, VALID_RULES_AMOUNT);
        prepareInvalidDeviceModelRules(userDeviceModel, 1);
        firstFormula = prepareFormulaWithTwoExpressions();
        createSnapshot();
        secondFormula = FormulaPreconditionSteps.createFormula(
                FIRST_PRIOR_DEFAULT_TRUE.getExpression(validRuleIds[1]),
                THIRD_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[0]));
    }

    @Test(description = "Formula - Internal: Evaluate existing formula with formula id as parameter",
            priority = Integer.MAX_VALUE, groups = {"suite.smoke"})
    public void evaluateExistingFormulaTest() {
        ExistingFormulaEvaluatorRequest formulaEvaluatorRequest = ExistingFormulaEvaluatorRequestGenerator.prepareFormulaEvaluatorRequest(
                gameAccountId, snapshotId, firstFormula.getId(), secondFormula.getId());
        Response response = evaluateExistingFormulaSteps.evaluateFormula(formulaEvaluatorRequest);
        ResponseSteps.checkStatusCode(response, SC_OK);
        evaluateExistingFormulaSteps.checkResultInResponse(formulaEvaluatorRequest, response, validRuleIds,
                firstFormula, secondFormula);
    }

    @Test(description = "Send POST /internal/v1/formulas/process", dataProvider = "validRequestProvider")
    public void evaluateFormulaWithCommonRequest(String name, ExistingFormulaEvaluatorRequest request,
                                                 FormulaResponse formulaResponse) {
        Response response = evaluateExistingFormulaSteps.evaluateFormula(request);
        ResponseSteps.checkStatusCode(response, SC_OK);
        evaluateExistingFormulaSteps.checkResultInResponse(request, response, validRuleIds, formulaResponse);
    }

    @Test(description = "Send POST /internal/v1/formulas/process with a non-existing formula id")
    public void evaluateFormulaWithNonExistingFormulaId() {
        ExistingFormulaEvaluatorRequest requestWithNonExistingFormulaId = ExistingFormulaEvaluatorRequestGenerator.prepareFormulaEvaluatorRequest(
                gameAccountId, snapshotId, FormulaGenerator.generateFormulaId());
        Response response = evaluateExistingFormulaSteps.evaluateFormula(requestWithNonExistingFormulaId);
        ResponseSteps.checkStatusCode(response, SC_OK);
        ResponseSteps.checkEmptyArrayDisplayed(response);
    }

    @Test(description = "Send POST /internal/v1/formulas/process with a non-existing snapshot id")
    public void evaluateFormulaWithNonExistingSnapshotId() {
        ExistingFormulaEvaluatorRequest requestWithNonExistingSnapshotId = ExistingFormulaEvaluatorRequestGenerator.prepareFormulaEvaluatorRequest(
                gameAccountId, UUID.randomUUID().toString(), firstFormula.getId());
        Response response = evaluateExistingFormulaSteps.evaluateFormula(requestWithNonExistingSnapshotId);
        ResponseSteps.checkStatusCode(response, SC_OK);
        formulaSnapshotTableSteps.checkSnapshotIsCreated(gameAccountId);
    }

    @Test(description = "Send POST /internal/v1/formulas/process with a formula that contains additional_attribute")
    public void evaluateFormulaWithAdditionalAttribute() {
        int attribute = MathUtils.calculate(firstFormula.getExpressions()[0].getExpression());
        ExistingFormulaEvaluatorRequest requestWithAdditionalAttribute = ExistingFormulaEvaluatorRequestGenerator.prepareFormulaEvaluatorRequest(
                gameAccountId, snapshotId, attribute, firstFormula.getId());
        Response response = evaluateExistingFormulaSteps.evaluateFormula(requestWithAdditionalAttribute);
        ResponseSteps.checkStatusCode(response, SC_OK);
        evaluateExistingFormulaSteps.checkResultInResponseIsEqualToAdditionalAttribute(response, attribute);
    }

    private void createSnapshot() {
        Response response = createSnapshotSteps.createSnapshot(FormulaSnapshotGenerator.prepareValidSnapshotRequest(gameAccountId));
        ResponseSteps.checkStatusCode(response, SC_OK);
        FormulaSnapshotResponse[] snapshotResponse = response.as(FormulaSnapshotResponse[].class);
        formulaSnapshotTableSteps.checkSnapshotConfigMatchesWithDatabase(snapshotResponse, true);
        snapshotId = snapshotResponse[0].getId();
    }

    @DataProvider
    private Object[][] validRequestProvider() {
        ExistingFormulaEvaluatorRequest requestWithOneFormulaId = ExistingFormulaEvaluatorRequestGenerator.prepareFormulaEvaluatorRequest(
                gameAccountId, snapshotId, firstFormula.getId());
        FormulaResponse formulaResponseWithRuleTrueForCurrentUser = FormulaPreconditionSteps.createFormula(
                FIRST_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]),
                SECOND_PRIOR_DEFAULT_TRUE.getExpression());
        ExistingFormulaEvaluatorRequest requestWithRuleTrueForCurrentUser = ExistingFormulaEvaluatorRequestGenerator.prepareFormulaEvaluatorRequest(
                gameAccountId, snapshotId, formulaResponseWithRuleTrueForCurrentUser.getId());
        FormulaResponse formulaResponseWithRuleFalseForCurrentUser = FormulaPreconditionSteps.createFormula(
                FIRST_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[0]),
                SECOND_PRIOR_DEFAULT_TRUE.getExpression());
        ExistingFormulaEvaluatorRequest requestWithRuleFalseForCurrentUser = ExistingFormulaEvaluatorRequestGenerator.prepareFormulaEvaluatorRequest(
                gameAccountId, snapshotId, formulaResponseWithRuleFalseForCurrentUser.getId());
        FormulaResponse formulaResponseWithTwoRuleTrueForCurrentUser = FormulaPreconditionSteps.createFormula(
                FIRST_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0]),
                SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[1]), THIRD_PRIOR_DEFAULT_TRUE.getExpression());
        ExistingFormulaEvaluatorRequest requestWithTwoRuleTrueForCurrentUser = ExistingFormulaEvaluatorRequestGenerator.prepareFormulaEvaluatorRequest(
                gameAccountId, snapshotId, formulaResponseWithTwoRuleTrueForCurrentUser.getId());
        return new Object[][]{
                {"with one formula id", requestWithOneFormulaId, firstFormula},
                {"with formula in which rule is true for the current user (default expression has the lowest priority)",
                        requestWithRuleTrueForCurrentUser, formulaResponseWithRuleTrueForCurrentUser},
                {"with formula in which rule is false for the current user (default expression has the lowest priority)",
                        requestWithRuleFalseForCurrentUser, formulaResponseWithRuleFalseForCurrentUser},
                {"with formula in which rule is true in several expressions for the current user " +
                        "(default expression has the lowest priority)", requestWithTwoRuleTrueForCurrentUser,
                        formulaResponseWithTwoRuleTrueForCurrentUser}
        };
    }
}
