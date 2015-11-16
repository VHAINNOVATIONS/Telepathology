/**
 * 
 */
package gov.va.med.siteservice.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author vhaiswbeckec
 *
 */
public class CompositeArtifactSourceResolutionException
extends ArtifactSourceResolutionException
implements Iterable<ArtifactSourceResolutionException>
{
	private static final long serialVersionUID = 1L;
	private List<ArtifactSourceResolutionException> exceptions = new ArrayList<ArtifactSourceResolutionException>();
	
	public void add(ArtifactSourceResolutionException x)
	{
		exceptions.add(x);
	}

	@Override
	public Iterator<ArtifactSourceResolutionException> iterator()
	{
		return exceptions.iterator();
	}
	
	@Override
	public String getLocalizedMessage()
	{
		StringBuilder sb = new StringBuilder();
		
		for(ArtifactSourceResolutionException x : this)
		{
			sb.append(x.getLocalizedMessage());
			sb.append('\n');
		}
		
		return sb.toString();
	}

	@Override
	public String getMessage()
	{
		StringBuilder sb = new StringBuilder();
		
		for(ArtifactSourceResolutionException x : this)
		{
			sb.append(x.getMessage());
			sb.append('\n');
		}
		
		return sb.toString();
	}

	@Override
	public void printStackTrace()
	{
		for(ArtifactSourceResolutionException x : this)
		{
			System.err.println(x.getClass().getName());
			x.printStackTrace();
		}
		
		return;
	}

	@Override
	public void printStackTrace(PrintStream s)
	{
		for(ArtifactSourceResolutionException x : this)
		{
			s.println(x.getClass().getName());
			x.printStackTrace(s);
		}
		
		return;
	}

	@Override
	public void printStackTrace(PrintWriter s)
	{
		for(ArtifactSourceResolutionException x : this)
		{
			s.println(x.getClass().getName());
			x.printStackTrace(s);
		}
		
		return;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		for(ArtifactSourceResolutionException x : this)
		{
			sb.append(x.toString());
			sb.append(',');
		}
		
		return sb.toString();
	}
}
