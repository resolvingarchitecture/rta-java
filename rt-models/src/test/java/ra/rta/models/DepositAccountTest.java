package ra.rta.models;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;

public class DepositAccountTest extends BaseKryoTest<DepositAccount> {

	@Test
	public void testKryo() throws Exception {
		DepositAccount in = new DepositAccount();
		in.setUuid(UUID.randomUUID());
		in.setOpenDate(new Date());
		in.setAvailableBalance(10000.00f);
		testKryo(in);
	}

}
