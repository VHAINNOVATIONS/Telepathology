package gov.va.med.imaging.exchange.business.dicom.importer;

import java.util.ArrayList;
import java.util.List;

import gov.va.med.imaging.StringUtil;
import gov.va.med.imaging.exchange.business.WorkItemTag;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.utils.StringUtilities;
import gov.va.med.imaging.xstream.FieldUpperCaseMapper;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class DicomCorrectFile 
{
	private String studyUid;
	private String seriesUid;
	private String instanceUid;
    private String modality;
    private String seriesDate;
    private String seriesNumber;
    private String fileNameBase;
	private String transferSyntaxUid;
	private String seriesDescription;
	private String facility;
	private String institutionAddress;

	public DicomCorrectFile(){}

	public DicomCorrectFile(
			String studyUid, 
			String seriesUid, 
			String instanceUid, 
			String seriesDescription,
			String seriesDate, 
			String modality, 
			String seriesNumber,
			String transferSyntaxUid, 
			String facility, 
			String institutionAddress, 
			String fileNameBase) {
		this.studyUid = studyUid;
		this.seriesUid = seriesUid;
		this.instanceUid = instanceUid;
		this.seriesDescription = seriesDescription;
		this.seriesDate = seriesDate;
		this.modality = modality;
		this.seriesNumber = seriesNumber;
		this.transferSyntaxUid = transferSyntaxUid;
		this.facility = facility;
		this.institutionAddress = institutionAddress;
		this.fileNameBase = fileNameBase;
	}

	public static DicomCorrectFile decodeFromTagValues(List<String> tagValues)
	{
		if (tagValues.size() != 3)
		{
			throw new IllegalArgumentException("Could not decode the DicomCorrectFile. Expected 3 tag values, but found " + tagValues.size());
		}
		
		// Create a new file and populate it with the values from the three tags
		DicomCorrectFile file = new DicomCorrectFile();
		decodeTag1(file, tagValues.get(0));
		decodeTag2(file, tagValues.get(1));
		decodeTag3(file, tagValues.get(2));
		
		return file;
		
	}
	
	public List<WorkItemTag> encodeToTags(String studySeriesSopUids)
	{
		List<WorkItemTag> tags = new ArrayList<WorkItemTag>();
		
		tags.add(new WorkItemTag(ImporterWorkItem.TAG_DICOM_CORRECT_INSTANCE_KEY, studySeriesSopUids));
		tags.add(new WorkItemTag(ImporterWorkItem.TAG_DICOM_CORRECT_FILE_1, encodeTag1()));
		tags.add(new WorkItemTag(ImporterWorkItem.TAG_DICOM_CORRECT_FILE_2, encodeTag2()));
		tags.add(new WorkItemTag(ImporterWorkItem.TAG_DICOM_CORRECT_FILE_3, encodeTag3()));
		
		return tags;
	}

	/**
	 * Encode the fields that belong in tag 1
	 *   Index 0: Study UID      MaxLength: 65 (64 + 1 character delimiter)
	 *   Index 1: Series UID     MaxLength: 65 (64 + 1 character delimiter)
	 *   Index 2: Instance UID   MaxLength: 65 (64 + 1 character delimiter)
	 *   Index 3: FileNameBase   MaxLength: 32 
	 *   -----------------------------------------------------------------
	 *   Total Max Length of tag: 65+65+65+32 = 227
	 * 
	 * @return the encoded string 
	 */
	protected String encodeTag1()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getStudyUid() + StringUtils.TILDE);
		sb.append(getSeriesUid() + StringUtils.TILDE);
		sb.append(getInstanceUid() + StringUtils.TILDE);
		sb.append(getFileNameBase());
		return sb.toString();
	}

	/**
	 * Decode the fields from tag 1
	 *   See encodeTag1 for details on which fields are at which index
	 * 
	 * @param the DicomCorrectFile to populate
	 * @param tag1
	 */
	protected static void decodeTag1(DicomCorrectFile file, String tag1)
	{
		String[] fields = StringUtils.Split(tag1, StringUtils.TILDE);
		file.setStudyUid(fields[0]);
		file.setSeriesUid(fields[1]);
		file.setInstanceUid(fields[2]);
		file.setFileNameBase(fields[3]);
	}

	/**
	 * Given a tag1 string, return the key (fileNameBase), which is at index 3 (Piece 4)
	 * @param tag1
	 * @return
	 */
	public static String getKeyFromTag1(String tag1)
	{
		return StringUtil.Piece(tag1, StringUtils.TILDE, 4);
	}


	/**
	 * Encode the fields that belong in tag2
	 *   Index 0: FileNameBase       MaxLength: 33 (32 + 1 character delimiter)
	 *   Index 1: TransferSyntax     MaxLength: 65 (64 + 1 character delimiter)
	 *   Index 2: SeriesDescription  MaxLength: 65 (64 + 1 character delimiter)
	 *   Index 3: Modality           MaxLength: 17 (16 + 1 character delimiter)
	 *   Index 4: Series Date        MaxLength: 23 (22 + 1 character delimiter)
	 *   Index 5: Series Number      MaxLength: 12 
	 *   -----------------------------------------------------------------------
	 *   Total Max Length of tag: 33+65+65+17+23+12 = 215
	 *   
	 * @return the encoded string
	 */
	protected String encodeTag2()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getFileNameBase() + StringUtils.TILDE);
		sb.append(getTransferSyntaxUid() + StringUtils.TILDE);
		sb.append(escapeAndTruncateString(getSeriesDescription(), 64) + StringUtils.TILDE);
		sb.append(getModality() + StringUtils.TILDE);
		sb.append(getSeriesDate() + StringUtils.TILDE);
		sb.append(getSeriesNumber());
		return sb.toString();
	}

	/**
	 * Decode the fields from tag 2
	 *   See encodeTag2 for details on which fields are at which index
	 * 
	 * @param the DicomCorrectFile to populate
	 * @param tag2
	 */
	protected static void decodeTag2(DicomCorrectFile file, String tag2)
	{
		String[] fields = StringUtils.Split(tag2, StringUtils.TILDE);
		file.setTransferSyntaxUid(fields[1]);
		file.setSeriesDescription(StringUtilities.unescapeMumpsString(fields[2]));
		file.setModality(fields[3]);
		file.setSeriesDate(fields[4]);
		file.setSeriesNumber(fields[5]);
	}

	/**
	 * Given a tag2 string, return the key (fileNameBase), which is at index 0 (Piece 1)
	 * @param tag2
	 * @return
	 */
	public static String getKeyFromTag2(String tag2)
	{
		return StringUtil.Piece(tag2, StringUtils.TILDE, 1);
	}

	/**
	 *   Index 0: FileNameBase        MaxLength: 33 (32 + 1 character delimiter)
	 *   Index 1: Facility            MaxLength: 65 (64 + 1 character delimiter)
	 *   Index 2: InstitutionAddress  MaxLength: 130
	 *   -------------------------------------------------------------------------
	 *   Total Max Length of tag: 33+65+130 = 228
	 *   
	 * @return the encoded string
	 */
	protected String encodeTag3()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getFileNameBase() + StringUtils.TILDE);
		sb.append(escapeAndTruncateString(getFacility(), 64) + StringUtils.TILDE);
		sb.append(escapeAndTruncateString(getInstitutionAddress(), 130));
		return sb.toString();
	}

	/**
	 * Decode the fields from tag 3
	 *   See encodeTag3 for details on which fields are at which index
	 * 
	 * @param the DicomCorrectFile to populate
	 * @param tag3
	 */
	protected static void decodeTag3(DicomCorrectFile file, String tag3)
	{
		String[] fields = StringUtils.Split(tag3, StringUtils.TILDE);
		file.setFacility(StringUtilities.unescapeMumpsString(fields[1]));
		file.setInstitutionAddress(StringUtilities.unescapeMumpsString(fields[2]));
	}
	
	/**
	 * Given a tag3 string, return the key (fileNameBase), which is at index 0 (Piece 1)
	 * @param tag3
	 * @return
	 */
	public static String getKeyFromTag3(String tag3)
	{
		return StringUtil.Piece(tag3, StringUtils.TILDE, 1);
	}

	/**
	 * Escape out special characters that could cause problems with mumps, the truncate
	 * the string if necessary to be less than maxLength. If it exceeds maxLength, truncate
	 * to maxLength - 4, and then add " ..." to the end to let the user know there was more data...
	 * @return
	 */
	public String escapeAndTruncateString(String theString, int maxLength)
	{
		theString = StringUtilities.escapeMumpsString(theString);
		if (theString.length() > maxLength)
		{
			theString = theString.substring(0, maxLength - 4) + " ...";
		}
		
		return theString;
	}
	
	public String getStudyUid() {
		return studyUid;
	}



	public void setStudyUid(String studyUid) {
		this.studyUid = studyUid;
	}



	public String getSeriesUid() {
		return seriesUid;
	}



	public void setSeriesUid(String seriesUid) {
		this.seriesUid = seriesUid;
	}



	public String getInstanceUid() {
		return instanceUid;
	}



	public void setInstanceUid(String instanceUid) {
		this.instanceUid = instanceUid;
	}

	public String getFilePath() {
		return fileNameBase + ".dcm";
	}


	public void setSeriesDate(String seriesDate) {
		this.seriesDate = seriesDate;
	}



	public String getSeriesDate() {
		return seriesDate;
	}



	public void setModality(String modality) {
		this.modality = modality;
	}



	public String getModality() {
		return modality;
	}



	public void setSeriesNumber(String seriesNumber) {
		this.seriesNumber = seriesNumber;
	}

	public String getSeriesNumber() {
		return seriesNumber;
	}

	public void setTransferSyntaxUid(String transferSyntaxUid) {
		this.transferSyntaxUid = transferSyntaxUid;
	}

	public String getTransferSyntaxUid() {
		return transferSyntaxUid;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	public String getFacility() {
		return facility + "";
	}
	
	public void setSeriesDescription(String seriesDescription) {
		this.seriesDescription = seriesDescription;
	}

	public String getSeriesDescription() {
		return seriesDescription + "";
	}
	
	public void setInstitutionAddress(String institutionAddress) {
		this.institutionAddress = institutionAddress;
	}

	public String getInstitutionAddress() {
		return institutionAddress + "";
	}

	public String getFileNameBase() {
		return fileNameBase;
	}

	public void setFileNameBase(String fileNameBase) {
		this.fileNameBase = fileNameBase;
	}

}
