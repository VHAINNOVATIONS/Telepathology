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
package gov.va.med.imaging.federation.commands.pathology;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.va.med.RoutingToken;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.translation.exceptions.TranslationException;
import gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseType;
import gov.va.med.imaging.federation.rest.translator.FederationRestTranslator;
import gov.va.med.imaging.pathology.PathologyCase;
import gov.va.med.imaging.web.commands.WebserviceInputParameterTransactionContextField;

/**
 * @author VHAISWWERFEJ
 *
 */
public class FederationPathologyGetCasesCommand
extends AbstractFederationPathologyCommand<List<PathologyCase>, PathologyFederationCaseType[]>
{
	private final String routingTokenString;
	private final boolean released;
	private final int days;
	private final String requestingSiteId;
	private final String interfaceVersion;

	/**
	 * @param methodName
	 */
	public FederationPathologyGetCasesCommand(String routingTokenString, 
			boolean released, int days, String requestingSiteId, String interfaceVersion)
	{
		super(released == true ? "getPathologyReleasedCases" : "getPathologyUnreleasedCases");
		this.routingTokenString = routingTokenString;
		this.released = released;
		this.interfaceVersion = interfaceVersion;
		this.requestingSiteId = requestingSiteId;
		this.days = days;
	}
	
	public FederationPathologyGetCasesCommand(String routingTokenString, 
			boolean released, int days, String interfaceVersion)
	{
		this(routingTokenString, released, days, null, interfaceVersion);
	}

	public String getRequestingSiteId()
	{
		return requestingSiteId;
	}

	public String getRoutingTokenString()
	{
		return routingTokenString;
	}

	public boolean isReleased()
	{
		return released;
	}

	public int getDays()
	{
		return days;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#executeRouterCommand()
	 */
	@Override
	protected List<PathologyCase> executeRouterCommand()
	throws MethodException, ConnectionException
	{
		try
		{
			RoutingToken routingToken = 
				FederationRestTranslator.translateRoutingToken(getRoutingTokenString());
			if(getRequestingSiteId() == null)
				return getRouter().getPathologyCases(routingToken, isReleased(), getDays());
			else				
				return getRouter().getPathologyCases(routingToken, isReleased(), getDays(), getRequestingSiteId());
		}
		catch(RoutingTokenFormatException rtfX)
		{
			throw new MethodException(rtfX);
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#getMethodParameterValuesString()
	 */
	@Override
	protected String getMethodParameterValuesString()
	{
		return (released == true ? "released" : "unreleased") + " cases from " + getRoutingTokenString() + " for " + getDays() + " days" + (requestingSiteId == null ? "" : " for requesting site '" + requestingSiteId + "'");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#translateRouterResult(java.lang.Object)
	 */
	@Override
	protected PathologyFederationCaseType[] translateRouterResult(
			List<PathologyCase> routerResult) 
	throws TranslationException, MethodException
	{
		return PathologyFederationRestTranslator.translateCases(routerResult);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#getResultClass()
	 */
	@Override
	protected Class<PathologyFederationCaseType[]> getResultClass()
	{
		return PathologyFederationCaseType[].class;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#getTransactionContextFields()
	 */
	@Override
	protected Map<WebserviceInputParameterTransactionContextField, String> getTransactionContextFields()
	{
		Map<WebserviceInputParameterTransactionContextField, String> transactionContextFields = 
			new HashMap<WebserviceInputParameterTransactionContextField, String>();
		
		transactionContextFields.put(WebserviceInputParameterTransactionContextField.quality, transactionContextNaValue);
		transactionContextFields.put(WebserviceInputParameterTransactionContextField.urn, transactionContextNaValue);
		transactionContextFields.put(WebserviceInputParameterTransactionContextField.queryFilter, transactionContextNaValue);

		return transactionContextFields;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#getInterfaceVersion()
	 */
	@Override
	public String getInterfaceVersion()
	{
		return interfaceVersion;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#getEntriesReturned(java.lang.Object)
	 */
	@Override
	public Integer getEntriesReturned(
			PathologyFederationCaseType[] translatedResult)
	{
		return translatedResult == null ? 0 : translatedResult.length;
	}

}
