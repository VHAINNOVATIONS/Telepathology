/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 8, 2012
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
package gov.va.med.imaging.core.router.commands;

import java.util.ArrayList;
import java.util.List;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.exchange.business.Site;

/**
 * @author VHAISWWERFEJ
 *
 */
public class GetSitesCommandImpl
extends AbstractCommandImpl<List<Site>>
{
	private static final long serialVersionUID = 1L;
	
	private final String [] siteNumbers;
	
	public GetSitesCommandImpl(String [] siteNumbers)
	{
		super();
		this.siteNumbers = siteNumbers;
	}
	
	public String[] getSiteNumbers()
	{
		return siteNumbers;
	}

	@Override
	public List<Site> callSynchronouslyInTransactionContext() 
	throws MethodException, ConnectionException
	{
		List<Site> result = new ArrayList<Site>();
		
		for(String siteNumber : getSiteNumbers())
		{
			result.add(getCommandContext().getSiteResolver().getSite(siteNumber));
		}
		
		return result;
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
		sb.append(getSiteNumbers() == null ? "<null site ids>" : getSiteNumbers());

		return sb.toString();
	}

}
