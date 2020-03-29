package ra.rta.rfm.conspref.models;

import org.junit.Test;

public class EnvelopeTest extends BaseKryoTest<Envelope> {

	@Test
	public void testKryo() throws Exception {
		Envelope in = new Envelope();
		testKryo(in);
	}

}
