package gov.va.med.imaging.xstream;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.SingleValueConverter;

public class VistaDateConverter implements SingleValueConverter {
	private static final String VISTA_DATE_FORMAT = "yyyyMMdd.hhmmss";

	public boolean canConvert(Class clazz) {
		return Date.class.isAssignableFrom(clazz);
	}

	public static Date parseDate(String value) {
		DateFormat formatter = new SimpleDateFormat(VISTA_DATE_FORMAT);
		Date date = null;
		if (value != null && !value.equals("")) {
			try {
				double dateTimeNumber = Double.parseDouble(value);
				DecimalFormat paddingFormatter = new DecimalFormat(
						"00000000.000000");
				value = paddingFormatter.format(dateTimeNumber);
				date = formatter.parse(value);
			} catch (ParseException e) {
				String msg = "can't parse \"" + value + "\" into a Date";
				throw new ConversionException(msg, e);
			}
		}
		return date;
	}

	public static String dateToString(Date date) {
		DateFormat formatter = new SimpleDateFormat(VISTA_DATE_FORMAT);
		return formatter.format(date);
	}

	public Object fromString(String value) {
		return parseDate(value);
	}

	public String toString(Object obj) {
		return dateToString((Date) obj);
	}
}
