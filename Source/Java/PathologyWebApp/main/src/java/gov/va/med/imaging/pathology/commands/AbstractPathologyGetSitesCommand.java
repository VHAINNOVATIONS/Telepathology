/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 13, 2012
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

import gov.va.med.RoutingToken;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.RoutingTokenHelper;
import gov.va.med.imaging.pathology.AbstractPathologySite;
import gov.va.med.imaging.web.commands.WebserviceInputParameterTransactionContextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author VHAISWWERFEJ
 *
 */
public abstract class AbstractPathologyGetSitesCommand<E extends Object>
extends AbstractPathologyCommand<List<AbstractPathologySite>, E>
{
	private final String siteId;
	private final boolean reading;

	/**
	 * @param methodName
	 */
	public AbstractPathologyGetSitesCommand(String siteId, boolean reading)
	{
		super(reading == true ? "getReadingSites" : "getAcqusitionSites");
		this.siteId = siteId;
		this.reading = reading;
	}

	public String getSiteId()
	{
		return siteId;
	}

	public boolean isReading()
	{
		return reading;
	}

	@Override
	protected List<AbstractPathologySite> executeRouterCommand()
	throws MethodException, ConnectionException
	{
		try
		{
			RoutingToken routingToken =
				RoutingTokenHelper.createSiteAppropriateRoutingToken(getSiteId());
			if(isReading())
				return getRouter().getPathologyReadingSites(routingToken);
			else
				return getRouter().getPathologyAcquisitionSites(routingToken);
		}
		catch(RoutingTokenFormatException rtfX)
		{
			throw new MethodException(rtfX);
		}
	}

	@Override
	protected String getMethodParameterValuesString()
	{
		return (reading == true ? "reading sites" : "acquisition sites") + " from " + getSiteId();
	}

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
}
