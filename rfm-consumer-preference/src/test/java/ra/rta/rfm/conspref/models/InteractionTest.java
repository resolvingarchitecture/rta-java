package ra.rta.rfm.conspref.models;

import java.util.Date;

import org.junit.Test;

public class InteractionTest extends BaseKryoTest<CustomerInteraction> {

	@Test
	public void testKryo() throws Exception {
        CustomerInteraction in = new CustomerInteraction();
		in.setAdId("adid");
		in.setDateTime(new Date());
		testKryo(in);
	}

}
