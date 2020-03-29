package ra.rta.rfm.conspref.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtility {

    public static String[] knownFormats() {
        return new String[]{
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd-HH.mm.ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd-HH.mm.ss",
                "yyyy-MM-dd-HH.mm",
                "yyyy-MM-dd-HH",
                "yyyy-MM-dd",
                "yyyyMMdd"
        };
    }

    public static Date stringToDate(String date) throws Exception {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date);
    }

    public static String timestampToSimpleDateString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static String timestampToSimpleDateStringWithSeparator(Date date, String separator) {
        return new SimpleDateFormat("yyyy"+separator+"MM"+separator+"dd").format(date);
    }

    public static String timestampToString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(date);
    }

    public static Date timestampStringToDate(String timestamp) throws Exception {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(timestamp);
    }

	public static int dateToInt(Date date) {
		return Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(date));
	}

	public static int dateStringToInt(String date) throws Exception {
		return dateToInt(new SimpleDateFormat("yyyyMMdd").parse(date));
	}

	public static int dateStringToInt(String date, String format) throws Exception {
		return dateToInt(new SimpleDateFormat(format).parse(date));
	}

	public static Date intToDate(int date) throws Exception {
		return new SimpleDateFormat("yyyyMMdd").parse(date + "");
	}

	public static Date intStringToDate(String integer) throws Exception {
		return new SimpleDateFormat("yyyyMMdd").parse(integer);
	}

	/**
	 * Days difference from 'fromDateInt' to 'toDateInt'
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public static long daysDifference(Date fromDate, Date toDate) {
		return TimeUnit.MILLISECONDS.toDays(Math.abs(fromDate.getTime() - toDate.getTime()));
	}

    /**
     *
     * @param date
     * @param numberOfDays Number of days to change date. Positive adds days, negative subtracts.
     * @return
     */
    public static Date changeDate(Date date, int numberOfDays) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE,numberOfDays);
        return cal.getTime();
    }

	public static boolean inStandardDateFormat(String input) {
		boolean inStandardDateFormat = false;
		try {
			new SimpleDateFormat("yyyy-MM-dd").parse(input);
			inStandardDateFormat = true;
		} catch (ParseException e) {
			//
		}
		return inStandardDateFormat;
	}

}
