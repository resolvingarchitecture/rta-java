package ra.rta.models;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Key Performance Indicator
 */
public final class KPI extends Entity implements Cloneable {

	private static final long serialVersionUID = 1L;

    public enum Type {Unknown, Flag, Derived, Competitive, NonCompetitive}

	private KPICustomerSummary KPICustomerSummary;
	private KPIPartnerSummary KPIPartnerSummary;

	private UUID adId;
	private String uaId;
	private int termcode;
	private Date date;
    private Type type = Type.Unknown;
	private Integer recency;
	private Integer recencyScore = 0;
	private Integer frequency = 0;
	private Integer frequencyScore = 0;
	private Double monetary = 0.0;
	private Integer monetaryScore = 0;
	private Integer windowDays;
	private Date determinationDate;
	private String description;
	private Boolean active = false;

	public KPI() {
		// no-arg
	}

	public KPI(int termcode) {
		this.termcode = termcode;
	}

	public KPICustomerSummary getKPICustomerSummary() {
		return KPICustomerSummary;
	}

	public void setKPICustomerSummary(KPICustomerSummary KPICustomerSummary) {
		this.KPICustomerSummary = KPICustomerSummary;
	}

	public KPIPartnerSummary getKPIPartnerSummary() {
		return KPIPartnerSummary;
	}

	public void setKPIPartnerSummary(KPIPartnerSummary KPIPartnerSummary) {
		this.KPIPartnerSummary = KPIPartnerSummary;
	}

	public UUID getAdId() {
		return adId;
	}

	public void setAdId(UUID adId) {
		this.adId = adId;
	}

	public String getUaId() {
		return uaId;
	}

	public void setUaId(String uaId) {
		this.uaId = uaId;
	}

	public int getTermcode() {
		return termcode;
	}

	public void setTermcode(int termcode) {
		this.termcode = termcode;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Integer getRecency() {
		return recency;
	}

	public void setRecency(Integer recency) {
		this.recency = recency;
	}

	public Integer getRecencyScore() {
		return recencyScore;
	}

	public void setRecencyScore(Integer recencyScore) {
		this.recencyScore = recencyScore;
	}

	public Integer getFrequency() {
		return frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	public Integer getFrequencyScore() {
		return frequencyScore;
	}

	public void setFrequencyScore(Integer frequencyScore) {
		this.frequencyScore = frequencyScore;
	}

	public Double getMonetary() {
		return monetary;
	}

	public void setMonetary(Double monetary) {
		this.monetary = monetary;
	}

	public Integer getMonetaryScore() {
		return monetaryScore;
	}

	public void setMonetaryScore(Integer monetaryScore) {
		this.monetaryScore = monetaryScore;
	}

	public Integer getWindowDays() {
		return windowDays;
	}

	public void setWindowDays(Integer windowDays) {
		this.windowDays = windowDays;
	}

	public Date getDeterminationDate() {
		return determinationDate;
	}

	public void setDeterminationDate(Date determinationDate) {
		this.determinationDate = determinationDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	@Override
	public int hashCode() {
		return termcode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		KPI other = (KPI) obj;
		return Objects.equals(termcode, other.termcode);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		super.clone();
		KPI kpi = new KPI(termcode);
		kpi.setActive(active);
		kpi.setAdId(adId);
		kpi.setDate(date);
		kpi.setDescription(description);
		kpi.setDeterminationDate(determinationDate);
		kpi.setFrequency(frequency);
		kpi.setFrequencyScore(frequencyScore);
		kpi.setKPICustomerSummary(KPICustomerSummary);
		kpi.setKPIPartnerSummary(KPIPartnerSummary);
		kpi.setMonetary(monetary);
		kpi.setMonetaryScore(monetaryScore);
		kpi.setRecency(recency);
		kpi.setRecencyScore(recencyScore);
		kpi.setType(type);
		kpi.setUaId(uaId);
		kpi.setWindowDays(windowDays);
		if(super.partner!=null) {
			Partner partner = new Partner();
			partner.setName(super.partner.getName());
			kpi.setPartner(partner);
		}
		kpi.setSave(getSave());
		return kpi;
	}
}
