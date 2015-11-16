package gov.va.med.imaging.datasource;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.exchange.business.ApplicationTimeoutParameters;
import gov.va.med.imaging.exchange.business.WorkItemCounts;
import gov.va.med.imaging.exchange.business.dicom.OriginIndex;
import gov.va.med.imaging.exchange.business.dicom.importer.DiagnosticCode;
import gov.va.med.imaging.exchange.business.dicom.importer.ImagingLocation;
import gov.va.med.imaging.exchange.business.dicom.importer.ImporterWorkItem;
import gov.va.med.imaging.exchange.business.dicom.importer.Order;
import gov.va.med.imaging.exchange.business.dicom.importer.OrderFilter;
import gov.va.med.imaging.exchange.business.dicom.importer.OrderingLocation;
import gov.va.med.imaging.exchange.business.dicom.importer.OrderingProvider;
import gov.va.med.imaging.exchange.business.dicom.importer.Procedure;
import gov.va.med.imaging.exchange.business.dicom.importer.ProcedureModifier;
import gov.va.med.imaging.exchange.business.dicom.importer.Reconciliation;
import gov.va.med.imaging.exchange.business.dicom.importer.Report;
import gov.va.med.imaging.exchange.business.dicom.importer.ReportParameters;
import gov.va.med.imaging.exchange.business.dicom.importer.StandardReport;
import gov.va.med.imaging.exchange.business.dicom.importer.Study;

import java.util.HashMap;
import java.util.List;

/**
 * This class defines the Service Provider Interface (SPI) for the DicomImporterDataSource class. 
 * All the abstract methods in this class must be implemented by each 
 * data source service provider who wishes to supply the implementation of a 
 * DicomDataSource for a particular datasource type.
 * 
 * @since 1.0
 * @author vhaiswlouthj
 *
 */
@SPI(description="The service provider interface for DICOM storage")
public interface DicomImporterDataSourceSpi
extends VersionableDataSourceSpi
{

	// Study-related methods
	Study getStudyImportStatus(Study study) throws MethodException, ConnectionException;
	List<OriginIndex> getOriginIndexList(RoutingToken routingToken) throws MethodException, ConnectionException;

	// Order-related methods
	List<Order> getOrderListForPatient(OrderFilter orderFilter) throws MethodException, ConnectionException;
	List<OrderingProvider> getOrderingProviderList(String siteId, String searchString) throws MethodException, ConnectionException;
	List<DiagnosticCode> getDiagnosticCodeList(String siteId) throws MethodException, ConnectionException;
	List<StandardReport> getStandardReportList(String siteId) throws MethodException, ConnectionException;
	List<OrderingLocation> getOrderingLocationList(String siteId) throws MethodException, ConnectionException;
	List<ImagingLocation> getImagingLocationList(String siteId) throws MethodException, ConnectionException;
	List<Procedure> getProcedureList(String siteId, String imagingLocationIen, String procedureIen) throws MethodException, ConnectionException;
	List<ProcedureModifier> getProcedureModifierList(String siteId) throws MethodException, ConnectionException;

	// Order-creation-related methods
	Order createRadiologyOrder(Reconciliation reconciliation) throws MethodException, ConnectionException;
	Order registerOrder(Reconciliation reconciliation, int hospitalLocationId) throws MethodException, ConnectionException;
	Order setOrderExamined(Reconciliation reconciliation, String technicianDuz, String placeId) throws MethodException, ConnectionException;
	Order setOrderExamComplete(Reconciliation reconciliation, String technicianDuz, String placeId) throws MethodException, ConnectionException;
	
	// Report-related methods
	Report getImporterReport(ReportParameters reportParameters) throws MethodException, ConnectionException;
	int postImporterMediaBundleReportData(ImporterWorkItem importerWorkItem) throws MethodException, ConnectionException;
	void postImporterStudyReportData(
			int mediaGroupIen, 
			ImporterWorkItem importerWorkItem,
			String accessionNumber,
			String studyUid,
			String patientDfn,
			String facility,
			String specialty,
			int numberOfSeries,
			int totalImagesInStudy,
			int failedImages,
			HashMap<String, String> modalityCounts)
	throws MethodException, ConnectionException;


	/**
	 * Gets a list of counts by subtype and status for the given work item type
	 * @param workItemType
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract WorkItemCounts getWorkItemCounts()
	throws MethodException, ConnectionException;


}
