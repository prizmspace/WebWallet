package prizm.client.bricks;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.Form;

public class ShriForm extends Form {
    private FieldSet fieldSet = new FieldSet();

    public static interface ShriFormHander {
        public void onSubmit(ShriForm form);
    }    
    
    private ShriFormHander shriFormHandler = null;
    
    public void shriHandlerSubmit() {
        if (shriFormHandler != null) shriFormHandler.onSubmit(this);
    }    

    public ShriForm(ShriFormHander shriFormHander) {
        this.add(fieldSet);
        this.shriFormHandler = shriFormHander;
    }

    public void add(ShriInput shriInput) {
        fieldSet.add(shriInput);
        shriInput.getTextBox().addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                    shriHandlerSubmit();
                }
            }
        });
    }
    
    public void clear() {
        fieldSet.clear();
    }
}
