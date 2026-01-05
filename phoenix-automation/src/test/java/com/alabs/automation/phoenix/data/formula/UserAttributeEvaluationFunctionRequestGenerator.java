package com.alabs.automation.phoenix.data.formula;

import com.alabs.automation.phoenix.models.formula.formula.UserAttributeEvaluationFunctionRequest;
import com.alabs.automation.phoenix.models.formula.formula.UserAttributeFunctionFormula;
import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public class UserAttributeEvaluationFunctionRequestGenerator {

    public static UserAttributeEvaluationFunctionRequest prepareUserAttributeEvaluationFunctionRequest(String gameAccountId,
                                                                                                       String attributeFunction,
                                                                                                       int userAttributeValue,
                                                                                                       String... formulaIds) {
        return UserAttributeEvaluationFunctionRequest
                .builder().gameAccountId(gameAccountId)
                .formulas(prepareUserAttributeEvaluationFunctionFormulas(attributeFunction, userAttributeValue, formulaIds))
                .build();
    }

    public static UserAttributeEvaluationFunctionRequest prepareUserAttributeEvaluationFunctionRequest(String gameAccountId,
                                                                                                       String... formulaIds) {
        return UserAttributeEvaluationFunctionRequest.builder()
                .gameAccountId(gameAccountId)
                .formulas(prepareUserAttributeEvaluationFunctionFormulas(formulaIds))
                .build();
    }

    private static UserAttributeFunctionFormula[] prepareUserAttributeEvaluationFunctionFormulas(String attributeFunction,
                                                                                                 int userAttributeValue,
                                                                                                 String... formulaIds) {
        return Arrays.stream(formulaIds).map(formulaId -> UserAttributeFunctionFormula.builder()
                .evaluationId(UUID.randomUUID().toString()).formulaId(formulaId)
                .additionalAttributes(prepareAdditionalAttributes(attributeFunction, userAttributeValue))
                .build()).toArray(UserAttributeFunctionFormula[]::new);
    }

    private static Map<String, Integer> prepareAdditionalAttributes(String attributeFunction, int userAttributeValue) {
        Map<String, Integer> attributeMap = new HashMap<>();
        attributeMap.put(attributeFunction, userAttributeValue);
        return attributeMap;
    }

    private static UserAttributeFunctionFormula[] prepareUserAttributeEvaluationFunctionFormulas(String... formulaIds) {
        return Arrays.stream(formulaIds)
                .map(formulaId -> UserAttributeFunctionFormula.builder()
                        .evaluationId(UUID.randomUUID().toString())
                        .snapshotId(UUID.randomUUID().toString())
                        .formulaId(formulaId)
                        .additionalAttributes(Collections.emptyMap())
                        .build()).toArray(UserAttributeFunctionFormula[]::new);
    }
}
