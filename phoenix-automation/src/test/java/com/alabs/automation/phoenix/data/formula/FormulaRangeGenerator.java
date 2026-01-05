package com.alabs.automation.phoenix.data.formula;

import com.alabs.automation.framework.utilities.Randomizer;
import com.alabs.automation.framework.utilities.StringUtils;
import com.alabs.automation.phoenix.configuration.PhoenixAutotestConfiguration;
import com.alabs.automation.phoenix.constants.formulaservice.FormulaValuePriority;
import com.alabs.automation.phoenix.models.common.WrappedNumeric;
import com.alabs.automation.phoenix.models.formula.range.FormulaRangeExportRequest;
import com.alabs.automation.phoenix.models.serviceproperties.formula.Attribute;
import com.alabs.automation.phoenix.models.formula.range.FormulaRangeLevel;
import com.alabs.automation.phoenix.models.formula.range.FormulaRangeRequest;
import com.alabs.automation.phoenix.models.formula.range.FormulaRangeValue;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.stream.IntStream;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaGenerationConstants.*;

@UtilityClass
public class FormulaRangeGenerator {
    private static final int DEFAULT_MAX_VALUE = 300;
    private static final int DEFAULT_MIN_VALUE = 100;
    private static final int RANDOM_VALUE_UPPER_BOUND = 10;
    private static final int DEFAULT_VALUE_ARRAY_LENGTH = 2;
    private static final String EXCLUDED_ATTRIBUTE_NAME = "attribute_excluded_from_snapshots";

    public static String generateRangeId() {
        return prepareRandomFormulaString(MAX_ID_LENGTH);
    }

    public static WrappedNumeric generateInvalidFromValue() {
        return new WrappedNumeric(StringUtils.randomAlphabetic(TEXT_VALUE_LENGTH));
    }

    public static FormulaRangeLevel prepareLevel(FormulaValuePriority priority, boolean isDefault) {
        return FormulaRangeLevel.builder()
                .defaultOutput(Randomizer.randomPositiveInt(DEFAULT_MAX_VALUE))
                .priority(priority)
                .isDefault(isDefault)
                .values(prepareValues())
                .build();
    }

    public static FormulaRangeLevel prepareLevel(FormulaValuePriority priority, boolean isDefault, String ruleId) {
        FormulaRangeLevel rangeLevel = prepareLevel(priority, isDefault);
        rangeLevel.setRuleId(ruleId);
        return rangeLevel;
    }

    public static FormulaRangeLevel prepareLevel(FormulaValuePriority priority, boolean isDefault, WrappedNumeric textValue) {
        FormulaRangeLevel rangeLevel = prepareLevel(priority, isDefault);
        rangeLevel.setValues(prepareRangeWithInvalidValue(textValue));
        return rangeLevel;
    }

    public static FormulaRangeRequest prepareRange(FormulaRangeLevel... rangeLevels) {
        return prepareRangeWithoutDescription(rangeLevels)
                .toBuilder()
                .description(generateValidDescription())
                .build();
    }

    public static FormulaRangeRequest prepareRange(String rangeId, FormulaRangeLevel... rangeLevels) {
        return prepareRange(rangeLevels)
                .toBuilder()
                .id(rangeId)
                .build();
    }

    public static FormulaRangeRequest prepareRangeWithoutDescription(FormulaRangeLevel... rangeLevels) {
        return prepareRangeWithLevelsOnly(rangeLevels)
                .toBuilder()
                .id(generateRangeId())
                .build();
    }

    public static FormulaRangeRequest prepareRangeWithLevelsOnly(FormulaRangeLevel... rangeLevels) {
        return FormulaRangeRequest.builder()
                .rangeLevels(rangeLevels)
                .userAttribute(getRandomAttributeId())
                .build();
    }

    public static FormulaRangeRequest prepareRangeWithoutId(FormulaRangeLevel... rangeLevels) {
        return prepareRangeWithLevelsOnly(rangeLevels)
                .toBuilder()
                .description(generateValidDescription())
                .build();
    }

    public static FormulaRangeRequest prepareRangeWithoutIdAndWithInvalidUserAttribute(FormulaRangeLevel... rangeLevels) {
        return prepareRangeWithoutId(rangeLevels)
                .toBuilder()
                .userAttribute(prepareRandomFormulaString(COMMON_DESCRIPTION_LENGTH))
                .build();
    }

    public static FormulaRangeRequest prepareRangeWithInvalidUserAttribute(FormulaRangeLevel... rangeLevels) {
        return prepareRangeWithoutIdAndWithInvalidUserAttribute(rangeLevels)
                .toBuilder()
                .id(generateRangeId())
                .build();
    }

    public static FormulaRangeRequest prepareRangeWithInvalidDescription(FormulaRangeLevel... rangeLevels) {
        return prepareRangeWithoutDescription(rangeLevels)
                .toBuilder()
                .description(generateInvalidDescription())
                .build();
    }

    public static Attribute[] getConfigAttributes() {
        return PhoenixAutotestConfiguration.formula().getFormulaServiceProperties()
                .getUserAttributes().getAttributes();
    }

    public static Attribute[] getNotExcludedConfigAttributes() {
        return Arrays.stream(getConfigAttributes())
                .filter(configAttribute -> !configAttribute.getId().contains(EXCLUDED_ATTRIBUTE_NAME))
                .toArray(Attribute[]::new);
    }

    public static String getRandomAttributeId() {
        return Randomizer.randomItem(Arrays.stream(getNotExcludedConfigAttributes()).map(Attribute::getId)
                .toArray(String[]::new));
    }

    public static FormulaRangeExportRequest prepareRangeExportRequest(String... ids) {
        return FormulaRangeExportRequest.builder()
                .ids(ids)
                .build();
    }

    private static WrappedNumeric generateNumberValue() {
        return new WrappedNumeric(Randomizer.randomPositiveInt(DEFAULT_MAX_VALUE));
    }

    private static String generateValidDescription() {
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

    private static FormulaRangeValue[] prepareRangeWithInvalidValue(WrappedNumeric textValue) {
        FormulaRangeValue[] rangeValues = prepareValues();
        rangeValues[0] = rangeValues[0].toBuilder().from(textValue).build();
        return rangeValues;
    }

    private static FormulaRangeValue[] prepareValues() {
        WrappedNumeric[] rangeArray = prepareArrayOfFromValuesInAscending(DEFAULT_VALUE_ARRAY_LENGTH);
        return Arrays.stream(rangeArray).map(wrappedNumeric -> FormulaRangeValue.builder()
                        .from(wrappedNumeric)
                        .output(generateNumberValue())
                        .build())
                .toArray(FormulaRangeValue[]::new);
    }

    private static WrappedNumeric[] prepareArrayOfFromValuesInAscending(int length) {
        int start = DEFAULT_MIN_VALUE + Randomizer.randomPositiveInt(RANDOM_VALUE_UPPER_BOUND);
        return IntStream.iterate(start, prev -> prev + DEFAULT_MIN_VALUE + Randomizer
                        .randomPositiveInt(RANDOM_VALUE_UPPER_BOUND))
                .limit(length)
                .mapToObj(WrappedNumeric::new)
                .toArray(WrappedNumeric[]::new);
    }
}
