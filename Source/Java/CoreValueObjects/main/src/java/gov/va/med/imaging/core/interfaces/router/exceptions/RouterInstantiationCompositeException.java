package gov.va.med.imaging.core.interfaces.router.exceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author vhaiswbeckec
 *
 */
public class RouterInstantiationCompositeException 
extends RouterInstantiationException 
{
	private static final long serialVersionUID = 1L;

	private List<RouterInstantiationException> exceptions = 
		new ArrayList<RouterInstantiationException>();
	
	public RouterInstantiationCompositeException()
	{
		
	}

	public void add(RouterInstantiationException x)
	{
		exceptions.add(x);
	}
	
	public int getCount()
	{
		return exceptions.size();
	}
	
	@Override
	public String getLocalizedMessage() 
	{
		return getMessage(true);
	}

	@Override
	public String getMessage() 
	{
		return getMessage(false);
	}

	private String getMessage(boolean localized)
	{
		if(getCount() == 0)
			return null;
		
		StringBuffer sb = new StringBuffer();
		
		for(RouterInstantiationException x : exceptions)
		{
			if(sb.length() > 0)
				sb.append(System.getProperty("line.separator"));
			
			sb.append(localized ? x.getLocalizedMessage() : x.getMessage());
		}
		
		return sb.toString();
		
	}
	
}
