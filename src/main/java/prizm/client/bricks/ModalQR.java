package prizm.client.bricks;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import java.util.Random;

import com.google.gwt.user.client.ui.HTML;
import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.shared.event.ModalShownHandler;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;
import org.gwtbootstrap3.client.ui.html.Div;
import prizm.client.Prizm;
import prizm.client.service.Calculate;

public class ModalQR extends Modal {

    private static Random random = new Random();
    
    private static int counter = 0;
    
    private static boolean isDefaultQr = true;
    
    private String publicKey = null;
    
    public ModalQR() {
        this(null);
    }
    
    public ModalQR(String publicKey) {
        this.publicKey = publicKey;
        setTitle("My PRIZM account");

        ModalBody body = new ModalBody();
        add(body);
        Well wellCode = new Well();
        Well wellData = new Well();
        body.add(wellCode);
        body.add(wellData);

        setFade(true);
        
        addShownHandler(new ModalShownHandler() {
            @Override
            public void onShown(ModalShownEvent evt) {
                String qrDivID = "qrshowr" + (counter++);
                String printDivID = "qrprint" + (counter++);
                final Div div = new Div();
                div.setId(qrDivID);
                div.getElement().setId(qrDivID);
                div.getElement().getStyle().setWidth(100, Style.Unit.PCT);
                div.getElement().getStyle().setPosition(Style.Position.RELATIVE);
                div.getElement().getStyle().setOpacity(0);
                wellCode.add(div);
                wellCode.setId(printDivID);
                wellCode.getElement().setId(printDivID);
                wellCode.addStyleName("well-qr");
                String publicKey = Prizm.getAccount().getPublicKey()!=null ? Prizm.getAccount().getPublicKey() : ModalQR.this.publicKey;

                HTML html = new HTML(
                        "<br><span class=\"gaze-qr print-only\">Address:<br>"+Prizm.getAccount().getReedSolomon()+"</span>" +
                        "<br><span class=\"gaze-qr print-only\">Public key:<br>"+publicKey+"</span>"
                );
                wellCode.add(html);

                String url = "https://wallet.prizm.space/?to="+Prizm.getAccount().getID()+":"+publicKey;
                String urlAndroid = Prizm.getAccount().getID()+":"+publicKey;
                
                wellCode.getElement().setAttribute("tabindex", "0");
                
                Calculate.qrCodemaster(qrDivID, url);
                Timer timer = new Timer() {
                    @Override
                    public void run() {
                        div.getElement().getStyle().setOpacity(1);
                    }
                };
                timer.schedule(100);
                ShriInput pubKey = new ShriInput("Public key", "", publicKey, true);
                ShriInput address = new ShriInput("Address", "", Prizm.getAccount().getReedSolomon(), true);
                address.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
                wellData.add(address);
                wellData.add(pubKey);
                pubKey.getTextBox().selectAll();
                ModalFooter footer = new ModalFooter();
                Button printButton = new Button("Print / Save as PDF", IconType.PRINT, new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        Calculate.print(Prizm.getAccount().getReedSolomon(),printDivID);
                    }
                });
                printButton.setType(ButtonType.SUCCESS);
                footer.add(printButton);
                Button submitButton = new Button("Close", IconType.TIMES, new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        hide();
                    }
                });
                submitButton.setType(ButtonType.DEFAULT);
                footer.add(submitButton);
                add(footer);
            }
        });

        
    }    
}
