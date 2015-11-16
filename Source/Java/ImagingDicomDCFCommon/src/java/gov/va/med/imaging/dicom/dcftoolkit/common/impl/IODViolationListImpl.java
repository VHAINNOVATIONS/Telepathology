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
import gov.va.med.imaging.dicom.common.interfaces.IIODViolationList;

import java.util.Iterator;
import java.util.Vector;

/**
 * @author vhaiswpeterb
 *
 */
public class IODViolationListImpl implements IIODViolationList {

	private Vector<IIODViolation> violations;
	private int errorCount;
	private int warningCount;
	private String manufacturer = null;
	private String model = null;
	private String software = null;
	private String sopClass = null;
	
	
    public IODViolationListImpl(){
    	this.errorCount = 0;
    	this.warningCount = 0;
    	this.violations = new Vector<IIODViolation>();
    }
    
    public IODViolationListImpl(String manufacturer, String model,
			String softwareVersion, String sopClass){
    	this.errorCount = 0;
    	this.warningCount = 0;
    	this.violations = new Vector<IIODViolation>();
    	
    	this.manufacturer = (manufacturer != null) ? manufacturer.trim() : manufacturer;
    	this.model = (model != null) ? model.trim() : model;
    	this.software = (softwareVersion != null) ? softwareVersion.trim() : softwareVersion;
    	this.sopClass = (sopClass != null) ? sopClass.trim() : sopClass;
    }
    
    
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IIODViolationList#addViolation(gov.va.med.imaging.dicom.common.interfaces.IIODViolation)
	 */
	@Override
	public void addViolation(IIODViolation violation) {
		
		int violationLevel = violation.getViolationLevel();
		if(violationLevel == IIODViolation.VIOLATION_ERROR){
			this.errorCount++;
		}
		if(violationLevel == IIODViolation.VIOLATION_WARNING){
			this.warningCount++;
		}
		this.violations.add(violation);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IIODViolationList#getErrorCount()
	 */
	@Override
	public int getErrorCount() {
		return this.errorCount;
	}


	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IIODViolationList#getManufacturerName()
	 */
	@Override
	public String getManufacturerName() {
		return this.manufacturer;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IIODViolationList#getModelName()
	 */
	@Override
	public String getModelName() {
		return this.model;
	}


	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IIODViolationList#getSOPClass()
	 */
	@Override
	public String getSOPClass() {
		return this.sopClass;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IIODViolationList#getSoftwareVersion()
	 */
	@Override
	public String getSoftwareVersion() {
		return this.software;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IIODViolationList#getViolationArray()
	 */
	@Override
	public IIODViolation[] getViolationArray() {
		return (IIODViolation[])this.violations.toArray();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IIODViolationList#getViolationAt(int)
	 */
	@Override
	public IIODViolation getViolationAt(int index) {
		return (IIODViolation)this.violations.elementAt(index);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IIODViolationList#getViolationCount()
	 */
	@Override
	public int getViolationCount() {
		return this.violations.size();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IIODViolationList#getWarningCount()
	 */
	@Override
	public int getWarningCount() {
		return this.warningCount;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IIODViolationList#hasViolationErrors()
	 */
	@Override
	public boolean hasViolationErrors() {
		return this.errorCount > 0 ? true: false;
	}

	
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IIODViolationList#hasViolationWarnings()
	 */
	@Override
	public boolean hasViolationWarnings() {
		return this.warningCount > 0 ? true: false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IIODViolationList#setDeviceInformation(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void setDeviceInformation(String manufacturer, String model,
			String softwareVersion, String sopClass) {
		this.manufacturer = manufacturer;
		this.model = model;
		this.software = softwareVersion;
		this.sopClass = sopClass;

	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}
	
	public String toString(){
		StringBuffer rtn = new StringBuffer("Modality Device: "+this.manufacturer+", "+this.model+", "+this.software+"\r\n");
		rtn = rtn.append("SOP Class: "+this.sopClass+"\r\n");
		Iterator<IIODViolation> iter = this.violations.iterator();
		while(iter.hasNext()){
			IODViolationImpl violation = (IODViolationImpl)iter.next();
			if(violation.getViolationLevel() == IIODViolation.VIOLATION_WARNING){
				rtn = rtn.append("Warning::");
			}
			if(violation.getViolationLevel() == IIODViolation.VIOLATION_ERROR){
				rtn = rtn.append("Error::");
			}
			rtn = rtn.append(violation.getError()+"\r\n");
		}
		return rtn.toString();
	}

}
