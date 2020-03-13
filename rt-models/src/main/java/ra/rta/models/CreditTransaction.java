package ra.rta.models;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 *
 */
public class CreditTransaction extends Transaction {

	@SuppressWarnings("hiding")
	static final long serialVersionUID = 1L;

	public enum Type {
		LateFee(2), OtherFee(4), Interest(5), Payment(6), PaymentReversal(12), FeeWaiver(13);
		private int id;
		Type(int id){this.id = id;}
		public int getId() {return id;}
	}

	private Type type;

	private String category;
	private Integer checkNumber;

	private String fundAccount;
	private String mrktingCode;
	private String zipCode;
	private String merchantId;
	private String sicCode;
	private String naicsCode;
	private String mcc;
	private Double intrchgRate;
	private String currencyId;
	private Double currencyConvRate;

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
