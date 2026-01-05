package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.framework.steps.api.ResponseSteps;
import com.alabs.automation.phoenix.constants.formulaservice.JsonSchemas;
import com.alabs.automation.phoenix.data.common.InvalidAuthHeadersDataProvider;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.data.common.InvalidIdsProvider;
import com.alabs.automation.phoenix.data.formula.FormulaGenerator;
import com.alabs.automation.phoenix.models.common.error.ErrorResponse;
import com.alabs.automation.phoenix.models.formula.formula.FormulaExportRequest;
import com.alabs.automation.phoenix.models.formula.formula.FormulaExportResponse;
import com.alabs.automation.phoenix.models.formula.formula.FormulaResponse;
import com.alabs.automation.phoenix.steps.common.ErrorResponseSteps;
import com.alabs.automation.phoenix.steps.formulaservice.database.tables.FormulaTableSteps;
import com.alabs.automation.phoenix.steps.formulaservice.formula.DeleteFormulaSteps;
import com.alabs.automation.phoenix.steps.formulaservice.formula.ExportFormulaSteps;
import com.alabs.automation.phoenix.steps.formulaservice.formula.FormulaPreconditionSteps;
import com.alabs.automation.phoenix.tests.formulaservice.BaseFormulasTest;
import io.qameta.allure.Allure;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.alabs.automation.phoenix.constants.common.AllureConstants.FIRST_ARGUMENT_NAME;
import static com.alabs.automation.phoenix.constants.formulaservice.FormulaValueOption.FIRST_PRIOR_DEFAULT_TRUE;
import static org.apache.http.HttpStatus.*;

@TmsLink("12045887")
public class ExportFormulaTest extends BaseFormulasTest {
    private final ExportFormulaSteps exportFormulaSteps = new ExportFormulaSteps();
    private final DeleteFormulaSteps deleteFormulaSteps = new DeleteFormulaSteps();
    private final FormulaTableSteps formulaTableSteps = new FormulaTableSteps();
    private FormulaResponse formulaResponse;

    @BeforeTest(groups = {"suite.smoke"})
    public void beforeTest() {
        prepareUserInfo();
        prepareValidDeviceModelRules(userDeviceModel, VALID_RULES_AMOUNT);
        formulaResponse = prepareFormulaWithTwoExpressions();
    }

    @Test(description = "Export formula", priority = Integer.MAX_VALUE, groups = {"suite.smoke"})
    public void exportFormulaTest() {
        FormulaExportRequest request = FormulaGenerator.prepareFormulaExportRequest(formulaResponse.getId());
        Response response = exportFormulaSteps.exportFormula(request);
        ResponseSteps.checkStatusCode(response, SC_OK);
        ResponseSteps.checkJsonSchema(response, JsonSchemas.FORMULA_EXPORT_RESPONSE);
        exportFormulaSteps.checkResponseContainsFormulas(response, formulaResponse.getId());
    }

    @Test(description = "Send POST /admin/v1/formulas/export without request body")
    public void exportFormulaWithoutRequestBody() {
        Response response = exportFormulaSteps.exportFormulaWithoutRequestBody();
        ResponseSteps.checkStatusCode(response, SC_OK);
        ResponseSteps.checkJsonSchema(response, JsonSchemas.FORMULA_EXPORT_RESPONSE);
        FormulaExportResponse[] formulaExportResponse = response.as(FormulaExportResponse[].class);
        exportFormulaSteps.checkResponseContainsAllExistingFormulas(formulaExportResponse, formulaTableSteps.countFormulaConfigsById(StringUtils.EMPTY));
    }

    @Test(description = "Send POST /admin/v1/formulas/export with empty ids")
    public void exportFormulaWithEmptyIds() {
        String[] emptyIds = new String[0];
        FormulaExportRequest request = FormulaGenerator.prepareFormulaExportRequest(emptyIds);
        Response response = exportFormulaSteps.exportFormula(request);
        ResponseSteps.checkStatusCode(response, SC_BAD_REQUEST);
    }

    @Test(description = "Send POST /admin/v1/formulas/export one non-existent formula id")
    public void exportFormulaWithNonExistingId() {
        FormulaExportRequest request = FormulaGenerator.prepareFormulaExportRequest(formulaResponse.getId(), FormulaGenerator.generateFormulaId());
        Response response = exportFormulaSteps.exportFormula(request);
        ResponseSteps.checkStatusCode(response, SC_OK);
        ResponseSteps.checkJsonSchema(response, JsonSchemas.FORMULA_EXPORT_RESPONSE);
        exportFormulaSteps.checkResponseContainsFormulas(response, formulaResponse.getId());
    }

    @Test(description = "Send POST /admin/v1/formulas/export with archived formula id")
    public void exportFormulaWithArchivedId() {
        FormulaResponse formulaToArchive = FormulaPreconditionSteps.createFormula(FIRST_PRIOR_DEFAULT_TRUE.getExpression());
        deleteFormulaSteps.deleteFormula(formulaToArchive.getId());
        FormulaExportRequest request = FormulaGenerator.prepareFormulaExportRequest(formulaToArchive.getId());
        Response response = exportFormulaSteps.exportFormula(request);
        ResponseSteps.checkStatusCode(response, SC_OK);
        ResponseSteps.checkEmptyArrayDisplayed(response);
    }

    @Test(description = "Send POST /admin/v1/formulas/export with invalid auth header", dataProviderClass = InvalidAuthHeadersDataProvider.class, dataProvider = "invalidAuthRequestSpecifications")
    public void exportFormulaWithInvalidAuthHeader(InvalidAuthRequestSpecification invalidAuthRequestSpecification) {
        FormulaExportRequest request = FormulaGenerator.prepareFormulaExportRequest(formulaResponse.getId());
        Response response = exportFormulaSteps.exportFormulaWithInvalidAuthHeader(invalidAuthRequestSpecification, request);
        ResponseSteps.checkStatusCode(response, SC_UNAUTHORIZED);
    }

    @Test(description = "Send POST /admin/v1/formulas/export with invalid 'ids' value", dataProviderClass = InvalidIdsProvider.class, dataProvider = "invalidIdsProvider")
    public void exportFormulaWithInvalidIdsTest(String name, String invalidId, String idValidationMessage) {
        Allure.parameter(FIRST_ARGUMENT_NAME, name);
        FormulaExportRequest request = FormulaGenerator.prepareFormulaExportRequest(invalidId);
        Response response = exportFormulaSteps.exportFormula(request);
        ResponseSteps.checkStatusCode(response, SC_BAD_REQUEST);
        ErrorResponseSteps.checkIdValidationMessage(response.as(ErrorResponse.class), idValidationMessage);
    }
}
