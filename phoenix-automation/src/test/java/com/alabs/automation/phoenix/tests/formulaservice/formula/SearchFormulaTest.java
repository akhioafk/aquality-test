package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.phoenix.constants.common.Parameters;
import com.alabs.automation.phoenix.data.formula.FormulaDataProvider;
import com.alabs.automation.phoenix.data.formula.FormulaGenerator;
import com.alabs.automation.framework.steps.api.ResponseSteps;
import com.alabs.automation.phoenix.constants.common.SearchProperty;
import com.alabs.automation.phoenix.constants.formulaservice.JsonSchemas;
import com.alabs.automation.phoenix.data.common.InvalidAuthHeadersDataProvider;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.models.common.search.SearchResponse;
import com.alabs.automation.phoenix.models.formula.formula.FormulaResponse;
import com.alabs.automation.phoenix.steps.formulaservice.formula.SearchFormulaSteps;
import com.alabs.automation.phoenix.tests.formulaservice.BaseFormulasTest;
import io.qameta.allure.TmsLink;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static com.alabs.automation.phoenix.constants.common.SearchProperty.ID;
import static com.alabs.automation.phoenix.constants.formulaservice.FormulaGenerationConstants.AUTO_FORMULA_STRING;
import static org.apache.http.HttpStatus.*;

@TmsLink("9030387")
public class SearchFormulaTest extends BaseFormulasTest {
    private final SearchFormulaSteps searchFormulaSteps = new SearchFormulaSteps();

    @Test(description = "Formula - Admin: Search formula", priority = Integer.MAX_VALUE, groups = {"suite.smoke"})
    public void searchFormulaTest() {
        Response response = searchAndCheck(() -> searchFormulaSteps.searchItem(ID, AUTO_FORMULA_STRING));
        searchFormulaSteps.checkResponseItemsCountEqualsToDB(response,
                formulaTableSteps.countFormulaConfigsById(AUTO_FORMULA_STRING));
    }

    @Test(description = "Send GET /admin/v1/formulas/search with invalid parameters", dataProviderClass = FormulaDataProvider.class,
            dataProvider = "searchRequestsWithInvalidParameters")
    public void searchFormulaWithInvalidParameters(String name, SearchProperty searchProperty, String query) {
        Response response = searchFormulaSteps.searchItem(searchProperty, query);
        ResponseSteps.checkStatusCode(response, SC_BAD_REQUEST);
    }

    @Test(description = "Send GET /admin/v1/formulas/search with non existing value of 'query'")
    public void searchFormulaWithNonExistingName() {
        Response response = searchFormulaSteps.searchItem(ID, FormulaGenerator.generateFormulaId());
        ResponseSteps.checkStatusCode(response, SC_OK);
        ResponseSteps.checkJsonSchema(response, JsonSchemas.FORMULA_LIST);
        searchFormulaSteps.checkResponseHasNoItems(response);
    }

    @Test(description = "Send GET /admin/v1/formulas/search with one parameter", dataProviderClass = FormulaDataProvider.class,
            dataProvider = "searchRequestsWithOneParameter")
    public void searchFormulaWithOneParameter(String name, String param, String value) {
        Response response = searchFormulaSteps.searchItemWithOneParam(param, value);
        ResponseSteps.checkStatusCode(response, SC_BAD_REQUEST);
    }

    @Test(description = "Send GET /admin/v1/formulas/search with offset")
    public void searchFormulaWithOffset() {
        Response initialList = searchAndCheck(() -> searchFormulaSteps.searchItem(ID, AUTO_FORMULA_STRING));
        Response response = searchAndCheck(() -> searchFormulaSteps.searchItemWithOffset(ID, AUTO_FORMULA_STRING, TEST_PAGINATION.getOffset()));
        searchFormulaSteps.checkOffset(initialList, response, TEST_PAGINATION.getOffset());
    }

    @Test(description = "Send GET /admin/v1/formulas/search with limit")
    public void searchFormulaWithLimit() {
        Response response = searchAndCheck(() -> searchFormulaSteps.searchItemWithLimit(ID, AUTO_FORMULA_STRING, TEST_PAGINATION.getLimit()));
        searchFormulaSteps.checkLimit(response, TEST_PAGINATION.getLimit());
    }

    @Test(description = "Send GET /admin/v1/formulas/search call with pagination")
    public void searchFormulaWithPagination() {
        Response initialList = searchAndCheck(() -> searchFormulaSteps.searchItem(ID, AUTO_FORMULA_STRING));
        Response response = searchAndCheck(() -> searchFormulaSteps.searchItem(ID, AUTO_FORMULA_STRING, TEST_PAGINATION));
        searchFormulaSteps.checkOffset(initialList, response, TEST_PAGINATION.getOffset());
        searchFormulaSteps.checkLimitWithOffset(response, TEST_PAGINATION.getLimit(), TEST_PAGINATION.getOffset());
    }

    @Test(description = "Send GET /admin/v1/formulas/search call with archived formula in 'query'")
    public void searchArchivedFormula() {
        Response archivedFormulasResponse = listFormulaSteps.getAllFormulasWithParam(Parameters.ARCHIVED, true);
        ResponseSteps.checkStatusCode(archivedFormulasResponse, SC_OK);
        ResponseSteps.checkJsonSchema(archivedFormulasResponse, JsonSchemas.FORMULA_LIST);
        String archivedFormulaId = archivedFormulasResponse.as(new TypeRef<SearchResponse<FormulaResponse>>() {}).getData()[0].getId();
        Response response = searchFormulaSteps.searchItem(ID, archivedFormulaId);
        ResponseSteps.checkStatusCode(response, SC_OK);
        searchFormulaSteps.checkResponseHasNoItems(response);
    }

    @Test(description = "Send GET /admin/v1/formulas/search with invalid auth header", dataProviderClass = InvalidAuthHeadersDataProvider.class,
            dataProvider = "invalidAuthRequestSpecifications")
    public void searchFormulaWithInvalidHeader(InvalidAuthRequestSpecification invalidAuthRequestSpecification) {
        Response response = searchFormulaSteps.searchItemWithInvalidAuthHeader(invalidAuthRequestSpecification, ID, AUTO_FORMULA_STRING);
        ResponseSteps.checkStatusCode(response, SC_UNAUTHORIZED);
    }

    private Response searchAndCheck(Supplier<Response> responseSupplier) {
        Response response = responseSupplier.get();
        ResponseSteps.checkStatusCode(response, SC_OK);
        ResponseSteps.checkJsonSchema(response, JsonSchemas.FORMULA_LIST);
        searchFormulaSteps.checkResponseContainsIds(response, AUTO_FORMULA_STRING);
        return response;
    }
}
