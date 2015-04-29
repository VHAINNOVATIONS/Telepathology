/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 15, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.tomcat.vistarealm;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.Container;
import org.apache.catalina.Realm;
import org.apache.catalina.ServerFactory;
import org.apache.catalina.Service;
import org.apache.catalina.core.StandardServer;
import org.apache.log4j.Logger;

/**
 * @author VHAISWWERFEJ
 *
 */
public class SpecificRealmAuthentication
{
	private final static Logger logger = Logger.getLogger(SpecificRealmAuthentication.class);
	private static List<Realm> realms = null;
	
	private synchronized static List<Realm> getRealms()
	{
		if(realms == null)
		{
			realms = new ArrayList<Realm>();
	    	StandardServer server = (StandardServer)ServerFactory.getServer();
	    	for (Service service : server.findServices())
	    	{
	    		addVistaRealmsToList(service);
	    	}
		}
		return realms;
	}

	public static Principal authenticate(Class<? extends Realm> searchRealm, String accessCode, String verifyCode)
	{
		for(Realm r : getRealms())
		{
			if(r.getClass() == searchRealm)
			{
				return r.authenticate(accessCode, verifyCode);
			}
		}
		return null;
	}
	
	private static void addVistaRealmsToList(Service service)
	{
		recurseContainers(service.getContainer());
	}

	private static void recurseContainers(Container container)
	{
		try
		{
			for (Container childContainer : container.findChildren())
			{
				recurseContainers(childContainer);
			}
	
			if (container instanceof org.apache.catalina.core.StandardHost ||
				container instanceof org.apache.catalina.core.StandardEngine ||
				container instanceof org.apache.catalina.core.StandardService)
				return;
			
			if(container.getRealm() != null)
				realms.add(container.getRealm());
		}
		catch (Throwable t)
		{
			logger.error(t.getMessage(), t);
		}
	}
}
