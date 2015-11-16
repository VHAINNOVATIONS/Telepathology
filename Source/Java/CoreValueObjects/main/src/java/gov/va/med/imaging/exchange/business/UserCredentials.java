/**
 * UserCredentials.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.exchange.business;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserCredentials  implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String fullname;
    private String duz;
    private String ssn;
    private String siteName;
    private String siteNumber;
    List<String> securityKeys = new ArrayList<String>();
    
    public UserCredentials(){}
    
	public UserCredentials(String fullname, String duz, String ssn,
			String siteName, String siteNumber, List<String> securityKeys) {
		this.fullname = fullname;
		this.duz = duz;
		this.ssn = ssn;
		this.siteName = siteName;
		this.siteNumber = siteNumber;
		this.securityKeys = securityKeys;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getDuz() {
		return duz;
	}

	public void setDuz(String duz) {
		this.duz = duz;
	}

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteNumber() {
		return siteNumber;
	}

	public void setSiteNumber(String siteNumber) {
		this.siteNumber = siteNumber;
	}

	public List<String> getSecurityKeys() {
		return securityKeys;
	}

	public void setUserKeys(List<String> securityKeys) {
		this.securityKeys = securityKeys;
	}
    
}
