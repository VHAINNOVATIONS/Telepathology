/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 10, 2008
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
package gov.va.med.imaging.vistadatasource;

import gov.va.med.HealthSummaryURN;
import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.AbstractVersionableDataSource;
import gov.va.med.imaging.datasource.PatientDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.UnsupportedServiceMethodException;
import gov.va.med.imaging.exchange.business.HealthSummaryType;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.exchange.business.PatientMeansTestResult;
import gov.va.med.imaging.exchange.business.PatientSensitiveValue;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.enums.ImagingSecurityContextType;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.SortedSet;
import org.apache.log4j.Logger;

/**
 * This data source implementation should not use any VistA Imaging RPC calls.
 *  It does not do a real version check, it always returns true.
 * 
 * @author VHAISWWERFEJ
 *
 */
public class VistaPatientDataSourceService
extends AbstractVersionableDataSource
implements PatientDataSourceSpi 
{
	private Logger logger = Logger.getLogger(this.getClass());
	public final static String SUPPORTED_PROTOCOL = "vista";
	
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaPatientDataSourceService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
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
	private ResolvedSite getResolvedSite()
	{
		return (ResolvedSite)getResolvedArtifactSource();
	}
	
	private Site getSite()
	{
		return getResolvedSite().getSite();
	}
	
	private VistaSession getVistaSession() 
    throws IOException, ConnectionException, MethodException, SecurityCredentialsExpiredException
    {
		return getVistaSession(true);
    }
	
    private VistaSession getVistaSession(boolean setDefaultContext) 
    throws IOException, ConnectionException, MethodException, SecurityCredentialsExpiredException
    {
    	if(setDefaultContext)
    		return VistaSession.getOrCreate(getMetadataUrl(), getSite(), ImagingSecurityContextType.CPRS_CONTEXT);
    	else
    		return VistaSession.getOrCreate(getMetadataUrl(), getSite());
    }

	@Override
    public boolean isVersionCompatible()
    {
		// for this implementation we are not using any MAG rpc calls, just assume version is ok
		return true;
    }

	@Override
	public List<String> getTreatingSites(RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier,
			boolean includeTrailingCharactersForSite200)
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getTreatingSites", getDataSourceVersion());
		VistaSession localVistaSession = null;
		logger.info("getTreatingSites(" + patientIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
        try
        {        	        
        	// JMW 9/5/2012
        	// this code is used in multiple ways.  When DoD users request studies from the VA we get the treating sites and in that case the user is
        	// authenticated by VistA on station 200.  In that case the account has programmer mode in station 200 so we can call the 
        	// VAFCTFU GET TREATING SITES rpc.  
        	// When VA users request DoD data we call this RPC on the CVIX to ensure the patient has actually been seen at site 200 before calling BHIE.
        	// in that case the user is not authenticated by VistA and must call the CPRS rpc to get treating sites.
        	// When VA users on the AWIV want DoD data the user is authenticated by VistA but not station 200 (another site), so we need to call the CPRS
        	// rpc to get treating sites
        	
        	// if the user is authenticated by VistA that means its a local login.  This means the standard RPC to get treating sites should be available
        	// if not local authentication then use CPRS RPC
        	
        	// JMW 9/11/2012
        	// There was a change (above) to always set the context to CPRS when getting a VistA session so no matter what is provided it will use the 
        	// CPRS context. In our internal test environment using the CPRS context to call the VAFCTFU rpc does not work because the account doesn't have programmer access
        	// but the account we use in production does have programmer access.  Using the CPRS rpc should work in all cases and makes things much easier
        	
        	// JMW 9/25/2012 
        	// The CPRS rpc works properly in our internal test environment except the CPRS rpc by design does not return the local site.  In our internal test environment 
        	// we use a SLC database as station 200 but it no longer returns SLC as a treating site which causes problems for our internal tests.  As a result for internal testing
        	// we need to call a different RPC (our old trusty RPC)
        	
        	if(VistaDataSourceProvider.getVistaConfiguration().isInternalTestEnvironment())
        	{
        		logger.info("Running in internal test environment, using the non-CPRS rpc to retrieve treating sites");
        		localVistaSession = getVistaSession(false);
        		return VistaCommonUtilities.getTreatingSites(localVistaSession, patientIdentifier);
        	}
        	else
        	{
        		logger.debug("Not running in internal test environment, using CPRS rpc to retrieve treating sites");        	
	        	localVistaSession = getVistaSession();	        	
	        	return VistaCommonUtilities.getTreatingSitesUsingCprsRpc(localVistaSession, patientIdentifier);
        	}
        	
        	/*
        	
        	TransactionContext transactionContext = TransactionContextFactory.get();        	        
        	String connectedSiteNumber = getSite().getSiteNumber();
        	String authenticatedSecurityRealm = transactionContext.getRealm();
        	if(transactionContext.isAuthenticatedByVista() && (connectedSiteNumber.equals(authenticatedSecurityRealm)))
        		return VistaCommonUtilities.getTreatingSites(localVistaSession, patientIcn);
        	else
        		return VistaCommonUtilities.getTreatingSitesUsingCprsRpc(localVistaSession, patientIcn);
        		*/
        }
        catch(IOException ioX)
        {
        	logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
        }
        finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}
	
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.PatientDataSource#getPatientSensitivityLevel(java.lang.String)
	 */
	@Override
	public PatientSensitiveValue getPatientSensitivityLevel(RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getPatientSensitivityLevel", getDataSourceVersion());
		logger.info("getPatientSensitivityLevel(" + patientIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");		
		VistaSession vistaSession = null;			
        try
        {        	
        	vistaSession = getVistaSession();        	
        	return VistaCommonUtilities.getPatientSensitivityValue(vistaSession, patientIdentifier);        	
        } 
        catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		finally
        {
        	try{vistaSession.close();}catch(Throwable t){}
        }
	}

	@Override
	public boolean logPatientSensitiveAccess(RoutingToken globalRoutingToken,
			PatientIdentifier patientIdentifier) 
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("logPatientSensitiveAccess", getDataSourceVersion());
		logger.info("logPatientSensitiveAccess(" + patientIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
        try
        {
        	localVistaSession = getVistaSession();
        	VistaCommonUtilities.logRestrictedAccess(localVistaSession, patientIdentifier);        	
        	return true;
        }
        catch(IOException ioX)
        {
        	logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
        }
        finally
        {
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.PatientDataSourceSpi#findPatients(gov.va.med.RoutingToken, java.lang.String)
	 */
	@Override
	public SortedSet<Patient> findPatients(RoutingToken globalRoutingToken, String searchName) throws MethodException,
		ConnectionException
	{
		throw new UnsupportedServiceMethodException(PatientDataSourceSpi.class, "findPatients");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.PatientDataSourceSpi#getPatientIdentificationImage(java.lang.String)
	 */
	@Override
	public InputStream getPatientIdentificationImage(PatientIdentifier patientIdentifier) throws MethodException, ConnectionException
	{
		throw new UnsupportedServiceMethodException(PatientDataSourceSpi.class, "getPatientIdentificationImage");
	}
	
	protected String getDataSourceVersion()
	{
		return "1";
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.PatientDataSourceSpi#getPatientInformation(gov.va.med.RoutingToken, java.lang.String)
	 */
	@Override
	public Patient getPatientInformation(RoutingToken globalRoutingToken,
			PatientIdentifier patientIdentifier) 
	throws MethodException, ConnectionException
	{
		throw new UnsupportedServiceMethodException(PatientDataSourceSpi.class, "getPatientInformation");
	}

	@Override
	public PatientMeansTestResult getPatientMeansTest(
			RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getPatientMeansTest", getDataSourceVersion());
		logger.info("getPatientMeansTest(" + patientIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");		
		VistaSession vistaSession = null;			
        try
        {        	
        	vistaSession = getVistaSession();        	
        	return VistaCommonUtilities.getPatientMeansTest(vistaSession, patientIdentifier);        	
        } 
        catch(IOException ioX)
		{
			logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
		}
		finally
        {
        	try{vistaSession.close();}catch(Throwable t){}
        }
	}
	
	@Override
	public List<HealthSummaryType> getHealthSummaryTypes(
			RoutingToken globalRoutingToken) 
	throws MethodException, ConnectionException
	{
		throw new UnsupportedServiceMethodException(PatientDataSourceSpi.class, "getHealthSummaryTypes");
	}

	@Override
	public String getHealthSummary(HealthSummaryURN healthSummaryUrn,
			PatientIdentifier patientIdentifier) 
	throws MethodException, ConnectionException
	{
		throw new UnsupportedServiceMethodException(PatientDataSourceSpi.class, "getHealthSummary");
	}
}
