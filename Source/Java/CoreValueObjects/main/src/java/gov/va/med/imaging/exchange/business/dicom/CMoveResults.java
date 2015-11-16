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
import java.util.HashSet;

/**
 * Core Value Object class that encapsulates a HashSet.  The HashSet entries represent the 
 * list of Instances to return in a C-Store operation based on the C-Move Request.  

 * @author vhaiswpeterb
 *
 */
public class CMoveResults 
extends HashSet<InstanceStorageInfo> 
implements PersistentEntity, Serializable {

	private static final long serialVersionUID = 1154978366662540049L;
	private boolean duplicateStudyInstanceUID = false;

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public void setId(int id) {		
	}

	/**
	 * @return the duplicateStudyInstanceUID
	 */
	public boolean isDuplicateStudyInstanceUID() {
		return duplicateStudyInstanceUID;
	}

	/**
	 * @param duplicateStudyInstanceUID the duplicateStudyInstanceUID to set
	 */
	public void setDuplicateStudyInstanceUID(boolean duplicateStudyInstanceUID) {
		this.duplicateStudyInstanceUID = duplicateStudyInstanceUID;
	}

	
}
