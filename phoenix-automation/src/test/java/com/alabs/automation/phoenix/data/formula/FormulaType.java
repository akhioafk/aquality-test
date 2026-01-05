package com.alabs.automation.phoenix.data.formula;

import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import lombok.AllArgsConstructor;

import java.util.function.Function;

import static java.lang.Math.*;

@AllArgsConstructor
public enum FormulaType {
    ADD_ONE_HUNDRED("%s+100", variable -> (int) (variable + 100)),
    SUBTRACT_ONE_HUNDRED("%s-100", variable -> (int) (variable - 100)),
    MULTIPLY_BY_TWO("%s*2", variable -> (int) (variable * 2)),
    DIVIDE_BY_TWO("%s/2", variable -> (int) (variable / 2)),
    MODULO_TEN("10%%%s", variable -> (int) (10 % variable)),
    LOGARITHM_BASE_3("log(%s,3)", variable -> log(variable) / log(3)),
    LOGARITHM_BASE_2("log2(%s)", variable -> log(variable) / log(2)),
    LOGARITHM_BASE_10("log10(%s)", variable -> log10(variable)),
    DIVIDE_BY_TEN("%s/10", variable -> (variable / 10)),
    DIVIDE_BY_THREE("%s/3", variable -> (variable / 3)),
    DIVIDE_BY_ZERO("%s/0", variable -> variable / 0),
    ADD_FOUR_OVER_TEN("%s+4/10", variable -> (variable + 4.0 / 10)),
    IF_GREATER_THAN_1000("IF(%1$s>1000,200,100)", variable -> variable > 1000 ? 200 : 100),
    IF_LESS_THAN_1000("IF(%1$s<1000,200,100)", variable -> variable < 1000 ? 200 : 100),
    IF_GREATER_OR_EQUAL_TO_1500("IF(%1$s>=1500,200,100)", variable -> variable >= 1500 ? 200 : 100),
    IF_LESS_OR_EQUAL_TO_1000("IF(%1$s<=1000,200,100)", variable -> variable <= 1000 ? 200 : 100),
    IF_LESS_OR_GREATER_THAN_1000("IF(%1$s<>1000,200,%1$s+100)", variable -> (int) (variable != 1000 ? 200 : variable + 100)),
    IF_NOT_EQUAL_TO_1500("IF(%1$s!=1500,200,%1$s+100)", variable -> (int) (variable != 1500 ? 200 : variable + 100)),
    IF_EQUAL_TO_1500("IF(%1$s==1500,200,100)", variable -> variable == 1500 ? 200 : 100),
    IF_GREATER_THAN_1000_AND_LESS_THAN_2000("IF(%1$s>1000 AND %1$s<2000,200,100)",
            variable -> variable > 1000 && variable < 2000 ? 200 : 100),
    IF_EQUAL_TO_1500_OR_EQUAL_TO_2000("IF(%1$s=1500 OR %1$s=2000,200,%1$s+100)",
            variable -> (int) (variable > 1000 && variable < 2000 ? 200 : variable + 100)),
    MIN_COMPARING_TO_100("MIN(%s,100)", variable -> (int) min(variable, 100)),
    MAX_COMPARING_TO_100("MAX(%s,100)", variable -> (int) max(variable, 100)),
    ROUND_MULTIPLICATION_BY_0_003("ROUND(%s*0.003)", variable -> (int) round(variable * 0.003f)),
    ROUND_DOWN_MULTIPLICATION_BY_0_003("ROUNDDOWN(%s*0.003)", variable -> (int) (variable * 0.003f)),
    ROUND_UP_MULTIPLICATION_BY_0_003("ROUNDUP(%s*0.003)", variable -> (int) ceil(variable * 0.003f)),
    ROUND("ROUND(%s)", variable -> (int) round(variable)),
    ROUND_DOWN("ROUNDDOWN(%s)", variable -> (int) floor(variable)),
    ROUND_UP("ROUNDUP(%s)", variable -> (int) ceil(variable)),
    ROUND_DOWN_BY_10("ROUNDDOWN(%s,10)", variable -> (int) floor(variable / 10.0) * 10),
    ROUND_UP_BY_100("ROUNDUP(%s,100)", variable -> (int) ceil(variable / 100.0) * 100),
    ROUND_WITH_TWO_SIGNIFICANT_FIGURES("ROUNDWITHSIGNIFICANTFIGURES(%s,2)", variable -> {
        if (variable == 0) return 0;
        final double d = ceil(log10(abs(variable)));
        final int power = 2 - (int) d;
        final double magnitude = pow(10, power);
        final long shifted = Math.round(variable * magnitude);
        return shifted / magnitude;
    }),
    COMPLEX("IF(NOT(MAX(%1$s,100)>1500) AND MIN(%1$s,100)<2000," +
            "ROUNDUP(%1$s*0.003)," +
            "log2(%1$s))+IF(%1$s<=1000,log10 (%1$s),ROUNDDOWN(%1$s*0.003))",
            variable -> ((!(max(variable, 100) > 1500) && min(variable, 100) < 2000
                    ? ceil(variable * 0.003f)
                    : log(variable) / log(2))
                    + (variable <= 1000
                    ? log10(variable)
                    : ceil(variable * 0.003)))),
    CONDITIONS_MULTIPLICATION("IF(1<=0 OR 2>=3,10,20)" +
            " * IF(NOT(MAX(1000000,100)>1500) AND MIN(2,1)<2,0,ROUNDUP(0.1*log2(1000000)))/IF(log10(1)<>6,2,1)",
            variable -> 20),
    CONDITION_INSIDE_CONDITION("(ROUNDDOWN(ROUND(IF(10%%*10!=1,1 , IF(1+1=2,2,2)) * 2.9)*1.1)) - log(16,2)", variable -> 2),
    CONDITION_INSIDE_LOG("LOG(IF(1>2, 0, 10))", variable -> 2);

    private final String pattern;
    private final Function<Double, Number> formula;

    public String getValue(String variableName) {
        return String.format(pattern, variableName);
    }

    @Step("Calculating result of formula '{this.pattern}' (where %s or %1$s is a variable, %% is percentage)")
    @Attachment("Formula evaluation result")
    public Number calculate(Double variableValue) {
        return formula.apply(variableValue);
    }
}
