package com.alabs.automation.phoenix.steps.formulaservice.formula;

import com.alabs.automation.phoenix.constants.formulaservice.Endpoints;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.data.formula.FormulaType;
import com.alabs.automation.phoenix.models.formula.formula.NonExistingFormulaEvaluatorResponse;
import com.alabs.automation.phoenix.models.formula.formula.NonExistingFormulaEvaluatorExpressionRequest;
import com.alabs.automation.phoenix.steps.formulaservice.BaseFormulaSteps;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import java.util.Arrays;
import java.util.stream.IntStream;

import static com.alabs.automation.phoenix.data.formula.NonExistingFormulaEvaluatorExpressionGenerator.VALUE;
import static com.alabs.automation.phoenix.data.formula.NonExistingFormulaEvaluatorExpressionGenerator.expressionToFormulaMap;

public final class EvaluateNonExistingFormulaSteps extends BaseFormulaSteps {

    private Response evaluateFormula(RequestSpecification requestSpecification, NonExistingFormulaEvaluatorExpressionRequest request) {
        return requestSpecification
                .and()
                .body(request)
                .when()
                .post(Endpoints.EVALUATE);
    }

    @Step("Evaluate formula with expression as parameter")
    public Response evaluateFormula(NonExistingFormulaEvaluatorExpressionRequest request) {
        return evaluateFormula(commonRequestSpecification(getAdminEmail()), request);
    }

    @Step("Evaluate formula with invalid auth header")
    public Response evaluateFormulaWithInvalidHeader(InvalidAuthRequestSpecification invalidAuthRequestSpecification,
                                                     NonExistingFormulaEvaluatorExpressionRequest request) {
        return evaluateFormula(invalidAuthRequestSpecification.getRequestSpecification(getServiceUrl(), getBasePath()), request);
    }

    @Step("Check that error is returned as a reason for individual expression that can't be evaluated")
    public void checkErrorMessageInResponse(NonExistingFormulaEvaluatorResponse[] response, String expression) {
        boolean containsErrorMessage = Arrays.stream(response)
                .filter(evaluation -> evaluation.getReason() != null)
                .anyMatch(evaluation -> evaluation.getExpression().equals(expression) && evaluation.getReason().contains("not found"));
        Assert.assertTrue(containsErrorMessage, "Expression should return a 'not found' error.");
    }

    @Step("Check that result value in response is mathematically correct")
    public void checkResultInResponse(Response response) {
        NonExistingFormulaEvaluatorResponse[] evaluationResponse = response.as(NonExistingFormulaEvaluatorResponse[].class);
        IntStream.range(0, evaluationResponse.length)
                .forEach(i -> {
                    String expression = evaluationResponse[i].getExpression();
                    FormulaType formula = expressionToFormulaMap.get(expression);
                    Number actualValue = evaluationResponse[i].getResult();
                    Number expectedValue = formula.calculate(Double.parseDouble(VALUE));
                    if (actualValue instanceof Integer) {
                        Assert.assertEquals(actualValue.intValue(), expectedValue.intValue(),
                                String.format("Formula result for %s is incorrect", expression));
                    } else if (actualValue instanceof Double) {
                        Assert.assertEquals(actualValue.doubleValue(), expectedValue.doubleValue(),
                                String.format("Formula result for %s is incorrect", expression));
                    } else {
                        throw new IllegalArgumentException(
                                String.format("Unsupported result type: %s for expression: %s", actualValue.getClass(), expression));
                    }
                });
    }

    @Step("Check that combined expressions are evaluated and return valid numeric results")
    public void checkCombinedExpressionsResult(Response response) {
        NonExistingFormulaEvaluatorResponse[] evaluationResponse = response.as(NonExistingFormulaEvaluatorResponse[].class);
        IntStream.range(0, evaluationResponse.length).forEach(i -> {
            NonExistingFormulaEvaluatorResponse item = evaluationResponse[i];
            String expression = item.getExpression();
            Number result = item.getResult();
            if (result instanceof Double) {
                double value = (Double) result;
                Assert.assertFalse(Double.isNaN(value) || Double.isInfinite(value),
                        String.format("Invalid result for expression '%s': %s", expression, value));
            }
        });
    }
}