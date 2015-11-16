/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: October 19, 2005
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWPETERB
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
package gov.va.med.imaging.dicom.utilities.impl;

import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.dicom.dcftoolkit.common.impl.DicomDataSetImpl;
import gov.va.med.imaging.dicom.utilities.interfaces.IBusinessDataSet;

/**
 *
 * @author William Peterson
 *
 */
public class BusinessDataSetImpl implements IBusinessDataSet {

    
    IDicomDataSet genericDDS = null;
    
    String sopInstanceUID = null;
    
    boolean vrValidDataSet = true;
    /**
     * Constructor
     *
     * 
     */
    public BusinessDataSetImpl() {
        super();
        genericDDS = new DicomDataSetImpl();
    }

    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.utilities.interfaces.IBusinessDataSet#setDicomDataSet(gov.va.med.imaging.dicom.dcftoolkit.scu.interfaces.IDicomDataSet)
     */
    public void setDicomDataSet(IDicomDataSet dicomDataSet) {
        //
        this.genericDDS = dicomDataSet;
        this.vrValidDataSet = true;
    }

    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.utilities.interfaces.IBusinessDataSet#getDicomDataSet()
     */
    public IDicomDataSet getDicomDataSet() {
        //
        return this.genericDDS;
    }

    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.scu.interfaces.IBusinessDataSet#getSeriesInstanceUID()
     */
    public String getSeriesInstanceUID() {
        String seriesInstance = this.genericDDS.getSeriesInstanceUID();
        return seriesInstance;
    }
    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.scu.interfaces.IBusinessDataSet#getSOPInstanceUID()
     */
    public String getSOPInstanceUID() {
        if(this.vrValidDataSet){
        	String sopInstance = this.genericDDS.getSOPInstanceUID();
        	return sopInstance;
        }
        return this.sopInstanceUID;
    }
    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.scu.interfaces.IBusinessDataSet#getStudyInstanceUID()
     */
    public String getStudyInstanceUID() {
        String studyInstance = this.genericDDS.getStudyInstanceUID();
        return studyInstance;
    }
    
    public void setVRInvalidDataSet(String sopInstanceUID){
    	this.sopInstanceUID = sopInstanceUID;
    	this.vrValidDataSet = false;
    }
    
    public boolean isDataSetVRValid(){
    	return this.vrValidDataSet;
    }
}
