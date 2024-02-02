package prizm.client.pojo;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.io.Serializable;
import prizm.client.Prizm;
import prizm.client.calc.Calculator;
import prizm.client.service.Epoch;

public class Account implements Serializable, IsSerializable {

    private String passPhrase;
    private String ID;
    private String publicKey;
    private String reedSolomon;
    private long balance = 0;
    private long amount = 0;
    private double multiplier;
    private int last;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getReedSolomon() {
        return reedSolomon;
    }

    public void setReedSolomon(String reedSolomon) {
        this.reedSolomon = reedSolomon;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public long getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }

    public String getPassPhrase() {
        return passPhrase;
    }

    public void setPassPhrase(String passPhrase) {
        this.passPhrase = passPhrase;
    }
    
    public double getGoodBalance() {
        return getBalance()/100d;
    }
    
    public double getGoodGrow() {
        double days = (Epoch.currentGlide()- (long)(getLast()*1000)) / 86400000d;
        return getGoodBalance() * (days * this.multiplier);

    }

    public double getGoodGrowPlus() {
        double seconds = (Epoch.currentGlide()- (double)getLast()*1000d) / 1000d;
        Calculator calc = Calculator.calculate(Prizm.getOverallBalance(), getBalance(), getAmount(), seconds, true);
        return (calc.getAmount()/100d);
    }

    @Override
    public String toString() {
        return "Account{" + "passPhrase=" + passPhrase + ", ID=" + ID + ", publicKey=" + publicKey + ", reedSolomon=" + reedSolomon + ", balance=" + balance + ", amount=" + amount + ", multiplier=" + multiplier + ", last=" + last + '}';
    }
}
