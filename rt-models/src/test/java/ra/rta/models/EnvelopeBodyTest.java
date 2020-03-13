package ra.rta.models;

import org.junit.Test;

import ra.rta.models.Envelope.Body;

public class EnvelopeBodyTest extends BaseKryoTest<Body> {

	@Test
	public void testKryo() throws Exception {
		Body in = new Body();
		testKryo(in);
	}
}
