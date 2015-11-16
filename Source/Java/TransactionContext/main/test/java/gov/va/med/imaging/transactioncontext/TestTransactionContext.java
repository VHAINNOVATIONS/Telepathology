package gov.va.med.imaging.transactioncontext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.GUID;
import gov.va.med.imaging.tomcat.vistarealm.VistaRealmPrincipal.AuthenticationCredentialsType;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import junit.framework.TestCase;

import org.junit.Ignore;

public class TestTransactionContext 
extends TestCase
{
	private TransactionContext tc;
	
	@Override
	protected void setUp() 
	throws Exception
	{
		super.setUp();
		
		List<String> roles = new ArrayList<String>();
		Map<String, Object> properties = new HashMap<String, Object>();
		ClientPrincipal cp = new ClientPrincipal("100", true, AuthenticationCredentialsType.Password,
				"hello", "world", "duz", "Alex DeLarge", "111-11-1111", "100", "Peoria", roles, properties);
		TransactionContextFactory.createClientTransactionContext(cp);
		
		tc = TransactionContextFactory.get();
	}

	public final void testGetRealm()
	{
		assertEquals("100", tc.getRealm());
	}

	public final void testSetGetAccessCode()
	{
		assertEquals("hello", tc.getAccessCode());
		tc.setAccessCode("yada");
		assertEquals("yada", tc.getAccessCode());
		tc.setAccessCode("hello");
		assertEquals("hello", tc.getAccessCode());
	}

	public final void testSetGetVerifyCode()
	{
		assertEquals("world", tc.getVerifyCode());
		tc.setVerifyCode("yada");
		assertEquals("yada", tc.getVerifyCode());
		tc.setVerifyCode("world");
		assertEquals("world", tc.getVerifyCode());
	}

	public final void testIsAuthenticatedByDelegate()
	{
		assertEquals(Boolean.TRUE, tc.isAuthenticatedByDelegate());
	}

	public final void testIsClientPrincipal()
	{
		assertEquals(Boolean.TRUE, tc.isClientPrincipal());
	}

	public final void testGetCredentialsType()
	{
		assertEquals(AuthenticationCredentialsType.Password.toString(), tc.getCredentialsType());
	}

	public final void testSetGetSiteName()
	{
		assertEquals("Peoria", tc.getSiteName());
		assertEquals("Peoria", tc.getLoggerSiteName());
		tc.setSiteName("Peoria2");
		assertEquals("Peoria2", tc.getSiteName());
		assertEquals("Peoria2", tc.getLoggerSiteName());
		tc.setSiteName("Peoria");
	}

	public final void testGetSetSiteNumber()
	{
		assertEquals("100", tc.getSiteNumber());
		assertEquals("100", tc.getLoggerSiteNumber());
		tc.setSiteNumber("101");
		assertEquals("101", tc.getSiteNumber());
		assertEquals("101", tc.getLoggerSiteNumber());
		tc.setSiteNumber("100");
	}

	public final void testGetSetSsn()
	{
		assertEquals("111-11-1111", tc.getSsn());
		assertEquals("111-11-1111", tc.getLoggerSsn());
		tc.setSsn("222-22-2222");
		assertEquals("222-22-2222", tc.getSsn());
		assertEquals("222-22-2222", tc.getLoggerSsn());
		tc.setSsn("111-11-1111");
	}

	public final void testGetSetDuz()
	{
		assertEquals("duz", tc.getDuz());
		assertEquals("duz", tc.getLoggerDuz());
		tc.setDuz("buzz");
		assertEquals("buzz", tc.getDuz());
		assertEquals("buzz", tc.getLoggerDuz());
		tc.setDuz("duz");
	}

	public final void testGetSetFullName()
	{
		assertEquals("Alex DeLarge", tc.getFullName());
		assertEquals("Alex DeLarge", tc.getLoggerFullName());
		tc.setFullName("Dim");
		assertEquals("Dim", tc.getFullName());
		assertEquals("Dim", tc.getLoggerFullName());
		tc.setFullName("Alex DeLarge");
	}

	public final void testGetSetTransactionId()
	{
		String transactionId = (new GUID()).toLongString();
		tc.setTransactionId(transactionId);
		assertEquals( transactionId, tc.getTransactionId() );
	}

	public final void testGetSetPurposeOfUse()
	{
		String purposeOfUse = "Just Because";
		tc.setPurposeOfUse(purposeOfUse);
		assertEquals( purposeOfUse, tc.getPurposeOfUse() );
	}

	public final void testGetDisplayIdentity()
	{
	}

	public final void testSetGetStartTime()
	{
		Long now = System.currentTimeMillis();
		tc.setStartTime(now);
		
		assertEquals( now, tc.getStartTime() );
	}

	public final void testSetGetPatientID()
	{
		String patientId = (new GUID()).toLongString();
		tc.setPatientID(patientId);
		assertEquals(patientId, tc.getPatientID());
	}

	public final void testSetGetRequestType()
	{
		tc.setRequestType("yada");
		assertEquals("yada", tc.getRequestType());
	}

	public final void testSetGetQueryFilter()
	{
		tc.setQueryFilter("yadayadayada");
		assertEquals("yadayadayada", tc.getQueryFilter());
	}

	public final void testSetGetEntriesReturned()
	{
		tc.setEntriesReturned(42);
		assertEquals(new Integer(42), tc.getEntriesReturned());
	}

	public final void testSetGetUrn()
	{
		tc.setUrn("yadayadayada");
		assertEquals("yadayadayada", tc.getUrn());
	}

	public final void testSetGetQuality()
	{
		tc.setQuality("Nice");
		assertEquals("Nice", tc.getQuality());
	}

	public final void testSetGetOriginatingAddress()
	{
		tc.setOriginatingAddress("vaihswbeckec.med.va.gov");
		assertEquals("vaihswbeckec.med.va.gov", tc.getOriginatingAddress());
	}

	public final void testSetGetItemCached()
	{
		tc.setItemCached(true);
		assertTrue(tc.isItemCached());
		tc.setItemCached(false);
		assertFalse(tc.isItemCached());
	}

	public final void testSetGetErrorMessage()
	{
		tc.setErrorMessage("yadayadayada");
		assertEquals("yadayadayada", tc.getErrorMessage());
	}

	public final void testSetGetRequestingSource()
	{
		tc.setRequestingSource("yadayadayada");
		assertEquals("yadayadayada", tc.getRequestingSource());
	}

	public final void testSetGetServicedSource()
	{
		tc.setServicedSource("yadayadayada");
		assertEquals("yadayadayada", tc.getServicedSource());
	}

	public final void testGetSecurityHashCode()
	{
	}

	public final void testSetGetDatasourceProtocol()
	{
		tc.setDatasourceProtocol("vftp");
		assertEquals("vftp", tc.getDatasourceProtocol());
	}

	public final void testSetGetFacadeBytesSent()
	{
		tc.setFacadeBytesSent(655321L);
		assertEquals(new Long(655321), tc.getFacadeBytesSent());
	}

	public final void testSetGetFacadeBytesReceived()
	{
		tc.setFacadeBytesReceived(655321L);
		assertEquals(new Long(655321), tc.getFacadeBytesReceived());
	}

	public final void testSetGetDataSourceBytesSent()
	{
		tc.setDataSourceBytesSent(655321L);
		assertEquals(new Long(655321), tc.getDataSourceBytesSent());
	}

	public final void testSetGetDataSourceBytesReceived()
	{
		tc.setDataSourceBytesReceived(655321L);
		assertEquals(new Long(655321), tc.getDataSourceBytesReceived());
	}

	public final void testSetGetModality()
	{
		tc.setModality("MR");
		assertEquals("MR", tc.getModality());
	}

	public final void testSetGetProtocolOverride()
	{
		tc.setOverrideProtocol("cdtp");
		assertEquals("cdtp", tc.getOverrideProtocol());
	}

	public final void testSetGetTargetServer() 
	throws RoutingTokenFormatException
	{
		
		tc.setOverrideRoutingToken(RoutingTokenImpl.createVADocumentSite("420"));
		assertEquals(RoutingTokenImpl.createVADocumentSite("420"), tc.getOverrideRoutingToken());
	}

	public final void testClearAndMemento()
	{
		TransactionContextMemento memento = tc.getMemento();
		tc.clear();
		
		//assertNull( tc.getAccessCode() );
		
		tc.setMemento(memento);
	}
	
	public final void testDebugString()
	{
		System.out.println( tc.getContextDebugState() );
	}
	
	public final void testMemento1()
	{
		TransactionContext tc;
		
		List<String> roles = new ArrayList<String>();
		Map<String, Object> properties = new HashMap<String, Object>();
		ClientPrincipal cp = new ClientPrincipal("100", true, AuthenticationCredentialsType.Password,
				"hello", "world", "duz", "Alex DeLarge", "111-11-1111", "100", "Peoria", roles, properties);
		TransactionContextFactory.createClientTransactionContext(cp);
		
		tc = TransactionContextFactory.get();

		tc.setChildRequestType("childRequest");
		
		TransactionContextMemento memento = tc.getMemento();
		tc.clear();
		
		tc.setMemento(memento);
		
		assertTrue( tc.isAuthenticatedByDelegate().booleanValue() );
		assertEquals(AuthenticationCredentialsType.Password.toString(), tc.getCredentialsType());
		assertEquals("100", tc.getRealm());
		assertEquals("hello", tc.getAccessCode());
		assertEquals("world", tc.getVerifyCode());
		assertEquals("duz", tc.getDuz());
		assertEquals("Alex DeLarge", tc.getFullName());
		assertEquals("111-11-1111", tc.getSsn());
		assertEquals("100", tc.getSiteNumber());
		assertEquals("Peoria", tc.getSiteName()); 
		assertEquals("childRequest", tc.getChildRequestType()); 
	}
	
	public final void testMementoSynchronousModification() 
	throws InterruptedException, BrokenBarrierException
	{
		TransactionContext tc;
		
		List<String> roles = new ArrayList<String>();
		Map<String, Object> properties = new HashMap<String, Object>();
		ClientPrincipal cp = new ClientPrincipal("100", true, AuthenticationCredentialsType.Password,
				"hello", "world", "duz", "Alex DeLarge", "111-11-1111", "100", "Peoria", roles, properties);
		TransactionContextFactory.createClientTransactionContext(cp);
		
		tc = TransactionContextFactory.get();
		tc.setChildRequestType("childRequest");
		
		CyclicBarrier barrier = new CyclicBarrier(3);

		TransactionContextModifier m1 = new TransactionContextModifier(tc, 1000, barrier);
		TransactionContextModifier m2 = new TransactionContextModifier(tc, 1000, barrier);

		m1.start();
		m2.start();
		
		barrier.await();
		
		assertTrue( tc.isAuthenticatedByDelegate().booleanValue() );
		assertEquals(AuthenticationCredentialsType.Password.toString(), tc.getCredentialsType());
		assertEquals("100", tc.getRealm());
		assertEquals("hello", tc.getAccessCode());
		assertEquals("world", tc.getVerifyCode());
		assertEquals("duz", tc.getDuz());
		assertEquals("Alex DeLarge", tc.getFullName());
		assertEquals("111-11-1111", tc.getSsn());
		assertEquals("100", tc.getSiteNumber());
		assertEquals("Peoria", tc.getSiteName()); 
		assertEquals("childRequest", tc.getChildRequestType()); 
	
		assertNotNull(tc.getDatasourceProtocol());
	}
	
	@Ignore
	private class TransactionContextModifier 
	extends Thread
	{
		private final TransactionContext tc;
		private final int iterations;
		private boolean complete = false;
		private CyclicBarrier barrier;
		
		TransactionContextModifier(TransactionContext tc, int iterations, CyclicBarrier barrier)
		{
			this.tc = tc;
			this.iterations = iterations;
			this.barrier = barrier;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run()
		{
			for(int n=0; n < iterations; ++n)
			{
				tc.setDatasourceProtocol("DS" + n);
			}
			complete = true;
			try
			{
				barrier.await();
			} 
			catch (InterruptedException x)
			{
				x.printStackTrace();
			} 
			catch (BrokenBarrierException x)
			{
				x.printStackTrace();
			}
		}

		/**
		 * @return the complete
		 */
		protected boolean isComplete()
		{
			return this.complete;
		}
	}	
}
