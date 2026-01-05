package com.alabs.automation.phoenix.steps.formulaservice.formula;

import com.alabs.automation.framework.utilities.MathUtils;
import com.alabs.automation.phoenix.constants.formulaservice.Endpoints;
import com.alabs.automation.phoenix.models.formula.formula.ExistingEvaluatorFormula;
import com.alabs.automation.phoenix.models.formula.formula.ExistingFormulaEvaluatorRequest;
import com.alabs.automation.phoenix.models.formula.formula.ExistingFormulaEvaluatorResponse;
import com.alabs.automation.phoenix.models.formula.formula.FormulaResponse;
import com.alabs.automation.phoenix.steps.formulaservice.BaseFormulaSteps;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public final class EvaluateExistingFormulaSteps extends BaseFormulaSteps {
    private static final String EVALUATION_RESULT_ASSERT_MESSAGE = "Result should match expression for formulaId: %s";

    private Response evaluateFormula(RequestSpecification requestSpecification,
                                     ExistingFormulaEvaluatorRequest request) {
        return requestSpecification
                .and()
                .body(request)
                .when()
                .post(Endpoints.PROCESS);
    }

    @Step("Evaluate formula with expression as parameter")
    public Response evaluateFormula(ExistingFormulaEvaluatorRequest request) {
        return evaluateFormula(internalRequestSpecification(), request);
    }

    @Step("Check if result in formula evaluation response is equal to additional attributes")
    public void checkResultInResponseIsEqualToAdditionalAttribute(Response response, int expected) {
        ExistingFormulaEvaluatorResponse[] evaluatorResponses = response.as(ExistingFormulaEvaluatorResponse[].class);
        Arrays.stream(evaluatorResponses)
                .forEach(evaluatorResponse -> {
                    int actual = evaluatorResponse.getResult().intValue();
                    Assert.assertEquals(actual, expected, "Result does not match expected attribute");
                });
    }

    @Step("Check that result value in formula evaluation response is as expected")
    public void checkResultInResponse(ExistingFormulaEvaluatorRequest evaluatorRequest,
                                      Response response,
                                      String[] validRuleIds,
                                      FormulaResponse... formulaResponses) {
        ExistingFormulaEvaluatorResponse[] evaluatorResponses = response.as(ExistingFormulaEvaluatorResponse[].class);
        Map<String, String> expressionsPerFormulas = getExpressionsWithHighestPriorityPerFormula(validRuleIds, formulaResponses);
        Arrays.stream(evaluatorResponses)
                .forEach(evaluatorResponse -> {
                    String formulaId = getFormulaIdFromEvaluatorFormulas(evaluatorResponse, evaluatorRequest);
                    String expression = expressionsPerFormulas.get(formulaId);
                    int expected = MathUtils.calculate(expression);
                    int actual = evaluatorResponse.getResult().intValue();
                    Assert.assertEquals(expected, actual,
                            "Result should match expression for formulaId: " + formulaId);
                });
    }

    @Step("Check that result value in formula evaluation response is as expected by function")
    public void checkResultInResponse(Response response, String formulaId, Number expected) {
        ExistingFormulaEvaluatorResponse[] evaluatorResponse = response.as(ExistingFormulaEvaluatorResponse[].class);
        if (expected instanceof Integer) {
            int actual = evaluatorResponse[0].getResult().intValue();
            Assert.assertEquals(actual, expected.intValue(),
                    String.format(EVALUATION_RESULT_ASSERT_MESSAGE, formulaId));
        } else if (expected instanceof Long) {
            long actual = evaluatorResponse[0].getResult().longValue();
            Assert.assertEquals(actual, expected.longValue(),
                    String.format(EVALUATION_RESULT_ASSERT_MESSAGE, formulaId));
        } else if (expected instanceof Double) {
            double actual = evaluatorResponse[0].getResult().doubleValue();
            Assert.assertEquals(actual, expected.doubleValue(),
                    String.format(EVALUATION_RESULT_ASSERT_MESSAGE, formulaId));
        } else {
            throw new IllegalArgumentException(String.format("Unsupported expected value type: %s", expected.getClass()));
        }
    }

    @Step("Check that result value in formula evaluation response for sum expression is as expected")
    public void checkResultInResponseForSumExpression(ExistingFormulaEvaluatorResponse evaluatorResponse, String expression) {
        String[] parts = expression.split("\\+");
        int result = 0;
        for (String part : parts) {
            result += Integer.parseInt(part.trim());
        }
        Assert.assertEquals(evaluatorResponse.getResult(), result, "Result of evaluation");
    }

    @Step("Check that reason in formula evaluation response contains {formulaId} and {entityId}")
    public void checkResponseReason(Response response, String formulaId, String entityId) {
        ExistingFormulaEvaluatorResponse evaluatorResponse = response.as(ExistingFormulaEvaluatorResponse[].class)[0];
        Assert.assertTrue(evaluatorResponse.getReason().contains(formulaId), String.format("Reason should contain formulaId: %s", formulaId));
        Assert.assertTrue(evaluatorResponse.getReason().contains(entityId), String.format("Reason should contain entity: %s", entityId));
    }

    private Map<String, String> getExpressionsWithHighestPriorityPerFormula(String[] validRuleIds,
                                                                            FormulaResponse... formulaResponses) {
        return Arrays.stream(formulaResponses)
                .collect(Collectors.toMap(
                        FormulaResponse::getId,
                        formulaResponse -> Arrays.stream(formulaResponse.getExpressions())
                                .sorted(Comparator.comparing(expr -> expr.getPriority().getPriority()))
                                .filter(expr -> expr.getRuleId() != null && Arrays.asList(validRuleIds).contains(expr.getRuleId()))
                                .findFirst()
                                .or(() -> Arrays.stream(formulaResponse.getExpressions())
                                        .filter(expr -> Boolean.TRUE.equals(expr.getIsDefault()))
                                        .findFirst())
                                .orElseThrow(() -> new IllegalArgumentException(
                                        String.format("No valid or default expressions found for %s", formulaResponse)))
                                .getExpression()
                ));
    }

    private String getFormulaIdFromEvaluatorFormulas(ExistingFormulaEvaluatorResponse evaluatorResponse,
                                                     ExistingFormulaEvaluatorRequest evaluatorRequest) {
        String evaluationId = evaluatorResponse.getId();
        ExistingEvaluatorFormula matchingFormula = Arrays.stream(evaluatorRequest.getFormulas())
                .filter(formula -> formula.getEvaluationId().equals(evaluationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No matching formula found for evaluationId: "
                        + evaluationId));
        return matchingFormula.getFormulaId();
    }
}
