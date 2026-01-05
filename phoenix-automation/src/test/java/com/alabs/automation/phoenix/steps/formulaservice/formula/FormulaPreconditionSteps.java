package com.alabs.automation.phoenix.steps.formulaservice.formula;

import com.alabs.automation.framework.steps.api.ResponseSteps;
import com.alabs.automation.phoenix.constants.formulaservice.JsonSchemas;
import com.alabs.automation.phoenix.models.formula.formula.FormulaExpression;
import com.alabs.automation.phoenix.models.formula.formula.FormulaRequest;
import com.alabs.automation.phoenix.models.formula.formula.FormulaResponse;
import com.alabs.automation.phoenix.models.gamesession.login.LoginRequest;
import com.alabs.automation.phoenix.models.gamesession.login.LoginResponse;
import com.alabs.automation.phoenix.steps.rulesservice.RulesPreconditionSteps;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.ImmutablePair;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaValueOption.FIRST_PRIOR_DEFAULT_TRUE;
import static com.alabs.automation.phoenix.constants.formulaservice.FormulaValueOption.SECOND_PRIOR_DEFAULT_FALSE;
import static com.alabs.automation.phoenix.data.formula.FormulaGenerator.prepareFormula;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;

@UtilityClass
public class FormulaPreconditionSteps {
    private static final CreateFormulaSteps createFormulaSteps = new CreateFormulaSteps();
    private static final RulesPreconditionSteps rulePreconditionSteps = new RulesPreconditionSteps();
    private static final DeleteFormulaSteps deleteFormulaSteps = new DeleteFormulaSteps();

    public static FormulaResponse createFormula(FormulaExpression... formulaExpressions) {
        FormulaRequest formulaRequest = prepareFormula(formulaExpressions);
        return createFormula(formulaRequest);
    }

    @Step("Precondition: Create formula and check status code")
    public static FormulaResponse createFormula(FormulaRequest formulaRequest) {
        Response response = createFormulaSteps.createFormula(formulaRequest);
        ResponseSteps.checkStatusCode(response, SC_CREATED);
        ResponseSteps.checkJsonSchema(response, JsonSchemas.FORMULA_RESPONSE);
        return response.as(FormulaResponse.class);
    }

    public static FormulaResponse createFormula(ImmutablePair<LoginRequest, LoginResponse> loginPair) {
        return createFormula(FIRST_PRIOR_DEFAULT_TRUE.getExpression(),
                SECOND_PRIOR_DEFAULT_FALSE.getExpression(rulePreconditionSteps.prepareRuleWithUser(loginPair)));
    }

    @Step("Delete Formula")
    public static void deleteFormula(String... ids) {
        Response response = deleteFormulaSteps.deleteFormula(ids);
        ResponseSteps.checkStatusCode(response, SC_NO_CONTENT);
    }
}
