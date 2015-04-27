/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 6, 2012
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
import java.util.Map;

import gov.va.med.RoutingToken;
import gov.va.med.URNFactory;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.translation.exceptions.TranslationException;
import gov.va.med.imaging.federation.rest.translator.FederationRestTranslator;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.rest.types.RestStringType;
import gov.va.med.imaging.web.commands.WebserviceInputParameterTransactionContextField;

/**
 * @author VHAISWWERFEJ
 *
 */
public class FederationPathologyPostCopyCaseCommand
extends AbstractFederationPathologyCommand<PathologyCaseURN, RestStringType>
{
	private final String routingTokenString;
	private final String caseId;
	private final String interfaceVersion;

	/**
	 * @param methodName
	 */
	public FederationPathologyPostCopyCaseCommand(String routingTokenString, String caseId, 
			String interfaceVersion)
	{
		super("pathologycopyCase");
		this.routingTokenString = routingTokenString;
		this.caseId = caseId;
		this.interfaceVersion = interfaceVersion;
	}

	public String getInterfaceVersion()
	{
		return interfaceVersion;
	}

	public String getCaseId()
	{
		return caseId;
	}

	public String getRoutingTokenString()
	{
		return routingTokenString;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#executeRouterCommand()
	 */
	@Override
	protected PathologyCaseURN executeRouterCommand() 
	throws MethodException, ConnectionException
	{
		try
		{
			RoutingToken routingToken = 
				FederationRestTranslator.translateRoutingToken(getRoutingTokenString());
			PathologyCaseURN pathologyCaseUrn = URNFactory.create(getCaseId(), PathologyCaseURN.class);
			getTransactionContext().setPatientID(pathologyCaseUrn.getPatientId().toString());			
			return getRouter().copyPathologyCase(routingToken, pathologyCaseUrn);
		}
		catch(RoutingTokenFormatException rtfX)
		{
			throw new MethodException(rtfX);
		}
		catch(URNFormatException rtfX)
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
		return "for case '" + getCaseId() + "'.";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#translateRouterResult(java.lang.Object)
	 */
	@Override
	protected RestStringType translateRouterResult(PathologyCaseURN routerResult)
	throws TranslationException, MethodException
	{
		return new RestStringType(routerResult.toString());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#getResultClass()
	 */
	@Override
	protected Class<RestStringType> getResultClass()
	{
		return RestStringType.class;
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
		transactionContextFields.put(WebserviceInputParameterTransactionContextField.urn, getCaseId());
		transactionContextFields.put(WebserviceInputParameterTransactionContextField.queryFilter, transactionContextNaValue);

		return transactionContextFields;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#getEntriesReturned(java.lang.Object)
	 */
	@Override
	public Integer getEntriesReturned(RestStringType translatedResult)
	{
		return null;
	}

}
