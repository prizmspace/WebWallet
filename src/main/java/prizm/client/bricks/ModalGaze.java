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
import org.gwtbootstrap3.client.ui.html.Text;
import prizm.client.Gaze;
import prizm.client.Prizm;
import prizm.client.service.Calculate;

public class ModalGaze extends Modal {

    private static Random random = new Random();

    private static int counter = 0;

    private static boolean isDefaultQr = true;

    private String publicKey = null;

    public ModalGaze() {
        this(null);
    }

    public ModalGaze(String publicKey) {
        this.publicKey = publicKey;
        setTitle("PRIZM Gaze");

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
                String qrDivID = "gaze" + (counter++);
                String printDivId = "gaze-print" + (counter++);
                final Div div = new Div();
                div.setId(qrDivID);
                div.getElement().setId(qrDivID);
                div.getElement().getStyle().setWidth(100, Style.Unit.PCT);
                div.getElement().getStyle().setPosition(Style.Position.RELATIVE);
                div.getElement().getStyle().setOpacity(0);
                HTML gazeTitle = new HTML("<h1 class=\"gaze-title print-only\">PRIZM Gaze</h1><hr class=\"gaze-split\">");
                wellCode.add(gazeTitle);
                wellCode.add(div);
                wellCode.setId(printDivId);
                wellCode.getElement().setId(printDivId);

                wellCode.getElement().setAttribute("tabindex", "0");
                
                Label disclaimer = new Label("Click \"Generate PRIZMGaze QR\" if you have not generated the code before or if you want to change the pin code. If you change the pin code, destroy the old QR code so that no one can access it.");
                disclaimer.setId("gaze-disclaimer");
                wellCode.add(disclaimer);

                Timer timer = new Timer() {
                    @Override
                    public void run() {
                        div.getElement().getStyle().setOpacity(1);
                    }
                };
                timer.schedule(100);
                ModalFooter footer = new ModalFooter();
                Button generateButton = new Button("Generate PRIZMGaze QR");
                generateButton.setIcon(IconType.CHECK);
                Button printButton = new Button("Print / Save as PDF", IconType.PRINT, new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        Calculate.print(Prizm.getAccount().getReedSolomon(),printDivId);
                    }
                });
                generateButton.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        new ModalPIN(true) {
                            @Override
                            public void submit(ModalPIN modalPIN, String PIN) {
                                modalPIN.hide();
                                Calculate.qrCodemaster(qrDivID, Gaze.generateQrContent(Prizm.getAccount().getPassPhrase(), PIN));
                                generateButton.setVisible(false);
                                printButton.setVisible(true);
                                disclaimer.setVisible(false);
                                HTML html = new HTML(
                                        "<span class=\"gaze-account\">"+Prizm.getAccount().getReedSolomon()+"</span>" +
                                        "<hr class=\"gaze-split\">" +
                                        "<div class=\"gaze-passphrase-container\"><span class=\"gaze-passphrase\">"+Prizm.getAccount().getPassPhrase()+"</span>" +
                                        "<span class=\"gaze-account\">"+Prizm.getAccount().getReedSolomon()+"</span></div>" +
                                        "<hr class=\"gaze-split\">" +
                                        "<h1 class=\"gaze-title print-only\">PRIZM Gaze</h1>"
                                );
                                wellCode.add(html);
                                new Timer() {
                                    @Override
                                    public void run() {
                                        Calculate.print(Prizm.getAccount().getReedSolomon(),printDivId);
                                    }
                                }.schedule(1125);
                            }
                        }.show();
                    }
                });
                
                generateButton.setType(ButtonType.SUCCESS);
                footer.add(generateButton);
                
                printButton.setType(ButtonType.SUCCESS);
                footer.add(printButton);
                printButton.setVisible(false);
                Button closeButton = new Button("Close", IconType.TIMES, new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        hide();
                    }
                });
                closeButton.setType(ButtonType.DEFAULT);
                footer.add(closeButton);
                add(footer);
            }
        });

        
    }
}
