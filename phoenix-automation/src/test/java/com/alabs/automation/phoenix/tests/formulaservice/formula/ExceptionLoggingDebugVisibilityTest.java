package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.framework.steps.api.ResponseSteps;
import com.alabs.automation.phoenix.constants.formulaservice.FormulaValueOption;
import com.alabs.automation.phoenix.models.formula.formula.ExistingFormulaEvaluatorRequest;
import com.alabs.automation.phoenix.models.formula.formula.FormulaExpression;
import com.alabs.automation.phoenix.models.formula.snapshot.FormulaSnapshotResponse;
import com.alabs.automation.phoenix.steps.formulaservice.constant.ConstantPreconditionSteps;
import com.alabs.automation.phoenix.steps.formulaservice.database.tables.FormulaConstantTableSteps;
import com.alabs.automation.phoenix.steps.formulaservice.formula.EvaluateExistingFormulaSteps;
import com.alabs.automation.phoenix.steps.formulaservice.formula.FormulaPreconditionSteps;
import com.alabs.automation.phoenix.steps.formulaservice.formula.InvalidateAllInMemoryCachesForFormulaSteps;
import com.alabs.automation.phoenix.steps.formulaservice.snapshot.CreateSnapshotSteps;
import com.alabs.automation.phoenix.tests.formulaservice.BaseFormulasTest;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.alabs.automation.phoenix.data.formula.ExistingFormulaEvaluatorRequestGenerator.prepareFormulaEvaluatorRequest;
import static com.alabs.automation.phoenix.data.formula.FormulaSnapshotGenerator.prepareValidSnapshotRequest;
import static org.apache.http.HttpStatus.SC_OK;

@TmsLink("11876422")
public class ExceptionLoggingDebugVisibilityTest extends BaseFormulasTest {
    private final FormulaConstantTableSteps formulaConstantTableSteps = new FormulaConstantTableSteps();
    private final InvalidateAllInMemoryCachesForFormulaSteps invalidateAllInMemoryCachesForFormulaSteps = new InvalidateAllInMemoryCachesForFormulaSteps();
    private final CreateSnapshotSteps createSnapshotSteps = new CreateSnapshotSteps();
    private final EvaluateExistingFormulaSteps evaluateExistingFormulaSteps = new EvaluateExistingFormulaSteps();
    private String snapshotId;
    private String constantId;
    private String formulaId;

    @BeforeTest(groups = {"suite.smoke"})
    public void beforeTest() {
        prepareUserInfo();
        snapshotId = createSnapshot();
        constantId = ConstantPreconditionSteps.createConstant().getId();
        FormulaExpression formulaExpression = FormulaValueOption.FIRST_PRIOR_DEFAULT_TRUE.getConstantExpression(constantId);
        formulaId = FormulaPreconditionSteps.createFormula(formulaExpression).getId();
    }

    @Test(description = "Verify that exception logging is visible in debug during formula processing",
            groups = {"suite.smoke"}, priority = Integer.MAX_VALUE)
    public void processFormulaTest() {
        formulaConstantTableSteps.deleteConstantConfigById(constantId);
        Response invalidateCacheResponse = invalidateAllInMemoryCachesForFormulaSteps.invalidateCache();
        ResponseSteps.checkStatusCode(invalidateCacheResponse, HttpStatus.SC_OK);
        ResponseSteps.checkNoDataDisplayed(invalidateCacheResponse);

        ExistingFormulaEvaluatorRequest formulaEvaluatorRequest = prepareFormulaEvaluatorRequest(
                gameAccountId, snapshotId, formulaId);
        Response response = evaluateExistingFormulaSteps.evaluateFormula(formulaEvaluatorRequest);
        ResponseSteps.checkStatusCode(response, SC_OK);
        evaluateExistingFormulaSteps.checkResponseReason(response, formulaId, constantId);
    }

    private String createSnapshot() {
        Response response = createSnapshotSteps.createSnapshot(prepareValidSnapshotRequest(gameAccountId));
        ResponseSteps.checkStatusCode(response, SC_OK);
        FormulaSnapshotResponse[] snapshotResponse = response.as(FormulaSnapshotResponse[].class);
        formulaSnapshotTableSteps.checkSnapshotConfigMatchesWithDatabase(snapshotResponse, true);
        return snapshotResponse[0].getId();
    }
}
