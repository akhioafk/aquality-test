package com.alabs.automation.phoenix.data.formula;

import com.alabs.automation.framework.utilities.DateTimeUtils;
import com.alabs.automation.framework.utilities.Randomizer;
import com.alabs.automation.phoenix.models.formula.attribute.*;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.IntStream;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaGenerationConstants.*;
import static java.util.Calendar.SECOND;

@UtilityClass
public class AttributeGenerator {

    public static AttributeRequest prepareAttributeRequest(String gameAccountId, String attributeName) {
        return prepareAttributeRequest(gameAccountId, attributeName, SHIFT_VALUE_FIFTEEN_SECONDS);
    }

    public static AttributeRequestWithDecimalValue prepareAttributeRequestWithDecimalValue(String gameAccountId, String attributeName) {
        Date expiresAt = DateTimeUtils.getShiftedDate(DateTimeUtils.getCurrentTime(), SECOND, SHIFT_VALUE_FIFTEEN_SECONDS);
        return prepareAttributeRequestWithDecimalValue(gameAccountId, prepareAttributeRequestAttributeWithDecimalValue(expiresAt, attributeName));
    }

    public static AttributeRequest prepareAttributeRequestWithAttributesOfDifferentExpiry(String gameAccountId,
                                                                                          String... attributeNames) {
        Date shortExpiresAt = DateTimeUtils.getShiftedDate(DateTimeUtils.getCurrentTime(), SECOND, SHIFT_VALUE_FIFTEEN_SECONDS);
        Date longExpiresAt = DateTimeUtils.getShiftedDate(shortExpiresAt, SECOND, SHIFT_VALUE_FIFTEEN_SECONDS);
        return prepareAttributeRequest(gameAccountId, prepareAttributeRequestAttributesWithDifferentExpiry(
                shortExpiresAt, longExpiresAt, attributeNames));
    }

    public static AttributeRequest prepareAttributeRequestAtEarlierTime(AttributeRequest attributeRequest,
                                                                        String attributeName) {
        Arrays.stream(attributeRequest.getAttributes())
                .filter(attribute -> attribute.getName().equals(attributeName))
                .findFirst()
                .ifPresent(attribute ->
                        attribute.setValue(Randomizer.randomInt(MAX_VALUE + 1, NEXT_MAX_VALUE)));

        return attributeRequest.toBuilder().utcTimestamp(DateTimeUtils.getShiftedDate(attributeRequest
                .getUtcTimestamp(), SECOND, -SHIFT_VALUE_FIFTEEN_SECONDS)).build();
    }

    public static AttributeRequest prepareAttributeRequestWithEarlierExpirationTime(String gameAccountId, String attributeName) {
        return prepareAttributeRequest(gameAccountId, attributeName, -SHIFT_VALUE_FIFTEEN_SECONDS);
    }

    public static AttributeRequest prepareAttributeRequestWithFixedValue(String gameAccountId, String attributeName, int value) {
        Date expiresAt = DateTimeUtils.getShiftedDate(DateTimeUtils.getCurrentTime(), SECOND, SHIFT_VALUE_FIFTEEN_SECONDS);
        return prepareAttributeRequest(gameAccountId, prepareFormulaUserAttribute(expiresAt, attributeName, value));
    }

    public static AttributeRequest prepareAttributeRequest(String gameAccountId, String attributeName, int durationSeconds) {
        Date expiresAt = DateTimeUtils.getShiftedDate(DateTimeUtils.getCurrentTime(), SECOND, durationSeconds);
        return prepareAttributeRequest(gameAccountId, prepareFormulaUserAttribute(expiresAt, attributeName));
    }

    public static AttributeRequest prepareAttributeRequest(String gameAccountId,
                                                           FormulaUserAttribute... formulaUserAttributes) {
        return AttributeRequest.builder()
                .attributes(formulaUserAttributes)
                .user(AttributeRequestUser.builder().gameAccountId(gameAccountId).build())
                .utcTimestamp(DateTimeUtils.getCurrentTime())
                .build();
    }

    public static FormulaUserAttribute prepareFormulaUserAttribute(Date expiresAt, String name, int value) {
        return FormulaUserAttribute.builder()
                .expiresAt(expiresAt)
                .name(name)
                .value(value)
                .build();
    }

    private static AttributeRequestWithDecimalValue prepareAttributeRequestWithDecimalValue(String gameAccountId,
                                                                                            AttributeRequestAttributeWithDecimalValue... attributeRequestAttributes) {
        return AttributeRequestWithDecimalValue.builder()
                .attributes(attributeRequestAttributes)
                .user(AttributeRequestUser.builder().gameAccountId(gameAccountId).build())
                .utcTimestamp(DateTimeUtils.getCurrentTime())
                .build();
    }

    private static AttributeRequestAttributeWithDecimalValue prepareAttributeRequestAttributeWithDecimalValue(Date expiresAt, String name) {
        return AttributeRequestAttributeWithDecimalValue.builder()
                .expiresAt(expiresAt)
                .name(name)
                .value(Randomizer.randomPositiveDouble(MAX_VALUE))
                .build();
    }

    private static FormulaUserAttribute[] prepareAttributeRequestAttributesWithDifferentExpiry(Date shortExpiresAt,
                                                                                               Date longExpiresAt,
                                                                                               String... attributeNames) {
        return IntStream.range(0, attributeNames.length)
                .mapToObj(attributeIndex -> {
                    String attributeName = attributeNames[attributeIndex];
                    Date expiresAtToSet = (attributeIndex < FIRST_TWO_ATTRIBUTES) ? shortExpiresAt : longExpiresAt;
                    return prepareFormulaUserAttribute(expiresAtToSet, attributeName);
                })
                .toArray(FormulaUserAttribute[]::new);
    }

    private static FormulaUserAttribute prepareFormulaUserAttribute(Date expiresAt, String name) {
        return FormulaUserAttribute.builder()
                .expiresAt(expiresAt)
                .name(name)
                .value(Randomizer.randomPositiveInt(ATTRIBUTE_MAX_VALUE))
                .build();
    }
}
