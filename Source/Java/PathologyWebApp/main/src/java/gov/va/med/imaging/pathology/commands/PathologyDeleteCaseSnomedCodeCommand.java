/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 21, 2012
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
package gov.va.med.imaging.pathology.commands;

import java.util.HashMap;
import java.util.Map;

import gov.va.med.URNFactory;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.translation.exceptions.TranslationException;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.pathology.enums.PathologyField;
import gov.va.med.imaging.pathology.rest.translator.PathologyRestTranslator;
import gov.va.med.imaging.pathology.rest.types.PathologyFieldType;
import gov.va.med.imaging.rest.types.RestBooleanReturnType;
import gov.va.med.imaging.rest.types.RestCoreTranslator;
import gov.va.med.imaging.web.commands.WebserviceInputParameterTransactionContextField;

/**
 * @author VHAISWWERFEJ
 *
 */
public class PathologyDeleteCaseSnomedCodeCommand
extends AbstractPathologyCommand<Boolean, RestBooleanReturnType>
{
	private final String caseId;
	private final String tissueId;
	private final String snomedId;
	private final PathologyFieldType snomedField;
	private final String etiologyId;

	/**
	 * @param methodName
	 */
	public PathologyDeleteCaseSnomedCodeCommand(String caseId, String tissueId,
			String snomedId, PathologyFieldType snomedField, String etiologyId)
	{
		super("deleteCaseSnomedCode");
		this.caseId = caseId;
		this.tissueId = tissueId;
		this.snomedId = snomedId;
		this.snomedField = snomedField;
		this.etiologyId = etiologyId;
	}

	public String getCaseId()
	{
		return caseId;
	}

	public String getTissueId()
	{
		return tissueId;
	}

	public String getSnomedId()
	{
		return snomedId;
	}

	public PathologyFieldType getSnomedField()
	{
		return snomedField;
	}

	public String getEtiologyId()
	{
		return etiologyId;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#executeRouterCommand()
	 */
	@Override
	protected Boolean executeRouterCommand() 
	throws MethodException, ConnectionException
	{
		try
		{
			PathologyCaseURN pathologyCaseUrn = URNFactory.create(getCaseId(), PathologyCaseURN.class);
			getTransactionContext().setPatientID(pathologyCaseUrn.getPatientId().toString());
			if(getSnomedId() == null)
				getRouter().deleteTissue(pathologyCaseUrn, getTissueId());
			else if(getEtiologyId() == null)
			{
				if(getSnomedField() == null)
					throw new MethodException("Snomed field type must be specified when deleting an entire snomed code");
				PathologyField field = PathologyRestTranslator.translate(getSnomedField());
				getRouter().deleteSnomedCode(pathologyCaseUrn, getTissueId(), getSnomedId(), field);
			}
			else
				getRouter().deleteSnomedEtiologyCode(pathologyCaseUrn, getTissueId(), getSnomedId(), getEtiologyId());
			return true;
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
		return "for case '" + getCaseId() + "', deleting tissue [" + getTissueId() + "], snomedId [" + getSnomedId()
		 + "], etiology [" + getEtiologyId() + "].";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#translateRouterResult(java.lang.Object)
	 */
	@Override
	protected RestBooleanReturnType translateRouterResult(Boolean routerResult)
	throws TranslationException, MethodException
	{
		return RestCoreTranslator.translate(routerResult);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#getResultClass()
	 */
	@Override
	protected Class<RestBooleanReturnType> getResultClass()
	{
		return RestBooleanReturnType.class;
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
	public Integer getEntriesReturned(RestBooleanReturnType translatedResult)
	{
		return null;
	}
}
