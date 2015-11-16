/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 15, 2009
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
package gov.va.med.imaging.vistaimagingdatasource.common;

import java.io.IOException;

import org.apache.log4j.Logger;

import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.protocol.vista.VistaImagingVistaRadTranslator;
import gov.va.med.imaging.protocol.vista.exceptions.InvalidVistaVistaRadVersionException;
import gov.va.med.imaging.core.interfaces.exceptions.PatientNotFoundException;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.url.vista.image.VistaRadSiteCredentials;
import gov.va.med.imaging.url.vista.storage.VistaImagingRadStorageManager;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingQueryFactory;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingVistaRadQueryFactory;
import gov.va.med.imaging.vistadatasource.session.VistaSession;

import java.net.URL;

import gov.va.med.imaging.exchange.business.Site;

/**
 * Implementation of some of the common VistARad specific methods
 * 
 * @author vhaiswwerfej
 *
 */
public class VistaImagingVistaRadCommonUtilities 
{
	//private final static String windowsXpVersionNumber = "5.1.2600";

	private final static Logger logger = Logger.getLogger(VistaImagingVistaRadCommonUtilities.class);
	
	
	
	/**
	 * Retrieves a VistaSession from the cache or creates one if necessary. If the session has not
	 * been logged in for VistARad, the login is attempted 
	 * @param url
	 * @param site
	 * @param requiredVersion
	 * @return
	 * @throws IOException
	 * @throws ConnectionException
	 * @throws MethodException
	 * @throws InvalidVistaVistaRadVersionException Occurs if the version of the VIX does not match the VistA version
	 */
	public static VistaSession getVistaSession(URL url, Site site, String requiredVersion) 
    throws IOException, ConnectionException, MethodException, InvalidVistaVistaRadVersionException, SecurityCredentialsExpiredException
    {	
	    VistaSession session = VistaSession.getOrCreate(url, site, requiredVersion);
	    boolean connectionFailed = false;
	    if(!session.isVistaRadLoggedIn())
	    {
	    	try
	    	{
	    		logger.info("Session (" + session.getSessionIndex() + ") not logged in for VistARad, logging in");
	    		executeVistaRadLogin(site, requiredVersion, session);
	    		connectionFailed = false;
	    	}
	    	catch(VistaMethodException vmX)
	    	{
	    		connectionFailed = true;
	    		throw new MethodException(vmX);
	    	}
	    	catch(InvalidVistaCredentialsException ivcX)
	    	{
	    		connectionFailed = true;
	    		throw new InvalidCredentialsException(ivcX);	    		
	    	}
	    	catch(InvalidVistaVistaRadVersionException vvrvX)
	    	{
	    		connectionFailed = true;
	    		throw vvrvX;
	    	}
	    	catch(IOException ioX)
	    	{
	    		connectionFailed = true;
	    		throw ioX;
	    	}
	    	finally
	    	{
	    		if(connectionFailed)
	    		{
	    			session.close();
	    			session = null;
	    		}
	    	}
	    }
	    return session;
    }
	
	private static void executeVistaRadLogin(Site site, String requiredVersion, VistaSession vistaSession)
	throws VistaMethodException, InvalidVistaCredentialsException, IOException, InvalidVistaVistaRadVersionException
	{
		magJUser(site, requiredVersion, vistaSession);
		magJCacheLocation(vistaSession);
		vistaSession.setVistaRadLoggedIn(true);
	}
	
	private static void magJCacheLocation(VistaSession vistaSession)
	throws VistaMethodException, InvalidVistaCredentialsException, IOException, InvalidVistaVistaRadVersionException 
	{	
		String cacheLocationId = getCacheLocationId();
		logger.info("magJCacheLocation cacheLocationId(" + cacheLocationId + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");		
		VistaQuery magJUserQuery = VistaImagingVistaRadQueryFactory.createMagJCacheLocationQuery(cacheLocationId);
		vistaSession.call(magJUserQuery);
		// if no exception, assuming all ok - not sure what to do with results								
	}
	
	private static String getCacheLocationId()
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		String cacheLocationId = transactionContext.getCacheLocationId();
		if(cacheLocationId == null)
			return "";
		return cacheLocationId;
	}
	
	private static String getVixVersion()
	{
		return "VIX session, vs. " + TransactionContextFactory.get().getVixSoftwareVersion();
	}
	
