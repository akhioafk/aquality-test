package com.alabs.automation.phoenix.steps.formulaservice.formula;

import com.alabs.automation.phoenix.constants.formulaservice.Endpoints;
import com.alabs.automation.phoenix.models.formula.formula.AdditionalAttributeFormulaEvaluatorResponse;
import com.alabs.automation.phoenix.models.formula.formula.ExistingFormulaEvaluatorResponse;
import com.alabs.automation.phoenix.models.formula.formula.UserAttributeEvaluationFunctionRequest;
import com.alabs.automation.phoenix.steps.formulaservice.BaseFormulaSteps;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.util.Arrays;

public class UserAttributeEvaluationFunctionSteps extends BaseFormulaSteps {

    private Response evaluateUserAttribute(RequestSpecification requestSpecification, UserAttributeEvaluationFunctionRequest request) {
        return requestSpecification
                .and()
                .body(request)
                .when()
                .post(Endpoints.PROCESS);
    }

    @Step("Evaluate user attribute function")
    public Response evaluateUserAttribute(UserAttributeEvaluationFunctionRequest request) {
        return evaluateUserAttribute(internalRequestSpecification(), request);
    }

    @Step("Check that result value by evaluator in response is as expected")
    public void checkResultInResponse(Response response, Number userAttributeValue) {
        ExistingFormulaEvaluatorResponse[] existingFormulaEvaluatorResponse = response.as(ExistingFormulaEvaluatorResponse[].class);
        SoftAssert softAssert = new SoftAssert();
        Arrays.stream(existingFormulaEvaluatorResponse)
                .forEach(evaluatorResponse -> {
                    Number actual = evaluatorResponse.getResult();
                    softAssert.assertEquals(actual, userAttributeValue, "The 'result' value in the evaluation response");
                });
        softAssert.assertAll();
    }

    @Step("Check that error is returned in reason")
    public void checkErrorMessageInResponse(AdditionalAttributeFormulaEvaluatorResponse[] response, String attribute) {
        boolean containsErrorMessage = Arrays.stream(response)
                .filter(evaluation -> evaluation.getReason() != null)
                .anyMatch(evaluation -> evaluation.getReason().contains(String.format("Additional attribute '%s' not found", attribute)));
        Assert.assertTrue(containsErrorMessage, "Expression should return a 'not found' error.");
    }
}
