package ra.rta.models;

import org.junit.Test;

import java.util.UUID;

public class IndividualTest extends BaseKryoTest<Individual> {

	@Test
	public void testKryo() throws Exception {
		Individual in = new Individual();
		in.setAdId(UUID.randomUUID());
		in.setAge(1);
		testKryo(in);
	}

}
