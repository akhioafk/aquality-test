package com.alabs.automation.phoenix.steps.formulaservice.formula;

import com.alabs.automation.phoenix.constants.formulaservice.Endpoints;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.models.formula.formula.FormulaExportRequest;
import com.alabs.automation.phoenix.models.formula.formula.FormulaExportResponse;
import com.alabs.automation.phoenix.steps.formulaservice.BaseFormulaSteps;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.util.Arrays;

public class ExportFormulaSteps extends BaseFormulaSteps {

    private Response exportFormula(RequestSpecification requestSpecification, FormulaExportRequest request) {
        return requestSpecification
                .body(request)
                .when()
                .post(Endpoints.FORMULAS_EXPORT);
    }

    private Response exportFormulaWithoutRequestBody(RequestSpecification requestSpecification) {
        return requestSpecification
                .when()
                .post(Endpoints.FORMULAS_EXPORT);
    }

    @Step("Export formula")
    public Response exportFormula(FormulaExportRequest request) {
        return exportFormula(commonRequestSpecification(getAdminEmail()), request);
    }

    @Step("Export formula without request body")
    public Response exportFormulaWithoutRequestBody() {
        return exportFormulaWithoutRequestBody(commonRequestSpecification(getAdminEmail()));
    }

    @Step("Export formula with invalid auth header")
    public Response exportFormulaWithInvalidAuthHeader(InvalidAuthRequestSpecification invalidAuthRequestSpecification, FormulaExportRequest request) {
        return exportFormula(invalidAuthRequestSpecification.getRequestSpecification(getServiceUrl(), getBasePath()), request);
    }

    @Step("Check that response contains formula with ids={ids}")
    public void checkResponseContainsFormulas(Response response, String... ids) {
        FormulaExportResponse[] formulaExportResponse = response.as(FormulaExportResponse[].class);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(formulaExportResponse.length, ids.length);
        softAssert.assertTrue(Arrays.stream(formulaExportResponse).map(FormulaExportResponse::getId).allMatch(
                formulaId -> Arrays.asList(ids).contains(formulaId)),
                "Response should contain formulas with ids: " + String.join(", ", ids));
        softAssert.assertAll();
    }

    @Step("Check that response contains list of all existing formulas")
    public void checkResponseContainsAllExistingFormulas(FormulaExportResponse[] formulaExportResponse, int expectedFormulasCount) {
        Assert.assertEquals(formulaExportResponse.length, expectedFormulasCount, "Count of all existing active formulas");
    }
}
