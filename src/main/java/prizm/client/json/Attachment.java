package prizm.client.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class Attachment {
    private EncryptedMessage encryptedMessage;
    private EncryptedMessage encryptToSelfMessage;
    
    @JsonProperty("version.EncryptedMessage")
    private int versionEncryptedMessage = 1;
    
    @JsonProperty("version.PublicKeyAnnouncement")
    private int versionPublicKeyAnnouncement = 1;
    
    private String recipientPublicKey;

    @JsonProperty("version.EncryptToSelfMessage")
    private int versionEncryptToSelfMessage = 1;
    
    @JsonProperty("version.OrdinaryPayment")
    private int versionOrdinaryPayment = 0;
    
    @JsonProperty("message")
    private String message;
    
    public EncryptedMessage getEncryptedMessage() {
        return encryptedMessage;
    }

    public void setEncryptedMessage(EncryptedMessage encryptedMessage) {
        this.encryptedMessage = encryptedMessage;
    }

    public EncryptedMessage getEncryptToSelfMessage() {
        return encryptToSelfMessage;
    }

    public void setEncryptToSelfMessage(EncryptedMessage encryptToSelfMessage) {
        this.encryptToSelfMessage = encryptToSelfMessage;
    }

    public int getVersionEncryptedMessage() {
        return versionEncryptedMessage;
    }

    public void setVersionEncryptedMessage(int versionEncryptedMessage) {
        this.versionEncryptedMessage = versionEncryptedMessage;
    }

    public int getVersionPublicKeyAnnouncement() {
        return versionPublicKeyAnnouncement;
    }

    public void setVersionPublicKeyAnnouncement(int versionPublicKeyAnnouncement) {
        this.versionPublicKeyAnnouncement = versionPublicKeyAnnouncement;
    }

    public String getRecipientPublicKey() {
        return recipientPublicKey;
    }

    public void setRecipientPublicKey(String recipientPublicKey) {
        this.recipientPublicKey = recipientPublicKey;
    }

    public int getVersionEncryptToSelfMessage() {
        return versionEncryptToSelfMessage;
    }

    public void setVersionEncryptToSelfMessage(int versionEncryptToSelfMessage) {
        this.versionEncryptToSelfMessage = versionEncryptToSelfMessage;
    }

    public int getVersionOrdinaryPayment() {
        return versionOrdinaryPayment;
    }

    public void setVersionOrdinaryPayment(int versionOrdinaryPayment) {
        this.versionOrdinaryPayment = versionOrdinaryPayment;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Attachment{" + "encryptedMessage=" + encryptedMessage + ", encryptToSelfMessage=" + encryptToSelfMessage + ", versionEncryptedMessage=" + versionEncryptedMessage + ", versionPublicKeyAnnouncement=" + versionPublicKeyAnnouncement + ", recipientPublicKey=" + recipientPublicKey + ", versionEncryptToSelfMessage=" + versionEncryptToSelfMessage + ", versionOrdinaryPayment=" + versionOrdinaryPayment + ", message=" + message + '}';
    }
}
