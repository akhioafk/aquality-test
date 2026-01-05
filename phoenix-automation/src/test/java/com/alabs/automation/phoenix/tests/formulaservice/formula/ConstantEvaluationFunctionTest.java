package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.phoenix.data.formula.ConstantGenerator;
import com.alabs.automation.phoenix.data.formula.ExistingFormulaEvaluatorRequestGenerator;
import com.alabs.automation.phoenix.data.formula.FormulaGenerator;
import com.alabs.automation.framework.steps.api.ResponseSteps;
import com.alabs.automation.framework.utilities.Randomizer;
import com.alabs.automation.phoenix.constants.formulaservice.ValueCombinationDescription;
import com.alabs.automation.phoenix.models.common.WrappedNumeric;
import com.alabs.automation.phoenix.models.formula.constant.ConstantRequest;
import com.alabs.automation.phoenix.models.formula.constant.ConstantResponse;
import com.alabs.automation.phoenix.models.formula.formula.ExistingFormulaEvaluatorRequest;
import com.alabs.automation.phoenix.models.formula.formula.FormulaRequest;
import com.alabs.automation.phoenix.models.formula.formula.FormulaResponse;
import com.alabs.automation.phoenix.steps.formulaservice.constant.CreateConstantSteps;
import com.alabs.automation.phoenix.tests.formulaservice.BaseEvaluationFunctionTest;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaGenerationConstants.MAX_VALUE;
import static com.alabs.automation.phoenix.constants.formulaservice.FormulaValueOption.*;
import static org.apache.http.HttpStatus.SC_CREATED;

@TmsLink("9030395")
public class ConstantEvaluationFunctionTest extends BaseEvaluationFunctionTest {
    private static final String CONSTANT_FUNCTION = "constant(\"%s\")+1";
    private static final WrappedNumeric FORMULA_VALUE = new WrappedNumeric(Randomizer.randomPositiveInt(MAX_VALUE));
    private final CreateConstantSteps createConstantSteps = new CreateConstantSteps();

    @BeforeTest(groups = {"suite.smoke"})
    public void beforeTest() {
        prepareInvalidDeviceModelRules(userDeviceModel, INVALID_RULES_AMOUNT);
    }

