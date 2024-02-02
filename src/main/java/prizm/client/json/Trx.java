package prizm.client.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Trx {
    private String transactionIndex;
    private String sender;
    private String senderRS;
    private String senderPublicKey;
    private String recipient;
    private String recipientRS;
    private String block;
    private int confirmations;
    private long amountNQT;
    private int timestamp;
    private long feeNQT;
    
    
    private String transaction;
    private Attachment attachment;

    public String getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(String transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getSenderRS() {
        return senderRS;
    }

    public void setSenderRS(String senderRS) {
        this.senderRS = senderRS;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public String getSenderPublicKey() {
        return senderPublicKey;
    }

    public void setSenderPublicKey(String senderPublicKey) {
        this.senderPublicKey = senderPublicKey;
    }

    public String getRecipientRS() {
        return recipientRS;
    }

    public void setRecipientRS(String recipientRS) {
        this.recipientRS = recipientRS;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public int getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(int confirmations) {
        this.confirmations = confirmations;
    }

    public long getAmountNQT() {
        return amountNQT;
    }

    public void setAmountNQT(long amountNQT) {
        this.amountNQT = amountNQT;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public long getFeeNQT() {
        return feeNQT;
    }

    public void setFeeNQT(long feeNQT) {
        this.feeNQT = feeNQT;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "Trx{" + "transactionIndex=" + transactionIndex + ", sender=" + sender + ", senderRS=" + senderRS + ", senderPublicKey=" + senderPublicKey + ", recipient=" + recipient + ", recipientRS=" + recipientRS + ", block=" + block + ", confirmations=" + confirmations + ", amountNQT=" + amountNQT + ", timestamp=" + timestamp + ", feeNQT=" + feeNQT + ", transaction=" + transaction + ", attachment=" + attachment + '}';
    }
}
