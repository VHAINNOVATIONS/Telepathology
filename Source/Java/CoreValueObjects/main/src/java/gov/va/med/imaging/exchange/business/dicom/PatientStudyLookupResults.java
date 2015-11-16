package gov.va.med.imaging.exchange.business.dicom;

import gov.va.med.imaging.exchange.business.PersistentEntity;

import java.util.ArrayList;
import java.util.List;

public class PatientStudyLookupResults implements PersistentEntity 
{
	private List<String> errorMessages = new ArrayList<String>();
	
	private int id;
	private PatientStudyInfo patientStudyInfo;
	
	/**
	 * This is a universal constructor for good and bad cases.
	 * It is possible to have patient info, and an error!
	 * 
	 * @param patientStudyInfo
	 * @param errorMessages
	 */
	public PatientStudyLookupResults(PatientStudyInfo patientStudyInfo, List<String> errorMessages)
	{
		this.patientStudyInfo = patientStudyInfo;
		if (errorMessages != null)
		{
			this.errorMessages = errorMessages;
		}
	}

	/**
	 * Returns the fully-populated patientStudyInfo object if lookup was successful. Returns
	 * null if lookup failed for any reason (i.e. isOK()==false).
	 * 
	 * @return
	 */
	public PatientStudyInfo getPatienStudyInfo()
	{
		return patientStudyInfo;
	}

	/**
	 * Returns true if no errors were found during patient/study lookup. False, otherwise.
	 * @return
	 */
	public boolean isOk()
	{
		return (errorMessages.size() == 0);
	}

	/**
	 * Returns the list of error messages received during patient/study lookup. If lookup was successful,
	 * the list of errors will be empty.
	 * 
	 * @return
	 */
	public List<String> getErrorMessages() {
		return errorMessages;
	}
	
    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	

}

