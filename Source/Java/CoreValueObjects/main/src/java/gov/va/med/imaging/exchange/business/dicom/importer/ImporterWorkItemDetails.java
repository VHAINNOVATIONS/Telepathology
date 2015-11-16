package gov.va.med.imaging.exchange.business.dicom.importer;

import gov.va.med.imaging.exchange.business.Patient;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ImporterWorkItemDetails 
{

    private int networkLocationIen;
    private int parentWorkItemIen;
    private String mediaBundleStagingRootDirectory;
    private String dicomCorrectReason;
    private String instrumentNickName;
    private String instrumentService;
    private String instrumentAcqLocation;
	private Patient vaPatientFromStaging;
    private boolean isMediaBundleStaged;
    private boolean mediaHasDicomDir;
    private String workstationName;
    private int mediaValidationStatusCode;
    private String mediaValidationMessage;
    private String importErrorMessage;

    private String reconcilingTechnicianDuz;
    private List<String> workItemProcessingErrors;

    private List<Study> studies;
    private List<Reconciliation> reconciliations;
    private List<String> dicomDirPaths;
    
    private MediaCategory mediaCategory;
    private List<NonDicomFile> nonDicomFiles;
    private String reconcilerNotes;

	public boolean hasImportErrors()
	{
		for (Study study : getStudies())
		{

			// If the study has a reconciliation, check the study to see if there were any errors.
			if (study.getReconciliation() != null)
			{
				// Check the study, stopping if it has an error
				if (study.getFailedImport())
					return true;
	
				// Check each of the SOP Instances, stopping as soon as we find one with an error
				for (Series series : study.getSeries())
				{
					for (SopInstance instance : series.getSopInstances())
					{
						if (!instance.isImportedSuccessfully())
							return true;
					}
				}
				
				// Check the non-DICOM files, if any, stopping as soon as we find one with an error
				List<NonDicomFile> nonDicomFiles = study.getReconciliation().getNonDicomFiles();
				if (nonDicomFiles != null)
				{
					for (NonDicomFile file : nonDicomFiles)
					{
						if (!file.isImportedSuccessfully())
						{
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	public String getImportErrorSummary()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Importer item with IEN '" + this.getParentWorkItemIen() + "' failed processing.\n\n");
		sb.append("Please navigate to Failed Imports in the Administration area of the Importer GUI for more details.\n\n");

		// Get the summary information for each failed study
		for (Study study : this.getStudies())
		{
			boolean studyHasErrors = false;
			
			// If the study itself failed (i.e. bad order, etc), note it.
			if (study.getFailedImport())
			{
				studyHasErrors = true;
			}
			
			int failedImageCount = 0;
			for (Series series : study.getSeries())
			{
				for (SopInstance instance : series.getSopInstances())
				{
					if (!instance.isImportedSuccessfully())
					{
						failedImageCount++ ;
					}
				}
			}

			// Add on the count of failed non-DICOM files, if any
			List<NonDicomFile> nonDicomFiles = study.getReconciliation().getNonDicomFiles();
			if (nonDicomFiles != null)
			{
				for (NonDicomFile file : nonDicomFiles)
				{
					if (!file.isImportedSuccessfully())
					{
						failedImageCount++;
					}
				}
			}
					
			// Only instance failures...
			if (!studyHasErrors && failedImageCount > 0)
			{
				sb.append("  * Study " + study.getIdInMediaBundle() + ": " + getImageString(failedImageCount) + " failed to process.\n");
			}
			
			// Only a study level failure...
			if (studyHasErrors && failedImageCount == 0)
			{
				sb.append("  * Study " + study.getIdInMediaBundle() + ": A study-level failure occurred.\n");
			}
			
			// Both study and instance failures...
			if (studyHasErrors && failedImageCount > 0)
			{
				sb.append("  * Study " + study.getIdInMediaBundle() + ": A study-level failure occurred. Also, " + getImageString(failedImageCount) + " failed to process.\n");
			}
			
		}
		
		return sb.toString();
		
	}
	
	private String getImageString(int failedImageCount) 
	{
		if (failedImageCount == 1)
		{
			return "1 image";
		}
		else
		{
			return failedImageCount + " images";
		}
	}

	public String getImportErrorDetails()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("*********************************************************\n");
		sb.append("Importer item with IEN '" + this.getParentWorkItemIen() + "' failed processing...\n");
		sb.append("*********************************************************\n");
		sb.append("The media bundle can be found here: " + getNetworkLocationIen() + "\n");
		sb.append("  Network Location IEN: " + getNetworkLocationIen() + "\n");
		sb.append("  Root path: " + getMediaBundleStagingRootDirectory() + "\n");
		sb.append("*********************************************************\n");
		
		for (Study study : this.getStudies())
		{
			sb.append("Errors processing study:\n");
			sb.append("  UID: " + study.getUid() + "\n");
			
			// If the study itself failed (i.e. bad order, etc), write out the failure reason
			if (study.getFailedImport())
			{
				sb.append("  Study failure reason: " + study.getImportErrorMessage() + "\n");
			}
			
			for (Series series : study.getSeries())
			{
				for (SopInstance instance : series.getSopInstances())
				{
					if (!instance.isImportedSuccessfully())
					{
						sb.append("    Instance failure reason: " + instance.getImportErrorMessage() + "\n");
						sb.append("      Instance UID: " + instance.getUid() + "\n");
					}
				}
			}
			
			// Add on the non-DICOM file failure information
			List<NonDicomFile> nonDicomFiles = study.getReconciliation().getNonDicomFiles();
			if (nonDicomFiles != null)
			{
				for (NonDicomFile file : nonDicomFiles)
				{
					if (!file.isImportedSuccessfully())
					{
						sb.append("    Non-DICOM file failure reason: " + file.getImportErrorMessage() + "\n");
					}
				}
			}

		}
		sb.append("*********************************************************\n");
		
		return sb.toString();
		
	}
	
	public String getImportErrorMessage() {
		return importErrorMessage;
	}

	public void getImportErrorMessage(String importErrorMessage) {
		this.importErrorMessage = importErrorMessage;
	}

    public List<Study> getStudies() {
        return studies;
    }

    public void setStudies(List<Study> value) {
        this.studies = value;
    }

    public List<Reconciliation> getReconciliations() {
        return reconciliations;
    }

    public void setReconciliations(List<Reconciliation> value) {
        this.reconciliations = value;
    }

	public void setMediaBundleStagingRootDirectory(
			String mediaBundleStagingRootDirectory) {
		this.mediaBundleStagingRootDirectory = mediaBundleStagingRootDirectory;
	}

	public String getMediaBundleStagingRootDirectory() {
		return mediaBundleStagingRootDirectory;
	}

	public void setNetworkLocationIen(int networkLocationIen) {
		this.networkLocationIen = networkLocationIen;
	}

	public int getNetworkLocationIen() {
		return networkLocationIen;
	}
	
	public String getDicomCorrectReason() {
		return dicomCorrectReason;
	}

	public void setDicomCorrectReason(String dicomCorrectReason) {
		this.dicomCorrectReason = dicomCorrectReason;
	}

	public void setInstrumentNickName(String instrumentNickName) {
		this.instrumentNickName = instrumentNickName;
	}

	public String getInstrumentNickName() {
		return instrumentNickName;
	}

	public void setVaPatientFromStaging(Patient vaPatientFromStaging) {
		this.vaPatientFromStaging = vaPatientFromStaging;
	}

	public Patient getVaPatientFromStaging() {
		return vaPatientFromStaging;
	}

	public void setMediaBundleStaged(boolean isMediaBundleStaged) {
		this.isMediaBundleStaged = isMediaBundleStaged;
	}

	public boolean isMediaBundleStaged() {
		return isMediaBundleStaged;
	}

	public void setDicomDirPaths(List<String> dicomDirPaths) {
		this.dicomDirPaths = dicomDirPaths;
	}

	public List<String> getDicomDirPaths() {
		return dicomDirPaths;
	}

	public void setMediaHasDicomDir(boolean mediaHasDicomDir) {
		this.mediaHasDicomDir = mediaHasDicomDir;
	}

	public boolean isMediaHasDicomDir() {
		return mediaHasDicomDir;
	}

	public void setReconcilingTechnicianDuz(String reconcilingTechnicianDuz) {
		this.reconcilingTechnicianDuz = reconcilingTechnicianDuz;
	}

	public String getReconcilingTechnicianDuz() {
		return reconcilingTechnicianDuz;
	}

	public void setWorkItemProcessingErrors(List<String> workItemProcessingErrors) {
		this.workItemProcessingErrors = workItemProcessingErrors;
	}

	public List<String> getWorkItemProcessingErrors() {
		return workItemProcessingErrors;
	}

	public void setParentWorkItemIen(int parentWorkItemIen) {
		this.parentWorkItemIen = parentWorkItemIen;
	}

	public int getParentWorkItemIen() {
		return parentWorkItemIen;
	}

	public void setWorkstationName(String workstationName) {
		this.workstationName = workstationName;
	}

	public String getWorkstationName() {
		return workstationName;
	}

	public void setMediaValidationStatusCode(int mediaValidationStatusCode) {
		this.mediaValidationStatusCode = mediaValidationStatusCode;
	}

	public int getMediaValidationStatusCode() {
		return mediaValidationStatusCode;
	}

	public void setMediaValidationMessage(String mediaValidationMessage) {
		this.mediaValidationMessage = mediaValidationMessage;
	}

	public String getMediaValidationMessage() {
		return mediaValidationMessage;
	}

	public void setInstrumentService(String instrumentService) {
		this.instrumentService = instrumentService;
	}

	public String getInstrumentService() {
		return instrumentService;
	}
	
	public String getInstrumentAcqLocation() {
		return instrumentAcqLocation;
	}

	public void setInstrumentAcqLocation(String instrumentAcqLocation) {
		this.instrumentAcqLocation = instrumentAcqLocation;
	}

	/**
	 * @return the reconcilerNotes
	 */
	public String getReconcilerNotes() {
		return reconcilerNotes;
	}

	/**
	 * @param reconcilerNotes the reconcilerNotes to set
	 */
	public void setReconcilerNotes(String reconcilerNotes) {
		this.reconcilerNotes = reconcilerNotes;
	}

	/**
	 * @return the mediaCategory
	 */
	public MediaCategory getMediaCategory() {
		return mediaCategory;
	}

	/**
	 * @param mediaCategory the mediaCategory to set
	 */
	public void setMediaCategory(MediaCategory mediaCategory) {
		this.mediaCategory = mediaCategory;
	}

	/**
	 * @return the nonDicomFiles
	 */
	public List<NonDicomFile> getNonDicomFiles() {
		return nonDicomFiles;
	}

	/**
	 * @param nonDicomFiles the nonDicomFiles to set
	 */
	public void setNonDicomFiles(List<NonDicomFile> nonDicomFiles) {
		this.nonDicomFiles = nonDicomFiles;
	}
}
