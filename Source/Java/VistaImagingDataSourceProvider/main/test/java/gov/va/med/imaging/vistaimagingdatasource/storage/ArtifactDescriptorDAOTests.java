/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Nov, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
  Description: DICOM Study cache manager. Maintains the cache of study instances
  			   and expires old studies after 15 minutes. 

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

package gov.va.med.imaging.vistaimagingdatasource.storage;

import gov.va.med.imaging.exchange.business.storage.ArtifactDescriptor;
import gov.va.med.imaging.exchange.business.storage.RetentionPolicy;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.exchange.business.storage.exceptions.UpdateException;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ArtifactDescriptorDAOTests
{
	
	private ArtifactDescriptorDAO dao = new ArtifactDescriptorDAO();

	@Test
	public void testTranslateFindAll() throws RetrievalException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("0^^3\r\n");
		builder.append("IEN^RETENTION POLICY FK^ARTIFACT TYPE^ARTIFACT FORMAT^FILE EXTENSION^IS ACTIVE\r\n");
		builder.append("1^4^MedicalImage^DICOM`dcm^1\r\n");
		builder.append("3^5^MedicalImageTextFile^DCM TEXT FILE^txt^0\r\n");
		builder.append("2^6^MedicalImageAbstract^JPEG^jpg^1");
		List<ArtifactDescriptor> list = dao.translateFindAll(builder.toString());
		 
		Assert.assertTrue(list.size() == 3);
		
		ArtifactDescriptor item = list.get(0);
		Assert.assertTrue(item.getId() == 1);
		Assert.assertTrue(item.getRetentionPolicyId() == 4);
		Assert.assertTrue(item.getArtifactType().equals("MedicalImage"));
		Assert.assertTrue(item.getArtifactFormat().equals("DICOM"));
		Assert.assertTrue(item.getFileExtension().equals("dcm"));
		Assert.assertTrue(item.isActive() == true);

		item = list.get(1);
		Assert.assertTrue(item.getId() == 3);
		Assert.assertTrue(item.getRetentionPolicyId() == 5);
		Assert.assertTrue(item.getArtifactType().equals("MedicalImageTextFile"));
		Assert.assertTrue(item.getArtifactFormat().equals("DCM TEXT FILE"));
		Assert.assertTrue(item.getFileExtension().equals("txt"));
		Assert.assertTrue(item.isActive() == false);

		item = list.get(2);
		Assert.assertTrue(item.getId() == 2);
		Assert.assertTrue(item.getRetentionPolicyId() == 6);
		Assert.assertTrue(item.getArtifactType().equals("MedicalImageAbstract"));
		Assert.assertTrue(item.getArtifactFormat().equals("JPEG"));
		Assert.assertTrue(item.getFileExtension().equals("jpg"));
		Assert.assertTrue(item.isActive() == true);

	}
	
	@Test
	public void testTranslateFindAllNoResults() throws RetrievalException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("0^^0\r\n");
		builder.append("IEN^RETENTION POLICY FK^ARTIFACT TYPE^ARTIFACT FORMAT^FILE EXTENSION^IS ACTIVE");
		List<ArtifactDescriptor> list = dao.translateFindAll(builder.toString());
		 
		Assert.assertTrue(list.size() == 0);

	}

	@Test (expected=RetrievalException.class)
	public void testTranslateFindAllWithException() throws RetrievalException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("-1^Retrieval Exception`");
		List<ArtifactDescriptor> list = dao.translateFindAll(builder.toString());
		 
		Assert.assertTrue(list.size() == 0);

	}

	@Test
	public void testTranslateUpdateRetentionPolicy() throws Exception
	{
		dao.translateUpdate(new ArtifactDescriptor(), "0");
	}
	
	@Test (expected=UpdateException.class)
	public void testTranslateUpdateRetentionPolicyWithException() throws UpdateException
	{
		dao.translateUpdate(new ArtifactDescriptor(), "-1^Entity Not Found");
	}
}