package com.alabs.automation.phoenix.data.formula;

import com.alabs.automation.framework.utilities.Randomizer;
import com.alabs.automation.framework.utilities.StringUtils;
import com.alabs.automation.phoenix.models.formula.nestedformula.ExpressionDto;
import com.alabs.automation.phoenix.models.formula.nestedformula.NestedFormulaExportRequest;
import com.alabs.automation.phoenix.models.formula.nestedformula.NestedFormulaRequest;
import com.alabs.automation.phoenix.models.formula.nestedformula.NestedFormulaRequestWithoutDescription;
import lombok.experimental.UtilityClass;

import java.util.UUID;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaGenerationConstants.*;

@UtilityClass
public class NestedFormulaGenerator {

    public static String generateNestedFormulaId() {
        return prepareRandomNestedFormulaString(MAX_ID_LENGTH);
    }

    public static NestedFormulaRequest generateRandomNestedFormula() {
        return NestedFormulaRequest.builder()
                .id(generateNestedFormulaId())
                .description(generateDescription())
                .expressionDto(ExpressionDto.builder()
                        .expression(generateExpression())
                        .build())
                .build();
    }

    public static NestedFormulaRequest generateNestedFormulaRequestForUpdate() {
        return NestedFormulaRequest.builder()
                .description(generateDescription())
                .expressionDto(ExpressionDto.builder()
                        .expression(generateExpression())
                        .build())
                .build();
    }

    public static NestedFormulaRequestWithoutDescription generateNestedFormulaRequestWithoutDescription() {
        return NestedFormulaRequestWithoutDescription.builder()
                .id(generateNestedFormulaId())
                .expressionDto(ExpressionDto.builder()
                        .expression(generateExpression())
                        .build())
                .build();
    }

    public static NestedFormulaRequestWithoutDescription generateNestedFormulaRequestForUpdateWithoutDescription() {
        return NestedFormulaRequestWithoutDescription.builder()
                .expressionDto(ExpressionDto.builder()
                        .expression(generateExpression())
                        .build())
                .build();
    }

    public static NestedFormulaExportRequest generateNestedFormulaExportRequest(String... ids) {
        return NestedFormulaExportRequest.builder()
                .ids(ids)
                .build();
    }

    public static String generateDescription() {
        return String.format(AUTO_DESCRIPTION_TEMPLATE, UUID.randomUUID());
    }

    public static String generateExpression() {
        return FormulaType.ADD_ONE_HUNDRED.getValue(String.format("%s", Randomizer.randomPositiveInt(MAX_VALUE)));
    }

    private static String prepareRandomNestedFormulaString(int length) {
        return String.format(AUTO_NESTED_FORMULA_STRING_TEMPLATE, StringUtils.randomAlphanumeric(length))
                .substring(0, length)
                .toLowerCase();
    }
}
