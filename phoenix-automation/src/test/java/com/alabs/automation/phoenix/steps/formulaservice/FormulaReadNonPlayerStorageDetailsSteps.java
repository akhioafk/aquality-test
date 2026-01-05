package com.alabs.automation.phoenix.steps.formulaservice;

import com.alabs.automation.phoenix.constants.formulaservice.Endpoints;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public final class FormulaReadNonPlayerStorageDetailsSteps extends BaseFormulaSteps {

    private Response readNonPlayerStorageDetails(RequestSpecification requestSpecification) {
        return requestSpecification
                .get(Endpoints.STORAGE_DEPENDENCIES);
    }

    @Step("Read non player storage details")
    public Response readNonPlayerStorageDetails() {
        return readNonPlayerStorageDetails(internalRequestSpecification());
    }
}
