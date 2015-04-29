/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 9, 2012
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
package gov.va.med.imaging.pathology;

import gov.va.med.URNFactory;
import gov.va.med.imaging.pathology.enums.PathologyField;

import org.junit.Test;


import static org.junit.Assert.*;

/**
 * @author VHAISWWERFEJ
 *
 */
public class PathologyFieldURNTest
{
	
	@Test
	public void testPathologyFieldURN()
	{
		try
		{
			PathologyFieldURN pathologyFieldUrn =
				PathologyFieldURN.create("660", PathologyField.morphology, "123");
			assertEquals("urn:vapathologyfield:660-morphology-123", pathologyFieldUrn.toString());
			assertNotNull(pathologyFieldUrn.getPathologyField());
			
			PathologyFieldURN newPathologyFieldUrn =
				URNFactory.create(pathologyFieldUrn.toString(), PathologyFieldURN.class);
			assertTrue(pathologyFieldUrn.equals(newPathologyFieldUrn));
			assertEquals(pathologyFieldUrn.toString(), newPathologyFieldUrn.toString());
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}

}
