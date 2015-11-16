package gov.va.med.imaging.exchange.business.dicom.importer;

import gov.va.med.imaging.exchange.business.PersistentEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlRootElement
public class Order implements PersistentEntity, Serializable
{

    private int id;
    private int examinationsIen;
    private String registeredExamsIen;
    private String accessionNumber;
    private String caseNumber;
    private boolean isToBeCreated;
    private String location;
    private String specialty;
    private String orderDate;
    private String orderReason;
	private String examDate;
	private String examStatus;
    private String description;
    private String orderType;
    private Reconciliation reconciliation;
    private int orderingProviderIen;
    private OrderingProvider orderingProvider;
    private int orderingLocationIen;
    private OrderingLocation orderingLocation;
    private int procedureId;
    private String procedureName;
	private Procedure procedure;
	private List<ProcedureModifier> procedureModifiers = new ArrayList<ProcedureModifier>();
	private StatusChangeDetails statusChangeDetails;
	private String creditMethod;
	private String vistaGeneratedStudyUid;

    
    public String getOrderReason() {
		return orderReason;
	}

	public void setOrderReason(String orderReason) {
		this.orderReason = orderReason;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

    public boolean isToBeCreated() {
		return isToBeCreated;
	}

	public void setToBeCreated(boolean isToBeCreated) {
		this.isToBeCreated = isToBeCreated;
	}

    public String getExamDate() {
		return examDate;
	}

	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}

	public int getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(int procedureId) {
		this.procedureId = procedureId;
	}

	/**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Gets the value of the isToBeCreated property.
     * 
     */
    public boolean isIsToBeCreated() {
        return isToBeCreated;
    }

    /**
     * Sets the value of the isToBeCreated property.
     * 
     */
    public void setIsToBeCreated(boolean value) {
        this.isToBeCreated = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocation(String value) {
        this.location = value;
    }

    /**
     * Gets the value of the specialty property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialty() {
        return specialty;
    }

    /**
     * Sets the value of the specialty property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialty(String value) {
        this.specialty = value;
    }

    /**
     * Gets the value of the orderDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public String getOrderDate() {
        return orderDate;
    }

    /**
     * Sets the value of the orderDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOrderDate(String value) {
        this.orderDate = value;
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
     * Gets the value of the examStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExamStatus() {
        return examStatus;
    }

    /**
     * Sets the value of the examStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExamStatus(String value) {
        this.examStatus = value;
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
     * Gets the value of the reconciliation property.
     * 
     * @return
     *     possible object is
     *     {@link Reconciliation }
     *     
     */
    public Reconciliation getReconciliation() {
        return reconciliation;
    }

    /**
     * Sets the value of the reconciliation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Reconciliation }
     *     
     */
    public void setReconciliation(Reconciliation value) {
        this.reconciliation = value;
    }

    /**
     * Gets the value of the orderingLocation property.
     * 
     * @return
     *     possible object is
     *     {@link OrderingLocation }
     *     
     */
    public OrderingLocation getOrderingLocation() {
        return orderingLocation;
    }

    /**
     * Sets the value of the orderingLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderingLocation }
     *     
     */
    public void setOrderingLocation(OrderingLocation value) {
        this.orderingLocation = value;
    }

    /**
     * Gets the value of the orderingProvider property.
     * 
     * @return
     *     possible object is
     *     {@link OrderingProvider }
     *     
     */
    public OrderingProvider getOrderingProvider() {
        return orderingProvider;
    }

    /**
     * Sets the value of the orderingProvider property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderingProvider }
     *     
     */
    public void setOrderingProvider(OrderingProvider value) {
        this.orderingProvider = value;
    }

    /**
     * Gets the value of the procedure property.
     * 
     * @return
     *     possible object is
     *     {@link Procedure }
     *     
     */
    public Procedure getProcedure() {
        return procedure;
    }

    /**
     * Sets the value of the procedure property.
     * 
     * @param value
     *     allowed object is
     *     {@link Procedure }
     *     
     */
    public void setProcedure(Procedure value) {
        this.procedure = value;
    }

	public void setProcedureModifiers(List<ProcedureModifier> procedureModifiers) {
		this.procedureModifiers = procedureModifiers;
	}

	public List<ProcedureModifier> getProcedureModifiers() {
		return procedureModifiers;
	}

	public String getOrderType() 
	{
		return orderType;
	}

	public void setOrderType(String orderType) 
	{
		this.orderType = orderType;
	}

	public void setCaseNumber(String caseNumber) {
		this.caseNumber = caseNumber;
	}

	public String getCaseNumber() {
		return caseNumber;
	}

	public void setRegisteredExamsIen(String registeredExamsIen) {
		this.registeredExamsIen = registeredExamsIen;
	}

	public String getRegisteredExamsIen() {
		return registeredExamsIen;
	}

	public void setExaminationsIen(int examinationsIen) {
		this.examinationsIen = examinationsIen;
	}

	public int getExaminationsIen() {
		return examinationsIen;
	}

	public void setOrderingProviderIen(int orderingProviderIen) {
		this.orderingProviderIen = orderingProviderIen;
	}

	public int getOrderingProviderIen() {
		return orderingProviderIen;
	}

	public void setOrderingLocationIen(int orderingLocationIen) {
		this.orderingLocationIen = orderingLocationIen;
	}

	public int getOrderingLocationIen() {
		return orderingLocationIen;
	}

	/**
	 * @return the Status Change Details
	 */
	public StatusChangeDetails getStatusChangeDetails() {
		return statusChangeDetails;
	}

	/**
	 * @param statusChangeDetails the Status Change Details to set
	 */
	public void setStatusChangeDetails(StatusChangeDetails statusChangeDetails) {
		this.statusChangeDetails = statusChangeDetails;
	}

	/**
	 * @return the creditMethod
	 */
	public String getCreditMethod() {
		return creditMethod;
	}

	/**
	 * @param creditMethod the creditMethod to set
	 */
	public void setCreditMethod(String creditMethod) {
		this.creditMethod = creditMethod;
	}

	/**
	 * @return the vistaGeneratedStudyUid
	 */
	public String getVistaGeneratedStudyUid() {
		return vistaGeneratedStudyUid;
	}

	/**
	 * @param vistaGeneratedStudyUid the vistaGeneratedStudyUid to set
	 */
	public void setVistaGeneratedStudyUid(String vistaGeneratedStudyUid) {
		this.vistaGeneratedStudyUid = vistaGeneratedStudyUid;
	}

}
