package prizm.client.service;

import com.googlecode.gwt.crypto.bouncycastle.digests.SHA1Digest;
import prizm.client.Prizm;
import prizm.client.json.EncryptedMessage;

import java.util.Random;

public class Calculate {
    public static native String getPublicKeyPrizm(String passphrass)
    /*-{
            return $wnd.getPublicKeyPrizm(passphrass);
    }-*/;
    
    public static native String sysGetAccountId(String publicKey)
    /*-{
            return $wnd.getAccountId(publicKey);
    }-*/;
    
    public static String getAccountID(String prizmPublicKey) {
        String retval = sysGetAccountId(prizmPublicKey);
        if (retval.contains("error")) return null;
        return retval;
    }
    
    public static native String sysGetAccountIdByRs(String RS)
    /*-{
            return $wnd.getIDByRSaddressPrizm(RS);
    }-*/;
    
    public static String getAccountIDByRS(String RS) {
        String retval = sysGetAccountIdByRs(RS);
        if (retval.contains("error")) return null;
        return retval;        
    }
    
    public static native String getRSaddressPrizm(String accountID)
    /*-{
            return $wnd.getRSaddressPrizm(accountID);
    }-*/;
    
    public static native String signBytes(String message, String passwd)
    /*-{
            return $wnd.signBytes(message, passwd);
    }-*/;

    public static native String getAccountToSend()
    /*-{
            return $wnd.getAccountToSend();
    }-*/;
    
    public static native void dropAccountToSend()
    /*-{
            $wnd.setAccountToSend(null);
    }-*/;

    public static native void startScanner()
    /*-{
        return $wnd.startScanner();
    }-*/;

    public static native String getScanResult()
    /*-{
        return $wnd.getScanResult();
    }-*/;

    public static native boolean isScanning()
    /*-{
        return $wnd.isScanning();
    }-*/;

    public static native boolean hasCamera()
    /*-{
        return $wnd.hasCamera();
    }-*/;

    public static native boolean hasFlashlight()
    /*-{
        return $wnd.hasFlashlight();
    }-*/;

    public static native boolean toggleFlashlight()
    /*-{
        return $wnd.toggleFlashlight();
    }-*/;

    public static native String stringToHexString(String string)
    /*-{
            return $wnd.converters.stringToHexString(string);
    }-*/;

    private static native String decMessage(String encrypted, String nonce, String otherAccount, String password)
    /*-{
            return $wnd.decMessage(encrypted, nonce, otherAccount, password);
    }-*/;

    private static Random random = new Random();
    public static String encrypt(String data, String password) {
        String SALT = Math.abs(random.nextLong())+"";
        String fullPW = SALT+password;
        String pk = Calculate.getPublicKeyPrizm(fullPW);
        String ID = Calculate.getAccountID(pk);
        EncryptedMessage out = Calculate.encryptMessage(data, ID, pk, fullPW);
        String RETVAL = SALT+":"+out.getNonce()+":"+out.getData();
        return RETVAL;
    }

    public static String decrypt(String cipherText, String password) {
        if (cipherText == null) return "ERROR (null)";
        String enc[] = cipherText.split(":", 3);
        String encrypter = enc[0]+password;
        String pk = Calculate.getPublicKeyPrizm(encrypter);
        String decrypted = Calculate.decryptMessage(enc[2], enc[1], pk, encrypter);
        return decrypted;
    }

    public static String getSHA1for(String text) {
        SHA1Digest sd = new SHA1Digest();
        byte[] bs = text.getBytes();
        sd.update(bs, 0, bs.length);
        byte[] result = new byte[20];
        sd.doFinal(result, 0);
        return byteArrayToHexString(result);
    }

    public static String byteArrayToHexString(final byte[] b) {
        final StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0, len = b.length; i < len; i++) {
            int v = b[i] & 0xff;
            if (v < 16) sb.append('0');
            sb.append(Integer.toHexString(v));
        }
        return sb.toString();
    }
    
    private static final String ENCRYPTED_MESSAGE = "encrypted message";
    
    public static String decryptMessage(String encrypted, String nonce, String publicKey, String password) {
        if (password == null || password.isEmpty()) return ENCRYPTED_MESSAGE;
        return decMessage(encrypted, nonce, publicKey, password);
    }

    public static native void print(String title, String elementId)
    /*-{
            return $wnd.printElement(title,elementId);
    }-*/;


    private static native String encMessage(String message, String account, String publicKey, String password)
    /*-{
            return $wnd.encMessage(message, account, publicKey, password);
    }-*/;
    
    public static EncryptedMessage encryptMessage(String message, String account, String publicKey, String password) {
        String unDivided = encMessage(message, account, publicKey, password);
        if (unDivided == null || unDivided.isEmpty() || unDivided.split(":").length != 2) return null;
        EncryptedMessage encryptedMessage = new EncryptedMessage();
        encryptedMessage.setIsText(true);
        encryptedMessage.setIsCompressed(true);
        encryptedMessage.setData(unDivided.split(":")[0]);
        encryptedMessage.setNonce(unDivided.split(":")[1]);
        return encryptedMessage;
    }
    
    public static native void qrCodemaster(String id, String message)
    /*-{
            $wnd.qrCodemaster(id, message);
    }-*/;
    
    public static boolean isValidReedSolomon(String prizmAddress) {
        if (prizmAddress.matches("^PRIZM-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{5}$")) {
            if (!Calculate.getRSaddressPrizm(prizmAddress).equals(prizmAddress)) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean isReedSolomon(String prizmAddress) {
        if (prizmAddress.matches("^PRIZM-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{5}$")) {
            return true;
        } else {
            return false;
        }
    }
    
    public static void recalculateAuthCredentials() {
        if (Prizm.getAccount() == null) return;
        if (Prizm.getAccount().getPassPhrase() != null && !Prizm.getAccount().getPassPhrase().isEmpty()) {
            Prizm.getAccount().setPublicKey(Calculate.getPublicKeyPrizm(Prizm.getAccount().getPassPhrase()));
            Prizm.getAccount().setID(Calculate.getAccountID(Prizm.getAccount().getPublicKey()));
            Prizm.getAccount().setReedSolomon(Calculate.getRSaddressPrizm(Prizm.getAccount().getID()));
        } else {
            Prizm.getAccount().setPublicKey(null);
            Prizm.getAccount().setPassPhrase(null);
        }
    }
}
