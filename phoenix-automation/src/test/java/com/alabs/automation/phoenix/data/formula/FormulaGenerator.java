package com.alabs.automation.phoenix.data.formula;

import com.alabs.automation.framework.utilities.Randomizer;
import com.alabs.automation.framework.utilities.StringUtils;
import com.alabs.automation.phoenix.constants.formulaservice.FormulaValuePriority;
import com.alabs.automation.phoenix.models.formula.formula.FormulaExportRequest;
import com.alabs.automation.phoenix.models.formula.formula.FormulaExpression;
import com.alabs.automation.phoenix.models.formula.formula.FormulaRequest;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaGenerationConstants.*;

@UtilityClass
public class FormulaGenerator {

    public static String generateFormulaId() {
        return prepareRandomFormulaString(MAX_ID_LENGTH);
    }

    public static FormulaExpression prepareExpression(FormulaValuePriority priority, boolean isDefault) {
        return prepareExpressionWithoutRuleId(priority, isDefault, generateExpression());
    }

    public static FormulaExpression prepareUserAttributeExpression(FormulaValuePriority priority, boolean isDefault, String userAttribute) {
        return prepareExpressionWithoutRuleId(priority, isDefault, generateExpressionWithAttributeFunction(userAttribute));
    }

    public static FormulaExpression prepareUserAttributeExpressionWithCoefficient(FormulaValuePriority priority, boolean isDefault, String userAttribute, int coefficient) {
        return prepareExpressionWithoutRuleId(priority, isDefault, generateExpressionWithUserAttributeFunction(userAttribute, coefficient));
    }

    public static FormulaExpression prepareSeveralUserAttributesExpression(FormulaValuePriority priority, boolean isDefault, String... userAttributes) {
        return prepareExpressionWithoutRuleId(priority, isDefault, generateExpressionWithSeveralAttributesFunction(userAttributes));
    }

    public static FormulaExpression prepareAdditionalAttributeExpression(FormulaValuePriority priority, boolean isDefault, String additionalAttribute, int coefficient) {
        return prepareExpressionWithoutRuleId(priority, isDefault, generateExpressionWithAdditionalAttributeFunction(additionalAttribute, coefficient));
    }

    public static FormulaExpression prepareConstantExpression(FormulaValuePriority priority, boolean isDefault, String constantId) {
        return prepareExpressionWithoutRuleId(priority, isDefault, generateExpressionWithConstant(constantId));
    }

    public static FormulaExpression prepareExpression(FormulaValuePriority priority, boolean isDefault, String ruleId) {
        FormulaExpression formulaExpression = prepareExpression(priority, isDefault);
        formulaExpression.setRuleId(ruleId);
        return formulaExpression;
    }

    public static FormulaExpression prepareExpressionWithoutRuleId(FormulaValuePriority priority, boolean isDefault, String expression) {
        return FormulaExpression.builder()
                .priority(priority)
                .expression(expression)
                .isDefault(isDefault)
                .build();
    }

    public static FormulaRequest prepareFormulaWithExpressionOnly(FormulaExpression... formulaExpressions) {
        return FormulaRequest.builder()
                .expressions(formulaExpressions)
                .build();
    }

    public static FormulaRequest prepareFormulaWithoutDescription(FormulaExpression... formulaExpressions) {
        return prepareFormulaWithExpressionOnly(formulaExpressions)
                .toBuilder()
                .id(generateFormulaId())
                .build();
    }

    public static FormulaRequest prepareFormulaWithoutDescription(String expression, FormulaValuePriority priority, boolean isDefault) {
        FormulaExpression formulaExpression = prepareExpressionWithoutRuleId(priority, isDefault, expression);
        return prepareFormulaWithoutDescription(formulaExpression);
    }

    public static FormulaRequest prepareFormulaWithoutId(FormulaExpression... formulaExpressions) {
        return prepareFormulaWithExpressionOnly(formulaExpressions)
                .toBuilder()
                .description(generateValidDescription())
                .build();
    }

    public static FormulaRequest prepareFormula(FormulaExpression... formulaExpressions) {
        return prepareFormulaWithExpressionOnly(formulaExpressions)
                .toBuilder()
                .id(generateFormulaId())
                .description(generateValidDescription())
                .build();
    }

    public static FormulaRequest prepareFormula(String formulaId, FormulaExpression... formulaExpressions) {
        return prepareFormula(formulaExpressions)
                .toBuilder()
                .id(formulaId)
                .build();
    }

    public static FormulaRequest prepareFormulaWithInvalidDescription(FormulaExpression... formulaExpressions) {
        return prepareFormula(formulaExpressions)
                .toBuilder()
                .description(generateInvalidDescription())
                .build();
    }

    public static FormulaExportRequest prepareFormulaExportRequest(String... formulaIds) {
        return FormulaExportRequest.builder()
                .ids(formulaIds)
                .build();
    }

    public static String generateValidDescription() {
        return prepareRandomFormulaString(COMMON_DESCRIPTION_LENGTH);
    }

    private static String generateInvalidDescription() {
        return prepareRandomFormulaString(MAX_DESCRIPTION_LENGTH + 1);
    }

    private static String prepareRandomFormulaString(int length) {
        return String.format(FORMULA_STRING_TEMPLATE, StringUtils.randomAlphanumeric(length))
                .substring(0, length)
                .toLowerCase();
    }

    private static String generateExpression() {
        return FormulaType.ADD_ONE_HUNDRED.getValue(String.format("%s", Randomizer.randomPositiveInt(MAX_VALUE)));
    }

    private static String generateExpressionWithAttributeFunction(String userAttribute) {
        return String.format("attribute(\"%s\")", userAttribute);
    }

    private static String generateExpressionWithAdditionalAttributeFunction(String userAttribute, int coefficient) {
        return String.format("additionalAttribute('%s')*%d", userAttribute, coefficient);
    }

    private static String generateExpressionWithUserAttributeFunction(String userAttribute, int coefficient) {
        return String.format("userAttribute('%s')*%d", userAttribute, coefficient);
    }

    private static String generateExpressionWithSeveralAttributesFunction(String... userAttributes) {
        return Arrays.stream(userAttributes)
                .map(attr -> String.format("attribute(\"%s\")", attr))
                .collect(Collectors.joining(" + "));
    }

    private static String generateExpressionWithConstant(String id) {
        return String.format("constant('%s')", id);
    }
}
