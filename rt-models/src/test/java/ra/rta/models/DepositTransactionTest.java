package ra.rta.models;

import org.junit.Test;

import java.util.UUID;

public class DepositTransactionTest extends BaseKryoTest<DepositTransaction> {

	@Test
	public void testKryo() throws Exception {
		DepositTransaction in = new DepositTransaction();
		in.setAdId(UUID.randomUUID());
		in.setAmount(1125.00);
		testKryo(in);
	}

}
