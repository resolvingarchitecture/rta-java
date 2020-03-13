package ra.rta.models;

import org.junit.Test;

public class KPIPartnerSummaryTest extends BaseKryoTest<KPIPartnerSummary> {

	@Test
	public void testKryo() throws Exception {
		KPIPartnerSummary in = new KPIPartnerSummary();
		in.setDate(20151005);
		in.setTermcode(1);
		testKryo(in);
	}
}
