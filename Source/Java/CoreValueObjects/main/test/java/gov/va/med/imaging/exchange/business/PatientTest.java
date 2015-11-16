/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 7, 2012
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
package gov.va.med.imaging.exchange.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import gov.va.med.imaging.exchange.business.Patient.PatientSex;

import org.junit.Test;

/**
 * @author VHAISWWERFEJ
 *
 */
public class PatientTest
{
	
	@Test
	public void testSsnFormatting()
	{
		String originalSsn = "123456789";
		Patient p = new Patient("test,name", null, null, PatientSex.Female, null, originalSsn, null, false);
		assertNotSame(originalSsn, p.getFilteredSsn());
		assertEquals("*****6789", p.getFilteredSsn());
	}
	
	@Test
	public void testSsnWithDashesFormatting()
	{
		String originalSsn = "123-45-6789";
		Patient p = new Patient("test,name", null, null, PatientSex.Female, null, originalSsn, null, false);
		String formattedSsn = p.getFilteredSsn();
		assertNotSame(originalSsn, formattedSsn);
		assertEquals("*****6789", formattedSsn);
		
		originalSsn = "12345-6789";
		p = new Patient("test,name", null, null, PatientSex.Female, null, originalSsn, null, false);
		formattedSsn = p.getFilteredSsn();
		assertNotSame(originalSsn, formattedSsn);
		assertEquals("*****6789", formattedSsn);
		
		originalSsn = "123-456789";
		p = new Patient("test,name", null, null, PatientSex.Female, null, originalSsn, null, false);
		formattedSsn = p.getFilteredSsn();
		assertNotSame(originalSsn, formattedSsn);
		assertEquals("*****6789", formattedSsn);
	}

}
