package gov.va.med.imaging.dicom.dcftoolkit.common.validation.interfaces;

import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.exchange.business.dicom.DicomAE;
import gov.va.med.imaging.exchange.business.dicom.exceptions.IODViolationException;

/**
 * @author vhaiswtittoc
 *
 */
public interface IDoIODValidation {
	
//	public void DoIODValidationImpl ();
//	/**
//	 * 
//	 * @param dicomdataset
//	 */
//	public void DoIODValidationImpl(Object dicomdataset);
//	
	/**
     * 
     * @param dicomAE  AE title of Storage SCU, has specific settings (Validate, Reject, Warning, etc options). 
     * @param dds	represents the DicomDataSet object.
     * @param dataset  represents the IDicomDataSet object.
     * @return
     * @throws DCSException
     * @throws IODViolationException
     */
	public int doIODValidation(DicomAE dicomAE, IDicomDataSet dataset) throws IODViolationException;
}
