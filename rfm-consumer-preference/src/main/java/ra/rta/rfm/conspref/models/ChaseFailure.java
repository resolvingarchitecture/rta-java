package ra.rta.rfm.conspref.models;

public class ChaseFailure {

    private int termCode;
    private int frequency;
    private int transFrequency;
    private String termDesc;
    private String type;
    private String vehicle;
    private String firstSeen;
    private String lastSeen;
    private boolean posted;

    public ChaseFailure(int termCode) {
        this.termCode = termCode;
    }

    public int getTermCode() {
        return termCode;
    }

    public void setTermCode(int termCode) {
        this.termCode = termCode;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getTransFrequency() {
        return transFrequency;
    }

    public void setTransFrequency(int transFrequency) {
        this.transFrequency = transFrequency;
    }

    public String getTermDesc() {
        return termDesc;
    }

    public void setTermDesc(String termDesc) {
        this.termDesc = termDesc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getFirstSeen() {
        return firstSeen;
    }

    public void setFirstSeen(String firstSeen) {
        this.firstSeen = firstSeen;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean getPosted() {
        return posted;
    }

    public void setPosted(boolean posted) {
        this.posted = posted;
    }
}
