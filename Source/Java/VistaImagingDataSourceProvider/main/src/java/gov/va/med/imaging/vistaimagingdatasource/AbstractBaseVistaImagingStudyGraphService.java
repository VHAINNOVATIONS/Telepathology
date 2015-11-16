/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 16, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
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
package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.*;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityException;
import gov.va.med.imaging.datasource.StudyGraphDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.datasource.exceptions.UnsupportedServiceMethodException;
import gov.va.med.imaging.exchange.business.*;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * Abstract study graph service for VistA.  This implements some of the common functions needed by the version 0 and main
 * VistA study graph service. This just eliminates some duplicate code that was in both classes 
 * 
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractBaseVistaImagingStudyGraphService 
extends AbstractVistaImagingDataSourceService
implements StudyGraphDataSourceSpi 
{
	private final Logger logger = Logger.getLogger(AbstractBaseVistaImagingStudyGraphService.class);	
	
	/**
	 * Return the required version of VistA Imaging necessary to use this service
	 * @return
	 */
	protected abstract String getRequiredVistaImagingVersion();
	
	protected abstract String getDataSourceVersion();
	
	
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public AbstractBaseVistaImagingStudyGraphService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
		if(! (resolvedArtifactSource instanceof ResolvedSite) )
			throw new UnsupportedOperationException("The artifact source must be an instance of ResolvedSite and it is a '" + resolvedArtifactSource.getClass().getSimpleName() + "'.");
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


	/**
	 * 
	 * @param patientDfn
	 * @param PatientIcn
	 * @return
	 * @throws IOException 
	 * @throws VistaMethodException 
	 * @throws InvalidVistaCredentialsException 
	 * @throws VistaException 
	 */
	protected PatientSensitiveValue getPatientSensitivityLevel(VistaSession vistaSession, String patientDfn) 
	throws IOException, InvalidCredentialsException, MethodException 
	{		
		logger.info("isPatientRestricted(" + patientDfn + ", ...) TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		PatientSensitiveValue value = VistaCommonUtilities.getPatientSensitivityValueFromDfn(vistaSession, patientDfn);
		return value;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.StudyGraphDataSource#isVersionCompatible()
	 */
	@Override
	public boolean isVersionCompatible() 
	throws SecurityException
	{
		logger.info("isVersionCompatible searching for version [" + getRequiredVistaImagingVersion() + "], TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;		
		try
		{			
			localVistaSession = getVistaSession();
			return VistaImagingCommonUtilities.isVersionCompatible(getRequiredVistaImagingVersion(), localVistaSession);
		}
		catch(MethodException mX)
		{
			logger.error("There was an error finding the installed Imaging version from VistA", mX);
			TransactionContextFactory.get().addDebugInformation("isVersionCompatible() failed, " + (mX == null ? "<null error>" : mX.getMessage()));
		}
		catch(SecurityCredentialsExpiredException sceX)
		{
			// caught here to be sure it gets thrown as SecurityCredentialsExpiredException, not ConnectionException
			throw sceX;
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
	
	protected VistaSession getVistaSession() 
    throws IOException, ConnectionException, MethodException, SecurityCredentialsExpiredException
    {
	    return VistaSession.getOrCreate(getMetadataUrl(), getSite());
    }	
	
	/**
	 * 
	 * @param ien Base32 encoded IEN of the image to get the report for
	 * @return
	 * @throws MethodException 
	 * @throws ConnectionException 
	 * @throws RpcException
	 */
	protected String getReport(VistaSession vistaSession, String ien) 
	throws VistaMethodException, ConnectionException, MethodException 
	{
		logger.info("getReport(" + ien + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		// CTB 29Nov2009
		//ien = Base32ConversionUtility.base32Decode(ien);
		ien += "^";
		VistaQuery vm = VistaImagingQueryFactory.createGetReportVistaQuery(ien);
		String rtn = null;
		try
		{
			rtn = vistaSession.call(vm);
			return VistaImagingCommonUtilities.extractInvalidCharactersFromReport(rtn);
		}
		catch (Exception ex)
		{
			logger.error(ex);
			throw new VistaMethodException(ex);
		}	
	}
	
	/**
     * Determines if the patient is sensitive.  If so then compares that sensitive value to the value
     * in the filter, if the filter contains a sufficient sensitive value, then logging is done (if necessary)
     * and the function returns. If the filter does not provide sufficient sensitive level then an exception
     * is thrown.
     *  
     * @param session
     * @param patientDfn
     * @param patientIcn
     * @param filter
     * @throws IOException
     * @throws MethodException
     * @throws ConnectionException
     */
    protected void checkPatientSensitive(VistaSession session, String patientDfn, 
    		PatientIdentifier patientIdentifier, StudyFilter filter)
    throws IOException, MethodException, ConnectionException
    {
    	PatientSensitiveValue patientSensitiveValue = getPatientSensitivityLevel(session, 
				patientDfn );			
        
		if(patientSensitiveValue.getSensitiveLevel().getCode() > getMaximumPatientSensitivityLevel(filter))
		{
			// if patient is restricted (has sensitivity level 3) at this site, return an empty list (to not blow up when merging lists together)
			// fact is logged already (alternatively could throw an exception)
			InsufficientPatientSensitivityException ipsX = 
				InsufficientPatientSensitivityException.createInsufficientPatientSensitivityException(
					patientSensitiveValue, 
					patientIdentifier, filter.getMaximumAllowedLevel());
			logger.info(ipsX.getMessage());
			throw ipsX;
		}
		if(patientSensitiveValue.getSensitiveLevel().isLoggingRequired())
		{
			VistaCommonUtilities.logRestrictedAccess(session, patientDfn);
		}
    }
    
    /**
	 * Return the maximum permissible patient sensitivity level
	 * that will be included in results.  Sensitivity levels
	 * range from:
	 * 0 - least sensitive
	 *     - to -
	 * 3 - most sensitive
	 * 
	 * @return
	 */
	protected int getMaximumPatientSensitivityLevel(StudyFilter filter)
	{
		return filter.getMaximumAllowedLevel().getCode();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.StudyGraphDataSourceSpi#getPatientStudies(gov.va.med.RoutingToken, java.lang.String, gov.va.med.imaging.exchange.business.StudyFilter, gov.va.med.imaging.exchange.enums.StudyLoadLevel)
	 */
	@Override
	public StudySetResult getPatientStudies(RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier, StudyFilter filter,
		StudyLoadLevel studyLoadLevel) throws MethodException, ConnectionException
	{
		throw new UnsupportedServiceMethodException(StudyGraphDataSourceSpi.class, "getPatientStudies");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.StudyGraphDataSourceSpi#getStudy(java.lang.String, gov.va.med.GlobalArtifactIdentifier)
	 */
	@Override
	public Study getStudy(PatientIdentifier patientIdentifier, GlobalArtifactIdentifier studyId) throws MethodException,
		ConnectionException
	{
		throw new UnsupportedServiceMethodException(StudyGraphDataSourceSpi.class, "getStudy");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.StudyGraphDataSourceSpi#getStudyReport(java.lang.String, gov.va.med.GlobalArtifactIdentifier)
	 */
	@Override
	public String getStudyReport(PatientIdentifier patientIdentifier, GlobalArtifactIdentifier studyId) throws MethodException,
		ConnectionException
	{
		throw new UnsupportedServiceMethodException(StudyGraphDataSourceSpi.class, "getStudyReport");
	}
}
