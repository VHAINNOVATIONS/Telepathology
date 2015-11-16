/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: March, 2011
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
package gov.va.med.imaging.vistaimagingdatasource.worklist;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.AbstractVersionableDataSource;
import gov.va.med.imaging.datasource.WorkListDataSourceSpi;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.WorkItem;
import gov.va.med.imaging.exchange.business.WorkItemFilter;
import gov.va.med.imaging.exchange.business.WorkItemTag;
import gov.va.med.imaging.exchange.business.dicom.StorageCommitWorkItem;
import gov.va.med.imaging.exchange.enums.ImagingSecurityContextType;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.io.IOException;
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
public class VistaImagingWorkListDataSourceService 
extends AbstractVersionableDataSource 
implements WorkListDataSourceSpi, VistaSessionFactory
{
	// The required version of VistA Imaging needed to execute the RPC calls for
	// this operation
	
	public final static String MAG_REQUIRED_VERSION = "3.0P118";
	public final static int DEFAULT_PATIENT_SENSITIVITY_LEVEL = 2;
	public final static String SUPPORTED_PROTOCOL = "vistaimaging";
	
	private final static Logger logger = Logger.getLogger(VistaImagingWorkListDataSourceService.class);

//	private Logger logger = Logger.getLogger(this.getClass());
	
	/*
	 * =====================================================================
	 * Instance fields and methods
	 * =====================================================================
	 */
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingWorkListDataSourceService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
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
		TransactionContext transactionContext = TransactionContextFactory.get();
		String imagingSecurityContextString = transactionContext.getImagingSecurityContextType();
		if((imagingSecurityContextString == null) || (imagingSecurityContextString.length() == 0))
		{
			logger.debug("null Imaging Security Context in transaction context, using default '" + ImagingSecurityContextType.DICOM_QR_CONTEXT.name() + "'.");
			transactionContext.setImagingSecurityContextType(ImagingSecurityContextType.DICOM_QR_CONTEXT.name());
		}
			
		return VistaSession.getOrCreate(getMetadataUrl(), getSite());
	}
	

	@Override
	public boolean isVersionCompatible()
	{
		return true;
	}
	
	@Override
	public WorkItem createWorkItem(WorkItem workItem) throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("createWorkItem", getDataSourceVersion());
		WorkItemDAO dao = new WorkItemDAO(this);
		return dao.create(workItem);
	}
	
	@Override
	public boolean deleteWorkItem(int id) throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("deleteWorkItem", getDataSourceVersion());
		WorkItemDAO dao = new WorkItemDAO(this);
		dao.delete(id);
		return true;
	}
	
	@Override
	public WorkItem getAndTransitionWorkItem(int id,
			String expectedStatus, String newStatus, String updatingUser, String updatingApplication)
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getAndTransitionWorkItem", getDataSourceVersion());
		WorkItemDAO dao = new WorkItemDAO(this);
		return dao.getAndTransitionWorkItem(id, expectedStatus, newStatus, updatingUser, updatingApplication);
	}
	
	@Override
	public WorkItem getAndTransitionNextWorkItem(String type,
			String expectedStatus, String newStatus, String updatingUser, String updatingApplication, String placeId)
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getAndTransitionNextWorkItem", getDataSourceVersion());
		WorkItemDAO dao = new WorkItemDAO(this);
		return dao.getAndTransitionNextWorkItem(type, expectedStatus, newStatus, updatingUser, updatingApplication, placeId);
	}
	
	@Override
	public List<WorkItem> getWorkItemList(WorkItemFilter filter) throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getWorkItemList", getDataSourceVersion());
		WorkItemDAO dao = new WorkItemDAO(this);
		return dao.findByCriteria(filter);
	}
	
	@Override
	public boolean postWorkItemTags(
			int workItemId,
			List<String> allowedStatuses, 
			List<WorkItemTag> newTags, 
			String updatingUser, 
			String updatingApplication)
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("postWorkItemTags", getDataSourceVersion());
		WorkItemDAO dao = new WorkItemDAO(this);
		return dao.addTagsToExistingWorkItem(workItemId, allowedStatuses, newTags, updatingUser, updatingApplication);
	}
	
	@Override
	public boolean updateWorkItem(int workItemId, String expectedStatus, String newStatus, String newMessage, String updatingUser, String updatingApplication)
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("updateWorkItem", getDataSourceVersion());
		WorkItemDAO dao = new WorkItemDAO(this);
		return dao.updateWorkItem(workItemId, expectedStatus, newStatus, newMessage, updatingUser, updatingApplication);
	}
	

	// .................................. Storage Commit Work Item methods ............................................

	@Override
	public StorageCommitWorkItem createSCWorkItem(StorageCommitWorkItem scWI) throws MethodException, ConnectionException 
	{
		SCWorkItemDAO dao = new SCWorkItemDAO(this);
		return dao.create(scWI);
	}
	
	@Override
	public  List<StorageCommitWorkItem> listSCWorkItems(String hostname) throws MethodException, ConnectionException 
	{
		SCWorkItemDAO dao = new SCWorkItemDAO(this);
		return dao.findByCriteria(hostname);
	}

	@Override
	public StorageCommitWorkItem getSCWorkItem(String scWIID, boolean doProcess) throws MethodException, ConnectionException 
	{
		SCWorkItemDAO dao = new SCWorkItemDAO(this);
		StorageCommitWorkItem scWI = new StorageCommitWorkItem();
		scWI.setId(Integer.parseInt(scWIID));
		scWI.setDoProcess(doProcess);
		return dao.getEntityByExample(scWI);
	}

	@Override
	public boolean updateSCWorkItemStatus(String scWIID, String status) throws MethodException, ConnectionException 
	{
		SCWorkItemDAO dao = new SCWorkItemDAO(this);
		StorageCommitWorkItem scWI = new StorageCommitWorkItem();
		scWI.setId(Integer.parseInt(scWIID));
		scWI.setStatus(status);
		scWI = dao.update(scWI);
		return true; 
	}

	@Override
	public boolean deleteSCWorkItem(String scWIID) throws MethodException, ConnectionException 
	{
		SCWorkItemDAO dao = new SCWorkItemDAO(this);
		dao.delete(Integer.parseInt(scWIID));
		return true;
	}

//	..................................................................................................................

	private String getDataSourceVersion()
	{
		return "1";
	}
}
