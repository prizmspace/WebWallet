package prizm.client.service;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import java.util.Date;

public class DataUtils {
    private static DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");
    private static DateTimeFormat timeFormat = DateTimeFormat.getFormat("HH:mm");
    private static DateTimeFormat complexFormat = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");
    private static NumberFormat numberFormat = NumberFormat.getFormat("#######0.00");
    private static NumberFormat numberFormat1 = NumberFormat.getFormat("#######0.000");
    private static NumberFormat numberFormat2 = NumberFormat.getFormat("#######0.0000");
    private static NumberFormat numberFormat3 = NumberFormat.getFormat("#######0.00000");
    private static NumberFormat numberFormat4 = NumberFormat.getFormat("#######0.000000");
    private static NumberFormat numberFormat5 = NumberFormat.getFormat("#######0.0000000");
    private static NumberFormat numberFormat6 = NumberFormat.getFormat("#######0.00000000");

    public static String formatOnlyDate(Date date) {
        return dateFormat.format(date);
    }
    public static String formatOnlyTime(Date date) {
        return timeFormat.format(date);
    }
    public static String formatFull(Date date) {
        return complexFormat.format(date);
    }
    public static String formatNumber(Number number) {
        return numberFormat.format(number);
    }
    public static String formatNumberSmart(Number number) {
        if (number.doubleValue() < 1) {
            return numberFormat6.format(number);
        }
        if (number.doubleValue() < 10) {
            return numberFormat5.format(number);
        }
        if (number.doubleValue() < 100) {
            return numberFormat4.format(number);
        }
        if (number.doubleValue() < 1000) {
            return numberFormat3.format(number);
        }
        if (number.doubleValue() < 10000) {
            return numberFormat2.format(number);
        }
        if (number.doubleValue() < 100000) {
            return numberFormat1.format(number);
        }
        return numberFormat.format(number);
    }
    public static boolean atSameDay(Date date1, Date date2) {
        return formatOnlyDate(date1).equals(formatOnlyDate(date2));
    }
    
    public static double fix(double in) {
        return Math.round(in * 100d) / 100d;
    }
    
    public static double fixWithCents(double in) {
        return Math.round((in/100d) * 100d) / 100d;
    }
    
    public static String minimize(String value) {
        if (value == null) return "";
        if (value.length()<=20) return value;
        return value.substring(0, 20)+"...";
    }

    public static long getFixedFee(long amount) {
        return (long) (amount * 0.005 <= 5 ? 5 : (amount * 0.005 >= 1000 ? 1000 : amount * 0.005));
    }
    
    public static double getAmountWithoutFee(double amount) {
        if (amount <= 0d) return 0d;
        double  calculated = Math.floor((amount-(amount * 100d / 100.5d))*100)/100d;
        return calculated < 0.05d ? amount-0.05d : (calculated > 10.0d ? amount - 10.0d : amount - calculated);
    }
    
    public static boolean weHaveNeeededAmount(long amount, long weeHave) {
        long fee = getFixedFee(amount);
        return amount + fee <= weeHave;
    }

    public static String clean(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.replaceAll("\\n", "").replaceAll("\\r", "").trim();
    }
}
