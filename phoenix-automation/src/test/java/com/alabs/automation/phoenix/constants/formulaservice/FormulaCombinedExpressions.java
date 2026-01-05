package com.alabs.automation.phoenix.constants.formulaservice;

public final class FormulaCombinedExpressions {
    public static final String[] combinedExpressions = new String[]{
            "IF((additionalAttribute('test', 7) > 1000) AND (1500 < 2000), 200, 100)",
            "IF((additionalAttribute('test', 7) * 10.78 > 1000) AND (1500 < 2000), 200, 100)",
            "IF((max(additionalAttribute('test', 7) * 10.78, 1100) > 1000) AND (1500 < 2000), 200, 100)",
            "IF((max(additionalAttribute('test', 7) * 10.78, 1100) > 1000) AND (1500 < 2000), 200, 100) * additionalAttribute('test_1', 8)",
            "IF((max(additionalAttribute('test', 7) * 10.78, 1100) > 1000) AND (1500 < 2000), 200, 100) * additionalAttribute('test', 3 / 7)",
            "roundDown(IF((max(additionalAttribute('test', 7) * 10.78, 1100) > 1000) AND (1500 < 2000), 200, 100) * additionalAttribute('test', 3 / 7))",
            "roundWithSignificantFigures(roundDown(IF((max(additionalAttribute('test', 7) * 10.78, 1100) > 1000) AND (1500 < 2000), 200, 100) * additionalAttribute('test', 3 / 7)), 2)",
            "roundDown(IF((max(additionalAttribute('test', 7) * 10.78, 1100) > 1000) AND (1500 < 2000), 200, 100) * additionalAttribute('test', 30.36))",
            "roundWithSignificantFigures(roundDown(IF((max(additionalAttribute('test', 7) * 10.78, 1100) > 1000) AND (1500 < 2000), 200, 100) * additionalAttribute('test', 30.36)), 2)",

            "IF((min(additionalAttribute('test_attribute'), 100) + 20) * userAttribute('tier_season_currency_modifier') >= 50, 2, 1)",
            "IF(additionalAttribute('test_attribute') >= 10, 100, 50)",
            "IF(additionalAttribute('test_attribute', 5) >= 10, 100, 50) + 2",
            "IF(additionalAttribute('test_attribute', 5) >= 10, 100, 50) % 100",
            "IF(additionalAttribute('test_attribute', 5) >= 10, 100, 50.3) * userAttribute('tier_season_currency_modifier')/3",
            "round(IF(additionalAttribute('test_attribute', 5) >= 10, 100, 50.3) * userAttribute('tier_season_currency_modifier') / 3)",
            "max(round(IF(additionalAttribute('test_attribute', 5) >= 10, 100, 50.3) * userAttribute('tier_season_currency_modifier') / 3), 17.5)",
            "max(round(IF(additionalAttribute('test_attribute', 5) >= 10, 100, 50.3) * userAttribute('tier_season_currency_modifier') / 3), 17.5) + 1/2",
            "max(round(IF(additionalAttribute('test_2', 15) >= 10, 100, 50.3) * userAttribute('tier_season_currency_modifier') / 3), 17.5) + 1/2"
    };
    public static final String[] mathExpressions = new String[]{
            "max(pow(2 + 3, 2) - 10, 10)",
            "max(pow(2 + 3.9, 9) - 10, 10)",
            "round(max(pow(2 + 3.9, 9) - 10, 10), 10)",
            "roundWithSignificantFigures(round(max(pow(2 + 3.9, 9) - 10, 10), 10), 1)",
            "roundWithSignificantFigures(round(max(pow(2 + 3.9, 9) - 10, 10), 10), 2)",
            "roundWithSignificantFigures(round(max(pow(2 + 3.9, 9) - 10, 10), 10), 3)",
            "roundWithSignificantFigures(round(max(pow(2 + 3.9, 9) - 10, 10), 10), 4)",
            "roundWithSignificantFigures(round(max(pow(2 + 3.9, 9) - 10, 10), 10), 1) / 2",
            "max(roundWithSignificantFigures(round(max(pow(2 + 3.9, 9) - 10, 10), 10), 10), additionalAttribute('test', 4660000)) * 2.5"
    };
    public static final String[] logicExpressions = new String[]{
            "((24 + 22 / 21) * 2) - 45",
            "min((((24 + 22 / 21) * 2) - 45), additionalAttribute('test_attribute', 3))",
            "round((24 + 22 / 21) * 2, 10)",
            "roundUp(5.21 * 2 + 0.3, 2)",
            "pow(2 + 3, 2) - 10",
            "(log10(1000) + 2) / additionalAttribute('test_2', 0)"
    };
}