/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 4, 2013
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
package gov.va.med.imaging.vistadatasource.session;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.SiteImpl;
import gov.va.med.imaging.tomcat.vistarealm.VistaRealmRoles;
import gov.va.med.imaging.tomcat.vistarealm.VistaRealmPrincipal.AuthenticationCredentialsType;
import gov.va.med.imaging.transactioncontext.ClientPrincipal;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.VistaQuery;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This test is not meant to be run as a unit test because it requires a real VistA connection. This test is meant to be run manually when an
 * actual VistA database is available.
 * 
 * @author VHAISWWERFEJ
 *
 */
public class VistaSessionFunctionalTest
{
	
	@Test
	public void testGoodThenBadThenGoodCalls()
	{
		String handlerPackages = System.getProperty("java.protocol.handler.pkgs");
		System.setProperty("java.protocol.handler.pkgs",
		        handlerPackages == null || handlerPackages.length() == 0 ? 
		        "gov.va.med.imaging.url" : 
		        handlerPackages + "|" + "gov.va.med.imaging.url");	
		
		List<String> roles = new ArrayList<String>();
		roles.add(VistaRealmRoles.VistaUserRole.getRoleName());
		ClientPrincipal principal = new ClientPrincipal(
				"660", true, AuthenticationCredentialsType.Password, 
				"boating1", "boating1.", 
				"126", "IMAGPROVIDERONETWOSIX,ONETWOSIX", "843924956", "660", "Salt lake City", 
				roles, 
				new HashMap<String, Object>()
		);
		
		principal.setAuthenticatedByVista(true);
		TransactionContextFactory.createClientTransactionContext(principal);
		
		
		try
		{
			VistaQuery goodQuery = createTestQuery(true);
			SessionCallResult goodResult = makeCall(goodQuery);
			assertNull(goodResult.getException());
			long firstIndex = goodResult.getSessionIndex();
			VistaQuery badQuery = createTestQuery(false);
			SessionCallResult failedResult = makeCall(badQuery);
			assertNotNull(failedResult.getException());
			long failedIndex = failedResult.getSessionIndex();
			assertEquals(firstIndex, failedIndex);
			SessionCallResult secondGoodResult = makeCall(goodQuery);
			assertNull(secondGoodResult.getException());
			assertNotSame(firstIndex, secondGoodResult.getSessionIndex());
			
		}
		catch(Exception ex)
		{
			fail(ex.getMessage());
		}
	}
	
	private SessionCallResult makeCall(VistaQuery query)
	{
		VistaSession vistaSession = null;
		long sessionIndex = -1;
		Exception exception = null;
		try
		{
			vistaSession = getSession();
			sessionIndex = vistaSession.getSessionIndex();
			vistaSession.call(query);
		}
		catch(Exception ex)
		{
			exception = ex;
		}
		finally
		{
			if(vistaSession != null)
				try { vistaSession.close(); } catch(Exception ex) {}
		}
		return new SessionCallResult(sessionIndex, exception);
	}
	
	private VistaSession getSession() 
	throws SecurityCredentialsExpiredException, IOException, 
	ConnectionException, MethodException
	{
		URL url = new URL("vistaimaging://localhost:9300");
		URL []urls = new URL[] {url};
		Site s = new SiteImpl("", "660", "Salt Lake City", "SLC", urls);
		
		return VistaSession.getOrCreate(url, s);
	}
	
	
	private VistaQuery createTestQuery(boolean isValid)
	{
		VistaQuery query = new VistaQuery("MAG4 VERSION CHECK");	
		if(isValid)
			query.addParameter(VistaQuery.LITERAL, "3.0.83.15");
		return query;
	}

	
	class SessionCallResult
	{
		final long sessionIndex;
		final Exception exception;
		
		public SessionCallResult(long sessionIndex, Exception exception)
		{
			super();
			this.sessionIndex = sessionIndex;
			this.exception = exception;
		}
		
		public SessionCallResult(long sessionIndex)
		{
			this(sessionIndex, null);
		}

		/**
		 * @return the sessionIndex
		 */
		public long getSessionIndex()
		{
			return sessionIndex;
		}

		/**
		 * @return the exception
		 */
		public Exception getException()
		{
			return exception;
		}
	}
	
}
