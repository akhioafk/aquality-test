package com.alabs.automation.phoenix.steps.formulaservice.formula;

import com.alabs.automation.framework.constants.common.Parameters;
import com.alabs.automation.phoenix.constants.common.SearchProperty;
import com.alabs.automation.phoenix.constants.formulaservice.Endpoints;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.models.common.search.QuickSearchResponse;
import com.alabs.automation.phoenix.steps.common.IQuickSearchSteps;
import com.alabs.automation.phoenix.steps.formulaservice.BaseFormulaSteps;
import com.alabs.automation.phoenix.steps.formulaservice.database.tables.FormulaTableSteps;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

public final class QuickSearchFormulaSteps extends BaseFormulaSteps implements IQuickSearchSteps {

    private Response quickSearchItem(RequestSpecification requestSpecification) {
        return requestSpecification
                .get(Endpoints.FORMULAS_QUICK_SEARCH);
    }

    private Response quickSearchItem(RequestSpecification requestSpecification, SearchProperty searchProperty, String query) {
        String searchValue = searchProperty == null ? null : searchProperty.getProperty();
        return quickSearchItem(requestSpecification
                .queryParam(Parameters.QUERY, query)
                .queryParam(Parameters.SEARCH_PROPERTIES, searchValue));
    }

    @Step("Quick search formulas")
    public Response quickSearchItem(SearchProperty searchProperty, String query) {
        return quickSearchItem(commonRequestSpecification(getAdminEmail()), searchProperty, query);
    }

    @Step("Quick search formulas with parameter '{param}' with value '{value}'")
    public Response quickSearchItemWithOneParam(String param, String value) {
        return quickSearchItem(commonRequestSpecification(getAdminEmail())
                .queryParam(param, value));
    }

    @Step("Quick search formulas with invalid auth header")
    public Response quickSearchItemWithInvalidAuthHeader(InvalidAuthRequestSpecification invalidAuthRequestSpecification,
                                                         SearchProperty searchProperty, String query) {
        return quickSearchItem(invalidAuthRequestSpecification.getRequestSpecification(getServiceUrl(),
                getBasePath()), searchProperty, query);
    }

    @Step("Quick search formulas with limit={limit}")
    public Response quickSearchItemWithLimit(SearchProperty searchProperty, String query, int limit) {
        RequestSpecification withLimit = commonRequestSpecification(getAdminEmail()).queryParam(Parameters.LIMIT, limit);
        return quickSearchItem(withLimit, searchProperty, query);
    }

    @Step("Check that quick search response 'total_found' field equals to amount of items found in database by id '{searchId}'")
    public void checkQuickSearchResponseItemsCountEqualsToDB(Response response, String searchId) {
        QuickSearchResponse quickSearchResponse = extractResponse(response, SearchProperty.ID);
        Assert.assertEquals(quickSearchResponse.getTotalFound(), new FormulaTableSteps().countFormulaConfigsById(searchId),
                "Total found field in response should be equal to amount of items found in database");
    }
}
