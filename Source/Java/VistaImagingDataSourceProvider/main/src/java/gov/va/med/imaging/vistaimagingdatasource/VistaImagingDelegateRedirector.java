/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Apr 29, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.vistaimagingdatasource;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.RoutingOverrideSpi;
import gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi;
import gov.va.med.imaging.datasource.VersionableDataSourceSpi;
import gov.va.med.imaging.exchange.business.ResolvedSite;

/**
 * @author VHAISWBECKEC
 *
 */
public class VistaImagingDelegateRedirector 
implements RoutingOverrideSpi
{
	// This service MUST identify its protocol as null and its
	// protocol version as 0.0 else it will not be found.
	public static final String protocol = null;
	public static final float protocolVersion = 0.0F;
	private Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * 
	 */
	public VistaImagingDelegateRedirector()
    {
	    super();
    }

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.RoutingOverrideSpi#resolve(gov.va.med.imaging.artifactsource.ResolvedArtifactSource, java.lang.Class, java.lang.reflect.Method, java.lang.Object[], gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi)
	 */
	@Override
	public ResolvedArtifactSource resolve(ResolvedArtifactSource naturalDestination,
		Class<? extends VersionableDataSourceSpi> spi, Method method, Object[] parameters,
		SiteResolutionDataSourceSpi siteResolver) throws ConnectionException, MethodException
	{
		// do nothing, except log the call
		// we're just testing this now
		logger.debug("VistaDelegateRedirector.resolve(ResolvedArtifactSource,Class<? extends VersionableDataSourceSpi>,Method,Object[],SiteResolutionDataSourceSpi,Resolver) called.");
		return naturalDestination;
	}

	/*
	 * No Configuration
	 * @see gov.va.med.imaging.datasource.DataSourceSpi#setConfiguration(java.lang.Object)
	 */
	@Override
	public void setConfiguration(Object configuration)
	{
	}

}
