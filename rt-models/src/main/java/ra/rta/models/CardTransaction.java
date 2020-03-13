package ra.rta.models;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 *
 */
public class CardTransaction extends Transaction {

	@SuppressWarnings("hiding")
	static final long serialVersionUID = 1L;

	public enum Type {
		OverLimitFee(1), LateFee(2), AnnualFee(3), OtherFee(4), Interest(5), Payment(6), Cash(7), Purchase(8),
		BalanceTranser(9), Check(10), Return(11), PaymentReversal(12), FeeWaiver(13);
		private int id;
		Type(int id) {this.id = id;}
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
