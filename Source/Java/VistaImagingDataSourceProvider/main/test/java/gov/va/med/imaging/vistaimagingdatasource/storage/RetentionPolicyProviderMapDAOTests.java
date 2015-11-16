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

import gov.va.med.imaging.exchange.business.storage.RetentionPolicyProviderMapping;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.exchange.business.storage.exceptions.DeletionException;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.exchange.business.storage.exceptions.UpdateException;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class RetentionPolicyProviderMapDAOTests {
	private RetentionPolicyProviderMapDAO dao = new RetentionPolicyProviderMapDAO();

	@Test
	public void testTranslateCreate() throws CreationException {
		RetentionPolicyProviderMapping mapping = new RetentionPolicyProviderMapping();
		Assert.assertTrue(mapping.getId() == 0);

		StringBuilder builder = new StringBuilder();
		builder.append("0^^5");
		mapping = dao.translateCreate(mapping, builder.toString());

		Assert.assertTrue(mapping.getId() == 5);

	}

	@Test
	public void testTranslateFindAll() throws RetrievalException
	{

		StringBuilder builder = new StringBuilder();
		builder.append("0^^4\r\n");
		builder.append("IEN^RETENTION POLICY FK^PROVIDER FK^SOURCE PLACE FK^IS SYNCHRONOUS^IS OFFSITE\r\n");
		builder.append("3^1^2^1^1^0\r\n");
		builder.append("4^1^2^2^0^1\r\n");
		builder.append("1^2^1^1^1^0\r\n");
		builder.append("2^2^3^2^0^1");
		List<RetentionPolicyProviderMapping> list = dao.translateFindAll(builder.toString());

		Assert.assertTrue(list.size() == 4);

		RetentionPolicyProviderMapping item = list.get(0);
		Assert.assertTrue(item.getId() == 3);
		Assert.assertTrue(item.getRetentionPolicyId() == 1);
		Assert.assertTrue(item.getProviderId() == 2);
		Assert.assertTrue(item.getPlaceId() == 1);
		Assert.assertTrue(item.isSynchronous() == true);
		Assert.assertTrue(item.isOffsite() == false);

		item = list.get(1);
		Assert.assertTrue(item.getId() == 4);
		Assert.assertTrue(item.getRetentionPolicyId() == 1);
		Assert.assertTrue(item.getProviderId() == 2);
		Assert.assertTrue(item.getPlaceId() == 2);
		Assert.assertTrue(item.isSynchronous() == false);
		Assert.assertTrue(item.isOffsite() == true);

		item = list.get(2);
		Assert.assertTrue(item.getId() == 1);
		Assert.assertTrue(item.getRetentionPolicyId() == 2);
		Assert.assertTrue(item.getProviderId() == 1);
		Assert.assertTrue(item.getPlaceId() == 1);
		Assert.assertTrue(item.isSynchronous() == true);
		Assert.assertTrue(item.isOffsite() == false);

		item = list.get(3);
		Assert.assertTrue(item.getId() == 2);
		Assert.assertTrue(item.getRetentionPolicyId() == 2);
		Assert.assertTrue(item.getProviderId() == 3);
		Assert.assertTrue(item.getPlaceId() == 2);
		Assert.assertTrue(item.isSynchronous() == false);
		Assert.assertTrue(item.isOffsite() == true);

	}

	@Test
	public void testTranslateFindAllNoResults() throws RetrievalException {
		StringBuilder builder = new StringBuilder();
		builder.append("0^^0\r\n");
		builder.append("IEN^RETENTION POLICY FK^PROVIDER FK^SOURCE PLACE FK^IS SYNCHRONOUS^IS OFFSITE");
		List<RetentionPolicyProviderMapping> list = dao.translateFindAll(builder.toString());

		Assert.assertTrue(list.size() == 0);

	}

	@Test(expected = RetrievalException.class)
	public void testTranslateFindAllWithException() throws RetrievalException {
		StringBuilder builder = new StringBuilder();
		builder.append("-1^Retrieval Exception^");
		List<RetentionPolicyProviderMapping> list = dao.translateFindAll(builder.toString());

		Assert.assertTrue(list.size() == 0);

	}

	@Test
	public void testTranslateUpdate() throws Exception {
		dao.translateUpdate(new RetentionPolicyProviderMapping(), "0");
	}

	@Test(expected = UpdateException.class)
	public void testTranslateUpdateWithException() throws UpdateException {
		dao.translateUpdate(new RetentionPolicyProviderMapping(), "-1^Entity Not Found");
	}

	@Test
	public void testTranslateDelete() throws Exception {
		dao.translateDelete("0");
	}

	@Test(expected = DeletionException.class)
	public void testTranslateDeleteWithException() throws DeletionException {
		dao.translateDelete("-1^Entity Not Found");
	}

}