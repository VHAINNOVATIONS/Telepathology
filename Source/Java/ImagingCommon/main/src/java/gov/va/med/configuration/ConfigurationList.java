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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.naming.Name;

/**
 * A List implementation, derived from ArrayList, that notifies ContextEventListener of
 * all changes made to its content.
 * 
 * @author vhaiswbeckec
 *
 */
public class ConfigurationList
extends ArrayList<Object>
{
	private static final long serialVersionUID = 1L;
	private final ConfigurationContext parent;
	private final Name name;

	ConfigurationList(ConfigurationContext parent, Name name)
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
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	@Override
	public boolean add(Object element)
	{
		boolean result = super.add(element);
		if(result)
		{
			int index = indexOf(element);
			notifyParent(ConfigurationContextEvent.TYPE.CREATE, index, null, element);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#add(int, java.lang.Object)
	 */
	@Override
	public void add(int index, Object element)
	{
		super.add(index, element);
		notifyParent(ConfigurationContextEvent.TYPE.CREATE, index, null, element);
		return;
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends Object> c)
	{
		boolean result = super.addAll(size(), c);

		return result;
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#addAll(int, java.util.Collection)
	 */
	@Override
	public boolean addAll(int index, Collection<? extends Object> c)
	{
		boolean result = super.addAll(size(), c);

		for(Object element : c)
		{
			int elementIndex = indexOf(element);
			notifyParent(ConfigurationContextEvent.TYPE.CREATE, elementIndex, null, element);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#clear()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void clear()
	{
		List<Object> cloneList = (List<Object>)this.clone();
		super.clear();
		
		int elementIndex = 0;
		for(Object element : (List<Object>)cloneList)
			notifyParent(ConfigurationContextEvent.TYPE.DELETE, elementIndex++, element, null);
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#get(int)
	 */
	@Override
	public Object get(int index)
	{
		Object element = super.get(index);
		
		notifyParent(ConfigurationContextEvent.TYPE.READ, index, element, element);
		
		return element;
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#remove(int)
	 */
	@Override
	public Object remove(int index)
	{
		Object result = super.remove(index);
		
		notifyParent(ConfigurationContextEvent.TYPE.DELETE, index, result, null);
		
		return result;
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o)
	{
		int index = indexOf(o);
		return index >= 0 ? remove(index) != null : false;
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#set(int, java.lang.Object)
	 */
	@Override
	public Object set(int index, Object element)
	{
		Object oldValue = super.set(index, element);
		
		notifyParent(ConfigurationContextEvent.TYPE.UPDATE, index, oldValue, element);
		
		return oldValue;
	}

	// ============================================================================================================
	// 
	// ============================================================================================================
	private void notifyParent(
		ConfigurationContextEvent.TYPE eventType, 
		int index, 
		Object oldValue, 
		Object newValue)
	{
		ConfigurationContextEvent event = new ConfigurationContextEvent(
			eventType,
			ConfigurationContextEvent.OBJECT_TYPE.PROPERTY,
			null,
			this.getName(),
			index,
			oldValue,
			newValue);
		getParent().childCollectionEventNotification(event);
	}
}
