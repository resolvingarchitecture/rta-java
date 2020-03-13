package ra.rta.models;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Date;

public class CustomerInteraction extends Entity {

	@SuppressWarnings("hiding")
	static final long serialVersionUID = 1L;

	public enum Type {
		BalanceInquiry(1),
        PaymentInquiry(2),
        AddressChange(3),
        AccountOpening(4),
        AccountClosing(5),
        Other(6);
		private int id;
		Type(int id){this.id = id;}
		public int getId(){return id;}
	}

	public enum Channel {
		InboundCall(1),
        OutboundCall(2),
        ATM(3),
        Teller(4),
        Platform(5),
        Collections(6),
        MobileBanking(7),
		OnlineBanking(8),
        Social(9),
        Email(10),
        Mail(11),
        Text(12),
        Other(13);
		private int id;
		Channel(int id){this.id = id;}
		public int getId(){return id;}
	}

    private Customer customer;
    private String ucId;
    private String adId;
    private String uhId;
    private String uaId;
    private String reltype;
	private Type type;
	private Channel channel;
    private Date dateTime;
    private String memo;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getUcId() {
        return ucId;
    }

    public void setUcId(String ucId) {
        this.ucId = ucId;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getUhId() {
        return uhId;
    }

    public void setUhId(String uhId) {
        this.uhId = uhId;
    }

    public String getUaId() {
        return uaId;
    }

    public void setUaId(String uaId) {
        this.uaId = uaId;
    }

    public String getReltype() {
        return reltype;
    }

    public void setReltype(String reltype) {
        this.reltype = reltype;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
