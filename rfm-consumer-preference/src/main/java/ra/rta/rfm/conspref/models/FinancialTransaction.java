package ra.rta.rfm.conspref.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.models.KPI;

import java.util.*;

public class FinancialTransaction {

	static Logger LOG = LoggerFactory.getLogger(FinancialTransaction.class);

	@SuppressWarnings("hiding")
	static final long serialVersionUID = 1L;

	public enum Status {
		Unknown, Pending, Processed, Other, Authorized, Rejected, Hold, Suspended
	}

    public enum Type {
        Unknown, Debit, Credit, Interest, NonSufficientFundsFee, EstatementFee, MiscellaneousFee,
        FeeReversal, Waived, Float, System, NonPost, DepositReversal, Return, TruthInSavings
    }

    public enum Category {
        Unknown, Check, ACH, OverdraftFee, OnlineBillpay, Mobile, POS, ATM, Teller
    }

	public LinkedHashSet<KPI> kpis = new LinkedHashSet<>();

	public long id;
	public long individualId;
	public Date date;
	public Status status = Status.Unknown;
	public Type type = Type.Unknown;
	public Category category = Category.Unknown;
	public Date postDate;
	public Date processDate;
	public Double amount = 0.00;
	public String memo;
	public String payee;
	public boolean suspended = false;

}
