/**
 * 
 */
package gov.va.med;

import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestProtocolSchema
	extends TestCase
{

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		ProtocolHandlerUtility.initialize(true);
	}

	public void testHandlerAvailability()
	{
		assertTrue( ProtocolSchema.CDTP.isConnectionHandlerInstalled() );
		assertTrue( ProtocolSchema.EXCHANGE.isConnectionHandlerInstalled() );
		assertTrue( ProtocolSchema.HTTP.isConnectionHandlerInstalled() );
		assertTrue( ProtocolSchema.VFTP.isConnectionHandlerInstalled() );
		assertTrue( ProtocolSchema.VISTAIMAGING.isConnectionHandlerInstalled() );
		assertTrue( ProtocolSchema.XCA.isConnectionHandlerInstalled() );
	}
	
	public void testSchemaValues()
	{
		assertEquals("cdtp", ProtocolSchema.CDTP.toString());
		assertEquals("exchange", ProtocolSchema.EXCHANGE.toString());
		assertEquals("http", ProtocolSchema.HTTP.toString());
		assertEquals("vftp", ProtocolSchema.VFTP.toString());
		assertEquals("vista", ProtocolSchema.VISTA.toString());
		assertEquals("vistaimaging", ProtocolSchema.VISTAIMAGING.toString());
		assertEquals("xca", ProtocolSchema.XCA.toString());
	}
}
