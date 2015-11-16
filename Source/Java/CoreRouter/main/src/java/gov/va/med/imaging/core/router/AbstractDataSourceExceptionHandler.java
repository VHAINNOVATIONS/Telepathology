/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 29, 2009
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
package gov.va.med.imaging.core.router;

import gov.va.med.imaging.core.interfaces.DataSourceExceptionHandler;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.UserAuthorizationDataSourceSpi;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.net.URL;
import java.util.List;

/**
 * Abstract implementation of the DataSourceExceptionHandler.  This abstract class is used to handle
 * exceptions that occur from datasources, in this case specifically the SecurityCredentialsExpiredException.
 * 
 * This class is usually extended by AbstractDataSourceCommandImpl but in some cases is extended by an actual command implementation.
 * This is necessary if the command does not actually get data from a data source but does create a datasource. Since creating
 * a data source might call isVersionCompatible() - handling for SecurityCredentialsExpiredException exceptions is 
 * necessary and is handled in this class without needing to implement the AbstractDataSourceCommandImpl methods. 
 * 
 * 
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractDataSourceExceptionHandler<R> 
extends AbstractCommandImpl<R>
implements DataSourceExceptionHandler 
{
	private static final long serialVersionUID = -7129248073928795610L;

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.DataSourceExceptionHandler#handleException(java.lang.Exception)
	 */
	@Override
	public boolean handleException(Exception ex) 
	{
		if(ex.getClass() == SecurityCredentialsExpiredException.class)
		{
			getLogger().warn("handling exception '" + ex.getClass().getSimpleName() + "'.");
			try
			{
				generateNewSecurityToken();
				return true;
			}
			catch(MethodException mX)
			{
				getLogger().error("Error generating new token from expierd token", mX);
				return false;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.DataSourceExceptionHandler#isExceptionHandled(java.lang.Exception)
	 */
	@Override
	public boolean isExceptionHandled(Exception ex) 
	{
		if(ex.getClass() == SecurityCredentialsExpiredException.class)
			return true;
		return false;
	}
	
	 /**
     * Requests a new security token from a local provider and puts the token into the context.  If the token cannot
     * be generated, a MethodException is thrown and the data source should stop.
     * @throws MethodException
     */
    protected void generateNewSecurityToken()
    throws MethodException
    {
    	Exception lastException = null;
    	TransactionContext transactionContext = TransactionContextFactory.get();
    	String securityTokenApplicatioName = transactionContext.getBrokerSecurityApplicationName();
    	if((securityTokenApplicatioName == null) || (securityTokenApplicatioName.length() <= 0))
    	{
    		String msg = "No security application defined to generate a new token, cannot create a token with security application.";
    		getLogger().error(msg);
    		throw new MethodException(msg);
    	}
    	ResolvedSite site = getCommandContext().getLocalSite();
    	if(site == null)
    	{
    		String msg = "No local Site configured to generate a new token, cannot create a new token.";
    		getLogger().error(msg);
    		throw new MethodException(msg);
    	}
    	getLogger().info("Retrieving new security token from site '" + site.getSite().getSiteNumber() + "' for application '" + securityTokenApplicatioName + "'.");
    	List<URL> resolvedUrls = site.getMetadataUrls();
    	for(URL url : resolvedUrls )
		{
    		try
    		{
    			// userAuthenticationSpi should never throw a SecurityCredentialsExpiredException exception, so special
    			// handling of it is not necessary.  If this exception occurs, then just give up, not going to be able 
    			// to get a new token.
    			UserAuthorizationDataSourceSpi userAuthenticationSpi = 
    				getProvider().createVersionableDataSource(UserAuthorizationDataSourceSpi.class, site, url.getProtocol());
    			String token = userAuthenticationSpi.getUserToken(securityTokenApplicatioName);
    			transactionContext.setBrokerSecurityToken(token);
    			getLogger().debug("Updated security token with new value '" + token + "'.");
    			return;
    		}
    		catch(ConnectionException cX)
    		{
    			getLogger().error("Failed to contact site '" + site.getSite().getSiteNumber() + "' with URL '" + url.toExternalForm() + "' to retrieve token.");
    			lastException = cX;
    		}
		}
    	getLogger().warn("Was not able to find a provider to generate a new token, cannot continue");
    	if(lastException != null)
    		throw new MethodException(lastException);
    	else
    		throw new MethodException("Unable to find provider for new security token.");
    }
    
    /**
     * Determines if the transaction context contains enough information to generate a new security token.
     * @return
     */
    protected boolean canGenerateNewToken()
	{
    	TransactionContext context = TransactionContextFactory.get(); 
		Boolean value = context.isTokenLocallyGenerated();
		if(value == null)
			return false;
		// if there is no current token this indicates the realm that logged the user in could not create a token
		// even though it should have.  in this case it is unlikely a new token can be created so no point in trying
		String currentToken = context.getBrokerSecurityToken();
		if((currentToken == null) || (currentToken.length() <= 0))
			return false;
		return value.booleanValue();
	}
}
