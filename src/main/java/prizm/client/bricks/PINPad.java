package prizm.client.bricks;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import java.util.ArrayList;
import java.util.List;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.Well;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Span;

public abstract class PINPad extends Container {
    
    private class IntHandler implements ClickHandler {

        private int number;
        private boolean isBackSpace;
        private boolean isSubmit;

        public IntHandler(int number, boolean isBackSpace, boolean isSubmit) {
            this.number = number;
            this.isBackSpace = isBackSpace;
            this.isSubmit = isSubmit;
        }
        
        @Override
        public void onClick(ClickEvent event) {
            clearError();
            if (!isBackSpace && !isSubmit) {
                if (PIN.length() >= 4) return;
                PIN+=(number+"");
                fix();
                return;
            }
            if (isBackSpace) {
                if (PIN.length()<1) return;
                PIN = PIN.substring(0, PIN.length()-1);
                fix();
                return;
            }
            if (isSubmit) onSubmit(PIN);
        }
        
    }
    
    private List<Button> numericBtn = new ArrayList<Button>();
    private Button backSpaceBtn = null;
    private Button submitBtn = null;

    public void reset() {
        PIN = "";
        fix();
    }
    
    private Button generate(int counter) {
        Button button;
        switch(counter) {
            case 10:
                button = new Button("", IconType.ARROW_LEFT, new IntHandler(-1, true, false));
                backSpaceBtn = button;
                break;
            case 11:
                button = new Button("0", new IntHandler(0, false, false));
                numericBtn.add(button);
                break;
            case 12:
                button = new Button("", IconType.CHECK, new IntHandler(-1, false, true));
                button.setType(ButtonType.LINK);
                submitBtn = button;
                break;
            default:
                button = new Button(counter+"", new IntHandler(counter, false, false));
                numericBtn.add(button);
                break;
        }
        button.setBlock(true);
        button.setSize(ButtonSize.LARGE);
        return button;
    }
    
    String PIN = "";
    
    private boolean isNumpadEnabled = true;
    
    private void setNumpad(boolean enabled) {
        if (isNumpadEnabled == enabled) return;
        for (Button btn : numericBtn) {
            btn.setEnabled(enabled);
        }
        isNumpadEnabled = enabled;
        
    }
    
    private void fix() {
        for (int i = 0; i < 4; i++) {
            if (PIN.length() < (i+1)) {
                heading[i].setText(".");
                continue;
            }
            heading[i].setText("*");
        }
        if (PIN.length() == 4) {
            setNumpad(false);
            submitBtn.setVisible(true);
        } else {
            setNumpad(true);
            submitBtn.setVisible(false);
        }
        if (PIN.length() >= 1) {
            backSpaceBtn.setVisible(true);
        } else {
            backSpaceBtn.setVisible(false);
        }
    }
    
    private Well well[] = new Well[4];
    private Heading heading[] = new Heading[4];
    private Span error = new Span();
    private boolean errored = false;
    
    public void setError(String errorMessage) {
        error.setHTML(errorMessage);
        error.setColor("#FF0000");
        errored = true;
    }
    
    public void clearError() {
        if (!errored) return;
        errored = false;
        error.setHTML("");
    }
    
    public PINPad() {
        
        setFluid(true);
        
        Row erow = new Row();
        Column eColumn = new Column(ColumnSize.XS_12, ColumnSize.SM_12, ColumnSize.MD_12, ColumnSize.LG_12);
        erow.add(eColumn);
        eColumn.add(error);
        add(erow);
        
        Row wrow = new Row();
        wrow.setMarginBottom(10);
        for (int i = 0; i < 4; i++) {
            well[i] = new Well();
            heading[i] = new Heading(HeadingSize.H3);
            heading[i].setText(".");
            well[i].add(heading[i]);
            well[i].getElement().getStyle().setTextAlign(Style.TextAlign.CENTER);
            Column column = new Column(ColumnSize.XS_3, ColumnSize.SM_3, ColumnSize.MD_3, ColumnSize.LG_3);
            column.add(well[i]);
            wrow.add(column);
        }
        add(wrow);
        
        int counter = 0;
        for (int i = 0; i < 4; i++) {
            Row row = new Row();
            for (int j = 0; j < 3; j++) {
                Column column = new Column(ColumnSize.XS_4, ColumnSize.SM_4, ColumnSize.MD_4, ColumnSize.LG_4);
                Button button = generate(++counter);
                column.add(button);
                row.add(column);
            }
            row.setMarginBottom(10);
            add(row);
        }
        backSpaceBtn.setVisible(false);
        submitBtn.setVisible(false);
    }
    
    public abstract void onSubmit(String code);
    
}
