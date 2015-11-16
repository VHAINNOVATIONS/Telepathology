package gov.va.med.imaging.dicom.dcftoolkit.common.validation;

import gov.va.med.imaging.dicom.common.Constants;
import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.dicom.common.interfaces.IIODViolationList;
import gov.va.med.imaging.dicom.dcftoolkit.common.validation.interfaces.IDoIODValidation;
import gov.va.med.imaging.exchange.business.dicom.DicomAE;
import gov.va.med.imaging.exchange.business.dicom.exceptions.DicomException;
import gov.va.med.imaging.exchange.business.dicom.exceptions.IODViolationException;
import gov.va.med.imaging.exchange.business.dicom.exceptions.UnknownSOPClassException;
import gov.va.med.imaging.exchange.business.dicom.exceptions.ValidateIODException;

import org.apache.log4j.Logger;

import com.lbs.DCS.DCSException;
import com.lbs.DCS.DimseMessage;
import com.lbs.DCS.VRValidator;
import com.lbs.DCS.ValidationErrorList;


public class DoIODValidationImpl implements IDoIODValidation {

    private String lastSOPClass = "";
    
    private String lastSeriesInstanceUID = "";
    
    private boolean hasLastObjectFailed = false;
        
    private static Logger logger = Logger.getLogger(DoIODValidationImpl.class);

    /**
     * Constructor
     *
     */
    public DoIODValidationImpl() {
    }
    
	public int doIODValidation(DicomAE dicomAE,	IDicomDataSet dataset) throws IODViolationException {
		int iodValidationStatus = Constants.IOD_NOT_CHECKED;
		
		if (dicomAE.isValidateIODs()) {
			try {
				boolean changedSOPClassOrSeriesUID = false;
				IIODViolationList violations = null;

				// Validate the object when the SOP Class or the Series Instance
				// UID changes.
				// Determine if SOP Class changed.
				if (!(dataset.getSOPClass().equals(this.lastSOPClass))) {
					changedSOPClassOrSeriesUID = true;
					this.lastSOPClass = dataset.getSOPClass();
				}
				// Determine if Series Instance UID changed.
				if (!(dataset.getSeriesInstanceUID()
						.equals(this.lastSeriesInstanceUID))) {
					changedSOPClassOrSeriesUID = true;
					this.lastSeriesInstanceUID = dataset.getSeriesInstanceUID();
				}
				// Validate the object if the previous object failed or the SOP
				// Class changed or the Series Instance UID changed.
				if (this.hasLastObjectFailed || changedSOPClassOrSeriesUID) {
					// The reason I have a try/catch block is in case we
					// determine we want different behavior if
					// an exception is thrown while attempting to validate the
					// object. The current behavior is to ignore
					// the exceptions and continue with processing the object.

					violations = this.validateIOD(dataset); // "this" is coming from ServiceSCP
				}

				// Determine the results of the IOD Validation.
				if (violations != null) {
					if (violations.hasViolationErrors()) {
						this.hasLastObjectFailed = true;
						String str = this.logViolations(violations); // "this" is coming from ServiceSCP
						if (!dicomAE.isRelaxValidation()) {
							// set essential data in exception message
							throw new IODViolationException(
									"Error=IOD Violation Error(s) in object="
											+ dataset.getSOPInstanceUID()
											+ " of Series="
											+ dataset.getSeriesInstanceUID()
											+ " ->\n" + str);
						} else {
							iodValidationStatus = Constants.IOD_NOT_VALID;
						}
					} else {
						this.hasLastObjectFailed = false;
						iodValidationStatus = Constants.IOD_VALID;
					}
				}
			} catch (UnknownSOPClassException usopcX) {
				logger.error("Unknown SOP Class for IOD Validation: "
						+ dataset.getSOPClass());
				iodValidationStatus = Constants.IOD_UNKNOWN;
			}
		}
		return iodValidationStatus;
	}
	
    /**
     * Validate the IOD (Image Object Definition) of a DicomDataSet.
     * Currently, the IOD Validation is performed using a compiled XLST and code that was written by VistA Imaging.  The DCF Toolkit 
     * is not used for this method.  Primary reason was the DCF Toolkit did not offer a way to perform this work.
     * 
     * @param dds represents the DicomDataSet.
     * @return IIODViolation  This is generic (nonDICOM Toolkit specific) list of errors and warnings.
     * @throws IODViolationException
     * @throws UnknownSOPClassException
     */
    public  IIODViolationList validateIOD(IDicomDataSet dds)
		throws IODViolationException, UnknownSOPClassException{

    	IIODViolationList violations = null;
		try{
			violations = dds.validateIOD();
		}
		catch(ValidateIODException viodX){
			logger.error(this.getClass().getName()+": Exception thrown while validating object IOD. /n"+viodX.getMessage());
			throw new IODViolationException("IOD Validation Exception");
		}
		return violations;
	}

    /**
     * Validate the VRs (Value Representation) of each element in the DIMSE Message.
     * Currently, the VR Validation is performed using the DCF Toolkit.
     * 
     * @param ae represents the DICOM AE.
     * @param dm represents the DIMSE Message to validate.
     * @return ValidationErrorList represents an object that contains possible VR violations.
     */
    public ValidationErrorList validateVRs(DicomAE ae, DimseMessage dm){

    	ValidationErrorList vrList = new ValidationErrorList();
    	if(ae.isValidateVRs()){
    		vrList = VRValidator.instance().validateDimseMessage(dm);
        	if(ae.isRelaxValidation()){
            	if(vrList.hasErrors()){
            		int commandField;
					try {
						commandField = dm.commandField();
					} catch (DCSException dcsX){
						commandField = 0;
					}
            		String str = this.logViolations(vrList);
            		logger.warn(this.getClass().getName()+": VR Violation Error(s) in "
            				+DimseMessage.translateCommand(commandField)+" message from "+ae.getRemoteAETitle()+".\n"
            				+"However, an attempt will still be made to process the Dimse Message.\n"
            				+"VR Violations= "+ str);
            		//pass a clean, empty error list.
            		vrList = new ValidationErrorList();
            	}
            }
    	}
    	return vrList;
    }	

    public String logViolations (IIODViolationList list){
    	return list.toString();
    }    
    
    public String logViolations(ValidationErrorList list){
    	return list.toString();
    }
    

}
