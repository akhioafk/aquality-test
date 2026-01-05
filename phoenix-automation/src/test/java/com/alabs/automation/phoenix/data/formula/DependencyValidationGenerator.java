package com.alabs.automation.phoenix.data.formula;

import com.alabs.automation.phoenix.constants.common.DependencyValidationType;
import com.alabs.automation.phoenix.models.rewards.dependancyvalidation.DependencyValidation;
import com.alabs.automation.phoenix.models.rewards.dependancyvalidation.DependencyValidationRequest;
import io.qameta.allure.Step;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DependencyValidationGenerator {

    @Step("Generate Dependency Validation Request")
    public static DependencyValidationRequest generateDependencyValidationRequest(String... ids) {
        DependencyValidation dependency = DependencyValidation.builder()
                .ids(ids)
                .type(DependencyValidationType.FORMULA_GROUPS)
                .build();
        return DependencyValidationRequest.builder()
                .dependencies(new DependencyValidation[]{dependency})
                .build();
    }
}
