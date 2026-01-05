package com.alabs.automation.phoenix.constants.formulaservice;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValueCombinationDescription {
    public static final String DESCRIPTION_1 = "1.rule=true, priority=1, 2.default, rule=false, priority=2, 3.rule=false, priority=3";
    public static final String DESCRIPTION_2 = "1.rule=true, priority=1, 2.default, no rule, priority=2, 3.rule=false, priority=3";
    public static final String DESCRIPTION_3 = "1.default, no rule, priority=1, 2.rule=true, priority=2, 3.rule=false, priority=3";
    public static final String DESCRIPTION_4 = "1.default, rule=false, priority=1, 2.rule=true, priority=2, 3.rule=false, priority=3";
    public static final String DESCRIPTION_5 = "1.rule=false, priority=1, 2.default, rule=true, priority=2, 3.rule=false, priority=3";
    public static final String DESCRIPTION_6 = "1.default, no rule, priority=1, 2.rule=false, priority=2, 3.rule=true, priority=3";
    public static final String DESCRIPTION_7 = "1.rule=false, priority=1, 2.rule=true, priority=2, 3.default, no rule, priority=3";
    public static final String DESCRIPTION_8 = "1.default, rule=true, priority=1, 2.rule=true, priority=2, 3.rule=false, priority=3";
    public static final String DESCRIPTION_9 = "1.rule=true, priority=1, 2.default, rule=true, priority=2, 3.rule=false, priority=3";
    public static final String DESCRIPTION_10 = "1.default, no rule, priority=1, 2.rule=true, priority=2, 3.rule=true, priority=3";
    public static final String DESCRIPTION_11 = "1.rule=true, priority=1, 2.rule=true, priority=2, 3.default, no rule, priority=3";
    public static final String DESCRIPTION_12 = "1.default, rule=false, priority=1, 2.rule=false, priority=2, 3.rule=false, priority=3";
    public static final String DESCRIPTION_13 = "1.rule=false, priority=1, 2.default, rule=false, priority=2, 3.rule=false, priority=3";
    public static final String DESCRIPTION_14 = "1.default, no rule, priority=1, 2.rule=false, priority=2, 3.rule=false, priority=3";
    public static final String DESCRIPTION_15 = "1.rule=false, priority=1, 2.rule=false, priority=2, 3.default, no rule, priority=3";
}
