package ra.rta.models;

import org.junit.Test;

public class PartnerTest extends BaseKryoTest<Partner> {

	@Test
	public void testKryo() throws Exception {
		Partner in = new Partner();
		in.setActive(true);
		in.setCore("ibs");
		testKryo(in);
	}
}
