/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Jul 28, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
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
 */

package gov.va.med.configuration;

import java.text.DateFormat;

/**
 * @author vhaiswbeckec
 *
 */
public class ConfigurationEvent
{
	enum TYPE{CREATE, READ, UPDATE, DELETE, STORE, LOAD}
	
	private final TYPE type;
	private final long time = System.currentTimeMillis();
	private final String propertyName;
	private final Object oldValue;
	private final Object newValue;
	
	public static ConfigurationEvent createStoreEvent(String name)
	{
		return new ConfigurationEvent(TYPE.STORE, name, null, null);
	}
	
	public static ConfigurationEvent createLoadEvent(String name)
	{
		return new ConfigurationEvent(TYPE.LOAD, name, null, null);
	}
	
	public static ConfigurationEvent createCreateEvent(String name, Object newValue)
	{
		return new ConfigurationEvent(TYPE.CREATE, name, null, newValue);
	}
	
	public static ConfigurationEvent createReadEvent(String name, Object value)
	{
		return new ConfigurationEvent(TYPE.READ, name, value, value);
	}
	
	public static ConfigurationEvent createUpdateEvent(String name, Object oldValue, Object newValue)
	{
		return new ConfigurationEvent(TYPE.UPDATE, name, oldValue, newValue);
	}
	
	public static ConfigurationEvent createDeleteEvent(String name, Object oldValue)
	{
		return new ConfigurationEvent(TYPE.DELETE, name, oldValue, null);
	}
	
	/**
	 * @param type
	 * @param propertyName
	 * @param oldValue
	 * @param newValue
	 */
	public ConfigurationEvent(TYPE type, String propertyName, Object oldValue, Object newValue)
	{
		super();
		this.type = type;
		this.propertyName = propertyName;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	/**
	 * @return the type
	 */
	public TYPE getType()
	{
		return this.type;
	}

	/**
	 * @return the time
	 */
	public long getTime()
	{
		return this.time;
	}

	/**
	 * @return the propertyName
	 */
	public String getPropertyName()
	{
		return this.propertyName;
	}

	/**
	 * @return the oldValue
	 */
	public Object getOldValue()
	{
		return this.oldValue;
	}

	/**
	 * @return the newValue
	 */
	public Object getNewValue()
	{
		return this.newValue;
	}
	
	@Override
	public String toString()
	{
		DateFormat df = DateFormat.getDateTimeInstance();
		return 
			this.getClass().getSimpleName() + 
			"@" + df.format(getTime()) +
			" " + getType() + 
			" " + getPropertyName() + 
			" " + getOldValue() + (getNewValue() != null ? " => " + getNewValue() : "");
	}
}
