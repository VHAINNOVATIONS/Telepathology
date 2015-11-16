/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: 
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswpeterb
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

import gov.va.med.imaging.exchange.business.PersistentEntity;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Core Value Object class that encapsulates a HashMap.  The HashMap contains updated
 * Patient/Study information for an DICOM Instance.  It is represented in name-value pairs
 * where the name represents the DICOM Tag (i.e. 0008,0050).
 * 
 * @author vhaiswpeterb
 *
 */
public class DicomInstanceUpdateInfo 
extends HashMap<String, String> 
implements PersistentEntity, Serializable{

	private static final long serialVersionUID = -2650560179239178578L;

	private int id;
	

	/**
	 * 
	 */
	public DicomInstanceUpdateInfo() {
		super();
	}

	
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

}
