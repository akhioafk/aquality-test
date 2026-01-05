package com.alabs.automation.phoenix.steps.formulaservice.formula;

import com.alabs.automation.framework.constants.common.Parameters;
import com.alabs.automation.phoenix.constants.common.SearchProperty;
import com.alabs.automation.phoenix.constants.formulaservice.Endpoints;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.models.common.PaginationParams;
import com.alabs.automation.phoenix.models.common.search.SearchResponse;
import com.alabs.automation.phoenix.models.formula.formula.FormulaResponse;
import com.alabs.automation.phoenix.steps.common.ISearchSteps;
import com.alabs.automation.phoenix.steps.formulaservice.BaseFormulaSteps;
import io.qameta.allure.Step;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public final class SearchFormulaSteps extends BaseFormulaSteps implements ISearchSteps<FormulaResponse> {

    private Response searchItem(RequestSpecification requestSpecification, SearchProperty searchProperty, String query) {
        String searchValue = searchProperty == null ? null : searchProperty.getProperty();
        return requestSpecification
                .queryParam(Parameters.QUERY, query)
                .queryParam(Parameters.SEARCH_PROPERTIES, searchValue)
                .get(Endpoints.FORMULAS_SEARCH);
    }

    @Step("Search formulas with property={searchProperty} and value={query}")
    public Response searchItem(SearchProperty searchProperty, String query) {
        return searchItem(commonRequestSpecification(getAdminEmail()), searchProperty, query);
    }

    @Step("Search formulas with parameter '{param}' with value '{value}'")
    public Response searchItemWithOneParam(String param, String value) {
        return commonRequestSpecification(getAdminEmail())
                .queryParam(param, value)
                .get(Endpoints.FORMULAS_SEARCH);
    }

    @Step("Search formulas with invalid auth header")
    public Response searchItemWithInvalidAuthHeader(InvalidAuthRequestSpecification invalidAuthRequestSpecification,
                                                    SearchProperty searchProperty, String query) {
        return searchItem(invalidAuthRequestSpecification.getRequestSpecification(getServiceUrl(),
                getBasePath()), searchProperty, query);
    }

    @Step("Search formulas with pagination")
    public Response searchItem(SearchProperty searchProperty, String query, PaginationParams params) {
        RequestSpecification withPagination = searchItem(commonRequestSpecification(getAdminEmail()), params.getLimit(), params.getOffset());
        return searchItem(withPagination, searchProperty, query);
    }

    @Step("Search formulas with offset={offset}")
    public Response searchItemWithOffset(SearchProperty searchProperty, String query, int offset) {
        RequestSpecification withOffset = searchItemWithOffset(commonRequestSpecification(getAdminEmail()), offset);
        return searchItem(withOffset, searchProperty, query);
    }

    @Step("Search formulas with limit={limit}")
    public Response searchItemWithLimit(SearchProperty searchProperty, String query, int limit) {
        RequestSpecification withLimit = searchItemWithLimit(commonRequestSpecification(getAdminEmail()), limit);
        return searchItem(withLimit, searchProperty, query);
    }

    @Step("Check that search response contains expected query '{query}' in formula ids")
    public void checkResponseContainsIds(Response response, String query) {
        SearchResponse<FormulaResponse> actualList = response.as(new TypeRef<>() {});
        checkResponseItemsContainQuery(actualList, query, FormulaResponse::getId);
    }
}
