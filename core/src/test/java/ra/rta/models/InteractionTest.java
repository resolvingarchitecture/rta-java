package ra.rta.models;

import java.util.Date;

import org.junit.Test;

public class InteractionTest extends BaseKryoTest<IdentityInteraction> {

	@Test
	public void testKryo() throws Exception {
        IdentityInteraction in = new IdentityInteraction();
		in.setAdId("adid");
		in.setDateTime(new Date());
		testKryo(in);
	}

}
