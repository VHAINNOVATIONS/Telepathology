package gov.va.med.imaging.core.interfaces.exceptions;

import java.net.URL;

/**
 * A private class to hold the member exceptions linked to the URL that
 * was being tried when the exception occurred.
 * 
 * @author VHAISWBECKEC
 *
 * NOTE: Comparable.compareTo() is NOT consistent with equals().
 * equals() is the default equals method and should remain so to assure
 * inclusion in a Set.  compareTo() is based on the exception type only.
 */
public class CompositeExceptionComponent<T extends Exception>
implements Comparable<CompositeExceptionComponent<T>>
{
	// The order of the elements in this array determine the ordering
	// according to the Comparable interface.
	// Unknown exceptions types precede the known types on the assumption that
	// it is a serious error.
	private final Class<?>[] ordering = new Class<?>[]
	{
		ConnectionException.class, 
		MethodException.class
	};
	
	private int getOrderingIndex()
	{
		int index = 0;
		for(Class<?> knownExceptionClass : ordering)
		{
			if( knownExceptionClass.getName() == this.getException().getClass().getName() )
				return index;
			++index;
		}
		
		return -1;
	}
	
	private final URL url;
	private final T exception;
	
	CompositeExceptionComponent(T exception)
	{
        super();
        this.url = null;
        this.exception = exception;
	}
	
	CompositeExceptionComponent(URL url, T exception)
    {
        super();
        this.url = url;
        this.exception = exception;
    }
	public URL getUrl()
    {
    	return url;
    }
	public T getException()
    {
    	return exception;
    }
	
	@Override
    public int compareTo(CompositeExceptionComponent that)
    {
        return this.getOrderingIndex() < that.getOrderingIndex() ? -1 :
        	this.getOrderingIndex() > that.getOrderingIndex() ? 1 : 0;
    }
}