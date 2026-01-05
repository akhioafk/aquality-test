package com.alabs.automation.phoenix.steps.formulaservice.formula;

import com.alabs.automation.phoenix.constants.formulaservice.Endpoints;
import com.alabs.automation.phoenix.models.common.PaginationParams;
import com.alabs.automation.phoenix.models.common.search.SearchResponse;
import com.alabs.automation.phoenix.models.formula.constant.ConstantResponse;
import com.alabs.automation.phoenix.models.formula.formula.FormulaResponse;
import com.alabs.automation.phoenix.steps.common.ISearchSteps;
import com.alabs.automation.phoenix.steps.formulaservice.BaseFormulaSteps;
import com.alabs.automation.phoenix.steps.formulaservice.database.tables.FormulaTableSteps;
import io.qameta.allure.Step;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.ArrayUtils;
import org.testng.Assert;

import java.util.Arrays;
import java.util.Comparator;

public final class ListFormulaSteps extends BaseFormulaSteps implements ISearchSteps<ConstantResponse> {

    private Response getAllFormulas(RequestSpecification requestSpecification) {
        return requestSpecification
                .get(Endpoints.FORMULAS);
    }

    @Step("Get all formulas")
    public Response getAllFormulas() {
        return getAllFormulas(commonRequestSpecification(getAdminEmail()));
    }

    @Step("Get all formulas with {paramType} '{param}'")
    public Response getAllFormulasWithParam(String paramType, Object param) {
        return getAllFormulas(commonRequestSpecification(getAdminEmail()).queryParam(paramType, param));
    }

    @Step("Get all formulas with pagination")
    public Response getAllFormulasWithPagination(PaginationParams params) {
        RequestSpecification withPagination = searchItem(commonRequestSpecification(getAdminEmail()), params.getLimit(), params.getOffset());
        return getAllFormulas(withPagination);
    }

    @Step("Check that response 'total_count' field equals to amount of items found in database")
    public void checkResponseItemsCountEqualsToDB(Response response) {
        SearchResponse<FormulaResponse> actualList = response.as(new TypeRef<SearchResponse<FormulaResponse>>() {});
        Assert.assertEquals(actualList.getTotalCount(), new FormulaTableSteps().countFormulaConfigs(),
                "Total count in response should be equal to amount of items found in database");
    }

    @Step("Check that formulas list is sorted by updated_at parameter")
    public void checkFormulasListResponseIsSortedByUpdatedAt(Response response, boolean isAscending) {
        SearchResponse<FormulaResponse> actualList = response.as(new TypeRef<SearchResponse<FormulaResponse>>() {});
        if (isAscending) {
            Assert.assertTrue(ArrayUtils.isSorted(actualList.getData(), Comparator.comparing(FormulaResponse::getUpdatedAt)),
                    "List should be sorted in ascending order by updated_at parameter");
        } else {
            Assert.assertTrue(ArrayUtils.isSorted(actualList.getData(), (a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt())),
                    "List should be sorted in descending order by updated_at parameter");
        }
    }

    @Step("Check that all formulas in list response have archived={isArchived}")
    public void checkFormulasListResponseIsReceivedByArchived(Response response, boolean isArchived) {
        SearchResponse<FormulaResponse> actualList = response.as(new TypeRef<SearchResponse<FormulaResponse>>() {});
        if (isArchived) {
            Assert.assertTrue(Arrays.stream(actualList.getData()).allMatch(FormulaResponse::getArchived),
                    "List data should contain only archived formulas");
        } else {
            Assert.assertTrue(Arrays.stream(actualList.getData()).noneMatch(FormulaResponse::getArchived),
                    "List data should contain only active formulas");
        }
    }
}
