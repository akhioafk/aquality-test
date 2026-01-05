package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.phoenix.data.formula.FormulaGenerator;
import com.alabs.automation.framework.steps.api.ResponseSteps;
import com.alabs.automation.phoenix.data.common.InvalidAuthHeadersDataProvider;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.models.formula.formula.FormulaResponse;
import com.alabs.automation.phoenix.steps.formulaservice.formula.FormulaResponseSteps;
import com.alabs.automation.phoenix.steps.formulaservice.formula.ReadFormulaSteps;
import com.alabs.automation.phoenix.tests.formulaservice.BaseFormulasTest;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.apache.http.HttpStatus.*;

@TmsLink("9030387")
public class ReadFormulaByIdTest extends BaseFormulasTest {
    private final ReadFormulaSteps readFormulaSteps = new ReadFormulaSteps();
    private FormulaResponse formulaResponse;

    @BeforeTest(groups = {"suite.smoke"})
    public void beforeTest() {
        prepareUserInfo();
        prepareValidDeviceModelRules(userDeviceModel, VALID_RULES_AMOUNT);
        formulaResponse = prepareFormulaWithTwoExpressions();
    }

    @Test(description = "Formula - Admin: GET formula", priority = Integer.MAX_VALUE, groups = {"suite.smoke"})
    public void getFormula() {
        Response response = readFormulaSteps.getFormula(formulaResponse.getId());
        ResponseSteps.checkStatusCode(response, SC_OK);
        FormulaResponse formulaResponse = response.as(FormulaResponse.class);
        FormulaResponseSteps.checkFormulaResponsesAreEqual(formulaResponse, this.formulaResponse);
    }

    @Test(description = "Send GET /admin/v1/formulas/{formula_id} with non existing id")
    public void getFormulaWithNonExistingId() {
        Response response = readFormulaSteps.getFormula(FormulaGenerator.generateFormulaId());
        ResponseSteps.checkStatusCode(response, HttpStatus.SC_NOT_FOUND);
    }

    @Test(description = "Send GET /admin/v1/formulas/{formula_id} with invalid auth header", dataProviderClass = InvalidAuthHeadersDataProvider.class,
            dataProvider = "invalidAuthRequestSpecifications")
    public void getFormulaWithInvalidHeader(InvalidAuthRequestSpecification invalidAuthRequestSpecification) {
        Response response = readFormulaSteps.getFormulaWithInvalidHeader(invalidAuthRequestSpecification, formulaResponse.getId());
        ResponseSteps.checkStatusCode(response, SC_UNAUTHORIZED);
    }
}
