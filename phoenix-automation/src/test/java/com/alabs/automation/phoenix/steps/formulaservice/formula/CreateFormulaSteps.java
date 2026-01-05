package com.alabs.automation.phoenix.steps.formulaservice.formula;

import com.alabs.automation.phoenix.constants.formulaservice.Endpoints;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.models.formula.formula.FormulaRequest;
import com.alabs.automation.phoenix.steps.formulaservice.BaseFormulaSteps;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public final class CreateFormulaSteps extends BaseFormulaSteps {

    private Response createFormula(RequestSpecification requestSpecification, FormulaRequest request) {
        return requestSpecification
                .and()
                .body(request)
                .when()
                .post(Endpoints.FORMULAS);
    }

    @Step("Create formula")
    public Response createFormula(FormulaRequest request) {
        return createFormula(commonRequestSpecification(getAdminEmail()), request);
    }

    @Step("Create formula with invalid auth header")
    public Response createFormulaWithInvalidHeader(InvalidAuthRequestSpecification invalidAuthRequestSpecification, FormulaRequest request) {
        return createFormula(invalidAuthRequestSpecification.getRequestSpecification(getServiceUrl(), getBasePath()), request);
    }
}
