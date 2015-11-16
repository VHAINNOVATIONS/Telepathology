/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Jun 17, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 * 
 * This class does encapsulates binary orders of magnitude for display
 * purposes ONLY.  The results of parsing and formatting to/from
 * strings is not precise.
 */
package gov.va.med.imaging;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author VHAISWBECKEC
 *
 */
public enum BinaryOrdersOfMagnitude
{
	B("Byte", BigInteger.valueOf(1L)),
	KB("Kilobyte", BigInteger.valueOf(1024L) ),
	MB("Megabyte", BigInteger.valueOf(1048576L) ),
	GB("Gigabyte", BigInteger.valueOf(1073741824L) ),
	TB("Terabyte", BigInteger.valueOf(1099511627776L) ),
	PB("Petabyte", BigInteger.valueOf(1125899906842624L) ),
	EB("Exabyte", BigInteger.valueOf(1152921504606846976L) ),
	ZB("Zettabyte", BigInteger.valueOf(1152921504606846976L).multiply(BigInteger.valueOf(1024)) ),
	YB("Yottabyte", BigInteger.valueOf(1152921504606846976L).multiply(BigInteger.valueOf(1048576)) );
	
	private final String name;
	private final BigInteger value;
	
	BinaryOrdersOfMagnitude(String name, BigInteger value)
	{
		this.name = name;
		this.value = value;
	}

	/**
     * @return the name
     */
    public String getName()
    {
    	return name;
    }

	/**
     * @return the value
     */
    public BigInteger getValue()
    {
    	return value;
    }
	
	public int getValueAsInt()
	{
		return getValue().intValue();
	}

	public long getValueAsLong()
	{
		return getValue().longValue();
	}
	
	public float getValueAsFloat()
	{
		return getValue().floatValue();
	}
	
	public double getValueAsDouble()
	{
		return getValue().doubleValue();
	}
	
	public BigDecimal getValueAsBigDecimal()
	{
		return new BigDecimal(getValue());
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static BinaryOrdersOfMagnitude valueOfIgnoreCase(String value)
	{
		value = value.toUpperCase();
		return BinaryOrdersOfMagnitude.valueOf(value);
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static BinaryOrdersOfMagnitude greatestMagnitudeLessThan(long value)
	{
		return greatestMagnitudeLessThan(BigInteger.valueOf(value));
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static BinaryOrdersOfMagnitude greatestMagnitudeLessThan(BigInteger value)
	{
		if( BigDecimal.ZERO.equals(value) )
			return BinaryOrdersOfMagnitude.B;
		
		BigInteger absValue = value.abs();
		
		BinaryOrdersOfMagnitude result = BinaryOrdersOfMagnitude.B;
		for(BinaryOrdersOfMagnitude mag : BinaryOrdersOfMagnitude.values())
		{
			if( mag == BinaryOrdersOfMagnitude.B )
				continue;
			if( mag.getValue().compareTo(absValue) > 0 )
				break;
			result = mag;
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param value - the value to format
	 * @param precision - the number of digits to the right of the decimal
	 * @return
	 */
	public static String format(long value, int precision)
	{
		return format(BigInteger.valueOf(value), precision, false);
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static String format(long value)
	{
		return format(BigInteger.valueOf(value), 4, false);
	}
	
	/**
	 * 
	 * @param value
	 * @param precision
	 * @param longform
	 * @return
	 */
	public static String format(long value, int precision, boolean longform)
	{
		precision = Math.max(0, precision);
		return format(BigInteger.valueOf(value), precision, longform);
	}
	
	/**
	 * 
	 * @param value
	 * @param precision
	 * @return
	 */
	public static String format(BigInteger value, int precision)
	{
		return format(value, precision, false);
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static String format(BigInteger value)
	{
		return format(value, 4, false);
	}
	
	/**
	 * Take a BigDecimal value and turn it into a human readable String
	 * in the form: number OrderOfMagnitude
	 * e.g. 1024 -> 1 KB
	 *      
	 * @param value
	 * @return
	 */
	public static String format(BigInteger value, int precision, boolean longform)
	{
		precision = Math.max(0, precision);
		BinaryOrdersOfMagnitude targetMagnitude = greatestMagnitudeLessThan(value);
		
		BigDecimal magValue = (new BigDecimal(value)).divide( new BigDecimal(targetMagnitude.getValue()) );
		BigInteger magValueAsInt = magValue.toBigInteger();
		BigInteger magDecimal = magValue.subtract( new BigDecimal(magValueAsInt) ).movePointRight(precision).toBigInteger();
			
		StringBuilder sb = new StringBuilder();
		sb.append(magValue.longValue());
		if(precision > 0)
		{
			sb.append(".");
			sb.append(magDecimal.toString());
		}
		
		sb.append(' ');
		sb.append(longform ? targetMagnitude.getName() : targetMagnitude.toString());
		
		return sb.toString();
	}
	
	// match the pattern of a number followed by whitespace and a unit
	// e.g. 45 KB matches as:
	// start() = 0, end() = 5
	// group(0) = "45 KB"
	// group(1) = "45"
	// group(2) = "KB"
	private static Pattern valuePattern = Pattern.compile("([0-9]+\\x2e?[0-9]*)\\s([KkMmGgTtPp][Bb])?");
	private static int valueGroup = 1;
	private static int valueUnitsGroup = 2;
	/**
	 * 
	 * @param formatted
	 * @return
	 */
	public static BigDecimal parse(String formatted)
	{
		double value = 0L;
		BinaryOrdersOfMagnitude units = BinaryOrdersOfMagnitude.B;
		
		Matcher matcher = valuePattern.matcher(formatted);
		matcher.lookingAt();
		if( matcher.group(valueGroup) != null )
			value = Double.parseDouble( matcher.group(valueGroup) );
		if(  matcher.group(valueUnitsGroup) != null )
			units = BinaryOrdersOfMagnitude.valueOfIgnoreCase(matcher.group(valueUnitsGroup));
		
		return (new BigDecimal(value)).max( new BigDecimal(units.getValue()) );
	}
}
