package prizm.client.pojo;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.io.Serializable;

public class Balance implements Serializable, IsSerializable {
    private long unconfirmedBalanceNQT;
    private long forgedBalanceNQT;
    private long balanceNQT;
    private long requestProcessingTime;

    public long getUnconfirmedBalanceNQT() {
        return unconfirmedBalanceNQT;
    }

    public void setUnconfirmedBalanceNQT(long unconfirmedBalanceNQT) {
        this.unconfirmedBalanceNQT = unconfirmedBalanceNQT;
    }

    public long getForgedBalanceNQT() {
        return forgedBalanceNQT;
    }

    public void setForgedBalanceNQT(long forgedBalanceNQT) {
        this.forgedBalanceNQT = forgedBalanceNQT;
    }

    public long getBalanceNQT() {
        return balanceNQT;
    }

    public void setBalanceNQT(long balanceNQT) {
        this.balanceNQT = balanceNQT;
    }

    public long getRequestProcessingTime() {
        return requestProcessingTime;
    }

    public void setRequestProcessingTime(long requestProcessingTime) {
        this.requestProcessingTime = requestProcessingTime;
    }
    
    public EmissionData getEmissionData() {
        EmissionData emissionData = new EmissionData();
        emissionData.setSupplyPQT(Math.abs(this.getBalanceNQT()));
        emissionData.setAvailableSupplyPQT(emissionData.getSupplyPQT());
        return emissionData;
    }
}
