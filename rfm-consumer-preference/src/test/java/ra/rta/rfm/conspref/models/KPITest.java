package ra.rta.rfm.conspref.models;

import org.junit.Test;
import ra.rta.models.KPI;

import java.util.UUID;

public class KPITest extends BaseKryoTest<ra.rta.models.KPI> {

	@Test
	public void testKryo() throws Exception {
		ra.rta.models.KPI in = new KPI();
		in.setActive(true);
		in.setAdId(UUID.randomUUID());
        in.setTermcode(123456);
		testKryo(in);
	}

}
