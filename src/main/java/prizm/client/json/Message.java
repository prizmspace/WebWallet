package prizm.client.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class Message {
    private double random;
    private long inblockID;
    private int paraTax;
    private long inblockHeight;
    private long inTransactId;

    public double getRandom() {
        return random;
    }

    public void setRandom(double random) {
        this.random = random;
    }

    public long getInblockID() {
        return inblockID;
    }

    public void setInblockID(long inblockID) {
        this.inblockID = inblockID;
    }

    public int getParaTax() {
        return paraTax;
    }

    public void setParaTax(int paraTax) {
        this.paraTax = paraTax;
    }

    public long getInblockHeight() {
        return inblockHeight;
    }

    public void setInblockHeight(long inblockHeight) {
        this.inblockHeight = inblockHeight;
    }

    public long getInTransactId() {
        return inTransactId;
    }

    public void setInTransactId(long inTransactId) {
        this.inTransactId = inTransactId;
    }
    
}
