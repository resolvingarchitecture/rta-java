package ra.rta.rfm.conspref.models;

import org.junit.Test;

public class ClusterTest extends BaseKryoTest<Cluster> {

	@Test
	public void testKryo() throws Exception {
		Cluster in = new Cluster();
		in.setActive(true);
		in.setCore("ibs");
		testKryo(in);
	}
}
