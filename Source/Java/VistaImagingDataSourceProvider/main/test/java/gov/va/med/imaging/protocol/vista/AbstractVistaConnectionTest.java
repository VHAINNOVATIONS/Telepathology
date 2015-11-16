package gov.va.med.imaging.protocol.vista;

import gov.va.med.ProtocolHandlerUtility;
import gov.va.med.imaging.datasource.DataSourceProvider;
import gov.va.med.imaging.datasource.Provider;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.ResolvedSiteImpl;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.SiteImpl;
import gov.va.med.imaging.tomcat.vistarealm.VistaRealmRoles;
import gov.va.med.imaging.tomcat.vistarealm.VistaRealmPrincipal.AuthenticationCredentialsType;
import gov.va.med.imaging.transactioncontext.ClientPrincipal;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * An abstract test case for any test case that uses a "vista" protocol
 * 
 * @author VHAISWBECKEC
 *
 */
public class AbstractVistaConnectionTest 
extends TestCase
{
	private static final List<URL> metadataUrls = new ArrayList<URL>();
	private static final List<URL> artifactUrls = new ArrayList<URL>();
	
	static
	{
		try
		{
			metadataUrls.add( new URL("vista://locahost:9300") );
			metadataUrls.add( new URL("vistaimaging://localhost:8080") );
			metadataUrls.add( new URL("vftp://localhost:8080") );
			
			artifactUrls.add( new URL("vista://locahost:9300") );
			artifactUrls.add( new URL("vistaimaging://localhost:9300") );
			artifactUrls.add( new URL("vftp://locahost:8080") );
		}
		catch (MalformedURLException x)
		{
			x.printStackTrace();
		}
	}
	
	@Override
	protected void setUp() 
	throws Exception
	{
		super.setUp();
		
		// turn logging up to max
	    Logger.getRootLogger().setLevel(Level.INFO);
	    
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
	}
	private DataSourceProvider provider;
	protected synchronized DataSourceProvider getProvider()
	{
		if(provider == null)
			provider = new Provider();
		
		return provider;
	}

	protected Site getTestSite() 
	throws MalformedURLException
	{
		return new SiteImpl("660", "Salt Lake City", "SLC", "localhost", 9300, "localhost", 8080, "");
	}
	
	private ResolvedSiteImpl resolvedSite = null;
	protected synchronized ResolvedSite getTestResolvedSite() 
	throws MalformedURLException
	{
		if(resolvedSite == null)
		{
			resolvedSite = ResolvedSiteImpl.create(
				getTestSite(), 
				true, 
				false, 
				true,
				metadataUrls, 
				artifactUrls);
		}		
		return resolvedSite;
		
	}
	
}
