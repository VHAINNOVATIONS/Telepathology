/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 6, 2009
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

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.AbstractVersionableDataSource;
import gov.va.med.imaging.datasource.UserAuthorizationDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.enums.ImagingSecurityContextType;
import gov.va.med.imaging.protocol.vista.exceptions.InvalidVistaVistaRadVersionException;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingVistaRadCommonUtilities;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * @author vhaiswwerfej
 *
 */
public class VistaImagingUserAuthorizationDataSourceService
extends AbstractVersionableDataSource
implements UserAuthorizationDataSourceSpi 
{
	/* =====================================================================
	 * Instance fields and methods
	 * ===================================================================== */
	private final static Logger logger = 
		Logger.getLogger(VistaImagingUserAuthorizationDataSourceService.class);
	
	public final static String SUPPORTED_PROTOCOL = "vistaimaging";
	
	public final static String MAG_VRAD_REQUIRED_VERSION = "3.0.76.14";
	public final static String MAG_REQUIRED_VERSION = "3.0P111";

	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingUserAuthorizationDataSourceService(ResolvedArtifactSource resolvedArtifactSource,
		String protocol)
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
	 * @see gov.va.med.imaging.datasource.UserAuthenticationDataSource#getUserToken(java.lang.String)
	 */
	@Override
	public String getUserToken(String applicationName) 
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getUserToken", getDataSourceVersion());
		VistaSession vistaSession = null;
		logger.info("getUserToken(" + applicationName + 
				") TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		try
		{
			vistaSession = getVistaSession();
			
			VistaQuery query = VistaImagingQueryFactory.createMagBrokerSecurityQuery();
			logger.info("Executing query '" + query.getRpcName() + "'.");
			String rtn = vistaSession.call(query);
			
			return VistaCommonUtilities.createFullBrokerTokenStringFromToken(rtn, 
					applicationName, getSite());
		}
		catch(VistaMethodException vmX)
		{
			throw new MethodException(vmX);
		}
		catch(InvalidVistaCredentialsException icX)
		{
			throw new InvalidCredentialsException(icX);
		}
		catch(IOException ioX)
		{
			throw new ConnectionException(ioX);
		}
		finally
		{
			try{vistaSession.close();}
			catch(Throwable t){}
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.UserAuthenticationDataSource#isVersionCompatible()
	 */
	@Override
	public boolean isVersionCompatible() 
	throws SecurityCredentialsExpiredException
	{		
		VistaSession localVistaSession = null;		
		try
		{						
			ImagingSecurityContextType contextType = VistaSession.getImagingSecurityContextType();
			if(contextType == ImagingSecurityContextType.MAG_WINDOWS)
			{
				logger.info("isVersionCompatible searching for version [" + MAG_REQUIRED_VERSION + "], TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
				localVistaSession = getVistaSession();
				return VistaImagingCommonUtilities.isVersionCompatible(MAG_REQUIRED_VERSION, 
						localVistaSession);	
			}
			else
			{
				logger.info("isVersionCompatible searching for version [" + MAG_VRAD_REQUIRED_VERSION + "], TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
				try
				{
				VistaImagingVistaRadCommonUtilities.getVistaSession(getMetadataUrl(), 
					getSite(), MAG_VRAD_REQUIRED_VERSION);
				}
				catch(InvalidVistaVistaRadVersionException ivvrvX)
				{
					// error already displayed (hopefully)
					return false;
				}
			}
			// if here then VRad and version checking already handled
			return true;
			
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
	
	protected VistaSession getVistaSession() 
    throws IOException, ConnectionException, MethodException, SecurityCredentialsExpiredException
    {
		try
		{
			// somewhat kludgy - need to determine if work on behalf of VRad or not since VRad has a 
			// different login mechanism
			ImagingSecurityContextType contextType = VistaSession.getImagingSecurityContextType();
			if(contextType == ImagingSecurityContextType.MAGJ_VISTARAD)
			{
				// version only done if VRad connection, not otherwise
				return VistaImagingVistaRadCommonUtilities.getVistaSession(getMetadataUrl(), 
					getSite(), MAG_VRAD_REQUIRED_VERSION);
			}
			else
			{
				return VistaSession.getOrCreate(getMetadataUrl(), getSite());
			}			
		}
		catch(InvalidVistaVistaRadVersionException ivvrvX)
		{
			// exception already displayed
			throw new ConnectionException(ivvrvX);
		}		
		catch(SecurityCredentialsExpiredException sceX)
		{
			// if a security credentials exception occurs then the user isn't actually locally logged in, this should never happen
    		logger.fatal("Got security credentials expired exception when trying to get a new token! This should NEVER happen because should only try to get a token with local credentials", sceX);
    		throw new MethodException(sceX);
		}
    }
	
	protected String getDataSourceVersion()
	{
		return "1";
	}
}
