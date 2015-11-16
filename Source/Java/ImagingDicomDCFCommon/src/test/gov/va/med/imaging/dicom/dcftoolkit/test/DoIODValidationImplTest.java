package gov.va.med.imaging.dicom.dcftoolkit.test;

import gov.va.med.imaging.dicom.dcftoolkit.common.validation.DoIODValidationImpl;
import gov.va.med.imaging.exchange.business.dicom.DicomAE;
import gov.va.med.imaging.exchange.business.dicom.exceptions.ValidateVRException;

import org.junit.Test;

import com.lbs.DCS.DCM;
import com.lbs.DCS.DCSException;
import com.lbs.DCS.DicomDataSet;
import com.lbs.DCS.DimseMessage;
import com.lbs.DCS.ValidationErrorList;


public class DoIODValidationImplTest extends DicomDCFCommonTestBase {

	/**
	 * @param arg0
	 */
	public DoIODValidationImplTest(String arg0) {
		super(arg0);
	}

	@Test
	public void testValidateVRNoErrors(){
		DicomAE ae = null;
		DicomDataSet dds = null;
		DimseMessage dm = null;
		
		try {
			ae = this.createInitialDicomAE();
			dds = this.createInitialGoodDDS();
			dm = this.createInitialDM();
			dm.data(dds);
		} catch (DCSException e1) {
			fail();
		}

		DoIODValidationImpl validate = new DoIODValidationImpl();
		ValidationErrorList vrList = null;
		vrList = validate.validateVRs(ae, dm);
		assertFalse(vrList.hasErrors());
	}

	@Test
	public void testValidateVRErrors(){
		DicomAE ae = null;
		DicomDataSet dds = null;
		DimseMessage dm = null;
		
		try {
			ae = this.createInitialDicomAE();
			dds = this.createInitialBadDDS();
			dm = this.createInitialDM();
			dm.data(dds);
		} catch (DCSException e1) {
			fail();
		}

		DoIODValidationImpl validate = new DoIODValidationImpl();
		ValidationErrorList vrList = null;
		vrList = validate.validateVRs(ae, dm);
		assertTrue(vrList.hasErrors());
	}

	@Test
	public void testValidateVRNoErrorsNoCheck(){
		DicomAE ae = null;
		DicomDataSet dds = null;
		DimseMessage dm = null;
		
		try {
			ae = this.createInitialDicomAE();
			ae.setValidateIODs(false);
			ae.setRelaxValidation(true);
			dds = this.createInitialGoodDDS();
			dm = this.createInitialDM();
			dm.data(dds);
		} catch (DCSException e1) {
			fail();
		}

		DoIODValidationImpl validate = new DoIODValidationImpl();
		ValidationErrorList vrList = null;
		vrList = validate.validateVRs(ae, dm);
		assertFalse(vrList.hasErrors());
	}

	public void testValidateVRErrorsNoValidation(){
		DicomAE ae = null;
		DicomDataSet dds = null;
		DimseMessage dm = null;
		
		try {
			ae = this.createInitialDicomAE();
			ae.setValidateIODs(false);
			dds = this.createInitialBadDDS();
			dm = this.createInitialDM();
			dm.data(dds);
		} catch (DCSException e1) {
			fail();
		}

		DoIODValidationImpl validate = new DoIODValidationImpl();
		ValidationErrorList vrList = null;
		vrList = validate.validateVRs(ae, dm);
		assertFalse(vrList.hasErrors());
		
	}
		
	public void testValidateVRErrorsRelaxValidation(){
		DicomAE ae = null;
		DicomDataSet dds = null;
		DimseMessage dm = null;
		
		try {
			ae = this.createInitialDicomAE();
			ae.setRelaxValidation(true);
			dds = this.createInitialBadDDS();
			dm = this.createInitialDM();
			dm.data(dds);
		} catch (DCSException e1) {
			fail();
		}

		DoIODValidationImpl validate = new DoIODValidationImpl();
		ValidationErrorList vrList = null;
		vrList = validate.validateVRs(ae, dm);
		assertFalse(vrList.hasErrors());
		
	}

	public void testValidateVRErrorsBothFlagsOpposite(){
		DicomAE ae = null;
		DicomDataSet dds = null;
		DimseMessage dm = null;
		
		try {
			ae = this.createInitialDicomAE();
			ae.setValidateIODs(false);
			ae.setRelaxValidation(true);
			dds = this.createInitialBadDDS();
			dm = this.createInitialDM();
			dm.data(dds);
		} catch (DCSException e1) {
			fail();
		}

		DoIODValidationImpl validate = new DoIODValidationImpl();
		ValidationErrorList vrList = null;
		vrList = validate.validateVRs(ae, dm);
		assertFalse(vrList.hasErrors());
		
	}

	private DicomDataSet createInitialGoodDDS() throws DCSException{

		DicomDataSet dds = new DicomDataSet();
		dds.insert(DCM.E_STUDY_DATE, "20121122-20121122");
		dds.insert(DCM.E_QUERYRETRIEVE_LEVEL, "STUDY");
		dds.insert(DCM.E_STUDY_INSTANCE_UID, "");
		return dds;
	}

	private DicomDataSet createInitialBadDDS() throws DCSException{

		DicomDataSet dds = new DicomDataSet();
		char nul = 0x0;  
        String nulString = Character.toString(nul);
		dds.insert(DCM.E_STUDY_DATE, "20121122-20121122"+nulString);
		dds.insert(DCM.E_QUERYRETRIEVE_LEVEL, "STUDY");
		dds.insert(DCM.E_STUDY_INSTANCE_UID, "");
		return dds;
	}

	private DimseMessage createInitialDM() throws DCSException{

		DimseMessage dm = new DimseMessage();
		dm.affectedSopclassUid("1.2.840.10008.5.1.4.1.2.2.1");
		dm.commandField(DimseMessage.C_FIND_RQ);
		dm.messageId(1);
		dm.priority(0);
		dm.dataSetType(256);
		return dm;
	}
	
	private DicomAE createInitialDicomAE(){
		DicomAE ae = new DicomAE();
		ae.setApplicationName("TEST");
		ae.setRemoteAETitle("FUDGE");
		ae.setLocalAETitle("RIPPLE");
		ae.setValidateIODs(true);
		ae.setRelaxValidation(false);
		return ae;
	}
}
