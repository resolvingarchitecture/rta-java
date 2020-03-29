package ra.rta.models;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.UUID;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.twitter.chill.java.UUIDSerializer;

public abstract class BaseKryoTest<T> {

	private final Kryo kryo = new Kryo();
	private final Class<T> clazz;

	@SuppressWarnings("unchecked")
	protected BaseKryoTest() {
		ParameterizedType genericSuperClass = (ParameterizedType) getClass().getGenericSuperclass();
		Type[] genericTypes = genericSuperClass.getActualTypeArguments();
		clazz = (Class<T>) genericTypes[0];
        register(UUID.class, new UUIDSerializer());
	}

	protected void register(Class<?> type, Serializer<?> serializer) {
		kryo.register(type, serializer);
	}

	protected T testKryo(T in) throws Exception {
		try (Output output = new Output(new ByteArrayOutputStream())) {
			kryo.writeObject(output, in);
			try (Input input = new Input(output.getBuffer())) {
				T out = kryo.readObject(input, clazz);
				assertThat(out, samePropertyValuesAs(in));
				return out;
			}
		}
	}
}
