package com.alabs.automation.phoenix.tests.formulaservice.formula;

import com.alabs.automation.phoenix.data.formula.DependencyValidationGenerator;
import com.alabs.automation.phoenix.data.formula.FormulaGenerator;
import com.alabs.automation.framework.steps.api.ResponseSteps;
import com.alabs.automation.phoenix.constants.rewardsservice.JsonSchemas;
import com.alabs.automation.phoenix.data.common.InvalidAuthHeadersDataProvider;
import com.alabs.automation.phoenix.data.common.InvalidAuthRequestSpecification;
import com.alabs.automation.phoenix.models.formula.formula.FormulaResponse;
import com.alabs.automation.phoenix.models.rewards.dependancyvalidation.DependencyResults;
import com.alabs.automation.phoenix.models.rewards.dependancyvalidation.DependencyValidationRequest;
import com.alabs.automation.phoenix.models.rewards.dependancyvalidation.DependencyValidationResponse;
import com.alabs.automation.phoenix.steps.formulaservice.formula.*;
import com.alabs.automation.phoenix.tests.formulaservice.BaseFormulasTest;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaValueOption.*;

@TmsLink("11130929")
public class DependencyValidationTest extends BaseFormulasTest {
    private final DependencyValidationSteps dependencyValidationSteps = new DependencyValidationSteps();
    private final DeleteFormulaSteps deleteFormulaSteps = new DeleteFormulaSteps();
    private String[] activeFormulaGroupIds, archivedFormulaGroupIds, nonExistentFormulaGroupIds;

    @BeforeClass(groups = {"suite.smoke"})
    public void createFormulaGroups() {
        FormulaResponse firstFormulaResponse = FormulaPreconditionSteps.createFormula(FIRST_PRIOR_DEFAULT_TRUE.getExpression());
        FormulaResponse secondFormulaResponse = FormulaPreconditionSteps.createFormula(SECOND_PRIOR_DEFAULT_TRUE.getExpression());
        FormulaResponse firstFormulaResponseToArchive = FormulaPreconditionSteps.createFormula(FIRST_PRIOR_DEFAULT_TRUE.getExpression());
        FormulaResponse secondFormulaResponseToArchive = FormulaPreconditionSteps.createFormula(SECOND_PRIOR_DEFAULT_TRUE.getExpression());
        activeFormulaGroupIds = new String[]{firstFormulaResponse.getId(), secondFormulaResponse.getId()};
        archivedFormulaGroupIds = new String[]{firstFormulaResponseToArchive.getId(), secondFormulaResponseToArchive.getId()};
        deleteFormulaSteps.deleteFormula(archivedFormulaGroupIds);
        nonExistentFormulaGroupIds = new String[]{FormulaGenerator.generateFormulaId(), FormulaGenerator.generateFormulaId()};
    }

    @Test(description = "Dependency validation for id of an active formula group", priority = Integer.MAX_VALUE, groups = {"suite.smoke"})
    public void dependencyValidationTest() {
        DependencyValidationRequest request = DependencyValidationGenerator.generateDependencyValidationRequest(activeFormulaGroupIds[0]);
        Response response = dependencyValidationSteps.dependencyValidation(request);
        checkStatusAndJsonSchema(response);
        DependencyValidationResponse dependencyValidationResponse = response.as(DependencyValidationResponse.class);
        dependencyValidationSteps.checkGroupIdPresent(dependencyValidationResponse, DependencyResults::getValidDependencies, activeFormulaGroupIds[0]);
    }

    @Test(description = "Send POST /internal/v1/dependency-validation/validate with different formula group IDs", dataProvider = "dependencyValidationData")
    public void dependencyValidationForDifferentFormulaGroupTest(String name, String[] formulaGroupIds, Function<DependencyResults, String[]> getDependencies) {
        DependencyValidationRequest request = DependencyValidationGenerator.generateDependencyValidationRequest(formulaGroupIds);
        Response response = dependencyValidationSteps.dependencyValidation(request);
        checkStatusAndJsonSchema(response);
        DependencyValidationResponse dependencyValidationResponse = response.as(DependencyValidationResponse.class);
        dependencyValidationSteps.checkGroupIdPresent(dependencyValidationResponse, getDependencies, formulaGroupIds);
    }

    @Test(description = "Send POST /internal/v1/dependency-validation/validate with several active, archive and non-existent formula group IDs")
    public void dependencyValidationWithMixedFormulaIds() {
        String[] mixedFormulaIds = Stream.of(activeFormulaGroupIds, archivedFormulaGroupIds, nonExistentFormulaGroupIds)
                .flatMap(Arrays::stream).toArray(String[]::new);
        DependencyValidationRequest request = DependencyValidationGenerator.generateDependencyValidationRequest(mixedFormulaIds);
        Response response = dependencyValidationSteps.dependencyValidation(request);
        checkStatusAndJsonSchema(response);
        DependencyValidationResponse dependencyValidationResponse = response.as(DependencyValidationResponse.class);
        dependencyValidationSteps.checkGroupIdPresent(dependencyValidationResponse, DependencyResults::getValidDependencies, mixedFormulaIds[0], mixedFormulaIds[1]);
        dependencyValidationSteps.checkGroupIdPresent(dependencyValidationResponse, DependencyResults::getArchivedDependencies, mixedFormulaIds[2], mixedFormulaIds[3]);
        dependencyValidationSteps.checkGroupIdPresent(dependencyValidationResponse, DependencyResults::getMissingDependencies, mixedFormulaIds[4], mixedFormulaIds[5]);
    }

    @Test(description = "Send POST /internal/v1/dependency-validation/validate with non-valid Auth token",
                            dataProviderClass = InvalidAuthHeadersDataProvider.class, dataProvider = "invalidAuthRequestSpecifications")
    public void dependencyValidationWithInvalidAuthToken(InvalidAuthRequestSpecification invalidAuthRequestSpecification) {
        DependencyValidationRequest request = DependencyValidationGenerator.generateDependencyValidationRequest(activeFormulaGroupIds);
        Response response = dependencyValidationSteps.dependencyValidationWithInvalidAuthToken(invalidAuthRequestSpecification, request);
        ResponseSteps.checkStatusCode(response, HttpStatus.SC_UNAUTHORIZED);
    }

    @Test(description = "Send POST /internal/v1/dependency-validation/validate with empty formula group IDs")
    public void dependencyValidationWithEmptyId() {
        DependencyValidationRequest request = DependencyValidationGenerator.generateDependencyValidationRequest();
        Response response = dependencyValidationSteps.dependencyValidation(request);
        ResponseSteps.checkStatusCode(response, HttpStatus.SC_BAD_REQUEST);
    }

    @DataProvider
    private Object[][] dependencyValidationData() {
        return new Object[][]{
                {"Dependency validation for active formula group IDs",
                        activeFormulaGroupIds,
                        (Function<DependencyResults, String[]>) DependencyResults::getValidDependencies},
                {"Dependency validation for archive formula group IDs",
                        archivedFormulaGroupIds,
                        (Function<DependencyResults, String[]>) DependencyResults::getArchivedDependencies},
                {"Dependency validation for non-existent formula group IDs",
                        nonExistentFormulaGroupIds,
                        (Function<DependencyResults, String[]>) DependencyResults::getMissingDependencies}
        };
    }

    @Step("Check status code and json schema")
    private void checkStatusAndJsonSchema(Response response) {
        ResponseSteps.checkStatusCode(response, HttpStatus.SC_OK);
        ResponseSteps.checkJsonSchema(response, JsonSchemas.DEPENDENCY_VALIDATION);
    }
}
