/**
 * 
 */
package gov.va.med.imaging.monitor;

import gov.va.med.imaging.monitor.DriveLimit.LimitType;
import gov.va.med.imaging.monitor.DriveLimit.LimitStatistic;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class DriveSpaceMonitorTest 
extends TestCase
{
	private DriveSpaceMonitor monitor;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		monitor = DriveSpaceMonitor.getSingleton();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testPeriodic() 
	throws DriveLimitInitializationException 
	{
		monitor.addListener(DriveLimit.create("C", LimitType.MONITOR, LimitStatistic.TOTAL_SPACE, 0L), listener);
		monitor.addListener(DriveLimit.create("C", LimitType.NOTIFY_UNDER, LimitStatistic.FREE_SPACE_PERCENTAGE, 40L), listener);
		
		monitor.addListener(DriveLimit.create("D", LimitType.NOTIFY_UNDER, LimitStatistic.FREE_SPACE, 10000L), listener);
		monitor.addListener(DriveLimit.create("D", LimitType.NOTIFY_UNDER, LimitStatistic.FREE_SPACE_PERCENTAGE, 10L), listener);
		
		try
		{
			Thread.sleep(61000L);
		} 
		catch (InterruptedException x)
		{
			x.printStackTrace();
		}
	}
	
	private DriveSpaceMonitorListener listener = new MonitorListener();
	
	class MonitorListener
	implements DriveSpaceMonitorListener
	{
		public void limitExceededNotification(DriveLimit limit, DriveStatistics currentDriveStats)
		{
			System.out.println("DriveLimit - " + limit.getExceedLimitMessage() + ", stats => " + currentDriveStats.toString());
		}
	}
}
