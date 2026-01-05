package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.phoenix.constants.common.Parameters;
import com.alabs.automation.phoenix.data.formula.FormulaDataProvider;
import com.alabs.automation.framework.steps.api.ResponseSteps;
import com.alabs.automation.phoenix.constants.formulaservice.JsonSchemas;
import com.alabs.automation.phoenix.tests.formulaservice.BaseFormulasTest;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static org.apache.http.HttpStatus.*;

@TmsLink("9030387")
public class ListFormulaTest extends BaseFormulasTest {

    @Test(description = "Get all formulas", priority = Integer.MAX_VALUE, groups = {"suite.smoke"})
    public void getAllFormulas() {
        Response response = getFormulasAndCheck(listFormulaSteps::getAllFormulas);
        listFormulaSteps.checkResponseItemsCountEqualsToDB(response);
    }

    @Test(description = "Send GET /admin/v1/formulas with limit")
    public void getAllFormulasWithLimit() {
        Response response = getFormulasAndCheck(() -> listFormulaSteps.getAllFormulasWithParam(Parameters.LIMIT, TEST_PAGINATION.getLimit()));
        listFormulaSteps.checkLimit(response, TEST_PAGINATION.getLimit());
    }

    @Test(description = "Send GET /admin/v1/formulas with offset")
    public void getAllFormulasWithOffset() {
        Response initialList = getFormulasAndCheck(listFormulaSteps::getAllFormulas);
        Response response = getFormulasAndCheck(() -> listFormulaSteps.getAllFormulasWithParam(Parameters.OFFSET, TEST_PAGINATION.getOffset()));
        listFormulaSteps.checkOffset(initialList, response, TEST_PAGINATION.getOffset());
    }

    @Test(description = "Send GET /admin/v1/formulas with both limit and offset")
    public void getAllFormulasWithLimitAndOffset() {
        Response initialList = getFormulasAndCheck(listFormulaSteps::getAllFormulas);
        Response response = getFormulasAndCheck(() -> listFormulaSteps.getAllFormulasWithPagination(TEST_PAGINATION));
        listFormulaSteps.checkOffset(initialList, response, TEST_PAGINATION.getOffset());
        listFormulaSteps.checkLimitWithOffset(response, TEST_PAGINATION.getLimit(), TEST_PAGINATION.getOffset());
    }

    @Test(description = "Send GET /admin/v1/formulas specifying 'sort' parameter", dataProviderClass = FormulaDataProvider.class,
            dataProvider = "sortParameters")
    public void getAllFormulasWithSort(String name, String sortingParam, boolean isAscending) {
        Response response = getFormulasAndCheck(() -> listFormulaSteps.getAllFormulasWithParam(Parameters.SORT, sortingParam));
        listFormulaSteps.checkFormulasListResponseIsSortedByUpdatedAt(response, isAscending);
    }

    @Test(description = "Send GET /admin/v1/formulas specifying 'archived' parameter", dataProviderClass = FormulaDataProvider.class,
            dataProvider = "archivedParameters")
    public void getAllFormulasWithArchived(String name, boolean isArchived) {
        Response response = getFormulasAndCheck(() -> listFormulaSteps.getAllFormulasWithParam(Parameters.ARCHIVED, isArchived));
        listFormulaSteps.checkFormulasListResponseIsReceivedByArchived(response, isArchived);
    }

    private Response getFormulasAndCheck(Supplier<Response> responseSupplier) {
        Response response = responseSupplier.get();
        ResponseSteps.checkStatusCode(response, SC_OK);
        ResponseSteps.checkJsonSchema(response, JsonSchemas.FORMULA_LIST);
        return response;
    }
}
