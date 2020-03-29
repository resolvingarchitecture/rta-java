package ra.rta.rfm.conspref.models;

import org.junit.Test;

public class GroupTest extends BaseKryoTest<Group> {

	@Test
	public void testKryo() throws Exception {
		Group in = new Group();
		in.setActive(true);
		in.setCore("ibs");
		testKryo(in);
	}
}
