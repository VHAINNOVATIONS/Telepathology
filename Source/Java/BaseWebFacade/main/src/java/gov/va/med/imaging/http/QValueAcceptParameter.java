/*
 * Originally HttpQValueHeaderElement.java 
 * created on Nov 18, 2004 @ 4:49:18 PM
 * by Chris Beckey mailto:c.beckey@seetab.com
 *
 */
package gov.va.med.imaging.http;

import gov.va.med.imaging.http.exceptions.*;

/**
 * @author Chris Beckey mailto:c.beckey@seetab.com
 * @since Nov 18, 2004 4:49:18 PM
 *
 * A base class for any type of Http Header Field Element that includes a
 * quality value.  This includes Accept and Charset elements.  
 */
public class QValueAcceptParameter
extends AcceptParameter
implements Comparable, Cloneable
{
	public static final String qValueKey = "q";
	public static final double maxQValue = 1.0;
	public static final double minQValue = 0.0;
	
	// qValues with a difference less than .001 are considered equivalent
	public static final double qValueDifferentiator = 0.0009;	

	protected double qualityValue = maxQValue;

	/**
	 * This will through exceptions if the 
	 * value is not parsable into a double between 0.0 and 1.0.
	 * 
	 * @param qValue
	 * @return
	 * @throws HttpHeaderParseException
	 */
	public static QValueAcceptParameter parseQValueParameter(String qValue)
	throws HttpHeaderParseException
	{
		QValueAcceptParameter newQValue = new QValueAcceptParameter();
		newQValue.parse(qValue);
		
		return newQValue;
	}

	/**
	 * This will through exceptions if the 
	 * value is not a double between 0.0 and 1.0.
	 * 
	 * @param qValue
	 * @return
	 * @throws HttpHeaderParseException
	 */
	public static QValueAcceptParameter createQValueParameter(double qValue)
	throws HttpHeaderParseException
	{
		QValueAcceptParameter newQValue = new QValueAcceptParameter();
		newQValue.setQualityValue(qValue);
		
		return newQValue;
	}
	
	/**
	 * Create a QValueAcceptParameter from a generic AcceptParameter.
	 * This will through exceptions of the attribute is not "q" or the
	 * value is not parsable into a double between 0.0 and 1.0.
	 * 
	 * @param genericAcceptParameter
	 * @return
	 * @throws HttpHeaderParseException
	 */
	public static QValueAcceptParameter createQValueParameter(AcceptParameter genericAcceptParameter)
	throws HttpHeaderParseException
	{
		QValueAcceptParameter newQValue = new QValueAcceptParameter();
		newQValue.setAttribute(genericAcceptParameter.getAttribute());
		newQValue.setValue( genericAcceptParameter.getValue() );
		
		return newQValue;
	}
	
	/**
	 *
	 */
	private QValueAcceptParameter()
	{
		try{ this.setAttribute(qValueKey); }
		catch (HttpHeaderParseException e){}		// this exception will never occur in this context
	}

	/**
	 * From the HTTP 1.1 spec (ftp://ftp.isi.edu/in-notes/rfc2616.txt)
	 * A quality value is a "short" floating point number, ranging from 0 to 1
	 * with no more than three digits to the right of the decimal point.
	 * 
	 * qvalue         = ( "0" [ "." 0*3DIGIT ] ) | ( "1" [ "." 0*3("0") ] )
	 * 
	 * @return Returns the qualityValue.
	 */
	public double getQualityValue()
	{
		return qualityValue;
	}

	/**
	 * @param qualityValue The qualityValue to set.
	 */
	private void setQualityValue(double qualityValue)
	throws HttpHeaderParseException
	{
		if( qualityValue > maxQValue )
			this.qualityValue = maxQValue;
		else if( qualityValue < minQValue)
			this.qualityValue = minQValue;
		
		this.qualityValue = qualityValue;
		super.setValue(Double.toString(qualityValue));
	}
	
	/**
	 * Set the quality value as a String.
	 * We should check to see that the value is formatted as:
	 * qvalue         = ( "0" [ "." 0*3DIGIT ] ) | ( "1" [ "." 0*3("0") ] )
	 * but instead we just assure that the double value is between 0 and 1, inclusive.
	 */
	protected void setValue(String value)
	throws HttpHeaderParseException
	{
		try
		{
			setQualityValue( Double.parseDouble(value) );
		}
		catch (NumberFormatException e)
		{
			throw new HttpQValueParseException(
				"[" + value + 
				"] has invalid quality value.  Valid values range from 0.0 to 1.0"
			);
		}
	}

	/**
	 * @see gov.va.med.imaging.exchange.wado.query.HttpHeaderModifier#setKey(java.lang.String)
	 */
	protected void setAttribute(String key) 
	throws HttpHeaderParseException
	{
		if(qValueKey.equals(key))
			super.setAttribute(key);
		else
			throw new HttpQValueParseException("[" + key + "] is invalid quality modifier, q is not specified ");
	}

	
	/**
	 * Parse a string of the format "q" "=" value
	 * @param parameter
	 */
	protected void parse(String qValue)
	throws HttpHeaderParseException
	{
		super.parse(qValue);
		if(getAttribute()==null || getValue()==null)
			throw new HttpQValueParseException("[" + qValue + "] is invalid quality modifier ");
	}
	
	/**
	 * Compares this object with the specified object for order. 
	 * Returns a negative integer, zero, or a positive integer as this object is 
	 * less than, equal to, or greater than the specified object.
	 * 
	 * Note: this class has a natural ordering that is CONSISTENT with equals.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object that)
	{
		QValueAcceptParameter thatQValue = (QValueAcceptParameter)that;
		return this.equals(thatQValue) ? 0 :
			this.getQualityValue() < thatQValue.getQualityValue() ? -1 : 1;
	}

	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if(obj instanceof QValueAcceptParameter)
		{
			QValueAcceptParameter that = (QValueAcceptParameter)obj;
			return 
				Math.abs(this.getQualityValue() - that.getQualityValue()) 
				< 
				QValueAcceptParameter.qValueDifferentiator;
		}
		else
			return false;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	protected Object clone() throws CloneNotSupportedException
	{
		try
		{
			return QValueAcceptParameter.createQValueParameter(this.getQualityValue());
		}
		catch (HttpHeaderParseException pX)
		{
			pX.printStackTrace();
			throw new CloneNotSupportedException(pX.getMessage());
		}
	}

}
