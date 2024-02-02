package prizm.client.service;

import java.util.Date;

public class Epoch {
    
    public static final String GENESIS = "PRIZM-TE8N-B3VM-JJQH-5NYJB";
    
    private static final long EPOCH_BEGINNING = 1532715480000L;
    
    public static int current() {
        return (int)(new Date().getTime()-EPOCH_BEGINNING)/1000;
    }
    
    public static Date getDate(int stamp) {
        return new Date((stamp*1000L)+EPOCH_BEGINNING);
    }
    
    public static long currentGlide() {
        return (new Date().getTime()-EPOCH_BEGINNING);
    }
}
