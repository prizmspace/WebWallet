package prizm.client.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SendMoney {
    private Trx transactionJSON;
    private String unsignedTransactionBytes;
    private boolean broadcasted;
    private int requestProcessingTime;

    public Trx getTransactionJSON() {
        return transactionJSON;
    }

    public void setTransactionJSON(Trx transactionJSON) {
        this.transactionJSON = transactionJSON;
    }

    public String getUnsignedTransactionBytes() {
        return unsignedTransactionBytes;
    }

    public void setUnsignedTransactionBytes(String unsignedTransactionBytes) {
        this.unsignedTransactionBytes = unsignedTransactionBytes;
    }

    public boolean isBroadcasted() {
        return broadcasted;
    }

    public void setBroadcasted(boolean broadcasted) {
        this.broadcasted = broadcasted;
    }

    public int getRequestProcessingTime() {
        return requestProcessingTime;
    }

    public void setRequestProcessingTime(int requestProcessingTime) {
        this.requestProcessingTime = requestProcessingTime;
    }
}
