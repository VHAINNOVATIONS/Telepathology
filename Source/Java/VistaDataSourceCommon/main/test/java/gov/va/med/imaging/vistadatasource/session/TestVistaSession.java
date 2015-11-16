package gov.va.med.imaging.vistadatasource.session;

import gov.va.med.ProtocolHandlerUtility;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.SiteImpl;
import gov.va.med.imaging.exchange.enums.ImagingSecurityContextType;
import gov.va.med.imaging.tomcat.vistarealm.VistaRealmRoles;
import gov.va.med.imaging.tomcat.vistarealm.VistaRealmPrincipal.AuthenticationCredentialsType;
import gov.va.med.imaging.transactioncontext.ClientPrincipal;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import junit.framework.TestCase;

public class TestVistaSession 
extends TestCase
{
	URL url;
	Site site;
	
	private final static String MAG_WINDOWS_CONTEXT = "MAG WINDOWS";
	
	protected void setUp() throws Exception
	{
		super.setUp();
		
		// turn logging up to max
	    Logger.getRootLogger().setLevel(Level.INFO);
	    Logger.getRootLogger().removeAllAppenders();
	    Logger.getRootLogger().addAppender(new ConsoleAppender());
	    
	    ProtocolHandlerUtility.initialize(true);
		
		List<String> roles = new ArrayList<String>();
		roles.add(VistaRealmRoles.VistaUserRole.getRoleName());
		ClientPrincipal principal = new ClientPrincipal(
				"660", true, AuthenticationCredentialsType.Password, 
				"boating1", "boating1.", 
				"126", "IMAGPROVIDERONETWOSIX,ONETWOSIX", "843924956", "660", "Salt lake City", 
				roles, 
				new HashMap<String, Object>()
		);
		TransactionContextFactory.createClientTransactionContext(principal);
		TransactionContextFactory.get().setImagingSecurityContextType(ImagingSecurityContextType.MAG_WINDOWS.toString());
		url = new URL("vista://localhost:9300");
		site = new SiteImpl("660", "Salt Lake City", "SLC", "localhost", 9300, "localhost", 8080, "");
	}

	public void testCreate() 
	throws IOException, ConnectionException, MethodException
	{
		VistaSession vistaSession = VistaSession.getOrCreate(url, site);
		
		assertNotNull(vistaSession);
		vistaSession.close();
	}
	
	public void testCacheHit() 
	throws IOException, ConnectionException, MethodException
	{
		VistaSession vistaSession1 = VistaSession.getOrCreate(url, site);
		assertNotNull(vistaSession1);
		vistaSession1.close();
		
		VistaSession vistaSession2 = VistaSession.getOrCreate(url, site);
		assertNotNull(vistaSession2);
		
		if( System.identityHashCode(vistaSession1) != System.identityHashCode(vistaSession2) )
			fail("VistaSession instances should be the same.");
	}
	
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	
}
