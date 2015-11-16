/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 24, 2012
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

import java.io.IOException;

import org.apache.log4j.Logger;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityException;
import gov.va.med.imaging.datasource.AbstractVersionableDataSource;
import gov.va.med.imaging.datasource.SiteDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.WelcomeMessage;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistadatasource.session.VistaSession;

/**
 * This implementation is for site specific methods from a site without VistA Imaging installed
 * 
 * @author VHAISWWERFEJ
 *
 */
public class VistaSiteDataSourceService
extends AbstractVersionableDataSource
implements SiteDataSourceSpi
{
	
	/* =====================================================================
	 * Instance fields and methods
	 * ===================================================================== */
	private final static Logger logger = 
		Logger.getLogger(VistaSiteDataSourceService.class);
	
	public final static String SUPPORTED_PROTOCOL = "vista";
	
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaSiteDataSourceService(ResolvedArtifactSource resolvedArtifactSource,
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
	public WelcomeMessage getWelcomeMessage(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getWelcomeMessage", getDataSourceVersion());
		logger.info("getWelcomeMessage TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		try
		{
			String welcomeMessage = VistaSession.getWelcomeMessage(getMetadataUrl(), getSite());
			return new WelcomeMessage(welcomeMessage);
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
	}
	
	@Override
	public boolean isVersionCompatible() 
	throws SecurityException
	{
		
		// This SPI contains a method (getWelcomeMessage) that is called from an unauthenticated connection.
		// This method should be available to all sites
		return true;
	}
	
	protected String getDataSourceVersion()
	{
		return "1";
	}
}
