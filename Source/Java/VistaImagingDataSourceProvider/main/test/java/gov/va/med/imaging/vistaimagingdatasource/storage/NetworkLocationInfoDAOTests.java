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

import gov.va.med.imaging.exchange.business.storage.NetworkLocationInfo;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;

import org.junit.Assert;
import org.junit.Test;

public class NetworkLocationInfoDAOTests
{
	NetworkLocationInfoDAO dao = new NetworkLocationInfoDAO();
	
	@Test
	public void testTranslateGetCurrentWriteLocation() throws RetrievalException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("0^^1\r\n");
		builder.append("NETWORK LOCATION IEN^PHYSICAL REFERENCE\r\n");
		builder.append("2^\\\\vhaiswimgvms501\\image1$\\");
		NetworkLocationInfo info = dao.translateGetWriteLocation(builder.toString());
		
		Assert.assertTrue(info.getNetworkLocationIEN().equals("2"));
		Assert.assertTrue(info.getPhysicalPath().equals("\\\\vhaiswimgvms501\\image1$\\"));
		
	}

	
	@Test(expected = RetrievalException.class)
	public void testTranslateGetCurrentWriteLocationFailure() throws RetrievalException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("1^No Write Location Found for this Place IEN^0");
		NetworkLocationInfo info = dao.translateGetWriteLocation(builder.toString());
	}

	
	@Test
	public void testTranslateGetNetworkLocationDetails() throws RetrievalException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("0^^1\r\n");
		builder.append("PHYSICAL REFERENCE\r\n");
		builder.append("\\\\vhaiswimgvms501\\image1$\\");
		NetworkLocationInfo info = dao.translateGetNetworkLocationDetails("17", builder.toString());
		
		Assert.assertTrue(info.getNetworkLocationIEN().equals("17"));
		Assert.assertTrue(info.getPhysicalPath().equals("\\\\vhaiswimgvms501\\image1$\\"));
		
	}

	
	@Test(expected = RetrievalException.class)
	public void testTranslateGetNetworkLocationDetailsFailure() throws RetrievalException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("1^No Network Location Found with this IEN^0");
		NetworkLocationInfo info = dao.translateGetNetworkLocationDetails("17", builder.toString());
	}
}