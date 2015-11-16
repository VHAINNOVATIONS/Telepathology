/**
 * 
 */
package gov.va.med.imaging;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * The International Standard prefixes for units of measure. 
 * 
 * @see http://en.wikipedia.org/wiki/SI_prefix
 * @author vhaiswbeckec
 *
 */
public enum DecimalOrdersOfMagnitude
{
	YOCTO("yocto", BigDecimal.valueOf(0.000000000000000000000001)),
	ZEPTO("zepto", BigDecimal.valueOf(0.000000000000000000001)),
	ATTO("atto", BigDecimal.valueOf(0.000000000000000001)),
	FEMTO("femto", BigDecimal.valueOf(0.000000000000001)),
	PICO("pico", BigDecimal.valueOf(0.000000000001)),
	NANO("nano", BigDecimal.valueOf(0.000000001)),
	MICRO("micro", BigDecimal.valueOf(0.000001)),
	MILLI("milli", BigDecimal.valueOf(0.001)),
	CENTI("centi", BigDecimal.valueOf(0.01)),
	DECI("deci", BigDecimal.valueOf(0.1)),
	UNIT("", BigDecimal.valueOf(1)),
	KILO("kilo", BigDecimal.valueOf(1000L) ),
	MEGA("mega", BigDecimal.valueOf(1000000L) ),
	GIGA("giga", BigDecimal.valueOf(1000000000L) ),
	TERA("tera", BigDecimal.valueOf(1000000000000L) ),
	PETA("peta", BigDecimal.valueOf(1000000000000000L) ),
	EXA("exa", BigDecimal.valueOf( 1000000000000000000L) ),
	ZETTA("zetta", BigDecimal.valueOf(1000000000000000000L).multiply(BigDecimal.valueOf(1000L)) ),
	YOTTA("yotta", BigDecimal.valueOf(1000000000000000000L).multiply(BigDecimal.valueOf(1000000L)) );
	
	private final String name;
	private final BigDecimal value;
	
	DecimalOrdersOfMagnitude(String name, BigDecimal value)
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
    public BigDecimal getValue()
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
	
	public BigInteger getValueAsBigInteger()
	{
		return BigInteger.valueOf(getValue().longValue());
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static DecimalOrdersOfMagnitude valueOfIgnoreCase(String value)
	{
		value = value.toUpperCase();
		return DecimalOrdersOfMagnitude.valueOf(value);
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static DecimalOrdersOfMagnitude greatestMagnitudeLessThan(long value)
	{
		return greatestMagnitudeLessThan(BigDecimal.valueOf(value));
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static DecimalOrdersOfMagnitude greatestMagnitudeLessThan(BigDecimal value)
	{
		if( BigDecimal.ZERO.equals(value) )
			return DecimalOrdersOfMagnitude.UNIT;
		
		BigDecimal absValue = value.abs();
		
		DecimalOrdersOfMagnitude result = DecimalOrdersOfMagnitude.UNIT;
		for(DecimalOrdersOfMagnitude mag : DecimalOrdersOfMagnitude.values())
		{
			if( mag.getValue().compareTo(absValue) > 0 )
				break;
			result = mag;
		}
		
		return result;
	}
	
    
}
