/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 12, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
  Description: 

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
package gov.va.med.imaging.vistaimagingdatasource.pathology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.pathology.AbstractPathologySite;
import gov.va.med.imaging.pathology.PathologyAcquisitionSite;
import gov.va.med.imaging.pathology.PathologyCase;
import gov.va.med.imaging.pathology.PathologyCaseConsultationURN;
import gov.va.med.imaging.pathology.PathologyCaseReportField;
import gov.va.med.imaging.pathology.PathologyCaseSlide;
import gov.va.med.imaging.pathology.PathologyCaseSpecimen;
import gov.va.med.imaging.pathology.PathologyCaseSupplementalReport;
import gov.va.med.imaging.pathology.PathologyCaseTemplate;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.pathology.PathologyCaseUpdateAttributeResult;
import gov.va.med.imaging.pathology.PathologyCptCode;
import gov.va.med.imaging.pathology.PathologyCptCodeResult;
import gov.va.med.imaging.pathology.PathologyFieldURN;
import gov.va.med.imaging.pathology.PathologyFieldValue;
import gov.va.med.imaging.pathology.PathologyReadingSite;
import gov.va.med.imaging.pathology.PathologySaveCaseReportResult;
import gov.va.med.imaging.pathology.PathologySite;
import gov.va.med.imaging.pathology.PathologySnomedCode;
import gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi;
import gov.va.med.imaging.pathology.enums.PathologyCaseAssistance;
import gov.va.med.imaging.pathology.enums.PathologyCaseConsultationUpdateStatus;
import gov.va.med.imaging.pathology.enums.PathologyCaseReserveResult;
import gov.va.med.imaging.pathology.enums.PathologyElectronicSignatureNeed;
import gov.va.med.imaging.pathology.enums.PathologyField;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimaging.codegenerator.VistaImagingCodeGeneratorDataSourceService;
import gov.va.med.imaging.vistaimaging.codegenerator.annotation.VistaImagingGeneratedDataSourceServiceType;
import gov.va.med.imaging.vistaimaging.codegenerator.annotation.VistaImagingGeneratedDataSourceMethod;
import gov.va.med.imaging.vistaimagingdatasource.AbstractVistaImagingDataSourceService;
import gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory;
import gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator;

/**
 * @author VHAISWWERFEJ
 *
 */
