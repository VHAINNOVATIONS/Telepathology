/**
 * 
 */
package gov.va.med.imaging.federation.commands.pathology;

import gov.va.med.URNFactory;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.translation.exceptions.TranslationException;
import gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseSlideType;
import gov.va.med.imaging.pathology.PathologyCaseSlide;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.web.commands.WebserviceInputParameterTransactionContextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Julian
 *
 */
public class FederationPathologyGetCaseSlidesCommand
extends AbstractFederationPathologyCommand<List<PathologyCaseSlide>, PathologyFederationCaseSlideType[]>
{
	private final String caseId;
	private final String interfaceVersion;

	/**
	 * @param methodName
	 */
	public FederationPathologyGetCaseSlidesCommand(String caseId, String interfaceVersion)
	{
		super("getPathologyCaseSlides");
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

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#executeRouterCommand()
	 */
	@Override
	protected List<PathologyCaseSlide> executeRouterCommand()
	throws MethodException, ConnectionException
	{
		try
		{
			PathologyCaseURN pathologyCaseUrn = URNFactory.create(getCaseId(), PathologyCaseURN.class);
			getTransactionContext().setPatientID(pathologyCaseUrn.getPatientId().toString());
			return getRouter().getPathologyCaseSlides(pathologyCaseUrn);
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
	protected PathologyFederationCaseSlideType[] translateRouterResult(
			List<PathologyCaseSlide> routerResult)
	throws TranslationException, MethodException
	{
		return PathologyFederationRestTranslator.translateCaseSlides(routerResult);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#getResultClass()
	 */
	@Override
	protected Class<PathologyFederationCaseSlideType[]> getResultClass()
	{
		return PathologyFederationCaseSlideType[].class;
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
	public Integer getEntriesReturned(PathologyFederationCaseSlideType[] translatedResult)
	{
		return translatedResult == null ? 0 : translatedResult.length;
	}

}