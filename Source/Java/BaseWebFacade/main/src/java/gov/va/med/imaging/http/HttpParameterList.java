/*
 * Originally HttpParameterList.java 
 * created on Nov 22, 2004 @ 5:51:14 PM
 * by Chris Beckey mailto:c.beckey@seetab.com
 *
 */
package gov.va.med.imaging.http;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * @author Chris Beckey mailto:c.beckey@seetab.com
 * @since Nov 22, 2004 5:51:14 PM
 *
 * A typesfe LinkedList derivation which is guaranteed to contain
 * only instances of HttpParameter
 */
public class HttpParameterList 
extends LinkedList
{
	/**
	 * 
	 */
	public HttpParameterList()
	{
		super();
	}

	/**
	 * @param c
	 */
	public HttpParameterList(Collection c)
	{
		super(c);
	}

	/**
	 * Add an instance of HttpParameter, or throw a ClassCastException
	 * 
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, Object element)
	{
		super.add(index, (HttpParameter)element);
	}

	/**
	 * Add an instance of HttpParameter, or throw a ClassCastException
	 * 
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(Object o)
	{
		return super.add((HttpParameter)o);
	}

	public boolean collectionIsAddable(Collection c)
	{
		for(Iterator iter = c.iterator(); iter.hasNext();)
			if( ! (iter.next() instanceof HttpParameter) )
				return false;
				
		return true;
	}

	/**
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection c)
	{
		if(collectionIsAddable(c))
			return super.addAll(c);
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection c)
	{
		if(collectionIsAddable(c))
			return super.addAll(index, c);
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.LinkedList#addFirst(java.lang.Object)
	 */
	public void addFirst(Object o)
	{
		super.addFirst((HttpParameter)o);
	}

	/* (non-Javadoc)
	 * @see java.util.LinkedList#addLast(java.lang.Object)
	 */
	public void addLast(Object o)
	{
		super.addLast((HttpParameter)o);
	}
	
	/**
	 * 
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer(64);
			
		for(Iterator iter = iterator();
			iter.hasNext(); )
		{
			if(sb.length() > 0)
				sb.append(";");
			sb.append( ((HttpParameter)iter.next()).toString() );
		}
			
		return sb.toString();
	}
}
