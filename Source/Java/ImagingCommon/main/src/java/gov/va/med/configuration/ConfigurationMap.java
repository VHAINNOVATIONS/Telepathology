/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Aug 9, 2010
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

import java.util.HashMap;
import java.util.Map;
import javax.naming.Name;

/**
 * @author vhaiswbeckec
 *
 */
public class ConfigurationMap
extends HashMap<Object, Object>
{
	private static final long serialVersionUID = 1L;

	private final ConfigurationContext parent;
	private final Name name;

	ConfigurationMap(ConfigurationContext parent, Name name)
	{
		this.parent = parent;
		this.name = name;
	}
	
	/**
	 * @return the parent
	 */
	public ConfigurationContext getParent()
	{
		return this.parent;
	}
	
	/**
	 * @return the name
	 */
	public Name getName()
	{
		return this.name;
	}

	/* (non-Javadoc)
	 * @see java.util.HashMap#clear()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void clear()
	{
		Map<? extends Object, ? extends Object> clone = (Map<? extends Object, ? extends Object>) this.clone();
		super.clear();
		for(Map.Entry<? extends Object, ? extends Object> entry : clone.entrySet())
			notifyParent(ConfigurationContextEvent.TYPE.DELETE, entry.getKey(), null, entry.getValue());
		
	}

	/* (non-Javadoc)
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	@Override
	public Object get(Object key)
	{
		Object value = super.get(key);
		
		notifyParent(ConfigurationContextEvent.TYPE.READ, key, value, null);
		
		return value;
	}

	/* (non-Javadoc)
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object put(Object key, Object value)
	{
		Object oldValue = super.put(key, value);
		
		notifyParent(ConfigurationContextEvent.TYPE.CREATE, key, oldValue, value);
		
		return oldValue;
	}

	/* (non-Javadoc)
	 * @see java.util.HashMap#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends Object, ? extends Object> m)
	{
		super.putAll(m);
		for(Map.Entry<? extends Object, ? extends Object> entry : m.entrySet())
			notifyParent(ConfigurationContextEvent.TYPE.CREATE, entry.getKey(), null, entry.getValue());
	}

	/* (non-Javadoc)
	 * @see java.util.HashMap#remove(java.lang.Object)
	 */
	@Override
	public Object remove(Object key)
	{
		Object value = super.remove(key);
		
		notifyParent(ConfigurationContextEvent.TYPE.DELETE, key, value, null);

		return value;
	}
	
	// ============================================================================================================
	// 
	// ============================================================================================================
	private void notifyParent(
		ConfigurationContextEvent.TYPE eventType, 
		Object key, 
		Object oldValue, 
		Object newValue)
	{
		ConfigurationContextEvent event = new ConfigurationContextEvent(
			eventType,
			ConfigurationContextEvent.OBJECT_TYPE.PROPERTY,
			null,
			this.getName(),
			key,
			oldValue,
			newValue);
		getParent().childCollectionEventNotification(event);
	}
}
