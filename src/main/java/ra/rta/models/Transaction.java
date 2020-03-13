package ra.rta.models;

import ra.rta.models.utilities.Scrubber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class Transaction extends Entity {

	static Logger LOG = LoggerFactory.getLogger(Transaction.class);

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

    // Currently multiple accounts can be associated with a Transaction although each will have the same UAIC yet different ADICs.
	protected Set<Account> accounts = new HashSet<>();
	private LinkedHashSet<KPI> KPIS = new LinkedHashSet<>();

    // One UAIC per Transaction
    private UUID id;
    private String uaId;
	private Date date;
	private UUID adId;
	private String uhId;
	private Status status = Status.Unknown;
    private Type type = Type.Unknown;
    private Category category = Category.Unknown;
	private Date postDate;
	private Date processDate;
	private Double amount = 0.00;
	private String memo;
	private String payee;
    private boolean suspended = false;

	public Set<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(Set<Account> accounts) {
		this.accounts = accounts;
	}

	public LinkedHashSet<KPI> getKPIS() {
		return KPIS;
	}

	public void setKPIS(LinkedHashSet<KPI> KPIS) {
		this.KPIS = KPIS;
	}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAdId() {
		return adId;
	}

	public void setAdId(UUID adId) {
		this.adId = adId;
	}

	public String getUhId() {
		return uhId;
	}

	public void setUhId(String uhId) {
		this.uhId = Scrubber.trim(uhId);
	}

	public String getUaId() {
		return uaId;
	}

	public void setUaId(String uaId) {
		this.uaId = Scrubber.trim(uaId);
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getProcessDate() {
		return processDate;
	}

	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = Scrubber.trim(memo);
	}

	public String getPayee() {
		return payee;
	}

	public void setPayee(String payee) {
		this.payee = Scrubber.scrub(payee);
	}

    public boolean getSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }
}
