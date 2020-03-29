package ra.rta.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.lang3.StringEscapeUtils;
import ra.rta.utilities.Scrubber;

public class ScrubberTest {

	private static final String HOST_PORT = "localhost:9000";
	private static final String URL = "jdbc:postgresql://" + HOST_PORT
			+ "/dev?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

	public static void main(String[] args) throws Exception {
		try (
				Connection conn = DriverManager.getConnection(URL, "ra", "89jf410f84j01H987huhNI");
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("select * from p1.scrub_check");) {
			int errors = 0;
			while (rs.next()) {
				String raw = rs.getString(1);
				String dbScrubbed = StringEscapeUtils.escapeJava(rs.getString(2));
				String scrubbed = Scrubber.scrub(raw);
				if (!dbScrubbed.trim().equals(scrubbed)) {
					errors++;
					System.out.println(StringEscapeUtils.escapeJava(raw));
					System.out.println("\t" + dbScrubbed);
					System.out.println("\t" + scrubbed);
				}
			}
			System.out.println(errors);
		}
	}
}
