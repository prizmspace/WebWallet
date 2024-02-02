package prizm.client.calc;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.io.Serializable;
import java.util.Date;

public class Calculator implements IsSerializable,Serializable
{   
    private double amount = 0d;
    private boolean compound = false;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isCompound() {
        return compound;
    }

    public void setCompound(boolean compound) {
        this.compound = compound;
    }
    
    public static Calculator calculate(long overall, long balance, long structure, double seconds, boolean compoundInterest) {
        ParaMetrics paraMetrics = new ParaMetrics();
        paraMetrics.setBeforeStamp(0);
        paraMetrics.setAfterStamp(seconds);
        paraMetrics.setAmount(structure);
        paraMetrics.setBalance(balance);
        ParaMetrics.setParataxPercent(paraMetrics, compoundInterest ? 900000 : 800000, overall*100, true);
        boolean compound = false;
        if (compoundInterest) {
            compound = paraMetrics.calculateCompoundInterest();
        } else {
            paraMetrics.calculateOrdinaryInterest();
        }
        double payout = paraMetrics.getPayout();
        
        Calculator calc = new Calculator();
        calc.setAmount(payout);
        calc.setCompound(compound);
        return calc;
    }    
}
