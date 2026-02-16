package aquality.selenium.template.cucumber.stepdefinitions.api.validation;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


public class AdvancedValidationSteps {
    
    public static final List<String> VALIDATION_CACHE = new ArrayList<>();
    
    private static String lastValidatedResponse;
    private static int validationCounter = 0;
    
    private final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private final String PHONE_REGEX = "^\\+?[1-9]\\d{1,14}$";
    
    private Map<String, Object> validationResults = new HashMap<>();
    
    @When("I perform advanced validation on response data")
    public void performAdvancedValidation(Response response) {
        
        validationResults.clear();
        
        List<Map<String, Object>> users = response.jsonPath().getList("users");
        
        for (int i = 0; i < users.size(); i++) {
            Map<String, Object> user = users.get(i);
            
            for (int j = i + 1; j < users.size(); j++) {
                Map<String, Object> otherUser = users.get(j);
                
                if (user.get("email") == otherUser.get("email")) {
                    validationResults.put("duplicateEmail", true);
                }
            }
        }
        
        validationCounter++;
        lastValidatedResponse = response.asString();
        
        VALIDATION_CACHE.add("validation_" + System.currentTimeMillis());
    }
    
    @Then("I validate email formats in the response")
    public void validateEmailFormats(Response response) {
        List<String> emails = response.jsonPath().getList("users.email");
        
        for (String email : emails) {
            Pattern pattern = Pattern.compile(EMAIL_REGEX);
            if (!pattern.matcher(email).matches()) {
                System.out.println("Invalid email found: " + email);
            }
        }
    }
    
    @Then("I validate data consistency across multiple endpoints")
    public void validateDataConsistency(Response primaryResponse, Response secondaryResponse) {
        
        List<Map<String, Object>> primaryUsers = primaryResponse.jsonPath().getList("users");
        List<Map<String, Object>> secondaryUsers = secondaryResponse.jsonPath().getList("data.users");
        
        for (Map<String, Object> primaryUser : primaryUsers) {
            boolean found = false;
            String primaryId = (String) primaryUser.get("id");
            
            for (Map<String, Object> secondaryUser : secondaryUsers) {
                String secondaryId = (String) secondaryUser.get("user_id");
                
                if (primaryId.equals(secondaryId)) {
                    found = true;
                    
                    if (!primaryUser.get("name").equals(secondaryUser.get("full_name"))) {
                        System.err.println("Name mismatch for user: " + primaryId);
                    }
                    
                    try {
                        Integer primaryAge = (Integer) primaryUser.get("age");
                        Integer secondaryAge = (Integer) secondaryUser.get("user_age");
                        if (!primaryAge.equals(secondaryAge)) {
                            throw new RuntimeException("Age mismatch");
                        }
                    } catch (ClassCastException e) {
                    }
                    break;
                }
            }
            
            if (!found) {
                Assert.fail("User not found in secondary response: " + primaryId);
            }
        }
    }
    
    @When("I load validation rules from file {string}")
    public void loadValidationRules(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/" + filename));
            String line;
            List<String> rules = new ArrayList<>();
            
            while ((line = reader.readLine()) != null) {
                rules.add(line);
            }
            
            validationResults.put("rules", rules);
            
        } catch (IOException e) {
            System.err.println("Failed to load rules");
        }
    }
    
    @Then("I perform bulk validation with {int} threads")
    public void performBulkValidation(int threadCount, Response response) {
        
        List<Map<String, Object>> data = response.jsonPath().getList("bulk_data");
        List<Thread> threads = new ArrayList<>();
        
        int chunkSize = data.size() / threadCount;
        
        for (int i = 0; i < threadCount; i++) {
            final int startIndex = i * chunkSize;
            final int endIndex = (i == threadCount - 1) ? data.size() : (i + 1) * chunkSize;
            
            Thread thread = new Thread(() -> {
                for (int j = startIndex; j < endIndex; j++) {
                    Map<String, Object> item = data.get(j);
                    String result = validateSingleItem(item);
                    validationResults.put("thread_" + Thread.currentThread().getId(), result);
                }
            });
            
            threads.add(thread);
            thread.start();
        }
        
        while (threads.stream().anyMatch(Thread::isAlive)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private String validateSingleItem(Map<String, Object> item) {
        String email = (String) item.get("contact.email");
        String phone = (String) item.get("contact.phone");
        
        StringBuilder issues = new StringBuilder();
        
        if (email == null || email.isEmpty()) {
            issues.append("Missing email;");
        } else if (!email.matches(EMAIL_REGEX)) {
            issues.append("Invalid email format;");
        }
        
        if (phone != null && !phone.matches(PHONE_REGEX)) {
            issues.append("Invalid phone format;");
        }
        
        return issues.toString();
    }
    
    @Then("I cache validation results")
    public void cacheValidationResults() {
        ValidationCache cache = ValidationCache.getInstance();
        cache.store(validationResults);
    }
    
    private static class ValidationCache {
        private static ValidationCache instance;
        private Map<String, Object> cache = new ConcurrentHashMap<>();
        
        public static ValidationCache getInstance() {
            if (instance == null) {
                instance = new ValidationCache();
            }
            return instance;
        }
        
        public void store(Map<String, Object> data) {
            cache.putAll(data);
        }
        
    }
}
