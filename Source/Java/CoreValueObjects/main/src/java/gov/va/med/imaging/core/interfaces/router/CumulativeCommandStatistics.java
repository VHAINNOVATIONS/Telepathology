/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 28, 2010
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
package gov.va.med.imaging.core.interfaces.router;

import gov.va.med.RoutingToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author vhaiswwerfej
 *
 */
public class CumulativeCommandStatistics<R>
{
	private List<R> cumulativeResults = Collections.synchronizedList(new ArrayList<R>());
	private int childGetSuccessCount = 0;
	private int childGetErrorCount = 0;
	private List<CumulativeCommandRoutingTokenException> errors = 
		Collections.synchronizedList(new ArrayList<CumulativeCommandRoutingTokenException>());
	
	public CumulativeCommandStatistics()
	{
		super();
	}

	public List<R> getCumulativeResults()
	{
		return cumulativeResults;
	}

	public void addToCumulativeResults(R item)
	{
		cumulativeResults.add(item);
	}

	public int getChildGetSuccessCount()
	{
		return childGetSuccessCount;
	}
	
	public void incrementChildGetSuccessCount()
	{
		childGetSuccessCount++;
	}
	
	public int getChildGetErrorCount()
	{
		return childGetErrorCount;
	}
	
	public void incrementChildGetErrorCount()
	{
		childGetErrorCount++;
	}

	public List<CumulativeCommandRoutingTokenException> getErrors()
	{
		return errors;
	}
	
	public void addError(CumulativeCommandRoutingTokenException t)
	{
		errors.add(t);
	}
	
	public void addError(RoutingToken routingToken, Throwable t)
	{
		errors.add(new CumulativeCommandRoutingTokenException(routingToken, t));
	}
}
