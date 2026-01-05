package com.alabs.automation.phoenix.steps.formulaservice;

import com.alabs.automation.framework.filters.SignatureFilter;
import com.alabs.automation.phoenix.configuration.PhoenixAutotestConfiguration;
import com.alabs.automation.phoenix.constants.formulaservice.Endpoints;
import io.restassured.specification.RequestSpecification;
import lombok.AccessLevel;
import lombok.Getter;

import static com.alabs.automation.framework.steps.api.RequestSpecs.requestSpecWithoutSignature;
import static io.restassured.RestAssured.given;

@Getter(AccessLevel.PROTECTED)
public abstract class BaseFormulaSteps {
    private final String adminEmail;
    private final String serviceUrl;
    private final String basePath;

    public BaseFormulaSteps() {
        this.adminEmail = PhoenixAutotestConfiguration.getAdminEmail();
        this.serviceUrl = PhoenixAutotestConfiguration.formula().getAdminUrl();
        this.basePath = Endpoints.BASE_ADMIN_PATH;
    }

    public RequestSpecification commonRequestSpecification(String email) {
        return given()
                .spec(requestSpecWithoutSignature(serviceUrl, basePath))
                .filter(new SignatureFilter(email));
    }

    public RequestSpecification internalRequestSpecification() {
        return given()
                .spec(requestSpecWithoutSignature(serviceUrl, Endpoints.BASE_INTERNAL_PATH));
    }
}
