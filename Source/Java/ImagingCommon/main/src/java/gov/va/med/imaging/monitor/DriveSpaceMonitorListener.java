/**
 * 
 */
package gov.va.med.imaging.monitor;

/**
 * @author vhaiswbeckec
 *
 */
public interface DriveSpaceMonitorListener
{
	public void limitExceededNotification(DriveLimit limit, DriveStatistics currentDriveStats);
}
