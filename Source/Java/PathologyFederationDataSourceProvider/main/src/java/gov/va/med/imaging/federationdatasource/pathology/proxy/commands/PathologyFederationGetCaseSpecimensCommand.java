/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 22, 2012
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

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.federation.pathology.rest.endpoints.PathologyFederationRestUri;
import gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseSpecimenType;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.pathology.PathologyCaseSpecimen;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.proxy.services.ProxyServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author VHAISWWERFEJ
 *
 */
public class PathologyFederationGetCaseSpecimensCommand
extends AbstractPathologyFederationRestProxyGetCommand<PathologyFederationCaseSpecimenType [], List<PathologyCaseSpecimen>>
{
	private final PathologyCaseURN pathologyCaseUrn;

	/**
	 * @param methodName
	 * @param proxyServices
	 * @param federationConfiguration
	 */
	public PathologyFederationGetCaseSpecimensCommand(
			String dataSourceVersion,
			ProxyServices proxyServices,
			FederationConfiguration federationConfiguration, 
			PathologyCaseURN pathologyCaseUrn)
	{
		super("getCaseSpecimens", dataSourceVersion, proxyServices, federationConfiguration);
		this.pathologyCaseUrn = pathologyCaseUrn;
	}

	public PathologyCaseURN getPathologyCaseUrn()
	{
		return pathologyCaseUrn;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.rest.proxy.commands.AbstractFederationRestProxyCommand#getMethodParametersDescription()
	 */
	@Override
	protected String getMethodParametersDescription()
	{
		return "initiated, to site '" + pathologyCaseUrn.toString() + "'.";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.rest.proxy.commands.AbstractFederationRestProxyCommand#getMethodUri()
	 */
	@Override
	protected String getMethodUri()
	{
		return PathologyFederationRestUri.getCaseSpecimensPath;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.rest.proxy.commands.AbstractFederationRestProxyCommand#getUrlParametersKeyValues()
	 */
	@Override
	protected Map<String, String> getUrlParametersKeyValues()
	{
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{caseId}", getPathologyCaseUrn().toString());
		return urlParameterKeyValues;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.rest.proxy.commands.AbstractFederationRestProxyCommand#getWebServiceResultClass()
	 */
	@Override
	protected Class<PathologyFederationCaseSpecimenType[]> getWebServiceResultClass()
	{
		return PathologyFederationCaseSpecimenType[].class;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.rest.proxy.commands.AbstractFederationRestProxyCommand#translateWebServiceResult(java.lang.Object)
	 */
	@Override
	protected List<PathologyCaseSpecimen> translateWebServiceResult(
			PathologyFederationCaseSpecimenType[] webServiceResult)
	throws MethodException
	{
		return PathologyFederationRestTranslator.translate(webServiceResult);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.rest.proxy.commands.AbstractFederationRestProxyCommand#getTranslatedResultDescription(java.lang.Object)
	 */
	@Override
	protected String getTranslatedResultDescription(
			List<PathologyCaseSpecimen> result)
	{
		return "returned [" + (result == null ? "null" : result.size()) + "] case specimens.";
	}

}
