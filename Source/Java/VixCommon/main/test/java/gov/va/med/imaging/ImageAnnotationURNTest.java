/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 17, 2011
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
package gov.va.med.imaging;

import gov.va.med.SERIALIZATION_FORMAT;
import gov.va.med.URNFactory;
import gov.va.med.WellKnownOID;
import gov.va.med.imaging.exceptions.URNFormatException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author VHAISWWERFEJ
 *
 */
public class ImageAnnotationURNTest
{
	
	@Test
	public void testAnnotationURN()
	throws URNFormatException
	{
		String originatingSiteId = "siteId";
		String annotationId = "annotationId";
		String imageId = "imageId";
		String patientId = "patId";
		
		ImageAnnotationURN urn =
			ImageAnnotationURN.create(originatingSiteId, annotationId, imageId, patientId);
		System.out.println(urn.toString());
		System.out.println(urn.toStringCDTP());
		//System.out.println(urn.toURI());
		System.out.println(urn.toString(SERIALIZATION_FORMAT.CDTP));
		System.out.println(urn.toString(SERIALIZATION_FORMAT.NATIVE));
		System.out.println(urn.toString(SERIALIZATION_FORMAT.PATCH83_VFTP));
		System.out.println(urn.toString(SERIALIZATION_FORMAT.RAW));
		System.out.println(urn.toString(SERIALIZATION_FORMAT.RFC2141));
		System.out.println(urn.toString(SERIALIZATION_FORMAT.VFTP));
		
		ImageAnnotationURN urn2 =
			URNFactory.create("urn:vaannotation:siteId-annotationId-imageId-patId");
		System.out.println(urn2.toString());
		
		assertEquals(urn.toString(), urn2.toString());
		
		ImageAnnotationURN badUrn =
			URNFactory.create("urn:vaannotation:siteId-annotationId-bad-patId");
		assertNotSame(urn, badUrn);
		
		assertEquals(originatingSiteId, urn.getOriginatingSiteId());
		assertEquals(originatingSiteId, urn.getRepositoryUniqueId());
		assertEquals(originatingSiteId, urn2.getOriginatingSiteId());
		assertEquals(originatingSiteId, urn2.getRepositoryUniqueId());
		
		assertEquals(urn.getDocumentUniqueId(), urn2.getDocumentUniqueId());
		assertEquals(annotationId, urn.getAnnotationId());
		assertEquals(annotationId, urn2.getAnnotationId());
		
		assertEquals(patientId, urn.getPatientId());
		assertEquals(patientId, urn.getPatientIdentifier());
		assertEquals(patientId, urn2.getPatientId());
		assertEquals(patientId, urn2.getPatientIdentifier());
		
		assertEquals(imageId, urn.getImageId());
		assertEquals(imageId, urn2.getImageId());
		
		assertEquals(WellKnownOID.VA_RADIOLOGY_IMAGE.getCanonicalValue().toString(), 
				urn.getHomeCommunityId());
		assertEquals(WellKnownOID.VA_RADIOLOGY_IMAGE.getCanonicalValue().toString(), 
				urn2.getHomeCommunityId());
		
	}

}
