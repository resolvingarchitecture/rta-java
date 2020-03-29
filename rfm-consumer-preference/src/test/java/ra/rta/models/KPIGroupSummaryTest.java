package ra.rta.models;

import org.junit.Test;

public class KPIGroupSummaryTest extends BaseKryoTest<KPIGroupSummary> {

	@Test
	public void testKryo() throws Exception {
		KPIGroupSummary in = new KPIGroupSummary();
		in.setDate(20151005);
		in.setTermcode(1);
		testKryo(in);
	}
}
