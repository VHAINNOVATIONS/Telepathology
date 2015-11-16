package gov.va.med.imaging.exchange.business.dicom.importer;

import java.util.List;

import gov.va.med.imaging.exchange.business.Patient;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Reconciliation {

	private boolean useExistingOrder;
    private boolean createRadiologyOrder;
    private boolean isStudyToBeReadByVaRadiologist;
    private boolean isReconciliationComplete;
    private Patient patient;
    private Study study;
    private Order order;
    private boolean isPatientFromStaging;
    private boolean isPatientPreviouslyResolved;
    private ImagingLocation imagingLocation;
    private List<NonDicomFile> nonDicomFiles;
    
    public boolean isUseExistingOrder() {
		return useExistingOrder;
	}

	public void setUseExistingOrder(boolean useExistingOrder) {
		this.useExistingOrder = useExistingOrder;
	}

	public boolean isCreateRadiologyOrder() {
		return createRadiologyOrder;
	}

	public void setCreateRadiologyOrder(boolean createRadiologyOrder) {
		this.createRadiologyOrder = createRadiologyOrder;
	}

    public boolean isIsStudyToBeReadByVaRadiologist() 
    {
        return isStudyToBeReadByVaRadiologist;
    }

    public void setIsStudyToBeReadByVaRadiologist(boolean value) 
    {
        this.isStudyToBeReadByVaRadiologist = value;
    }

    public boolean isIsReconciliationComplete() 
    {
        return isReconciliationComplete;
    }

    public void setIsReconciliationComplete(boolean value) 
    {
        this.isReconciliationComplete = value;
    }

    public Patient getPatient() 
    {
        return patient;
    }

    public void setPatient(Patient value) 
    {
        this.patient = value;
    }

    public Study getStudy() 
    {
        return study;
    }

    public void setStudy(Study value) 
    {
        this.study = value;
    }

    public Order getOrder() 
    {
        return order;
    }

    public void setOrder(Order value) 
    {
        this.order = value;
    }

	public void setPatientFromStaging(boolean isPatientFromStaging) {
		this.isPatientFromStaging = isPatientFromStaging;
	}

	public boolean isPatientFromStaging() {
		return isPatientFromStaging;
	}

	public void setPatientPreviouslyResolved(boolean isPatientPreviouslyResolved) {
		this.isPatientPreviouslyResolved = isPatientPreviouslyResolved;
	}

	public boolean isPatientPreviouslyResolved() {
		return isPatientPreviouslyResolved;
	}

	/**
	 * @return the imagingLocation
	 */
	public ImagingLocation getImagingLocation() {
		return imagingLocation;
	}

	/**
	 * @param imagingLocation the imagingLocation to set
	 */
	public void setImagingLocation(ImagingLocation imagingLocation) {
		this.imagingLocation = imagingLocation;
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
