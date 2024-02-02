package prizm.client.bricks;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import java.util.Date;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.shared.event.ModalShownHandler;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.Well;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import prizm.client.Gaze;
import prizm.client.Prizm;
import prizm.client.json.AccountSub;
import prizm.client.json.SendMoney;
import prizm.client.json.SendMoneyResponse;
import prizm.client.json.SendMoneySubmit;
import prizm.client.pojo.Transaction;
import prizm.client.service.Calculate;
import prizm.client.service.DataUtils;
import prizm.client.service.Requests;

public class ModalPayment extends Modal {

    private Button loginBtn = new Button("Proceed");
    private Button cancelBtn = new Button("Cancel");
    private ShriForm form = new ShriForm(new ShriForm.ShriFormHander() {
        @Override
        public void onSubmit(ShriForm form) {
            validate(false);
        }
    });
    private ShriInput recipient = new ShriInput("Recipient:", "PRIZM-XXXX-XXXX-XXXX-XXXXX");
    private ShriInput recipientPublicKey = new ShriInput("Recipient Public Key:", "xxxxxxx.....");
    private ShriInput amount = new ShriInput("Amount:", "0.00");
    private ShriInput info = new ShriInput("Comment:", "Additional comment...");
    private ShriInput password = new ShriInput("Passphrase:", "Passphrase for your wallet...");
    private Well well = new Well();
    
    private String VID = null;
    private Double amountD = null;
    private String infoS = null;

    private void reinit() {
        if (VID != null) VID = null;
        if (amountD != null) amountD = null;
        if (infoS != null) infoS = null;
        
        amount.getTextBox().setReadOnly(false);
        amount.getTextBox().setValue("");
        info.getTextBox().setReadOnly(false);
        info.getTextBox().setValue("");
        recipient.getTextBox().setReadOnly(false);
        recipient.getTextBox().setValue("");
        recipientPublicKey.getTextBox().setReadOnly(false);
        recipientPublicKey.getTextBox().setValue("");
        password.getTextBox().setReadOnly(false);
        password.getTextBox().setValue("");
        
        amount.setVisible(true);
        info.setVisible(true);
        recipient.setVisible(true);
        recipientPublicKey.setVisible(true);
        password.setVisible(!Prizm.havePrivateKey());
    }
    
    public void activate(String RID) {
        recipient.getTextBox().setValue(RID);
        validate(false);
        show();
    }
    
    private Transaction transaction = null;
    
    private static final String NONE="none";
    
    private static String commentDePassInternal(String comment) {
        String PK = Calculate.getPublicKeyPrizm(comment);
        String ID = Calculate.getAccountID(PK);
        String RS = Calculate.getRSaddressPrizm(ID);
        if ((Prizm.getAccount().getReedSolomon().equals(RS)) 
                || (Prizm.getAccount().getID().equals(ID)) ) {
            return NONE;
        }
        return comment;
    }
    
    public static boolean commentCheckPass(String comment) {
        String depassed = commentDePassInternal(comment);
        return depassed.equalsIgnoreCase(comment);
    }
    
    public static String commentDePass(String comment) {
        if (commentCheckPass(comment)) 
            if (commentCheckPass(comment.trim()))
                if (commentCheckPass(comment.trim().toLowerCase()))
                    if (commentCheckPass(DataUtils.clean(comment)))
                        return comment;
        return NONE;
    }
    
