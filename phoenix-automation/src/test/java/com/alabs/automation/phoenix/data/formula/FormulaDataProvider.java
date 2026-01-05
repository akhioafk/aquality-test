package com.alabs.automation.phoenix.data.formula;

import com.alabs.automation.framework.constants.common.Parameters;
import com.alabs.automation.phoenix.constants.common.SearchProperty;
import lombok.experimental.UtilityClass;
import org.testng.annotations.DataProvider;

import static com.alabs.automation.framework.utilities.StringUtils.randomAlphanumeric;
import static com.alabs.automation.phoenix.constants.common.SearchProperty.ID;
import static com.alabs.automation.phoenix.constants.formulaservice.FormulaGenerationConstants.AUTO_FORMULA_STRING;

@UtilityClass
public class FormulaDataProvider {
    private static final int INVALID_QUERY_LENGTH = 2;

    @DataProvider
    public static Object[][] searchRequestsWithInvalidParameters() {
        return new Object[][]{
                {"Search request without specifying a value of 'query'", ID, org.apache.commons.lang3.StringUtils.EMPTY},
                {"Search request without specifying a value of 'search_properties'", null, AUTO_FORMULA_STRING},
                {"Search request with a query value that contains less than 3 characters", ID, randomAlphanumeric(INVALID_QUERY_LENGTH)},
                {"Search request with incorrect search property", SearchProperty.DESCRIPTION, AUTO_FORMULA_STRING}
        };
    }

    @DataProvider
    public static Object[][] searchRequestsWithOneParameter() {
        return new Object[][]{
                {"Search request without 'query' param", Parameters.SEARCH_PROPERTIES, ID.getProperty()},
                {"Search request without 'search property' param", Parameters.QUERY, AUTO_FORMULA_STRING}
        };
    }

    @DataProvider
    public static Object[][] sortParameters() {
        return new Object[][]{
                {"Sort parameters ascending", "updated_at,asc", true},
                {"Sort parameters descending", "updated_at,desc", false}
        };
    }

    @DataProvider
    public static Object[][] archivedParameters() {
        return new Object[][]{
                {"Archived parameter true", true},
                {"Archived parameter false", false}
        };
    }
}
