/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 18, 2011
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

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityException;
import gov.va.med.imaging.datasource.AbstractVersionableDataSource;
import gov.va.med.imaging.datasource.UserDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exchange.business.ApplicationTimeoutParameters;
import gov.va.med.imaging.exchange.business.Division;
import gov.va.med.imaging.exchange.business.ElectronicSignatureResult;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.User;
import gov.va.med.imaging.exchange.business.UserInformation;
import gov.va.med.imaging.protocol.vista.VistaImagingTranslator;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.VistaCommonQueryFactory;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;

/**
 * @author vhaiswwerfej
 *
 */
public class VistaImagingUserDataSourceService
extends AbstractVersionableDataSource
implements UserDataSourceSpi
{
	
	/* =====================================================================
	 * Instance fields and methods
	 * ===================================================================== */
	private final static Logger logger = 
		Logger.getLogger(VistaImagingUserDataSourceService.class);
	
	public final static String SUPPORTED_PROTOCOL = "vistaimaging";
	public final static String MAG_REQUIRED_VERSION = "3.0P45";
	
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingUserDataSourceService(ResolvedArtifactSource resolvedArtifactSource,
		String protocol)
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
	
	@Override
	public List<String> getUserKeys(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getUserKeys", getDataSourceVersion());
		VistaSession vistaSession = null;
		logger.info("getUserKeys TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		try
		{
			vistaSession = getVistaSession();
			return getUserKeys(vistaSession);			
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
	
	private List<String> getUserKeys(VistaSession vistaSession) 
	throws IOException, InvalidVistaCredentialsException, VistaMethodException
	{
		VistaQuery query = VistaImagingQueryFactory.createGetUserKeysQuery();
		String rtn = vistaSession.call(query);
		return VistaImagingTranslator.translateUserKeys(rtn);
	}
	
	@Override
	public List<Division> getDivisionList(String accessCode, RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getDivisions", getDataSourceVersion());
		VistaSession vistaSession = null;
		logger.info("getDivisions TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		try
		{
			vistaSession = getVistaSession();
			
			VistaQuery query = VistaImagingQueryFactory.createGetDivisionsQuery(accessCode);
			logger.info("Executing query '" + query.getRpcName() + "'.");
			String rtn = vistaSession.call(query);
			return VistaImagingTranslator.translateDivisions(rtn);			
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
	
	protected String getDataSourceVersion()
	{
		return "1";
	}
	
	protected String getRequiredVersion()
	{
		return MAG_REQUIRED_VERSION;
	}
	
	protected VistaSession getVistaSession() 
    throws IOException, ConnectionException, MethodException, SecurityCredentialsExpiredException
    {
	    return VistaSession.getOrCreate(getMetadataUrl(), getSite());
    }	

	@Override
	public UserInformation getUserInformation(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getUserInformation", getDataSourceVersion());
		VistaSession vistaSession = null;
		logger.info("getUserInformation TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		try
		{
			vistaSession = getVistaSession();
			VistaQuery query = VistaCommonQueryFactory.createGetUserInformationQuery();			
			String rtn = vistaSession.call(query);
			User user = VistaImagingTranslator.translateUser(rtn);
			List<String> keys = getUserKeys(vistaSession);
			// this version cannot call the RPC to determine if the user can create annotations
			return new UserInformation(user, keys, userCanAnnotate(vistaSession));			
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
	
	protected boolean userCanAnnotate(VistaSession vistaSession)
	throws IOException, InvalidVistaCredentialsException, VistaMethodException, MethodException
	{
		return false;
	}

	@Override
	public boolean isVersionCompatible() 
	throws SecurityException
	{
		logger.info("isVersionCompatible searching for version [" + getRequiredVersion() + "], TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession localVistaSession = null;		
		try
		{			
			localVistaSession = getVistaSession();
			return VistaImagingCommonUtilities.isVersionCompatible(getRequiredVersion(), localVistaSession);
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

	@Override
	public ElectronicSignatureResult verifyElectronicSignature(RoutingToken globalRoutingToken, String electronicSignature)
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("verifyElectronicSignature", getDataSourceVersion());
		VistaSession vistaSession = null;
		logger.info("verifyElectronicSignature TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		try
		{
			vistaSession = getVistaSession();
			VistaQuery vistaQuery = VistaImagingQueryFactory.createVerifyElectronicSignatureQuery(electronicSignature);
			
			String rtn = vistaSession.call(vistaQuery);
			return VistaImagingTranslator.translateElectronicSignature(rtn);
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

	@Override
	public ApplicationTimeoutParameters getApplicationTimeoutParameters(String siteId, String applicationName) 
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getApplicationTimeoutParameters", getDataSourceVersion());
		VistaSession vistaSession = null;
		logger.info("getApplicationTimeoutParameters TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		try
		{
			vistaSession = getVistaSession();
			VistaQuery vistaQuery = VistaImagingQueryFactory.createGetApplicationTimeoutParameters(siteId, applicationName);
			
			String rtn = vistaSession.call(vistaQuery);
			return VistaImagingTranslator.translateApplicationTimeoutParameters(rtn);
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
}
