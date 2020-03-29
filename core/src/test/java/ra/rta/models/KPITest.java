package ra.rta.models;

import org.junit.Test;

import java.util.UUID;

public class KPITest extends BaseKryoTest<KPI> {

	@Test
	public void testKryo() throws Exception {
		KPI in = new KPI();
		in.setActive(true);
		in.setAdId(UUID.randomUUID());
        in.setTermcode(123456);
		testKryo(in);
	}

}
