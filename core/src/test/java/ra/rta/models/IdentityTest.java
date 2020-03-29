package ra.rta.models;

import org.junit.Test;

import java.util.UUID;

public class IdentityTest extends BaseKryoTest<Identity> {

	@Test
	public void testKryo() throws Exception {
		Identity in = new Identity();
		in.setAdId(UUID.randomUUID());
		in.setAge(1);
		testKryo(in);
	}

}