	private static void magJUser(Site site, String requiredVersion, VistaSession vistaSession)
	throws VistaMethodException, InvalidVistaCredentialsException, IOException, InvalidVistaVistaRadVersionException 
	{
		logger.info("magJUser(" + requiredVersion + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaQuery magJUserQuery = VistaImagingVistaRadQueryFactory.createMagJUserQuery(requiredVersion, 
				getVixVersion());
		String result = vistaSession.call(magJUserQuery);			
		String pieces[] = StringUtils.Split(result, StringUtils.STICK);		
		
		
		
		
			/*
			 1^1~Version Check OK. Server: 3.0.76.14 Client: 3.0.76.14|126^IMAGPROVIDERONETWOSIX,ONETWOSIX^SAF^1^3.0.76.14
isw-buckk-lt\vhaiswiu^,/mmYVV-s*^3^0
*KEYS
*END
			 */
			
		// can't continue
		if(result.startsWith("0"))
		{
			// bad VistARad version
			String msg = "Version [" + requiredVersion + "] not valid, response from VistA '" + pieces[0] + "'";
			logger.error(msg);
			throw new InvalidVistaVistaRadVersionException(msg);
		}
		else
		{
			// going to be allowed to continue
			VistaRadSiteCredentials siteCredentials = VistaImagingVistaRadTranslator.createSiteCredentialsFromResponse(result, site.getSiteNumber());
			VistaImagingRadStorageManager.updateSiteCredentials(siteCredentials);
			if(result.startsWith("2"))
			{
				// test version, allowed to continue
				logger.warn("Version [" + requiredVersion + "] is test version, allowed to continue. Response from VistA '" + pieces[0] + "'");
			}
			else if(result.startsWith("1"))
			{
				logger.info("Version check ok");
			}
		}
		TransactionContextFactory.get().addDebugInformation("Logged in using version '" + requiredVersion + "'.");
	}
	
	/**
	 * Retrieves the exam requisition report
	 * 
	 * @param vistaSession
	 * @param studyUrn
	 * @return
	 * @throws VistaMethodException
	 * @throws ConnectionException
	 * @throws MethodException
	 */
	public static String getMagJRequisitionReport(VistaSession vistaSession, StudyURN studyUrn)
	throws VistaMethodException, ConnectionException, MethodException 
	{
		// CTB 29Nov2009
		//String examId = Base32ConversionUtility.base32Decode(studyUrn.getStudyId());
		String examId = studyUrn.getStudyId();
		logger.info("getMagJRequisitionReport(" + examId + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");			
		VistaQuery query = VistaImagingVistaRadQueryFactory.createMagJRequisitionReportQuery(examId);
		try
		{
			return vistaSession.call(query);
		}
		catch(IOException ioX)
		{
			throw new ConnectionException(ioX);
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			throw new InvalidCredentialsException(ivcX);
		}		
	}
	
	/**
	 * Converts the patient ICN to a DFN. This function requires an open Vista connection and requires the calling
	 * function to manage (Close) the connection. The site to which the query is made is based on the vista session
	 * @param vistaSession
	 * @param patientICN
	 * @return
	 * @throws MethodException
	 * @throws IOException
	 * @throws ConnectionException
	 * @throws PatientNotFoundException
	 */
	public static String getPatientDFN(VistaSession vistaSession, String patientICN) 
	throws MethodException, IOException, ConnectionException, PatientNotFoundException
	{
		logger.info("getPatientDFN(" + patientICN + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		VistaQuery vm = VistaImagingQueryFactory.createGetPatientDFNVistaQuery(patientICN);
		
        String rtn = "";
    	try
        {
	        rtn = vistaSession.call(vm);
	    	if( rtn.startsWith("-1") ) 
	    	{
	    		logger.warn("ICN [" + patientICN + "] not found in database, response [" + rtn + "]");
	    		throw new PatientNotFoundException("Patient ICN [" + patientICN + "] not found in the database");
	    	}
        } 
    	catch (VistaMethodException e)
        {
    		logger.error("VistaMethodException while getting DFN for patient '" + patientICN + "'.", e);
    		throw new MethodException(e.getMessage());
        } 
    	catch (InvalidVistaCredentialsException e)
        {
    		logger.error("InvalidVistaCredentialsException while getting DFN for patient '" + patientICN + "'.", e);
    		throw new InvalidCredentialsException(e.getMessage());
        }
    	return rtn;
	}
	
	public static String getPatientICN(VistaSession vistaSession, String patientDFN) 
	throws MethodException, IOException, ConnectionException, PatientNotFoundException, InvalidCredentialsException
	{
		logger.info("getPatientICN(" + patientDFN + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		VistaQuery vm = VistaImagingQueryFactory.createGetPatientICNVistaQuery(patientDFN);
		
        String rtn = "";
    	try
        {
	        rtn = vistaSession.call(vm);
	    	if( rtn.startsWith("-1") ) 
	    	{
	    		logger.warn("DFN [" + patientDFN + "] not found in database, response [" + rtn + "]");
	    		throw new PatientNotFoundException("Patient DFN [" + patientDFN + "] not found in the database");
	    	}
        } 
    	catch (VistaMethodException e)
        {
    		logger.error("VistaMethodException while getting ICN for patient '" + patientDFN + "'.", e);
    		throw new MethodException(e.getMessage());
        } 
    	catch (InvalidVistaCredentialsException e)
        {
    		logger.error("InvalidVistaCredentialsException while getting ICN for patient '" + patientDFN + "'.", e);
    		throw new InvalidCredentialsException(e.getMessage());
        }
    	return rtn;
	}
}
