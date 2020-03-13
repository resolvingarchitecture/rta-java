package ra.rta.models;

import java.util.*;

public abstract class Account extends Entity implements CustomerRelated {

    static final long serialVersionUID = 1L;

    private Customer customer;

    private UUID uuid;
    private String uaId;
    private String haId;
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

    public String getUaId() {
        return uaId;
    }

    public void setUaId(String uaId) {
        this.uaId = uaId;
    }

    public Boolean getCustomerAssociated() {
        return customer != null && customer.getAdId() != null;
    }

    public String getHaId() {
        return haId;
    }

    public void setHaId(String haId) {
        this.haId = haId;
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
