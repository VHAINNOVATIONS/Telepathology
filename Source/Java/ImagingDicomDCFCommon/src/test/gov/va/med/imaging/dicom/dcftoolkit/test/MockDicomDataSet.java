/*
 * Created on Apr 18, 2005
 *
 */
package gov.va.med.imaging.dicom.dcftoolkit.test;
import gov.va.med.imaging.dicom.common.Constants;
import gov.va.med.imaging.dicom.common.interfaces.IDicomElement;
import gov.va.med.imaging.dicom.dcftoolkit.common.impl.DicomDataSetImpl;
import gov.va.med.imaging.exchange.business.dicom.exceptions.DicomException;

import java.util.HashMap;

/**
 * @author Csaba Titton
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MockDicomDataSet extends DicomDataSetImpl{


	public static final short numDSets = 1; // number of datasets implemented in test

	private HashMap<String, IDicomElement> dicomElements = new HashMap<String, IDicomElement>();
	
	/** 
	 *
	 */
	public int getNumDSets() {
		return numDSets;
	}
	
	/* 
	 *
	 */
	public IDicomElement getDicomElement(String dicomTag) throws DicomException {
		// 
		return (IDicomElement)dicomElements.get(dicomTag);
	}
	
	/**
	 * Adds an element to the DataSet
	 * @param element
	 */
	public void addDicomElement(IDicomElement element)
	{
		dicomElements.put(element.getTagName(), element);
	}

	/**
	 * This is the method that creates the mocked datasets.
	 */
	public MockDicomDataSet() {
		super();
	}
	
	/**
	 * This is the method that contains all test data for the mocked datasets. Input parameter
	 * (see max count in static 'numDSets') decides which dataset is loaded to the dicomElements
	 * hashmap.
	 * Make sure the proper values are asserted in MappingTest.java!
	 */
	public void FillDicomDataSet(int dsNum) {
		// Add all the Dicom elements for the default Dataset
		dicomElements.clear();
		if (dsNum==1) {
			// Patient
			this.addDicomElement(new MockDicomElement("0010,0010", 1, Constants.DICOM_VR_PN, "familiy^given^middle^prefix^suffix")); // Nmae
			this.addDicomElement(new MockDicomElement("0010,0020", 1, Constants.DICOM_VR_LO, "666-34-1256")); // PatientId
			this.addDicomElement(new MockDicomElement("0010,0030", 1, Constants.DICOM_VR_DA, "19561224")); // DateOfBirth
			this.addDicomElement(new MockDicomElement("0010,0040", 1, Constants.DICOM_VR_CS, "M")); // Sex
			this.addDicomElement(new MockDicomElement("0010,1030", 1, Constants.DICOM_VR_DS, "78.5")); // Weight (kg)
			this.addDicomElement(new MockDicomElement("0010,1020", 1, Constants.DICOM_VR_DS, "159")); // Size (cm)
			this.addDicomElement(new MockDicomElement("0010,2160", 1, Constants.DICOM_VR_SH, "caucasian")); // EthnicGroup
			this.addDicomElement(new MockDicomElement("0010,21c0", 1, Constants.DICOM_VR_US, 1)); // PregnancyStatus
			this.addDicomElement(new MockDicomElement("0040,3001", 1, Constants.DICOM_VR_LO, "top secret")); // ConfidentialityConstraint
			this.addDicomElement(new MockDicomElement("0010,2000", 1, Constants.DICOM_VR_LO, "pacemaker")); // MedicalAlerts
			this.addDicomElement(new MockDicomElement("0010,2110", 1, Constants.DICOM_VR_LO, "Plutonium\\led\\anthrax")); // ContrastAllergies
			// visit
			this.addDicomElement(new MockDicomElement("0038,0010", 1, Constants.DICOM_VR_DA, "A321456")); // AdmissionId
			this.addDicomElement(new MockDicomElement("0038,0020", 1, Constants.DICOM_VR_DA, "2005.04.29")); // AdmittingDate - date
			this.addDicomElement(new MockDicomElement("0038,0021", 1, Constants.DICOM_VR_TM, "22:49:57.012345")); // AdmittingDate - time
	//		this.addDicomElement(new MockDicomElement("0038,0020", 1, Constants.DICOM_VR_DT, "20050429 224957.0123-0500")); // AdmittingDate (forced VR=DT for test!!!)
			this.addDicomElement(new MockDicomElement("0008,0080", 1, Constants.DICOM_VR_LO, "Saint No Mercy VA Hospital")); // InstituteName
			this.addDicomElement(new MockDicomElement("0008,0090", 1, Constants.DICOM_VR_LO, "Smith^John^^Dr.^M.D.")); // ReferringPhysicianName
			// ISR
			this.addDicomElement(new MockDicomElement("0008,0050", 1, Constants.DICOM_VR_SH, "20050428-12345")); // AccessionNumber
			this.addDicomElement(new MockDicomElement("0032,1032", 1, Constants.DICOM_VR_LO, "Dorkian^Mo^^Dr.^M.D.")); // RequestingPhysician
			this.addDicomElement(new MockDicomElement("0032,1033", 1, Constants.DICOM_VR_LO, "Orthopedix")); // RequestingService
			// RP
			this.addDicomElement(new MockDicomElement("0040,1001", 1, Constants.DICOM_VR_SH, "RP12345-1")); // RpId
			this.addDicomElement(new MockDicomElement("0032,1064", "0008,0100", "76543"));	// Procedure Code sequence (start)
			this.addDicomElement(new MockDicomElement("ffff,e000", 1, Constants.DICOM_VR_SQ_DELIM, "")); // sequence item start
			this.addDicomElement(new MockDicomElement("0008,0100", 1, Constants.DICOM_VR_SH, "76543")); // RpCode - value!!!
			this.addDicomElement(new MockDicomElement("0008,0102", 1, Constants.DICOM_VR_SH, "CPT")); // coding scheme
			this.addDicomElement(new MockDicomElement("0008,0102", 1, Constants.DICOM_VR_SH, "4")); // version number
			this.addDicomElement(new MockDicomElement("0008,0104", 1, Constants.DICOM_VR_LO, "bogus entry")); // meaning
			this.addDicomElement(new MockDicomElement("ffff,e00d", 1, Constants.DICOM_VR_SQ_DELIM, "")); // sequence item end
			this.addDicomElement(new MockDicomElement("ffff,e0dd", 1, Constants.DICOM_VR_SQ_DELIM, "")); // sequence end
			this.addDicomElement(new MockDicomElement("0040,1002", 1, Constants.DICOM_VR_LO, "limping to left leg")); // Reason
			this.addDicomElement(new MockDicomElement("0040,1008", 1, Constants.DICOM_VR_LO, "very confidential")); // ConfidentialityCode
			this.addDicomElement(new MockDicomElement("0032,1060", 1, Constants.DICOM_VR_LO, "Knee with Contrast")); // Description
			this.addDicomElement(new MockDicomElement("0040,1400", 1, Constants.DICOM_VR_LT, "This is the long text for requested procedure comments")); // Comments
			// SPS
			this.addDicomElement(new MockDicomElement("0040,0009", 1, Constants.DICOM_VR_SH, "SPS12345-1-1")); // ScheduledProcedureStepId
			this.addDicomElement(new MockDicomElement("0040,0020", 1, Constants.DICOM_VR_CS, "SCHEDULED")); // State (of SPS)
			this.addDicomElement(new MockDicomElement("0040,0001", 1, Constants.DICOM_VR_AE, "THIStheAETitle00")); // StationAeTitle
			this.addDicomElement(new MockDicomElement("0040,0002", 1, Constants.DICOM_VR_DA, "20050430")); // StartDate - date
			this.addDicomElement(new MockDicomElement("0040,0003", 1, Constants.DICOM_VR_TM, "074957.0")); // StartDate - time
			this.addDicomElement(new MockDicomElement("0008,0060", 1, Constants.DICOM_VR_CS, "CR")); // Modality
			// Study
			this.addDicomElement(new MockDicomElement("0020,000d", 1, Constants.DICOM_VR_UI, "1.2.840.113619.2.1.1.2703176852.560.948465036.612")); // InstanceUID (Study)
			this.addDicomElement(new MockDicomElement("0020,0010", 1, Constants.DICOM_VR_SH, "theStudyID")); // StudyId
			this.addDicomElement(new MockDicomElement("0008,0020", 1, Constants.DICOM_VR_DA, "20050429")); // StudyDate - date
			this.addDicomElement(new MockDicomElement("0008,0030", 1, Constants.DICOM_VR_TM, "152947.001")); // StudyDate - time
			this.addDicomElement(new MockDicomElement("0032,000a", 1, Constants.DICOM_VR_CS, "COMPLETED")); // StatusId
			this.addDicomElement(new MockDicomElement("0032,000c", 1, Constants.DICOM_VR_CS, "MEDIUM")); // StudyPriorityId
			this.addDicomElement(new MockDicomElement("0032,1030", 1, Constants.DICOM_VR_LO, "Double check single chance")); // Reason
			this.addDicomElement(new MockDicomElement("0008,1030", 1, Constants.DICOM_VR_LO, "Long description of study: who, when, what, why, how")); // Description
			this.addDicomElement(new MockDicomElement("0032,0034", 1, Constants.DICOM_VR_DA, "20050429")); // ReadDate - date
			this.addDicomElement(new MockDicomElement("0032,0035", 1, Constants.DICOM_VR_TM, "161359")); // ReadDate - time
			this.addDicomElement(new MockDicomElement("0008,1060", 1, Constants.DICOM_VR_PN, "Bread^Crumby^^^M.D.")); // ReadingPhysician
			this.addDicomElement(new MockDicomElement("0008,1050", 1, Constants.DICOM_VR_PN, "Ground^Beefy^^^M.D.")); // PerformingPhysician
			// Series
			this.addDicomElement(new MockDicomElement("0018,0015", 1, Constants.DICOM_VR_CS, "KNEE")); // BodyPart
			this.addDicomElement(new MockDicomElement("0020,000e", 1, Constants.DICOM_VR_UI, "1.2.840.113619.2.1.1.2703176852.560.948465036.613.1")); // InstanceUID (Series)
			this.addDicomElement(new MockDicomElement("0020,0052", 1, Constants.DICOM_VR_UI, "1.2.840.113619.2.1.1.2703176852.560.948465036.614")); // FrameOfReferenceUID
			this.addDicomElement(new MockDicomElement("0008,0021", 1, Constants.DICOM_VR_DA, "20050429")); // SeriesDate - date
			this.addDicomElement(new MockDicomElement("0008,0031", 1, Constants.DICOM_VR_TM, "161359")); // SeriesDate - time
			this.addDicomElement(new MockDicomElement("0020,0011", 1, Constants.DICOM_VR_IS, "3")); // SeriesNumber
			this.addDicomElement(new MockDicomElement("0020,0060", 1, Constants.DICOM_VR_CS, "L")); // Laterality
			this.addDicomElement(new MockDicomElement("0018,5100", 1, Constants.DICOM_VR_CS, "FFS")); // SpacialPosition
			// Instance
			this.addDicomElement(new MockDicomElement("0008,0008", 1, Constants.DICOM_VR_CS, "PRIMARY\\ORIGINAL")); // ImageType
			this.addDicomElement(new MockDicomElement("0008,0023", 1, Constants.DICOM_VR_DA, "20050429")); // ContentDate - date
			this.addDicomElement(new MockDicomElement("0008,0033", 1, Constants.DICOM_VR_TM, "161411")); // ContentDate - time
			this.addDicomElement(new MockDicomElement("0020,0012", 1, Constants.DICOM_VR_IS, "3")); // AcqusitionNumber
			this.addDicomElement(new MockDicomElement("0020,1002", 1, Constants.DICOM_VR_IS, "6")); // NumberOfImagesAcquired
			this.addDicomElement(new MockDicomElement("0008,0018", 1, Constants.DICOM_VR_UI, "1.2.840.113619.2.1.1.2703176852.560.948465036.613.1.3")); // SOP InstanceUID
			this.addDicomElement(new MockDicomElement("0018,0010", 1, Constants.DICOM_VR_LO, "cherry pitt")); // BolusAgent
			this.addDicomElement(new MockDicomElement("0018,0060", 1, Constants.DICOM_VR_DS, "5.67")); // KiloVoltPeak
			this.addDicomElement(new MockDicomElement("0018,1151", 1, Constants.DICOM_VR_IS, "320")); // XrayTubeCurrent
			double fl[]={1.234, 789.12, 2.3456};
			this.addDicomElement(new MockDicomElement("0020,0032", 3, Constants.DICOM_VR_FL, fl)); // ImagePosition 
			String sa1[]={"0.8743", "0.4362", "0.1", "0.24324e+2", "0.8653", "0.3426"};  
			this.addDicomElement(new MockDicomElement("0020,0037", 6, Constants.DICOM_VR_DS, sa1)); // ImageOrientation
			String sa2[]={"ARH", "PLF"};
			this.addDicomElement(new MockDicomElement("0020,0020", 2, Constants.DICOM_VR_CS, sa2)); // PatientOrientation
			this.addDicomElement(new MockDicomElement("0028,0004", 1, Constants.DICOM_VR_CS, "MONOCHROME1")); // PhotometricInterpretation
			this.addDicomElement(new MockDicomElement("0028,0010", 1, Constants.DICOM_VR_US, 512)); // Rows
			this.addDicomElement(new MockDicomElement("0028,0011", 1, Constants.DICOM_VR_US, 512)); // Columns
			String sa3[]={"1.34", "1.34"};
			this.addDicomElement(new MockDicomElement("0028,0030", 2, Constants.DICOM_VR_DS, sa3)); // PixelSpacing (mm)
			this.addDicomElement(new MockDicomElement("0028,0002", 1, Constants.DICOM_VR_US, 1)); // SamplesPerPixel
			this.addDicomElement(new MockDicomElement("0028,0100", 1, Constants.DICOM_VR_US, 10)); // BitsAllocated
			this.addDicomElement(new MockDicomElement("0028,0101", 1, Constants.DICOM_VR_US, 10)); // BitsStored
			this.addDicomElement(new MockDicomElement("0028,0101", 1, Constants.DICOM_VR_US, 9)); // HighBit
			this.addDicomElement(new MockDicomElement("0028,0103", 1, Constants.DICOM_VR_US, 0)); // PixelRepresentation (0 or 1)
			this.addDicomElement(new MockDicomElement("0018,1004", 1, Constants.DICOM_VR_LO, "20050430-078")); // PlateId
			this.addDicomElement(new MockDicomElement("0018,1403", 1, Constants.DICOM_VR_CS, "18CMX24CM")); // CasetteSize
			this.addDicomElement(new MockDicomElement("0018,1120", 1, Constants.DICOM_VR_DS, "70.45")); // GantryDetectorTilt (degree)
			this.addDicomElement(new MockDicomElement("0018,1130", 1, Constants.DICOM_VR_DS, "8.54")); // TableHeight (mm)
			this.addDicomElement(new MockDicomElement("0028,1052", 1, Constants.DICOM_VR_DS, "19.3")); // RescaleIntercept (mm)
			this.addDicomElement(new MockDicomElement("0028,1053", 1, Constants.DICOM_VR_DS, "1.1")); // RescaleSlope (mm)
			this.addDicomElement(new MockDicomElement("0018,0050", 1, Constants.DICOM_VR_DS, "3.4")); // SliceThickness (mm)
			this.addDicomElement(new MockDicomElement("0018,1100", 1, Constants.DICOM_VR_DS, "350")); // ReconstructionDiameter (mm)
			this.addDicomElement(new MockDicomElement("0018,0020", 1, Constants.DICOM_VR_CS, "SE/GR")); // ScanningSequence
			this.addDicomElement(new MockDicomElement("0018,0021", 1, Constants.DICOM_VR_CS, "NONE")); // ScanningVariant
			this.addDicomElement(new MockDicomElement("0018,0022", 1, Constants.DICOM_VR_CS, "PFF")); // ScanningOptions
			this.addDicomElement(new MockDicomElement("0018,0023", 1, Constants.DICOM_VR_CS, "2D")); // MrAcqType
			this.addDicomElement(new MockDicomElement("0018,0080", 1, Constants.DICOM_VR_DS, "37.24")); // RepetitionTime (ms)
			this.addDicomElement(new MockDicomElement("0018,0081", 1, Constants.DICOM_VR_DS, "6.47")); // EchoTime (ms)
			String sa4[]={"165.0", "97.4", "123.4", "511.12345"};
			this.addDicomElement(new MockDicomElement("0028,1050", 4 /*3*/, Constants.DICOM_VR_DS, sa4)); // WindowCenter
			String sa5[]={"125.54321", "91.12345", "100.4", "1.12345"};
			this.addDicomElement(new MockDicomElement("0028,1051", 4 /*3*/, Constants.DICOM_VR_DS, sa5)); // WindowWidth
		}
		else if (dsNum==2) {//ignore

		}
		else if (dsNum==3) {//ignore

		}
	}
}
