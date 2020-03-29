package ra.rta.rfm.conspref.models;

public class ExactMatchFailure {

    public String tradename;
    public String type;
    public String vehicle;
    public int count = 0;
    public String firstSeen;
    public String lastSeen;
    public boolean posted;

    public ExactMatchFailure(String tradename) {
        this.tradename = tradename;
    }

}
