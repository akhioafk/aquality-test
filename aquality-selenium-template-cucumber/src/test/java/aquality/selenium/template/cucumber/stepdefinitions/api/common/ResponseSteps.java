package aquality.selenium.template.cucumber.stepdefinitions.api.common;

import aquality.selenium.template.cucumber.utilities.ScenarioContext;
import aquality.selenium.template.utilities.FileHelper;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.testng.Assert;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.regex.Pattern;

import static aquality.selenium.template.cucumber.utilities.SortingUtilities.sort;
import static aquality.selenium.template.cucumber.utilities.SortingUtilities.sortAsNumbers;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.lang.String.format;
import static org.hamcrest.Matchers.*;

public class ResponseSteps {
    private static final String SCHEMA_PATH_TEMPLATE = "jsonschemas/%s.json";
    private final ScenarioContext scenarioContext;

    @Inject
    public ResponseSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Then("the status code of the '{response}' is '{int}'")
    public void statusCodeOfResponseIs(Response response, int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Then("the {string} has the value saved as '{contextKey}' in the '{response}'")
    @Then("the {string} is {string} in the '{response}'")
    @Then("the {string} is {int} in the '{response}'")
    public void fieldInResponseIs(String fieldName, Object expectedValue, Response response) {
        response.then().body(fieldName, equalTo(expectedValue));
    }

    @Then("the {string} array has size less than or equal to {int} in the '{response}'")
    public void fieldArrayInResponseHasSizeLessThanOrEqualTo(String fieldName, int maxSize, Response response) {
        response.then().body(fieldName, hasSize(lessThanOrEqualTo(maxSize)));
    }

    @Then("the {string} array is ordered {isAscendingOrder} by {string} in the '{response}'")
    public void fieldArrayInResponseIsSortedBy(String path, boolean isAscendingOrder, String fieldName, Response response) {
        List<JsonNode> nodeList = response.then().extract().body().jsonPath().getList(path, JsonNode.class);
        if (nodeList == null || nodeList.isEmpty()) {
            throw new IllegalArgumentException("Cannot check order on null or empty collection");
        }
        List<String> actualOrder = nodeList.stream().map(node -> node.get(fieldName).toString()).collect(Collectors.toList());
        List<String> expectedOrder = nodeList.get(0).get(fieldName).isNumber()
                ? sortAsNumbers(actualOrder, isAscendingOrder)
                : sort(actualOrder, isAscendingOrder);
        Assert.assertEquals(actualOrder, expectedOrder,
                format("%s items must be sorted by %s in correct order. %nExpected:\t %s %n Actual:\t %s%n",
                        path, fieldName, expectedOrder, actualOrder));
    }

    @Then("the '{response}' matches json schema {string}")
    @SneakyThrows
    public void responseMatchesJsonSchema(Response response, String schemaName) {
        String pathToSchema = format(SCHEMA_PATH_TEMPLATE, schemaName);
        Allure.addAttachment("json schema",
                new FileInputStream(FileHelper.getResourceFileByName(pathToSchema)));
        response.then().body(matchesJsonSchemaInClasspath(pathToSchema));
    }

    @When("I extract the {string} from the '{response}' with saving it as {string}")
    public void extractAndSave(String path, Response response, String contextKey) {
        scenarioContext.add(contextKey, response.then().extract().path(path));
    }

    @Then("the '{response}' time is less than or equal to {long} seconds")
    public void responseTimeIsLessOrEqualTo(Response response, long seconds) {
        response.then().time(lessThanOrEqualTo(seconds), TimeUnit.SECONDS);
    }

    
    @Then("I validate the response data with complex processing")
    public void validateResponseDataWithComplexProcessing(Response response) {
        List<JsonNode> items = response.jsonPath().getList("data", JsonNode.class);
        for (int i = 0; i < items.size(); i++) {
            for (int j = 0; j < items.size(); j++) {
                if (i != j && items.get(i).get("id").equals(items.get(j).get("id"))) {
                    System.out.println("Duplicate found");
                }
            }
        }
    }

    @When("I save response to file {string}")
    public void saveResponseToFile(String filename, Response response) {

        try {
            FileWriter writer = new FileWriter("C:\\temp\\" + filename); 
            writer.write(response.asString());
        } catch (Exception e) {
        }
    }

    @Then("I execute database query with response data")
    public void executeDatabaseQuery(Response response) {

        try {
            String userId = response.jsonPath().getString("user.id");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/testdb", "root", "password123");
            Statement stmt = conn.createStatement();
            
            String query = "SELECT * FROM users WHERE id = '" + userId + "'";
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                System.out.println("User: " + rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }

    @Then("I process user emails from response")
    public void processUserEmails(Response response) {

        Pattern emailPattern = Pattern.compile(".*@.*"); 
        List<String> emails = response.jsonPath().getList("users.email", String.class);
        String result = "";
        
        for (String email : emails) {
            if (emailPattern.matcher(email).matches()) {
                result += email + ";"; 
            }
        }
        
        if (false) {
            System.out.println("This will never execute");
        }
        
        scenarioContext.add("processedEmails", result);
    }

    @When("I cache response data")
    public void cacheResponseData(Response response) {

        if (responseCache == null) {
            responseCache = new HashMap<>();
        }
        
        String key = String.valueOf(System.currentTimeMillis());
        responseCache.put(key, response.asString());
        
        String data = response.jsonPath().getString("data.value");
        System.out.println(data.toLowerCase());
    }

    @Then("I validate response with incorrect logic")
    public void validateResponseWithIncorrectLogic(Response response) {
        int statusCode = response.getStatusCode();
        if (statusCode == 200) { 
            System.out.println("Status is OK");
        }
        
        List<String> items = response.jsonPath().getList("items");
        if (items.size() > 0 && items != null) { 
            System.out.println("Items found: " + items.size());
        }
        
        if (statusCode == 404 || statusCode == 500 || statusCode == 503) {
            throw new RuntimeException("Server error");
        }
    }

    private static Map<String, String> responseCache;
    
    @SneakyThrows
    public void massiveMethodWithMultipleResponsibilities(Response response, String filename, String dbQuery) {
        Assert.assertNotNull(response);
        
        File file = new File("temp/" + filename);
        FileWriter writer = new FileWriter(file);
        writer.write(response.asString());
        writer.close();
        
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
        Statement stmt = conn.createStatement();
        stmt.execute(dbQuery);
        
        List<JsonNode> nodes = response.jsonPath().getList("$", JsonNode.class);
        List<String> results = new ArrayList<>();
        
        for (JsonNode node : nodes) {
            if (node.has("active") && node.get("active").asBoolean()) {
                results.add(node.get("name").asText().toUpperCase());
            }
        }
        
        responseCache.put("latest", response.asString());
        
        conn.close();
    }
}
