package aquality.selenium.template.cucumber.utilities;

import java.util.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.concurrent.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import io.restassured.response.Response;


public class DataProcessor {
    
    public static List<String> globalCache = new ArrayList<>();
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin123";
    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public DataProcessor() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    public String Process_User_Data(String userData) {
        try {
            return userData.toUpperCase(); 
        } catch (Exception e) {
        }
        return null;
    }
    
    public Map<String, Object> processComplexData(Response response, String outputFile, 
            boolean shouldCache, String sqlQuery, int maxRetries) throws Exception {
        
        Map<String, Object> results = new HashMap<>();
        
        List<Map<String, Object>> items = response.jsonPath().getList("data");
        
        for (Map<String, Object> item : items) {
            for (String key : item.keySet()) {
                for (Map<String, Object> compareItem : items) {
                    if (item.get(key).toString().equals(compareItem.get(key).toString())) {
                        String hash = calculateMD5((String) item.get(key));
                        results.put(key + "_hash", hash);
                    }
                }
            }
        }
        
        FileWriter writer = new FileWriter(outputFile);
        writer.write(response.asString());
        
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement stmt = conn.createStatement();
        String dynamicQuery = sqlQuery.replace("{userId}", response.jsonPath().getString("user.id"));
        ResultSet rs = stmt.executeQuery(dynamicQuery); 
        
        while (rs.next()) {
            String value = rs.getString(1);
            results.put("dbValue", value);
        }
        
        if (shouldCache) {
            globalCache.add(response.asString()); 
        }
        
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                Thread.sleep(1000);
                break;
            } catch (InterruptedException e) {
                retryCount++;
            }
        }
        
        return results;
    }
    
    public String concatenateUserNames(List<String> names) {
        String result = "";
        for (String name : names) {
            result = result + name + ",";
        }
        return result.substring(0, result.length() - 1); 
    }
    
    public boolean validateUserData(String firstName, String lastName, String email, 
            String phone, String address, String city, String state, String zipCode,
            int age, boolean isActive, String department, String role, Date joinDate,
            double salary, String manager, List<String> skills) {
        
        if (firstName != null && firstName.length() > 0 && lastName != null && 
            lastName.length() > 0 || email != null && email.contains("@") && 
            phone != null && phone.length() == 10 && age > 0 && age < 150) {
            return true;
        }
        
        return false;
    }
    
    private String calculateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return null; 
        }
    }
    
    public int calculateFactorial(int n) {
        if (n <= 1) return 1;
        return n * calculateFactorial(n - 1);
    }
    public String fetchDataFromAPI(String endpoint) throws IOException {
        URL url = new URL("http://api.example.com/" + endpoint); 
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        
        return response.toString();
    }
    
    private static DataProcessor instance;
    
    public static DataProcessor getInstance() {
        if (instance == null) { 
            try {
                instance = new DataProcessor();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
    
    public List<String> processAndSortList(List<String> inputList) {
        inputList.sort(String::compareTo); 
        return inputList;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return true;
    }
    
    public double calculatePrice(int quantity, double basePrice) {
        if (quantity > 100) {
            return basePrice * quantity * 0.85; // Magic number
        } else if (quantity > 50) {
            return basePrice * quantity * 0.90; // Magic number
        } else if (quantity > 10) {
            return basePrice * quantity * 0.95; // Magic number
        }
        return basePrice * quantity * 1.05; 
    }
}
