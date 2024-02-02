package prizm.client.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class TrxOuter {
    List<Trx> transactions;

    public List<Trx> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Trx> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "TrxOuter{" + "transactions=" + transactions + '}';
    }
}
