package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.phoenix.data.formula.FormulaGenerator;
import com.alabs.automation.framework.steps.api.ResponseSteps;
import com.alabs.automation.framework.utilities.DateTimeUtils;
import com.alabs.automation.phoenix.constants.formulaservice.JsonSchemas;
import com.alabs.automation.phoenix.data.common.CommonFileUtils;
import com.alabs.automation.phoenix.data.common.InvalidAuthHeadersDataProvider;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.models.formula.database.mongodb.FormulaConfig;
import com.alabs.automation.phoenix.models.formula.formula.FormulaExpression;
import com.alabs.automation.phoenix.models.formula.formula.FormulaResponse;
import com.alabs.automation.phoenix.steps.formulaservice.formula.DeleteFormulaSteps;
import com.alabs.automation.phoenix.steps.formulaservice.formula.FormulaPreconditionSteps;
import com.alabs.automation.phoenix.steps.formulaservice.formula.ImportFormulaSteps;
import com.alabs.automation.phoenix.tests.formulaservice.BaseFormulasTest;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaValueOption.FIRST_PRIOR_DEFAULT_TRUE;
import static com.alabs.automation.phoenix.constants.formulaservice.FormulaValueOption.SECOND_PRIOR_DEFAULT_FALSE;
import static org.apache.http.HttpStatus.*;

@TmsLink("12045887")
public class ImportFormulaTest extends BaseFormulasTest {
    private final ImportFormulaSteps importFormulaSteps = new ImportFormulaSteps();
    private final DeleteFormulaSteps deleteFormulaSteps = new DeleteFormulaSteps();
    private final List<File> filesToDelete = new ArrayList<>();

    @BeforeTest(groups = {"suite.smoke"})
    public void beforeTest() {
        prepareUserInfo();
        prepareValidDeviceModelRules(userDeviceModel, 1);
    }

    @AfterTest(alwaysRun = true)
    public void deleteFiles() {
        filesToDelete.forEach(File::deleteOnExit);
    }

    @Test(description = "Import formula", priority = Integer.MAX_VALUE, groups = {"suite.smoke"})
    public void importFormulaTest() {
        FormulaResponse formulaResponse = FormulaPreconditionSteps.createFormula(FIRST_PRIOR_DEFAULT_TRUE.getExpression());
        File jsonFile = CommonFileUtils.generateJsonFile(new Object[]{formulaResponse});
        filesToDelete.add(jsonFile);
        Response response = importFormulaSteps.importFormula(jsonFile);
        ResponseSteps.checkStatusCode(response, SC_CREATED);
        ResponseSteps.checkJsonSchema(response, JsonSchemas.FORMULA_IMPORT_RESPONSE);
        importFormulaSteps.checkResponseContainsFormulasWithIds(response, formulaResponse.getId());
    }

    @Test(description = "Send POST /admin/v1/formulas/import with a formula in the file that already exists")
    public void importFormulaWithExistingId() {
        FormulaResponse formulaResponse = FormulaPreconditionSteps.createFormula(FIRST_PRIOR_DEFAULT_TRUE.getExpression());
        FormulaResponse formulaForUpdate = formulaResponse.toBuilder()
                .expressions(new FormulaExpression[]{FIRST_PRIOR_DEFAULT_TRUE.getExpression(),
                        SECOND_PRIOR_DEFAULT_FALSE.getExpression(validRuleIds[0])})
                .description(FormulaGenerator.generateValidDescription())
                .build();
        File jsonFile = CommonFileUtils.generateJsonFile(new Object[]{formulaForUpdate});
        filesToDelete.add(jsonFile);
        FormulaConfig formulaConfig = formulaTableSteps.selectFormulaConfigById(formulaForUpdate.getId());
        importFormulaSteps.checkFormulaResponseOverwritten(formulaConfig, formulaForUpdate);
        importFormulaSteps.readJsonFileContent(jsonFile);
        Response response = importFormulaSteps.importFormula(jsonFile);
        ResponseSteps.checkStatusCode(response, SC_CREATED);
        ResponseSteps.checkJsonSchema(response, JsonSchemas.FORMULA_IMPORT_RESPONSE);
        importFormulaSteps.checkResponseContainsFormulasWithIds(response, formulaResponse.getId());
        importFormulaSteps.checkFormulaResponseCorrespondsToRequest(response.as(FormulaResponse[].class)[0], formulaForUpdate);
    }

    @Test(description = "Send POST /admin/v1/formulas/import and check if 'updated_by' and 'updated_at' values are based on import action")
    public void importFormulaAndCheckUpdatedByAndUpdatedAt() {
        FormulaResponse formulaResponse = FormulaPreconditionSteps.createFormula(FIRST_PRIOR_DEFAULT_TRUE.getExpression());
        File jsonFile = CommonFileUtils.generateJsonFile(new Object[]{formulaResponse});
        filesToDelete.add(jsonFile);
        Date currentDate = DateTimeUtils.getCurrentTime();
        Response response = importFormulaSteps.importFormula(jsonFile);
        ResponseSteps.checkStatusCode(response, SC_CREATED);
        ResponseSteps.checkJsonSchema(response, JsonSchemas.FORMULA_IMPORT_RESPONSE);
        importFormulaSteps.checkUpdatedAtFieldInFormulaResponse(response.as(FormulaResponse[].class)[0], currentDate);
        importFormulaSteps.checkResponseContainsFormulasWithIds(response, formulaResponse.getId());
        importFormulaSteps.checkUpdatedByFieldInFormulaResponse(response.as(FormulaResponse[].class)[0]);
    }

