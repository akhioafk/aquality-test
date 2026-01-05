package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.phoenix.data.formula.AttributeGenerator;
import com.alabs.automation.phoenix.data.formula.ExistingFormulaEvaluatorRequestGenerator;
import com.alabs.automation.phoenix.data.formula.FormulaRangeGenerator;
import com.alabs.automation.framework.steps.api.ResponseSteps;
import com.alabs.automation.phoenix.constants.formulaservice.FormulaGenerationConstants;
import com.alabs.automation.phoenix.constants.formulaservice.ValueCombinationDescription;
import com.alabs.automation.phoenix.models.formula.attribute.AttributeRequest;
import com.alabs.automation.phoenix.models.formula.attribute.FormulaUserAttribute;
import com.alabs.automation.phoenix.models.formula.formula.ExistingFormulaEvaluatorRequest;
import com.alabs.automation.phoenix.models.formula.formula.FormulaExpression;
import com.alabs.automation.phoenix.models.formula.formula.FormulaResponse;
import com.alabs.automation.phoenix.models.formula.range.FormulaRangeRequest;
import com.alabs.automation.phoenix.models.formula.range.FormulaRangeResponse;
import com.alabs.automation.phoenix.steps.formulaservice.formula.FormulaPreconditionSteps;
import com.alabs.automation.phoenix.steps.formulaservice.range.RangePreconditionSteps;
import com.alabs.automation.phoenix.steps.formulaservice.range.RangeResponseSteps;
import com.alabs.automation.phoenix.tests.formulaservice.BaseEvaluationFunctionTest;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaValueOption.*;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;

@TmsLink("9030395")
public class RangeEvaluationFunctionTest extends BaseEvaluationFunctionTest {
    private static final String RANGE_FUNCTION = "range(\"%s\")";
    private String attributeName;
    private int attributeValue;

    @BeforeTest(groups = {"suite.smoke"})
    public void beforeTest() {
        prepareInvalidDeviceModelRules(userDeviceModel, INVALID_RULES_AMOUNT);
        attributeName = FormulaRangeGenerator.getRandomAttributeId();
        AttributeRequest attributeRequest = AttributeGenerator
                .prepareAttributeRequest(gameAccountId, attributeName, FormulaGenerationConstants.SHIFT_VALUE_SIXTY_SECONDS);
        attributeValue = sumOfAttributeValues(attributeRequest.getAttributes(),
                FormulaUserAttribute::getValue);
        Response responseAttribute = createAttributeSteps.createAttribute(attributeRequest);
        ResponseSteps.checkStatusCode(responseAttribute, SC_NO_CONTENT);
    }

