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
package gov.va.med.imaging.vistadatasource;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.AbstractVersionableDataSource;
import gov.va.med.imaging.datasource.UserAuthorizationDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * This SPI creates a user token from a site that does NOT have VistA Imaging installed.
 * 
 * @author vhaiswwerfej
 *
 */
public class VistaUserAuthorizationDataSourceProvider
extends AbstractVersionableDataSource
implements UserAuthorizationDataSourceSpi
{
	private Logger logger = Logger.getLogger(this.getClass());	
	
	public final static String SUPPORTED_PROTOCOL = "vista";
	

    /**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaUserAuthorizationDataSourceProvider(ResolvedArtifactSource resolvedArtifactSource, String protocol)
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
    throws IOException, ConnectionException, MethodException
    {
    	try
    	{
    		return VistaSession.getOrCreate(getMetadataUrl(), getSite());
    	}
    	catch(SecurityCredentialsExpiredException sceX)
    	{
    		// if a security credentials exception occurs then the user isn't actually locally logged in, this should never happen
    		logger.fatal("Got security credentials expired exception when trying to get a new token! This should NEVER happen because should only try to get a token with local credentials", sceX);
    		throw new MethodException(sceX);
    	}
    }
    
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.UserAuthenticationDataSource#getUserToken()
	 */
	@Override
	public String getUserToken(String applicationName) 
	throws MethodException, ConnectionException 
	{
		VistaSession localVistaSession = null;
        try
        {
        	localVistaSession = getVistaSession();
        	VistaQuery query = VistaQueryFactory.createGetBrokerTokenQuery();
        	String xwbToken = localVistaSession.call(query);
        	return VistaCommonUtilities.createFullBrokerTokenStringFromToken(xwbToken, 
        			applicationName, getSite());
        }
        catch(VistaMethodException vmX)
        {
        	logger.error("Exception making RPC call to VistA", vmX);
        	throw new MethodException(vmX);
        }
        catch (InvalidVistaCredentialsException e)
        {
        	logger.error("Exception making RPC call to VistA", e);
    		throw new InvalidCredentialsException(e);
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
	 * @see gov.va.med.imaging.datasource.UserAuthenticationDataSource#isVersionCompatible()
	 */
	@Override
	public boolean isVersionCompatible() 
	{
		// for this implementation we are not using any MAG rpc calls, just assume version is ok
		return true;
	}
}
