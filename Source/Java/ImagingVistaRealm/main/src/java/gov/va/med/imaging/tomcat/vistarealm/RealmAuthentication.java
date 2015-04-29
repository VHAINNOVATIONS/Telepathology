package gov.va.med.imaging.tomcat.vistarealm;


import java.util.HashMap;

import org.apache.catalina.Container;
import org.apache.catalina.ServerFactory;
import org.apache.catalina.Service;
import org.apache.catalina.core.StandardServer;
import org.apache.log4j.Logger;

public class RealmAuthentication
{
    private HashMap<String, VistaAccessVerifyRealm> realms = null;
    
    private static final Logger logger = Logger.getLogger (RealmAuthentication.class);

    public RealmAuthentication()
    {
    	// TODO: Fix this! 
    	// For now, log in to the Realm
		realms = new HashMap<String, VistaAccessVerifyRealm>();
    	StandardServer server = (StandardServer)ServerFactory.getServer();
    	for (Service service : server.findServices())
    	{
    		addVistaRealmsToList(service);
    	}
    }

	public void authenticate(String siteId, String accessCode, String verifyCode)
	{
    	// Try to log in to the configured site...
    	realms.get(siteId).authenticate(accessCode, verifyCode);
	}
	
	private void addVistaRealmsToList(Service service)
	{
		recurseContainers(service.getContainer());
	}

	private void recurseContainers(Container container)
	{
		try
		{
			Container[] childContainers = container.findChildren();
	
			for (Container childContainer : container.findChildren())
			{
				recurseContainers(childContainer);
			}
	
			if (container instanceof org.apache.catalina.core.StandardHost ||
				container instanceof org.apache.catalina.core.StandardEngine ||
				container instanceof org.apache.catalina.core.StandardService)
				return;
			
			if (container.getRealm() instanceof VistaAccessVerifyRealm)
			{
				VistaAccessVerifyRealm realm = (VistaAccessVerifyRealm) container.getRealm();
				if (!realms.containsKey(realm.getSiteNumber()))
				{
					realms.put(realm.getSiteNumber(), realm);
				}
			}
		}
		catch (Throwable t)
		{
			logger.error(t.getMessage(), t);
		}
	}
}
