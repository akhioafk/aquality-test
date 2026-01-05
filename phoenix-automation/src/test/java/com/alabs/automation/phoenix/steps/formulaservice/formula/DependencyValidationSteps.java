package com.alabs.automation.phoenix.steps.formulaservice.formula;

import com.alabs.automation.phoenix.constants.rewardsservice.Endpoints;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.models.rewards.dependancyvalidation.DependencyResults;
import com.alabs.automation.phoenix.models.rewards.dependancyvalidation.DependencyValidationRequest;
import com.alabs.automation.phoenix.models.rewards.dependancyvalidation.DependencyValidationResponse;
import com.alabs.automation.phoenix.steps.formulaservice.BaseFormulaSteps;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.asserts.SoftAssert;

import java.util.Arrays;
import java.util.function.Function;

import static com.alabs.automation.phoenix.constants.common.Parameters.COMMA_DELIMITER;

public class DependencyValidationSteps extends BaseFormulaSteps {

    private Response dependencyValidation(RequestSpecification requestSpecification, DependencyValidationRequest request) {
        return requestSpecification
                .body(request)
                .when()
                .post(Endpoints.DEPENDENCY_VALIDATION);
    }

    @Step("POST Dependency validation for formula group")
    public Response dependencyValidation(DependencyValidationRequest request) {
        return dependencyValidation(internalRequestSpecification(), request);
    }

    @Step("POST Dependency validation for formula group with invalid auth token: {invalidAuthRequestSpecification}")
    public Response dependencyValidationWithInvalidAuthToken(InvalidAuthRequestSpecification invalidAuthRequestSpecification, DependencyValidationRequest request) {
        return dependencyValidation(invalidAuthRequestSpecification
                .getRequestSpecification(getServiceUrl(), getBasePath()), request);
    }

    @Step("Check Formula Group Id={expectedGroupIds} present in the response")
    public void checkGroupIdPresent(DependencyValidationResponse response, Function<DependencyResults, String[]> dependencySelector,
                                    String... expectedGroupIds) {
        SoftAssert softAssert = new SoftAssert();
        for (String groupId : expectedGroupIds) {
            boolean isGroupIdPresent = Arrays.stream(response.getResults())
                    .map(dependencySelector)
                    .flatMap(Arrays::stream)
                    .flatMap(dependency -> Arrays.stream(dependency.split(COMMA_DELIMITER)))
                    .anyMatch(groupId::equals);
            softAssert.assertTrue(isGroupIdPresent, "Formula group id should be present");
        }
        softAssert.assertAll();
    }
}
