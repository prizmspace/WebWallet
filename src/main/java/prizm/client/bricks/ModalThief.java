package prizm.client.bricks;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.Well;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.IconType;

public class ModalThief extends Modal {

    public ModalThief() {
        setTitle("WARNING! ВНИМАНИЕ!");

        ModalBody body = new ModalBody();
        add(body);
        Well wellCode = new Well();
        body.add(wellCode);
        wellCode.getElement().getStyle().setTextAlign(Style.TextAlign.CENTER);

        setFade(true);
        
        Heading headingDescription1 = new Heading(HeadingSize.H3, "ATTENTION!");
        wellCode.add(headingDescription1);
        headingDescription1.setMarginBottom(30);
        
        Heading headingPasswordA = new Heading(HeadingSize.H5, "Your private key is compromised. ");
        wellCode.add(headingPasswordA);
        Heading headingPassword1 = new Heading(HeadingSize.H6, "Generate a new secure private key right now. "
                + "Coins PZM will be returned in manual mode through your superior follower. "
                + "To return your PZM coins you will need a new purse address, generate it right now and securely store it. "
                + "More detailed information you can get from someone who told you personally about PRIZM.");
        wellCode.add(headingPassword1);
        
        Heading headingDescription = new Heading(HeadingSize.H3, "ВНИМАНИЕ!");
        wellCode.add(headingDescription);
        headingDescription.setMarginBottom(30);
        
        Heading headingPasswordXA = new Heading(HeadingSize.H5, "Ваш приватный ключ скомпрометирован. ");
        wellCode.add(headingPasswordXA);
        Heading headingPassword = new Heading(HeadingSize.H6, "Сгенерируйте новый безопасный приватный ключ прямо сейчас. "
                + "Монеты PZM будут возвращены в ручном режиме через Вашего вышестоящего последователя. "
                + "Для возврата Ваших монет PZM понадобится новый адрес кошелька, сгенерируйте его прямо сейчас и надежно сохраните. "
                + "Более подробную информацию вы сможете получить от того кто рассказал лично Вам о PRIZM.");
        wellCode.add(headingPassword);


        ModalFooter footer = new ModalFooter();
        Button submitButton = new Button("OK", IconType.CHECK, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ModalThief.this.hide();
            }
        });
        submitButton.setType(ButtonType.SUCCESS);
        footer.add(submitButton);
        add(footer);        
    }    
}
