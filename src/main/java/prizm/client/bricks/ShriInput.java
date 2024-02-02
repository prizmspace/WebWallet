package prizm.client.bricks;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.InlineHelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import prizm.client.EasyCopy;

public class ShriInput extends FormGroup implements ShriInputInterface {
    
    private static String TAG = "texte";
    private static int counter = 0;
    
    public static interface ShriInputHandler {
        public void onChange(ShriInputInterface input);
    }

    public TextBox getTextBox() {
        return textBox;
    }
    
    private FormLabel formLabel = new FormLabel();
    private InlineHelpBlock helpBlock = new InlineHelpBlock();
    private TextBox textBox = new TextBox();
    
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
     
    public ShriInput(String title, String placeHolder, ShriInputHandler handler) {
        this(title, placeHolder);
        this.shriHandler = handler;
    }
    
    public void setTitle(String newTitle) {
        formLabel.setHTML(newTitle);
    }
    
    public ShriInput(String title, String placeHolder) {
        this(title,placeHolder,null,false);
    }
    
    public ShriInput(String title, String placeHolder, String predefined, boolean allowCopy) {        
        add(formLabel);
        if (predefined != null && allowCopy) {
            add(EasyCopy.createCopyButtonGWT(predefined));
        }
        add(helpBlock);
        add(textBox);
        textBox.setAutoComplete(false);

        textBox.getElement().setId(TAG+counter);
        formLabel.setFor(TAG+(counter++));
        
        formLabel.setHTML(title);
        textBox.setPlaceholder(placeHolder);
        
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
        if (predefined != null) {
            setValueRO(predefined);
        }
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
