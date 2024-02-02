package prizm.client.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AccountSub {
    private long unconfirmedBalanceNQT = 0;
    private long balanceNQT = 0;
    private long forgedBalanceNQT = 0;
    private String accountRS = null;
    private String publicKey = null;
    private String account = null;
    private int requestProcessingTime = 0;

    public long getUnconfirmedBalanceNQT() {
        if (unconfirmedBalanceNQT < 0L)
            return balanceNQT;
        return unconfirmedBalanceNQT;
    }

    public void setUnconfirmedBalanceNQT(long unconfirmedBalanceNQT) {
        this.unconfirmedBalanceNQT = unconfirmedBalanceNQT;
    }

    public long getBalanceNQT() {
        return balanceNQT;
    }

    public void setBalanceNQT(long balanceNQT) {
        this.balanceNQT = balanceNQT;
    }

    public long getForgedBalanceNQT() {
        return forgedBalanceNQT;
    }

    public void setForgedBalanceNQT(long forgedBalanceNQT) {
        this.forgedBalanceNQT = forgedBalanceNQT;
    }

    public String getAccountRS() {
        return accountRS;
    }

    public void setAccountRS(String accountRS) {
        this.accountRS = accountRS;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getRequestProcessingTime() {
        return requestProcessingTime;
    }

    public void setRequestProcessingTime(int requestProcessingTime) {
        this.requestProcessingTime = requestProcessingTime;
    }

    @Override
    public String toString() {
        return "AccountSub{" + "unconfirmedBalanceNQT=" + unconfirmedBalanceNQT + ", balanceNQT=" + balanceNQT + ", forgedBalanceNQT=" + forgedBalanceNQT + ", accountRS=" + accountRS + ", publicKey=" + publicKey + ", account=" + account + ", requestProcessingTime=" + requestProcessingTime + '}';
    }
}
