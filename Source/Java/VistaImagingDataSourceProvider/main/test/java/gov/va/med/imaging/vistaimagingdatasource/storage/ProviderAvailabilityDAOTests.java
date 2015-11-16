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

import gov.va.med.imaging.exchange.business.storage.ProviderAvailability;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.exchange.business.storage.exceptions.DeletionException;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.exchange.business.storage.exceptions.UpdateException;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ProviderAvailabilityDAOTests
{

	private ProviderAvailabilityDAO dao = new ProviderAvailabilityDAO();

	@Test
	public void testTranslateFindAll() throws RetrievalException
	{

		StringBuilder builder = new StringBuilder();
		builder.append("0^^2\r\n");
		builder.append("IEN^PROVIDER FK^SOURCE PLACE FK^START TIME^END TIME\r\n");
		builder.append("1^2^3^19000101.193^19000101.063000\r\n");
		builder.append("4^5^6^19000101.193002^19000102.240");
		List<ProviderAvailability> list = dao.translateFindAll(builder.toString());

		Assert.assertTrue(list.size() == 2);

		ProviderAvailability item = list.get(0);
		Assert.assertTrue(item.getId() == 1);
		Assert.assertTrue(item.getProviderId() == 2);
		Assert.assertTrue(item.getPlaceId() == 3);
		Assert.assertTrue(item.getStartTime().equals("193000"));
		Assert.assertTrue(item.getEndTime().equals("063000"));

		item = list.get(1);
		Assert.assertTrue(item.getId() == 4);
		Assert.assertTrue(item.getProviderId() == 5);
		Assert.assertTrue(item.getPlaceId() == 6);
		Assert.assertTrue(item.getStartTime().equals("193002"));
		Assert.assertTrue(item.getEndTime().equals("000000"));

	}

	@Test
	public void testTranslateFindAllNoResults() throws RetrievalException {
		StringBuilder builder = new StringBuilder();
		builder.append("0^^0\r\n");
		builder.append("IEN^PROVIDER PLACE FK^PROVIDER TYPE^IS ACTIVE^IS ARCHIVE^IS PRIMARY STORAGE^IS WRITABLE");
		List<ProviderAvailability> list = dao.translateFindAll(builder.toString());

		Assert.assertTrue(list.size() == 0);

	}

	@Test(expected = RetrievalException.class)
	public void testTranslateFindAllWithException() throws RetrievalException {
		StringBuilder builder = new StringBuilder();
		builder.append("-1^Retrieval Exception`");
		List<ProviderAvailability> list = dao.translateFindAll(builder.toString());

		Assert.assertTrue(list.size() == 0);

	}

	@Test
	public void testTranslateCreate() throws CreationException
	{
		ProviderAvailability providerAvailability = new ProviderAvailability();
		Assert.assertTrue(providerAvailability.getId() == 0);

		StringBuilder builder = new StringBuilder();
		builder.append("0^^5");
		providerAvailability = dao.translateCreate(providerAvailability, builder.toString());
		
		Assert.assertTrue(providerAvailability.getId() == 5);
		
	}


	@Test
	public void testTranslateUpdate() throws Exception
	{
		dao.translateUpdate(new ProviderAvailability(), "0");
	}
	
	@Test (expected=UpdateException.class)
	public void testTranslateUpdateWithException() throws UpdateException
	{
		dao.translateUpdate(new ProviderAvailability(), "-1^Entity Not Found");
	}


	@Test
	public void testTranslateDelete() throws Exception
	{
		dao.translateDelete("0");
	}
	
	@Test (expected=DeletionException.class)
	public void testTranslateDeleteWithException() throws DeletionException
	{
		dao.translateDelete("-1^Entity Not Found");
	}
}