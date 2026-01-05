package com.alabs.automation.phoenix.steps.formulaservice.formula;

import com.alabs.automation.phoenix.constants.common.Parameters;
import com.alabs.automation.phoenix.constants.formulaservice.Endpoints;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.steps.formulaservice.BaseFormulaSteps;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public final class ReadFormulaSteps extends BaseFormulaSteps {

    private Response getFormula(RequestSpecification requestSpecification, String formulaId) {
        return requestSpecification
                .pathParam(Parameters.ID, formulaId)
                .when()
                .get(Endpoints.FORMULA_ID);
    }

    @Step("Get formula by id")
    public Response getFormula(String formulaId) {
        return getFormula(commonRequestSpecification(getAdminEmail()), formulaId);
    }

    @Step("Get formula with invalid auth header")
    public Response getFormulaWithInvalidHeader(InvalidAuthRequestSpecification invalidAuthRequestSpecification, String formulaId) {
        return getFormula(invalidAuthRequestSpecification
                .getRequestSpecification(getServiceUrl(), getBasePath()), formulaId);
    }
}
