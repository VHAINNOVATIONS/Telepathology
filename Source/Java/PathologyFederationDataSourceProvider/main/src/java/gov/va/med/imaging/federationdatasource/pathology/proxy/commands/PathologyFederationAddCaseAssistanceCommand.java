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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.federation.pathology.rest.endpoints.PathologyFederationRestUri;
import gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseUpdateAttributeResultType;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.pathology.PathologyCaseUpdateAttributeResult;
import gov.va.med.imaging.pathology.enums.PathologyCaseAssistance;
import gov.va.med.imaging.proxy.services.ProxyServices;

/**
 * @author VHAISWWERFEJ
 *
 */
public class PathologyFederationAddCaseAssistanceCommand
extends AbstractPathologyFederationRestProxyGetCommand<PathologyFederationCaseUpdateAttributeResultType, PathologyCaseUpdateAttributeResult>
{
	private final PathologyCaseURN pathologyCaseUrn;
	private final PathologyCaseAssistance assistanceType;
	private final String stationNumber;
	
	private final static Logger logger = Logger.getLogger(PathologyFederationAddCaseAssistanceCommand.class);
	
	protected Logger getLogger()
	{
		return logger;
	}
	
	public PathologyFederationAddCaseAssistanceCommand(String dataSourceVersion,
			ProxyServices proxyServices,
			FederationConfiguration federationConfiguration,
			PathologyCaseURN pathologyCaseUrn,
			PathologyCaseAssistance assistanceType, String stationNumber)
	{
		super("addCaseAssistance", dataSourceVersion, proxyServices, federationConfiguration);
		this.pathologyCaseUrn = pathologyCaseUrn;
		this.assistanceType = assistanceType;
		this.stationNumber = stationNumber;
	}
	
	public PathologyCaseURN getPathologyCaseUrn()
	{
		return pathologyCaseUrn;
	}

	public PathologyCaseAssistance getAssistanceType()
	{
		return assistanceType;
	}

	public String getStationNumber()
	{
		return stationNumber;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.pathology.proxy.commands.AbstractFederationRestProxyCommand#getMethodParametersDescription()
	 */
	@Override
	protected String getMethodParametersDescription()
	{
		return " for case '" + getPathologyCaseUrn().toString() + ", assistance type '" + assistanceType + "'.";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.pathology.proxy.commands.AbstractFederationRestProxyCommand#getMethodUri()
	 */
	@Override
	protected String getMethodUri()
	{
		return PathologyFederationRestUri.addCaseAssistancePath;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.pathology.proxy.commands.AbstractFederationRestProxyCommand#getUrlParametersKeyValues()
	 */
	@Override
	protected Map<String, String> getUrlParametersKeyValues()
	{
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{caseId}", getPathologyCaseUrn().toString());
		urlParameterKeyValues.put("{assistanceType}", PathologyFederationRestTranslator.translate(getAssistanceType()).name());
		urlParameterKeyValues.put("{stationNumber}", getStationNumber());
		return urlParameterKeyValues;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.pathology.proxy.commands.AbstractFederationRestProxyCommand#getWebServiceResultClass()
	 */
	@Override
	protected Class<PathologyFederationCaseUpdateAttributeResultType> getWebServiceResultClass()
	{
		return PathologyFederationCaseUpdateAttributeResultType.class;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.pathology.proxy.commands.AbstractFederationRestProxyCommand#translateWebServiceResult(java.lang.Object)
	 */
	@Override
	protected PathologyCaseUpdateAttributeResult translateWebServiceResult(
			PathologyFederationCaseUpdateAttributeResultType webServiceResult)
			throws MethodException
	{
		return PathologyFederationRestTranslator.translate(webServiceResult);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.pathology.proxy.commands.AbstractFederationRestProxyCommand#getTranslatedResultDescription(java.lang.Object)
	 */
	@Override
	protected String getTranslatedResultDescription(
			PathologyCaseUpdateAttributeResult result)
	{
		return "returned [" + (result == null ? "null" : result.isSuccess()) + "] result.";
	}

}
