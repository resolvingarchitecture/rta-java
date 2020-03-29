package ra.rta.rfm.conspref.utilities;

import static java.util.regex.Pattern.compile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

public class Scrubber {

	// pre-compile the Patterns for speed and memory conservation instead of
	// using String.replaceAll

	// Perl \s*[!"\$%\*\+,;<=>\?\[\]\^\{\|\}~]\s*
	private static final Pattern PUNCTUATION = compile("\\s*[!\"$%*+,;<=>?\\[\\]^{|}~]\\s*");

	// Perl \s{2,}
	private static final Pattern MULTIPLE_SPACES = compile("\\s{2,}");

	// Perl \s+X{2}\b
	private static final Pattern XX = compile("\\s+X{2}\\b");

	// Perl X{3,}
	private static final Pattern XXX = compile("X{3,}");

	// Perl ^(40|49)([A-Z])
	private static final Pattern FORTY_49 = compile("^(40|49)([A-Z])");

	// Perl (\d\-\s+)(40|49)([A-Z])
	private static final Pattern DASH_40_49 = compile("(\\d-\\s+)(40|49)([A-Z])");

	// Perl \s*#\s*\d+$
	private static final Pattern STORE_NUMBER = compile("\\s*#\\s*\\d+$");

	// Perl \s*#\s*\d+\s*
	private static final Pattern TX_NUM = compile("\\s*#\\s*\\d+\\s*");

	// Perl ^(\d{6,}\-?)\s*
	private static final Pattern LEADING_NUMBERS = compile("^(\\d{6,}-?)\\s*");

	// Perl ^[A-Z]{2}\d{6,}\D
	private static final Pattern MORE_LEADING = compile("^[A-Z]{2}\\d{6,}\\D");

	private static final Pattern PHONE_1_TEST = compile("TEL[2-9]\\d{2}-?[2-9]\\d{2}\\s*\\d{2}\\s*\\d{2}(\\D|$)");
	private static final Pattern PHONE_1 = compile("(\\s*-?TEL[2-9]\\d{2}-?[2-9]\\d{2}\\s*\\d{2}\\s*\\d{2}\\s*)");

	// Perl \s*\-?TEL[2-9]\d{2}\-?[2-9]\d{2}\s*\d{2}\s*\d(\s+|$)
	private static final Pattern PHONE_2 = compile("\\s*-?TEL[2-9]\\d{2}-?[2-9]\\d{2}\\s*\\d{2}\\s*\\d(\\s+|$)");

	// Perl (\s*\-?1?[2-9]\d{2}[\-\.\s]?[2-9]\d{2}[\-\.\s]?\d{4}\s*)
	// (^|\D)\-?1?[2-9]\d{2}[\-\.\s]?[2-9]\d{2}[\-\.\s]?\d{4}(\D|$)
	private static final Pattern PHONE_3_TEST = compile("(^|\\D)-?1?[2-9]\\d{2}[-\\.\\s]?[2-9]\\d{2}[-\\.\\s]?\\d{4}(\\D|$)");
	private static final Pattern PHONE_3 = compile("\\s*-?1?[2-9]\\d{2}[-\\.\\s]?[2-9]\\d{2}[-\\.\\s]?\\d{4}\\s*");

	// Perl (\s*\-?1?\(?[2-9]\d{2}\)?[\-\.\s]?[2-9]\d{2}[\-\.\s]?\d{4}\s*)
	private static final Pattern PHONE_4_TEST = compile("(^|\\D)-?1?\\(?[2-9]\\d{2}\\)?[-\\.\\s]?[2-9]\\d{2}[-\\.\\s]?\\d{4}(\\D|$)");
	private static final Pattern PHONE_4 = compile("\\s*-?1?\\(?[2-9]\\d{2}\\)?[-\\.\\s]?[2-9]\\d{2}[-\\.\\s]?\\d{4}\\s*");

	// Perl (\s*\-?1?[2-9]\d{2}[\-\.\s]?[2-9]\d{2}[\-\.\s]?\d\s*\d{2}\s*\d\s*)
	private static final Pattern PHONE_5_TEST = compile("(^|\\D)-?1?[2-9]\\d{2}[-\\.\\s]?[2-9]\\d{2}[-\\.\\s]?\\d\\s*\\d{2}\\s*\\d(\\D|$)");
	private static final Pattern PHONE_5 = compile("\\s*-?1?[2-9]\\d{2}[-\\.\\s]?[2-9]\\d{2}[-\\.\\s]?\\d\\s*\\d{2}\\s*\\d\\s*");

	// Perl \s*[\(\)]\s*
	private static final Pattern PARENS = compile("\\s*[()]\\s*");

	// Perl \s+[#\-]\s+
	private static final Pattern HASH_DASH = compile("\\s+[#-]\\s+");

	// Perl \s+[\-\/]|[\-\/]\s+
	private static final Pattern MORE_PUNCT = compile("\\s+[-/]|[-/]\\s+");

	// Perl `
	private static final Pattern SINGLE_QUOTE_1 = compile("`");

