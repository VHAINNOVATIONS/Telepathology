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

import gov.va.med.imaging.exchange.business.storage.Provider;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.exchange.business.storage.exceptions.UpdateException;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ProviderDAOTests
{
	private ProviderDAO dao = new ProviderDAO();

	@Test
	public void testTranslateFindAll() throws RetrievalException
	{

		StringBuilder builder = new StringBuilder();
		builder.append("0^^3\r\n");
		builder.append("IEN^PROVIDER PLACE FK^PROVIDER TYPE^IS ACTIVE^IS ARCHIVE^IS PRIMARY STORAGE^IS WRITABLE\r\n");
		builder.append("1^4^RAID^1^0^1^0\r\n");
		builder.append("2^5^JukeBox^1^1^0^0\r\n");
		builder.append("3^6^RAID^0^0^1^1");
		List<Provider> list = dao.translateFindAll(builder.toString());

		Assert.assertTrue(list.size() == 3);

		Provider item = list.get(0);
		Assert.assertTrue(item.getId() == 1);
		Assert.assertTrue(item.getPlaceId() == 4);
		Assert.assertTrue(item.getProviderType().equals("RAID"));
		Assert.assertTrue(item.isActive() == true);
		Assert.assertTrue(item.isArchive() == false);
		Assert.assertTrue(item.isPrimaryStorage() == true);
		Assert.assertTrue(item.isWritable() == false);

		item = list.get(1);
		Assert.assertTrue(item.getId() == 2);
		Assert.assertTrue(item.getPlaceId() == 5);
		Assert.assertTrue(item.getProviderType().equals("JukeBox"));
		Assert.assertTrue(item.isActive() == true);
		Assert.assertTrue(item.isArchive() == true);
		Assert.assertTrue(item.isPrimaryStorage() == false);
		Assert.assertTrue(item.isWritable() == false);

		item = list.get(2);
		Assert.assertTrue(item.getId() == 3);
		Assert.assertTrue(item.getPlaceId() == 6);
		Assert.assertTrue(item.getProviderType().equals("RAID"));
		Assert.assertTrue(item.isActive() == false);
		Assert.assertTrue(item.isArchive() == false);
		Assert.assertTrue(item.isPrimaryStorage() == true);
		Assert.assertTrue(item.isWritable() == true);

	}

	@Test
	public void testTranslateFindAllNoResults() throws RetrievalException {
		StringBuilder builder = new StringBuilder();
		builder.append("0^^0\r\n");
		builder.append("IEN^PROVIDER PLACE FK^PROVIDER TYPE^IS ACTIVE^IS ARCHIVE^IS PRIMARY STORAGE^IS WRITABLE");
		List<Provider> list = dao.translateFindAll(builder.toString());

		Assert.assertTrue(list.size() == 0);

	}

	@Test(expected = RetrievalException.class)
	public void testTranslateFindAllWithException() throws RetrievalException {
		StringBuilder builder = new StringBuilder();
		builder.append("-1^Retrieval Exception^");
		List<Provider> list = dao.translateFindAll(builder.toString());

		Assert.assertTrue(list.size() == 0);

	}

	@Test
	public void testTranslateCreate() throws CreationException
	{
		Provider provider = new Provider();
		Assert.assertTrue(provider.getId() == 0);

		StringBuilder builder = new StringBuilder();
		builder.append("0^^5");
		provider = dao.translateCreate(provider, builder.toString());
		
		Assert.assertTrue(provider.getId() == 5);
		
	}


	@Test
	public void testTranslateUpdate() throws Exception
	{
		dao.translateUpdate(new Provider(), "0");
	}
	
	@Test (expected=UpdateException.class)
	public void testTranslateUpdateWithException() throws UpdateException
	{
		dao.translateUpdate(new Provider(), "-1^Entity Not Found");
	}

	@Test
	public void testTranslateGetCurrentWriteLocation() 
	{
		throw new NotImplementedException();
	}

}