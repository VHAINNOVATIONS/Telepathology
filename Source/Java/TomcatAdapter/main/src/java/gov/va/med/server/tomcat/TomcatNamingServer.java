/**
 * 
 */
package gov.va.med.server.tomcat;

import gov.va.med.server.GlobalNamingServer;

import org.apache.catalina.Server;
import org.apache.catalina.ServerFactory;
import org.apache.catalina.core.StandardServer;

/**
 * @author vhaiswbeckec
 * 
 */
public class TomcatNamingServer 
implements GlobalNamingServer
{
	/* (non-Javadoc)
	 * @see gov.va.med.server.tomcat.GlobalNamingServer#getGlobalContext()
	 */
	public javax.naming.Context getGlobalContext()
	{
		Server server = ServerFactory.getServer();
		javax.naming.Context globalContext = null; 

		if( (server != null) && (server instanceof StandardServer) )
			globalContext = ((StandardServer) server).getGlobalNamingContext();
		
		return globalContext;
	}
}
