package ra.rta.rfm.conspref.models;

import org.junit.Test;

public class KPIClusterSummaryTest extends BaseKryoTest<KPIGroupSummary> {

	@Test
	public void testKryo() throws Exception {
		KPIGroupSummary in = new KPIGroupSummary();
		in.setDate(20151005);
		in.setTermcode(1);
		testKryo(in);
	}
}