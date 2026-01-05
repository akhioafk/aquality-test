package com.alabs.automation.phoenix.constants.formulaservice;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonSchemas {
    private static final String PATH_TO_CONFIG_SCHEMAS = "jsonschemas/formula/";
    private static final String PATH_TO_CONSTANTS = PATH_TO_CONFIG_SCHEMAS + "constants/";
    private static final String PATH_TO_RANGES = PATH_TO_CONFIG_SCHEMAS + "ranges/";
    private static final String PATH_TO_METADATA = PATH_TO_CONFIG_SCHEMAS + "metadata/";
    private static final String PATH_TO_FORMULAS = PATH_TO_CONFIG_SCHEMAS + "formulas/";
    private static final String PATH_TO_NESTED_FORMULAS = PATH_TO_CONFIG_SCHEMAS + "nested/";
    public static final String CONSTANT_LIST = PATH_TO_CONSTANTS + "ConstantsListResponse.json";
    public static final String RANGE_LIST = PATH_TO_RANGES + "RangesListResponse.json";
    public static final String FORMULA_LIST = PATH_TO_FORMULAS + "FormulaListResponse.json";
    public static final String FORMULA_RESPONSE = PATH_TO_FORMULAS + "FormulaResponse.json";
    public static final String LIST_USER_FORMULA_ATTRIBUTES = PATH_TO_FORMULAS + "ListUserFormulaAttributes.json";
    public static final String LIST_FORMULA_ATTRIBUTES_METADATA = PATH_TO_FORMULAS + "ListFormulaAttributesMetadata.json";
    public static final String AVAILABLE_FORMULA_COMPONENTS = PATH_TO_METADATA + "AvailableFormulaComponents.json";
    public static final String NESTED_FORMULA_RESPONSE = PATH_TO_NESTED_FORMULAS + "NestedFormulaResponse.json";
    public static final String ALL_NESTED_FORMULA_RESPONSE = PATH_TO_NESTED_FORMULAS + "AllNestedFormulaResponse.json";
    public static final String IMPORT_NESTED_FORMULA_RESPONSE = PATH_TO_NESTED_FORMULAS + "ImportNestedFormulaResponse.json";
    public static final String EXPORT_NESTED_FORMULA_RESPONSE = PATH_TO_NESTED_FORMULAS + "ExportNestedFormulaResponse.json";
    public static final String FORMULA_EXPORT_RESPONSE = PATH_TO_FORMULAS + "ExportFormulaResponse.json";
    public static final String FORMULA_IMPORT_RESPONSE = PATH_TO_FORMULAS + "ImportFormulaResponse.json";
    public static final String EXPORT_IMPORT_CONSTANT_RESPONSE = PATH_TO_CONSTANTS + "ExportImportConstantResponse.json";
    public static final String EXPORT_IMPORT_RANGE_RESPONSE = PATH_TO_RANGES + "ExportImportRangeResponse.json";
}
