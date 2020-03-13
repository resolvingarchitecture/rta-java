package ra.rta.models;

import java.util.*;

public abstract class Account extends Entity implements CustomerRelated {

    static final long serialVersionUID = 1L;

    private Customer customer;

    private UUID uuid;
    private String aId;
    private String hId;
    private Relationship reltype;
    private Date processDate;

    @Override
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getaId() {
        return aId;
    }

    public void setaId(String aId) {
        this.aId = aId;
    }

    public Boolean getCustomerAssociated() {
        return customer != null && customer.getAdId() != null;
    }

    public String gethId() {
        return hId;
    }

    public void sethId(String hId) {
        this.hId = hId;
    }

    public Relationship getReltype() {
        return reltype;
    }

    public void setReltype(Relationship reltype) {
        this.reltype = reltype;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }
}
