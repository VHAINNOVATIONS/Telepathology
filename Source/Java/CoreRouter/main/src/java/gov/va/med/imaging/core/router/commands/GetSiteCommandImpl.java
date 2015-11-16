/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 11, 2011
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
package gov.va.med.imaging.core.router.commands;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;

import gov.va.med.imaging.exchange.business.Site;

/**
 * @author vhaiswwerfej
 *
 */
public class GetSiteCommandImpl
extends AbstractCommandImpl<Site>
{
	private static final long serialVersionUID = 1L;

	// NOTE: this siteID is not a RoutingToken, it is the subject
	// of the command
	private final String siteId;
	
	public GetSiteCommandImpl(String siteId)
	{
		this.siteId = siteId;
	}

	@Override
	public Site callSynchronouslyInTransactionContext() 
	throws MethodException, ConnectionException
	{
		return getCommandContext().getSiteResolver().getSite(getSiteId());
	}

	@Override
	public boolean equals(Object obj)
	{
		return false;
	}

	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(getSiteId() == null ? "<null site id>" : getSiteId());

		return sb.toString();
	}

	public String getSiteId()
	{
		return siteId;
	}

}
