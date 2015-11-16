package gov.va.med.imaging.exchange.business.dicom.importer;

import gov.va.med.imaging.exchange.business.WorkItem;
import gov.va.med.imaging.exchange.business.WorkItemFilter;

public class ImporterWorkItemFilter 
{
	

	private String type;
    private String subtype;
    private String status;
    private String placeId;
    private String patientName;
    private String source;
    private String studyUid;
    private String accessionNumber;
    private String patientId;
    private String studySeriesSopUids;
    private String originIndex;
	private String shortCircuitTagName = "";
	private String maximumNumberOfItemsToReturn = "";
    
    public WorkItemFilter getRawWorkItemFilter()
    {
    	WorkItemFilter filter = new WorkItemFilter();
    	
    	filter.setType(getType());
    	filter.setSubtype(getSubtype());
    	filter.setStatus(getStatus());
    	filter.setPlaceId(getPlaceId());
    	filter.setShortCircuitTagName(getShortCircuitTagName());
    	filter.setMaximumNumberOfItemsToReturn(getMaximumNumberOfItemsToReturn());
    	
    	addTag(filter, ImporterWorkItem.TAG_SOURCE, getSource());
    	addTag(filter, ImporterWorkItem.TAG_PATIENT_NAME, getPatientName());
    	addTag(filter, ImporterWorkItem.TAG_ACCESSION_NUMBER, getAccessionNumber());
    	addTag(filter, ImporterWorkItem.TAG_STUDY_UID, getStudyUid());
    	addTag(filter, ImporterWorkItem.TAG_DICOM_CORRECT_INSTANCE_KEY, getStudySeriesSopUids());
    	addTag(filter, ImporterWorkItem.TAG_PATIENT_NAME, getPatientName());
    	addTag(filter, ImporterWorkItem.TAG_ORIGIN_INDEX, getOriginIndex());
    	
    	return filter;
    	
    }
    
	private void addTag(WorkItemFilter filter, String key, String value) 
	{
		if (value!=null && value != "")
		{
			filter.getTags().addTag(key, value);
		}
	}


    public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSubtype() {
		return subtype;
	}
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPlaceId() {
		return placeId;
	}
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getStudyUid() {
		return studyUid;
	}
	public void setStudyUid(String studyUid) {
		this.studyUid = studyUid;
	}
	public String getAccessionNumber() {
		return accessionNumber;
	}
	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	
	public void setStudySeriesSopUids(String studySeriesSopUids) {
		this.studySeriesSopUids = studySeriesSopUids;
	}

	public String getStudySeriesSopUids() {
		return studySeriesSopUids;
	}

	public void setOriginIndex(String originIndex) {
		this.originIndex = originIndex;
	}

	public String getOriginIndex() {
		return originIndex;
	}

	public void setShortCircuitTagName(String shortCircuitTagName) {
		this.shortCircuitTagName = shortCircuitTagName;
	}

	public String getShortCircuitTagName() {
		return shortCircuitTagName;
	}

	public void setMaximumNumberOfItemsToReturn(
			String maximumNumberOfItemsToReturn) {
		this.maximumNumberOfItemsToReturn = maximumNumberOfItemsToReturn;
	}

	public String getMaximumNumberOfItemsToReturn() {
		return maximumNumberOfItemsToReturn;
	}
}