    @Test(description = "Formula - Internal: Constant evaluation function",
            priority = Integer.MAX_VALUE, groups = {"suite.smoke"})
    public void constantEvaluationTest() {
        ConstantRequest initialConstantRequest = ConstantGenerator.prepareConstant(FIRST_PRIOR_DEFAULT_TRUE.getValue(FORMULA_VALUE),
                SECOND_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[0]),
                THIRD_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[1]));
        evaluateFunction(createFormula(createConstantAndGetCustomizedExpression(initialConstantRequest)));
    }

    @Test(description = "Send POST /internal/v1/formulas/process to check constant with values :",
            dataProvider = "constantRequestProviderForAdditionalChecks")
    public void constantEvaluationAdditionalChecks(String name, ConstantRequest constantRequest) {
        evaluateFunction(createFormula(createConstantAndGetCustomizedExpression(constantRequest)));
    }

    private String createConstantAndGetCustomizedExpression(ConstantRequest request) {
        Response response = createConstantSteps.createConstant(request);
        ResponseSteps.checkStatusCode(response, SC_CREATED);
        return String.format(CONSTANT_FUNCTION, response.as(ConstantResponse.class).getId());
    }

    private FormulaResponse createFormula(String expression) {
        FormulaRequest request = FormulaGenerator.prepareFormula(FIRST_PRIOR_DEFAULT_TRUE.getCustomizedExpression(expression),
                SECOND_PRIOR_DEFAULT_FALSE.getExpression(invalidRuleIds[0]));
        Response response = createFormulaSteps.createFormula(request);
        ResponseSteps.checkStatusCode(response, SC_CREATED);
        return response.as(FormulaResponse.class);
    }

    private void evaluateFunction(FormulaResponse formulaResponse) {
        ExistingFormulaEvaluatorRequest formulaEvaluatorRequest = ExistingFormulaEvaluatorRequestGenerator.prepareFormulaEvaluatorRequest(
                gameAccountId, UUID.randomUUID().toString(), formulaResponse.getId());
        evaluateExistingFormulaSteps.checkResultInResponse(
                evaluateExistingFormulaSteps.evaluateFormula(formulaEvaluatorRequest),
                formulaResponse.getId(), (int) FORMULA_VALUE.getValue() + 1);
    }

    @DataProvider
    private Object[][] constantRequestProviderForAdditionalChecks() {
        ConstantRequest constantRequest1 = ConstantGenerator.prepareConstant(
                FIRST_PRIOR_DEFAULT_FALSE.getValue(validRuleIds[0], FORMULA_VALUE),
                SECOND_PRIOR_DEFAULT_TRUE.getValue(invalidRuleIds[0]),
                THIRD_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[1]));
        ConstantRequest constantRequest2 = ConstantGenerator.prepareConstant(
                FIRST_PRIOR_DEFAULT_FALSE.getValue(validRuleIds[0], FORMULA_VALUE),
                SECOND_PRIOR_DEFAULT_TRUE.getValue(),
                THIRD_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[0]));
        ConstantRequest constantRequest3 = ConstantGenerator.prepareConstant(
                FIRST_PRIOR_DEFAULT_TRUE.getValue(),
                SECOND_PRIOR_DEFAULT_FALSE.getValue(validRuleIds[0], FORMULA_VALUE),
                THIRD_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[0]));
        ConstantRequest constantRequest4 = ConstantGenerator.prepareConstant(
                FIRST_PRIOR_DEFAULT_TRUE.getValue(invalidRuleIds[0]),
                SECOND_PRIOR_DEFAULT_FALSE.getValue(validRuleIds[0], FORMULA_VALUE),
                THIRD_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[1]));
        ConstantRequest constantRequest5 = ConstantGenerator.prepareConstant(
                FIRST_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[0]),
                SECOND_PRIOR_DEFAULT_TRUE.getValue(validRuleIds[0], FORMULA_VALUE),
                THIRD_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[1]));
        ConstantRequest constantRequest6 = ConstantGenerator.prepareConstant(
                FIRST_PRIOR_DEFAULT_TRUE.getValue(),
                SECOND_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[0]),
                THIRD_PRIOR_DEFAULT_FALSE.getValue(validRuleIds[0], FORMULA_VALUE));
        ConstantRequest constantRequest7 = ConstantGenerator.prepareConstant(
                FIRST_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[0]),
                SECOND_PRIOR_DEFAULT_FALSE.getValue(validRuleIds[0], FORMULA_VALUE),
                THIRD_PRIOR_DEFAULT_TRUE.getValue());
        ConstantRequest constantRequest8 = ConstantGenerator.prepareConstant(
                FIRST_PRIOR_DEFAULT_TRUE.getValue(validRuleIds[0], FORMULA_VALUE),
                SECOND_PRIOR_DEFAULT_FALSE.getValue(validRuleIds[1]),
                THIRD_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[0]));
        ConstantRequest constantRequest9 = ConstantGenerator.prepareConstant(
                FIRST_PRIOR_DEFAULT_FALSE.getValue(validRuleIds[0], FORMULA_VALUE),
                SECOND_PRIOR_DEFAULT_TRUE.getValue(validRuleIds[1]),
                THIRD_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[0]));
        ConstantRequest constantRequest10 = ConstantGenerator.prepareConstant(
                FIRST_PRIOR_DEFAULT_TRUE.getValue(),
                SECOND_PRIOR_DEFAULT_FALSE.getValue(validRuleIds[0], FORMULA_VALUE),
                THIRD_PRIOR_DEFAULT_FALSE.getValue(validRuleIds[1]));
        ConstantRequest constantRequest11 = ConstantGenerator.prepareConstant(
                FIRST_PRIOR_DEFAULT_FALSE.getValue(validRuleIds[0], FORMULA_VALUE),
                SECOND_PRIOR_DEFAULT_FALSE.getValue(validRuleIds[1]),
                THIRD_PRIOR_DEFAULT_TRUE.getValue());
        ConstantRequest constantRequest12 = ConstantGenerator.prepareConstant(
                FIRST_PRIOR_DEFAULT_TRUE.getValue(invalidRuleIds[0], FORMULA_VALUE),
                SECOND_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[1]),
                THIRD_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[2]));
        ConstantRequest constantRequest13 = ConstantGenerator.prepareConstant(
                FIRST_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[0]),
                SECOND_PRIOR_DEFAULT_TRUE.getValue(invalidRuleIds[1], FORMULA_VALUE),
                THIRD_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[2]));
        ConstantRequest constantRequest14 = ConstantGenerator.prepareConstant(
                FIRST_PRIOR_DEFAULT_TRUE.getValue(FORMULA_VALUE),
                SECOND_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[0]),
                THIRD_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[1]));
        ConstantRequest constantRequest15 = ConstantGenerator.prepareConstant(
                FIRST_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[0]),
                SECOND_PRIOR_DEFAULT_FALSE.getValue(invalidRuleIds[1]),
                THIRD_PRIOR_DEFAULT_TRUE.getValue(FORMULA_VALUE));

        return new Object[][]{
                {ValueCombinationDescription.DESCRIPTION_1, constantRequest1},
                {ValueCombinationDescription.DESCRIPTION_2, constantRequest2},
                {ValueCombinationDescription.DESCRIPTION_3, constantRequest3},
                {ValueCombinationDescription.DESCRIPTION_4, constantRequest4},
                {ValueCombinationDescription.DESCRIPTION_5, constantRequest5},
                {ValueCombinationDescription.DESCRIPTION_6, constantRequest6},
                {ValueCombinationDescription.DESCRIPTION_7, constantRequest7},
                {ValueCombinationDescription.DESCRIPTION_8, constantRequest8},
                {ValueCombinationDescription.DESCRIPTION_9, constantRequest9},
                {ValueCombinationDescription.DESCRIPTION_10, constantRequest10},
                {ValueCombinationDescription.DESCRIPTION_11, constantRequest11},
                {ValueCombinationDescription.DESCRIPTION_12, constantRequest12},
                {ValueCombinationDescription.DESCRIPTION_13, constantRequest13},
                {ValueCombinationDescription.DESCRIPTION_14, constantRequest14},
                {ValueCombinationDescription.DESCRIPTION_15, constantRequest15}
        };
    }
}
