/**
 * 
 */
package gov.va.med.imaging.proxy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 * 
 * The superclass of request parameters, those things that serve to encapsulate the
 * parameters to a proxy request.
 */
public abstract class AbstractRequestParameters
implements java.io.Serializable
{
	private Logger logger = Logger.getLogger(this.getClass());
	// Request metadata properties.
	// These are usually not used in the actual invocation and resulting response.
	// The key values should use the TransactionPropertyName enumeration for 
	// "well-known" properties.
	private Map<String, String> properties = new HashMap<String,String>();
	
	// an internal date format used only for converting dates to/from 
	// a String format in the properties map
	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd:hh:mm:ss.SSSSZ");
	
	/**
	 * Create a RequestParameters instance with no
	 * additional properties and a unique transaction ID.
	 */
	public AbstractRequestParameters()
	{
		this(null);
	}
	
	/**
	 * Create a RequestParameters instance
	 * 
	 * @param properties - if not null then the map will be copied to this instance properties
	 */
	public AbstractRequestParameters(Map<String, String> properties)
	{
		this(null, null, properties);
	}

	/**
	 * 
	 * @param referenceTransactionIdentifier
	 * @param properties
	 */
	public AbstractRequestParameters(String referenceTransactionIdentifier, Map<String, String> properties)
	{
		this(null, null, properties);
	}
	
	public AbstractRequestParameters(
			String referenceTransactionIdentifier,
			Date requiredResponseDate,
			Map<String, String> properties)
	{
		super();
		
		if( referenceTransactionIdentifier != null )
			this.properties.put( RequestPropertyName.ReferenceTransactionId.toString(), referenceTransactionIdentifier );

		// properties must be strings, so put a String representation of the required date
		// if no required response date is specified then assume immediate response is required
		this.properties.put( 
			RequestPropertyName.RequiredResponseDate.toString(), 
			requiredResponseDate != null ?
				dateFormat.format(requiredResponseDate) :
				dateFormat.format(new Date())
		);

		// don't use putAll because there may be properties that overwrite the
		// known properties, which may not be mutable
		if(properties != null)
		{
			for(String key : properties.keySet())
				putProperty( key, properties.get(key) );
		}
	}
	
	// ==================================================================================================
	// Well known property getters and setters
	// ==================================================================================================
	
	public String getReferenceTransactionIdentifier()
	{
		return properties.get(RequestPropertyName.ReferenceTransactionId.toString());
	}
	public void setReferenceTransactionIdentifier(String value)
	{
		properties.put(RequestPropertyName.ReferenceTransactionId.toString(), value);
	}

	public Date getRequiredResponseDate()
	{
		try
		{
			return dateFormat.parse( properties.get(RequestPropertyName.RequiredResponseDate.toString()) );
		} 
		catch (ParseException x)
		{
			logger.error(x);
			return new Date();
		}
	}
	
	/**
	 * Sets the required response date to the current date
	 */
	private void resetRequiredResponseDate()
	{
		this.properties.put( 
			RequestPropertyName.RequiredResponseDate.toString(), 
			dateFormat.format(new Date())
		);
	}
	
	// ==================================================================================================
	// Generic Parameter put and get
	// ==================================================================================================

	/**
	 * Put an additional property that may be needed by the asynch listeners
	 * These are not used in the actual invocation and resulting response.
	 */
	public void putProperty(String key, String value)
	{
		RequestPropertyName wellKnownName = RequestPropertyName.valueOf(key);
		if(wellKnownName != null)
		{
			if(wellKnownName.isMutable() || getProperty(key) == null )
				properties.put(key, value);
			else
				logger.warn("Attempt to set immutable property '" + key + "' is being ignored");
		}
		else		// a user defined property, we don't enforse its immutability
			this.properties.put(key, value);
	}
	
	/**
	 * Get an additional property that may have been added by the caller.
	 * These are not used in the actual invocation and resulting response.
	 */
	public String getProperty(String key)
	{
		return this.properties.get(key);
	}
	
	public Map<String,String> getProperties()
	{
		return this.properties;
	}

	/**
	 * This setter is here only for use by XML encoder/decoder.
	 * DO NOT USE IT for other code else you will be hung, drawn,
	 * quartered, publicly flogged and humiliated.  Then you'll have
	 * to fix the resulting bugs.
	 * 
	 * @param properties
	 */
	public void setProperties(Map<String,String> properties)
	{
		this.properties = properties;
	}

	// ==================================================================================================
	// Object Overrides
	// ==================================================================================================
	
	/**
	 * The hashCode() method must be implemented for derived classes for the client's results recorder to
	 * work correctly.
	 */
	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.properties == null) ? 0 : this.properties.hashCode());
		return result;
	}
}
