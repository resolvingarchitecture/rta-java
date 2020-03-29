package ra.rta.rfm.conspref.models;

import org.junit.Test;

import java.util.UUID;

public class KPICustomerSummaryTest extends BaseKryoTest<KPIIndividualSummary> {

	@Test
	public void testKryo() throws Exception {
		KPIIndividualSummary in = new KPIIndividualSummary();
		in.setAdId(UUID.randomUUID());
		in.setDate(20151005);
		testKryo(in);
	}

}
