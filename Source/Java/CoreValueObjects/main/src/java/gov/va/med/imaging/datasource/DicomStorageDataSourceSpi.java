package gov.va.med.imaging.datasource;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.ParentREFDeletedMethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.exchange.business.dicom.DGWEmailInfo;
import gov.va.med.imaging.exchange.business.dicom.DicomCorrectInfo;
import gov.va.med.imaging.exchange.business.dicom.DicomUid;
import gov.va.med.imaging.exchange.business.dicom.InstanceFile;
import gov.va.med.imaging.exchange.business.dicom.InstrumentConfig;
import gov.va.med.imaging.exchange.business.dicom.ModalityConfig;
import gov.va.med.imaging.exchange.business.dicom.PatientRef;
import gov.va.med.imaging.exchange.business.dicom.PatientStudyInfo;
import gov.va.med.imaging.exchange.business.dicom.PatientStudyLookupResults;
import gov.va.med.imaging.exchange.business.dicom.ProcedureRef;
import gov.va.med.imaging.exchange.business.dicom.SOPInstance;
import gov.va.med.imaging.exchange.business.dicom.Series;
import gov.va.med.imaging.exchange.business.dicom.Study;
import gov.va.med.imaging.exchange.business.dicom.UIDActionConfig;
import gov.va.med.imaging.exchange.business.dicom.UIDCheckInfo;
import gov.va.med.imaging.exchange.business.dicom.UIDCheckResult;
import gov.va.med.imaging.exchange.business.dicom.rdsr.Dose;

import java.util.List;

/**
 * This class defines the Service Provider Interface (SPI) for the DicomDataSource class. 
 * All the abstract methods in this class must be implemented by each 
 * data source service provider who wishes to supply the implementation of a 
 * DicomDataSource for a particular datasource type.
 * 
 * @since 1.0
 * @author vhaiswlouthj
 *
 */
@SPI(description="The service provider interface for DICOM storage")
public interface DicomStorageDataSourceSpi
extends VersionableDataSourceSpi
{

	// Configuration data retrieval
	List<InstrumentConfig> getDgwInstrumentList(String hostName) throws MethodException, ConnectionException;
	List<ModalityConfig> getDgwModalityList(String hostName) throws MethodException, ConnectionException;
//	List<SourceAESecurityConfig> getSourceAESecurityMatrix() throws MethodException, ConnectionException;
	DGWEmailInfo getDgwEmailInfo(String hostName) throws MethodException, ConnectionException;
	List<UIDActionConfig> getDgwUIDActionTable(String type, String subType, String action) throws MethodException, ConnectionException;
	
	// Patient/Study lookup
	PatientStudyLookupResults getPatientStudyLookupResults(PatientStudyInfo patientStudyInfo) throws MethodException, ConnectionException;

	// UID checking methods
    UIDCheckResult getStudyUIDCheckResult(UIDCheckInfo uidCheckInfo) throws MethodException, ConnectionException;
    UIDCheckResult getSeriesUIDCheckResult(UIDCheckInfo uidCheckInfo) throws MethodException, ConnectionException;
    UIDCheckResult getSOPInstanceUIDCheckResult(UIDCheckInfo uidCheckInfo) throws MethodException, ConnectionException;

    // UID Generation methods
    DicomUid getDicomUid(String accessionNumber, String siteId, String instrument, String type)throws MethodException, ConnectionException;
	
    // Object storage and retrieval methods
    PatientRef getOrCreatePatientRef(PatientRef patientRef) throws MethodException, ConnectionException;
	ProcedureRef getOrCreateProcedureRef(PatientRef patientRef, ProcedureRef procedureRef) throws MethodException, ConnectionException;
	Study getOrCreateStudy(PatientRef patientRef, ProcedureRef procedureRef, Study study) throws MethodException, ConnectionException;
	Series getOrCreateSeries(Study study, Series series, Integer iodValidationStatus) throws MethodException, ConnectionException, ParentREFDeletedMethodException;
	SOPInstance createSOPInstance(Series series, SOPInstance sopInstance) throws MethodException, ConnectionException, ParentREFDeletedMethodException;
	InstanceFile createInstanceFile(SOPInstance sopInstance, InstanceFile instanceFile) throws MethodException, ConnectionException;
	Series getTIUPointer(Series series) throws MethodException, ConnectionException;
	Boolean deleteStudyAndSeriesCache() throws MethodException;	

    // DICOM Correct
//	DicomCorrectInfo postDicomCorrectEntry(DicomCorrectInfo dicomCorrectInfo) throws MethodException, ConnectionException;
//    List<DicomCorrectEntry> getDicomCorrectEntryList(DicomCorrectEntry dicomCorrectEntry) throws MethodException, ConnectionException;
//    Boolean deleteDicomCorrectEntry(DicomCorrectEntry dicomCorrectEntry) throws MethodException, ConnectionException;
	Integer getDicomCorrectCount(DicomCorrectInfo dicomCorrectInfo) throws MethodException, ConnectionException;
	
    // Radiation Dose Structured Report Processing
	Dose createRadiationDose(PatientRef patient, ProcedureRef procedure, Study study, Series series, Dose dose)
	throws MethodException, ConnectionException;
	
	List<Dose> getRadiationDoseDetails(String patientDfn, String accessionNumber) throws MethodException, ConnectionException;


}
