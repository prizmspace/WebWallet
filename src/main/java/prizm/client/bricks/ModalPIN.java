package prizm.client.bricks;

import java.util.Random;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;

public abstract class ModalPIN extends Modal {

    private Image image = new Image();
    
    private static Random random = new Random();

    public PINPad getPinpad() {
        return pinpad;
    }

    private void setRegister() {
        if(currentPIN == null) {
            setTitle("Set your new PIN code");
        } else {
            setTitle("Set your new PIN code again");
        }
    }
    
    private PINPad pinpad = null;
    private String currentPIN = null;
    
    public void setError(String error) {
        pinpad.setError(error);
        pinpad.reset();
    }
    
    public void clearError() {
        pinpad.clearError();
    }
    
    public ModalPIN(final boolean createPIN) {
        if (createPIN) {
            setRegister();
        } else {
            setTitle("Enter your PIN code");
        }
        ModalBody body = new ModalBody();
        add(body);
        
        setFade(true);

        pinpad = new PINPad() {
            @Override
            public void onSubmit(String code) {
                if (createPIN) {
                    if (currentPIN == null) {
                        currentPIN = code;
                        pinpad.reset();
                        setRegister();
                        return;
                    } else {
                        if (currentPIN.equals(code)) {
                            submit(ModalPIN.this, currentPIN);
                            return;
                        };
                        currentPIN = null;
                        setRegister();
                        pinpad.reset();
                        pinpad.setError("Error! Entered PIN codes does not match!");
                        return;
                    }
                } else {
                    submit(ModalPIN.this, code);
                }
            }

        };
        pinpad.setStyleName("pinpad", true);
        body.add(pinpad);

        add(body);
    }
    
    public abstract void submit(ModalPIN modalPIN, String PIN);
}
