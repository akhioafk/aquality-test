package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.phoenix.data.formula.FormulaGenerator;
import com.alabs.automation.phoenix.data.formula.UserAttributeEvaluationFunctionRequestGenerator;
import com.alabs.automation.framework.steps.api.ResponseSteps;
import com.alabs.automation.framework.utilities.Randomizer;
import com.alabs.automation.framework.utilities.StringUtils;
import com.alabs.automation.phoenix.models.formula.formula.*;
import com.alabs.automation.phoenix.steps.formulaservice.formula.CreateFormulaSteps;
import com.alabs.automation.phoenix.tests.formulaservice.BaseFormulasTest;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaGenerationConstants.MAX_VALUE;
import static com.alabs.automation.phoenix.constants.formulaservice.FormulaValueOption.FIRST_PRIOR_DEFAULT_TRUE;
import static org.apache.http.HttpStatus.*;

@TmsLink("9030396")
public class UserAttributeEvaluationFunctionTest extends BaseFormulasTest {
    private static final String USER_ATTRIBUTE_NAME = StringUtils.randomAlphabetic().toLowerCase();
    private static final int USER_ATTRIBUTE_VALUE = Randomizer.randomPositiveInt(MAX_VALUE);
    private final CreateFormulaSteps createFormulaSteps = new CreateFormulaSteps();
    private String formulaId;

    @BeforeMethod(groups = {"suite.smoke"})
    public void beforeMethod() {
        prepareUserInfo();
        FormulaResponse formulaResponse = createFormulaAndCheckStatus(FormulaGenerator.prepareFormulaWithoutDescription(
                FIRST_PRIOR_DEFAULT_TRUE.getUserAttributeExpression(USER_ATTRIBUTE_NAME)));
        formulaId = formulaResponse.getId();
    }

    @Test(description = "Formula - User Attribute evaluation function", priority = Integer.MAX_VALUE, groups = {"suite.smoke"})
    public void evaluateUserAttributeFunctionTest() {
        UserAttributeEvaluationFunctionRequest evaluationRequest =
                UserAttributeEvaluationFunctionRequestGenerator.prepareUserAttributeEvaluationFunctionRequest(gameAccountId, USER_ATTRIBUTE_NAME, USER_ATTRIBUTE_VALUE, formulaId);
        evaluateUserAttributeFunctionAndCheck(evaluationRequest, USER_ATTRIBUTE_VALUE);
    }

    private FormulaResponse createFormulaAndCheckStatus(FormulaRequest request) {
        Response response = createFormulaSteps.createFormula(request);
        ResponseSteps.checkStatusCode(response, SC_CREATED);
        return response.as(FormulaResponse.class);
    }
}
