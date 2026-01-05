package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.phoenix.data.formula.FormulaGenerator;
import com.alabs.automation.framework.steps.api.ResponseSteps;
import com.alabs.automation.phoenix.data.common.InvalidAuthHeadersDataProvider;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.models.formula.formula.FormulaResponse;
import com.alabs.automation.phoenix.steps.formulaservice.database.tables.FormulaTableSteps;
import com.alabs.automation.phoenix.steps.formulaservice.formula.DeleteFormulaSteps;
import com.alabs.automation.phoenix.steps.formulaservice.formula.FormulaPreconditionSteps;
import com.alabs.automation.phoenix.steps.formulaservice.formula.FormulaResponseSteps;
import com.alabs.automation.phoenix.steps.formulaservice.formula.ReadFormulaSteps;
import com.alabs.automation.phoenix.tests.formulaservice.BaseFormulasTest;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaValueOption.FIRST_PRIOR_DEFAULT_TRUE;
import static com.alabs.automation.phoenix.constants.formulaservice.FormulaValueOption.SECOND_PRIOR_DEFAULT_TRUE;
import static org.apache.http.HttpStatus.*;

@TmsLink("11318767")
public class DeleteFormulaTest extends BaseFormulasTest {
    private final DeleteFormulaSteps deleteFormulaSteps = new DeleteFormulaSteps();
    private final ReadFormulaSteps readFormulaSteps = new ReadFormulaSteps();
    private final FormulaTableSteps formulaTableSteps = new FormulaTableSteps();
    private FormulaResponse formulaResponseFirst;

    @BeforeMethod(groups = {"suite.smoke"})
    public void beforeMethod() {
        formulaResponseFirst = FormulaPreconditionSteps.createFormula(FIRST_PRIOR_DEFAULT_TRUE.getExpression());
    }

    @Test(description = "Delete formula", priority = Integer.MAX_VALUE, groups = {"suite.smoke"})
    public void testDeleteFormula() {
        Response response = deleteFormulaSteps.deleteFormula(formulaResponseFirst.getId());
        ResponseSteps.checkStatusCode(response, SC_NO_CONTENT);
        FormulaResponse formula = readFormulaSteps.getFormula(formulaResponseFirst.getId()).as(FormulaResponse.class);
        formulaTableSteps.checkFormulaConfigMatchesWithDatabase(formula);
    }

    @Test(description = "Send DELETE /admin/v1/formulas and check that formula is archived")
    public void testFormulaIsArchived() {
        Response response = deleteFormulaSteps.deleteFormula(formulaResponseFirst.getId());
        ResponseSteps.checkStatusCode(response, SC_NO_CONTENT);
        FormulaResponse formula = readFormulaSteps.getFormula(formulaResponseFirst.getId()).as(FormulaResponse.class);
        FormulaResponseSteps.checkFormulaResponseIsArchived(formula);
        formulaTableSteps.checkFormulaConfigMatchesWithDatabase(formula);
    }

    @Test(description = "Send DELETE /admin/v1/formulas call with several ids")
    public void testDeleteFormulaWithSeveralIds() {
        FormulaResponse formulaResponseSecond = FormulaPreconditionSteps.createFormula(SECOND_PRIOR_DEFAULT_TRUE.getExpression());
        Response response = deleteFormulaSteps.deleteFormula(formulaResponseFirst.getId(), formulaResponseSecond.getId());
        ResponseSteps.checkStatusCode(response, SC_NO_CONTENT);
        FormulaResponse formulaFirst = readFormulaSteps.getFormula(formulaResponseFirst.getId()).as(FormulaResponse.class);
        FormulaResponse formulaSecond = readFormulaSteps.getFormula(formulaResponseSecond.getId()).as(FormulaResponse.class);
        FormulaResponseSteps.checkFormulaResponseIsArchived(formulaFirst);
        FormulaResponseSteps.checkFormulaResponseIsArchived(formulaSecond);
        formulaTableSteps.checkFormulaConfigMatchesWithDatabase(formulaFirst);
        formulaTableSteps.checkFormulaConfigMatchesWithDatabase(formulaSecond);
    }

    @Test(description = "Send DELETE /admin/v1/formulas call with non-existing formula_id")
    public void testDeleteFormulaWithNonExistingIds() {
        Response response = deleteFormulaSteps.deleteFormula(FormulaGenerator.generateFormulaId());
        ResponseSteps.checkStatusCode(response, SC_NO_CONTENT);
    }

    @Test(description = "Send DELETE /admin/v1/formulas call with invalid auth header",
            dataProviderClass = InvalidAuthHeadersDataProvider.class, dataProvider = "invalidAuthRequestSpecifications")
    public void testDeleteFormulaWithoutUserIdHeader(InvalidAuthRequestSpecification invalidAuthRequestSpecification) {
        Response response = deleteFormulaSteps.deleteFormulaWithInvalidAuthHeader(invalidAuthRequestSpecification, formulaResponseFirst.getId());
        ResponseSteps.checkStatusCode(response, SC_UNAUTHORIZED);
    }

    @Test(description = "Send DELETE /admin/v1/formulas call with ids empty value")
    public void testDeleteFormulaWithIdsEmptyValue() {
        Response responseWithoutIds = deleteFormulaSteps.deleteFormula(StringUtils.EMPTY);
        ResponseSteps.checkStatusCode(responseWithoutIds, SC_BAD_REQUEST);
    }

    @Test(description = "Send DELETE /admin/v1/formulas call without ids parameter")
    public void testDeleteFormulaWithoutIdsParameter() {
        Response responseWithoutIds = deleteFormulaSteps.deleteFormulaWithoutIdsParameter();
        ResponseSteps.checkStatusCode(responseWithoutIds, SC_BAD_REQUEST);
    }
}
