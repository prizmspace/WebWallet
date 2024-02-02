package prizm.client.service;

public class Validators {
    public static String correctRequestVID(String input) throws Exception {
        String value = input.trim().replaceAll("[ -]{1,}", "").toUpperCase();
        if (value.length() != 9) throw new Exception("Invalid RequestID format");
        return value.substring(0, 3)+"-"+value.substring(3, 6)+"-"+value.substring(6, 9);
    }
    
    public static String correctKeyCode(String input) {
        return input.trim().replaceAll("[ -]{2,}", " ").toLowerCase();
    }
    
    public static String correctAccountVID(String input) throws Exception {
        String value = input.trim().replaceAll("[ -]{1,}", "").toUpperCase();
        if (value.length() != 12) throw new Exception("Invalid RequestID format");
        return value.substring(0, 4)+"-"+value.substring(4, 8)+"-"+value.substring(8, 12);
    }    

    public static boolean validateIPV4(String IP) {
        if (IP == null || IP.isEmpty()) return false;
        return IP.matches("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    }    
}
