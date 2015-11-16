/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Jun 9, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.asynchproxy.router;

import gov.va.med.asynchproxy.AsynchProxyFactory;
import gov.va.med.asynchproxy.AsynchProxyListener;
import gov.va.med.asynchproxy.GenericAsynchResult;

/**
 * @author VHAISWBECKEC
 *
 */
public class AsynchRouterClient
implements AsynchProxyListener
{
	private int resultCount = 0;
	public AsynchRouterClient()
	{
		
	}

	private synchronized void incrementResultCount()
	{
		++resultCount;
	}
	
	/**
     * @see biz.happycat.asynchproxy.AsynchProxyListener#result(biz.happycat.asynchproxy.GenericAsynchResult)
     */
    public void result(GenericAsynchResult result)
    {
    	incrementResultCount();
    	System.out.println("Got result of (wrapper) type :" + result.getClass().getSimpleName());
    	if(result != null && result.getResult() != null)
        	System.out.println("Got result of type :" + result.getResult().getClass().getSimpleName());
    }

	/**
     * @return the resultCount
     */
    public int getResultCount()
    {
    	return resultCount;
    }
}