    @Test(description = "Send POST /admin/v1/formulas/import with a file format that differs from json")
    public void importFormulaWithInvalidFileFormat() {
        File file = CommonFileUtils.generateTextFile();
        filesToDelete.add(file);
        Response response = importFormulaSteps.importFormula(file);
        ResponseSteps.checkStatusCode(response, SC_BAD_REQUEST);
    }

    @Test(description = "Send POST /admin/v1/formulas/import with file size bigger than 1 MB")
    public void importFormulaWithLargeFileSize() {
        File file = CommonFileUtils.generateLargeFile();
        filesToDelete.add(file);
        Response response = importFormulaSteps.importFormula(file);
        ResponseSteps.checkStatusCode(response, SC_REQUEST_TOO_LONG);
    }

    @Test(description = "Send POST /admin/v1/formulas/import with a formula in the file that already exists as an archived")
    public void importFormulaWithArchivedId() {
        FormulaResponse formulaToArchive = FormulaPreconditionSteps.createFormula(FIRST_PRIOR_DEFAULT_TRUE.getExpression());
        deleteFormulaSteps.deleteFormula(formulaToArchive.getId());
        File jsonFile = CommonFileUtils.generateJsonFile(new Object[]{formulaToArchive});
        filesToDelete.add(jsonFile);
        Response response = importFormulaSteps.importFormula(jsonFile);
        ResponseSteps.checkStatusCode(response, SC_CONFLICT);
        importFormulaSteps.checkConflictEntitiesInResponse(response, formulaToArchive.getId());
    }

    @Test(description = "Send POST /admin/v1/formulas/import with one of formulas in the file that already exists as an archived (others are active)")
    public void importFormulaWithArchivedAndActive() {
        FormulaResponse formulaResponse = FormulaPreconditionSteps.createFormula(FIRST_PRIOR_DEFAULT_TRUE.getExpression());
        FormulaResponse formulaToArchive = FormulaPreconditionSteps.createFormula(FIRST_PRIOR_DEFAULT_TRUE.getExpression());
        deleteFormulaSteps.deleteFormula(formulaToArchive.getId());
        File jsonFile = CommonFileUtils.generateJsonFile(new Object[]{formulaResponse, formulaToArchive});
        filesToDelete.add(jsonFile);
        Response response = importFormulaSteps.importFormula(jsonFile);
        ResponseSteps.checkStatusCode(response, SC_CONFLICT);
        importFormulaSteps.checkConflictEntitiesInResponse(response, formulaToArchive.getId());
    }

    @Test(description = "Send POST /admin/v1/formulas/import with some validation restrictions in the JSON")
    public void importFormulaWithValidationRestrictions() {
        FormulaResponse formulaResponse = FormulaPreconditionSteps.createFormula(FIRST_PRIOR_DEFAULT_TRUE.getExpression())
                .toBuilder()
                .expressions(null)
                .build();
        File jsonFile = CommonFileUtils.generateJsonFile(new Object[]{formulaResponse});
        filesToDelete.add(jsonFile);
        Response response = importFormulaSteps.importFormula(jsonFile);
        ResponseSteps.checkStatusCode(response, SC_UNPROCESSABLE_ENTITY);
    }

    @Test(description = "Send POST /admin/v1/formulas/import with invalid auth header", dataProviderClass = InvalidAuthHeadersDataProvider.class,
            dataProvider = "invalidAuthRequestSpecifications")
    public void importFormulaWithInvalidAuthHeader(InvalidAuthRequestSpecification invalidAuthRequestSpecification) {
        FormulaResponse formulaResponse = FormulaPreconditionSteps.createFormula(FIRST_PRIOR_DEFAULT_TRUE.getExpression());
        File jsonFile = CommonFileUtils.generateJsonFile(new Object[]{formulaResponse});
        filesToDelete.add(jsonFile);
        Response response = importFormulaSteps.importFormulaWithInvalidAuthHeader(invalidAuthRequestSpecification, jsonFile);
        ResponseSteps.checkStatusCode(response, SC_UNAUTHORIZED);
    }

    @Test(description = "Send POST /admin/v1/formulas/import without 'updated_at', 'created_at' and 'updated_by' fields in json file")
    public void importFormulaWithoutUpdatedFields() {
        FormulaResponse formulaResponse = FormulaPreconditionSteps.createFormula(FIRST_PRIOR_DEFAULT_TRUE.getExpression());
        FormulaResponse modifiedFormula = importFormulaSteps.deleteFieldsFromFormulaResponse(formulaResponse);
        File jsonFile = CommonFileUtils.generateFileExcludingNulls(new Object[]{modifiedFormula});
        filesToDelete.add(jsonFile);
        importFormulaSteps.readJsonFileContent(jsonFile);
        Response response = importFormulaSteps.importFormula(jsonFile);
        ResponseSteps.checkStatusCode(response, SC_CREATED);
        ResponseSteps.checkJsonSchema(response, JsonSchemas.FORMULA_IMPORT_RESPONSE);
        importFormulaSteps.checkResponseContainsFormulasWithIds(response, modifiedFormula.getId());
    }
}
