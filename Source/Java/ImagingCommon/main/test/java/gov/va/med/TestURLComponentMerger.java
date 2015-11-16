/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Oct 14, 2010
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

package gov.va.med;

import java.net.MalformedURLException;
import java.net.URL;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestURLComponentMerger
extends TestCase
{
	public void testURLPrecedence() 
	throws MalformedURLException
	{
		URLComponentMerger merger = null;
		URL urlSource = null;
		URL urlResult = null;
		
		merger = new URLComponentMerger(
			"ftp", 
			null, null, 
			"hostname", -1, "filename", 
			URLComponentMerger.URLComponentMergerPrecedence.URLFirst
		);
		urlSource = new URL("http://www.google.com");
		urlResult = merger.merge(urlSource);
		assertEquals("http", urlResult.getProtocol());
		assertEquals(null, urlResult.getUserInfo());
		assertEquals("www.google.com", urlResult.getHost());
		assertEquals(-1, urlResult.getPort());
		assertEquals("/filename", urlResult.getFile());
		
		merger = new URLComponentMerger(
			"ftp", 
			"userid", "password", 
			"hostname", -1, "filename", 
			URLComponentMerger.URLComponentMergerPrecedence.URLFirst
		);
		urlSource = new URL("http://www.google.com");
		urlResult = merger.merge(urlSource);
		assertEquals("http", urlResult.getProtocol());
		assertEquals("userid:password", urlResult.getUserInfo());
		assertEquals("www.google.com", urlResult.getHost());
		assertEquals(-1, urlResult.getPort());
		assertEquals("/filename", urlResult.getFile());
		
		merger = new URLComponentMerger(
			"ftp", 
			"userid", "password", 
			"hostname", 42, "filename", 
			URLComponentMerger.URLComponentMergerPrecedence.URLFirst
		);
		urlSource = new URL("http://www.google.com");
		urlResult = merger.merge(urlSource);
		assertEquals("http", urlResult.getProtocol());
		assertEquals("userid:password", urlResult.getUserInfo());
		assertEquals("www.google.com", urlResult.getHost());
		assertEquals(42, urlResult.getPort());
		assertEquals("/filename", urlResult.getFile());
	}
	
	public void testMergerComponentsPrecedence() 
	throws MalformedURLException
	{
		URLComponentMerger merger = null;
		URL urlSource = null;
		URL urlResult = null;
		
		merger = new URLComponentMerger(
			"ftp", 
			null, null, 
			"hostname", -1, null, 
			URLComponentMerger.URLComponentMergerPrecedence.MergerComponentsFirst
		);
		urlSource = new URL("http://www.google.com/yada");
		urlResult = merger.merge(urlSource);
		assertEquals("ftp", urlResult.getProtocol());
		assertEquals(null, urlResult.getUserInfo());
		assertEquals("hostname", urlResult.getHost());
		assertEquals(-1, urlResult.getPort());
		assertEquals("/yada", urlResult.getFile());
		
		merger = new URLComponentMerger(
			"ftp", 
			null, null, 
			"hostname", -1, "filename", 
			URLComponentMerger.URLComponentMergerPrecedence.MergerComponentsFirst
		);
		urlSource = new URL("http://www.google.com/yada");
		urlResult = merger.merge(urlSource);
		assertEquals("ftp", urlResult.getProtocol());
		assertEquals(null, urlResult.getUserInfo());
		assertEquals("hostname", urlResult.getHost());
		assertEquals(-1, urlResult.getPort());
		assertEquals("/filename", urlResult.getFile());
		
		urlSource = new URL("http://www.google.com");
		urlResult = merger.merge(urlSource);
		assertEquals("/filename", urlResult.getFile());

		// this is the typical case for a data source provider
		merger = new URLComponentMerger(
			"ftp", 
			"userid", "password", 
			null, -1, "filename", 
			URLComponentMerger.URLComponentMergerPrecedence.MergerComponentsFirst
		);
		urlSource = new URL("http://www.google.com:80");
		urlResult = merger.merge(urlSource);
		assertEquals("ftp", urlResult.getProtocol());
		assertEquals("userid:password", urlResult.getUserInfo());
		assertEquals("www.google.com", urlResult.getHost());
		assertEquals(80, urlResult.getPort());
		assertEquals("/filename", urlResult.getFile());
	}
	
	public void testMergerProtocolOverride() 
	throws MalformedURLException
	{
		URLComponentMerger merger = null;
		URL urlSource = null;
		URL urlResult = null;
		
		merger = new URLComponentMerger(
			"ftp", 
			"userid", "password", 
			"hostname", 32, null, 
			URLComponentMerger.URLComponentMergerPrecedence.URLFirstOverrideProtocol
		);
		urlSource = new URL("http://me:pwd@www.google.com");
		urlResult = merger.merge(urlSource);
		assertEquals("ftp", urlResult.getProtocol());
		assertEquals("me:pwd", urlResult.getUserInfo());
		assertEquals("www.google.com", urlResult.getHost());
		assertEquals(32, urlResult.getPort());
		assertEquals("", urlResult.getFile());
		
		merger = new URLComponentMerger(
			"ftp", 
			null, null, 
			"hostname", -1, "filename", 
			URLComponentMerger.URLComponentMergerPrecedence.URLFirstOverrideProtocol
		);
		urlSource = new URL("http://www.google.com");
		urlResult = merger.merge(urlSource);
		assertEquals("ftp", urlResult.getProtocol());
		assertEquals(null, urlResult.getUserInfo());
		assertEquals("www.google.com", urlResult.getHost());
		assertEquals(-1, urlResult.getPort());
		assertEquals("/filename", urlResult.getFile());
		
		merger = new URLComponentMerger(
			null, 
			"userid", "password", 
			"hostname", -1, "filename", 
			URLComponentMerger.URLComponentMergerPrecedence.URLFirst
		);
		urlSource = new URL("http://www.google.com/thing");
		urlResult = merger.merge(urlSource);
		assertEquals("http", urlResult.getProtocol());
		assertEquals("userid:password", urlResult.getUserInfo());
		assertEquals("www.google.com", urlResult.getHost());
		assertEquals(-1, urlResult.getPort());
		assertEquals("/thing", urlResult.getFile());
	}
}
