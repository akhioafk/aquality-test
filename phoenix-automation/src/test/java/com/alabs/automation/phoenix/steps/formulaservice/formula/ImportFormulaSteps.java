package com.alabs.automation.phoenix.steps.formulaservice.formula;

import com.alabs.automation.framework.utilities.FileUtils;
import com.alabs.automation.phoenix.constants.common.Parameters;
import com.alabs.automation.phoenix.constants.formulaservice.Endpoints;
import com.alabs.automation.phoenix.data.common.CommonFileUtils;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.models.formula.database.mongodb.FormulaConfig;
import com.alabs.automation.phoenix.models.formula.formula.ArchivedFormulaImportResponse;
import com.alabs.automation.phoenix.models.formula.formula.FormulaResponse;
import com.alabs.automation.phoenix.steps.formulaservice.BaseFormulaSteps;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

public final class ImportFormulaSteps extends BaseFormulaSteps {

    private Response importFormula(RequestSpecification requestSpecification, File importRequestFile) {
        return requestSpecification
                .and()
                .multiPart(Parameters.JSON_FILE, importRequestFile)
                .contentType(ContentType.MULTIPART)
                .when()
                .post(Endpoints.FORMULAS_IMPORT);
    }

    @Step("Import formula with file")
    public Response importFormula(File importRequestFile) {
        return importFormula(commonRequestSpecification(getAdminEmail()), importRequestFile);
    }

    @Step("Import formula")
    public Response importFormula(FormulaResponse... formulas) {
        return importFormula(commonRequestSpecification(getAdminEmail()), CommonFileUtils.generateJsonFile(formulas));
    }

    @Step("Import formula with invalid auth header")
    public Response importFormulaWithInvalidAuthHeader(InvalidAuthRequestSpecification invalidAuthRequestSpecification, File importRequestFile) {
        return importFormula(invalidAuthRequestSpecification.getRequestSpecification(getServiceUrl(), getBasePath()), importRequestFile);
    }

    @Step("Check that response contains formulas with ids={ids}")
    public void checkResponseContainsFormulasWithIds(Response response, String... ids) {
        FormulaResponse[] formulas = response.as(FormulaResponse[].class);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(formulas.length, ids.length, "number of formulas in response");
        softAssert.assertTrue(Arrays.stream(formulas).allMatch(
                formulaResponse -> Arrays.asList(ids).contains(formulaResponse.getId())),
                "Response should contain formulas with ids: " + String.join(", ", ids));
    }

    @Step("Check that formula response corresponds to formula from import request")
    public void checkFormulaResponseCorrespondsToRequest(FormulaResponse formulaResponse, FormulaResponse formulaRequest) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(formulaResponse.getId(), formulaRequest.getId(), "id");
        softAssert.assertEquals(formulaResponse.getDescription(), formulaRequest.getDescription(), "description");
        softAssert.assertEquals(formulaResponse.getExpressions(), formulaRequest.getExpressions(), "expressions");
        softAssert.assertEquals(formulaResponse.getArchived(), formulaRequest.getArchived(), "archived");
        softAssert.assertEquals(formulaResponse.getUpdatedBy(), formulaRequest.getUpdatedBy(), "updated_by");
        softAssert.assertAll();
    }

    @Step("Check that formula to import is overwritten in json file")
    public void checkFormulaResponseOverwritten(FormulaConfig formulaConfig, FormulaResponse formulaRequest) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(formulaConfig.getId(), formulaRequest.getId(), "id");
        softAssert.assertNotEquals(formulaConfig.getDescription(), formulaRequest.getDescription(), "description");
        softAssert.assertNotEquals(formulaConfig.getExpressions(), formulaRequest.getExpressions(), "expressions");
        softAssert.assertAll();
    }

    @Step("Read content of JSON file")
    @Attachment(value = "Import JSON File Content", type = "application/json")
    public String readJsonFileContent(File file) {
        return FileUtils.readFile(file);
    }

    @Step("Check that 'updated_by' field in formula response is equal to x-user-id header")
    public void checkUpdatedByFieldInFormulaResponse(FormulaResponse formulaResponse) {
        Assert.assertEquals(formulaResponse.getUpdatedBy(), getAdminEmail());
    }

    @Step("Check that 'updated_at' field in formula response is based on import action")
    public void checkUpdatedAtFieldInFormulaResponse(FormulaResponse formulaResponse, Date expectedDate) {
        Assert.assertTrue(formulaResponse.getUpdatedAt().getTime() - expectedDate.getTime() < 2000, "updated_at should be based on import action");
    }

    @Step("Check that conflict entities are listed in the response")
    public void checkConflictEntitiesInResponse(Response response, String id) {
        ArchivedFormulaImportResponse archivedFormulaImportResponse = response.as(ArchivedFormulaImportResponse.class);
        Assert.assertEquals(archivedFormulaImportResponse.getDetails().getValue(), id, "value");
    }

    @Step("Delete 'updated_at', 'created_at and 'updated_by' fields from formula response in JSON file")
    public FormulaResponse deleteFieldsFromFormulaResponse(FormulaResponse formulaResponse) {
        return formulaResponse.toBuilder()
                .updatedAt(null)
                .updatedBy(null)
                .build();
    }
}
