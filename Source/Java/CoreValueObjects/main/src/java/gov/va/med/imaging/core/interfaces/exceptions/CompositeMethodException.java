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
public class CompositeMethodException
extends MethodException
{
	private static final long serialVersionUID = 1L;

	private SortedSet<CompositeExceptionComponent<MethodException>> componentExceptions =
		new TreeSet<CompositeExceptionComponent<MethodException>>();
	
	public CompositeMethodException(){}
	
	public CompositeMethodException(URL url, MethodException exception)
	{
		addException(url, exception);
	}
	public CompositeMethodException(MethodException exception)
	{
		addException(exception);
	}
	
	public void addException(URL url, MethodException exception)
	{
		componentExceptions.add(new CompositeExceptionComponent<MethodException>(url, exception));
	}
	
	public void addException(MethodException exception)
	{
		componentExceptions.add(new CompositeExceptionComponent<MethodException>(exception));
	}
	
	public int size()
	{
		return componentExceptions.size();
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
		
		for(CompositeExceptionComponent<MethodException> componentException : componentExceptions)
		{
			if(componentException.getUrl() != null)
			{
				sb.append( componentException.getUrl().toString() );
				sb.append( "->" );
			}
			sb.append( componentException.getException().getLocalizedMessage() );
			sb.append( "\n" );
		}
		
	    return sb.toString();
    }

	@Override
    public String getMessage()
    {
		StringBuilder sb = new StringBuilder();
		
		for(CompositeExceptionComponent<MethodException> componentException : componentExceptions)
		{
			if(componentException.getUrl() != null)
			{
				sb.append( componentException.getUrl().toString() );
				sb.append( "->" );
			}
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
    public Iterator<CompositeExceptionComponent<MethodException>> iterator()
    {
	    return componentExceptions.iterator();
    }
    
    public <T extends Throwable> T findException(Class<T> clazz, boolean checkCause)
    {
    	for(CompositeExceptionComponent<MethodException> ex : componentExceptions)
    	{
    		if(ex.getException().getClass() == clazz)
    			return (T)ex.getException();
    		if(checkCause)
    		{
    			if((ex.getException().getCause() != null) && (ex.getException().getCause().getClass() == clazz))
    				return (T)ex.getException().getCause();
    		}
    	}
    	
    	return null;
    }
}
