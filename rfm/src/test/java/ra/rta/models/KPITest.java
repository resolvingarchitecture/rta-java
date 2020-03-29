package ra.rta.models;

import org.junit.Test;

import java.util.UUID;

public class KPITest extends BaseKryoTest<RFMKPI> {

	@Test
	public void testKryo() throws Exception {
		RFMKPI in = new RFMKPI();
		in.setActive(true);
		in.setAdId(UUID.randomUUID());
        in.setTermcode(123456);
		testKryo(in);
	}

}
