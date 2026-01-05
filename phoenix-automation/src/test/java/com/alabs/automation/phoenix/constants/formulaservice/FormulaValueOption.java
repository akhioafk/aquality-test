package com.alabs.automation.phoenix.constants.formulaservice;

import com.alabs.automation.phoenix.models.common.WrappedNumeric;
import com.alabs.automation.phoenix.models.formula.constant.ConstantValue;
import com.alabs.automation.phoenix.models.formula.range.FormulaRangeLevel;
import com.alabs.automation.phoenix.models.formula.formula.FormulaExpression;
import lombok.AllArgsConstructor;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaValuePriority.*;
import static com.alabs.automation.phoenix.data.formula.ConstantGenerator.prepareValue;
import static com.alabs.automation.phoenix.data.formula.FormulaGenerator.*;
import static com.alabs.automation.phoenix.data.formula.FormulaRangeGenerator.prepareLevel;

@AllArgsConstructor
public enum FormulaValueOption {
    FIRST_PRIOR_DEFAULT_TRUE(FIRST_PRIORITY, true),
    SECOND_PRIOR_DEFAULT_FALSE(SECOND_PRIORITY, false),
    THIRD_PRIOR_DEFAULT_FALSE(THIRD_PRIORITY, false),
    SECOND_PRIOR_DEFAULT_TRUE(SECOND_PRIORITY, true),
    FIRST_PRIOR_DEFAULT_FALSE(FIRST_PRIORITY, false),
    THIRD_PRIOR_DEFAULT_TRUE(THIRD_PRIORITY, true);

    private final FormulaValuePriority priority;
    private final boolean isDefault;

    public ConstantValue getValue() {
        return prepareValue(priority, isDefault);
    }

    public ConstantValue getValue(String id) {
        return prepareValue(priority, isDefault, id);
    }

    public ConstantValue getValue(WrappedNumeric wrappedNumeric) {
        return prepareValue(priority, isDefault, wrappedNumeric);
    }

    public ConstantValue getValue(String id, WrappedNumeric wrappedNumeric) {
        ConstantValue constantValue = getValue(id);
        constantValue.setValue(wrappedNumeric);
        return constantValue;
    }

    public FormulaRangeLevel getLevel() {
        return prepareLevel(priority, isDefault);
    }

    public FormulaRangeLevel getLevel(String id) {
        return prepareLevel(priority, isDefault, id);
    }

    public FormulaRangeLevel getLevel(WrappedNumeric wrappedNumeric) {
        return prepareLevel(priority, isDefault, wrappedNumeric);
    }

    public FormulaExpression getExpression() {
        return prepareExpression(priority, isDefault);
    }

    public FormulaExpression getExpression(String id) {
        return prepareExpression(priority, isDefault, id);
    }

    public FormulaExpression getExpressionWithPriority(FormulaValuePriority priority, String id) {
        return prepareExpression(priority, isDefault, id);
    }

    public FormulaExpression getExpression(String id, String expression) {
        FormulaExpression formulaExpression = getExpression(id);
        return setExpression(formulaExpression, expression);
    }

    public FormulaExpression getCustomizedExpression(String expression) {
        FormulaExpression formulaExpression = getExpression();
        return setExpression(formulaExpression, expression);
    }

    public FormulaExpression getCustomizedExpression(FormulaValuePriority priority, String expression, String ruleId) {
        FormulaExpression formulaExpression = getExpressionWithPriority(priority, ruleId);
        return setExpression(formulaExpression, expression);
    }

    public FormulaExpression getUserAttributeExpression(String userAttribute) {
        return prepareUserAttributeExpression(priority, isDefault, userAttribute);
    }

    public FormulaExpression getUserAttributeExpressionWithCoefficient(String userAttribute, int coefficient) {
        return prepareUserAttributeExpressionWithCoefficient(priority, isDefault, userAttribute, coefficient);
    }

    public FormulaExpression getAdditionalAttributeExpression(String additionalAttribute, int coefficient) {
        return prepareAdditionalAttributeExpression(priority, isDefault, additionalAttribute, coefficient);
    }

    public FormulaExpression getSeveralUserAttributesExpression(String... userAttributes) {
        return prepareSeveralUserAttributesExpression(priority, isDefault, userAttributes);
    }

    public FormulaExpression getConstantExpression(String constantId) {
        return prepareConstantExpression(priority, isDefault, constantId);
    }

    private FormulaExpression setExpression(FormulaExpression formulaExpression, String expression) {
        formulaExpression.setExpression(expression);
        return formulaExpression;
    }
}
