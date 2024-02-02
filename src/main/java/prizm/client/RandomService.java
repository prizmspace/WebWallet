package prizm.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("random")
public interface RandomService extends RemoteService {
  Integer[] getNumbers(int amount) throws IllegalArgumentException;
}