    @Test(description = "Formula - Internal: Range evaluation function",
            priority = Integer.MAX_VALUE, groups = {"suite.smoke"})
    public void rangeEvaluationTest() {
        FormulaRangeRequest initialFormulaRangeRequest = FormulaRangeGenerator.prepareRange(
                FIRST_PRIOR_DEFAULT_TRUE.getLevel(validRuleIds[0]),
                SECOND_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[0]),
                THIRD_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[1]));
        evaluationFunction(initialFormulaRangeRequest);
    }

    @Test(description = "Send POST /internal/v1/formulas/process to check range with values :",
            dataProvider = "formulaRangeRequestProviderForAdditionalChecks")
    public void rangeEvaluationAdditionalChecks(String name, FormulaRangeRequest formulaRangeRequest) {
        evaluationFunction(formulaRangeRequest);
    }

    private void evaluationFunction(FormulaRangeRequest formulaRangeRequest) {
        FormulaRangeResponse formulaRangeResponse = RangePreconditionSteps
                .createRangeWithAttribute(attributeName, formulaRangeRequest);
        FormulaResponse formulaResponse = createFormula(String.format(RANGE_FUNCTION, formulaRangeResponse.getId()));

        ExistingFormulaEvaluatorRequest formulaEvaluatorRequest = ExistingFormulaEvaluatorRequestGenerator.prepareFormulaEvaluatorRequest(
                gameAccountId, UUID.randomUUID().toString(), formulaResponse.getId());
        Response response = evaluateExistingFormulaSteps.evaluateFormula(formulaEvaluatorRequest);
        long expectedEvaluationResult = RangeResponseSteps
                .getRangeValueOutputByRuleIdAndAttributeValue(formulaRangeResponse, attributeValue, validRuleIds[0]);
        evaluateExistingFormulaSteps.checkResultInResponse(response, formulaResponse.getId(), expectedEvaluationResult);
    }

    private FormulaResponse createFormula(String expression) {
        FormulaExpression formulaExpression = FIRST_PRIOR_DEFAULT_TRUE.getCustomizedExpression(expression);
        return FormulaPreconditionSteps.createFormula(formulaExpression);
    }

    @DataProvider
    private Object[][] formulaRangeRequestProviderForAdditionalChecks() {
        FormulaRangeRequest rangeRequest1 = FormulaRangeGenerator.prepareRange(FIRST_PRIOR_DEFAULT_FALSE.getLevel(validRuleIds[0]),
                SECOND_PRIOR_DEFAULT_TRUE.getLevel(invalidRuleIds[0]),
                THIRD_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[1]));
        FormulaRangeRequest rangeRequest2 = FormulaRangeGenerator.prepareRange(FIRST_PRIOR_DEFAULT_FALSE.getLevel(validRuleIds[0]),
                SECOND_PRIOR_DEFAULT_TRUE.getLevel(),
                THIRD_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[0]));
        FormulaRangeRequest rangeRequest3 = FormulaRangeGenerator.prepareRange(FIRST_PRIOR_DEFAULT_TRUE.getLevel(),
                SECOND_PRIOR_DEFAULT_FALSE.getLevel(validRuleIds[0]),
                THIRD_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[0]));
        FormulaRangeRequest rangeRequest4 = FormulaRangeGenerator.prepareRange(FIRST_PRIOR_DEFAULT_TRUE.getLevel(invalidRuleIds[0]),
                SECOND_PRIOR_DEFAULT_FALSE.getLevel(validRuleIds[0]),
                THIRD_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[1]));
        FormulaRangeRequest rangeRequest5 = FormulaRangeGenerator.prepareRange(FIRST_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[0]),
                SECOND_PRIOR_DEFAULT_TRUE.getLevel(validRuleIds[0]),
                THIRD_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[1]));
        FormulaRangeRequest rangeRequest6 = FormulaRangeGenerator.prepareRange(FIRST_PRIOR_DEFAULT_TRUE.getLevel(),
                SECOND_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[0]),
                THIRD_PRIOR_DEFAULT_FALSE.getLevel(validRuleIds[0]));
        FormulaRangeRequest rangeRequest7 = FormulaRangeGenerator.prepareRange(FIRST_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[0]),
                SECOND_PRIOR_DEFAULT_FALSE.getLevel(validRuleIds[0]),
                THIRD_PRIOR_DEFAULT_TRUE.getLevel());
        FormulaRangeRequest rangeRequest8 = FormulaRangeGenerator.prepareRange(FIRST_PRIOR_DEFAULT_TRUE.getLevel(validRuleIds[0]),
                SECOND_PRIOR_DEFAULT_FALSE.getLevel(validRuleIds[1]),
                THIRD_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[0]));
        FormulaRangeRequest rangeRequest9 = FormulaRangeGenerator.prepareRange(FIRST_PRIOR_DEFAULT_FALSE.getLevel(validRuleIds[0]),
                SECOND_PRIOR_DEFAULT_TRUE.getLevel(validRuleIds[1]),
                THIRD_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[0]));
        FormulaRangeRequest rangeRequest10 = FormulaRangeGenerator.prepareRange(FIRST_PRIOR_DEFAULT_TRUE.getLevel(),
                SECOND_PRIOR_DEFAULT_FALSE.getLevel(validRuleIds[0]),
                THIRD_PRIOR_DEFAULT_FALSE.getLevel(validRuleIds[1]));
        FormulaRangeRequest rangeRequest11 = FormulaRangeGenerator.prepareRange(FIRST_PRIOR_DEFAULT_FALSE.getLevel(validRuleIds[0]),
                SECOND_PRIOR_DEFAULT_FALSE.getLevel(validRuleIds[1]),
                THIRD_PRIOR_DEFAULT_TRUE.getLevel(invalidRuleIds[0]));
        FormulaRangeRequest rangeRequest12 = FormulaRangeGenerator.prepareRange(FIRST_PRIOR_DEFAULT_TRUE.getLevel(invalidRuleIds[0]),
                SECOND_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[1]),
                THIRD_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[2]));
        FormulaRangeRequest rangeRequest13 = FormulaRangeGenerator.prepareRange(FIRST_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[0]),
                SECOND_PRIOR_DEFAULT_TRUE.getLevel(invalidRuleIds[1]),
                THIRD_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[2]));
        FormulaRangeRequest rangeRequest14 = FormulaRangeGenerator.prepareRange(FIRST_PRIOR_DEFAULT_TRUE.getLevel(),
                SECOND_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[0]),
                THIRD_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[1]));
        FormulaRangeRequest rangeRequest15 = FormulaRangeGenerator.prepareRange(FIRST_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[0]),
                SECOND_PRIOR_DEFAULT_FALSE.getLevel(invalidRuleIds[1]),
                THIRD_PRIOR_DEFAULT_TRUE.getLevel(invalidRuleIds[2]));

        return new Object[][]{
                {ValueCombinationDescription.DESCRIPTION_1, rangeRequest1},
                {ValueCombinationDescription.DESCRIPTION_2, rangeRequest2},
                {ValueCombinationDescription.DESCRIPTION_3, rangeRequest3},
                {ValueCombinationDescription.DESCRIPTION_4, rangeRequest4},
                {ValueCombinationDescription.DESCRIPTION_5, rangeRequest5},
                {ValueCombinationDescription.DESCRIPTION_6, rangeRequest6},
                {ValueCombinationDescription.DESCRIPTION_7, rangeRequest7},
                {ValueCombinationDescription.DESCRIPTION_8, rangeRequest8},
                {ValueCombinationDescription.DESCRIPTION_9, rangeRequest9},
                {ValueCombinationDescription.DESCRIPTION_10, rangeRequest10},
                {ValueCombinationDescription.DESCRIPTION_11, rangeRequest11},
                {ValueCombinationDescription.DESCRIPTION_12, rangeRequest12},
                {ValueCombinationDescription.DESCRIPTION_13, rangeRequest13},
                {ValueCombinationDescription.DESCRIPTION_14, rangeRequest14},
                {ValueCombinationDescription.DESCRIPTION_15, rangeRequest15}
        };
    }
}
