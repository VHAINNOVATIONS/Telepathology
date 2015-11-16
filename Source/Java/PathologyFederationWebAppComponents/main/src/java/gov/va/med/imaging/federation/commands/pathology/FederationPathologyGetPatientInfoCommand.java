package gov.va.med.imaging.federation.commands.pathology;

import gov.va.med.URNFactory;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.translation.exceptions.TranslationException;
import gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationPatientInfoItemType;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.pathology.PathologyPatientInfoItem;
import gov.va.med.imaging.web.commands.WebserviceInputParameterTransactionContextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FederationPathologyGetPatientInfoCommand 
extends AbstractFederationPathologyCommand<List<PathologyPatientInfoItem>, PathologyFederationPatientInfoItemType[]>
{

	private final String caseId;
	private final String interfaceVersion;
	
	public FederationPathologyGetPatientInfoCommand(String caseId,
			String interfaceVersion)
	{
		super("getPathologyPatientInfo");
		this.caseId = caseId;
		this.interfaceVersion = interfaceVersion;
	}

	public String getCaseId()
	{
		return caseId;
	}

	public String getInterfaceVersion()
	{
		return interfaceVersion;
	}

	@Override
	protected List<PathologyPatientInfoItem> executeRouterCommand()
	throws MethodException, ConnectionException 
	{
		try
		{
			PathologyCaseURN pathologyCaseUrn = URNFactory.create(getCaseId(), PathologyCaseURN.class);
			return getRouter().getPatientInfo(pathologyCaseUrn);
		}
		catch(URNFormatException urnfX)
		{
			throw new MethodException(urnfX);		
		}
	}

	@Override
	protected String getMethodParameterValuesString() 
	{
		return "for case '" + getCaseId() + "'.";
	}

	@Override
	protected PathologyFederationPatientInfoItemType[] translateRouterResult(
			List<PathologyPatientInfoItem> routerResult)	
	throws TranslationException, MethodException 
	{
		return PathologyFederationRestTranslator.translatePatientInfo(routerResult);
	}

	@Override
	protected Class<PathologyFederationPatientInfoItemType[]> getResultClass() {
		return PathologyFederationPatientInfoItemType[].class;
	}

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

	@Override
	public Integer getEntriesReturned(
			PathologyFederationPatientInfoItemType[] translatedResult) 
	{
		return translatedResult == null ? 0 : translatedResult.length;
	}
}
