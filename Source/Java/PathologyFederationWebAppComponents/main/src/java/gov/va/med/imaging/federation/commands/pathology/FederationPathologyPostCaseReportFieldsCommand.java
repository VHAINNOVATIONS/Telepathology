/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 9, 2012
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

import gov.va.med.URNFactory;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.translation.exceptions.TranslationException;
import gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseReportFieldType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationSaveCaseReportResultType;
import gov.va.med.imaging.pathology.PathologyCaseReportField;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.pathology.PathologySaveCaseReportResult;
import gov.va.med.imaging.web.commands.WebserviceInputParameterTransactionContextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author VHAISWWERFEJ
 *
 */
public class FederationPathologyPostCaseReportFieldsCommand
extends AbstractFederationPathologyCommand<PathologySaveCaseReportResult, PathologyFederationSaveCaseReportResultType>
{
	private final String caseId;
	private final PathologyFederationCaseReportFieldType[] caseReportFields;
	private final String interfaceVersion;
	
	public FederationPathologyPostCaseReportFieldsCommand(String caseId, PathologyFederationCaseReportFieldType[] caseReportFields,
			String interfaceVersion)
	{
		super("savePathologyCaseReportFields");
		this.caseId = caseId;
		this.caseReportFields = caseReportFields;
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

	public PathologyFederationCaseReportFieldType[] getCaseReportFields()
	{
		return caseReportFields;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#executeRouterCommand()
	 */
	@Override
	protected PathologySaveCaseReportResult executeRouterCommand() 
	throws MethodException, ConnectionException
	{
		try
		{
			PathologyCaseURN pathologyCaseUrn = URNFactory.create(getCaseId(), 
					PathologyCaseURN.class);
			getTransactionContext().setPatientID(pathologyCaseUrn.getPatientId().toString());
			List<PathologyCaseReportField> fields = PathologyFederationRestTranslator.translateCaseReportFields(getCaseReportFields());
			return getRouter().savePathologyCaseReportFields(pathologyCaseUrn, fields);
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
	protected PathologyFederationSaveCaseReportResultType translateRouterResult(PathologySaveCaseReportResult routerResult)
	throws TranslationException, MethodException
	{
		return PathologyFederationRestTranslator.translate(routerResult);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#getResultClass()
	 */
	@Override
	protected Class<PathologyFederationSaveCaseReportResultType> getResultClass()
	{
		return PathologyFederationSaveCaseReportResultType.class;
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
	public Integer getEntriesReturned(PathologyFederationSaveCaseReportResultType translatedResult)
	{
		return null;
	}

}
