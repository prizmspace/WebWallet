package prizm.client.service;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import java.util.List;
import prizm.client.Prizm;
import prizm.client.json.AccountSub;
import prizm.client.json.EncryptedMessage;
import prizm.client.json.ParaMetrics;
import prizm.client.json.SendMoney;
import prizm.client.json.SendMoneyResponse;
import prizm.client.json.SendMoneySubmit;
import prizm.client.json.TrxOuter;
import prizm.client.pojo.Account;
import prizm.client.pojo.Transaction;

public class Requests {
        
    private static final String REQUEST_URL = "/prizm";

    public static interface TrxOuterMapper extends ObjectMapper<TrxOuter> {};
    
    public static abstract class GetTransactionsHandler {
        public abstract void onSuccess(List<Transaction> elements);
        public void onFail() {
        }
    }
    
    private static int ITEMS_PER_PAGE = 500;
    
    public static void getTransactions(int offsetIn, GetTransactionsHandler callback) {
        int offset = offsetIn * ITEMS_PER_PAGE;
        PostRequest postRequest = new PostRequest(REQUEST_URL) {
            @Override
            public void onSuccess(String result) {
                TrxOuterMapper mapper = GWT.create(TrxOuterMapper.class);
                TrxOuter trxOuter = mapper.read(result);
                if (trxOuter != null && trxOuter.getTransactions() != null) {
                    callback.onSuccess(Transaction.parse(trxOuter.getTransactions()));
                    return;
                }
                callback.onFail();
            }

            @Override
            public void onError() {
                callback.onFail();
            }

        };
        if (Prizm.getAccount() == null || Prizm.getAccount().getID() == null) {
            callback.onFail();
        }
        postRequest.getValues().put("requestType", "getBlockchainTransactions");
        postRequest.getValues().put("account", Prizm.getAccount().getID());
        postRequest.getValues().put("firstIndex", ""+offset);
        postRequest.getValues().put("lastIndex", ""+(offset+(ITEMS_PER_PAGE-1)));
        postRequest.action();
    }
    
    public static interface ParaMetricsMapper extends ObjectMapper<ParaMetrics> {}

    public static abstract class GetAccountDataHandler {
        public abstract void onSuccess(Account account);
        public void onFail() {
        }
    }
    
    public static void getAccountDataByRS(final String accountRS, final GetAccountDataHandler handler) {
        GetRequest postRequest = new GetRequest(REQUEST_URL) {
            @Override
            public void onSuccess(String result) {
                Account account = new Account();
                ParaMetricsMapper mapper = GWT.create(ParaMetricsMapper.class);
                ParaMetrics metrics = mapper.read(result);

                if (metrics == null) {
                    return;
                }
                account.setBalance(metrics.getBalance());
                account.setAmount(metrics.getAmount());
                account.setLast(metrics.getLast());
                account.setMultiplier(metrics.getMultiplier());
                if (handler != null) handler.onSuccess(account);
            }

            @Override
            public void onError() {
                Prizm.setAccount(null);
                Prizm.fix();
                if (handler != null) handler.onFail();
            }
        };
        postRequest.getValues().put("requestType", "getPara");
        postRequest.getValues().put("account", accountRS);
        postRequest.action();
    }

    
    public static void getAccountData(final String passPhrase, final GetAccountDataHandler handler) {
        GetRequest postRequest = new GetRequest(REQUEST_URL) {
            @Override
            public void onSuccess(String result) {
                Account account = new Account();
                ParaMetricsMapper mapper = GWT.create(ParaMetricsMapper.class);
                ParaMetrics metrics = mapper.read(result);

                if (metrics == null) {
                    Prizm.setAccount(null);
                    Prizm.fix();
                    return;
                }

                if (passPhrase != null && !passPhrase.isEmpty()) {
                    account.setPublicKey(Calculate.getPublicKeyPrizm(passPhrase));
                    account.setID(Calculate.getAccountID(account.getPublicKey()));
                    account.setReedSolomon(Calculate.getRSaddressPrizm(account.getID()));
                    account.setBalance(metrics.getBalance());
                    account.setAmount(metrics.getAmount());
                    account.setLast(metrics.getLast());
                    account.setMultiplier(metrics.getMultiplier());
                    account.setPassPhrase(passPhrase);                    
                } else {
                    account.setID(Prizm.getAccount().getID());
                    account.setReedSolomon(Prizm.getAccount().getReedSolomon());
                    account.setBalance(metrics.getBalance());
                    account.setAmount(metrics.getAmount());
                    account.setLast(metrics.getLast());
                    account.setMultiplier(metrics.getMultiplier());
                    account.setPassPhrase(null);
                    account.setPublicKey(null);
                }

                Prizm.setAccount(account);
                Prizm.fix();
                if (handler != null) handler.onSuccess(account);
            }

            @Override
            public void onError() {
                Prizm.setAccount(null);
                Prizm.fix();
                if (handler != null) handler.onFail();
            }
        };
        postRequest.getValues().put("requestType", "getPara");
        postRequest.getValues().put("account", Prizm.getAccount().getID());
        postRequest.action();
    }
        
