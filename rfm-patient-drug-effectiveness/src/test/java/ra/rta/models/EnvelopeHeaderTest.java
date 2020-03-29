package ra.rta.models;

import org.junit.Test;

import ra.rta.models.Envelope.Header;

public class EnvelopeHeaderTest extends BaseKryoTest<Header> {

	@Test
	public void testKryo() throws Exception {
		Header in = new Header();
		testKryo(in);
	}
}