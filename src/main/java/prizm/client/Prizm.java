package prizm.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Well;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.WellSize;
import prizm.client.bricks.*;
import prizm.client.calc.Calculator;
import prizm.client.json.AccountSub;
import prizm.client.pojo.Account;
import prizm.client.pojo.Transaction;
import prizm.client.service.BrowserStorage;
import prizm.client.service.Calculate;
import prizm.client.service.DataUtils;
import prizm.client.service.PasswordGen;
import prizm.client.service.Requests;

public class Prizm implements EntryPoint {

    private static long overallBalance = 0l;
    private static final Object OVERALL_BALANCE_LOCK = new Object();

  /**
   * The message displayed to the user when the server cannot be reached or
   * returns an error.
   */
  private static final String SERVER_ERROR = "An error occurred while "
      + "attempting to contact the server. Please check your network "
      + "connection and try again.";

  private static final int REFRESH_DELAY = 1000;
  private static final int BASIC_SEED_SIZE = 128;
  
  /**
   * Create a remote service proxy to talk to the server-side Greeting service.
   */
  private static final RandomServiceAsync randomService = GWT.create(RandomService.class);

  private final Messages messages = GWT.create(Messages.class);
  
  private static LogsTableNew logsTableNew = new LogsTableNew();
  private static Container logsTableContainer = new Container();
  
  private static PrizmNavbar navbar = new PrizmNavbar() {
      @Override
      public void onRegister() {
          randomService.getNumbers(BASIC_SEED_SIZE, new AsyncCallback<Integer[]>() {
              @Override
              public void onFailure(Throwable caught) {
                  throw new UnsupportedOperationException("Not supported yet.");
              }

              @Override
              public void onSuccess(Integer[] result) {
                  String password = PasswordGen.generatePassword(16, result);
                  ModalKeySecure modalKeySecure = new ModalKeySecure(password) {
                      @Override
                      public void onPasswordChanged(String password) {
                          ModalLogin login = new ModalLogin(true, password);
                          login.show();
                      }
                  };
                  modalKeySecure.show();
              }
          });
      } 

      @Override
      public void onLogin() {
          ModalLogin login = new ModalLogin();
          login.show();
      }

      @Override
      public void onExit() {
          BrowserStorage.clearAll();
          resetAll();
          Window.Location.reload();
      }
        
      
      @Override
      public void onPIN() {
      }

      @Override
      public void onPay() {
          ModalPayment modalPayment = new ModalPayment();
          modalPayment.show();
      }

      @Override
      public void onDecrypt() {
          ModalLogin login = new ModalLogin();
          login.show();          
      }
  };

    private static Container container = new Container();
    private static Well well = new Well();
    private static Heading heading = new Heading(HeadingSize.H5);  
    private static Button gaze = new Button("");
    private static boolean wellInitialized = false;
    private static boolean wellShown = false;

    public static long getForgedBalanceNQT() {
        return forgedBalanceNQT;
    }

    public static long getUnconfirmedBalanceNQT() {
        return unconfirmedBalanceNQT;
    }

    public static long getBalanceNQT() {
        return balanceNQT;
    }
    
    public static void hideWell() {
        if (wellShown) {
            container.removeFromParent();
            wellShown = false;
        }
    }
    
