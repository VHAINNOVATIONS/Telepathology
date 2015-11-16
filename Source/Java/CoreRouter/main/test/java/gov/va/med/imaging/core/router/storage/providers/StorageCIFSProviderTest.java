package gov.va.med.imaging.core.router.storage.providers;

import gov.va.med.imaging.core.interfaces.Router;
import junit.framework.Assert;
import junit.framework.TestCase;

public class StorageCIFSProviderTest 
extends TestCase
{
	public void testCreateFileNameWithExtensionContainingPeriod()
	{
		int id = 42;
		String divisionNumber = "660";
		String zeroPaddedIen = StorageCIFSProvider.getZeroPaddedIen(id);
		String fileExtension = ".dcm";
		
		String fileName = StorageCIFSProvider.createFileName(divisionNumber, zeroPaddedIen, fileExtension);
		
		Assert.assertEquals("660_00000000000042.dcm", fileName);
	}
	
	public void testCreateFileNameWithExtensionMissingPeriod()
	{
		int id = 505;
		String divisionNumber = "660";
		String zeroPaddedIen = StorageCIFSProvider.getZeroPaddedIen(id);
		String fileExtension = "dcm";
		
		String fileName = StorageCIFSProvider.createFileName(divisionNumber, zeroPaddedIen, fileExtension);
		
		Assert.assertEquals("660_00000000000505.dcm", fileName);
	}
	
	public void testCreateFileNameWithEmptyExtension()
	{
		int id = 1;
		String divisionNumber = "660AA";
		String zeroPaddedIen = StorageCIFSProvider.getZeroPaddedIen(id);
		String fileExtension = "";
		
		String fileName = StorageCIFSProvider.createFileName(divisionNumber, zeroPaddedIen, fileExtension);
		
		Assert.assertEquals("660AA_00000000000001", fileName);
	}
	
	public void testCreateFileNameWithNullExtension()
	{
		int id = 12345;
		String divisionNumber = "660_ABCD";
		String zeroPaddedIen = StorageCIFSProvider.getZeroPaddedIen(id);
		String fileExtension = null;
		
		String fileName = StorageCIFSProvider.createFileName(divisionNumber, zeroPaddedIen, fileExtension);
		
		Assert.assertEquals("660_ABCD_00000000012345", fileName);
	}
	
	public void testCreateFilePath()
	{
		int id = 45;
		String divisionNumber = "660_ABCD";
		String zeroPaddedIen = StorageCIFSProvider.getZeroPaddedIen(id);
		
		String filePath = StorageCIFSProvider.createFilePath(divisionNumber, zeroPaddedIen);
		
		Assert.assertEquals("660_ABCD\\00\\00\\00\\00\\00\\00\\", filePath);

	}
	
	public void testCreateFileNameAndPathWithExtension()
	{
		int id = 42;
		String divisionNumber = "660";
		String zeroPaddedIen = StorageCIFSProvider.getZeroPaddedIen(id);
		String fileExtension = "dcm";
	
		String filePathAndName = StorageCIFSProvider.createFilePathAndName(divisionNumber, zeroPaddedIen, fileExtension);

		Assert.assertEquals("660\\00\\00\\00\\00\\00\\00\\660_00000000000042.dcm", filePathAndName);
	}

	public void testCreateFileNameAndPathWithEmptyExtension()
	{
		int id = 1;
		String divisionNumber = "660AA";
		String zeroPaddedIen = StorageCIFSProvider.getZeroPaddedIen(id);
		String fileExtension = "";

		String filePathAndName = StorageCIFSProvider.createFilePathAndName(divisionNumber, zeroPaddedIen, fileExtension);

		Assert.assertEquals("660AA\\00\\00\\00\\00\\00\\00\\660AA_00000000000001", filePathAndName);
	}

	public void testCreateFileNameAndPathWithNullExtension()
	{
		int id = 12345;
		String divisionNumber = "660_ABCD";
		String zeroPaddedIen = StorageCIFSProvider.getZeroPaddedIen(id);
		String fileExtension = null;
		
		String filePathAndName = StorageCIFSProvider.createFilePathAndName(divisionNumber, zeroPaddedIen, fileExtension);

		Assert.assertEquals("660_ABCD\\00\\00\\00\\00\\01\\23\\660_ABCD_00000000012345", filePathAndName);
	}

	public void testCreateFileNameAndPathWithLongIen()
	{
		int id = 123456789;
		String divisionNumber = "660_ABCD";
		String zeroPaddedIen = StorageCIFSProvider.getZeroPaddedIen(id);
		String fileExtension = null;
		
		String filePathAndName = StorageCIFSProvider.createFilePathAndName(divisionNumber, zeroPaddedIen, fileExtension);

		Assert.assertEquals("660_ABCD\\00\\00\\01\\23\\45\\67\\660_ABCD_00000123456789", filePathAndName);
	}

}
