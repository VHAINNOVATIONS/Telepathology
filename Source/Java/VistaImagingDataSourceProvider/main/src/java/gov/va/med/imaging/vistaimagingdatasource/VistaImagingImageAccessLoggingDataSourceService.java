/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 13, 2008
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
package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.AbstractVersionableDataSource;
import gov.va.med.imaging.datasource.ImageAccessLoggingSpi;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.datasource.exceptions.UnsupportedProtocolException;
import gov.va.med.imaging.exchange.ImageAccessLogEvent;
import gov.va.med.imaging.exchange.ImagingLogEvent;
import gov.va.med.imaging.exchange.business.ImageAccessReason;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.enums.ImageAccessReasonType;
import gov.va.med.imaging.core.interfaces.exceptions.PatientNotFoundException;
import gov.va.med.imaging.protocol.vista.VistaImagingTranslator;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author VHAISWWERFEJ
 *
 */
public class VistaImagingImageAccessLoggingDataSourceService
extends AbstractVersionableDataSource
implements ImageAccessLoggingSpi 
{
	private final static Logger logger = Logger.getLogger(VistaImagingImageAccessLoggingDataSourceService.class);
	
	// The required version of VistA Imaging needed to execute the RPC calls for this operation
	public final static String MAG_REQUIRED_VERSION = "3.0P59"; // JMW reducing required version number to work at V2V sites without P83
	public static final String SUPPORTED_PROTOCOL = "vistaimaging";
	
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingImageAccessLoggingDataSourceService(ResolvedArtifactSource resolvedArtifactSource,
		String protocol)
	throws UnsupportedProtocolException
	{
		super(resolvedArtifactSource, protocol);
		if(! (resolvedArtifactSource instanceof ResolvedSite) )
			throw new UnsupportedOperationException("The artifact source must be an instance of ResolvedSite and it is a '" + resolvedArtifactSource.getClass().getSimpleName() + "'.");
		
    	if( ! SUPPORTED_PROTOCOL.equals(protocol) )
			throw new UnsupportedProtocolException("This implementation does not support the '" +
				protocol + "' protocol.");
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
	

	
	private VistaSession getVistaSession() 
	throws IOException, ConnectionException, MethodException, SecurityCredentialsExpiredException
	{
		return VistaSession.getOrCreate(getMetadataUrl(), getSite());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageAccessLoggingSpi#LogImageAccessEvent(gov.va.med.imaging.exchange.ImageAccessLogEvent)
	 */
	@Override
	public void LogImageAccessEvent(ImageAccessLogEvent logEvent)
	throws UnsupportedOperationException, MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("LogImageAccessEvent", getDataSourceVersion());
		logger.info("LogImageAccessEvent(" + logEvent.getImageIen() + "), TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession vistaSession = null;
		try 
		{
			// decode the image ien
			// CTB 29Nov2009
			//String imageIen = Base32ConversionUtility.base32Decode(logEvent.getImageIen());
			String imageIen = logEvent.getImageIen();
			logEvent.setDecodedImageIen(imageIen);
			vistaSession = getVistaSession();
			populateAccessLogPatientDfn(vistaSession, logEvent);
			// if an Imaging session has not been created, create a new session
			if(!vistaSession.isSessionCreated())
			{
				VistaImagingCommonUtilities.createSession(vistaSession);
				vistaSession.setSessionCreated(true);
			}
			switch(logEvent.getEventType())
			{
				case IMAGE_ACCESS:
					logImageAccess(vistaSession, logEvent);
					break;
				case IMAGE_COPY:
					logImageCopy(vistaSession, logEvent);
					break;
				case IMAGE_PRINT:
					logImagePrint(vistaSession, logEvent);
					break;
				case PATIENT_ID_MISMATCH:
					logPatientIdMismatch(vistaSession, logEvent);
					break;
				case RESTRICTED_ACCESS:
					VistaCommonUtilities.logRestrictedAccess(vistaSession, logEvent.getPatientDfn());
					break;
			}
		}
		catch(IOException ioX)
		{
			logger.error(ioX.getMessage());
			throw new ConnectionException(ioX);
		}
		finally
		{
			try{vistaSession.close();}
			catch(Throwable t){}
		}
	}
	
	private void logImagePrint(VistaSession vistaSession, ImageAccessLogEvent event)
	throws MethodException, IOException, ConnectionException  
	{
		logger.info("logImagePrint(" + event.getImageIen() + "), TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaQuery logCopyQuery = VistaImagingQueryFactory.createLogImagePrintQuery(event);
		
		try
		{
			String rtn = vistaSession.call(logCopyQuery);
			logger.info("Response from logging image print [" + rtn + "]");
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.error("Exception logging image print", ivcX);
			throw new InvalidCredentialsException(ivcX);
		}
		catch(IOException ioX)
		{
			logger.error("Exception logging image print", ioX);
			throw new ConnectionException(ioX);
		}
		catch(VistaMethodException vmX)
		{
			logger.error("Exception logging image print", vmX);
			throw new MethodException(vmX);
		}	
	}
	
	private void logImageCopy(VistaSession vistaSession, ImageAccessLogEvent event) 
	throws MethodException, IOException, ConnectionException 
	{
		logger.info("logImageCopy(" + event.getImageIen() + "), TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaQuery logCopyQuery = VistaImagingQueryFactory.createLogImageCopyQuery(event);
		
		try
		{
			String rtn = vistaSession.call(logCopyQuery);
			logger.info("Response from logging image copy [" + rtn + "]");
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.error("Exception logging image copy", ivcX);
			throw new InvalidCredentialsException(ivcX);
		}
		catch(IOException ioX)
		{
			logger.error("Exception logging image copy", ioX);
			throw new ConnectionException(ioX);
		}
		catch(VistaMethodException vmX)
		{
			logger.error("Exception logging image copy", vmX);
			throw new MethodException(vmX);
		}		
	}
	
	private void logPatientIdMismatch(VistaSession vistaSession, ImageAccessLogEvent event) 
	throws MethodException, IOException, ConnectionException 
	{
		logger.info("logPatientIdMismatch(" + event.getImageIen() + "), TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaQuery logIdMismatchQuery = VistaImagingQueryFactory.createLogPatientIdMismatchQuery(event.getDecodedImageIen(), event.getPatientDfn());
		
		try
		{
			String rtn = vistaSession.call(logIdMismatchQuery);
			logger.info("Response from logging patient ID mismatch [" + rtn + "]");
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.error("Exception logging patient Id mismatch", ivcX);
			throw new InvalidCredentialsException(ivcX);
		}
		catch(IOException ioX)
		{
			logger.error("Exception logging patient Id mismatch", ioX);
			throw new ConnectionException(ioX);
		}
		catch(VistaMethodException vmX)
		{
			logger.error("Exception logging patient Id mismatch", vmX);
			throw new MethodException(vmX);
		}		
	}
	
	private void logImageAccess(VistaSession vistaSession, ImageAccessLogEvent event)
	throws MethodException, IOException, ConnectionException 
	{		
		logger.info("LogImageAccess(" + event.getDecodedImageIen() + ":Base32{" + event.getImageIen() + "}), TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaQuery imageLogQuery = VistaImagingQueryFactory.createLogImageAccessQuery(event.isDodImage(), 
				TransactionContextFactory.get().getDuz(), event.getDecodedImageIen(), 
				event.getPatientDfn(), event.getUserSiteNumber());
		try 
		{
			// don't really care about the result of this RPC as long as it executes 
			String rtn = vistaSession.call(imageLogQuery);
			logger.info("Response from logging image access [" + rtn + "]");
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.error("Exception logging image access", ivcX);
			throw new InvalidCredentialsException(ivcX);
		}
		catch(IOException ioX)
		{
			logger.error("Exception logging image access", ioX);
			throw new ConnectionException(ioX);
		}
		catch(VistaMethodException vmX)
		{
			logger.error("Exception logging image access", vmX);
			throw new MethodException(vmX);
		}
	}
	
	@Override
	public void LogImagingLogEvent(ImagingLogEvent logEvent)
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("LogImagingLogEvent", getDataSourceVersion());
		logger.info("LogImagingLogEvent(" + logEvent.toString() + "), TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession vistaSession = null;
		try 
		{
			vistaSession = getVistaSession();
			if(!vistaSession.isSessionCreated())
			{
				VistaImagingCommonUtilities.createSession(vistaSession);
				vistaSession.setSessionCreated(true);
			}
			
			String patientDfn = 
				VistaCommonUtilities.getPatientDFN(vistaSession, logEvent.getPatientIcn());
			
			VistaQuery imageLogQuery = VistaImagingQueryFactory.createLogImagingQuery(logEvent, patientDfn);
			// don't really care about the result of this RPC as long as it executes 
			String rtn = vistaSession.call(imageLogQuery);
			logger.info("Response from logging image event [" + rtn + "]");
			TransactionContextFactory.get().addDebugInformation("Response from logging image event [" + rtn + "]");
			if(rtn == null  || (!rtn.startsWith("1")))
				throw new MethodException("Error logging imaging event, " + rtn);
			
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.error("Exception logging image event", ivcX);
			throw new InvalidCredentialsException(ivcX);
		}
		catch(IOException ioX)
		{
			logger.error("Exception logging image event", ioX);
			throw new ConnectionException(ioX);
		}
		catch(VistaMethodException vmX)
		{
			logger.error("Exception logging image event", vmX);
			throw new MethodException(vmX);
		}
		finally
		{
			try{vistaSession.close();}
			catch(Throwable t){}
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VersionableDataSourceSpi#isVersionCompatible()
	 */
	@Override
	public boolean isVersionCompatible() 
	throws SecurityCredentialsExpiredException
	{
		String version = VistaImagingCommonUtilities.getVistaDataSourceImagingVersion(
				VistaImagingDataSourceProvider.getVistaConfiguration(), this.getClass(), 
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
	
	private void populateAccessLogPatientDfn(VistaSession vistaSession, ImageAccessLogEvent event)
	throws MethodException, IOException, ConnectionException, PatientNotFoundException
	{
		if((event.getPatientDfn() == null) || (event.getPatientDfn().length() <= 0)) 
			event.setPatientDfn( VistaCommonUtilities.getPatientDFN(vistaSession, event.getPatientIcn()) );
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageAccessLoggingSpi#getImageAccessReasons(gov.va.med.RoutingToken, java.util.List)
	 */
	@Override
	public List<ImageAccessReason> getImageAccessReasons(
			RoutingToken globalRoutingToken,
			List<ImageAccessReasonType> reasonTypes) 
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getImageAccessReasons", getDataSourceVersion());
		logger.info("getImageAccessReasons(), TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession vistaSession = null;
		try 
		{
			vistaSession = getVistaSession();
			
			VistaQuery imageLogQuery = VistaImagingQueryFactory.createGetReasonsListQuery(reasonTypes);
			// don't really care about the result of this RPC as long as it executes 
			String rtn = vistaSession.call(imageLogQuery);
			
			return VistaImagingTranslator.translateImageAccessReasons(getSite(), rtn);
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.error("Exception getting image access reasons", ivcX);
			throw new InvalidCredentialsException(ivcX);
		}
		catch(IOException ioX)
		{
			logger.error("Exception getting image access reasons", ioX);
			throw new ConnectionException(ioX);
		}
		catch(VistaMethodException vmX)
		{
			logger.error("Exception getting image access reasons", vmX);
			throw new MethodException(vmX);
		}
		finally
		{
			try{vistaSession.close();}
			catch(Throwable t){}
		}
	}

	protected String getDataSourceVersion()
	{
		return "1";
	}
}
