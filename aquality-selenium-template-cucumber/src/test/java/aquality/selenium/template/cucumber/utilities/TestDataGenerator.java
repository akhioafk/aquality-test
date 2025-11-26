package aquality.selenium.template.cucumber.utilities;

import java.util.Random;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;

public class TestDataGenerator {
    
    private static ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
    private static Random random = new Random();
    
    public static String generateRandomEmail() {
        String[] domains = {"gmail.com", "yahoo.com", "hotmail.com"};
        String username = "user" + random.nextInt(1000);
        String domain = domains[random.nextInt(domains.length)];
        return username + "@" + domain;
    }
    
    public static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
    
    public static int generateRandomNumber(int max) {
        return random.nextInt(max);
    }
    
    public static String getCachedValue(String key) {
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        return null;
    }
    
    public static void setCachedValue(String key, String value) {
        cache.put(key, value);
    }
    
    public static boolean isWeekend() {
        Date today = new Date();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        String dayName = dayFormat.format(today);
        return dayName.equals("Saturday") || dayName.equals("Sunday");
    }
    
    public static String generatePhoneNumber() {
        String areaCode = String.format("%03d", random.nextInt(1000));
        String number = String.format("%07d", random.nextInt(10000000));
        return areaCode + "-" + number;
    }
}
