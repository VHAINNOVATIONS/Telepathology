/**
 * 
 */
package gov.va.med.imaging.exchange.business;

import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author VHAISWBECKEC
 *
 */
public class Requestor  
implements java.io.Serializable 
{
	private static final long serialVersionUID = -4017500105944224750L;

	public enum PurposeOfUse
	{
		routineMedicalCare("Routine medical care");
		
		private final String description;
		PurposeOfUse(String description)
		{
			this.description = description;
		}
		
		public String getDescription()
		{
			return this.description;
			
		}
	};
	
    private String username;
    private String ssn;
    private String facilityId;
    private String facilityName;
    private PurposeOfUse purposeOfUse;

    public Requestor() 
    {
    	TransactionContext transactionContext = TransactionContextFactory.get();
    	this.username = transactionContext.getFullName();
    	this.ssn = transactionContext.getSsn();
    	this.facilityId = transactionContext.getSiteNumber();
    	this.facilityName = transactionContext.getSiteName();
    	this.purposeOfUse = PurposeOfUse.routineMedicalCare; // DKB - established default
    }
    
    public Requestor(
           java.lang.String username,
           java.lang.String ssn,
           java.lang.String facilityId,
           java.lang.String facilityName,
           PurposeOfUse purposeOfUse) 
    {
           this.username = username;
           this.ssn = ssn;
           this.facilityId = facilityId;
           this.facilityName = facilityName;
           this.purposeOfUse = purposeOfUse;
    }

    public Requestor(
            java.lang.String username,
            java.lang.String ssn,
            java.lang.String facilityId,
            java.lang.String facilityName) 
     {
            this.username = username;
            this.ssn = ssn;
            this.facilityId = facilityId;
            this.facilityName = facilityName;
            this.purposeOfUse = PurposeOfUse.routineMedicalCare;
     }

    /**
     * Gets the username value for this RequestorType.
     * 
     * @return username
     */
    public java.lang.String getUsername() {
        return username;
    }


    /**
     * Sets the username value for this RequestorType.
     * 
     * @param username
     */
    public void setUsername(java.lang.String username) {
        this.username = username;
    }


    /**
     * Gets the ssn value for this RequestorType.
     * 
     * @return ssn
     */
    public java.lang.String getSsn() {
        return ssn;
    }


    /**
     * Sets the ssn value for this RequestorType.
     * 
     * @param ssn
     */
    public void setSsn(java.lang.String ssn) {
        this.ssn = ssn;
    }


    /**
     * Gets the facilityId value for this RequestorType.
     * 
     * @return facilityId
     */
    public java.lang.String getFacilityId() {
        return facilityId;
    }


    /**
     * Sets the facilityId value for this RequestorType.
     * 
     * @param facilityId
     */
    public void setFacilityId(java.lang.String facilityId) {
        this.facilityId = facilityId;
    }


    /**
     * Gets the facilityName value for this RequestorType.
     * 
     * @return facilityName
     */
    public java.lang.String getFacilityName() {
        return facilityName;
    }


    /**
     * Sets the facilityName value for this RequestorType.
     * 
     * @param facilityName
     */
    public void setFacilityName(java.lang.String facilityName) {
        this.facilityName = facilityName;
    }


    /**
     * Gets the purposeOfUse value for this RequestorType.
     * 
     * @return purposeOfUse
     */
    public PurposeOfUse getPurposeOfUse() {
        return purposeOfUse;
    }


    /**
     * Sets the purposeOfUse value for this RequestorType.
     * 
     * @param purposeOfUse
     */
    public void setPurposeOfUse(PurposeOfUse purposeOfUse) {
        this.purposeOfUse = purposeOfUse;
    }


}
