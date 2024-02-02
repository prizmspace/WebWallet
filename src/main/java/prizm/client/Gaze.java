package prizm.client;

import com.googlecode.gwt.crypto.bouncycastle.util.encoders.Base64;
import com.googlecode.gwt.crypto.util.SecureRandom;
import prizm.client.service.Calculate;
import prizm.client.service.PasswordGen;

import java.util.Random;

public class Gaze {

    public static boolean isAuthCode(String qrCodeContent) {
        return qrCodeContent.startsWith("prizmgaze://");
    }

    final String encrypted;
    final long id;

    public Gaze(String qrCodeContent) {
        final String data = new String(Base64.decode(qrCodeContent.substring(12).getBytes()));
        id = Long.parseLong(data.substring(0, data.indexOf("h")));
        encrypted = data.substring(data.indexOf("h") + 1).replaceAll("h", ":");
    }
    
    private static String[] numbers(String pin) {
        String[] numbers = new String[pin.length()];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = pin.charAt(i) + "";
        }
        return numbers;
    }

    public boolean testPin(String pin) {
        try {
            String phrase = decryptPassphrase(pin);
            return !phrase.equals("error");
        } catch (Throwable t) {
            return false;
        }
    }

    public String decryptPassphrase(String pin) {
        return Calculate.decrypt(encrypted, calculatePassword(id, numbers(pin)));
    }

    
    private static final Random random = new SecureRandom();

    private static String calculatePassword(long id, String...numbers) {
        final String[] lib = PasswordGen.getElements();
        final int[] pin = pin(numbers);
        long shift = id % lib.length;
        String[] words = new String[4];
        for (int i = 0; i < 4; i++) {
            long index = shift + pin[i];
            index = index % lib.length;
            words[i] = lib[(int)index];
        }
        String cryptoPhrase = "";
        for (int i = 0; i < 4; i++) {
            cryptoPhrase = cryptoPhrase + words[i] + pin[i];
        }
        return Calculate.getSHA1for(id + cryptoPhrase);
    }
    
    public static String generateQrContent(String passphrase, String pin) {
        final String[] numbers = numbers(pin); 
        long id = Math.abs(random.nextLong());
        String password = calculatePassword(id, numbers);
        String qr = id + "h" + Calculate.encrypt(passphrase, password).replaceAll(":", "h");
	qr = "prizmgaze://" + new String(Base64.encode(qr.getBytes()));
        return qr;
    }

    private static int[] pin(String...numbers) {
        int[] pin = new int[4];
        for (int i = 0; i < 4; i++) {
            pin[i] = Integer.parseInt(numbers[i]);
        }
        return pin;
    }
}
