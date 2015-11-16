/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Mar 10, 2008
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
 */
package gov.va.med.imaging.core.interfaces.exceptions;


import java.net.URL;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author VHAISWBECKEC
 *
 */
public class CompositeConnectionException
extends ConnectionException
{
	private static final long serialVersionUID = 1L;

	private SortedSet<CompositeExceptionComponent<ConnectionException>> componentExceptions =
		new TreeSet<CompositeExceptionComponent<ConnectionException>>();
	
	public CompositeConnectionException(){}
	
	public CompositeConnectionException(URL url, ConnectionException exception)
	{
		addException(url, exception);
	}
	
	public void addException(URL url, ConnectionException exception)
	{
		componentExceptions.add(new CompositeExceptionComponent<ConnectionException>(url, exception));
	}
	
	/**
	 * The cause is the last exception that occurred to make this composite.
	 * The cause is only slightly useful, the component exceptions really must be
	 * examined individually.
	 *  
	 * @see java.lang.Throwable#getCause()
	 */
	@Override
    public Exception getCause()
    {
		return componentExceptions.size() == 0 ? 
			null : 
			componentExceptions.last().getException();
    }

	@Override
    public String getLocalizedMessage()
    {
		StringBuilder sb = new StringBuilder();
		
		for(CompositeExceptionComponent<ConnectionException> componentException : componentExceptions)
		{
			sb.append( componentException.getUrl().toString() );
			sb.append( "->" );
			sb.append( componentException.getException().getLocalizedMessage() );
			sb.append( "\n" );
		}
		
	    return sb.toString();
    }

	@Override
    public String getMessage()
    {
		StringBuilder sb = new StringBuilder();
		
		for(CompositeExceptionComponent<ConnectionException> componentException : componentExceptions)
		{
			sb.append( componentException.getUrl().toString() );
			sb.append( "->" );
			sb.append( componentException.getException().getMessage() );
			sb.append( "\n" );
		}
		
	    return sb.toString();
    }

	/**
	 * Return an Iterator over the component exceptions, in their
	 * natural ordering.  The natural ordering is by severity of exception
	 * and is not the same as the order of occurrence.
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
    public Iterator<CompositeExceptionComponent<ConnectionException>> iterator()
    {
	    return componentExceptions.iterator();
    }
}
