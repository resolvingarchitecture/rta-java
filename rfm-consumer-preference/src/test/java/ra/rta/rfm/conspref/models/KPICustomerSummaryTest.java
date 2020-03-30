package ra.rta.rfm.conspref.models;

import org.junit.Test;

import java.util.UUID;

public class KPICustomerSummaryTest extends BaseKryoTest<KPICustomerSummary> {

	@Test
	public void testKryo() throws Exception {
		KPICustomerSummary in = new KPICustomerSummary();
		in.setAdId(UUID.randomUUID());
		in.setDate(20151005);
		testKryo(in);
	}

}
