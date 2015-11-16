package gov.va.med.imaging.exchange.business.dicom.importer;

import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.exchange.business.PersistentEntity;
import gov.va.med.imaging.exchange.business.dicom.DicomUtils;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlRootElement
public class Study implements PersistentEntity
{

	private int id;
	private int idInMediaBundle;
    private String uid;
    private String accessionNumber;
    private String studyDate;
    private String studyTime;
    private String description;
    private String referringPhysician;
	private String imageStatistics;
    private Patient patient;
    private String procedure;
    private String modalitiesInStudy = null;
    private String importStatus;
    private boolean toBeDeletedOnly;
    private int totalNumberOfImagesInStudy;
    private int numberOfImagesAlreadyImported;
    private List<Series> series = new ArrayList<Series>();
    private Reconciliation reconciliation;
	private boolean failedImport;
	private String importErrorMessage;
    private Patient previouslyReconciledPatient;
    private Order previouslyReconciledOrder;
    private String originIndex;

	/**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return idInMediaBundle;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(int value) {
        this.idInMediaBundle = value;
    }


    /**
     * Gets the value of the idInMediaBundle property.
     * 
     */
    public int getIdInMediaBundle() {
        return idInMediaBundle;
    }

    /**
     * Sets the value of the idInMediaBundle property.
     * 
     */
    public void setIdInMediaBundle(int value) {
        this.idInMediaBundle = value;
    }

    /**
     * Gets the value of the uid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets the value of the uid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUid(String value) {
        this.uid = value;
    }

    /**
     * Gets the value of the accessionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccessionNumber() {
        return accessionNumber;
    }

    /**
     * Sets the value of the accessionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccessionNumber(String value) {
        this.accessionNumber = value;
    }

    /**
     * Gets the value of the studyDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public String getStudyDate() {
        return studyDate;
    }

    /**
     * Sets the value of the studyDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStudyDate(String value) {
        this.studyDate = value;
    }

    public String getStudyTime() {
		return studyTime;
	}

	public void setStudyTime(String studyTime) {
		this.studyTime = studyTime;
	}

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the imageStatistics property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImageStatistics() {
        return imageStatistics;
    }

    /**
     * Sets the value of the imageStatistics property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImageStatistics(String value) {
        this.imageStatistics = value;
    }

    /**
     * Gets the value of the patient property.
     * 
     * @return
     *     possible object is
     *     {@link Patient }
     *     
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Sets the value of the patient property.
     * 
     * @param value
     *     allowed object is
     *     {@link Patient }
     *     
     */
    public void setPatient(Patient value) {
        this.patient = value;
    }

    /**
     * Gets the value of the procedure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcedure() {
        return procedure;
    }

    /**
     * Sets the value of the procedure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcedure(String value) {
        this.procedure = value;
    }

    /**
     * Gets the value of the ImportStatus property.
     * 
     */
    public String getImportStatus() {
        return importStatus;
    }

    /**
     * Sets the value of the ImportStatus property.
     * 
     */
    public void setImportStatus(String value) {
        this.importStatus = value;
    }

    /**
     * Gets the value of the totalNumberOfImagesInStudy property.
     * 
     */
    public int getTotalNumberOfImagesInStudy() {
        return totalNumberOfImagesInStudy;
    }

    /**
     * Sets the value of the totalNumberOfImagesInStudy property.
     * 
     */
    public void setTotalNumberOfImagesInStudy(int value) {
        this.totalNumberOfImagesInStudy = value;
    }

    /**
     * Gets the value of the numberOfImagesAlreadyImported property.
     * 
     */
    public int getNumberOfImagesAlreadyImported() {
        return numberOfImagesAlreadyImported;
    }

    /**
     * Sets the value of the numberOfImagesAlreadyImported property.
     * 
     */
    public void setNumberOfImagesAlreadyImported(int value) {
        this.numberOfImagesAlreadyImported = value;
    }

    /**
     * Gets the value of the series property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSeries }
     *     
     */
    public List<Series> getSeries() {
        return series;
    }

    /**
     * Sets the value of the series property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSeries }
     *     
     */
    public void setSeries(List<Series> value) {
        this.series = value;
    }

	public void setModalitiesInStudy(String modalitiesInStudy) {
		this.modalitiesInStudy = modalitiesInStudy;
	}

	public String getModalitiesInStudy() {
		return modalitiesInStudy;
	}
    public Reconciliation getReconciliation() {
		return reconciliation;
	}

	public void setReconciliation(Reconciliation reconciliation) {
		this.reconciliation = reconciliation;
	}

	public void clearAllSeries() 
	{
		setSeries(new ArrayList<Series>());
	}

	public void setToBeDeletedOnly(boolean toBeDeletedOnly) {
		this.toBeDeletedOnly = toBeDeletedOnly;
	}

	public boolean isToBeDeletedOnly() {
		return toBeDeletedOnly;
	}

	public void setReferringPhysician(String referringPhysician) {
		this.referringPhysician = DicomUtils.reformatDicomName(referringPhysician);
	}

	public String getReferringPhysician() {
		return referringPhysician;
	}

	public void setFailedImport(boolean failedImport) {
		this.failedImport = failedImport;
	}

	public boolean getFailedImport() {
		return failedImport;
	}

	public void setImportErrorMessage(String importErrorMessage) {
		this.importErrorMessage = importErrorMessage;
	}

	public String getImportErrorMessage() {
		return importErrorMessage;
	}

	public void setPreviouslyReconciledPatient(
			Patient previouslyReconciledPatient) {
		this.previouslyReconciledPatient = previouslyReconciledPatient;
	}

	public Patient getPreviouslyReconciledPatient() {
		return previouslyReconciledPatient;
	}

	public void setPreviouslyReconciledOrder(Order previouslyReconciledOrder) {
		this.previouslyReconciledOrder = previouslyReconciledOrder;
	}

	public Order getPreviouslyReconciledOrder() {
		return previouslyReconciledOrder;
	}

	public void setOriginIndex(String originIndex) {
		this.originIndex = originIndex;
	}

	public String getOriginIndex() {
		return originIndex;
	}
}