    public static void showWell() {
        if (!wellInitialized) {
            container = new Container();
            well.setId("account-well");
            well.add(heading);
            well.setSize(WellSize.SMALL);
            well.getElement().getStyle().setTextAlign(Style.TextAlign.CENTER);
            container.add(well);
            wellInitialized = true;
        }
        if (!wellShown) {
            RootPanel.get().add(container);
            wellShown = true;
        }
        heading.setText(getAccount().getReedSolomon());
        if (!initialized) {
            initialized = true;
            heading.addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (getAccount() == null) return;
                    if (getAccount().getPublicKey() == null) {
                        if (BrowserStorage.readAuthPublicKey() != null && !BrowserStorage.readAuthPublicKey().isEmpty()) {
                                ModalQR modalQR = new ModalQR(BrowserStorage.readAuthPublicKey());
                                modalQR.show();
                                return;
                        }
                        Requests.getAccountSubData(getAccount().getID(), new Requests.GetAccountSubDataHandler() {
                            @Override
                            public void onSuccess(AccountSub accountData) {
                                if (accountData.getPublicKey() == null || accountData.getPublicKey().isEmpty()) return;
                                ModalQR modalQR = new ModalQR(accountData.getPublicKey());
                                modalQR.show();
                            }
                        });
                        return;
                    }
                    ModalQR modalQR = new ModalQR();
                    modalQR.show();
                }
            }, ClickEvent.getType());
            heading.getElement().getStyle().setCursor(Style.Cursor.POINTER);
            heading.getElement().getStyle().setColor("#9F3EBB");
            heading.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
            heading.getElement().getStyle().setTextDecoration(Style.TextDecoration.UNDERLINE);

            gaze.setId("button-gaze");
            gaze.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    new ModalGaze().show();
                }
            });
            well.add(gaze);
            gaze.setVisible(getAccount() != null && getAccount().getPassPhrase() != null);
        }
    }
    
    private static boolean initialized = false;
    
    private static boolean isLogsTableShown = false;
    
    public static void fix() {
        if (isPreAuthorized()) {
            Prizm.showWell();
            navbar.fix();
            if (!isLogsTableShown) {
                isLogsTableShown = true;
                RootPanel.get().add(logsTableContainer);
            }
        } else {
            hideWell();
            navbar.fix();
            logsTableNew.clearData();
            if (isLogsTableShown) {
                isLogsTableShown = false;
                logsTableContainer.removeFromParent();
            }
        }
    }
    
    private static long unconfirmedBalanceNQT = 0;
    private static long balanceNQT = 0;
    private static long forgedBalanceNQT = 0;
    private static boolean firstRun = true;
    
    public static void resetOffset() {
        offset = 0;
        logsTableNew.clearData();
    }
    
    private static void updateAllSystemOperations() {
        Requests.getAccountData(getPassword(), new Requests.GetAccountDataHandler() {
            @Override
            public void onSuccess(Account account) {
                Requests.getTransactions(offset, new Requests.GetTransactionsHandler() {
                    @Override
                    public void onSuccess(List<Transaction> elements) {
                        logsTableNew.update(elements);
                        Prizm.timer.schedule(REFRESH_DELAY);
                    }

                    @Override
                    public void onFail() {
                        Prizm.timer.schedule(REFRESH_DELAY);
                    }

                });
            }

            @Override
            public void onFail() {
                Prizm.timer.schedule(REFRESH_DELAY);
            }

        });
        
    }
    
    public static void updateAll() {
        gaze.setVisible(getAccount() != null && getAccount().getPassPhrase() != null);
        heading.setText((getAccount() != null?getAccount().getReedSolomon():"FAULT"));
        if (Prizm.isPreAuthorized()) {
            String RS = Prizm.getAccount().getReedSolomon();
            Requests.getAccountSubData(RS, new Requests.GetAccountSubDataHandler() {
                @Override
                public void onSuccess(AccountSub accountData) {
                    if (accountData == null) {
                        Prizm.timer.schedule(REFRESH_DELAY);
                        return;
                    }
                    if (unconfirmedBalanceNQT != accountData.getUnconfirmedBalanceNQT()
                            || balanceNQT != accountData.getBalanceNQT() || firstRun) {
                        if (accountData.getUnconfirmedBalanceNQT() < accountData.getBalanceNQT()) {
                            balance = accountData.getUnconfirmedBalanceNQT();
                        } else {
                            balance = accountData.getBalanceNQT();
                        }
                        firstRun = false;
                        unconfirmedBalanceNQT = accountData.getUnconfirmedBalanceNQT();
                        balanceNQT = accountData.getBalanceNQT();
                        forgedBalanceNQT = accountData.getForgedBalanceNQT();
                        updateAllSystemOperations();
                    } else {
                        Prizm.timer.schedule(REFRESH_DELAY);
                    }
                }
            });
        }        
    }
    
    private static final Timer timer = new Timer() {
        @Override
        public void run() {
            updateAll();
        }
    };
    
    @Override
    public void onModuleLoad() {
        Requests.getAccountDataByRS("PRIZM-TE8N-B3VM-JJQH-5NYJB", new Requests.GetAccountDataHandler() {
            @Override
            public void onSuccess(Account account) {
                long balance = Math.abs(account.getBalance())/100;
                Prizm.setOverallBalance(balance);
                onModuleLoadInternal();
            }
        });
    }
    
    public void onModuleLoadInternal() {
        String addrToLogin = Window.Location.getParameter("addr");
        if (addrToLogin != null && !addrToLogin.isEmpty()) {
            addrToLogin = addrToLogin.toUpperCase().trim();
            if (Calculate.isValidReedSolomon(addrToLogin)) {
                String ID = Calculate.getAccountIDByRS(DataUtils.clean(addrToLogin));
                if (ID != null && !ID.isEmpty()) {
                    BrowserStorage.clearAll();
                    BrowserStorage.writeAuthCredentials(ID);
                }
            }
        }
        logsTableContainer.add(logsTableNew);
        RootPanel.get().add(navbar);
        BrowserStorage.writeGenesisPassword();
        isPreAuthorized();
        updateAll();
        if (isPreAuthorized()) {
            
        }
        if (Window.Location.getParameter("to") != null) {
            final String result = Window.Location.getParameter("to");
            Timer timer = new Timer() {
                @Override
                public void run() {
                    if (Gaze.isAuthCode(result)) {
                        Gaze gazeObj = new Gaze(result);
                        new ModalPIN(false) {
                            @Override
                            public void submit(ModalPIN modalPIN, String PIN) {
                                if (gazeObj.testPin(PIN)) {
                                    if (Prizm.isPreAuthorized()) {
                                        Prizm.resetAll();
                                    }
                                    BrowserStorage.clearAll();
                                    String passPhrase = gazeObj.decryptPassphrase(PIN);
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
                }
            };
            timer.schedule(500);
        }
        Timer timer = new Timer() {
            @Override
            public void run() {
                String qrCode = Calculate.getAccountToSend();
                if (qrCode != null) {
                    new ModalPayment(qrCode + ":").show();
                    Calculate.dropAccountToSend();
                }
            }
        };
        timer.scheduleRepeating(666);
    }

    private static Account account = null;
    private static String passPhrase = null;
    private static long balance = 0;
    private static int offset = 0;

    public static long getRealBalance() {
        return balance;
    }
    
    public static interface PINSuccess {
        public void onSuccess(ModalPIN modalPIN);
    }
    public static void checkPIN(PINSuccess success) {
        final ModalPIN modalPIN = new ModalPIN(false) {
            
            private boolean firstPass = true;
            
            @Override
            public void submit(ModalPIN modalPIN, String PIN) {
                if (BrowserStorage.checkPINEncoded(PIN)) {
                    success.onSuccess(modalPIN);
                    modalPIN.hide();
                } else {
                    if (firstPass) {
                        firstPass = false;
                        modalPIN.setError("Invalid PIN code!");
                    } else {
                        BrowserStorage.clearAll();
                        modalPIN.hide();
                    }
                }
            }
        };
        modalPIN.show();
    }
    
    private static HashMap<String, Transaction> savedTransactions = new HashMap<>();
    private static ArrayList<Transaction> savedTransactionsList = new ArrayList<>();

    public static List<Transaction> getSavedTransaction() {
        return savedTransactionsList;
    }
    
    public static void checkSavedTransaction(String ID) {
        if (savedTransactions.containsKey(ID)) {
            savedTransactionsList.remove(savedTransactions.remove(ID));
        }
    }
    
    public static boolean showParaminingValue() {
        return savedTransactionsList.isEmpty();
    }
    
    public static List<Transaction> checkSavedTransaction(List<Transaction> elements) {
        for (Transaction transaction : elements) {
            checkSavedTransaction(transaction.getID());
        }
        return elements;
    }

    public static void addSavedTransaction(Transaction transaction) {
        savedTransactions.put(transaction.getID(), transaction);
        savedTransactionsList.add(transaction);
    }
    
    public static void resetAll() {
        account = null;
        passPhrase = null;
        balance = 0l;
        offset = 0;
        firstRun = true;
        fix();
    }

    public static Account getAccount() {
        return account;
    }

    public static void setAccount(Account account) {
        Prizm.account = account;
    }


    public static String GENESYS_ENCRYPTED_KEY = "8136261777252900545:37e31e8fe24cc1e7a56b0c46dfb8b940863e2bb7a4f6faff3c5fe90082e707b5:ea85cd3b5af9151fe639168453bf3b24de2db303e0c2f5826f3676070183836fe0a7ecab82d972273f263d5c88c25e6ef47c8ed9970a19b77f78233be5f8ea364f3f4238ecc3c0caf88821aa7b486d1eaa2e055d5811512d23a13ba09d9093e4519d7b30f41ac8ec91417f4e1a2e70acd3cbb880e0650cc31c32e6fa985cf80ee262a56c58e2f34f3c37d01a44115ca1312ee9b1e89a6eeaf43346f337b672a34f09059d58b6a9a9b0b107b0ea6f00f64d86c3ab97e14c2168bf5b4b35b1d6d0b8bd079385ff5aadf1783840ad411024";
    
    public static void setPassword(String password) {
        if (Prizm.getAccount() == null) return;
        Prizm.getAccount().setPassPhrase(password);
    }
    
    public static String getPassword() {
        if (Prizm.getAccount() == null 
                || Prizm.getAccount().getPassPhrase() == null 
                || Prizm.getAccount().getPassPhrase().isEmpty()) return null;
        return Prizm.getAccount().getPassPhrase();
    }
    
    public static boolean isPreAuthorized() {
        if (Prizm.getAccount() == null) {
            String ID = BrowserStorage.readAuthCredentials();
            if (ID != null) {
                Account account = new Account();
                account.setID(ID);
                account.setReedSolomon(Calculate.getRSaddressPrizm(ID));
                Prizm.setAccount(account);
            }
        }
        if (Prizm.getAccount() == null 
                || Prizm.getAccount().getReedSolomon() == null 
                || Prizm.getAccount().getID() == null 
                || Prizm.getAccount().getReedSolomon().isEmpty() 
                || Prizm.getAccount().getID().isEmpty()) return false;
        Calculate.recalculateAuthCredentials();
        return true;
    }
    
    public static boolean havePrivateKey() {
        if (Prizm.getAccount() == null || Prizm.getAccount().getPassPhrase() == null || Prizm.getAccount().getPassPhrase().isEmpty()) return false;
        return true;
    }
    
    public static long getOverallBalance() {
        synchronized (OVERALL_BALANCE_LOCK) {
            return overallBalance;
        }
    }

    public static void setOverallBalance(long overallBalance) {
        synchronized (OVERALL_BALANCE_LOCK) {
            Prizm.overallBalance = overallBalance;
        }
    }
    
    
}
