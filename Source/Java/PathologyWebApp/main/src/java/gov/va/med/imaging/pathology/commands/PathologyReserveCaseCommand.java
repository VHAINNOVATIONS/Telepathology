/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 3, 2012
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
import gov.va.med.imaging.pathology.enums.PathologyCaseReserveResult;
import gov.va.med.imaging.pathology.rest.translator.PathologyRestTranslator;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseReserveResultType;
import gov.va.med.imaging.web.commands.WebserviceInputParameterTransactionContextField;

/**
 * @author VHAISWWERFEJ
 *
 */
public class PathologyReserveCaseCommand
extends AbstractPathologyCommand<PathologyCaseReserveResult, PathologyCaseReserveResultType>
{

	private final String pathologyCaseId;
	private final boolean lock;
	
	public PathologyReserveCaseCommand(String pathologyCaseId, boolean lock)
	{
		super("reserveCase");
		this.pathologyCaseId = pathologyCaseId;
		this.lock = lock;
	}

	public String getPathologyCaseId()
	{
		return pathologyCaseId;
	}

	public boolean isLock()
	{
		return lock;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#executeRouterCommand()
	 */
	@Override
	protected PathologyCaseReserveResult executeRouterCommand()
	throws MethodException, ConnectionException
	{
		try
		{
			PathologyCaseURN pathologyCaseUrn = URNFactory.create(getPathologyCaseId(), PathologyCaseURN.class);
			getTransactionContext().setPatientID(pathologyCaseUrn.getPatientId().toString());
			return getRouter().reservePathologyCase(pathologyCaseUrn, isLock());
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
		return "for case '" + getPathologyCaseId() + "', setting lock: '" + isLock();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#translateRouterResult(java.lang.Object)
	 */
	@Override
	protected PathologyCaseReserveResultType translateRouterResult(
			PathologyCaseReserveResult routerResult) 
	throws TranslationException, MethodException
	{
		return PathologyRestTranslator.translate(routerResult);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#getResultClass()
	 */
	@Override
	protected Class<PathologyCaseReserveResultType> getResultClass()
	{
		return PathologyCaseReserveResultType.class;
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
		transactionContextFields.put(WebserviceInputParameterTransactionContextField.urn, getPathologyCaseId());
		transactionContextFields.put(WebserviceInputParameterTransactionContextField.queryFilter, transactionContextNaValue);

		return transactionContextFields;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.web.commands.AbstractWebserviceCommand#getEntriesReturned(java.lang.Object)
	 */
	@Override
	public Integer getEntriesReturned(
			PathologyCaseReserveResultType translatedResult)
	{
		return null;
	}
}