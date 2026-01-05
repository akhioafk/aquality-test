package com.alabs.automation.phoenix.constants.formulaservice;

import com.alabs.automation.framework.utilities.StringUtils;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FormulaGenerationConstants {
    public static final String FORMULA_STRING_TEMPLATE = StringUtils.prepareAutoFormatString("formula_%s");
    public static final String AUTO_FORMULA_STRING = StringUtils.prepareAutoFormatString("formula_");
    public static final String AUTO_NESTED_FORMULA_STRING_TEMPLATE = StringUtils.prepareAutoFormatString("nested_formula_%s");
    public static final String AUTO_NESTED_FORMULA_STRING = StringUtils.prepareAutoFormatString("nested_formula_");
    public static final String AUTO_DESCRIPTION_TEMPLATE = StringUtils.prepareAutoFormatString("description_%s");
    public static final int MAX_ID_LENGTH = 50;
    public static final int MAX_VALUE = 100;
    public static final int NEXT_MAX_VALUE = 200;
    public static final int ATTRIBUTE_MAX_VALUE = 300;
    public static final int COMMON_DESCRIPTION_LENGTH = 50;
    public static final int MAX_DESCRIPTION_LENGTH = 1000;
    public static final int TEXT_VALUE_LENGTH = 10;
    public static final int SHIFT_VALUE_FIFTEEN_SECONDS = 15;
    public static final int SHIFT_VALUE_SIXTY_SECONDS = 60;
    public static final int FIRST_TWO_ATTRIBUTES = 2;
    public static final int AT_MOST_MINUTES = 2;
    public static final int INVALID_DESCRIPTION_LENGTH = 1001;
}
