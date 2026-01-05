package com.alabs.automation.phoenix.steps.formulaservice.formula;

import com.alabs.automation.phoenix.constants.formulaservice.Endpoints;
import com.alabs.automation.phoenix.steps.formulaservice.BaseFormulaSteps;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public final class InvalidateAllInMemoryCachesForFormulaSteps extends BaseFormulaSteps {

    private Response invalidateCache(RequestSpecification requestSpecification) {
        return requestSpecification
                .post(Endpoints.INVALIDATE_CACHE);
    }

    @Step("Invalidate all in memory caches")
    public Response invalidateCache() {
        return invalidateCache(internalRequestSpecification());
    }
}
