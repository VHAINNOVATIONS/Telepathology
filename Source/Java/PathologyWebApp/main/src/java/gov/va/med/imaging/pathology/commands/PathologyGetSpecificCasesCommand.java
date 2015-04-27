/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 25, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
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
package gov.va.med.imaging.pathology.commands;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.translation.exceptions.TranslationException;
import gov.va.med.imaging.pathology.PathologyCase;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.pathology.rest.translator.PathologyRestTranslator;
import gov.va.med.imaging.pathology.rest.types.PathologyCasesType;
import gov.va.med.imaging.rest.types.RestStringArrayType;
import gov.va.med.imaging.web.commands.WebserviceInputParameterTransactionContextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vhaiswwerfej
 *
 */
public class PathologyGetSpecificCasesCommand
extends AbstractPathologyCommand<List<PathologyCase>, PathologyCasesType>
{
	private final RestStringArrayType cases;

	/**
	 * @param methodName
	 */
	public PathologyGetSpecificCasesCommand(RestStringArrayType cases)
	{
		super("getSpecificCases");
		this.cases = cases;
	}

	public RestStringArrayType getCases()
	{
		return cases;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#executeRouterCommand()
	 */
	@Override
	protected List<PathologyCase> executeRouterCommand()
	throws MethodException, ConnectionException
	{
		List<PathologyCaseURN> caseUrns = PathologyRestTranslator.translateCaseIds(getCases());
		return getRouter().getSpecificCases(caseUrns);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#getMethodParameterValuesString()
	 */
	@Override
	protected String getMethodParameterValuesString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("For cases [");
		String prefix = "";
		for(String caseId : getCases().getValue())
		{
			sb.append(prefix);
			sb.append(caseId);
		}
		sb.append("]");
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#translateRouterResult(java.lang.Object)
	 */
	@Override
	protected PathologyCasesType translateRouterResult(
			List<PathologyCase> routerResult) 
	throws TranslationException, MethodException
	{
		return PathologyRestTranslator.translateCases(routerResult);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#getResultClass()
	 */
	@Override
	protected Class<PathologyCasesType> getResultClass()
	{
		return PathologyCasesType.class;
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
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#getEntriesReturned(java.lang.Object)
	 */
	@Override
	public Integer getEntriesReturned(PathologyCasesType translatedResult)
	{
		return translatedResult == null ? 0 : translatedResult.getPathologyCase().length;
	}
}
