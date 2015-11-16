/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Jul 28, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */

package gov.va.med.configuration;

import java.net.URI;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 * 
 */
public class TestConfiguration
	extends TestCase
{
	private Configuration root = null;
	private MockConfiguration mockConfiguration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		try
		{
			root = ConfigurationInvocationHandler.getRootConfiguration();
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}

		mockConfiguration = root.createContext(MockConfiguration.class, "MockConfiguration");

		mockConfiguration.addObserver(new NotificationReceiver());
	}

	/**
	 * 
	 * @throws Throwable
	 */
	public void testSimpleTypes() throws Throwable
	{
		Integer expected = new Integer(99);
		mockConfiguration.setPort(expected);
		Integer actual = mockConfiguration.getPort();
		assertEquals(expected, actual);
	}

	public void testArrayTypes() throws Throwable
	{
		String[] expectedParameters = new String[]
		{ "hello", "world" };
		mockConfiguration.setProtocolPreferenceFactoryParameters(expectedParameters);
		String[] actualParameters = mockConfiguration.getProtocolPreferenceFactoryParameters();
		assertEquals(expectedParameters, actualParameters);
	}

	public void testSubConfiguration() throws Throwable
	{
		URI expectedSubHost = new URI("http://www.happycat.biz/x/y");
		mockConfiguration.setSiteServiceUri(expectedSubHost);
		URI actualSubHost = mockConfiguration.getSiteServiceUri();
		assertEquals(expectedSubHost, actualSubHost);
		
		MockSubConfiguration subConfig = mockConfiguration.getSubConfiguration();
		assertNotNull(subConfig);
		URI actualSubHost2 = subConfig.getSiteServiceUri();
		assertEquals(expectedSubHost, actualSubHost2);
	}

	public void testMapTypes() throws Throwable
	{
		mockConfiguration.setProtocolPreference("Key1", "Value1");
		assertNotNull(mockConfiguration.getProtocolPreference());
		assertEquals("Value1", mockConfiguration.getProtocolPreference("Key1"));
	}

	public void testListTypes() throws Throwable
	{
	}
	
	public void testPersistence() throws Throwable
	{
		root.store();
	}

	class NotificationReceiver
		implements ConfigurationObserver
	{
		@Override
		public void configurationEvent(ConfigurationEvent event)
		{
			System.out.println(event.toString());
		}
	}
}
