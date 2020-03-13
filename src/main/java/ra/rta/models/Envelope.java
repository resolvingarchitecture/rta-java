package ra.rta.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public final class Envelope implements Serializable {

	private static final long serialVersionUID = 1L;

	private Header header = new Header();
	private Body body = new Body();

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public static class Header implements Serializable {

		private static final long serialVersionUID = 1L;

		private String command;

		public String getCommand() {
			return command;
		}

		public void setCommand(String command) {
			this.command = command;
		}

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}

		@Override
		public int hashCode() {
			return command == null ? 0 : command.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Header other = (Header) obj;
			return Objects.equals(command, other.command);
		}
	}

	public static class Body implements Serializable {

		private static final long serialVersionUID = 1L;

		private List<Record> records = new ArrayList<>();

		public List<Record> getRecords() {
			return records;
		}

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}

		@Override
		public int hashCode() {
			return records == null ? 0 : records.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Body other = (Body) obj;
			return Objects.equals(records, other.records);
		}

	}

}
