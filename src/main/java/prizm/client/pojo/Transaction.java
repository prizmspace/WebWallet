package prizm.client.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.github.nmorel.gwtjackson.client.exception.JsonDeserializationException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import prizm.client.Prizm;
import prizm.client.json.Message;
import prizm.client.json.Trx;
import prizm.client.service.Calculate;
import prizm.client.service.Epoch;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Transaction {
    
    public static enum Direction {
        SEND,
        RECEIVE;
    }

    public static interface MessageMapper extends ObjectMapper<Message> {};
    
    private String ID = null;
    private Date date;
    private String opponent = null;
    private double amount = 0;
    private double fee = 0;
    private String comment = null;
    private long paratax = 0;
    private Direction direction = Direction.RECEIVE;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public long getParatax() {
        return paratax;
    }

    public void setParatax(long paratax) {
        this.paratax = paratax;
    }
    
    public static List<Transaction> parse(List<Trx> transactions) {
        ArrayList<Transaction> retval = new ArrayList<>();
        for (Trx item : transactions) {
            retval.add(parse(item));
        }
        return retval;
    }
    
    public static Transaction parse(Trx trx) {
        Transaction transaction = new Transaction();
        transaction.setID(trx.getTransaction());
        transaction.setAmount(trx.getAmountNQT()/100d);
        transaction.setFee(trx.getFeeNQT()/100d);
        transaction.setDate(Epoch.getDate(trx.getTimestamp()));
        
        try {
            if (trx.getAttachment() != null && trx.getAttachment().getMessage() != null && trx.getAttachment().getMessage().length() > 10) {
                MessageMapper mapper = GWT.create(MessageMapper.class);
                Message message = mapper.read(trx.getAttachment().getMessage());
                if (message.getParaTax() > 0) transaction.setParatax(message.getParaTax());
            }
        } catch (JsonDeserializationException ex) {}
        if (trx.getSenderRS().equals(Prizm.getAccount().getReedSolomon())) {
            transaction.setDirection(Direction.SEND);
        }
        transaction.setOpponent(transaction.getDirection() == Direction.RECEIVE?trx.getSenderRS():trx.getRecipientRS());
        if (trx.getAttachment() != null) {
                switch (transaction.getDirection()) {
                    case RECEIVE:
                        if (trx.getAttachment().getEncryptedMessage() != null) {
                            transaction.setComment(Calculate.decryptMessage(trx.getAttachment().getEncryptedMessage().getData(), trx.getAttachment().getEncryptedMessage().getNonce(), trx.getSenderPublicKey(), Prizm.getPassword()));
                        }
                        break;
                    case SEND:
                        if (trx.getAttachment().getEncryptToSelfMessage() != null) {
                            transaction.setComment(Calculate.decryptMessage(trx.getAttachment().getEncryptToSelfMessage().getData(), trx.getAttachment().getEncryptToSelfMessage().getNonce(), Prizm.getAccount().getPublicKey(), Prizm.getPassword()));
                        }
                        break;
                }
        }
        return transaction;
    }
}