    public static abstract class SendMoneyHandler {
        public abstract void onSuccess(SendMoney sendMoneyData);
        public void onFail() {
        }
    }
    
    public static interface SendMoneyMapper extends ObjectMapper<SendMoney> {}
    
    public static void sendMoney(String recipient, String recipientPublicKey, String message, long amountNQT, boolean encrypted, String password, final SendMoneyHandler handler) {
        final String myPublicKey = Calculate.getPublicKeyPrizm(Prizm.getPassword() != null ? Prizm.getPassword() : password);
        PostRequest postRequest = new PostRequest(REQUEST_URL+"?requestType=sendMoney") {
            @Override
            public void onSuccess(String result) {
                SendMoneyMapper mapper = GWT.create(SendMoneyMapper.class);
                SendMoney sendMoney = mapper.read(result);
                if (handler != null) handler.onSuccess(sendMoney);
            }

            @Override
            public void onError() {
                if (handler != null) handler.onFail();
            }  
        };
        postRequest.getValues().put("deadline", "1440");
        postRequest.getValues().put("amountNQT", "" + amountNQT);
        postRequest.getValues().put("feeNQT", "" + 5);
        postRequest.getValues().put("messageToEncryptIsText", "true");
        postRequest.getValues().put("messageToEncryptToSelfIsText", "true");
        postRequest.getValues().put("permanent_message", "1");
        postRequest.getValues().put("phased", "false");
        postRequest.getValues().put("phasingHashedSecret", "");
        postRequest.getValues().put("phasingHashedSecretAlgorithm", "2");
        postRequest.getValues().put("phasingLinkedFullHash", "");
        postRequest.getValues().put("publicKey", Prizm.getAccount().getPublicKey()!=null ? Prizm.getAccount().getPublicKey() : myPublicKey);
        postRequest.getValues().put("recipient", recipient);
        postRequest.getValues().put("recipientPublicKey", recipientPublicKey);
        
        EncryptedMessage transactionMessage = Calculate.encryptMessage(message, Calculate.getAccountID(recipientPublicKey), recipientPublicKey, Prizm.getPassword() != null ? Prizm.getPassword() : password);
        EncryptedMessage transactionToMeMessage = Calculate.encryptMessage(message, Prizm.getAccount().getID(), myPublicKey, Prizm.getPassword() != null ? Prizm.getPassword() : password);
        
        postRequest.getValues().put("encryptToSelfMessageData", transactionToMeMessage.getData());
        postRequest.getValues().put("encryptToSelfMessageNonce", transactionToMeMessage.getNonce());
        postRequest.getValues().put("encryptedMessageData", transactionMessage.getData());
        postRequest.getValues().put("encryptedMessageNonce", transactionMessage.getNonce());        
        postRequest.action();
    }
    
    public static abstract class SendMoneySubmitHandler {
        public abstract void onSuccess(SendMoneyResponse sendMoneySubmitData);
        public void onFail() {
        }
    }
    
    public static interface SendMoneyResponseMapper extends ObjectMapper<SendMoneyResponse> {};
    
    public static void submitPayment(final SendMoneySubmit submitData, SendMoneySubmitHandler handler) {
        
        PostRequest postRequest = new PostRequest(REQUEST_URL+"?requestType=broadcastTransaction") {
            @Override
            public void onSuccess(String result) {
                SendMoneyResponseMapper mapper = GWT.create(SendMoneyResponseMapper.class);
                if (handler != null && result != null) handler.onSuccess(mapper.read(result));
            }

            @Override
            public void onError() {
                if (handler != null) handler.onFail();
            }  
        };
        postRequest.getValues().put("prunableAttachmentJSON", submitData.getPrunableAttachmentJSON());
        postRequest.getValues().put("transactionBytes", submitData.getTransactionBytes());
        postRequest.action();
    }
    
    public static abstract class GetAccountSubDataHandler {
        public abstract void onSuccess(AccountSub accountData);
        public void onFail() {
        }
    }
    
    public static interface GetAccountSubDataMapper extends ObjectMapper<AccountSub> {};
    
    public static void getAccountSubData(final String account, GetAccountSubDataHandler handler) {        
        PostRequest postRequest = new PostRequest(REQUEST_URL+"?requestType=getAccount") {
            @Override
            public void onSuccess(String result) {
                GetAccountSubDataMapper mapper = GWT.create(GetAccountSubDataMapper.class);
                if (handler != null) handler.onSuccess(mapper.read(result));
            }

            @Override
            public void onError() {
                if (handler != null) handler.onFail();
            }  
        };
        postRequest.getValues().put("account", account);
        postRequest.action();
    }
    
}
