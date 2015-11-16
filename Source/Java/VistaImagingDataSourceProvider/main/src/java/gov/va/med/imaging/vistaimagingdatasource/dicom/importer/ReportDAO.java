/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Nov, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
  Description: DICOM Study cache manager. Maintains the cache of study instances
  			   and expires old studies after 15 minutes. 

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

package gov.va.med.imaging.vistaimagingdatasource.dicom.importer;

import java.util.HashMap;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.dicom.DicomServerConfiguration;
import gov.va.med.imaging.exchange.business.dicom.importer.ImporterWorkItem;
import gov.va.med.imaging.exchange.business.dicom.importer.ImporterWorkItemDetails;
import gov.va.med.imaging.exchange.business.dicom.importer.Reconciliation;
import gov.va.med.imaging.exchange.business.dicom.importer.Report;
import gov.va.med.imaging.exchange.business.dicom.importer.ReportParameters;
import gov.va.med.imaging.exchange.business.dicom.importer.Study;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

public class ReportDAO extends BaseImporterDAO<Study>
{
	private static String POST_MEDIA_BUNDLE_REPORT_DATA = "MAGV IMPORT MEDIA LOG STORE";
	private static String POST_STUDY_REPORT_DATA = "MAGV IMPORT STUDY LOG STORE";
	private static String GET_IMPORTER_REPORT = "MAGV IMPORT STUDY LOG REPORT";
	//
	// Constructor
	//
	public ReportDAO(){}
	public ReportDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	//
	// Post media bundle level report data
	//
	public int postImporterMediaBundleReportData(ImporterWorkItem workItem) throws MethodException, ConnectionException 
	{
		ImporterWorkItemDetails details = workItem.getWorkItemDetails();
		
		String mediaValidationStatusMessage = details.getMediaValidationMessage() != null ?
				                              details.getMediaValidationMessage() :
				                              "";
				                              
		VistaQuery vm = new VistaQuery(POST_MEDIA_BUNDLE_REPORT_DATA);
		vm.addParameter(VistaQuery.LITERAL, details.getReconcilingTechnicianDuz());
		vm.addParameter(VistaQuery.LITERAL, details.getWorkstationName());
		vm.addParameter(VistaQuery.LITERAL, workItem.getSubtype());
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(details.getMediaValidationStatusCode()));
		vm.addParameter(VistaQuery.LITERAL, mediaValidationStatusMessage);
		String returnValue = executeRPC(vm);
		
		return translatePostImporterMediaBundleReportData(returnValue);

	}


	public int translatePostImporterMediaBundleReportData(String returnValue) throws MethodException
	{
		int mediaReportDataIen = 0;
		String[] lines = StringUtils.Split(returnValue, StringUtils.NEW_LINE);
		String[] fields = StringUtils.Split(lines[0], StringUtils.BACKTICK);
		
		if (fields[0].equals("0"))
		{
			mediaReportDataIen = Integer.parseInt(fields[1]);
		}
		else
		{
			logger.error("Error storing bundle-level report data: " + returnValue);
		}
		
		return mediaReportDataIen;
	}

	
	//
	// Post study-level report data
	//
	public void postImporterStudyReportData(
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
	throws MethodException, ConnectionException 
	{
		ImporterWorkItemDetails details = importerWorkItem.getWorkItemDetails();
		
		VistaQuery vm = new VistaQuery(POST_STUDY_REPORT_DATA);
		vm.addParameter(VistaQuery.LITERAL, details.getReconcilingTechnicianDuz());
		vm.addParameter(VistaQuery.LITERAL, DicomServerConfiguration.getConfiguration().getSiteId());
		vm.addParameter(VistaQuery.LITERAL, patientDfn);
		vm.addParameter(VistaQuery.LITERAL, accessionNumber);
		vm.addParameter(VistaQuery.LITERAL, studyUid);
		vm.addParameter(VistaQuery.LITERAL, facility);
		vm.addParameter(VistaQuery.LITERAL, specialty);
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(numberOfSeries));
		vm.addParameter(VistaQuery.LIST, modalityCounts);
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(mediaGroupIen));
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(failedImages));

		String returnValue = executeRPC(vm);
		translatePostImporterStudyReportData(returnValue);
		
	}


	public void translatePostImporterStudyReportData(String returnValue) throws MethodException
	{
		// This should not prevent us from continuing with processing. Simply log it and move on
		// if there's an error
		if (!returnValue.startsWith("0"))
		{
			logger.error("Error storing study-level report data: " + returnValue);
		}
	}

	
	//
	// Get a report
	//
	public Report getImporterReport(ReportParameters reportParameters) throws MethodException, ConnectionException 
	{
		VistaQuery vm = new VistaQuery(GET_IMPORTER_REPORT);
		vm.addParameter(VistaQuery.LITERAL, reportParameters.getReportTypeCode());
		vm.addParameter(VistaQuery.LITERAL, reportParameters.getStartDate());
		vm.addParameter(VistaQuery.LITERAL, reportParameters.getEndDate());
		String returnValue = executeRPC(vm);
		return translateGetImporterReport(returnValue);
	}

	public Report translateGetImporterReport(String returnValue) throws MethodException
	{
		Report report = new Report();
		
		// Split the result into lines
		String[] lines = StringUtils.Split(returnValue, LINE_SEPARATOR);
		
		// If the report has data, build it. Otherwise, just return the return value
		String[] fields = StringUtils.Split(lines[0], StringUtils.BACKTICK);
		
		if (fields[0].equals("0") && lines.length == 1)
		{
			// fields[1] is the start date
			report.setReportText(fields[1]);
		}
		else if (fields[0].equals("0") && lines.length > 1)
		{
			// We have actual report data. Build it.
			StringBuilder reportText = new StringBuilder();
			
			for (int i = 1; i<lines.length; i++)
			{
				reportText.append(lines[i] + LINE_SEPARATOR);
			}

			report.setReportText(reportText.toString());

		}
		else
		{
			// Probably an error code of -1 meaning no data yet in reports file.
			report.setReportText(returnValue);
		}
		
		return report;

	}

	
}
