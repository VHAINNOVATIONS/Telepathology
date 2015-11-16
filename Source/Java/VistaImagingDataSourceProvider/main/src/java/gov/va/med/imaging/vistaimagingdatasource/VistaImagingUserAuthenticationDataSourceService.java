/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 21, 2012
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

import java.io.IOException;

import org.apache.log4j.Logger;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.CredentialsExpiredException;
import gov.va.med.imaging.core.interfaces.exceptions.InvalidUserCredentialsException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.UserAuthenticationSpi;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistadatasource.session.VistaSession;

/**
 * @author VHAISWWERFEJ
 *
 */
public class VistaImagingUserAuthenticationDataSourceService
extends VistaImagingUserAuthorizationDataSourceService
implements UserAuthenticationSpi
{

	private Logger logger = Logger.getLogger(VistaImagingUserAuthenticationDataSourceService.class);
	
	public final static String SUPPORTED_PROTOCOL = "vistaimaging";
	
    /**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingUserAuthenticationDataSourceService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
		if(! (resolvedArtifactSource instanceof ResolvedSite) )
			throw new UnsupportedOperationException("The artifact source must be an instance of ResolvedSite and it is a '" + resolvedArtifactSource.getClass().getSimpleName() + "'.");
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.UserAuthenticationDataSource#isVersionCompatible()
	 */
	@Override
	public boolean isVersionCompatible() 
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.UserAuthenticationSpi#authenticateUser(java.lang.String, java.lang.String)
	 */
	@Override
	public void authenticateUser()
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("authenticateUser", getDataSourceVersion());
		logger.info("authenticateUser TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		try
		{
			VistaSession.authenticateUser(getMetadataUrl(), getSite());
			TransactionContext transactionContext = TransactionContextFactory.get(); 
			String applicationName = transactionContext.getBrokerSecurityApplicationName();
			if(applicationName == null || applicationName.length() <= 0)
			{
				logger.warn("No application name specified, cannot generate BSE token");
			}
			else
			{
				String bseToken = 
					getUserToken(transactionContext.getBrokerSecurityApplicationName());
				transactionContext.setBrokerSecurityToken(bseToken);
			}	
		}
		catch(InvalidCredentialsException icX)
		{			
			// convert from a ConnectionException base to a MethodException (so that it is fatal and no other path is attempted)
			
			// kludgey way to get proper exception thrown so it is available to users
			if(icX.getMessage() != null && icX.getMessage().contains("Expired Verify Code"))
			{
				throw new CredentialsExpiredException(icX.getMessage(), icX);
			}
			else
			{			
				throw new InvalidUserCredentialsException(icX.getMessage(), icX);
			}
		}
		catch(IOException ioX)
		{
			throw new ConnectionException(ioX);
		}
	}
	
	protected String getDataSourceVersion()
	{
		return "1";
	}
}
