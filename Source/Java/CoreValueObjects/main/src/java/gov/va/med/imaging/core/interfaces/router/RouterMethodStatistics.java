/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Oct 17, 2008
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
package gov.va.med.imaging.core.interfaces.router;

/**
 * @author VHAISWBECKEC
 *
 */
public class RouterMethodStatistics
{
	private int calledCount;
	private int successCount;
	private int throwableCount;
	private int nullResultCount;
	private long resetTime;
	
	RouterMethodStatistics(){reset();}

	public void reset()
	{
		calledCount = 0;
		successCount = 0;
		throwableCount = 0;
		nullResultCount = 0;
		resetTime = System.currentTimeMillis();
	}
	
	void incrementCalledCount(){calledCount++;}
	public int getCalledCount(){return calledCount;}

	void incrementSuccessCount(){successCount++;}
	public int getSuccessCount(){return successCount;}

	void incrementThrowableCount(){throwableCount++;}
	public int getThrowableCount(){return throwableCount;}

	void incrementNullResultCount(){nullResultCount++;}
	public int getNullResultCount(){return nullResultCount;}

	public long getResetTime(){return resetTime;}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("Called Count=");
		sb.append(getCalledCount());
		
		sb.append(", Success Count=");
		sb.append(getSuccessCount());
		sb.append("(");
		sb.append(getNullResultCount());
		sb.append(" null)");
		
		sb.append(", Throwable Count=");
		sb.append(getThrowableCount());
		
		return sb.toString();
	}
}
