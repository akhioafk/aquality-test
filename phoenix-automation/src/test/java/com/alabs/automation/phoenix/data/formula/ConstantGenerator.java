package com.alabs.automation.phoenix.data.formula;

import com.alabs.automation.framework.utilities.Randomizer;
import com.alabs.automation.framework.utilities.StringUtils;
import com.alabs.automation.phoenix.constants.formulaservice.FormulaValuePriority;
import com.alabs.automation.phoenix.models.formula.constant.ConstantExportRequest;
import com.alabs.automation.phoenix.models.formula.constant.ConstantResponse;
import com.alabs.automation.phoenix.models.formula.constant.ConstantValue;
import com.alabs.automation.phoenix.models.formula.constant.ConstantRequest;
import com.alabs.automation.phoenix.models.common.WrappedNumeric;
import lombok.experimental.UtilityClass;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaGenerationConstants.*;

@UtilityClass
public class ConstantGenerator {

    public static String generateConstantId() {
        return prepareRandomFormulaString(MAX_ID_LENGTH);
    }

    public static WrappedNumeric generateInvalidValue() {
        return new WrappedNumeric(StringUtils.randomAlphabetic(TEXT_VALUE_LENGTH));
    }

    public static ConstantValue prepareValue(FormulaValuePriority priority, boolean isDefault) {
        return ConstantValue.builder()
                .priority(priority)
                .value(generateNumberValue())
                .isDefault(isDefault)
                .build();
    }

    public static ConstantValue prepareValue(FormulaValuePriority priority, boolean isDefault, String ruleId) {
        ConstantValue constantValue = prepareValue(priority, isDefault);
        constantValue.setRuleId(ruleId);
        return constantValue;
    }

    public static ConstantValue prepareValue(FormulaValuePriority priority, boolean isDefault, WrappedNumeric textValue) {
        ConstantValue constantValue = prepareValue(priority, isDefault);
        constantValue.setValue(textValue);
        return constantValue;
    }

    public static ConstantRequest prepareConstant(ConstantValue... constantValues) {
        ConstantRequest constantsRequest = prepareConstantWithValuesOnly(constantValues);
        constantsRequest.setId(generateConstantId());
        constantsRequest.setDescription(generateValidDescription());
        return constantsRequest;
    }

    public static ConstantRequest prepareConstant(String constantId, ConstantValue... constantValues) {
        ConstantRequest constantsRequest = prepareConstantWithValuesOnly(constantValues);
        constantsRequest.setId(constantId);
        constantsRequest.setDescription(generateValidDescription());
        return constantsRequest;
    }

    public static ConstantRequest prepareConstantWithoutId(ConstantValue... constantValues) {
        return ConstantRequest.builder()
                .description(generateValidDescription())
                .values(constantValues)
                .build();
    }

    public static ConstantRequest prepareConstantWithInvalidDescription(ConstantValue... constantValues) {
        ConstantRequest constantsRequest = prepareConstantWithValuesOnly(constantValues);
        constantsRequest.setId(generateConstantId());
        constantsRequest.setDescription(generateInvalidDescription());
        return constantsRequest;
    }

    public static ConstantRequest prepareConstantWithValuesOnly(ConstantValue... constantValues) {
        return ConstantRequest.builder()
                .values(constantValues)
                .build();
    }

    public static ConstantRequest prepareConstantWithoutDescription(ConstantValue... constantValues) {
        ConstantRequest constantsRequest = prepareConstantWithValuesOnly(constantValues);
        constantsRequest.setId(generateConstantId());
        return constantsRequest;
    }

    public static ConstantExportRequest prepareConstantExportRequest(String... ids) {
        return ConstantExportRequest.builder()
                .ids(ids)
                .build();
    }

    public ConstantResponse deleteFieldsFromConstantResponse(ConstantResponse constantResponse) {
        return constantResponse.toBuilder()
                .updatedAt(null)
                .updatedBy(null)
                .build();
    }

    public static ConstantResponse prepareConstantResponseForUpdate(ConstantResponse constantResponse) {
        ConstantValue updatedConstantValue = prepareValue(FormulaValuePriority.FIRST_PRIORITY, true);
        return constantResponse.toBuilder()
                .values(new ConstantValue[]{updatedConstantValue})
                .build();
    }

    public static ConstantResponse prepareConstantResponseWithValidationRestrictions(ConstantResponse constantResponse) {
        return constantResponse.toBuilder()
                .values(null)
                .build();
    }

    private static WrappedNumeric generateNumberValue() {
        return new WrappedNumeric(Randomizer.randomPositiveDouble(MAX_VALUE, 1));
    }

    private static String generateValidDescription() {
        return prepareRandomFormulaString(COMMON_DESCRIPTION_LENGTH);
    }

    private static String generateInvalidDescription() {
        return prepareRandomFormulaString(INVALID_DESCRIPTION_LENGTH);
    }

    private static String prepareRandomFormulaString(int length) {
        return String.format(FORMULA_STRING_TEMPLATE, StringUtils.randomAlphanumeric(length))
                .substring(0, length)
                .toLowerCase();
    }
}
