package ra.rta.models;

import java.io.Serializable;

public abstract class Entity implements Serializable {

	static final long serialVersionUID = 1L;

	protected Partner partner;
	protected String save;

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

    public String getSave() {
        return save;
    }

    public void setSave(String save) {
        this.save = save;
    }
}
