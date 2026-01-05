package com.alabs.automation.phoenix.steps.formulaservice.formula;

import com.alabs.automation.phoenix.constants.common.Parameters;
import com.alabs.automation.phoenix.constants.formulaservice.Endpoints;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.steps.formulaservice.BaseFormulaSteps;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public final class DeleteFormulaSteps extends BaseFormulaSteps {

    private Response deleteFormula(RequestSpecification requestSpecification, String... ids) {
        String formulaIds = String.join(Parameters.COMMA_DELIMITER, ids);
        return requestSpecification
                .queryParam(Parameters.IDS, formulaIds)
                .when()
                .delete(Endpoints.FORMULAS);
    }

    private Response deleteFormulaWithoutIdsParameter(RequestSpecification requestSpecification) {
        return requestSpecification
                .delete(Endpoints.FORMULAS);
    }

    @Step("Delete formula with ids: {ids}")
    public Response deleteFormula(String... ids) {
        return deleteFormula(commonRequestSpecification(getAdminEmail()), ids);
    }

    @Step("Delete formula without ids parameter")
    public Response deleteFormulaWithoutIdsParameter() {
        return deleteFormulaWithoutIdsParameter(commonRequestSpecification(getAdminEmail()));
    }

    @Step("Delete formula with invalid auth header")
    public Response deleteFormulaWithInvalidAuthHeader(InvalidAuthRequestSpecification invalidAuthRequestSpecification, String... ids) {
        return deleteFormula(invalidAuthRequestSpecification.getRequestSpecification(getServiceUrl(), getBasePath()), ids);
    }
}
