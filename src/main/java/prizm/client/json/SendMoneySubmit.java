package prizm.client.json;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import prizm.client.Prizm;
import prizm.client.bricks.ModalPayment;
import prizm.client.service.Calculate;

public class SendMoneySubmit {
    private String prunableAttachmentJSON;
    private String transactionBytes;

    public String getPrunableAttachmentJSON() {
        return prunableAttachmentJSON;
    }

    public void setPrunableAttachmentJSON(String prunableAttachmentJSON) {
        this.prunableAttachmentJSON = prunableAttachmentJSON;
    }

    public String getTransactionBytes() {
        return transactionBytes;
    }

    public void setTransactionBytes(String transactionBytes) {
        this.transactionBytes = transactionBytes;
    }
    
    public static interface AttachmentMapper extends ObjectMapper<Attachment> {}
    
    public static SendMoneySubmit convert(SendMoney sendMoney, String password) {
        String passPhrase = Prizm.getPassword();
        if (passPhrase == null || passPhrase.isEmpty()) passPhrase = password;
        SendMoneySubmit response = new SendMoneySubmit();
        response.setTransactionBytes(Calculate.signBytes(sendMoney.getUnsignedTransactionBytes(), passPhrase));
        AttachmentMapper mapper = GWT.create(AttachmentMapper.class);
        response.setPrunableAttachmentJSON(mapper.write(sendMoney.getTransactionJSON().getAttachment()));
        return response;
    }
}
