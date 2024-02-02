package prizm.client.bricks;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.PasswordTextBox;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.InlineHelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.IconType;

public class ShriInputPassword extends FormGroup {
    
    private static String TAG = "texte";
    private static int counter = 0;
    
    public static interface ShriInputHandler {
        public void onChange(ShriInputPassword input);
    }

    public PasswordTextBox getTextBox() {
        return textBox;
    }
    
    private FormLabel formLabel = new FormLabel();
    private InlineHelpBlock helpBlock = new InlineHelpBlock();
    private PasswordTextBox textBox = new PasswordTextBox();
    
    private ShriInputHandler shriHandler = null;
    
    private void shriHandlerValueChange() {
        if (shriHandler != null) shriHandler.onChange(this);
    }
    
    private boolean errored = false;

    public void setError(String errorMessage) {
        helpBlock.setIconType(IconType.EXCLAMATION_TRIANGLE);
        helpBlock.setError(errorMessage);
        helpBlock.setColor("#FF0000");
        errored = true;
    }
    
    public void setSuccess(String successMessage) {
        helpBlock.setIconType(IconType.CHECK);
        helpBlock.setError(successMessage);
        helpBlock.setColor("#449d44");        
        errored = false;
    }
    
    public void clearError() {
        helpBlock.clearError();
        errored = false;
    }

    public boolean isErrored() {
        return errored;
    }
     
    public ShriInputPassword(String title, String placeHolder, ShriInputHandler handler) {
        this(title, placeHolder);
        this.shriHandler = handler;
    }
    
    public void setTitle(String newTitle) {
        formLabel.setHTML(newTitle);
    }
    
    public ShriInputPassword(String title, String placeHolder) {        
        add(formLabel);
        add(helpBlock);
        add(textBox);

        textBox.getElement().setId(TAG+counter);
        formLabel.setFor(TAG+(counter++));
        
        formLabel.setHTML(title);
        
        textBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                shriHandlerValueChange();
            }
        });
        textBox.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                shriHandlerValueChange();
            }
        });
    }
    
    public void setShriInputHandler(ShriInputHandler shriInputHandler) {
        this.shriHandler = shriInputHandler;
    }
    
    public String getValue() {
        return textBox.getValue();
    }
    
    public void setValueRO(String value) {
        setVisible(true);
        textBox.setValue(value);
        textBox.setReadOnly(true);
    }
    
    public void setFocus() {
        textBox.setFocus(true);
    }
}
