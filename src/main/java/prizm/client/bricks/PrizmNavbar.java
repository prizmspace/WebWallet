package prizm.client.bricks;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Navbar;
import org.gwtbootstrap3.client.ui.NavbarBrand;
import org.gwtbootstrap3.client.ui.NavbarCollapse;
import org.gwtbootstrap3.client.ui.NavbarCollapseButton;
import org.gwtbootstrap3.client.ui.NavbarHeader;
import org.gwtbootstrap3.client.ui.NavbarNav;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.NavbarPosition;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.html.Div;
import prizm.client.Gaze;
import prizm.client.Prizm;
import prizm.client.pojo.Account;
import prizm.client.service.BrowserStorage;
import prizm.client.service.Calculate;
import prizm.client.service.DataUtils;

public abstract class PrizmNavbar extends Navbar {
    private Container container = new Container();
    private NavbarHeader header = new NavbarHeader();
    private NavbarNav nav = new NavbarNav();
    private AnchorListItem register = new AnchorListItem("Registration");
    private AnchorListItem login = new AnchorListItem("Sign in");
    private AnchorListItem pay = new AnchorListItem("Send");
    private AnchorListItem scan = new AnchorListItem("");
    private AnchorListItem exit = new AnchorListItem("");
    private NavbarBrand span = new NavbarBrand();
    private static Gaze gaze = null;

    public void setAmount(double amount, double potential) {
        span.setHTML("<span style=\"top: -10px;position: relative;\"><b>B:" + DataUtils.formatNumber(DataUtils.fixWithCents(amount)) + "</b><br>P:"
                + (Prizm.showParaminingValue() ? DataUtils.formatNumberSmart(potential) : "--------") + "</span>");
    }
    
    public void clearAmount() {
        span.setHTML("");
    }
    
    private Timer timer = new Timer() {
        @Override
        public void run() {
            setAmount(Prizm.getRealBalance(), Prizm.getAccount().getGoodGrowPlus());
        }
    };
    
    public void fix() {
        if (Prizm.getAccount() != null) {
            register.setVisible(false);
            login.setVisible(false);
            scan.setVisible(true);
            pay.setVisible(true);
            exit.setVisible(true);
            timer.scheduleRepeating(100);
            getElement().removeClassName("login");
        } else {
            register.setVisible(true);
            login.setVisible(true);
            scan.setVisible(true);
            pay.setVisible(false);
            exit.setVisible(false);
            timer.cancel();
            clearAmount();
            getElement().addClassName("login");
        }
    }
    
    public PrizmNavbar() {
        setPosition(NavbarPosition.FIXED_TOP);
        Div image = new Div();
        image.setHeight("45px");
        image.setPull(Pull.LEFT);
        image.getElement().setClassName("prizm-logo");
        image.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        image.getElement().getStyle().setMarginLeft(4, Style.Unit.PX);

        header.add(image);
        header.add(span);
        span.setMarginLeft(10);
        container.add(header);
        pay.setId("header-button-send");
        pay.setPull(Pull.RIGHT);
        scan.setId("header-button-scan");
        scan.setPull(Pull.RIGHT);
        exit.setPull(Pull.RIGHT);
        exit.setId("header-button-exit");
        
        pay.setVisible(false);
        exit.setVisible(false);
        scan.setVisible(true);
        getElement().addClassName("login");
        
        nav.add(exit);
        nav.add(pay);
        nav.add(scan);

        login.setId("header-button-login");
        register.setId("header-button-register");
        nav.add(register);
        nav.add(login);
        nav.setPull(Pull.RIGHT);

        container.add(nav);
        login.setPull(Pull.RIGHT);
        register.setPull(Pull.RIGHT);
        
        
        register.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onRegister();
            }
        });
        login.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onLogin();
            }
        });
        exit.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onExit();
            }
        });
        pay.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onPay();
            }
        });
        scan.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Calculate.startScanner();
                new Timer() {
                    @Override
                    public void run() {
                        String result = Calculate.getScanResult();
                        if (result == null) {
                            if (!Calculate.isScanning()) {
                                cancel();
                                return;
                            }
                            return;
                        }
                        if (Gaze.isAuthCode(result)) {
                            gaze = new Gaze(result);
                            new ModalPIN(false) {
                                @Override
                                public void submit(ModalPIN modalPIN, String PIN) {
                                    if (gaze.testPin(PIN)) {
                                        if (Prizm.isPreAuthorized()) {
                                            Prizm.resetAll();
                                        }
                                        BrowserStorage.clearAll();
                                        String passPhrase = gaze.decryptPassphrase(PIN);
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
                                        Prizm.resetOffset();
                                        Prizm.updateAll();
                                    }
                                    modalPIN.hide();
                                }
                            }.show();
                        } else {
                            new ModalPayment(result).show();
                        }
                        cancel();
                    }
                }.scheduleRepeating(666);
            }
        });
        add(container);
        
    }
    
    public abstract void onRegister();

    public abstract void onLogin();
    
    public abstract void onExit();
    
    public abstract void onPIN();
    
    public abstract void onPay();
    
    public abstract void onDecrypt();
}
