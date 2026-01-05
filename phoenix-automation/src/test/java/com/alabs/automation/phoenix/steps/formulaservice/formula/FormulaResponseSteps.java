package com.alabs.automation.phoenix.steps.formulaservice.formula;

import com.alabs.automation.phoenix.models.formula.formula.FormulaRequest;
import com.alabs.automation.phoenix.models.formula.formula.FormulaResponse;
import io.qameta.allure.Step;
import lombok.experimental.UtilityClass;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.util.Date;

@UtilityClass
public class FormulaResponseSteps {

    public static void checkFormulaResponseCorrespondToRequestAfterCreate(FormulaResponse response, FormulaRequest request) {
        checkFormulaResponseCorrespondToRequest(response, request);
        Assert.assertEquals(response.getId(), request.getId(), "Id should equals to expected");
    }

    public static void checkFormulaResponseCorrespondToRequestAfterUpdate(FormulaResponse response, FormulaRequest request) {
        checkFormulaResponseCorrespondToRequest(response, request);
    }

    @Step("Check that 'updated_at' parameter has changed")
    public static void checkUpdatedAtParameterChanged(FormulaResponse response, Date oldValue) {
        Assert.assertNotEquals(response.getUpdatedAt(), oldValue, "'updated_at' parameter should not be equal to the old one");
    }

    @Step("Check that formula response correspond to formula request")
    private static void checkFormulaResponseCorrespondToRequest(FormulaResponse response, FormulaRequest request) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getDescription(), request.getDescription(), "Description should equals to expected");
        softAssert.assertEquals(response.getExpressions(), request.getExpressions(), "Expression should equals to expected");
        softAssert.assertAll();
    }

    @Step("Check that formula responses are equal")
    public static void checkFormulaResponsesAreEqual(FormulaResponse actualResponse, FormulaResponse expectedResponse) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(actualResponse.getId(), expectedResponse.getId(), "id");
        softAssert.assertEquals(actualResponse.getDescription(), expectedResponse.getDescription(), "description");
        softAssert.assertEquals(actualResponse.getExpressions(), expectedResponse.getExpressions(), "expressions");
        softAssert.assertAll();
    }

    @Step("Check that formula response is archived")
    public static void checkFormulaResponseIsArchived(FormulaResponse formula) {
        Assert.assertTrue(formula.getArchived(), "Formula should be archived");
    }
}
