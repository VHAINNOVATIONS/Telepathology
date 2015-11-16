/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date May 4, 2010
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

package gov.va.med;

import java.io.Serializable;
import java.util.*;

/**
 * @author vhaiswbeckec
 *
 */
public class ObservableMap<K extends Object, V extends Object>
extends Observable
implements Map<K,V>, Serializable
{
	private static final long serialVersionUID = -7507506346039680006L;
	private final Map<K, V> wrappedMap;
	
	public ObservableMap()
	{
		this(new HashMap<K,V>());
	}
	
	public ObservableMap(Map<K, V> wrappedMap)
	{
		this.wrappedMap = wrappedMap;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear()
	{
		wrappedMap.clear();
		this.setChanged();
		notifyObservers(new Event<K,V>(Event.TYPE.CLEAR));
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object key)
	{
		return wrappedMap.containsKey(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object value)
	{
		return wrappedMap.containsValue(value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet()
	{
		return wrappedMap.entrySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public V get(Object key)
	{
		return wrappedMap.get(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		return wrappedMap.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	@Override
	public Set<K> keySet()
	{
		return wrappedMap.keySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public V put(K key, V value)
	{
		V oldValue = wrappedMap.put(key, value);
		this.setChanged();
		notifyObservers(new Event<K,V>(Event.TYPE.PUT, key, oldValue, value));
		
		return oldValue;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m)
	{
		wrappedMap.putAll(m);
		this.setChanged();
		notifyObservers(new Event<K,V>(Event.TYPE.PUTALL));
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public V remove(Object key)
	{
		V oldValue = wrappedMap.remove(key);
		this.setChanged();
		notifyObservers( new Event<K,V>(Event.TYPE.REMOVE, (K)key, oldValue, (V)null) );
		
		return oldValue;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	@Override
	public int size()
	{
		return wrappedMap.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	@Override
	public Collection<V> values()
	{
		return wrappedMap.values();
	}

	public static class Event<K, V>
	{
		public enum TYPE{PUT, REMOVE, PUTALL, CLEAR}
		private final TYPE type;
		private final K key;
		private final V oldValue;
		private final V newValue;
		
		/**
		 * @param type
		 * @param key
		 * @param oldValue
		 * @param newValue
		 */
		public Event(TYPE type, K key, V oldValue, V newValue)
		{
			super();
			this.type = type;
			this.key = key;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		public Event(TYPE type)
		{
			super();
			this.type = type;
			this.key = null;
			this.oldValue = null;
			this.newValue = null;
		}
		
		protected TYPE getType()
		{
			return this.type;
		}

		protected K getKey()
		{
			return this.key;
		}

		protected V getOldValue()
		{
			return this.oldValue;
		}

		protected V getNewValue()
		{
			return this.newValue;
		}
	}
}
