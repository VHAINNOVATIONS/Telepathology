/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 01, 2007
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWLOUTHJ
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
package gov.va.med.imaging.exchange.business.storage;

import gov.va.med.imaging.core.interfaces.StorageCredentials;
import gov.va.med.imaging.exchange.business.PersistentEntity;

import java.io.Serializable;

public class Place implements PersistentEntity, Serializable, StorageCredentials
{
	private static final long serialVersionUID = 1L;

	//
	// Fields
	//
	private int id;
	private String siteId;
	private String siteNumber;
	private String siteName;
	private String username;
	private String password;

	//
	// Default constructor
	//
	public Place()
	{
	}

	//
	// Additional constructors
	//
	public Place(int id, String siteId, String siteNumber, String siteName) {
		this.id = id;
		this.siteId = siteId;
		this.siteNumber = siteNumber;
		this.siteName = siteName;
	}

	public Place(int id, String siteId, String siteNumber, String siteName, String username, String password) {
		this.id = id;
		this.siteId = siteId;
		this.siteNumber = siteNumber;
		this.siteName = siteName;
		this.username = username;
		this.password = password;
	}

	//
	// Properties
	//
    public int getId() {
		return id;
	}

	public String getPassword() {
		return password;
	}

	public String getSiteId() {
		return siteId;
	}
	public String getSiteName() {
		return siteName;
	}

	public String getSiteNumber() {
		return siteNumber;
	}

    public String getUsername() {
		return username;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public void setSiteNumber(String siteNumber) {
		this.siteNumber = siteNumber;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		final Place other = (Place) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
