package prizm.server;

import prizm.shared.FieldVerifier;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.security.SecureRandom;
import net.nullschool.util.DigitalRandom;
import prizm.client.RandomService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class RandomServiceImpl extends RemoteServiceServlet implements
    RandomService {

    
    private static DigitalRandom DIGITAL_RANDOM = null;
    private final static SecureRandom SECURE_RANDOM = new SecureRandom();
    private static boolean isHardwareRandom = true;

    public Integer[] getNumbers(int amount) throws IllegalArgumentException {

        Integer arrayOfIntegers[] = new Integer[amount];
        synchronized (SECURE_RANDOM) {
            if (isHardwareRandom) {
                try {
                    if (DIGITAL_RANDOM == null) {
                        DIGITAL_RANDOM = new DigitalRandom();
                    }
                    for (int i = 0; i < amount; i++) {
                        arrayOfIntegers[i] = DIGITAL_RANDOM.nextInt();
                    }
                } catch (UnsupportedOperationException ex) {
                    DIGITAL_RANDOM = null;
                    isHardwareRandom = false;
                    System.out.println(" === Hardware Intel Random generator NOT EXISTS !!!");
                }
            }
            if (!isHardwareRandom) {
                for (int i = 0; i < amount; i++) {
                    arrayOfIntegers[i] = SECURE_RANDOM.nextInt();
                }
            }
        }
        return arrayOfIntegers;
    }

  /**
   * Escape an html string. Escaping data received from the client helps to
   * prevent cross-site script vulnerabilities.
   *
   * @param html the html string to escape
   * @return the escaped string
   */
  private String escapeHtml(String html) {
    if (html == null) {
      return null;
    }
    return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(
        ">", "&gt;");
  }
}
