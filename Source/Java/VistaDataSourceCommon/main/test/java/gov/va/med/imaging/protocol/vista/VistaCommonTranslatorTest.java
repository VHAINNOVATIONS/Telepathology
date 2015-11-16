/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 7, 2011
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
public class VistaCommonTranslatorTest
{
	
	@Test
	public void removeExtraCharacters()
	{ 
		// new sort order based on using hash to exclude duplicates
		String [] expectedSiteNumbers = new String [] {
				"675",
				"504",
				"200",
				"672",
				"508",
				"501",
				"502",
				"436",
				"463",
				"405",
				"442",
				"740",
				"402",
				"437",
		"438"};
		
		String siteList = 
				"463^ALASKA VA HLTCR SYSTEM (AVAHS)^3110414.10321^3^" +
				"OC402^VA MAINE HCS^3110526.140421^3^" +
				"M&ROC405^WHITE RIVER JCT VAMROC^3110628.103425^3^" +
				"M&ROC436^MONTANA HCS^3110624.153^3^" +
				"M&ROC437^FARGO VA HCS^3110516.1304^3^" +
				"VAMC438^SIOUX FALLS VA HCS^3100308.124743^3^" +
				"VAMC442^CHEYENNE VAMC^3110316.124751^3^" +
				"M&ROC672^SAN JUAN VAMC^3110629.1^3^" +
				"VAMC501^NEW MEXICO HCS^3101029.1332^3^" +
				"VAMC502^ALEXANDRIA VAMC^3110622.104636^3^" +
				"VAMC504^AMARILLO HCS^3110629.113^3^" +
				"VAMC508^ATLANTA VAMC^3100309.0808^3^" + 
				"OTHER200T1^TELEHEALTH HEALTH HERO^^^" +
				"OTHER200T4^TELEHEALTH VITEL NET^^^" +
				"OTHER200T5^TELEHEALTH VITERION^^^" +
				"OTHER200ESR^ENROLLMENT SYSTEM REDESIGN^^^" +
				"OTHER675^ORLANDO VAMC^3101028.104907^3^" +
				"VAMC740^TEXAS VALLEY COASTAL BEND HCS^3110531.1132^3^VAMC";

		List<String> sites =
			VistaCommonTranslator.convertSiteStringToSiteStringList(siteList);
		assertEquals(15, sites.size());
		for(int i = 0; i < sites.size(); i++)
		{
			assertEquals(expectedSiteNumbers[i], sites.get(i));
			//System.out.println(sites.get(i));
		}
		
		
	}
	
	@Test
	public void testCprsTranslator()
	{
		String input = "644^PHOENIX, AZ^3050229^^1\n" + 
			"688^WASHINGTON, DC^3050229^^1\n" + 
			"756^EL PASO, TX^3050229^^1\n" + 
			"200^DEPT. OF DEFENSE^3050229^^1";
		
		String [] expectedSiteNumbers = new String [] {"644", "688", "756", "200"};
		
		List<String> sites = VistaCommonTranslator.convertCprsSiteList(input);
		assertEquals(expectedSiteNumbers.length, sites.size());
		for(int i = 0; i < sites.size(); i++)
		{
			assertEquals(expectedSiteNumbers[i], sites.get(i));
		}
	}
}
