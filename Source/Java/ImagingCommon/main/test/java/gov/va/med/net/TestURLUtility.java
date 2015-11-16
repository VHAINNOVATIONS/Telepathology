/**
 * 
 */
package gov.va.med.net;

import java.net.MalformedURLException;
import java.net.URL;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestURLUtility
	extends TestCase
{

	/**
	 * 
	 */
	private static final String COMPLETE_HTTP_URL = 
		"http://chris:beckey@www.google.com/search?source=ig&hl=en&rlz=&=&q=junk&aq=f&aqi=g10&aql=&oq=#lastOne";

	/**
	 * Test method for {@link gov.va.med.net.URLUtility#changeProtocol(java.net.URL, java.lang.String)}.
	 * @throws MalformedURLException 
	 */
	public void testChangeProtocol()
	throws MalformedURLException
	{
		URL sourceUrl = new URL(COMPLETE_HTTP_URL);
		URL destinationUrl = URLUtility.changeProtocol(sourceUrl, "ftp");
		
		assertEquals("ftp", destinationUrl.getProtocol());
		assertEquals(sourceUrl.getUserInfo(), destinationUrl.getUserInfo());
		assertEquals(sourceUrl.getHost(), destinationUrl.getHost());
		assertEquals(sourceUrl.getPort(), destinationUrl.getPort());
		assertEquals(sourceUrl.getPath(), destinationUrl.getPath());
		assertEquals(sourceUrl.getQuery(), destinationUrl.getQuery());
		assertEquals(sourceUrl.getRef(), destinationUrl.getRef());
	}

	/**
	 * Test method for {@link gov.va.med.net.URLUtility#removeUserInfo(java.net.URL)}.
	 */
	public void testRemoveUserInfo()
	throws MalformedURLException
	{
		URL sourceUrl = new URL(COMPLETE_HTTP_URL);
		URL destinationUrl = URLUtility.removeUserInfo(sourceUrl);
		
		assertEquals(sourceUrl.getProtocol(), destinationUrl.getProtocol());
		assertEquals(null, destinationUrl.getUserInfo());
		assertEquals(sourceUrl.getHost(), destinationUrl.getHost());
		assertEquals(sourceUrl.getPort(), destinationUrl.getPort());
		assertEquals(sourceUrl.getPath(), destinationUrl.getPath());
		assertEquals(sourceUrl.getQuery(), destinationUrl.getQuery());
		assertEquals(sourceUrl.getRef(), destinationUrl.getRef());
	}

	/**
	 * Test method for {@link gov.va.med.net.URLUtility#changeUserInfo(java.net.URL, java.lang.String)}.
	 */
	public void testChangeUserInfo()
	throws MalformedURLException
	{
		URL sourceUrl = new URL(COMPLETE_HTTP_URL);
		URL destinationUrl = URLUtility.changeUserInfo(sourceUrl, "a:b");
		
		assertEquals(sourceUrl.getProtocol(), destinationUrl.getProtocol());
		assertEquals("a:b", destinationUrl.getUserInfo());
		assertEquals(sourceUrl.getHost(), destinationUrl.getHost());
		assertEquals(sourceUrl.getPort(), destinationUrl.getPort());
		assertEquals(sourceUrl.getPath(), destinationUrl.getPath());
		assertEquals(sourceUrl.getQuery(), destinationUrl.getQuery());
		assertEquals(sourceUrl.getRef(), destinationUrl.getRef());
	}

	/**
	 * Test method for {@link gov.va.med.net.URLUtility#changeHost(java.net.URL, java.lang.String)}.
	 */
	public void testChangeHost()
	throws MalformedURLException
	{
		URL sourceUrl = new URL(COMPLETE_HTTP_URL);
		URL destinationUrl = URLUtility.changeHost(sourceUrl, "www.bing.com");
		
		assertEquals(sourceUrl.getProtocol(), destinationUrl.getProtocol());
		assertEquals(sourceUrl.getUserInfo(), destinationUrl.getUserInfo());
		assertEquals("www.bing.com", destinationUrl.getHost());
		assertEquals(sourceUrl.getPort(), destinationUrl.getPort());
		assertEquals(sourceUrl.getPath(), destinationUrl.getPath());
		assertEquals(sourceUrl.getQuery(), destinationUrl.getQuery());
		assertEquals(sourceUrl.getRef(), destinationUrl.getRef());
	}

	/**
	 * Test method for {@link gov.va.med.net.URLUtility#changePort(java.net.URL, int)}.
	 */
	public void testChangePort()
	throws MalformedURLException
	{
		URL sourceUrl = new URL(COMPLETE_HTTP_URL);
		URL destinationUrl = URLUtility.changePort(sourceUrl, 8080);
		
		assertEquals(sourceUrl.getProtocol(), destinationUrl.getProtocol());
		assertEquals(sourceUrl.getUserInfo(), destinationUrl.getUserInfo());
		assertEquals(sourceUrl.getHost(), destinationUrl.getHost());
		assertEquals(8080, destinationUrl.getPort());
		assertEquals(sourceUrl.getPath(), destinationUrl.getPath());
		assertEquals(sourceUrl.getQuery(), destinationUrl.getQuery());
		assertEquals(sourceUrl.getRef(), destinationUrl.getRef());
	}

	/**
	 * Test method for {@link gov.va.med.net.URLUtility#changePath(java.net.URL, java.lang.String)}.
	 */
	public void testChangePath()
	throws MalformedURLException
	{
		URL sourceUrl = new URL(COMPLETE_HTTP_URL);
		URL destinationUrl = URLUtility.changePath(sourceUrl, "/find");
		
		assertEquals(sourceUrl.getProtocol(), destinationUrl.getProtocol());
		assertEquals(sourceUrl.getUserInfo(), destinationUrl.getUserInfo());
		assertEquals(sourceUrl.getHost(), destinationUrl.getHost());
		assertEquals(sourceUrl.getPort(), destinationUrl.getPort());
		assertEquals("/find", destinationUrl.getPath());
		assertEquals(sourceUrl.getQuery(), destinationUrl.getQuery());
		assertEquals(sourceUrl.getRef(), destinationUrl.getRef());
	}

	/**
	 * Test method for {@link gov.va.med.net.URLUtility#changeQuery(java.net.URL, java.lang.String)}.
	 */
	public void testChangeQuery()
	throws MalformedURLException
	{
		URL sourceUrl = new URL(COMPLETE_HTTP_URL);
		URL destinationUrl = URLUtility.changeQuery(sourceUrl, "a1=42");
		
		assertEquals(sourceUrl.getProtocol(), destinationUrl.getProtocol());
		assertEquals(sourceUrl.getUserInfo(), destinationUrl.getUserInfo());
		assertEquals(sourceUrl.getHost(), destinationUrl.getHost());
		assertEquals(sourceUrl.getPort(), destinationUrl.getPort());
		assertEquals(sourceUrl.getPath(), destinationUrl.getPath());
		assertEquals("a1=42", destinationUrl.getQuery());
		assertEquals(sourceUrl.getRef(), destinationUrl.getRef());
	}

	/**
	 * Test method for {@link gov.va.med.net.URLUtility#changeRef(java.net.URL, java.lang.String)}.
	 */
	public void testChangeReg()
	throws MalformedURLException
	{
		URL sourceUrl = new URL(COMPLETE_HTTP_URL);
		URL destinationUrl = URLUtility.changeRef(sourceUrl, "reference001");
		
		assertEquals(sourceUrl.getProtocol(), destinationUrl.getProtocol());
		assertEquals(sourceUrl.getUserInfo(), destinationUrl.getUserInfo());
		assertEquals(sourceUrl.getHost(), destinationUrl.getHost());
		assertEquals(sourceUrl.getPort(), destinationUrl.getPort());
		assertEquals(sourceUrl.getPath(), destinationUrl.getPath());
		assertEquals(sourceUrl.getQuery(), destinationUrl.getQuery());
		assertEquals("reference001", destinationUrl.getRef());
	}
}
