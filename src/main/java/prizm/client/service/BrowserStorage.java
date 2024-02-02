package prizm.client.service;

import com.google.gwt.storage.client.Storage;
import java.util.Random;
import prizm.client.Prizm;
import prizm.client.json.EncryptedMessage;

public class BrowserStorage {
    
    private static final String PIN_CODE = "PINcode";
    private static final String PASSWORD = "Password";
    private static final String AUTHINFO = "authinfo";
    private static final String AUTH_ID = "authid";
    private static final String AUTH_PK = "authpk";
    public static final String DEFAULT_PIN = "0000";
    
    private static final Storage LOCAL_STORAGE = Storage.getLocalStorageIfSupported();
    private static String encryptedPassword = null;
    
    public static class AuthCredentials {
        boolean ok;
        String password;

        public boolean isOk() {
            return ok;
        }

        public void setOk(boolean ok) {
            this.ok = ok;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
    
    private static Random random = new Random();
    
    public static void convertIfExists() {
        writeGenesisPassword();
    }
    
    public static boolean hasAuthCredentials() {
        writeGenesisPassword();
        convertIfExists();
        String authInfo = encryptedPassword;
        if (authInfo == null || authInfo.isEmpty()) return false;
        return true;
    }
    
    public static void setPasswordEncoded(String password) {
        writeGenesisPassword();
        String SALT = Math.abs(random.nextLong())+"";
        String fullPW = SALT+DEFAULT_PIN;
        String pk = Calculate.getPublicKeyPrizm(fullPW);
        String ID = Calculate.getAccountID(pk);
        EncryptedMessage out = Calculate.encryptMessage("ok:"+password, ID, pk, fullPW);
        String RETVAL = SALT+":"+out.getNonce()+":"+out.getData();
        encryptedPassword = RETVAL;
    }
    
    private static AuthCredentials returnError() {
        AuthCredentials credentials = new AuthCredentials();
        credentials.setOk(false);
        credentials.setPassword("");
        return credentials;
    }
    
    public static AuthCredentials getPasswordEncoded(String PIN) {
        writeGenesisPassword();
        String encodedString = encryptedPassword;
        if (encodedString == null) return returnError();
        String enc[] = encodedString.split(":", 3);
        String encrypter = enc[0]+PIN;
        String pk = Calculate.getPublicKeyPrizm(encrypter);
        String password = Calculate.decryptMessage(enc[2], enc[1], pk, encrypter);
        if (password.matches("^ok.*$")) {
            String ac[] = password.split(":", 2);
            if (ac.length != 2) return returnError();
            
            AuthCredentials authCredentials = new AuthCredentials();
            authCredentials.setPassword(ac[1]);
            authCredentials.setOk(true);
            return authCredentials;
        }
        return returnError();
    }
    
    public static boolean checkPINEncoded(String PIN) {
        writeGenesisPassword();
        AuthCredentials authCredentials = getPasswordEncoded(PIN);
        return authCredentials.isOk();
    }
    
    public static void clearAll() {
        encryptedPassword = null;
        LOCAL_STORAGE.clear();
        writeGenesisPassword();
    }
    
    public static void writeGenesisPassword() {
        LOCAL_STORAGE.setItem(AUTHINFO, Prizm.GENESYS_ENCRYPTED_KEY);
    }
    
    public static void writeAuthCredentials(String ID) {
        LOCAL_STORAGE.clear();
        LOCAL_STORAGE.setItem(AUTH_ID, ID);
    }
    
    public static void writeAuthCredentials(String ID, String publicKey) {
        LOCAL_STORAGE.clear();
        LOCAL_STORAGE.setItem(AUTH_ID, ID);
        LOCAL_STORAGE.setItem(AUTH_PK, publicKey);
    }
    
    public static String readAuthCredentials() {
        return LOCAL_STORAGE.getItem(AUTH_ID);
    }

    public static String readAuthPublicKey() {
        return LOCAL_STORAGE.getItem(AUTH_PK);
    }
 }
