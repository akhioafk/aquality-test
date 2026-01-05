package com.alabs.automation.phoenix.steps.formulaservice.formula;

import com.alabs.automation.phoenix.constants.common.Parameters;
import com.alabs.automation.phoenix.constants.formulaservice.Endpoints;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.models.formula.formula.FormulaRequest;
import com.alabs.automation.phoenix.steps.formulaservice.BaseFormulaSteps;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public final class UpdateFormulaSteps extends BaseFormulaSteps {

    private Response updateFormula(RequestSpecification requestSpecification, String formulaId, FormulaRequest request) {
        return requestSpecification
                .and()
                .pathParam(Parameters.ID, formulaId)
                .body(request)
                .when()
                .put(Endpoints.FORMULA_ID);
    }

    @Step("Update formula")
    public Response updateFormula(String formulaId, FormulaRequest request) {
        return updateFormula(commonRequestSpecification(getAdminEmail()), formulaId, request);
    }

    @Step("Update formula with invalid auth header")
    public Response updateFormulaWithInvalidHeader(InvalidAuthRequestSpecification invalidAuthRequestSpecification, String formulaId, FormulaRequest request) {
        return updateFormula(invalidAuthRequestSpecification.getRequestSpecification(getServiceUrl(), getBasePath()), formulaId, request);
    }
}
