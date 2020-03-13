package ra.rta.models;

import org.junit.Test;

import java.util.UUID;

public class CustomerTest extends BaseKryoTest<Customer> {

	@Test
	public void testKryo() throws Exception {
		Customer in = new Customer();
		in.setAdId(UUID.randomUUID());
		in.setAge(1);
		testKryo(in);
	}

}
