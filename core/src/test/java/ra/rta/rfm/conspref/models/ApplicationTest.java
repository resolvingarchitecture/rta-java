package ra.rta.rfm.conspref.models;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class ApplicationTest {

	@Test
	public void testKryo() {
		Application in = new Application();
		in.setaId("adid");
		in.setBusinessAnnualSales(10.00);
		in.setBusinessInceptionDate(new Date());
		in.setBusinessNumberEmployees(1);

		Kryo kryo = new Kryo();
		try (Output output = new Output(new ByteArrayOutputStream())) {
			kryo.writeObject(output, in);
			try (Input input = new Input(output.getBuffer())) {
				Application out = kryo.readObject(input, Application.class);
				assertThat(out, samePropertyValuesAs(in));
			}
		}
	}

}
