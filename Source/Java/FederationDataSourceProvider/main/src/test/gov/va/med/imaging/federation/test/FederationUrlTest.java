/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 14, 2008
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
package gov.va.med.imaging.federation.test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author VHAISWWERFEJ
 *
 */
public class FederationUrlTest 
extends FederationTestBase 
{

	public void testValidFederationUrl()
	{
		try
		{
			URL url = new URL("vftp://localhost:8080");		
			System.out.println("Created url [" + url + "]");
		}
		catch(MalformedURLException murlX)
		{
			fail(murlX.toString());
		}
		try
		{
			URL url = new URL("VFTP://localhost:8080");		
			System.out.println("Created url [" + url + "]");
		}
		catch(MalformedURLException murlX)
		{
			fail(murlX.toString());
		}
		try
		{
			URL url = new URL("vFtP://localhost:8080");		
			System.out.println("Created url [" + url + "]");
		}
		catch(MalformedURLException murlX)
		{
			fail(murlX.toString());
		}
	}
	
	public void testInvalidFederationUrls()
	{
		URL url = null;
		try
		{
			url = new URL("federation://localhost:8080");
			fail("URL [" + url + "] is invalid");
		}
		catch(MalformedURLException murlX) {}
		
		try
		{
			url = new URL("");
			fail("URL [" + url + "] is invalid");
		}
		catch(MalformedURLException murlX) {}
		
	}

}