@VistaImagingGeneratedDataSourceServiceType
public abstract class VistaImagingPathologyDataSourceService
extends AbstractVistaImagingDataSourceService
implements VistaImagingCodeGeneratorDataSourceService, PathologyDataSourceSpi
{
	
	public final static String SUPPORTED_PROTOCOL = "vistaimaging";
	private Logger logger = Logger.getLogger(this.getClass());
	
	// The required version of VistA Imaging needed to execute the RPC calls for this operation
	public final static String MAG_REQUIRED_VERSION = "3.0P122"; 
	
	public VistaImagingPathologyDataSourceService(ResolvedArtifactSource resolvedArtifactSource, 
			String protocol)
	{
		super(resolvedArtifactSource, protocol);
		if(! (resolvedArtifactSource instanceof ResolvedSite) )
			throw new UnsupportedOperationException("The artifact source must be an instance of ResolvedSite and it is a '" + resolvedArtifactSource.getClass().getSimpleName() + "'.");
	}
	
	@Override
	public String getVistaImagingRequiredVersion()
	{
		return MAG_REQUIRED_VERSION;
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

	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createGetLockMinutesQuery",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translateLockMinutes",
			queryFactoryParameters="")
	public abstract Integer getLockExpiresMinutes(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException;
	
	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createGetSlidesVistaQuery",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translateSpecimens",
			inputParametersDescription="pathologyCaseUrn.toString()")
	public abstract List<PathologyCaseSpecimen> getCaseSpecimens(
			PathologyCaseURN pathologyCaseUrn) 
	throws MethodException, ConnectionException;

	@Override
	public List<PathologyCase> getCases(RoutingToken globalRoutingToken,
			boolean released, int days, String requestingSiteId) 
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getCases", getDataSourceVersion());
		logger.info("getCases (" + (released == true ? "released" : "unreleased") + ", days='" + days + "'" + (requestingSiteId == null ? "" : " requestingSiteId='" + requestingSiteId + "'") + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try 
		{
			localVistaSession = getVistaSession();
			VistaQuery query = 
				VistaImagingPathologyQueryFactory.createGetCasesVistaQuery(released, days, requestingSiteId);
			String rtn = localVistaSession.call(query);
			return translateAndLoadCaseResult(localVistaSession, rtn);
		}
		catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		catch (InvalidVistaCredentialsException e)
		{
			throw new InvalidCredentialsException(e.getMessage());
		}
		catch (VistaMethodException e)
		{
			throw new MethodException(e.getMessage());
		}
		finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}
	
	private List<PathologyCase> translateAndLoadCaseResult(VistaSession vistaSession, String vistaResult) 
	throws MethodException, IOException, InvalidVistaCredentialsException, VistaMethodException
	{
		List<PathologyCase> cases = VistaImagingPathologyTranslator.translateLabCasesResult(getSite(), vistaResult);
		
		
		// JMW 6/28/3013 p138 - no longer loading consultation status information for each case. This functionality
		// is de-scoped from the patch and the RPC is no longer included in the KIDS
		 
		for(PathologyCase pathologyCase : cases)
		{
			VistaQuery query = VistaImagingPathologyQueryFactory.createGetConsultStatusQuery(pathologyCase.getPathologyCaseUrn());
			String rtn = vistaSession.call(query);
			
			pathologyCase.setConsultations(VistaImagingPathologyTranslator.translateConsultations(pathologyCase.getPathologyCaseUrn(), rtn));
		}
		
		return cases;
	}

	@Override
	public List<PathologyCase> getPatientCases(RoutingToken globalRoutingToken,
			PatientIdentifier patientIdentifier, String requestingSiteId) 
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getSites", getDataSourceVersion());
		logger.info("getPatientCases (" + patientIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try 
		{
			localVistaSession = getVistaSession();
			String patientDfn = getPatientDfn(localVistaSession, patientIdentifier);			
			VistaQuery query = 
				VistaImagingPathologyQueryFactory.createGetPatientCasesVistaQuery(patientDfn,
						requestingSiteId);
			String rtn = localVistaSession.call(query);
			return translateAndLoadCaseResult(localVistaSession, rtn);
		}
		catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		catch (InvalidVistaCredentialsException e)
		{
			throw new InvalidCredentialsException(e.getMessage());
		}
		catch (VistaMethodException e)
		{
			throw new MethodException(e.getMessage());
		}
		finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}

	@Override
	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createGetSitesVistaQuery",
			queryFactoryParameters="reading",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translatePathologySites",
			translationResultMethodParameters="rtn, reading",
			inputParametersDescription="reading")			
	public abstract List<AbstractPathologySite> getSites(
			RoutingToken globalRoutingToken, boolean reading)
	throws MethodException, ConnectionException;

	@Override
	public PathologyCaseUpdateAttributeResult lockCase(
			PathologyCaseURN pathologyCaseUrn, boolean lock)
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("lockCase", getDataSourceVersion());
		logger.info("lockCase (" + pathologyCaseUrn.toString() + ", " + lock +") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try 
		{
			localVistaSession = getVistaSession();			
			VistaQuery query = 
				VistaImagingPathologyQueryFactory.createLockCaseVistaQuery(pathologyCaseUrn, lock);
			String rtn = localVistaSession.call(query);
			return VistaImagingPathologyTranslator.translateCaseLockResult(rtn);
		}
		catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		catch (InvalidVistaCredentialsException e)
		{
			throw new InvalidCredentialsException(e.getMessage());
		}
		catch (VistaMethodException e)
		{
			throw new MethodException(e.getMessage());
		}
		finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}

	@Override
	public PathologyCaseUpdateAttributeResult addCaseAssistance(
			PathologyCaseURN pathologyCaseUrn,
			PathologyCaseAssistance assistanceType, String stationNumber)
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("addCaseAssistance", getDataSourceVersion());
		logger.info("addCaseAssistance (" + pathologyCaseUrn.toString() + ", " + assistanceType +") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try 
		{
			localVistaSession = getVistaSession();			
			VistaQuery query = 
				VistaImagingPathologyQueryFactory.createPutConsultVistaQuery(pathologyCaseUrn, assistanceType, stationNumber);
			String rtn = localVistaSession.call(query);
			return VistaImagingPathologyTranslator.translatePutCaseResult(rtn);
		}
		catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		catch (InvalidVistaCredentialsException e)
		{
			throw new InvalidCredentialsException(e.getMessage());
		}
		catch (VistaMethodException e)
		{
			throw new MethodException(e.getMessage());
		}
		finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}

	@Override
	public List<String> getSiteTemplate(RoutingToken globalRoutingToken,
			List<String> apSections) 
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getSiteTemplate", getDataSourceVersion());
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		for(String apSection : apSections)
		{
			sb.append(prefix);
			sb.append(apSection);
			prefix = ", ";
		}
		logger.info("getSiteTemplate (" + sb.toString() +") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try 
		{
			List<String> result = new ArrayList<String>();
			localVistaSession = getVistaSession();
			for(String apSection : apSections)
			{
				VistaQuery query = 
					VistaImagingPathologyQueryFactory.createGetTemplateVistaQuery(apSection);
				String rtn = localVistaSession.call(query);
				String template = VistaImagingPathologyTranslator.translateTemplate(rtn, apSection);
				result.add(template);
			}
			return result;
		}
		catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		catch (InvalidVistaCredentialsException e)
		{
			throw new InvalidCredentialsException(e.getMessage());
		}
		catch (VistaMethodException e)
		{
			throw new MethodException(e.getMessage());
		}
		finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}

	@Override
	public void saveSiteTemplate(RoutingToken globalRoutingToken,
			String xmlTemplate, String apSection) 
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("saveSiteTemplate", getDataSourceVersion());		
		logger.info("saveSiteTemplate (" + apSection + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try 
		{			
			localVistaSession = getVistaSession();
			VistaQuery query = VistaImagingPathologyQueryFactory.createSaveTemplateVistaQuery(apSection, xmlTemplate);
			String rtn = localVistaSession.call(query);
			if(!rtn.startsWith("1"))
			{
				throw new MethodException("Error updating site template: " + rtn);
			}
		}
		catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		catch (InvalidVistaCredentialsException e)
		{
			throw new InvalidCredentialsException(e.getMessage());
		}
		catch (VistaMethodException e)
		{
			throw new MethodException(e.getMessage());
		}
		finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}

	@Override
	public void updateReadingSite(RoutingToken globalRoutingToken,
			PathologyReadingSite readingSite, boolean delete) 
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("updateReadingSiteList", getDataSourceVersion());	
		logger.info("updateReadingSiteList (" + readingSite.getSiteId() + ", delete[" + delete + "]) TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try 
		{
			localVistaSession = getVistaSession();
			VistaQuery query = VistaImagingPathologyQueryFactory.createSaveReadingSiteVistaQuery(readingSite, delete);
			String rtn = localVistaSession.call(query);
			if(!rtn.startsWith("1"))
			{
				throw new MethodException("Error updating reading site list: " + rtn);
			}
		}
		catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		catch (InvalidVistaCredentialsException e)
		{
			throw new InvalidCredentialsException(e.getMessage());
		}
		catch (VistaMethodException e)
		{
			throw new MethodException(e.getMessage());
		}
		finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}

	@Override
	public void updateAcquisitionSite(RoutingToken globalRoutingToken,
			PathologyAcquisitionSite acquisitionSite, boolean delete)
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("updateAcquisitionSiteList", getDataSourceVersion());	
		logger.info("updateAcquisitionSiteList (" + acquisitionSite.getSiteId() + ", delete[" + delete + "]) TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try 
		{
			localVistaSession = getVistaSession();
			VistaQuery query = VistaImagingPathologyQueryFactory.createSaveAcquisitionSiteVistaQuery(acquisitionSite, delete);
			String rtn = localVistaSession.call(query);
			if(!rtn.startsWith("1"))
			{
				throw new MethodException("Error updating acquisition site list: " + rtn);
			}
		}
		catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		catch (InvalidVistaCredentialsException e)
		{
			throw new InvalidCredentialsException(e.getMessage());
		}
		catch (VistaMethodException e)
		{
			throw new MethodException(e.getMessage());
		}
		finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}

	@Override
	public String getPathologyCaseReport(PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getPathologyCaseReport", getDataSourceVersion());	
		logger.info("getPathologyCaseReport (" + pathologyCaseUrn.toString() + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try 
		{
			localVistaSession = getVistaSession();
			VistaQuery query = VistaImagingPathologyQueryFactory.createGetCprsCaseReport(pathologyCaseUrn);
			String rtn = localVistaSession.call(query);
			return VistaImagingPathologyTranslator.translatePathologyReport(rtn);
		}
		catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		catch (InvalidVistaCredentialsException e)
		{
			throw new InvalidCredentialsException(e.getMessage());
		}
		catch (VistaMethodException e)
		{
			throw new MethodException(e.getMessage());
		}
		finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}

	@Override
	public List<PathologyCaseSupplementalReport> getCaseSupplementalReports(
			PathologyCaseURN pathologyCaseUrn) 
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getSupplementalReports", getDataSourceVersion());	
		logger.info("getSupplementalReports (" + pathologyCaseUrn.toString() + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try 
		{
			localVistaSession = getVistaSession();
			VistaQuery query = VistaImagingPathologyQueryFactory.createGetSupplementalReports(pathologyCaseUrn);
			String rtn = localVistaSession.call(query);
			return VistaImagingPathologyTranslator.translateSupplementalReports(rtn);
		}
		catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		catch (InvalidVistaCredentialsException e)
		{
			throw new InvalidCredentialsException(e.getMessage());
		}
		catch (VistaMethodException e)
		{
			throw new MethodException(e.getMessage());
		}
		finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}

	@Override
	public PathologyCaseTemplate getCaseTemplateData(
			PathologyCaseURN pathologyCaseUrn, List<String> fields)
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getTemplateData", getDataSourceVersion());	
		logger.info("getTemplateData (" + pathologyCaseUrn.toString() + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try 
		{
			localVistaSession = getVistaSession();
			VistaQuery query = VistaImagingPathologyQueryFactory.createGetTemplateDataQuery(pathologyCaseUrn, fields);
			String rtn = localVistaSession.call(query);
			return VistaImagingPathologyTranslator.translateTemplate(rtn);
		}
		catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		catch (InvalidVistaCredentialsException e)
		{
			throw new InvalidCredentialsException(e.getMessage());
		}
		catch (VistaMethodException e)
		{
			throw new MethodException(e.getMessage());
		}
		finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}

	@Override
	public PathologyCaseReserveResult reserveCase(
			PathologyCaseURN pathologyCaseUrn, boolean lock)
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("reserveCase", getDataSourceVersion());
		logger.info("reserveCase (" + pathologyCaseUrn.toString() + ", " + lock + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try 
		{
			localVistaSession = getVistaSession();			
			VistaQuery query = 
				VistaImagingPathologyQueryFactory.createReserveCaseVistaQuery(pathologyCaseUrn, lock);
			String rtn = localVistaSession.call(query);
			return VistaImagingPathologyTranslator.translateCaseReserveResult(rtn);
		}
		catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		catch (InvalidVistaCredentialsException e)
		{
			throw new InvalidCredentialsException(e.getMessage());
		}
		catch (VistaMethodException e)
		{
			throw new MethodException(e.getMessage());
		}
		finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}

	@Override
	public PathologyElectronicSignatureNeed checkElectronicSignatureNeeded(
			RoutingToken routingToken, String apSection)
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("checkElectronicSignatureNeeded", getDataSourceVersion());
		logger.info("checkElectronicSignatureNeeded (" + apSection + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try 
		{
			localVistaSession = getVistaSession();			
			VistaQuery query = 
				VistaImagingPathologyQueryFactory.createGetElectronicSignatureAvailable(apSection);
			String rtn = localVistaSession.call(query);
			TransactionContextFactory.get().addDebugInformation("ESig required check for '" + apSection + "', " + rtn);
			logger.info("ESig required check for '" + apSection + "', " + rtn);
			return VistaImagingPathologyTranslator.translateElectronicSignatureNeedResult(rtn);
		}
		catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		catch (InvalidVistaCredentialsException e)
		{
			throw new InvalidCredentialsException(e.getMessage());
		}
		catch (VistaMethodException e)
		{
			throw new MethodException(e.getMessage());
		}
		finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}

	@Override
	public List<PathologyFieldValue> getPathologyFields(
			RoutingToken globalRoutingToken, PathologyField pathologyField,
			String searchParameter) 
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getPathologyFields", getDataSourceVersion());
		logger.info("getPathologyFields (" + pathologyField + ", " + searchParameter + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try 
		{
			localVistaSession = getVistaSession();			
			VistaQuery query = 
				VistaImagingPathologyQueryFactory.createGetListQuery(pathologyField, searchParameter);
			String rtn = localVistaSession.call(query);
			//TransactionContextFactory.get().addDebugInformation("ESig required check for '" + apSection + "', " + rtn);
			//logger.info("ESig required check for '" + apSection + "', " + rtn);
			return VistaImagingPathologyTranslator.translateFieldValues(rtn, getSite(), pathologyField);
		}
		catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		catch (InvalidVistaCredentialsException e)
		{
			throw new InvalidCredentialsException(e.getMessage());
		}
		catch (VistaMethodException e)
		{
			throw new MethodException(e.getMessage());
		}
		finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}

	@Override
	public void updateConsultationStatus(
			PathologyCaseConsultationURN pathologyCaseConsultationUrn,
			PathologyCaseConsultationUpdateStatus consultationUpdateStatus)
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("updateConsultationStatus", getDataSourceVersion());
		logger.info("updateConsultationStatus (" + pathologyCaseConsultationUrn.toString() + ", " + consultationUpdateStatus + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try 
		{
			localVistaSession = getVistaSession();			
			VistaQuery query = 
				VistaImagingPathologyQueryFactory.createChangeConsultationStatusQuery(pathologyCaseConsultationUrn, 
						consultationUpdateStatus);
			String rtn = localVistaSession.call(query);
			if(!rtn.startsWith("1"))
			{
				throw new MethodException("Error updating pathology case consultation status: " + rtn);
			}			
		}
		catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		catch (InvalidVistaCredentialsException e)
		{
			throw new InvalidCredentialsException(e.getMessage());
		}
		catch (VistaMethodException e)
		{
			throw new MethodException(e.getMessage());
		}
		finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}

	@Override
	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createSaveCaseReportQuery",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translateSavingCaseReportFields",
			inputParametersDescription="pathologyCaseUrn.toString()")
	public abstract PathologySaveCaseReportResult saveCaseReportFields(PathologyCaseURN pathologyCaseUrn,
			List<PathologyCaseReportField> fields) 
	throws MethodException, ConnectionException;

	@Override
	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createSaveCaseSupplementalReportQuery",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translateSavingCaseSupplementalReport",
			inputParametersDescription="pathologyCaseUrn.toString()")
	public abstract void saveCaseSupplementalReport(PathologyCaseURN pathologyCaseUrn,
			String reportContents, Date date, boolean verified)
	throws MethodException, ConnectionException;

	@Override
	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createGetSitesQuery",
			queryFactoryParameters="",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translateSites")
	public abstract List<PathologySite> getSites(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException;

	@Override
	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createSetLockMinutesQuery",
			queryFactoryParameters="minutes",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translateSavingLockMinutes",
			inputParametersDescription="minutes")
	public abstract void setLockExpiresMinutes(RoutingToken globalRoutingToken, int minutes)
	throws MethodException, ConnectionException;

	@Override
	public VistaSession getVistaSession() 
	throws IOException, ConnectionException, MethodException
	{
		return VistaSession.getOrCreate(getMetadataUrl(), getSite());
	}

	@Override
	public String getDataSourceVersion()
	{
		return "1";
	}

	@Override
	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createGetUserPreferencesQuery",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translateUserPreferences",
			inputParametersDescription="userId,label",
			queryFactoryParameters="userId,label")
	public abstract String getPreferences(RoutingToken globalRoutingToken, String userId, String label)
	throws MethodException, ConnectionException;

	@Override
	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createSaveUserPreferencesQuery",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translateSavingUserPreferences",
			inputParametersDescription="userId,label",
			queryFactoryParameters="userId,label,xml")
	public abstract void savePreferences(RoutingToken globalRoutingToken, String userId, String label, String xml)
	throws MethodException, ConnectionException;

	@Override
	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createGetSnomedCodesQuery",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translateSnomedResults",
			inputParametersDescription="pathologyCaseUrn.toString()")
	public abstract List<PathologySnomedCode> getCaseSnomedCodes(
			PathologyCaseURN pathologyCaseUrn) 
	throws MethodException, ConnectionException;

	@Override
	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createSaveSnomedCodes",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translateSavingSnomedCode",
			inputParametersDescription="pathologyCaseUrn.toString(),tissueId:tissueId,field:pathologyFieldUrn.toString()")
	public abstract String saveCaseSnomedCode(
			PathologyCaseURN pathologyCaseUrn, 
			String tissueId, 
			PathologyFieldURN pathologyFieldUrn) 
	throws MethodException, ConnectionException;

	@Override
	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createSaveSnomedCodes",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translateSavingSnomedCode",
			inputParametersDescription="pathologyCaseUrn.toString(),tissueId:tissueId,morphologyId:morphologyId,etiologyField:etiologyFieldUrn.toString()")
	public abstract String saveCaseEtiologySnomedCodeForMorphology(PathologyCaseURN pathologyCaseUrn,
			String tissueId, String morphologyId,
			PathologyFieldURN etiologyFieldUrn) 
	throws MethodException, ConnectionException;

	@Override
	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createSaveCptCodesQuery",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translateSavingCptCode",
			inputParametersDescription="pathologyCaseUrn.toString(),location:locationFieldUrn.toString()")
	public abstract List<PathologyCptCodeResult> saveCaseCptCodes(PathologyCaseURN pathologyCaseUrn,
			PathologyFieldURN locationFieldUrn, List<String> cptCodes)
	throws MethodException, ConnectionException;

	@Override
	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createGetCptCodesQuery",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translateCptCodesResults",
			inputParametersDescription="pathologyCaseUrn.toString()")
	public abstract List<PathologyCptCode> getCaseCptCodes(
			PathologyCaseURN pathologyCaseUrn) 
	throws MethodException, ConnectionException;

	@Override
	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createGetUserQuery",
			queryFactoryParameters="",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translateUserKeys")
	public abstract List<String> getPathologyUserKeys(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException;

	@Override
	public List<PathologyCase> getSpecificCases(List<PathologyCaseURN> cases)
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getSpecificCases", getDataSourceVersion());
		logger.info("getSpecificCases () TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try 
		{
			localVistaSession = getVistaSession();
			VistaQuery query = 
				VistaImagingPathologyQueryFactory.createGetCasesQuery(cases);
			String rtn = localVistaSession.call(query);
			return translateAndLoadCaseResult(localVistaSession, rtn);
		}
		catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		catch (InvalidVistaCredentialsException e)
		{
			throw new InvalidCredentialsException(e.getMessage());
		}
		catch (VistaMethodException e)
		{
			throw new MethodException(e.getMessage());
		}
		finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}

	@Override
	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createCheckPendingConsultations",
			queryFactoryParameters="stationNumber",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translateCheckPendingConsultations",
			inputParametersDescription="stationNumber")
	public abstract Boolean checkPendingConsultationStatus(
			RoutingToken globalRoutingToken, String stationNumber)
	throws MethodException, ConnectionException;

	@Override
	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createSaveCaseTissues",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translateSavingSnomedCode",
			inputParametersDescription="pathologyCaseUrn.toString()")
	public abstract String saveCaseTissue(PathologyCaseURN pathologyCaseUrn,
			PathologyFieldURN tissueFieldUrn) 
	throws MethodException, ConnectionException;

	@Override
	public PathologyCaseURN copyCase(RoutingToken globalRoutingToken, PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("copyCase", getDataSourceVersion());
		logger.info("copyCase (" + pathologyCaseUrn.toString() + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try 
		{
			localVistaSession = getVistaSession();
			PatientIdentifier patientIdentifier = pathologyCaseUrn.getPatientId();
			String patientDfn = getPatientDfn(localVistaSession, patientIdentifier);			
			VistaQuery query = 
				VistaImagingPathologyQueryFactory.createCopyCaseQuery(patientDfn, pathologyCaseUrn);
			String rtn = localVistaSession.call(query);
			
			return VistaImagingPathologyTranslator.translateCopyCase(rtn, getSite(), pathologyCaseUrn.getPatientId());
		}
		catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		catch (InvalidVistaCredentialsException e)
		{
			throw new InvalidCredentialsException(e.getMessage());
		}
		catch (VistaMethodException e)
		{
			throw new MethodException(e.getMessage());
		}
		finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}

	@Override
	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createDeleteSnomedCode",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translateDeletingSnomedCode",
			inputParametersDescription="pathologyCaseUrn.toString(),tissueId:tissueId,snomedId:snomedId,etiologyId:etiologyId",
			queryFactoryParameters="pathologyCaseUrn,tissueId,snomedId,snomedField,etiologyId")
	public abstract void deleteSnomedCode(PathologyCaseURN pathologyCaseUrn,
			String tissueId, String snomedId, PathologyField snomedField, String etiologyId)
	throws MethodException, ConnectionException;

	@Override
	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createSaveCaseNoteQuery",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translateSavingCaseNote",
			inputParametersDescription="pathologyCaseUrn.toString()")
	public abstract void saveCaseNote(PathologyCaseURN pathologyCaseUrn, String note)
	throws MethodException, ConnectionException;

	@Override
	@VistaImagingGeneratedDataSourceMethod(queryFactoryMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory.createGetCaseNoteQuery",
			translationResultMethodName="gov.va.med.imaging.vistaimagingdatasource.pathology.translator.VistaImagingPathologyTranslator.translateCaseNote",
			inputParametersDescription="pathologyCaseUrn.toString()")
	public abstract String getCaseNote(PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException;
	
	@Override
	public List<PathologyCaseSlide> getCaseSlideInformation(
			PathologyCaseURN pathologyCaseUrn) throws MethodException,
			ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getCaseSlideInformation", getDataSourceVersion());
		logger.info("getCaseSlideInformation (" + pathologyCaseUrn.toString() + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try 
		{
			localVistaSession = getVistaSession();			
			VistaQuery query = VistaImagingPathologyQueryFactory.createGetCaseSlideInformationQuery(pathologyCaseUrn);
			String rtn = localVistaSession.call(query);
			return VistaImagingPathologyTranslator.translateCaseSlideInformation(rtn);						
		}
		catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		catch (InvalidVistaCredentialsException e)
		{
			throw new InvalidCredentialsException(e.getMessage());
		}
		catch (VistaMethodException e)
		{
			throw new MethodException(e.getMessage());
		}
		finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}
}
