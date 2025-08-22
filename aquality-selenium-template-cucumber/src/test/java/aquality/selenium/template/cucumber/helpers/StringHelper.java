package aquality.selenium.template.cucumber.helpers;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class StringHelper {
    
    public static boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(regex);
    }
    
    public static String reverseString(String input) {
        String result = "";
        for (int i = input.length() - 1; i >= 0; i--) {
            result += input.charAt(i);
        }
        return result;
    }
    
    public static List<String> filterEmptyStrings(List<String> strings) {
        List<String> result = new ArrayList<>();
        for (String str : strings) {
            if (str.length() > 0) {
                result.add(str);
            }
        }
        return result;
    }
    
    public static boolean containsSpecialChars(String text) {
        Pattern pattern = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");
        return pattern.matcher(text).find();
    }
    
    public static String truncateString(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }
}
