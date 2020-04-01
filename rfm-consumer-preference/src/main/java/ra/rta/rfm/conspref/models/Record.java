package ra.rta.rfm.conspref.models;

import ra.rta.EventException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class Record implements Serializable {

	static final long serialVersionUID = 1L;

	public String raw;
	public int gId;
	public Group group;
	public int cId;
	public Customer customer;
	public FinancialTransaction trx;
	public List<EventException> eventErrors = new ArrayList<>();
	public Integer tried = 0;

}
