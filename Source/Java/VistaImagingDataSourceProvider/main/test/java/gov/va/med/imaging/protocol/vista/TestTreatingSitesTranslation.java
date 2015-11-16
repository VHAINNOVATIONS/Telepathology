/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 18, 2012
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
package gov.va.med.imaging.protocol.vista;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author VHAISWWERFEJ
 *
 */
public class TestTreatingSitesTranslation
{
	@Test
	public void testIncludingTrailingCharacters()
	{
		StringBuilder vistaResult = new StringBuilder();
		vistaResult.append("4^1~Treating facilities returned\n");
		vistaResult.append("660^SALT LAKE CITY^3050829^^\n");
		vistaResult.append("688^WASHINGTON, DC^3050229^^\n");
		vistaResult.append("756^EL PASO, TX^3050229^^\n");
		vistaResult.append("200^AUSTIN^3050229^^DPC\n");
		vistaResult.append("A200DOD^AUSTIN^3050229^^DPC\n");
		vistaResult.append("200DOD^AUSTIN^3050229^^DPC\n");
		
		List<String> sites = VistaImagingTranslator.convertTreatingSiteListToSiteNumbers(vistaResult.toString(), 
				true);
		assertEquals(5, sites.size());
	}
	
	@Test
	public void testExcludingTrailingCharacters()
	{
		StringBuilder vistaResult = new StringBuilder();
		vistaResult.append("4^1~Treating facilities returned\n");
		vistaResult.append("660^SALT LAKE CITY^3050829^^\n");
		vistaResult.append("688^WASHINGTON, DC^3050229^^\n");
		vistaResult.append("756^EL PASO, TX^3050229^^\n");
		vistaResult.append("200^AUSTIN^3050229^^DPC\n");
		vistaResult.append("A200DOD^AUSTIN^3050229^^DPC\n");
		vistaResult.append("200DOD^AUSTIN^3050229^^DPC\n");
		
		List<String> sites = VistaImagingTranslator.convertTreatingSiteListToSiteNumbers(vistaResult.toString(), 
				false);
		assertEquals(4, sites.size());
	}

}