    public void validate(boolean repeated) {
        if (sendMoneySubmit == null) {
            try {
                recipient.clearError();
                recipientPublicKey.clearError();
                info.clearError();
                amount.clearError();

                String addressRS = recipient.getValue().toUpperCase();
                if (!Calculate.getRSaddressPrizm(addressRS).equals(addressRS)) {
                    recipient.setError("Invalid address");
                    return;
                }

                recipientPublicKey.getTextBox().setReadOnly(true);
                String publicKey = recipientPublicKey.getValue();
                if (Calculate.getRSaddressPrizm(Calculate.getAccountID(publicKey)) != null) {
                    if (!Calculate.getRSaddressPrizm(Calculate.getAccountID(publicKey)).equals(addressRS)) {
                        Requests.getAccountSubData(addressRS, new Requests.GetAccountSubDataHandler() {
                            @Override
                            public void onSuccess(AccountSub accountData) {
                                if (accountData.getPublicKey() == null || accountData.getPublicKey().isEmpty()) {
                                    recipientPublicKey.getTextBox().setReadOnly(false);
                                    recipientPublicKey.setVisible(true);
                                } else {
                                    recipientPublicKey.getTextBox().setValue(accountData.getPublicKey());
                                    recipientPublicKey.setVisible(false);
                                    recipientPublicKey.clearError();
                                    recipientPublicKey.getTextBox().setReadOnly(false);
                                    if (!repeated) validate(true);
                                }
                            }
                        });
                        recipientPublicKey.setError("Invalid public key");
                        return;
                    }
                } else {
                    recipientPublicKey.setError("Invalid public key");
                    recipientPublicKey.getTextBox().setReadOnly(false);
                    return;
                }

                long amountL;
                double amountD;
                try {
                    amountD = Double.parseDouble(this.amount.getValue());
                    amountD = DataUtils.fix(amountD);
                    if (amountD <= 0d) {
                        amount.setError("Can't be zero");
                        return;
                    }
                    amountL = Math.round(amountD * 100d);
                    if (!DataUtils.weHaveNeeededAmount(amountL, Prizm.getRealBalance())) {
                        amount.getTextBox().setValue(DataUtils.formatNumber(DataUtils.getAmountWithoutFee((double)(Prizm.getRealBalance()/100d))));
                        amount.setError("Set maximum available amount!");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    amount.setError("Invalid amount");
                    return;
                }

                transaction = new Transaction();
                transaction.setAmount(amountD);
                transaction.setOpponent(addressRS);
                String infoComment = info.getValue();
                if (infoComment == null || infoComment.isEmpty()) infoComment = NONE;
                transaction.setComment(commentDePass(infoComment));
                transaction.setDate(new Date());
                transaction.setDirection(Transaction.Direction.SEND);
                transaction.setFee(DataUtils.fixWithCents(DataUtils.getFixedFee(Math.round(amountD * 100d))));
                transaction.setID(null);
                final String passwordValue = DataUtils.clean(password.getValue());
                if (Prizm.getPassword() == null) {
                    if (Prizm.getAccount() == null || Prizm.getAccount().getID() == null) {
                        password.setError("System error!");
                        return;
                    } else {
                        password.clearError();
                    }
                    if (passwordValue == null || passwordValue.isEmpty() || !Prizm.getAccount().getID().equals(Calculate.getAccountID(Calculate.getPublicKeyPrizm(passwordValue)))) {
                        password.setError("Invalid private key!");
                        return;
                    } else {
                        password.clearError();
                        password.getTextBox().setValue("***");
                    }
                }
                if (publicKey == null || publicKey.isEmpty()) return;
                Requests.sendMoney(recipient.getValue(), recipientPublicKey.getValue(), transaction.getComment(), amountL, true, passwordValue, new Requests.SendMoneyHandler() {
                    @Override
                    public void onSuccess(final SendMoney sendMoneyData) {
                        
                        sendMoneySubmit = SendMoneySubmit.convert(sendMoneyData, passwordValue);
                        recipient.getTextBox().setReadOnly(true);
                        recipient.getTextBox().setValue(transaction.getOpponent());

                        recipientPublicKey.getTextBox().setReadOnly(true);
                        recipientPublicKey.getTextBox().setValue(recipientPublicKey.getValue().trim().toLowerCase());
                        recipientPublicKey.getTextBox().setVisible(false);

                        amount.getTextBox().setReadOnly(true);
                        amount.getTextBox().setValue(DataUtils.formatNumber(transaction.getAmount()) + " (fee: " + DataUtils.formatNumber(transaction.getFee()) + ")");

                        info.getTextBox().setReadOnly(true);
                        info.getTextBox().setValue(transaction.getComment());
                        
                        password.setVisible(false);

                        loginBtn.setText("Submit");
                        loginBtn.setEnabled(true);
                    }
                });
            } finally {
                loginBtn.setEnabled(true);
            }
        } else {
            Requests.submitPayment(sendMoneySubmit, new Requests.SendMoneySubmitHandler() {
                @Override
                public void onSuccess(SendMoneyResponse sendMoneySubmitData) {
                    transaction.setID(sendMoneySubmitData.getTransaction());
                    Prizm.addSavedTransaction(transaction);
                    ModalPayment.this.hide();
                }
            });
        }
    }
    
    private static String passwordForSign = null;

    public static String getPasswordForSign() {
        String retval = passwordForSign;
        passwordForSign = null;
        return retval;
    }

    public static void setPasswordForSign(String passwordForSign) {
        ModalPayment.passwordForSign = passwordForSign;
    }
    
    
    private SendMoneySubmit sendMoneySubmit = null;

    public ModalPayment(String to) {
        this();
        if (to == null || to.isEmpty()) return;
        if (to.startsWith("https://") && to.indexOf("to=") + 3 < to.length()) {
            to = to.substring(to.indexOf("to=") + 3);
        }
        String para[] = to.split(":",4);
        if (para == null || para.length < 2) return;
        recipient.setValueRO(Calculate.getRSaddressPrizm(para[0]));
        if (!para[1].isEmpty()) {
            recipientPublicKey.setValueRO(para[1]);
            recipientPublicKey.setVisible(false);
        } else {
            Requests.getAccountSubData(recipient.getValue(), new Requests.GetAccountSubDataHandler() {
                @Override
                public void onSuccess(AccountSub accountData) {
                    recipientPublicKey.setValueRO(accountData.getPublicKey());
                    recipientPublicKey.setVisible(accountData.getPublicKey() != null && !accountData.getPublicKey().isEmpty());
                }
            });
        }
        if (para.length >= 3) {
            try {
                amount.setValueRO(DataUtils.formatNumber(Double.parseDouble(para[2])));
            } catch (NumberFormatException ex) {}
        }
        if (para.length >= 4) {
            info.setValueRO(para[3]);
        }
        addShownHandler(new ModalShownHandler() {
            @Override
            public void onShown(ModalShownEvent evt) {
                amount.setFocus();
            }
        });
    }
    
    public ModalPayment() {
        super();
        setTitle("Create Transaction");
        setClosable(true);
        setFade(true);
        
        ModalBody body = new ModalBody();
        ModalFooter footer = new ModalFooter();
        add(body);
        add(footer);
        
        body.add(well);
        
        form.add(recipient);
        form.add(recipientPublicKey);
        form.add(amount);
        form.add(info);
        form.add(password);
        reinit();
        well.add(form);
        
        loginBtn.setType(ButtonType.SUCCESS);
        loginBtn.setIcon(IconType.CHECK);
        cancelBtn.setType(ButtonType.DEFAULT);
        cancelBtn.setIcon(IconType.TIMES);
        
        footer.add(loginBtn);
        footer.add(cancelBtn);
        ClickHandler okHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                loginBtn.setEnabled(false);
                validate(false);
            }
        };
        loginBtn.addClickHandler(okHandler);
        cancelBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        addShownHandler(new ModalShownHandler() {
            @Override
            public void onShown(ModalShownEvent evt) {
                recipient.setFocus();
            }
        });
        addHiddenHandler(new ModalHiddenHandler() {
            @Override
            public void onHidden(ModalHiddenEvent evt) {
                String RID = Window.Location.getParameter("id");
            }
        });
        recipient.getTextBox().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> valueChangeEvent) {
                recipientPublicKey.getTextBox().setValue("");
                recipientPublicKey.setVisible(true);
                if (recipientPublicKey.getValue() != null && !recipientPublicKey.getValue().isEmpty()) {
                    return;
                }
                recipientPublicKey.getTextBox().setReadOnly(true);
                recipient.getTextBox().setReadOnly(true);
                String recipientAddress = DataUtils.clean(recipient.getValue());
                if (!Calculate.isValidReedSolomon(recipientAddress)) {
                    recipientPublicKey.getTextBox().setReadOnly(false);
                    recipient.getTextBox().setReadOnly(false);
                    return;
                }
                Requests.getAccountSubData(recipientAddress, new Requests.GetAccountSubDataHandler() {
                    @Override
                    public void onSuccess(AccountSub accountData) {
                        if (accountData.getPublicKey() == null || accountData.getPublicKey().isEmpty()) {
                            recipientPublicKey.getTextBox().setReadOnly(false);
                            recipient.getTextBox().setReadOnly(false);
                            return;
                        }
                        recipientPublicKey.getTextBox().setValue(accountData.getPublicKey());
                        recipientPublicKey.getTextBox().setReadOnly(false);
                        recipientPublicKey.setVisible(false);
                        recipient.getTextBox().setReadOnly(false);
                    }
                });
            }
        });
        recipient.getTextBox().addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                

            }
        });
    }
}
