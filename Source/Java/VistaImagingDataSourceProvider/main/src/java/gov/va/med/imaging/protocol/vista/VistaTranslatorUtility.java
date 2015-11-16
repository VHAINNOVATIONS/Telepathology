/**
 * 
 */
package gov.va.med.imaging.protocol.vista;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.apache.log4j.Logger;

/**
 * @author vhaiswbeckec
 * 
 */
public class VistaTranslatorUtility {
	private static Logger logger = Logger
			.getLogger(VistaImagingTranslator.class);

	/**
	 * 
	 * @param date
	 * @return - a String representation of the date in the format needed to
	 *         make an RPC call or a zero-length string if date is null.
	 */
	public static String convertDateToRpcFormat(Date date) {
		if (date == null)
			return "";

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		// int hour = calendar.get(Calendar.HOUR_OF_DAY);
		// int minute = calendar.get(Calendar.MINUTE);
		// int second = calendar.get(Calendar.SECOND);

		String mDateFormat = "";
		/*
		 * int yearDifference = ((year - 1700) / 100); mDateFormat =
		 * yearDifference + ""; year = (year % 100);
		 */

		DecimalFormat twoDigitFormat = new DecimalFormat("00");

		mDateFormat = twoDigitFormat.format(month) + "/"
				+ twoDigitFormat.format(day) + "/" + year;

		/*
		 * mDateFormat += twoDigitFormat.format(year) +
		 * twoDigitFormat.format(month) + twoDigitFormat.format(day) +
		 * twoDigitFormat.format(hour) + twoDigitFormat.format(minute) +
		 * twoDigitFormat.format(second);
		 */
		return mDateFormat;
	}

	/**
	 * 
	 * @param dicomDate
	 * @return
	 */
	public static String convertDICOMDateToRpcFormat(String dicomDate) {
		if ((dicomDate == null) || (dicomDate.equals(""))) {
			return "";
		}
		if (dicomDate.length() < 8) {
			return "";
		}

		// TODO: update this function to handle if only part of the date is
		// given (no month, etc)
		// TODO: month and day are now required, do a check for length and parse
		// on that
		// TODO: if the date is invalid, should this throw an exception or
		// always get full list of studies?
		// String dicomDate = "20061018143643.655321+0200";
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss",
		// Locale.US);

		String format = getDateFormat(dicomDate);
		if ("".equals(format))
			return "";
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
		DateFormat sdf = new SimpleDateFormat(format, Locale.US);
		Date d = null;
		try {
			d = sdf.parse(dicomDate);

			return convertDateToRpcFormat(d);
		} catch (ParseException pX) {
			logger.error(pX);
			return "";
		}

		/*
		 * YYYYMMDDHHMMSS.FFFFFF+ZZZZ The components of this string, from left
		 * to right, are YYYY = Year, MM = Month, DD = Day, HH = Hour, MM =
		 * Minute, SS = Second, FFFFFF = Fractional Second, “+” or “-” and ZZZZ =
		 * Hours and Minutes of offset. &ZZZZ is an optional suffix for
		 * plus/minus offset from Coordinated Universal Time. A component that
		 * is omitted from the string is termed a null component. Trailing null
		 * components of Date Time are ignored. Nontrailing null components are
		 * prohibited, given that the optional suffix is not considered as a
		 * component.
		 * 
		 * The pattern should read as: 4 required digits (the year) 2 optional
		 * digits for each of month, day, hour, minute and seconds optionally a
		 * decimal point followed by 6 digits (the milliseconds) optionally an
		 * ampersand followed by '+' or '-' and 4 digits, 2 for hours and 2 for
		 * minutes (the offset from UCT) Note that ranges are not checked by
		 * this pattern, that is there may be 63 minutes in an hour.
		 * 
		 * Ex: 2:36:43PM on 18 October, 2006 in EST would be:
		 * 20061018143643.655321+0500
		 * 
		 */

	}

	/**
	 * 
	 * @param vistaDate
	 * @return
	 */
	public static Date convertVistaDatetoDate(String vistaDate) {
		if ((vistaDate == null) || (vistaDate.length() <= 0))
			return null;
		try {
			// if the value includes the time, include that in the parse
			if (vistaDate.length() > 10) {
				SimpleDateFormat format = new SimpleDateFormat(
						"MM/dd/yyyy HH:mm", Locale.US);
				return format.parse(vistaDate);
			} else {
				SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy",
						Locale.US);
				return format.parse(vistaDate);
			}
		} catch (ParseException pX) {
			logger.error("Error parsing date [" + vistaDate + "] from VistA",
					pX);
		}
		return null;
	}
	
	public static boolean convertVistaBooleanToBoolean(String vistaBoolean)
	{
		if((vistaBoolean == null) || (vistaBoolean.length() <= 0))
			return false;
		return ("1".equals(vistaBoolean)) || ("TRUE".equalsIgnoreCase(vistaBoolean));
	}

	private static String getDateFormat(String date) {
		if (date == null)
			return "";

		switch (date.length()) {
		case 4:
			return "yyyy";
		case 6:
			return "yyyyMM";
		case 8:
			return "yyyyMMdd";
		case 10:
			return "yyyyMMddHH";
		case 12:
			return "yyyyMMddHHmm";
		case 14:
			return "yyyyMMddHHmmss";
		default:
			return "yyyyMMddHHmmss";

		}
	}
}
