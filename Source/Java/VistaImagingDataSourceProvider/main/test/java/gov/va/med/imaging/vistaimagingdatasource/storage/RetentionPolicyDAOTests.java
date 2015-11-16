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

import gov.va.med.imaging.exchange.business.storage.RetentionPolicy;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.exchange.business.storage.exceptions.UpdateException;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class RetentionPolicyDAOTests
{
	private RetentionPolicyDAO dao = new RetentionPolicyDAO();
	
	@Test
	public void testTranslateCreate() 
	{
		throw new NotImplementedException();
	}

	@Test
	public void testTranslateFindAll() throws RetrievalException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("0^^2\r\n");
		builder.append("IEN^DISPLAY NAME^ARCHIVE DURATION YEARS^ARCHIVE DURATION TRIGGER^MINIMUM ARCHIVE COPIES^MINIMUM OFFSITE COPIES^BUSINESS KEY^IS ACTIVE\r\n");
		builder.append("2^Medical Information Retention Policy 25^25^LAD^1^2^BKEY1^0\r\n");
		builder.append("1^Medical Information Retention Policy 75^75^DD^3^4^BKEY2^1");
		List<RetentionPolicy> list = dao.translateFindAll(builder.toString());
		 
		Assert.assertTrue(list.size() == 2);
		
		RetentionPolicy item = list.get(0);
		Assert.assertTrue(item.getId() == 2);
		Assert.assertTrue(item.getDisplayName().equals("Medical Information Retention Policy 25"));
		Assert.assertTrue(item.getArchiveDurationYears().equals("25"));
		Assert.assertTrue(item.getArchiveDurationTrigger().equals("LAD"));
		Assert.assertTrue(item.getMinimumArchiveCopies() == (1));
		Assert.assertTrue(item.getMinimumOffsiteCopies() == (2));
		Assert.assertTrue(item.getBusinessKey().equals("BKEY1"));
		Assert.assertTrue(item.isActive() == false);

		item = list.get(1);
		Assert.assertTrue(item.getId() == 1);
		Assert.assertTrue(item.getDisplayName().equals("Medical Information Retention Policy 75"));
		Assert.assertTrue(item.getArchiveDurationYears().equals("75"));
		Assert.assertTrue(item.getArchiveDurationTrigger().equals("DD"));
		Assert.assertTrue(item.getMinimumArchiveCopies() == (3));
		Assert.assertTrue(item.getMinimumOffsiteCopies() == (4));
		Assert.assertTrue(item.getBusinessKey().equals("BKEY2"));
		Assert.assertTrue(item.isActive() == true);

	}

	@Test
	public void testTranslateFindAllNoResults() throws RetrievalException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("0^^0\r\n");
		builder.append("IEN^DISPLAY NAME^ARCHIVE DURATION YEARS^ARCHIVE DURATION TRIGGER^MINIMUM ARCHIVE COPIES^MINIMUM OFFSITE COPIES^BUSINESS KEY^IS ACTIVE");
		List<RetentionPolicy> list = dao.translateFindAll(builder.toString());
		 
		Assert.assertTrue(list.size() == 0);

	}

	@Test (expected=RetrievalException.class)
	public void testTranslateFindAllWithException() throws RetrievalException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("-1^Retrieval Exception^");
		List<RetentionPolicy> list = dao.translateFindAll(builder.toString());
		 
		Assert.assertTrue(list.size() == 0);

	}

	@Test
	public void testTranslateUpdate() 
	{
		throw new NotImplementedException();
	}

	
	
}