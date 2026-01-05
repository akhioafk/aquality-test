package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.phoenix.constants.common.Parameters;
import com.alabs.automation.phoenix.data.formula.FormulaDataProvider;
import com.alabs.automation.framework.steps.api.ResponseSteps;
import com.alabs.automation.phoenix.constants.common.CommonJsonSchemas;
import com.alabs.automation.phoenix.constants.common.SearchProperty;
import com.alabs.automation.phoenix.constants.formulaservice.JsonSchemas;
import com.alabs.automation.phoenix.data.common.InvalidAuthHeadersDataProvider;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.models.common.search.SearchResponse;
import com.alabs.automation.phoenix.models.formula.formula.FormulaResponse;
import com.alabs.automation.phoenix.steps.formulaservice.formula.QuickSearchFormulaSteps;
import com.alabs.automation.phoenix.tests.formulaservice.BaseFormulasTest;
import io.qameta.allure.TmsLink;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;
import java.util.function.Supplier;

import static com.alabs.automation.phoenix.constants.common.SearchProperty.ID;
import static com.alabs.automation.phoenix.constants.formulaservice.FormulaGenerationConstants.AUTO_FORMULA_STRING;
import static org.apache.http.HttpStatus.*;

@TmsLink("9030387")
public class QuickSearchFormulaTest extends BaseFormulasTest {
    private final QuickSearchFormulaSteps quickSearchFormulaSteps = new QuickSearchFormulaSteps();

    @Test(description = "Formula - Admin: Quick search formula", priority = Integer.MAX_VALUE, groups = {"suite.smoke"})
    public void quickSearchFormulaTest() {
        Response response = quickSearchAndCheck(() -> quickSearchFormulaSteps.quickSearchItem(ID, AUTO_FORMULA_STRING));
        quickSearchFormulaSteps.checkQuickSearchResponseItemsCountEqualsToDB(response, AUTO_FORMULA_STRING);
    }

    @Test(description = "Send GET /admin/v1/formulas/quicksearch with invalid parameters", dataProviderClass = FormulaDataProvider.class,
            dataProvider = "searchRequestsWithInvalidParameters")
    public void quickSearchFormulaWithInvalidParameters(String name, SearchProperty searchProperty, String query) {
        Response response = quickSearchFormulaSteps.quickSearchItem(searchProperty, query);
        ResponseSteps.checkStatusCode(response, SC_BAD_REQUEST);
    }

    @Test(description = "Send GET /admin/v1/formulas/quicksearch with non existing value of 'query'")
    public void quickSearchFormulaWithNonExistingName() {
        Response response = quickSearchFormulaSteps.quickSearchItem(ID, UUID.randomUUID().toString());
        ResponseSteps.checkStatusCode(response, SC_OK);
        ResponseSteps.checkJsonSchema(response, CommonJsonSchemas.QUICK_SEARCH_RESPONSE);
        quickSearchFormulaSteps.checkQuickSearchIsEmpty(response, ID);
    }

    @Test(description = "Send GET /admin/v1/formulas/quicksearch with one parameter", dataProviderClass = FormulaDataProvider.class,
            dataProvider = "searchRequestsWithOneParameter")
    public void quickSearchFormulaWithOneParameter(String name, String param, String value) {
        Response response = quickSearchFormulaSteps.quickSearchItemWithOneParam(param, value);
        ResponseSteps.checkStatusCode(response, SC_BAD_REQUEST);
    }

    @Test(description = "Send GET /admin/v1/formulas/quicksearch with limit")
    public void quickSearchFormulaWithLimit() {
        Response response = quickSearchAndCheck(() -> quickSearchFormulaSteps.quickSearchItemWithLimit(ID, AUTO_FORMULA_STRING, TEST_PAGINATION.getLimit()));
        quickSearchFormulaSteps.checkLimit(response, ID, TEST_PAGINATION.getLimit());
    }

    @Test(description = "Send GET /admin/v1/formulas/quicksearch with archived formula in 'query'")
    public void quickSearchArchivedFormula() {
        Response archivedFormulaResponse = listFormulaSteps.getAllFormulasWithParam(Parameters.ARCHIVED, true);
        ResponseSteps.checkStatusCode(archivedFormulaResponse, SC_OK);
        ResponseSteps.checkJsonSchema(archivedFormulaResponse, JsonSchemas.FORMULA_LIST);
        String archivedFormulaId = archivedFormulaResponse.as(new TypeRef<SearchResponse<FormulaResponse>>() {}).getData()[0].getId();
        Response response = quickSearchFormulaSteps.quickSearchItem(ID, archivedFormulaId);
        ResponseSteps.checkStatusCode(response, SC_OK);
        quickSearchFormulaSteps.checkQuickSearchIsEmpty(response, ID);
    }

    @Test(description = "Send GET /admin/v1/formulas/quicksearch with invalid auth header", dataProviderClass = InvalidAuthHeadersDataProvider.class,
            dataProvider = "invalidAuthRequestSpecifications")
    public void quickSearchFormulaWithInvalidHeader(InvalidAuthRequestSpecification invalidAuthRequestSpecification) {
        Response response = quickSearchFormulaSteps.quickSearchItemWithInvalidAuthHeader(invalidAuthRequestSpecification, ID, AUTO_FORMULA_STRING);
        ResponseSteps.checkStatusCode(response, SC_UNAUTHORIZED);
    }

    private Response quickSearchAndCheck(Supplier<Response> responseSupplier) {
        Response response = responseSupplier.get();
        ResponseSteps.checkStatusCode(response, SC_OK);
        ResponseSteps.checkJsonSchema(response, CommonJsonSchemas.QUICK_SEARCH_RESPONSE);
        quickSearchFormulaSteps.checkResponseContainsIds(response, AUTO_FORMULA_STRING);
        return response;
    }
}
