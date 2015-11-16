/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: September 26, 2006
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
package gov.va.med.imaging.dicom.utilities.api.reconstitution.interfaces;

import gov.va.med.imaging.SizedInputStream;
import gov.va.med.imaging.dicom.utilities.exceptions.GenericDicomReconstitutionException;
import gov.va.med.imaging.dicom.utilities.interfaces.IBusinessDataSet;

import java.util.HashMap;

/**
 * Interface.  The implementation of this Interface is the Boundary class for 
 * reconstituting a DICOM object based on current files in the Legacy environment and 
 * changes from Vista HIS. 
 *
 * @author William Peterson
 *
 */
public interface DicomObjectReconstitutionFacade {

    /**
     * Assemble a DICOM object based on the existing Text and Targa files in Legacy Vista 
     * Imaging and additional changes from Vista HIS.  Since this is a Boundary class, the 
     * actual work will not take place in the implementation of this Interface.
     * 
     * @param textFilename represents the name of the Text file.
     * @param tgaFilename represents the name of the Targa file.
     * @param hischanges represents the Vista HIS changes.
     * @param toValidate represents if the DicomDataSet object is to be validate before returning.
     * @return represents the generic DicomDataSet object.
     */
    public abstract IBusinessDataSet assembleDicomObject(String textFilename, 
            String tgaFilename, HashMap<String,String> hisChanges, boolean toValidate)
            throws GenericDicomReconstitutionException;
    
    /**
     * Assemble a DICOM stream based on the existing Text and Targa streams from Legacy Vista 
     * Imaging and additional changes from Vista HIS.  Since this is a Boundary class, the 
     * actual work will not take place in the implementation of this Interface. Note: the 
     * stream format of the TXT file is expected to contain the HIS changes that represents
     * the latest local Vista database values to the patient/study.
     * 
     * @param sizedTextStream represents the stream of VistA Imaging TXT data with byte size.
     * @param sizedTgaStream represents the stream of the VistA Imaging Targa file with byte size..
     * @param toValidate represents if the DicomDataSet object is to be validate before returning.
     * @return represents the generic DicomDataSet object (part 10 format) in a byte array.
     */
    public abstract byte[] assembleDicomStream(SizedInputStream sizedTextStream, 
    		SizedInputStream sizedTgaStream, boolean toValidate)
            throws GenericDicomReconstitutionException;
    
    /**
     * Update a DICOM object based on the existing DICOM file in Legacy Vista Imaging and
     * additional changes from Vista HIS.  Since this is a Boundary class, the actual work 
     * will not take place in the implementation of this Interface.
     * 
     * @param dicomFile represents the DICOM filename.
     * @param hisChanges represents the Vista HIS changes.
     * @param toValidate represents if the DicomDataSet object is to be validate before returning.
     * @return represents the generic DicomDataSet object.
     */
    public abstract IBusinessDataSet updateDicomObject(String dicomFile, HashMap<String,String> hisChanges,
            boolean toValidate) throws GenericDicomReconstitutionException;   
    /**
     * Update a DICOM stream based on the existing DICOM stream from Legacy Vista Imaging and
     * additional changes from Vista HIS.  Since this is a Boundary class, the actual work 
     * will not take place in the implementation of this Interface. Note: the stream format of
     * the TXT file is expected to contain the HIS changes that represents the latest local
     * Vista database values to the patient/study.
     * 
     * @param sizedDicomStream represents the stream of VistA Imaging DCM (DICOM) data with byte size.
     * @param sizedTextStream represents the stream of VistA Imaging TXT data with byte size.
     * @param toValidate represents if the DicomDataSet object is to be validate before returning.
     * @return represents the generic DicomDataSet object (part 10 format) in a byte array.
    */
    public abstract byte[] updateDicomStream(SizedInputStream sizedDicomStream, SizedInputStream sizedTextStream,
            boolean toValidate) throws GenericDicomReconstitutionException;   
}
