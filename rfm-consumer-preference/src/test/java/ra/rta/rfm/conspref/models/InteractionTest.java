package ra.rta.rfm.conspref.models;

import java.util.Date;

import org.junit.Test;

public class InteractionTest extends BaseKryoTest<IndividualInteraction> {

	@Test
	public void testKryo() throws Exception {
        IndividualInteraction in = new IndividualInteraction();
		in.setAdId("adid");
		in.setDateTime(new Date());
		testKryo(in);
	}

}
