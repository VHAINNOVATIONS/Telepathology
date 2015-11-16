/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 28, 2009
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
package gov.va.med.imaging.vistadatasource.common;

import gov.va.med.PatientIdentifier;
import gov.va.med.PatientIdentifierType;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exchange.business.PatientMeansTestResult;
import gov.va.med.imaging.exchange.business.PatientSensitiveValue;
import gov.va.med.imaging.protocol.vista.VistaCommonTranslator;
import gov.va.med.imaging.core.interfaces.exceptions.PatientNotFoundException;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.VistaCommonQueryFactory;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.exchange.business.Site;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Implementations of some common functions that are used in multiple data sources.  Put common functions here to 
 * reduce copying of code.
 * 
 * @author vhaiswwerfej
 *
 */
public class VistaCommonUtilities 
{
	private static Logger logger = Logger.getLogger(VistaCommonUtilities.class);			
	
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
		
		VistaQuery vm = VistaCommonQueryFactory.createGetPatientDFNVistaQuery(patientICN);
		
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
    		throw new MethodException(e.getMessage());
        } 
    	catch (InvalidVistaCredentialsException e)
        {
    		throw new InvalidCredentialsException(e.getMessage());
        }
    	return rtn;
	}
	
	public static String getPatientDfn(VistaSession vistaSession, PatientIdentifier patientIdentifier) 
	throws PatientNotFoundException, MethodException, IOException, ConnectionException
	{
		if(patientIdentifier.getPatientIdentifierType() == PatientIdentifierType.dfn)
			return patientIdentifier.getValue();
		String patientDfn = VistaCommonUtilities.getPatientDFN(vistaSession, patientIdentifier.getValue());
		return patientDfn;
	}
	
	private static String workstationId = null;
	/**
	 * Get the local host name if available, else get a default name
	 * 
	 * @return
	 */
	public static synchronized String getWorkstationId() 
	{
		if(workstationId == null) {
			try {
				InetAddress addr = InetAddress.getLocalHost();
				workstationId = addr.getHostName();
			}
			catch(UnknownHostException uhX) {
				logger.warn("Unable to get local hostname, using default value");
				workstationId = "ViX-DataSource";
			}
			logger.info("Local host name set to [" + workstationId + "]");
			return workstationId;
		}
		return workstationId;
	}	
	
	public static PatientSensitiveValue getPatientSensitivityValue(VistaSession vistaSession, PatientIdentifier patientIdentifier)
	throws MethodException, IOException, ConnectionException, PatientNotFoundException
	{
		if(patientIdentifier.getPatientIdentifierType() == PatientIdentifierType.dfn)
		{
			return getPatientSensitivityValueFromDfn(vistaSession, patientIdentifier.getValue());
		}
		else
		{
			return getPatientSensitivityValueFromIcn(vistaSession, patientIdentifier.getValue());
		}
	}
	
	public static PatientSensitiveValue getPatientSensitivityValueFromIcn(VistaSession vistaSession, String patientIcn)
	throws MethodException, IOException, ConnectionException, PatientNotFoundException
	{
		logger.info("getPatientSensitivityValueFromIcn(" + patientIcn + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		String patientDfn = getPatientDFN(vistaSession, patientIcn);
		return getPatientSensitivityValueFromDfn(vistaSession, patientDfn);
	}
	
	public static PatientSensitiveValue getPatientSensitivityValueFromDfn(VistaSession vistaSession, String patientDfn)
	throws MethodException, IOException, InvalidCredentialsException
	{
		logger.info("getPatientSensitivityValueFromDfn(" + patientDfn + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		try
		{
			VistaQuery vm = VistaCommonQueryFactory.createIsPatientRestrictedVistaQuery(patientDfn);
			String rtn = vistaSession.call(vm);
			return VistaCommonTranslator.convertStringToPatientSensitiveValue(rtn, patientDfn);			
		}
		catch (VistaMethodException e)
        {
    		throw new MethodException(e.getMessage());
        } 
    	catch (InvalidVistaCredentialsException e)
        {
    		throw new InvalidCredentialsException(e.getMessage());
        }
	}
	
	public static void logRestrictedAccess(VistaSession vistaSession, PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException, IOException
	{
		if(patientIdentifier.getPatientIdentifierType() == PatientIdentifierType.dfn)
		{
			logRestrictedAccess(vistaSession, patientIdentifier.getValue());
		}
		else
		{
			logRestrictedAccessFromIcn(vistaSession, patientIdentifier.getValue());
		}
	}
	
	public static void logRestrictedAccessFromIcn(VistaSession vistaSession, String patientIcn)
	throws MethodException, ConnectionException, IOException
	{
		String dfn = getPatientDFN(vistaSession, patientIcn);
    	logRestrictedAccess(vistaSession, dfn);
	}
	
	public static void logRestrictedAccess(VistaSession vistaSession, String patientDfn)
	throws MethodException, ConnectionException 
	{
		logger.info("logRestrictedAccess(" + patientDfn + "), TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaQuery imageLogQuery = 
			VistaCommonQueryFactory.createLogRestrictedAccessQuery(patientDfn);
		try 
		{
			// don't really care about the result of this RPC as long as it executes 
			String rtn = vistaSession.call(imageLogQuery);
			logger.info("Response from logging restricted access [" + rtn + "]");
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.error("Exception logging restricted access", ivcX);
			throw new InvalidCredentialsException(ivcX);
		}
		catch(IOException ioX)
		{
			logger.error("Exception logging restricted access", ioX);
			throw new ConnectionException(ioX);
		}
		catch(VistaMethodException vmX)
		{
			logger.error("Exception logging restricted access", vmX);
			throw new MethodException(vmX);
		}
	}	
	
	/**
	 * Retrieves the sites the patient (from the patientIdentifier) has been seen at. RpcBroker must be connected before using this function. 
	 * 
	 * @param patientIdentifier Identifier for finding the patient (must be patient ICN)
	 * @return List of Strings representing the site numbers of the sites the patient has been seen at.
	 * @throws ConnectionException 
	 * @throws InvalidVistaCredentialsException 
	 * @throws RpcException Occurs if there is an exception executing an rpc during the method
	 */
	public static List<String> getTreatingSites(VistaSession vistaSession, PatientIdentifier patientIdentifier) 
	throws MethodException, ConnectionException, IOException 
	{
		logger.info("getTreatingSites(" + patientIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");

        String rtn = "";
        try
        {
        	String patientDfn = getPatientDfn(vistaSession, patientIdentifier);
        	VistaQuery vm = VistaCommonQueryFactory.createGetTreatingSitesVistaQuery(patientDfn);
	        rtn = vistaSession.call(vm);
        } 
        catch (VistaMethodException e)
        {
        	logger.error("Error in getTreatingSites", e);
        	throw new MethodException(e.getMessage());
        } 
        catch (InvalidVistaCredentialsException e)
        {
        	logger.error("Error in getTreatingSites", e);
        	throw new InvalidCredentialsException(e.getMessage());
        }
        catch(PatientNotFoundException pnfX)
        {
        	logger.error("Patient not found [" + patientIdentifier + "]", pnfX);
        	// JMW 12/14/2010 - throw the exception now
        	// necessary so we can provide the correct error for XCA requests
        	throw pnfX;
        	// return null;
        	// JMW 9/22/2009
        	// not really sure about this, was returning null but that creating inconsistencies
        	// with Federation which would return an empty array list since the command 
        	// converts the null to the array list, now doing that here instead to be 
        	// consistent.  Might want to throw exception or return null and handle 
        	// differently to allow knowledge of the patient not found versus having now sites.
        	//return new ArrayList<String>(0);
        }
        
		return VistaCommonTranslator.convertSiteStringToSiteStringList(rtn);
	}    
	
	public static String createFullBrokerTokenStringFromToken(String token, String applicationName, 
			Site site)
	{
		return applicationName + StringUtils.CARET + 
			token + StringUtils.CARET + 			
			site.getSiteNumber() + StringUtils.CARET  +
			site.getVistaPort();
	}
	
	/*
	public static List<Site> getTreatingSites(VistaSession vistaSession, String patientIdentifier) 
	throws MethodException, IOException, ConnectionException 
	{
		logger.info("getTreatingSites(" + patientIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		VistaQuery vm = VistaCommonQueryFactory.createGetTreatingSitesVistaQuery(patientIdentifier);
		
        String rtn = "";
        try
        {
	        rtn = vistaSession.call(vm);
        } 
        catch (VistaMethodException e)
        {
        	throw new MethodException(e.getMessage());
        } 
        catch (InvalidVistaCredentialsException e)
        {
        	throw new InvalidCredentialsException(e.getMessage());
        }
        
		return VistaCommonTranslator.convertSiteStringToSiteList(rtn);
	}	*/
	
	public static void setDataSourceMethodAndVersion(String methodName, String dataSourceVersion)
	{
		setDataSourceMethodVersionAndProtocol(methodName, dataSourceVersion, null);
	}
	
	public static void setDataSourceMethodVersionAndProtocol(String methodName, String dataSourceVersion, String datasourceProtocol)
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setDataSourceMethod(methodName);
		transactionContext.setDataSourceVersion(dataSourceVersion);
		if(datasourceProtocol != null && datasourceProtocol.length() > 0)
			transactionContext.setDatasourceProtocol(datasourceProtocol);
	}
	
	/**
	 * Method to change the verify code of a user. This requires the connection to already contain the user context (so the RPC
	 * can be run as the user to change their verify code). This is a problem for the realm because the realm will reject the user.
	 * 
	 * This method has not been tested but it should work. If successful there will be no return value. If there is an error
	 * changing the verify code a MethodException will be thrown containing the error message from VistA.
	 * 
	 * @param vistaSession Connected and authenticated VistaSession
	 * @param oldVerifyCode
	 * @param newVerifyCode
	 * @param confirmNewVerifyCode
	 * @throws MethodException
	 * @throws ConnectionException
	 * @throws IOException
	 */
	public static void changeVerifyCode(VistaSession vistaSession, String oldVerifyCode, 
			String newVerifyCode, String confirmNewVerifyCode)
	throws MethodException, ConnectionException, IOException
	{
		VistaQuery vistaQuery = 
			VistaCommonQueryFactory.createChangeVerifyCodeQuery(oldVerifyCode, 
					newVerifyCode, confirmNewVerifyCode);
		String rtn = "";
		try
		{
			rtn = vistaSession.call(vistaQuery);
			String [] lines = StringUtils.Split(rtn, StringUtils.NEW_LINE);
			if(lines.length > 0)
			{
				if("0".equals(lines[0].trim()))
				{
					logger.debug("Successfully changed verify code");
					// verify code changed
					return;
				}
			}
			String errorMessage = "<unknown reason>";
			if(lines.length > 1)
				errorMessage = lines[1].trim();
			throw new MethodException("Unable to change verify code, " + errorMessage);
		}
		catch (VistaMethodException e)
        {
        	logger.error("VistaMethodException in changeVerifyCode", e);
        	throw new MethodException(e.getMessage());
        } 
        catch (InvalidVistaCredentialsException e)
        {
        	logger.error("Error in getTreatingSites", e);
        	throw new InvalidCredentialsException(e.getMessage());
        }
	}
	
	public static PatientMeansTestResult getPatientMeansTest(VistaSession vistaSession, PatientIdentifier patientIdentifier)
	throws MethodException, IOException, ConnectionException, PatientNotFoundException
	{
		logger.info("getPatientMeansTestFromIcn(" + patientIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		String patientDfn = getPatientDfn(vistaSession, patientIdentifier);
		
		try
		{
			VistaQuery vm = VistaCommonQueryFactory.createMeansTestVistaQuery(patientDfn);
			String rtn = vistaSession.call(vm);
			return VistaCommonTranslator.convertMeansTestResult(rtn, patientDfn);			
		}
		catch (VistaMethodException e)
        {
    		throw new MethodException(e.getMessage());
        } 
    	catch (InvalidVistaCredentialsException e)
        {
    		throw new InvalidCredentialsException(e.getMessage());
        }
	}
	
	/**
	 * Get treating sites using the CPRS rpc. One key difference between this RPC and the VAFCTFU rpc is this RPC does not return the site
	 * being queried - that one is excluded. This RPC is meant to show the remote sites so it does not show the local site
	 * @param vistaSession
	 * @param patientIcn
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 * @throws IOException
	 */
	public static List<String> getTreatingSitesUsingCprsRpc(VistaSession vistaSession, PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException, IOException 
	{
		logger.info("getTreatingSitesUsingCprsRpc(" + patientIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");

        String rtn = "";
        try
        {
        	String patientDfn = getPatientDfn(vistaSession, patientIdentifier);
        	VistaQuery vm = VistaCommonQueryFactory.createCprsGetTreatingSitesVistaQuery(patientDfn);
	        rtn = vistaSession.call(vm);
        } 
        catch (VistaMethodException e)
        {
        	logger.error("Error in getTreatingSitesUsingCprsRpc", e);
        	throw new MethodException(e.getMessage());
        } 
        catch (InvalidVistaCredentialsException e)
        {
        	logger.error("Error in getTreatingSitesUsingCprsRpc", e);
        	throw new InvalidCredentialsException(e.getMessage());
        }
        catch(PatientNotFoundException pnfX)
        {
        	logger.error(pnfX.getMessage());
        	// JMW 12/14/2010 - throw the exception now
        	// necessary so we can provide the correct error for XCA requests
        	throw pnfX;
        	// return null;
        	// JMW 9/22/2009
        	// not really sure about this, was returning null but that creating inconsistencies
        	// with Federation which would return an empty array list since the command 
        	// converts the null to the array list, now doing that here instead to be 
        	// consistent.  Might want to throw exception or return null and handle 
        	// differently to allow knowledge of the patient not found versus having now sites.
        	//return new ArrayList<String>(0);
        }
        
		return VistaCommonTranslator.convertCprsSiteList(rtn);
	}
}
