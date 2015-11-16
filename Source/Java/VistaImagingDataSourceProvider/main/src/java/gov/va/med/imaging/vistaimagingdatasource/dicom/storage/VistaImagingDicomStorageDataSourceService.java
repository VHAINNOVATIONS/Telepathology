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
package gov.va.med.imaging.vistaimagingdatasource.dicom.storage;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.ParentREFDeletedMethodException;
import gov.va.med.imaging.datasource.AbstractVersionableDataSource;
import gov.va.med.imaging.datasource.DicomStorageDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
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
import gov.va.med.imaging.exchange.enums.ImagingSecurityContextType;
import gov.va.med.imaging.protocol.vista.VistaImagingDicomTranslator;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingDicomQueryFactory;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.io.IOException;
import java.util.ArrayList;
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
public class VistaImagingDicomStorageDataSourceService 
extends AbstractVersionableDataSource 
implements DicomStorageDataSourceSpi, VistaSessionFactory
{
	// The required version of VistA Imaging needed to execute the RPC calls for
	// this operation
	
	// TODO CHANGE TO 3.0P34
	public final static String MAG_REQUIRED_VERSION = "3.0P83";
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
	public VistaImagingDicomStorageDataSourceService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
	}
	
	// to support local data source
	public VistaImagingDicomStorageDataSourceService(ResolvedArtifactSource resolvedArtifactSource)
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
	public VistaSession getVistaSession() throws IOException, ConnectionException, MethodException
	{
		TransactionContextFactory.get().setImagingSecurityContextType(ImagingSecurityContextType.DICOM_QR_CONTEXT.name());
		return VistaSession.getOrCreate(getMetadataUrl(), getSite());
	}

	public List<InstrumentConfig> getDgwInstrumentList(String hostName) throws MethodException, ConnectionException
	{
		logger.info("Executing getDgwInstrumentList in the VistA implementation of the DicomStorageDataSourceSpi");
		VistaSession session = null;
		List<InstrumentConfig> instruments = new ArrayList<InstrumentConfig>();

		try
		{
			session = getVistaSession();
			
			VistaQuery vm = VistaImagingDicomQueryFactory.createGetDgwInstrumentListQuery(hostName);
			String returnValue = getVistaSession().call(vm);
			logger.info(vm.getRpcName()+" RPC: "+StringUtils.displayEncodedChars(returnValue));
			instruments = VistaImagingDicomTranslator.translateInstrumentList(returnValue);
		} 
		catch (IOException e)
        {
			throw new ConnectionException(e);
        }
    	catch (VistaMethodException e)
        {
    		throw new MethodException(e.getMessage());
        } 
    	catch (InvalidVistaCredentialsException e)
        {
    		throw new InvalidCredentialsException(e.getMessage());
        }
		finally
		{
			try{session.close();}
			catch(Throwable t){}
		}
		
		return instruments;
		
	}	
	
	public List<ModalityConfig> getDgwModalityList(String hostName) throws MethodException, ConnectionException
	{
		logger.info("Executing getDgwModalityList in the VistA implementation of the DicomStorageDataSourceSpi");
		VistaSession session = null;
		List<ModalityConfig> modalities = new ArrayList<ModalityConfig>();

		try
		{
			session = getVistaSession();
			
			VistaQuery vm = VistaImagingDicomQueryFactory.createGetDgwModalityListQuery(hostName);
			String returnValue = session.call(vm);
			logger.info(vm.getRpcName()+" RPC: "+StringUtils.displayEncodedChars(returnValue));			
			modalities = VistaImagingDicomTranslator.translateModalityList(returnValue);
		} 
		catch (IOException e)
        {
			throw new ConnectionException(e);
        }
    	catch (VistaMethodException e)
        {
    		throw new MethodException(e.getMessage());
        } 
    	catch (InvalidVistaCredentialsException e)
        {
    		throw new InvalidCredentialsException(e.getMessage());
        }
		finally
		{
			try{session.close();}
			catch(Throwable t){}
		}
		
		return modalities;

	}	
	
	
	public DGWEmailInfo getDgwEmailInfo(String hostName) throws MethodException, ConnectionException
	{
		logger.info("Executing getDgwEmailInfo in the VistA implementation of the DicomStorageDataSourceSpi");
		VistaSession session = null;
		DGWEmailInfo dgwEMI=null;

		try
		{
			session = getVistaSession();
			
			VistaQuery vm = VistaImagingDicomQueryFactory.createGetDGWEmailInfo(hostName);
			String returnValue = session.call(vm);
			logger.info(vm.getRpcName()+" RPC: "+StringUtils.displayEncodedChars(returnValue));
			dgwEMI = VistaImagingDicomTranslator.translateDGWEmailInfo(returnValue);
		} 
		catch (IOException e)
        {
			throw new ConnectionException(e);
        }
    	catch (VistaMethodException e)
        {
    		throw new MethodException(e.getMessage());
        } 
    	catch (InvalidVistaCredentialsException e)
        {
    		throw new InvalidCredentialsException(e.getMessage());
        }
		finally
		{
			try{session.close();}
			catch(Throwable t){}
		}
		
		return dgwEMI;

	}	
	
	public List<UIDActionConfig> getDgwUIDActionTable(String type, String subType, String action) throws MethodException, ConnectionException
	{
		
		logger.info("Executing getDgwUIDActionTable in the VistA implementation of the DicomStorageDataSourceSpi");
		VistaSession session = null;
		List<UIDActionConfig> uidActions = new ArrayList<UIDActionConfig>();

		try
		{
			session = getVistaSession();
			
			VistaQuery vm = VistaImagingDicomQueryFactory.createGetDgwUIDActionTableQuery(type, subType, action);
			String returnValue = session.call(vm);
			logger.info(vm.getRpcName()+" RPC: "+StringUtils.displayEncodedChars(returnValue));
			uidActions = VistaImagingDicomTranslator.translateUIDActions(returnValue);
		} 
		catch (IOException e)
        {
			throw new ConnectionException(e);
        }
    	catch (VistaMethodException e)
        {
    		throw new MethodException(e.getMessage());
        } 
    	catch (InvalidVistaCredentialsException e)
        {
    		throw new InvalidCredentialsException(e.getMessage());
        }
		finally
		{
			try{session.close();}
			catch(Throwable t){}
		}
		
		return uidActions;

	}
	
	public PatientStudyLookupResults getPatientStudyLookupResults(PatientStudyInfo patientStudyInfo) throws MethodException, ConnectionException
	{
		PatientStudyLookupResultsDAO dao = new PatientStudyLookupResultsDAO(this);
		return dao.getEntityByCriteria(patientStudyInfo);
	}
	
	@Override
	public UIDCheckResult getStudyUIDCheckResult(UIDCheckInfo uidCheckInfo) throws MethodException, ConnectionException
	{
		StudyUIDCheckResultDAO retriever = new StudyUIDCheckResultDAO(this);
		return retriever.getEntityByCriteria(uidCheckInfo);
	}

	@Override
	public UIDCheckResult getSeriesUIDCheckResult(UIDCheckInfo uidCheckInfo) throws MethodException, ConnectionException
	{
		SeriesUIDCheckResultDAO retriever = new SeriesUIDCheckResultDAO(this);
		return retriever.getEntityByCriteria(uidCheckInfo);
	}

	@Override
	public UIDCheckResult getSOPInstanceUIDCheckResult(UIDCheckInfo uidCheckInfo) throws MethodException, ConnectionException
	{
		SOPInstanceUIDCheckResultDAO retriever = new SOPInstanceUIDCheckResultDAO(this);
		return retriever.getEntityByCriteria(uidCheckInfo);
	}
	
	@Override
	public PatientRef getOrCreatePatientRef(PatientRef patientRef) throws MethodException, ConnectionException
	{
		PatientRefDAO dao = new PatientRefDAO(this);
		PatientRef persistentRef = dao.getEntityByExample(patientRef);
		
		if (persistentRef == null)
		{
			persistentRef = dao.create(patientRef);		
		}
		return persistentRef;
	}
	
	@Override
	public ProcedureRef getOrCreateProcedureRef(PatientRef patientRef, ProcedureRef procedureRef) throws MethodException, ConnectionException
	{
		ProcedureRefDAO dao = new ProcedureRefDAO(this);
		// Set foreign key to patientRef
		logger.debug(this.getClass().getName()+":Set Patient REF IEN.");
		procedureRef.setPatientRefIEN(patientRef.getIEN());
		logger.debug(this.getClass().getName()+": Get Procedure REF if exist.");
		ProcedureRef persistentRef = dao.getEntityByExample(procedureRef);
		
		if (persistentRef == null)
		{
			// store the procedureRef
			logger.debug(this.getClass().getName()+": Create Procedure REF.");
			persistentRef = dao.create(procedureRef);		
		}
		return persistentRef;
	}
	
	@Override
	public Study getOrCreateStudy(PatientRef patientRef, ProcedureRef procedureRef, Study study) 
						throws MethodException, ConnectionException
	{
		StudyDAO dao = new StudyDAO(this);
		// Set foreign key to patientRef and procedure Ref
		study.setPatientRefIEN(patientRef.getIEN());
		study.setProcedureRefIEN(procedureRef.getIEN());
		Study persistentRef = dao.getEntityByExample(study);
		
		if (persistentRef == null)
		{
			// store the study
			persistentRef = dao.create(study);		
		}
		return persistentRef;
	}
	
	@Override
	public Series getOrCreateSeries(Study study, Series series, Integer iodValidationStatus) 
						throws MethodException, ConnectionException, ParentREFDeletedMethodException
	{
		SeriesDAO dao = new SeriesDAO(this);
		// Set foreign key to study,
		series.setStudyIEN(study.getIEN());
		Series persistentRef = dao.getEntityByExample(series);
		if(persistentRef != null){
			if(!persistentRef.getStudyIEN().equals(series.getStudyIEN())){
				persistentRef = null;
			}
		}
		if (persistentRef == null)
		{
			// Persist the series
			persistentRef = dao.create(series);		
		} else {
			if (Integer.decode(persistentRef.getIODViolationDetected())<iodValidationStatus.intValue()) { // register worse status only 0 -> 3
				persistentRef.setIODViolationDetected(iodValidationStatus.toString());
				dao.update(persistentRef);
			}
		}
		return persistentRef;
	}
	
	@Override
	public SOPInstance createSOPInstance(Series series, SOPInstance sopInstance) 
						throws MethodException, ConnectionException, ParentREFDeletedMethodException
	{
		// Set foreign key to study, then persist the series
		sopInstance.setSeriesIEN(series.getIEN());
		SOPInstanceDAO dao = new SOPInstanceDAO(this);
		sopInstance = dao.create(sopInstance);		
		return sopInstance;
	}
	
	@Override
	public InstanceFile createInstanceFile(SOPInstance sopInstance, InstanceFile instanceFile) throws MethodException, ConnectionException
	{
		// Set foreign key to study, then persist the series
		instanceFile.setSOPInstanceIEN(sopInstance.getIEN());
		InstanceFileDAO dao = new InstanceFileDAO(this);
		instanceFile = dao.create(instanceFile);		
		return instanceFile;
	}
	
	@Override
	public Series getTIUPointer(Series series) throws MethodException, ConnectionException
	{
		TIUPointerDAO dao = new TIUPointerDAO(this);
		return dao.getEntityByExample(series);
	}

	
	public Integer getDicomCorrectCount(DicomCorrectInfo dicomCorrectInfo) throws MethodException, ConnectionException
	{
		DicomCorrectInfoDAO dao = new DicomCorrectInfoDAO(this);
		return dao.getDicomCorrectCount(dicomCorrectInfo);
	}

	/*
	 * (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.DicomStorageDataSourceSpi#deleteStudyAndSeriesCache()
	 */
	public Boolean deleteStudyAndSeriesCache() throws MethodException{	
		
		StudyDAO studydao = new StudyDAO(this);
		studydao.purgeStudyCache();
		
		SeriesDAO seriesdao = new SeriesDAO(this);
		seriesdao.purgeSeriesCache();
		
		return true;
	}
	
	@Override
	public boolean isVersionCompatible()
	{
		// TODO - Remove comments when able...
		return true;
	}
	

	@Override
	public Dose createRadiationDose(PatientRef patient, ProcedureRef procedure, Study study, Series series, Dose dose) 
			throws MethodException,ConnectionException
	{
		RadiationDosageDAO dao = new RadiationDosageDAO(this);
		return dao.createDoseRecord(patient, procedure, study, series, dose);
	}

	@Override
	public List<Dose> getRadiationDoseDetails(String patientDfn, String accessionNumber) throws MethodException, ConnectionException
	{
		RadiationDosageDAO dao = new RadiationDosageDAO(this);
		return dao.getRadiationDoseDetails(patientDfn, accessionNumber);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.DicomStorageDataSourceSpi#getDicomUid(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public DicomUid getDicomUid(String accessionNumber, String siteId, String instrument, String type) 
	throws MethodException, ConnectionException 
	{
		DicomUidDAO dao = new DicomUidDAO(this);
		return dao.createNewDicomUid(accessionNumber, siteId, instrument, type);
	}


}
