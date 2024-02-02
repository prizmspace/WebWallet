package prizm.client.calc;


import java.io.Serializable;

public class ParaMetrics implements Serializable {
    
    public static long getParataxPercent(long genesisAmount) {
        long ams = (Math.abs(genesisAmount) * 100L) / Math.abs(Constants.MAXIMUM_PARAMINING_AMOUNT);
        if (ams > Constants.MAX_PARATAX_PERCENT) return Constants.MAX_PARATAX_PERCENT;
        if (ams < 0) return 0;
        return ams;
    }

    public static void setParataxPercent(ParaMetrics paraMetrics, int height, long genesisAmount, boolean rememberHeight) {
        paraMetrics.setGenesisEmission(genesisAmount);
        if (rememberHeight) {
            paraMetrics.setHeight(height);
        }
        if (height >= Constants.ENABLE_COMPOUND_AND_2X_PARATAX) {
            long percent = getParataxPercent(genesisAmount) * 2;
            if (percent > Constants.MAX_PARATAX_PERCENT) percent = Constants.MAX_PARATAX_PERCENT;
            paraMetrics.setParaTax(percent);
            return;
        }
        paraMetrics.setParaTax(getParataxPercent(genesisAmount));
    }


    public static double getPercentAmount(double amount, double percent) {
        return (amount * percent) / 100d;
    }

    public static double getAmountMinusPercent(double amount, double percent) {
        return amount - getPercentAmount(amount, percent);
    }    
    
    private static final double ORDINARY_DIVIDER = 86400d;
    private static final double COMPOUND_DIVIDER = 50d;

    private int height = 0;
    private double balance = 0l;
    private double amount = 0l;
    private double payout = 0l;
    private double beforeStamp = 0;
    private double afterStamp = 0;
    private double multiplier = 0;
    private long paraTax = 0l;
    private double paraTaxAmount = 0l;
    private long genesisEmission = 0l;
    
    public boolean calculateOrdinaryInterest() {
        double multi = 1d;
        double percent = 0d;
        if (balance>=100l && balance<=9999l) percent = 0.12d;
        if (balance>=10000l && balance<=99999l) percent = 0.14d;
        if (balance>=100000l && balance<=999999l) percent = 0.18d;
        if (balance>=1000000l && balance<=4999999l) percent = 0.21d;
        if (balance>=5000000l && balance<=9999999l) percent = 0.25d;
        if (balance>=10000000l && balance<=49999999l) percent = 0.28d;
        if (balance>=50000000l && balance<100000000l) percent = 0.33d;
        
        if (amount>=100000l         && amount<=999999l) multi = 2.18d;
        if (amount>=1000000l        && amount<=9999999l) multi = 2.36d;
        if (amount>=10000000l       && amount<=99999999l) multi = 2.77d;
        if (amount>=100000000l      && amount<=999999999l) multi = 3.05d;
        if (amount>=1000000000l     && amount<=9999999999l) multi = 3.36d;
        if (amount>=10000000000l    && amount<=99999999999l) multi = 3.88d;
        if (amount>=100000000000l) multi = 4.37d;

        this.multiplier = (multi * percent) / 100d;
        double days = (afterStamp - beforeStamp) / ORDINARY_DIVIDER;
        payout = balance * (days * this.multiplier);

        if (paraTax > 0) {
            paraTaxAmount = getPercentAmount(payout, paraTax);
            payout = getAmountMinusPercent(payout, paraTax);
        }
        return true;
    }

    public void decreaseAmountByGenesis() {
        double mtx = ((double)Math.abs(genesisEmission)/(double)Math.abs(Constants.MAXIMUM_PARAMINING_AMOUNT));
        if (mtx>1d) mtx = 1d;
        mtx = 1 - mtx;
        if (mtx < 0.1) mtx = 0.1;
        payout *= mtx;
    }
    
