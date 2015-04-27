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

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.federation.pathology.rest.endpoints.PathologyFederationRestUri;
import gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.rest.types.RestCoreTranslator;
import gov.va.med.imaging.rest.types.RestStringArrayType;
import gov.va.med.imaging.url.vista.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author VHAISWWERFEJ
 *
 */
public class PathologyFederationGetSiteTemplateCommand
extends AbstractPathologyFederationRestProxyGetCommand<RestStringArrayType, List<String>>
{
	private final RoutingToken globalRoutingToken;
	private final List<String> apSections;	

	/**
	 * @param methodName
	 * @param proxyServices
	 * @param federationConfiguration
	 */
	public PathologyFederationGetSiteTemplateCommand(
			String dataSourceVersion,
			ProxyServices proxyServices,
			FederationConfiguration federationConfiguration,
			RoutingToken globalRoutingToken, 
			List<String> apSections)
	{
		super("getSiteTemplate", dataSourceVersion, proxyServices, federationConfiguration);
		this.apSections = apSections;
		this.globalRoutingToken = globalRoutingToken;		
	}

	public RoutingToken getGlobalRoutingToken()
	{
		return globalRoutingToken;
	}

	public List<String> getApSections()
	{
		return apSections;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.pathology.proxy.commands.AbstractFederationRestProxyCommand#getMethodParametersDescription()
	 */
	@Override
	protected String getMethodParametersDescription()
	{
		return "for AP Sections '" + PathologyFederationRestTranslator.translateStringListToDelimitedString(getApSections(), StringUtils.CARET) + "' to site '" + getGlobalRoutingToken().toRoutingTokenString() + "'.";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.pathology.proxy.commands.AbstractFederationRestProxyCommand#getMethodUri()
	 */
	@Override
	protected String getMethodUri()
	{
		return PathologyFederationRestUri.getTemplatesPath;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.pathology.proxy.commands.AbstractFederationRestProxyCommand#getUrlParametersKeyValues()
	 */
	@Override
	protected Map<String, String> getUrlParametersKeyValues()
	{
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{routingToken}", getGlobalRoutingToken().toRoutingTokenString());
		urlParameterKeyValues.put("{apSections}", PathologyFederationRestTranslator.translateStringListToDelimitedString(getApSections(), StringUtils.CARET));
		return urlParameterKeyValues;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.pathology.proxy.commands.AbstractFederationRestProxyCommand#getWebServiceResultClass()
	 */
	@Override
	protected Class<RestStringArrayType> getWebServiceResultClass()
	{
		return RestStringArrayType.class;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.pathology.proxy.commands.AbstractFederationRestProxyCommand#translateWebServiceResult(java.lang.Object)
	 */
	@Override
	protected List<String> translateWebServiceResult(RestStringArrayType webServiceResult)
	throws MethodException
	{
		return RestCoreTranslator.translate(webServiceResult);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.pathology.proxy.commands.AbstractFederationRestProxyCommand#getTranslatedResultDescription(java.lang.Object)
	 */
	@Override
	protected String getTranslatedResultDescription(List<String> result)
	{
		return "returned [" + (result == null ? "null" : result.size()) + "] templates.";
	}

}