	// Perl \s*DATE:\s*\d{2}\/\d{2}\s+TIME:\s*\d{2}:\d{2}
	private static final Pattern DATE = compile("\\s*DATE:\\s*\\d{2}/\\d{2}\\s+TIME:\\s*\\d{2}:\\d{2}");

	// Perl \s*([A-Z'\-\&]+)\s+\1(\s+|$)
	private static final Pattern DUPE_1 = compile("\\s*([&A-Z'-]+)\\s+\\1(\\s+|$)");

	// Perl \s*([A-Z'\-\&]+[\s+\-\&][A-Z'\-\&]+)\s+\1(\s+|$)
	private static final Pattern DUPE_2 = compile("\\s*([&A-Z'-]+[\\s+-][&A-Z'-]+)\\s+\\1(\\s+|$)");

	// Perl \s*([A-Z'\-\&]+[\s+\-\&][A-Z'\-\&]+[\s+\-\&][A-Z'\-\&]+)\s+\1(\s+|$)
	private static final Pattern DUPE_3 = compile("\\s*([&A-Z'-]+[\\s+-][&A-Z'-]+[\\s+-][&A-Z'-]+)\\s+\\1(\\s+|$)");

	// Perl (\s+)(TFR DEP|TFR WD|WD REV|DEP REV|CR
	// ADJ|WITHDRW|BILLPAY|RECUR|PURCHSE|DEPOSIT|ADJ-WDL|SAV-CHK|POS-PTP|POS-RTN|POS-IBP|CHK-DEP|POS-SIG)(\s+UNKNOWN
	// CODE)
	private static final Pattern UNKNOWN_CODE = compile("(\\s+)(TFR DEP|TFR WD|WD REV|DEP REV|CR ADJ|WITHDRW|BILLPAY|RECUR|PURCHSE|DEPOSIT|ADJ-WDL|SAV-CHK|POS-PTP|POS-RTN|POS-IBP|CHK-DEP|POS-SIG)(\\s+UNKNOWN CODE)");

	public static String trim(String in) {
		return in.trim();
	}

	public static String scrub(String in) {
		if (in == null) {
			return in;
		}
		String out = in.trim();
		if (out.length() == 0) {
			return out;
		}
		out = StringEscapeUtils.escapeJava(out);
		out = PUNCTUATION.matcher(out).replaceAll(" ");
		out = MULTIPLE_SPACES.matcher(out).replaceAll(" ");
		out = out.trim();
		out = out.toUpperCase();
		out = XX.matcher(out).replaceAll(" ");
		out = XXX.matcher(out).replaceAll(" ");
		Matcher matcher = FORTY_49.matcher(out);
		if (matcher.lookingAt()) {
			out = matcher.replaceFirst(matcher.group(2));
		}
		matcher = DASH_40_49.matcher(out);
		if (matcher.find()) {
			out = matcher.replaceFirst(matcher.group(3));
		}
		out = STORE_NUMBER.matcher(out).replaceAll("");
		out = TX_NUM.matcher(out).replaceAll(" ");
		out = LEADING_NUMBERS.matcher(out).replaceFirst(" ");
		out = MORE_LEADING.matcher(out).replaceAll(" ");
		if (PHONE_1_TEST.matcher(out).find()) {
			out = PHONE_1.matcher(out).replaceAll(" ");
		}
		out = PHONE_2.matcher(out).replaceAll(" ");
		if (PHONE_3_TEST.matcher(out).find()) {
			out = PHONE_3.matcher(out).replaceAll(" ");
		}
		if (PHONE_4_TEST.matcher(out).find()) {
			out = PHONE_4.matcher(out).replaceAll(" ");
		}
		if (PHONE_5_TEST.matcher(out).find()) {
			out = PHONE_5.matcher(out).replaceAll(" ");
		}
		out = PARENS.matcher(out).replaceAll(" ");
		out = HASH_DASH.matcher(out).replaceAll(" ");
		out = MORE_PUNCT.matcher(out).replaceAll(" ");
		// yes, again
		out = HASH_DASH.matcher(out).replaceAll(" ");
		// yes, again
		out = MORE_PUNCT.matcher(out).replaceAll(" ");
		out = SINGLE_QUOTE_1.matcher(out).replaceAll("'");
		out = DATE.matcher(out).replaceAll(" ");
		matcher = DUPE_1.matcher(out);
		if (matcher.find()) {
			// this *should* be '" " + matcher.group(1) + " "' but needs to
			// match existing bug in Perl stored proc
			out = matcher.replaceFirst(matcher.group(1) + " ");
		}
		matcher = DUPE_2.matcher(out);
		if (matcher.find()) {
			out = matcher.replaceAll(" " + matcher.group(1) + " ");
		}
		matcher = DUPE_3.matcher(out);
		if (matcher.find()) {
			out = matcher.replaceAll(" " + matcher.group(1) + " ");
		}
		out = UNKNOWN_CODE.matcher(out).replaceAll(" ");
		out = MULTIPLE_SPACES.matcher(out).replaceAll(" ");
		out = out.trim();
		return out;
	}

}
