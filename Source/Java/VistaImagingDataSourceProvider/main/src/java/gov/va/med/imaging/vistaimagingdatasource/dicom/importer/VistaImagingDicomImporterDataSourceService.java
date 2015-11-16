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

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityException;
import gov.va.med.imaging.datasource.AbstractVersionableDataSource;
import gov.va.med.imaging.datasource.DicomImporterDataSourceSpi;
import gov.va.med.imaging.exchange.business.ApplicationTimeoutParameters;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
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
import gov.va.med.imaging.exchange.business.dicom.importer.Series;
import gov.va.med.imaging.exchange.business.dicom.importer.SopInstance;
import gov.va.med.imaging.exchange.business.dicom.importer.StandardReport;
import gov.va.med.imaging.exchange.business.dicom.importer.Study;
import gov.va.med.imaging.exchange.enums.ImagingSecurityContextType;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.AbstractVistaImagingDataSourceService;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingDataSourceProvider;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;
import gov.va.med.imaging.vistaimagingdatasource.worklist.WorkItemDAO;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * An implementation of a DicomStorageDataSourceSpi that talks to VistA.
 * 
 * NOTE: 1.) public methods that do Vista access (particularly anything defined
 * in the DicomDataSourceSpi interface) must acquire a VistaSession instance
 * using getVistaSession(). 2.) private methods which are only called from
 * public methods that do Vista access must include a VistaSession parameter,
 * they should not acquire their own VistaSession 3.) Where a method is both
 * public and called from within this class, there should be a public version
 * following rule 1, calling a private version following rule 2.
 * 
 * @author vhaiswlouthj
 * 
 */
