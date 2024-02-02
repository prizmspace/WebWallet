package prizm.client.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SendMoneyResponse {
    private int requestProcessingTime;
    private String fullHash;
    private String transaction;

    public int getRequestProcessingTime() {
        return requestProcessingTime;
    }

    public void setRequestProcessingTime(int requestProcessingTime) {
        this.requestProcessingTime = requestProcessingTime;
    }

    public String getFullHash() {
        return fullHash;
    }

    public void setFullHash(String fullHash) {
        this.fullHash = fullHash;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }
}
