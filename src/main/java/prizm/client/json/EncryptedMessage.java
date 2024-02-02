package prizm.client.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class EncryptedMessage {
    private String data;
    private String nonce;
    private boolean isText;
    private boolean isCompressed;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public boolean isIsText() {
        return isText;
    }

    public void setIsText(boolean isText) {
        this.isText = isText;
    }

    public boolean isIsCompressed() {
        return isCompressed;
    }

    public void setIsCompressed(boolean isCompressed) {
        this.isCompressed = isCompressed;
    }

    @Override
    public String toString() {
        return "EncryptedMessage{" + "data=" + data + ", nonce=" + nonce + ", isText=" + isText + ", isCompressed=" + isCompressed + '}';
    }
}
