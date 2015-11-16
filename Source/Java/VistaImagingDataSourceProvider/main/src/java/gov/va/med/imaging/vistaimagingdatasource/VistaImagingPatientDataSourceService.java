/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 18, 2009
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

import gov.va.med.HealthSummaryURN;
import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.PatientDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exchange.business.HealthSummaryType;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.exchange.business.PatientMeansTestResult;
import gov.va.med.imaging.exchange.business.PatientSensitiveValue;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.enums.PatientSensitivityLevel;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedInputStream;
import gov.va.med.imaging.protocol.vista.VistaImagingTranslator;
import gov.va.med.imaging.core.interfaces.exceptions.PatientNotFoundException;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.url.vista.image.ImagingStorageCredentials;
import gov.va.med.imaging.url.vista.storage.VistaImagingStorageManager;
import gov.va.med.imaging.vista.storage.SmbStorageUtility;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.log4j.Logger;

/**
 * Methods for patient information that use VistA Imaging RPC calls.
 * 
 * @author vhaiswwerfej
 *
 */
public class VistaImagingPatientDataSourceService
extends AbstractVistaImagingDataSourceService
implements PatientDataSourceSpi 
{
	private SmbStorageUtility smbStorageUtility = null;
	private Logger logger = Logger.getLogger(this.getClass());
	
	public final static String SUPPORTED_PROTOCOL = "vistaimaging";
	
	// The required version of VistA Imaging needed to execute the RPC calls for this operation
	public final static String MAG_REQUIRED_VERSION = "3.0P46"; // don't need any new RPC calls, right?
	
    /**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingPatientDataSourceService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
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
	

	private synchronized SmbStorageUtility getSmbStorageUtility()
	{
    	if(this.smbStorageUtility == null)
    		this.smbStorageUtility = new SmbStorageUtility();
    	
		return this.smbStorageUtility;
	}

	protected VistaSession getVistaSession() 
    throws IOException, ConnectionException, MethodException
    {
	    return VistaSession.getOrCreate(getMetadataUrl(), getSite());
    }

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImagingPatientDataSource#findPatients(java.lang.String)
	 */
	@Override
	public SortedSet<Patient> findPatients(RoutingToken globalRoutingToken, String searchName)
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("findPatients", getDataSourceVersion());
		VistaSession localVistaSession = null;
		try 
		{
			localVistaSession = getVistaSession();
			return findPatients(localVistaSession, searchName);
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
	
	private SortedSet<Patient> findPatients(VistaSession vistaSession, String searchName)
	throws MethodException, ConnectionException, IOException 
	{
		logger.info("findPatients(" + searchName + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		SortedSet<Patient> patients = new TreeSet<Patient>();
		VistaQuery vm = VistaImagingQueryFactory.createFindPatientQuery(searchName);
		String rtn = "";
        try
        {        	
	        rtn = vistaSession.call(vm);
	        List<VistaPatient> vistaPatients = VistaImagingTranslator.convertFindPatientResultsToVistaPatient(rtn);
	        for(int i = 0; i < vistaPatients.size(); i++)
	        {
	        	VistaPatient vistaPatient = vistaPatients.get(i);
	        	Patient patient = getPatientDetails(vistaSession, vistaPatient);
	        	if(patient != null)
	        		patients.add(patient);
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
        return patients;
	}
	
	private Patient getPatientDetails(VistaSession vistaSession, VistaPatient vistaPatient)
	throws VistaMethodException, InvalidVistaCredentialsException, IOException
	{
		logger.info("getPatientDetails(" + vistaPatient.getDfn() + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaQuery vm = VistaImagingQueryFactory.createGetPatientInfoQuery(vistaPatient.getDfn());
		String rtn = vistaSession.call(vm);
		if(rtn.startsWith("0"))
		{
			logger.error("No patient for DFN [" + vistaPatient.getDfn() + "] found, '" + rtn + "', from site [" + getSite().getSiteNumber() + "]");
			return null;
		}
		Patient patient = null;
		try 
		{
			patient = VistaImagingTranslator.convertPatientInfoResultsToPatient(rtn, vistaPatient.isSensitive());
		}
		catch(ParseException pX)
		{
			logger.error("Error parsing patient details for patient Dfn '" + vistaPatient.getDfn() + "'", pX);
		}
		return patient;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImagingPatientDataSource#getPatientIdentificationImage(java.lang.String)
	 */
	@Override
	public InputStream getPatientIdentificationImage(PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getPatientIdentificationImage", getDataSourceVersion());
		logger.info("getPatientIdentificationImage for patient [" + patientIdentifier + "]");
    	VistaSession localVistaSession = null;
    	
    	try
        {
        	localVistaSession = getVistaSession();
        	return getPatientIdentificationImageInternal(localVistaSession, patientIdentifier);
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
	
	private InputStream getPatientIdentificationImageInternal(VistaSession localVistaSession, 
			PatientIdentifier patientIdentifier)
    throws MethodException, ConnectionException, IOException 
    {    	
    	try
    	{
    		String patientDfn = getPatientDfn(localVistaSession, patientIdentifier);
    		String filename = getPatientIdentificationImageFilename(localVistaSession, patientDfn);
    		
    		logger.info((filename == null ? "No photo Id " : "Found photo Id filename [" + filename + "]") + " for patient Dfn [" + patientDfn + "] from VistA" );
    	
    		if(filename == null)
    			return null;
    		return openPhotoIdFileStream(localVistaSession, filename);
	    } 
	    catch (VistaMethodException e)
	    {
	    	logger.error("Error in getPatientIdentificationImage", e);
	    	throw new MethodException(e.getMessage());
	    } 
	    catch (InvalidVistaCredentialsException e)
	    {
	    	logger.error("Error in getPatientIdentificationImage", e);
	    	throw new InvalidCredentialsException(e.getMessage());
	    }
	    catch(PatientNotFoundException pnfX)
	    {
	    	logger.error("Patient not found [" + patientIdentifier + "]", pnfX);
	    	return null;
	    }
    }
    
    /**
     * Opens the specified photo Id filename and returns the open input stream
     * @param localVistaSession
     * @param filename
     * @return The opened input stream to the photo Id or null if the photo is not found
     * @throws IOException
     * @throws ConnectionException
     * @throws MethodException
     */
    private InputStream openPhotoIdFileStream(VistaSession localVistaSession, String filename)
    throws IOException, ConnectionException, MethodException
    {
    	logger.info("Opening photo id file [" + filename + "]");
    	ImagingStorageCredentials imagingStorageCredentials = VistaImagingStorageManager.getImagingStorageCredentialsFromCache(filename, getSite().getSiteNumber());
    	String serverShare = VistaImagingTranslator.extractServerShare(filename);
    	if(imagingStorageCredentials == null)
    	{    		
			logger.info("Imaging Storage Credentials for site '" + getSite().getSiteNumber() + "' does not exist in the network location cache, getting from VistA");				
			imagingStorageCredentials = VistaImagingStorageManager.getImagingStorageCredentialsFromVista(localVistaSession, serverShare, getSite());						
		}
		else
		{
			logger.info("Found Imaging Storage Credentials for share [" + serverShare + "] in the network location cache");
		}    	
    	if(imagingStorageCredentials == null)
		{
    		String msg = "Could not find Imaging Storage Credentials for image share [" + serverShare + "] for image [" + filename + "] at site [" + getSite().getSiteNumber() + "]";
			logger.error(msg);
			throw new ImageNotFoundException(msg);			
		}		    	
    	
		logger.info("Opening file [" + filename + "]");
		ByteBufferBackedInputStream photoStream = getSmbStorageUtility().openPhotoId(filename, imagingStorageCredentials);
		if( (photoStream != null) && (photoStream.isReadable()))
		{
			logger.info("Returning photo Id file [" + filename + "] stream with size [" + photoStream.getSize() + "] bytes");
			return photoStream.getInputStream();
		}
		String msg = "Null input stream from SmbStorageUtility for file [" + filename + "]";
		logger.error(msg);
		throw new ImageNotFoundException(msg);    	
    }
    
    /**
     * Get the filename for the photo ID for the specified patient
     * @param localVistaSession The open vista session
     * @param patientDfn the DFN at the current site for the patient
     * @return the filename for the photo id or null if there is no photo Id for the patient dfn specified 
     * @throws VistaMethodException
     * @throws InvalidVistaCredentialsException
     * @throws IOException
     */
    private String getPatientIdentificationImageFilename(VistaSession localVistaSession, String patientDfn)
    throws VistaMethodException, InvalidVistaCredentialsException, IOException
    {
    	logger.info("Searching for patient Photo Id for patient DFN [" + patientDfn + "]");
    	VistaQuery vm = VistaImagingQueryFactory.createGetPatientPhotosQuery(patientDfn);
    	
    	String rtn = localVistaSession.call(vm);
        return VistaImagingTranslator.extractPatientPhotoIdFilenameFromVistaResult(rtn);
    }

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImagingPatientDataSource#isVersionCompatible()
	 */
	@Override
	public boolean isVersionCompatible() 
	throws SecurityCredentialsExpiredException
	{
		String version = getRequiredVistaImagingVersion();
		
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
	
	protected String getRequiredVistaImagingVersion()
	{
		return VistaImagingCommonUtilities.getVistaDataSourceImagingVersion(
				VistaImagingDataSourceProvider.getVistaConfiguration(), this.getClass(), 
				MAG_REQUIRED_VERSION);
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

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.PatientDataSource#getTreatingSites(java.lang.String)
	 */
	@Override
	public List<String> getTreatingSites(RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier, 
			boolean includeTrailingCharactersForSite200)
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getTreatingSites", getDataSourceVersion());
		logger.info("getTreatingSites(" + patientIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
        try
        {
        	localVistaSession = getVistaSession();
        	return VistaCommonUtilities.getTreatingSites(localVistaSession, patientIdentifier);
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
		VistaCommonUtilities.setDataSourceMethodAndVersion("getPatientInformation", getDataSourceVersion());
		logger.info("getPatientInformation(" + patientIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
		try
		{
			localVistaSession = getVistaSession();
			String patientDfn = getPatientDfn(localVistaSession, patientIdentifier);
			boolean sensitive = isPatientSensitive(localVistaSession, patientDfn);
			Patient patient = getPatientDetails(localVistaSession, new VistaPatient(patientDfn, sensitive));
	    	if(patient != null)
	    		return patient;
	    	throw new PatientNotFoundException("Cannot find information for patient '" + patientIdentifier + "'.");
		}
		catch(IOException ioX)
        {
        	logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
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
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}
	
	private boolean isPatientSensitive(VistaSession vistaSession,
			String patientDfn) 
	throws InvalidCredentialsException, MethodException, IOException
	{
		PatientSensitiveValue patientSensitiveValue = VistaCommonUtilities.getPatientSensitivityValueFromDfn(vistaSession, patientDfn);
		if(patientSensitiveValue == null)
			return false;
		if(patientSensitiveValue.getSensitiveLevel().getCode() > PatientSensitivityLevel.NO_ACTION_REQUIRED.getCode())
		{
			return true;
		}
		return false;
	}

	@Override
	public PatientMeansTestResult getPatientMeansTest(
			RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getPatientMeansTest", getDataSourceVersion());
		logger.info("getPatientMeansTest(" + patientIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
        try
        {
        	localVistaSession = getVistaSession();
        	return VistaCommonUtilities.getPatientMeansTest(localVistaSession, patientIdentifier);
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

	@Override
	public List<HealthSummaryType> getHealthSummaryTypes(
			RoutingToken globalRoutingToken) 
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getHealthSummaryTypes", getDataSourceVersion());
		logger.info("getHealthSummaryTypes() TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
        try
        {
        	localVistaSession = getVistaSession();
        	VistaQuery query = VistaImagingQueryFactory.createGetHealthSummariesQuery();
        	String rtn = localVistaSession.call(query);
        	return VistaImagingTranslator.translateHealthSummaries(rtn, getSite());        	
        }
        catch(IOException ioX)
        {
        	logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
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
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}

	@Override
	public String getHealthSummary(HealthSummaryURN healthSummaryUrn,
			PatientIdentifier patientIdentifier) 
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getHealthSummary", getDataSourceVersion());
		logger.info("getHealthSummary(" + healthSummaryUrn.toString() + " for patient '" + patientIdentifier + "') TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;
        try
        {
        	localVistaSession = getVistaSession();
        	String patientDfn = getPatientDfn(localVistaSession, patientIdentifier);
        	VistaQuery query = VistaImagingQueryFactory.createGetHealthSummary(healthSummaryUrn, patientDfn);
        	String rtn = localVistaSession.call(query);
        	return VistaImagingTranslator.translateHealthSummary(rtn);        	
        }
        catch(IOException ioX)
        {
        	logger.error("Exception getting VistA session", ioX);
        	throw new ConnectionException(ioX);
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
        	try{localVistaSession.close();}catch(Throwable t){}
        }
	}
}
