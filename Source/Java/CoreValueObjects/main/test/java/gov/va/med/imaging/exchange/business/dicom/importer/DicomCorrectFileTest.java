package gov.va.med.imaging.exchange.business.dicom.importer;


import java.util.ArrayList;
import java.util.List;

import gov.va.med.imaging.exchange.business.WorkItemTag;
import gov.va.med.imaging.exchange.business.dicom.InstrumentConfig;
import gov.va.med.imaging.url.vista.StringUtils;
import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DicomCorrectFileTest {

	private String shortStudyUid = "StudyUid1234";
	private String shortSeriesUid = "SeriesUid1234";
	private String shortInstanceUid = "InstanceUid1234";
	
	private String maxStudyUid = "Study11111222222222233333333334444444444555555555566666666661234";
	private String maxSeriesUid = "Series1111222222222233333333334444444444555555555566666666661234";
	private String maxInstanceUid = "Instance11222222222233333333334444444444555555555566666666661234";
	private String maxTxSyntaxUid = "TxSyntax11222222222233333333334444444444555555555566666666661234";

	private String fileNameBase = "11111111112222222222333333333312";
	private String filePath = "11111111112222222222333333333312.dcm";
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEncodeDecodeShortTag1()
	{
		
		DicomCorrectFile file = new DicomCorrectFile();
		file.setStudyUid(shortStudyUid);
		file.setSeriesUid(shortSeriesUid);
		file.setInstanceUid(shortInstanceUid);
		file.setFileNameBase(fileNameBase);
		
		String tag1 = file.encodeTag1();
		
		// Make sure the tag value is less than 230
		Assert.assertTrue(tag1.length() < 230);
		
		// Assert that the encoding happened correctly
		Assert.assertEquals(shortStudyUid + "~" + shortSeriesUid + "~" + shortInstanceUid + "~" + fileNameBase, tag1);
		
		// Create a new DicomCorrect file and decode the tag, verifying that the fields are reset 
		// correctly
		DicomCorrectFile decodedFile = new DicomCorrectFile();
		DicomCorrectFile.decodeTag1(decodedFile, tag1);
		
		Assert.assertEquals(shortStudyUid, decodedFile.getStudyUid());
		Assert.assertEquals(shortSeriesUid, decodedFile.getSeriesUid());
		Assert.assertEquals(shortInstanceUid, decodedFile.getInstanceUid());
		Assert.assertEquals(fileNameBase, decodedFile.getFileNameBase());
		Assert.assertEquals(filePath, decodedFile.getFilePath());
	}	

	@Test
	public void testEncodeDecodeMaxLengthTag1()
	{
		DicomCorrectFile file = new DicomCorrectFile();
		file.setStudyUid(maxStudyUid);
		file.setSeriesUid(maxSeriesUid);
		file.setInstanceUid(maxInstanceUid);
		file.setFileNameBase(fileNameBase);
		
		String tag1 = file.encodeTag1();
		
		// Make sure the tag value is less than 230
		Assert.assertTrue(tag1.length() < 230);
		
		// Assert that the encoding happened correctly
		Assert.assertEquals(maxStudyUid + "~" + maxSeriesUid + "~" + maxInstanceUid + "~" + fileNameBase, tag1);
		
		// Create a new DicomCorrect file and decode the tag, verifying that the fields are reset 
		// correctly
		DicomCorrectFile decodedFile = new DicomCorrectFile();
		DicomCorrectFile.decodeTag1(decodedFile, tag1);
		
		Assert.assertEquals(maxStudyUid, decodedFile.getStudyUid());
		Assert.assertEquals(maxSeriesUid, decodedFile.getSeriesUid());
		Assert.assertEquals(maxInstanceUid, decodedFile.getInstanceUid());
		Assert.assertEquals(fileNameBase, decodedFile.getFileNameBase());
		Assert.assertEquals(filePath, decodedFile.getFilePath());
	}	

	@Test
	public void testEncodeDecodeShortTag2()
	{
		String transferSyntaxUid = "TransferSyntaxUid";
		String seriesDescription = "ShortSeriesDescription~\nwith bad characters";
		String expectedEncodedSeriesDescription = "ShortSeriesDescription&#126;&#10;with bad characters";
		String modality = "1234567890123456";
		String seriesDate = "20120322.081128.828000";
		String seriesNumber = "123456789012";
		
		DicomCorrectFile file = new DicomCorrectFile();
		file.setTransferSyntaxUid(transferSyntaxUid);
		file.setSeriesDescription(seriesDescription);
		file.setModality(modality);
		file.setSeriesDate(seriesDate);
		file.setSeriesNumber(seriesNumber);
		file.setFileNameBase(fileNameBase);
		
		String tag2 = file.encodeTag2();
		
		// Make sure the tag value is less than 230
		Assert.assertTrue(tag2.length() < 230);
		
		// Assert that the encoding happened correctly
		String expectedTag2 = fileNameBase + "~" +
								  transferSyntaxUid + "~" +
								  expectedEncodedSeriesDescription + "~" +
								  modality + "~" +
								  seriesDate + "~" +
								  seriesNumber;
								  
		Assert.assertEquals(expectedTag2, tag2);
		
		// Create a new DicomCorrect file and decode the tag, verifying that the fields are reset 
		// correctly
		DicomCorrectFile decodedFile = new DicomCorrectFile();
		DicomCorrectFile.decodeTag2(decodedFile, tag2);
		
		Assert.assertEquals(transferSyntaxUid, decodedFile.getTransferSyntaxUid());
		Assert.assertEquals(seriesDescription, decodedFile.getSeriesDescription());
		Assert.assertEquals(modality, decodedFile.getModality());
		Assert.assertEquals(seriesDate, decodedFile.getSeriesDate());
		Assert.assertEquals(seriesNumber, decodedFile.getSeriesNumber());
	}	

	@Test
	public void testEncodeDecodeLongTag2()
	{
		String transferSyntaxUid = maxTxSyntaxUid;
		String seriesDescription = "Over length Series Description~\nwith bad characters that need to be encoded and decoded";
		String expectedEncodedSeriesDescription = "Over length Series Description&#126;&#10;with bad characters ...";
		String expectedDecodedSeriesDescription = "Over length Series Description~\nwith bad characters ...";
		String modality = "1234567890123456";
		String seriesDate = "20120322.081128.828000";
		String seriesNumber = "123456789012";
		
		DicomCorrectFile file = new DicomCorrectFile();
		file.setTransferSyntaxUid(transferSyntaxUid);
		file.setSeriesDescription(seriesDescription);
		file.setModality(modality);
		file.setSeriesDate(seriesDate);
		file.setSeriesNumber(seriesNumber);
		file.setFileNameBase(fileNameBase);
		
		String tag2 = file.encodeTag2();
		
		// Make sure the tag value is less than 230
		Assert.assertTrue(tag2.length() < 230);
		
		// Assert that the encoding happened correctly
		String expectedTag2 = fileNameBase + "~" +
								  transferSyntaxUid + "~" +
								  expectedEncodedSeriesDescription + "~" +
								  modality + "~" +
								  seriesDate + "~" +
								  seriesNumber;
								  
		Assert.assertEquals(expectedTag2, tag2);
		
		// Create a new DicomCorrect file and decode the tag, verifying that the fields are reset 
		// correctly
		DicomCorrectFile decodedFile = new DicomCorrectFile();
		DicomCorrectFile.decodeTag2(decodedFile, tag2);
		
		Assert.assertEquals(transferSyntaxUid, decodedFile.getTransferSyntaxUid());
		Assert.assertEquals(expectedDecodedSeriesDescription, decodedFile.getSeriesDescription());
		Assert.assertEquals(modality, decodedFile.getModality());
		Assert.assertEquals(seriesDate, decodedFile.getSeriesDate());
		Assert.assertEquals(seriesNumber, decodedFile.getSeriesNumber());
	}	

	@Test
	public void testEncodeDecodeShortTag3()
	{
		String facility = "OK length Facility~\nwith bad characters.";
		String expectedEncodedFacility = "OK length Facility&#126;&#10;with bad characters.";
		
		String institutionAddress = "OK length Institution Address~\nwith bad characters.";
		String expectedEncodedInstitutionAddress = "OK length Institution Address&#126;&#10;with bad characters.";
		
		DicomCorrectFile file = new DicomCorrectFile();
		file.setFacility(facility);
		file.setInstitutionAddress(institutionAddress);
		file.setFileNameBase(fileNameBase);
		
		String tag3 = file.encodeTag3();
		
		// Make sure the tag value is less than 230
		Assert.assertTrue(tag3.length() < 230);
		
		// Assert that the encoding happened correctly
		String expectedTag3 = fileNameBase + "~" +
								  expectedEncodedFacility + "~" +
								  expectedEncodedInstitutionAddress;
								  
		Assert.assertEquals(expectedTag3, tag3);
		
		// Create a new DicomCorrect file and decode the tag, verifying that the fields are reset 
		// correctly
		DicomCorrectFile decodedFile = new DicomCorrectFile();
		DicomCorrectFile.decodeTag3(decodedFile, tag3);
		
		Assert.assertEquals(facility, decodedFile.getFacility());
		Assert.assertEquals(institutionAddress, decodedFile.getInstitutionAddress());
	}	

	@Test
	public void testEncodeDecodeLongTag3()
	{
		String facility = "Over length Facility~\nwith bad characters that need to be encoded and decoded or else this thing may cause problems on the mumps side.";
		String expectedEncodedFacility = "Over length Facility&#126;&#10;with bad characters that need ...";
		String expectedDecodedFacility = "Over length Facility~\nwith bad characters that need ...";
		
		String institutionAddress = "Over length Institution Address~\nwith bad characters that need to be encoded and decoded " +
									"or else this thing may cause problems on the mumps side.";
		String expectedEncodedInstitutionAddress = "Over length Institution Address&#126;&#10;with bad characters that need to be encoded and decoded " +
									"or else this thing may cause ...";
		String expectedDecodedInstitutionAddress = "Over length Institution Address~\nwith bad characters that need to be encoded and decoded " +
									"or else this thing may cause ...";
		
		DicomCorrectFile file = new DicomCorrectFile();
		file.setFacility(facility);
		file.setInstitutionAddress(institutionAddress);
		file.setFileNameBase(fileNameBase);
		
		String tag3 = file.encodeTag3();
		
		// Make sure the tag value is less than 230
		Assert.assertTrue(tag3.length() < 230);
		
		// Assert that the encoding happened correctly
		String expectedTag3 = fileNameBase + "~" +
								  expectedEncodedFacility + "~" +
								  expectedEncodedInstitutionAddress;
								  
		Assert.assertEquals(expectedTag3, tag3);
		
		// Create a new DicomCorrect file and decode the tag, verifying that the fields are reset 
		// correctly
		DicomCorrectFile decodedFile = new DicomCorrectFile();
		DicomCorrectFile.decodeTag3(decodedFile, tag3);
		
		Assert.assertEquals(expectedDecodedFacility, decodedFile.getFacility());
		Assert.assertEquals(expectedDecodedInstitutionAddress, decodedFile.getInstitutionAddress());
	}	
	
	@Test 
	public void testFullShortEncodeAndDecode()
	{
		String seriesDescription = "seriesDescription";
		String modality = "1234567890123456";
		String seriesDate = "20120322.081128.828000";
		String seriesNumber = "123456789012";
		String facility = "OK length Facility~\nwith bad characters.";
		String institutionAddress = "OK length Institution Address~\nwith bad characters.";

		DicomCorrectFile originalFile = new DicomCorrectFile(
				maxStudyUid,
				maxSeriesUid, 
				maxInstanceUid,
				seriesDescription, 
				seriesDate, 
				modality, 
				seriesNumber, 
				maxTxSyntaxUid, 
				facility, 
				institutionAddress, 
				fileNameBase);
		
		List<WorkItemTag> tags = originalFile.encodeToTags("1~2~3");
		
		// Verify that there are 4 tags
		Assert.assertEquals(4, tags.size());
		
		// Create a list of tag values (tags 1 to and 3, and decode to a new instance.
		List<String> tagValues = new ArrayList<String>();
		
		tagValues.add(tags.get(1).getValue());
		tagValues.add(tags.get(2).getValue());
		tagValues.add(tags.get(3).getValue());
		
		DicomCorrectFile decodedFile = DicomCorrectFile.decodeFromTagValues(tagValues);
		
		// Compare each of the fields from the two files		
		Assert.assertEquals(originalFile.getStudyUid(), decodedFile.getStudyUid());
		Assert.assertEquals(originalFile.getSeriesUid(), decodedFile.getSeriesUid());
		Assert.assertEquals(originalFile.getInstanceUid(), decodedFile.getInstanceUid());
		Assert.assertEquals(originalFile.getSeriesDescription(), decodedFile.getSeriesDescription());
		Assert.assertEquals(originalFile.getSeriesDate(), decodedFile.getSeriesDate());
		Assert.assertEquals(originalFile.getModality(), decodedFile.getModality());
		Assert.assertEquals(originalFile.getSeriesNumber(), decodedFile.getSeriesNumber());
		Assert.assertEquals(originalFile.getTransferSyntaxUid(), decodedFile.getTransferSyntaxUid());
		Assert.assertEquals(originalFile.getFacility(), decodedFile.getFacility());
		Assert.assertEquals(originalFile.getInstitutionAddress(), decodedFile.getInstitutionAddress());
		Assert.assertEquals(originalFile.getFileNameBase(), decodedFile.getFileNameBase());
		Assert.assertEquals(originalFile.getFilePath(), decodedFile.getFilePath());
	}

	
	@Test 
	public void testFullMaxLengthEncodeAndDecode()
	{
		String seriesDescription = "Over length Series Description~\nwith bad characters that need to be encoded and decoded";
		String expectedDecodedSeriesDescription = "Over length Series Description~\nwith bad characters ...";

		String modality = "1234567890123456";
		String seriesDate = "20120322.081128.828000";
		String seriesNumber = "123456789012";

		String facility = "Over length Facility~\nwith bad characters that need to be encoded and decoded or else this thing may cause problems on the mumps side.";
		String expectedDecodedFacility = "Over length Facility~\nwith bad characters that need ...";
		
		String institutionAddress = "Over length Institution Address~\nwith bad characters that need to be encoded and decoded " +
									"or else this thing may cause problems on the mumps side.";
		String expectedDecodedInstitutionAddress = "Over length Institution Address~\nwith bad characters that need to be encoded and decoded " +
									"or else this thing may cause ...";

		DicomCorrectFile originalFile = new DicomCorrectFile(
				maxStudyUid,
				maxSeriesUid, 
				maxInstanceUid,
				seriesDescription, 
				seriesDate, 
				modality, 
				seriesNumber, 
				maxTxSyntaxUid, 
				facility, 
				institutionAddress, 
				fileNameBase);
		
		List<WorkItemTag> tags = originalFile.encodeToTags("1~2~3");
		
		// Verify that there are 4 tags
		Assert.assertEquals(4, tags.size());
		
		// Create a list of tag values (tags 1 to and 3, and decode to a new instance.
		List<String> tagValues = new ArrayList<String>();
		
		tagValues.add(tags.get(1).getValue());
		tagValues.add(tags.get(2).getValue());
		tagValues.add(tags.get(3).getValue());
		
		DicomCorrectFile decodedFile = DicomCorrectFile.decodeFromTagValues(tagValues);
		
		// Compare each of the fields from the two files		
		Assert.assertEquals(originalFile.getStudyUid(), decodedFile.getStudyUid());
		Assert.assertEquals(originalFile.getSeriesUid(), decodedFile.getSeriesUid());
		Assert.assertEquals(originalFile.getInstanceUid(), decodedFile.getInstanceUid());
		Assert.assertEquals(expectedDecodedSeriesDescription, decodedFile.getSeriesDescription());
		Assert.assertEquals(originalFile.getSeriesDate(), decodedFile.getSeriesDate());
		Assert.assertEquals(originalFile.getModality(), decodedFile.getModality());
		Assert.assertEquals(originalFile.getSeriesNumber(), decodedFile.getSeriesNumber());
		Assert.assertEquals(originalFile.getTransferSyntaxUid(), decodedFile.getTransferSyntaxUid());
		Assert.assertEquals(expectedDecodedFacility, decodedFile.getFacility());
		Assert.assertEquals(expectedDecodedInstitutionAddress, decodedFile.getInstitutionAddress());
		Assert.assertEquals(originalFile.getFileNameBase(), decodedFile.getFileNameBase());
		Assert.assertEquals(originalFile.getFilePath(), decodedFile.getFilePath());
	}


}
