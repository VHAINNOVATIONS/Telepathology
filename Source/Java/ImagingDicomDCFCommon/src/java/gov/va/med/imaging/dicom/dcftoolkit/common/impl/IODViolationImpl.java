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
package gov.va.med.imaging.dicom.dcftoolkit.common.impl;

import gov.va.med.imaging.dicom.common.interfaces.IIODViolation;

/**
 * @author vhaiswpeterb
 *
 */
public class IODViolationImpl implements IIODViolation {

	private int level;
	private String reason;
	
	public IODViolationImpl(){
		this.level = 0;
	}
	
	public IODViolationImpl(int level, String violation){
		this.level = level;
		this.reason = violation;
	}
	
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IIODViolation#getError()
	 */
	@Override
	public String getError() {
		return this.reason;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IIODViolation#getViolationLevel()
	 */
	@Override
	public int getViolationLevel() {
		return level;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IIODViolation#setViolationLevel(gov.va.med.imaging.dicom.common.interfaces.IDicomElement, int, java.lang.String)
	 */
	@Override
	public void setViolationLevel(int level, String violation) {
		this.level = level;
		this.reason = violation;
	}
}