public class VistaImagingDicomImporterDataSourceService 
extends AbstractVistaImagingDataSourceService 
implements DicomImporterDataSourceSpi, VistaSessionFactory
{
	// The required version of VistA Imaging needed to execute the RPC calls for
	// this operation
	
	public final static String MAG_REQUIRED_VERSION = "3.0P118";
	public final static int DEFAULT_PATIENT_SENSITIVITY_LEVEL = 2;
	public final static String SUPPORTED_PROTOCOL = "vistaimaging";

	
	/*
	 * =====================================================================
	 * Instance fields and methods
	 * =====================================================================
	 */
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingDicomImporterDataSourceService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
	}
	
	// to support local data source
	public VistaImagingDicomImporterDataSourceService(ResolvedArtifactSource resolvedArtifactSource)
	{
		super(resolvedArtifactSource, SUPPORTED_PROTOCOL);
	}
	/**
	 * The artifact source must be checked in the constructor to assure that it is an instance
	 * of ResolvedSite.
	 * 
	 * @return
	 */
	protected ResolvedSite getResolvedSite()
	{
		return (ResolvedSite)getResolvedArtifactSource();
	}
	
	protected Site getSite()
	{
		return getResolvedSite().getSite();
	}


	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistaimagingdatasource.dicom.storage.SessionFactory#getVistaSession()
	 */
	public VistaSession getVistaSession() 
	throws IOException, ConnectionException, MethodException
	{
		TransactionContextFactory.get().setImagingSecurityContextType(ImagingSecurityContextType.DICOM_QR_CONTEXT.name());
		return VistaSession.getOrCreate(getMetadataUrl(), getSite());
	}

	@Override
	public boolean isVersionCompatible() throws SecurityException
	{
		String version = VistaImagingCommonUtilities.getVistaDataSourceImagingVersion(
				getVistaImagingConfiguration(), this.getClass(), 
				MAG_REQUIRED_VERSION);
		logger.info("isVersionCompatible searching for version [" + version + "], TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try
		{			
			localVistaSession = getVistaSession();			
			return VistaImagingCommonUtilities.isVersionCompatible(version, localVistaSession);						
		}
		catch(SecurityCredentialsExpiredException sceX)
		{
			// caught here to be sure it gets thrown as SecurityCredentialsExpiredException, not ConnectionException
			throw sceX;
		}
		catch(MethodException mX)
		{
			logger.error("There was an error finding the installed Imaging version from VistA", mX);
			TransactionContextFactory.get().addDebugInformation("isVersionCompatible() failed, " + (mX == null ? "<null error>" : mX.getMessage()));
		}
		catch(ConnectionException cX)
		{
			logger.error("There was an error finding the installed Imaging version from VistA", cX);
			TransactionContextFactory.get().addDebugInformation("isVersionCompatible() failed, " + (cX == null ? "<null error>" : cX.getMessage()));
		}		
		catch(IOException ioX)
		{
			logger.error("There was an error finding the installed Imaging version from VistA", ioX);
			TransactionContextFactory.get().addDebugInformation("isVersionCompatible() failed, " + (ioX == null ? "<null error>" : ioX.getMessage()));
		}
		finally
		{
			try{localVistaSession.close();}
			catch(Throwable t){}
		}
		return false;
	}
	
	@Override
	public List<Order> getOrderListForPatient(OrderFilter orderFilter)
	throws MethodException, ConnectionException 
	{
		// If the orderFilter specifies a known specialty, just get the orders for 
		// that specialty. Otherwise, get everything
		String orderType = orderFilter.getOrderType() + "";
		if (orderType.trim().equals("RAD") || orderType.trim().equals("CON") )
		{
			return getOrderSubset(orderFilter);
		}
		else
		{
			return getAllOrders(orderFilter);
		}
	}
	
	private List<Order> getOrderSubset(OrderFilter orderFilter) 
	throws MethodException, ConnectionException
	{
		OrderDAO dao = new OrderDAO(this);
		List<Order> orders = dao.findByCriteria(orderFilter);
		return orders;
	}

	private List<Order> getAllOrders(OrderFilter orderFilter) 
	throws MethodException, ConnectionException
	{
		OrderDAO dao = new OrderDAO(this);

		// First get the RAD orders
		orderFilter.setOrderType("RAD");
		List<Order> orders = dao.findByCriteria(orderFilter);

		// Add the CON orders
		dao = new OrderDAO(this);
		orderFilter.setOrderType("CON");
		orders.addAll(dao.findByCriteria(orderFilter));
		
		// Add the LAB orders
		dao = new OrderDAO(this);
		orderFilter.setOrderType("LAB");
		orders.addAll(dao.findByCriteria(orderFilter));
		
		return orders;
	}

	@Override
	public List<OrderingLocation> getOrderingLocationList(String siteId)
	throws MethodException, ConnectionException 
	{
		OrderingLocationDAO dao = new OrderingLocationDAO(this);
		return dao.findAll();
	}

	@Override
	public List<ImagingLocation> getImagingLocationList(String siteId)
	throws MethodException, ConnectionException 
	{
		ImagingLocationDAO dao = new ImagingLocationDAO(this);
		return dao.findAll();
	}

	@Override
	public List<OrderingProvider> getOrderingProviderList(String siteId, String searchString)
	throws MethodException, ConnectionException 
	{
		OrderingProviderDAO dao = new OrderingProviderDAO(this);
		return dao.findProviders(searchString);
	}

	@Override
	public List<Procedure> getProcedureList(String siteId, String imagingLocationIen, String procedureIen) 
	throws MethodException, ConnectionException 
	{
		ProcedureDAO dao = new ProcedureDAO(this);
		return dao.findProceduresForDivision(siteId, imagingLocationIen, procedureIen);
	}
	
	@Override
	public List<ProcedureModifier> getProcedureModifierList(String siteId) 
	throws MethodException, ConnectionException 
	{
		ProcedureModifierDAO pmDao = new ProcedureModifierDAO(this);
		return pmDao.findAll();
	}
	
	@Override
	public Study getStudyImportStatus(Study study) 
	throws MethodException, ConnectionException 
	{
		StudyDAO dao = new StudyDAO(this);
		study = dao.getImportStatus(study);
		return getPreviousReconciliationDetails(study);
		
	}
	private Study getPreviousReconciliationDetails(Study study) 
	throws MethodException, ConnectionException 
	{
		// See if any of the images have already been imported. 
		// If so, take the UIDs of the first image found and get the previous reconciliation details
		boolean foundImportedImage = false;
		String studyUid = study.getUid();
		String seriesUid = "";
		String instanceUid = "";
		
		for (Series series : study.getSeries())
		{
			// If we've already found an imported image, break out of this loop, too
			if (foundImportedImage) break;
			
			for (SopInstance instance : series.getSopInstances())
			{
				if (instance.isImportedSuccessfully())
				{
					seriesUid = series.getUid();
					instanceUid = instance.getUid();
					foundImportedImage = true;
					break;
				}
			}
		}
		
		// If we have found a previously imported image, go get the details of what it was reconciled to, 
		// and attach the info to the study
		if (foundImportedImage)
		{
			StudyDAO dao = new StudyDAO(this);
			study = dao.getPreviousReconciliationDetails(study, studyUid, seriesUid, instanceUid);
		}
		return study;
	}

	@Override
	public Report getImporterReport(ReportParameters reportParameters)
	throws MethodException, ConnectionException 
	{
		ReportDAO dao = new ReportDAO(this);
		return dao.getImporterReport(reportParameters);
	}
	
	@Override
	public int postImporterMediaBundleReportData(ImporterWorkItem importerWorkItem)
	throws MethodException, ConnectionException 
	{
		ReportDAO dao = new ReportDAO(this);
		return dao.postImporterMediaBundleReportData(importerWorkItem);
	}

	
	@Override
	public void postImporterStudyReportData(int mediaGroupIen, 
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
		ReportDAO dao = new ReportDAO(this);
		dao.postImporterStudyReportData(
				mediaGroupIen, 
				importerWorkItem,
				accessionNumber,
				studyUid,
				patientDfn,
				facility,
				specialty,
				numberOfSeries,
				totalImagesInStudy,
				failedImages,
				modalityCounts);
	}

	@Override
	public Order createRadiologyOrder(Reconciliation reconciliation) 
	throws MethodException, ConnectionException 
	{
		OrderDAO dao = new OrderDAO(this);
		return dao.createRadiologyOrder(reconciliation);
	}

	@Override
	public Order registerOrder(Reconciliation reconciliation, int hospitalLocationId) 
	throws MethodException, ConnectionException 
	{
		OrderDAO dao = new OrderDAO(this);
		return dao.registerOrder(reconciliation, hospitalLocationId);
	}

	@Override
	public Order setOrderExamined(Reconciliation reconciliation, String technicianDuz, String placeId) 
	throws MethodException, ConnectionException 
	{
		OrderDAO dao = new OrderDAO(this);
		return dao.setOrderExamined(reconciliation, technicianDuz, placeId);
	}

	@Override
	public Order setOrderExamComplete(Reconciliation reconciliation, String technicianDuz, String placeId) 
	throws MethodException, ConnectionException 
	{
		OrderDAO dao = new OrderDAO(this);
		return dao.setOrderExamComplete(reconciliation, technicianDuz, placeId);
	}

	@Override
	public List<OriginIndex> getOriginIndexList(RoutingToken routingToken) 
	throws MethodException,ConnectionException 
	{
		OriginIndexDAO dao = new OriginIndexDAO(this);
		return dao.findAll();
	}
	
	@Override
	public WorkItemCounts getWorkItemCounts()
	throws MethodException, ConnectionException 
	{
		WorkItemDAO dao = new WorkItemDAO(this);
		return dao.getWorkItemCounts("IMPORTER");
	}

	@Override
	public List<DiagnosticCode> getDiagnosticCodeList(String siteId)
	throws MethodException, ConnectionException 
	{
		DiagnosticCodeDAO dao = new DiagnosticCodeDAO(this);
		return dao.findDiagnosticCodesForDivision(siteId);
	}

	@Override
	public List<StandardReport> getStandardReportList(String siteId)
	throws MethodException, ConnectionException 
	{
		StandardReportDAO dao = new StandardReportDAO(this);
		return dao.findStandardReportsForDivision(siteId);
	}

}