    public boolean calculateCompoundInterest() {

        boolean compound = true;
        
        setParataxPercent(this, 0, this.genesisEmission, false);
        this.calculateOrdinaryInterest();
        double ordinaryPayout = this.payout;

        setParataxPercent(this, Constants.ENABLE_COMPOUND_AND_2X_PARATAX, this.genesisEmission, false);
        this.calculateCompoundInterestInternal();
        double compoundPayout = this.payout;

        if (ordinaryPayout < compoundPayout) {
            setParataxPercent(this, 0, this.genesisEmission, false);
            this.calculateOrdinaryInterest();
            compound = false;
        }

        decreaseAmountByGenesis();
        
        if (payout > Constants.MAX_BALANCE_AFTER_PARAMINING_PAYOUT_NQT)
            payout = Constants.MAX_BALANCE_AFTER_PARAMINING_PAYOUT_NQT;

        if (payout > 0 && this.payout + this.balance > Constants.MAX_BALANCE_AFTER_PARAMINING_PAYOUT_NQT) {
            this.payout = Constants.MAX_BALANCE_AFTER_PARAMINING_PAYOUT_NQT - this.balance;
        }
                
        return compound;
    }

    private boolean calculateCompoundInterestInternal() {
        double multi = 1d;
        double percent = 0d;
        if (balance>=100l && balance<=9999l) percent = 0.12d;
        if (balance>=10000l && balance<=99999l) percent = 0.14d;
        if (balance>=100000l && balance<=999999l) percent = 0.18d;
        if (balance>=1000000l && balance<=4999999l) percent = 0.21d;
        if (balance>=5000000l && balance<=9999999l) percent = 0.25d;
        if (balance>=10000000l && balance<=49999999l) percent = 0.28d;
        if (balance>=50000000l && balance<100000000l) percent = 0.33d;
        
        if (amount>=100000l         && amount<=999999l) multi = 2.18d;
        if (amount>=1000000l        && amount<=9999999l) multi = 2.36d;
        if (amount>=10000000l       && amount<=99999999l) multi = 2.77d;
        if (amount>=100000000l      && amount<=999999999l) multi = 3.05d;
        if (amount>=1000000000l     && amount<=9999999999l) multi = 3.36d;
        if (amount>=10000000000l    && amount<=99999999999l) multi = 3.88d;
        if (amount>=100000000000l) multi = 4.37d;


        this.multiplier = ((multi * percent) / 100d) / (ORDINARY_DIVIDER/COMPOUND_DIVIDER);
        double periods = (afterStamp - beforeStamp) / COMPOUND_DIVIDER;
        payout = (balance * Math.pow(1d + this.multiplier, periods)) - balance;

        if (paraTax > 0) {
            paraTaxAmount = getPercentAmount(payout, paraTax);
            payout = getAmountMinusPercent(payout, paraTax);
        }

        if (payout < 0) payout = 0;


        return true;
    }
    
    public double getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public double getPayout() {
        return payout;
    }

    public double getBeforeStamp() {
        return beforeStamp;
    }

    public void setBeforeStamp(double beforeStamp) {
        this.beforeStamp = beforeStamp;
    }

    public double getAfterStamp() {
        return afterStamp;
    }

    public void setAfterStamp(double afterStamp) {
        this.afterStamp = afterStamp;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setPayout(long payout) {
        this.payout = payout;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public long getParaTax() {
        return paraTax;
    }

    public void setParaTax(long paraTax) {
        this.paraTax = paraTax;
    }

    public double getParaTaxAmount() {
        return paraTaxAmount;
    }

    public void setParaTaxAmount(long paraTaxAmount) {
        this.paraTaxAmount = paraTaxAmount;
    }
    
    public boolean isParaTaxed() {
        return paraTax > 0;
    }

    public long getGenesisEmission() {
        return genesisEmission;
    }

    public void setGenesisEmission(long genesisEmission) {
        this.genesisEmission = genesisEmission;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "ParaMetrics{" + "balance=" + balance + ", amount=" + amount + ", payout=" + payout + ", beforeStamp=" + beforeStamp + ", afterStamp=" + afterStamp + ", multiplier=" + multiplier + ", paraTax=" + paraTax + ", paraTaxAmount=" + paraTaxAmount + '}';
    }
}
