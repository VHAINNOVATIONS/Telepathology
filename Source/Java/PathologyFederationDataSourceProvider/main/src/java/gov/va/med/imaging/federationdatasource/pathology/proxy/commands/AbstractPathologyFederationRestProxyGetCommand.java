/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 21, 2012
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
package gov.va.med.imaging.federationdatasource.pathology.proxy.commands;

import gov.va.med.imaging.federation.pathology.rest.endpoints.PathologyFederationRestUri;
import gov.va.med.imaging.federation.rest.proxy.commands.AbstractFederationRestProxyGetCommand;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.federationdatasource.pathology.PathologyFederationProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;

/**
 * @author VHAISWWERFEJ
 *
 */
public abstract class AbstractPathologyFederationRestProxyGetCommand<R, T extends Object>
extends AbstractFederationRestProxyGetCommand<R, T>
{
	private final String dataSourceVersion;

	/**
	 * @param proxyServices
	 * @param federationConfiguration
	 */
	public AbstractPathologyFederationRestProxyGetCommand(String methodName,
			String dataSourceVersion,
			ProxyServices proxyServices,
			FederationConfiguration federationConfiguration)
	{
		super(methodName, proxyServices, federationConfiguration);
		this.dataSourceVersion = dataSourceVersion;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestImageProxy#getRestServicePath()
	 */
	@Override
	protected String getRestServicePath()
	{
		return PathologyFederationRestUri.pathologyServicePath;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestImageProxy#getProxyServiceType()
	 */
	@Override
	protected ProxyServiceType getProxyServiceType()
	{
		return new PathologyFederationProxyServiceType();
		//return ProxyServiceType.pathology;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.proxy.AbstractFederationProxy#getDataSourceVersion()
	 */
	@Override
	protected String getDataSourceVersion()
	{
		return dataSourceVersion;
	}

}
