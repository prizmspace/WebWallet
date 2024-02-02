package prizm.server;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class Format {

    private static NumberFormat numberFormat = null;
    private static NumberFormat uniNumberFormat = null;

    private static NumberFormat coinNumberFormat = null;
    private static NumberFormat coinUniNumberFormat = null;
    
    private static DateFormat dateFormat = null;
    private static DateFormat dateTimeFormat = null;
    private static DateFormat dateFormatShort = null;
    
    static {
        numberFormat = new DecimalFormat();
        numberFormat.setGroupingUsed(true);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        Currency currency = Currency.getInstance("USD");
        numberFormat.setCurrency(currency);

        coinNumberFormat = new DecimalFormat();
        coinNumberFormat.setGroupingUsed(true);
        coinNumberFormat.setMaximumFractionDigits(6);
        coinNumberFormat.setMinimumFractionDigits(6);
        coinNumberFormat.setCurrency(currency);
        
        uniNumberFormat = new DecimalFormat();
        uniNumberFormat.setGroupingUsed(false);
        uniNumberFormat.setMaximumFractionDigits(2);
        uniNumberFormat.setMinimumFractionDigits(2);

        coinUniNumberFormat = new DecimalFormat();
        coinUniNumberFormat.setGroupingUsed(false);
        coinUniNumberFormat.setMaximumFractionDigits(6);
        coinUniNumberFormat.setMinimumFractionDigits(6);
        
        dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm (Z)");
        dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        dateFormatShort = new SimpleDateFormat("dd.MM.yyyy");
    }
    
    public static synchronized String getNumber(Double input) {
        return numberFormat.format(input);
    }
    
    public static synchronized String getUniNumber(Double input) {
        return uniNumberFormat.format(input).replaceAll(",", ".");
    }
    
    public static synchronized String getCoinNumber(Double input) {
        return coinNumberFormat.format(input);
    }
    
    public static synchronized String getCoinUniNumber(Double input) {
        return coinUniNumberFormat.format(input).replaceAll(",", ".");
    }
    
    public static String getAPIDateFormat(Date date) {
        DateTime dateTime = new DateTime(DateTimeZone.UTC);
        DateTime retval = dateTime.withMillis(date.getTime());
        return retval.toString("MM/dd/yyyy kk:mm");
    }
    
    public static synchronized String getNumber(String input) {
        Double process = 0.00;
        try {
            process = Double.parseDouble(input);
        } catch (NumberFormatException ex) {}
        return getNumber(process);
    }
    
    public static synchronized String getCoinNumber(String input) {
        Double process = 0.00;
        try {
            process = Double.parseDouble(input);
        } catch (NumberFormatException ex) {}
        return getCoinNumber(process);
    }
    
    public static synchronized String getDate(Date date) {
        return dateFormat.format(date);
    }
    
    public static String matchString(String value) {
        if (value.matches("^.*$")) return value;
        return null;
    }

    public static synchronized String getDateShort(Date date) {
        return dateFormatShort.format(date);
    }

    public static synchronized String getDateTime(Date date) {
        return dateTimeFormat.format(date);
    }
    
    public static boolean decimalIsValid(Double decimal) {
        try {
            String decimalS = getUniNumber(decimal);
            Double newDecimal = Double.parseDouble(decimalS);
            return newDecimal.equals(decimal);
        } catch (NumberFormatException ex) {
            return false;
        }
    }
    
    public static boolean decimalCoinIsValid(Double decimal) {
        try {
            String decimalS = getCoinUniNumber(decimal);
            Double newDecimal = Double.parseDouble(decimalS);
            return newDecimal.equals(decimal);
        } catch (NumberFormatException ex) {
            return false;
        }
    }
    
    public static boolean containsInteger(String value) {
        if (value == null) return false;
        try { 
            Integer i = Integer.parseInt(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static Date getDate(String month, String day, String year) throws Exception {
        int monthI;
        int dayI;
        int yearI;
        Date retval = null;

        if ((month==null)||(day==null)||(year==null)) return null;
        
        try {
            monthI = Integer.parseInt(month);
            dayI = Integer.parseInt(day);
            yearI = Integer.parseInt(year);
        } catch (NumberFormatException ex) {
            throw new Exception("Number format error");
        }
        try {
            DateTime dateTime = new DateTime(DateTimeZone.UTC);
            DateTime retDate = dateTime.withDate(yearI, monthI, dayI);
            retval = retDate.toDate();
        } catch (IllegalArgumentException ex) {
            throw new Exception("Invalid date parameters");
        }
        return retval;
    }
}
