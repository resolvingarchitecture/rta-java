package ra.rta.rfm.conspref.models;

import org.junit.Test;

import ra.rta.rfm.conspref.models.Envelope.Header;

public class EnvelopeHeaderTest extends BaseKryoTest<Header> {

	@Test
	public void testKryo() throws Exception {
		Header in = new Header();
		testKryo(in);
	}
}