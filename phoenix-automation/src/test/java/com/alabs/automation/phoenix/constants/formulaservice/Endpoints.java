package com.alabs.automation.phoenix.constants.formulaservice;

import com.alabs.automation.phoenix.constants.common.Parameters;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Endpoints {
    public static final String BASE_ADMIN_PATH = "/admin/v1";
    public static final String BASE_EXTERNAL_PATH = "/external/v1";
    public static final String BASE_INTERNAL_PATH = "/internal/v1";
    public static final String SEARCH = "/search";
    public static final String QUICK_SEARCH = "/quicksearch";
    public static final String CONSTANTS = "/constants";
    public static final String CONSTANT_ID = String.format("%s/{%s}", CONSTANTS, Parameters.ID);
    public static final String RANGES = "/ranges";
    public static final String RANGE_ID = String.format("%s/{%s}", RANGES, Parameters.ID);
    public static final String FORMULAS = "/formulas";
    public static final String FORMULA_ID = String.format("%s/{%s}", FORMULAS, Parameters.ID);
    public static final String SNAPSHOTS = "/snapshots";
    public static final String ATTRIBUTES = "/attributes";
    public static final String IMPORT = "/import";
    public static final String EXPORT = "/export";
    public static final String CONSTANTS_SEARCH = CONSTANTS + SEARCH;
    public static final String CONSTANTS_QUICK_SEARCH = CONSTANTS + QUICK_SEARCH;
    public static final String RANGES_SEARCH = RANGES + SEARCH;
    public static final String RANGES_QUICK_SEARCH = RANGES + QUICK_SEARCH;
    public static final String FORMULAS_SEARCH = FORMULAS + SEARCH;
    public static final String FORMULAS_QUICK_SEARCH = FORMULAS + QUICK_SEARCH;
    public static final String EVALUATE = FORMULAS + "/evaluate";
    public static final String PROCESS = FORMULAS + "/process";
    public static final String ATTRIBUTES_METADATA = ATTRIBUTES + "/metadata";
    public static final String USER_ATTRIBUTES = "/user-attributes";
    public static final String METADATA = "/metadata";
    public static final String AVAILABLE_FORMULA_COMPONENTS = METADATA + "/available-formula-components";
    public static final String NESTED_FORMULAS = "/nested-formulas";
    public static final String NESTED_FORMULA_BY_ID = String.format("%s/{%s}", NESTED_FORMULAS, FormulaParameters.FORMULA_ID);
    public static final String SEARCH_NESTED_FORMULAS = NESTED_FORMULAS + SEARCH;
    public static final String NESTED_FORMULA_QUICK_SEARCH = NESTED_FORMULAS + QUICK_SEARCH;
    public static final String NESTED_FORMULA_IMPORT = NESTED_FORMULAS + IMPORT;
    public static final String NESTED_FORMULA_EXPORT = NESTED_FORMULAS + EXPORT;
    public static final String CONSTANTS_IMPORT = CONSTANTS + IMPORT;
    public static final String CONSTANTS_EXPORT = CONSTANTS + EXPORT;
    public static final String RANGES_IMPORT = RANGES + IMPORT;
    public static final String RANGES_EXPORT = RANGES + EXPORT;
    public static final String FORMULAS_IMPORT = FORMULAS + IMPORT;
    public static final String FORMULAS_EXPORT = FORMULAS + EXPORT;
    public static final String INVALIDATE_CACHE = "/caching/invalidate-all";
    public static final String STORAGE_DEPENDENCIES = "/storage/dependencies";
}
