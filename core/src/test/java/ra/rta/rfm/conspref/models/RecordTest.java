package ra.rta.rfm.conspref.models;

import org.junit.Test;

public class RecordTest extends BaseKryoTest<Record> {

	@Test
	public void testKryo() throws Exception {
		Record in = new Record();
		in.setJson("json");
		in.setTransformed(true);
		in.setTried(1);
        in.setRaw("raw");
		testKryo(in);
	}
}
