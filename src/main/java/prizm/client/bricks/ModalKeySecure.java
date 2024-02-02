package prizm.client.bricks;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.shared.event.ModalShownHandler;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.Well;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import prizm.client.service.Calculate;

public abstract class ModalKeySecure extends Modal {

    private final static int NEED_ADD = 16;
    private final static String LINE_PREFIX = "Add ";
    private final static String LINE_SUFFIX = " random symbols in random places [a-zA-Z ]:";
    private final Button loginBtn = new Button("Proceed");
    private final Button cancelBtn = new Button("Cancel");
    private final ShriForm form = new ShriForm(new ShriForm.ShriFormHander() {
        @Override
        public void onSubmit(ShriForm form) {
            validate();
        }
    });
    private final ShriInputMultiline key = new ShriInputMultiline(LINE_PREFIX+NEED_ADD+LINE_SUFFIX, "Secret key...");
    private final Well well = new Well();
    
    private String VID = null;
    private Double amountD = null;
    private String infoS = null;

    private void setTitle(int symbols) {
        key.setTitle(LINE_PREFIX+symbols+LINE_SUFFIX);
    }
    
    private void reinit() {
        if (VID != null) VID = null;
        if (amountD != null) amountD = null;
        if (infoS != null) infoS = null;
        
        key.getTextBox().setReadOnly(false);        
        key.setVisible(true);
        loginBtn.setVisible(false);
    }
    
    public void activate(String RID) {
        key.getTextBox().setValue(RID);
        validate();
        show();
    }
    
    private String forceCheckS;
    private boolean canSubmit = false;
    
    private boolean forceCheck(String valueIn) {
        String value = valueIn==null?key.getValue():valueIn;
        String resultString = "";
        for (int i = 0; i < value.length(); i++) {
            String check = "" + value.charAt(i);
            if (!check.matches("^[a-zA-Z ]{1}$")) continue;
            resultString += check;
        }
        resultString = resultString.replaceAll("[\\s]+", " ");
        try {
            if (!resultString.equals(value)) {
                key.getTextBox().setValue(resultString);
                forceCheckS = resultString;
                return true;
            } else {
                forceCheckS = value;
                return false;
            }
        } finally {
            int symbolsAmount = generatedKeyIn.length()+NEED_ADD - forceCheckS.length();
            setTitle(symbolsAmount >= 0?symbolsAmount:0);
            if (symbolsAmount <= 0) {
                canSubmit = true;
                loginBtn.setVisible(true);
                validate();
            } else {
                canSubmit = false;
                loginBtn.setVisible(false);
            }
        }
    }
    
    private boolean validatePushed = false;
    
    public void validate() {
        if (!canSubmit) return;
        if (validatePushed) return;
        validatePushed = true;
        ModalKeySecure.this.hide();
        String resultingPassword = "prizm "+forceCheckS;
        String sendString = resultingPassword.replaceAll("[\\s]+", " ").trim();
        onPasswordChanged(sendString);
    }
    
    public abstract void onPasswordChanged(String password);
    
    private String generatedKeyIn;
    
    public ModalKeySecure(String generatedKey) {
        super();
        generatedKeyIn = generatedKey;
        addShownHandler(new ModalShownHandler() {
            @Override
            public void onShown(ModalShownEvent evt) {
                key.setFocus();
            }
        });
        setTitle("Increase private key security");
        setClosable(true);
        setFade(true);
        
        ModalBody body = new ModalBody();
        ModalFooter footer = new ModalFooter();
        add(body);
        add(footer);
        
        body.add(well);
        
        form.add(key);
        reinit();
        well.add(form);
        key.getTextBox().setId("passphrase-");
        key.getTextBox().setValue(generatedKey);

        key.getTextBox().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                forceCheck(event.getValue());
            }
        });
        key.getTextBox().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                int position = key.getTextBox().getCursorPos();
                if (forceCheck(key.getTextBox().getValue())) {
                    key.getTextBox().setCursorPos(position<1?position:position-1);
                }
            }
        });
        
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
                validate();
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
                key.setFocus();
            }
        });
    }
}
