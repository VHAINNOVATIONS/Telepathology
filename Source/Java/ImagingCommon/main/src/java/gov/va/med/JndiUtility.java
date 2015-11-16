/**
 * 
 */
package gov.va.med;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * @author vhaiswbeckec
 *
 */
public class JndiUtility
{
	public static String contextDump(Context ctx, String root, boolean recurse) 
	{
		return contextDump(ctx, root, recurse, 0);
	}
	
	private static String contextDump(Context ctx, String root, boolean recurse, int level) 
	{
		StringBuilder sb = new StringBuilder();
		try
		{
			for( NamingEnumeration<NameClassPair> list = ctx.list(root); list.hasMore(); ) 
			{
			    NameClassPair nc = list.next();
			    for(int t=0; t<level; ++t)
			    	sb.append('\t');
			    sb.append(nc);
		    	sb.append("line.separator");
		    	if(recurse)
		    	{
		    		String childPath = root.length() > 0 ? root + "/" + nc.getName() : nc.getName();
		    		contextDump(ctx, childPath, recurse, level+1);
		    	}
			}
		}
		catch(NamingException nX)
		{
			sb.append(nX.getMessage());
		}
		
		return sb.toString();
	}
}
