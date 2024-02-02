package prizm.client.bricks;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.shared.event.ModalShownHandler;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import prizm.client.EasyCopy;
import prizm.client.Gaze;
import prizm.client.Prizm;
import prizm.client.pojo.Account;
import prizm.client.service.BrowserStorage;
import prizm.client.service.Calculate;
import prizm.client.service.DataUtils;

public class ModalLogin extends Modal {

    private ShriInput code = new ShriInput("PRIZM Address | Passphrase:", "PRIZM Address | Passphrase...");
    
    private void auth(String passPhrase) {
        BrowserStorage.clearAll();
        BrowserStorage.setPasswordEncoded(passPhrase);
        
        String PK = Calculate.getPublicKeyPrizm(passPhrase);
        String ID = Calculate.getAccountID(PK);
        String RS = Calculate.getRSaddressPrizm(ID);
        Account account = new Account();
        account.setPassPhrase(passPhrase);
        account.setPublicKey(PK);
        account.setReedSolomon(RS);
        account.setID(ID);
        Prizm.setAccount(account);
        BrowserStorage.writeAuthCredentials(ID, PK);
        Prizm.updateAll();
        ModalLogin.this.hide();
    }
    
    private void authByID(String reedSolomon) {
        BrowserStorage.clearAll();
        if (!Calculate.isValidReedSolomon(reedSolomon)) return;
        String ID = Calculate.getAccountIDByRS(reedSolomon);
        if (ID == null || ID.isEmpty()) return;
        Account account = new Account();
        account.setReedSolomon(reedSolomon);
        account.setID(ID);
        Prizm.setAccount(account);
        BrowserStorage.writeAuthCredentials(ID);
        Prizm.updateAll();
        ModalLogin.this.hide();
    }    
    
    private void validate() {
        String maybeRSAddress = code.getValue();
        if (Calculate.isValidReedSolomon(maybeRSAddress)) {
            if (Prizm.isPreAuthorized()) {
                Prizm.resetAll();
            }
            authByID(DataUtils.clean(maybeRSAddress));
            return;
        }
        if (code.getValue().length()<35) {
            code.setError("Password too short (minimum 35 symbols)");
            return;
        } else {
            code.clearError();
        }
        code.getTextBox().setReadOnly(true);
        if (Prizm.isPreAuthorized()) {
            if (code.getValue().isEmpty() || !Prizm.getAccount().getID().equals(Calculate.getAccountID(Calculate.getPublicKeyPrizm(code.getValue())))) {
                code.setError("Invalid private key!");
                code.getTextBox().setReadOnly(false);
                return;
            } else {
                code.clearError();
                Prizm.getAccount().setPassPhrase(code.getValue());
                Calculate.recalculateAuthCredentials();
                Prizm.resetOffset();
                Prizm.updateAll();
                return;
            }
        }
        auth(DataUtils.clean(code.getValue()));
    }
    
    private void validate(String password) {
        auth(password);
    }
    
    public static int[] getInts(Integer[] integers) {
        if (integers == null || integers.length < 1) return null;
        int result[] = new int[integers.length];
        for (int i = 0; i < integers.length; i++) {
            result[i] = integers[i];
        }
        return result;
    }
    
    public ModalLogin(boolean isRegister, String passKey) {
        setTitle("Create new account");

        ModalBody body = new ModalBody();
        add(body);
        Well wellCode = new Well();
        body.add(wellCode);
        wellCode.getElement().getStyle().setTextAlign(Style.TextAlign.CENTER);

        setFade(true);
        
        final String generatedPassword = passKey;

        Heading attention = new Heading(HeadingSize.H3, "Attention");
        attention.setStyleName("attention", true);
        Label warning = new Label("This passphrase cannot be recovered. Write down on paper or take a photo.");
        warning.setStyleName("warning", true);
        wellCode.add(attention);
        wellCode.add(warning);
        
        Heading headingDescription = new Heading(HeadingSize.H3, "Generated passphrase:");
        wellCode.add(headingDescription);
        wellCode.add(EasyCopy.createCopyButtonGWT(generatedPassword));
        
        headingDescription.setMarginBottom(30);
        
        TextArea area = new TextArea();
        area.setHeight("200px");
        area.addStyleName("selection");
        area.setReadOnly(true);
        area.setValue(generatedPassword);
        wellCode.add(area);

        addShownHandler(new ModalShownHandler() {
            @Override
            public void onShown(ModalShownEvent evt) {
                code.setFocus();
            }
        });

        ModalFooter footer = new ModalFooter();
        Button submitButton = new Button("Sign in", IconType.CHECK, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ModalLogin.this.hide();
                new ModalLogin().show();
//                validate(generatedPassword);
            }
        });
        Button cancelButton = new Button("Cancel", IconType.TIMES, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ModalLogin.this.hide();
            }
        });
        submitButton.setType(ButtonType.SUCCESS);
        footer.add(submitButton);
        footer.add(cancelButton);
        add(footer);
        if (BrowserStorage.readAuthCredentials() != null && !BrowserStorage.readAuthCredentials().isEmpty()) {
            code.getTextBox().setValue(Calculate.getRSaddressPrizm(BrowserStorage.readAuthCredentials()));
        }
    }
    
    public ModalLogin() {
        setTitle("Sign in");

        ModalBody body = new ModalBody();
        add(body);
        Well wellCode = new Well();
        body.add(wellCode);

        setFade(true);

        final ShriForm formCode = new ShriForm(new ShriForm.ShriFormHander() {
            @Override
            public void onSubmit(ShriForm form) {
                validate();
            }
        });

        formCode.add(code);
        wellCode.add(formCode);

        addShownHandler(new ModalShownHandler() {
            @Override
            public void onShown(ModalShownEvent evt) {
                code.setFocus();
            }
        });

        ModalFooter footer = new ModalFooter();
        Button submitButton = new Button("Sign in", IconType.CHECK, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                validate();
            }
        });
        Button cancelButton = new Button("Cancel", IconType.TIMES, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ModalLogin.this.hide();
            }
        });
        submitButton.setType(ButtonType.SUCCESS);
        footer.add(cancelButton);
        footer.add(submitButton);
        add(footer);
    }    
}
