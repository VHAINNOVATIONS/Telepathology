/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 10, 2008
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.exchange.business.dicom;

/**
 * Represents a patient Reference entity in persistence (DB).
 * 
 * @author vhaiswtittoc
 *
 */
public class DGWEmailInfo 
{	
	private String hostName="localhost";
	private String eMailAddress;
	private String smtpAddress="smtp.va.gov" ;	// default is smtp.va.gov
	private String smtpPort="25";				// default 25
	private Boolean importerRunning=false;		// default NO
	private String dgwSiteID="";			// location configured on legacy DGW
	
	/**
	 * Create a new patient
	 * @param HostName of DICOM Gateway
	 * @param eMail address of recepient
	 * @param smtpAddr e-mail sender application service (default is smtp.va.gov)
	 * @param smtpPort e-mail sender application port (default "25")
	 * @param importerRunning if true DICOM correct logic is different
	 */
	public DGWEmailInfo(String hostN, String eMail, String smtpAddr, String smtpPort, Boolean isImproterOn, String dgwSiteID)
	{
		if (!hostN.isEmpty())
			this.hostName = hostN;
		this.eMailAddress = eMail;
		if (!smtpAddr.isEmpty())
			this.smtpAddress = smtpAddr;
		if (!smtpPort.isEmpty())
			this.smtpPort = smtpPort;
		if (isImproterOn!=null)
			this.importerRunning = isImproterOn;
		if (dgwSiteID!=null)
			this.dgwSiteID = dgwSiteID;
	}
	

	@Override
	public String toString() 
	{
		return this.hostName + " e-mail (" + this.eMailAddress + "; Send to=" + this.smtpAddress + "; Port=" + this.smtpPort + 
								" Importer ON? = " + this.isImporterRunning() + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((hostName == null) ? 0 : hostName.hashCode());
		result = prime * result
				+ ((eMailAddress == null) ? 0 : eMailAddress.hashCode());
		result = prime * result
				+ ((smtpAddress == null) ? 0 : smtpAddress.hashCode());
		result = prime * result
				+ ((smtpPort == null) ? 0 : smtpPort.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DGWEmailInfo other = (DGWEmailInfo) obj;
		if (hostName == null) {
			if (other.hostName != null)
				return false;
		} else if (!hostName.equals(other.hostName))
			return false;
		if (eMailAddress == null) {
			if (other.eMailAddress != null)
				return false;
		} else if (!eMailAddress.equals(other.eMailAddress))
			return false;
		if (smtpAddress == null) {
			if (other.smtpAddress != null)
				return false;
		} else if (!smtpAddress.equals(other.smtpAddress))
			return false;
		if (smtpPort == null) {
			if (other.smtpPort != null)
				return false;
		} else if (!smtpPort.equals(other.smtpPort))
			return false;
		return true;
	}	
	
	// @Override
	public int compareTo(DGWEmailInfo that) 
	{
		return this.hostName.compareTo(that.hostName);
	}
	
	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getEMailAddress() {
		return eMailAddress;
	}

	public void setEMailAddress(String mailAddress) {
		eMailAddress = mailAddress;
	}

	public String getSmtpAddress() {
		return smtpAddress;
	}

	public void setSmtpAddress(String smtpAddress) {
		this.smtpAddress = smtpAddress;
	}

	public String getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}


	public Boolean isImporterRunning() {
		return importerRunning;
	}


	public void setImporterRunning(Boolean importerRunning) {
		this.importerRunning = importerRunning;
	}


	public String getDgwSiteID() {
		return dgwSiteID;
	}


	public void setDgwSiteID(String dgwSiteID) {
		this.dgwSiteID = dgwSiteID;
	}

}
