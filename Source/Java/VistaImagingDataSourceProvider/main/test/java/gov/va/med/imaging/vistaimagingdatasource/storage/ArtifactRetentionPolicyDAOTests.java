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

import gov.va.med.imaging.exchange.business.storage.ArtifactRetentionPolicy;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.exchange.business.storage.exceptions.UpdateException;

import org.junit.Assert;
import org.junit.Test;

public class ArtifactRetentionPolicyDAOTests
{
	ArtifactRetentionPolicyDAO dao = new ArtifactRetentionPolicyDAO();
	
	@Test
	public void testTranslateCreate() throws CreationException
	{
		ArtifactRetentionPolicy artifactRetentionPolicy = new ArtifactRetentionPolicy();
		Assert.assertTrue(artifactRetentionPolicy.getId() == 0);

		StringBuilder builder = new StringBuilder();
		builder.append("0^^5");
		artifactRetentionPolicy = dao.translateCreate(artifactRetentionPolicy, builder.toString());
		
		Assert.assertTrue(artifactRetentionPolicy.getId() == 5);
		
	}

	@Test
	public void testTranslateUpdate() throws Exception
	{
		dao.translateUpdate(new ArtifactRetentionPolicy(), "0");
	}
	
	@Test (expected=UpdateException.class)
	public void testTranslateUpdateWithException() throws UpdateException
	{
		dao.translateUpdate(new ArtifactRetentionPolicy(), "-1^Entity Not Found");
	}
}