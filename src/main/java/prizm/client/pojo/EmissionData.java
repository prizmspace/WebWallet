package prizm.client.pojo;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.io.Serializable;

public class EmissionData implements IsSerializable, Serializable {
    private long supplyPQT;
    private long availableSupplyPQT;

    public long getSupplyPQT() {
        return supplyPQT;
    }

    public void setSupplyPQT(long supplyPQT) {
        this.supplyPQT = supplyPQT;
    }

    public long getAvailableSupplyPQT() {
        return availableSupplyPQT;
    }

    public void setAvailableSupplyPQT(long availableSupplyPQT) {
        this.availableSupplyPQT = availableSupplyPQT;
    }
}
