/**
 * 
 */
package gov.va.med.imaging.monitor;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.*;
import java.util.logging.Logger;

/**
 * This class is definately NOT thread-safe.  It relies on a single thread doing the
 * collection of the drive statistics and the evaluation of the set limits.
 * 
 * @author vhaiswbeckec
 *
 */
public abstract class DriveSpaceMonitor
extends TimerTask
{
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private static DriveSpaceMonitor singleton;
	private static final long defaultPeriodicity = 60000L;
	
	public static synchronized DriveSpaceMonitor getSingleton()
	{
		if(singleton == null)
		{
			singleton = createOSSpecificMonitor();

			if(singleton != null && singleton instanceof TimerTask)
			{
				String timerThreadName = DriveSpaceMonitorProperties.getString("DriveSpaceMonitor.TimerThread");
				long periodicity = DriveSpaceMonitorProperties.getLong("DriveSpaceMonitor.Periodicity", defaultPeriodicity);
				
				new Timer(timerThreadName, true).schedule(singleton, new Date(), periodicity); //$NON-NLS-1$
			}
		}
		
		return singleton;
	}

	/**
	 * 
	 */
	private static DriveSpaceMonitor createOSSpecificMonitor()
	{
		String osName = System.getProperty("os.name"); //$NON-NLS-1$
		if(osName == null)
			return null;
		// trim and lower case to make comparison easier
		osName = osName.trim().toLowerCase();
		
		if(osName.startsWith("windows")) //$NON-NLS-1$
			return new WindowsDiskSpaceMonitor();
		
		return null;
	}
	
	private DriveStatistics[] driveStatistics;
	
	@Override
	public void run()
	{
		logger.entering(this.getClass().getName(), "run");
		acquireDriveStatistics();
		evaluateListenerLimits();
		logger.exiting(this.getClass().getName(), "run");
	}

	/**
	 * 
	 */
	private void acquireDriveStatistics()
	{
		logger.entering(this.getClass().getName(), "acquireDriverStatistics");
		try
		{
			char[] driveLetters = getDriveRoots();
			driveStatistics = new DriveStatistics[driveLetters == null ? 0 : driveLetters.length];
			
			for(int index=0; index < driveLetters.length; ++index)
				driveStatistics[index] = getDriveStatistics(driveLetters[index]);
		} 
		catch (Exception x)
		{
			x.printStackTrace();
		}
		logger.exiting(this.getClass().getName(), "acquireDriverStatistics");
	}
	
	/**
	 * 
	 */
	private Map<DriveLimit, DriveSpaceMonitorListener> listeners = new HashMap<DriveLimit, DriveSpaceMonitorListener>();
	
	public void addListener(DriveLimit limit, DriveSpaceMonitorListener listener)
	{
		synchronized(listeners)
		{
			listeners.put(limit, listener);
		}
	}
	
	/**
	 * Add a collection of drice limit notifications with a single listener
	 * 
	 * @param requestedNotifications
	 * @param notifier
	 */
	public void addListener(Collection<DriveLimit> requestedNotifications, DriveSpaceMonitorListener listener)
	{
		synchronized(listeners)
		{
			for( DriveLimit driveLimit : requestedNotifications )
				addListener(driveLimit, listener);
		}
	}
	
	public void removeListener(DriveLimit limit)
	{
		synchronized(listeners)
		{
			listeners.remove(limit);
		}
	}

	private void evaluateListenerLimits()
	{
		synchronized(listeners)
		{
			if(listeners != null && listeners.size() > 0 && this.driveStatistics != null && this.driveStatistics.length > 0)
				for(DriveLimit limit : listeners.keySet() )
					for(DriveStatistics stats : this.driveStatistics)
						if( limit.isOutOfLimit(stats) )
							notifyListener(listeners.get(limit), limit, stats);
		}
	}
	
	/**
	 * Notify a listener that a Limit has been exceeded.
	 * 
	 * @param listener
	 * @param limit
	 * @param stats
	 */
	private void notifyListener(DriveSpaceMonitorListener listener, DriveLimit limit, DriveStatistics stats)
	{
		listener.limitExceededNotification(limit, stats);
	}

	public abstract char[] getDriveRoots()
	throws Exception;
	
	public abstract DriveStatistics getDriveStatistics(char drive)
	throws Exception;
	
	public DriveStatistics getDriveStatistics() 
	throws Exception
	{
		return getDriveStatistics(getDriveRoots()[0]);
	}
	
	public static class WindowsDiskSpaceMonitor
	extends DriveSpaceMonitor
	{
		static final String driveListingCommand = "fsutil fsinfo drives"; //$NON-NLS-1$
		static final String freeSpaceCommand = "fsutil fsinfo ntfsinfo ";		// concatenate drive letter and a colon //$NON-NLS-1$

		public char[] getDriveRoots() 
		throws Exception
		{
			Process p = Runtime.getRuntime().exec(driveListingCommand);
			InputStream inStream = new BufferedInputStream( p.getInputStream() );
			LineNumberReader reader = new LineNumberReader(new InputStreamReader(inStream));
			for(String line = reader.readLine(); line != null; line = reader.readLine() )
			{
				line = line.trim().toUpperCase();
				if( line.startsWith("DRIVES:") ) //$NON-NLS-1$
				{
					for(int index=0; index < line.length(); ++index)
						System.out.print("0x00" + line.charAt(index) + "(" + Integer.toHexString((int)line.charAt(index)) + ")\t"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					System.out.println();
					
					String[] lineElements = line.split("[ \t \0x00]"); //$NON-NLS-1$
					char[] drives = new char[lineElements.length-1];
					for(int index=1; index < lineElements.length; ++index)
						drives[index-1] = lineElements[index].charAt(0);
					
					return drives;
				}
			}			
			return null;
		}

		public DriveStatistics getDriveStatistics(char drive)
		throws Exception
		{
			long totalClusters = 0L;
			long freeClusters = 0L;
			long bytesPerCluster = 0L;
			
			Process p = Runtime.getRuntime().exec(freeSpaceCommand + drive + ":"); //$NON-NLS-1$
			InputStream inStream = new BufferedInputStream( p.getInputStream() );
			LineNumberReader reader = new LineNumberReader(new InputStreamReader(inStream));
			for(String line = reader.readLine(); line != null; line = reader.readLine() )
			{
				line = line.trim().toUpperCase();
				if(line.startsWith("TOTAL CLUSTERS")) //$NON-NLS-1$
				{
					String[] lineElements = line.split("[ \t]"); //$NON-NLS-1$
					String number = lineElements[lineElements.length-1];
					totalClusters = parseNumeric(number);
				}
				if(line.startsWith("FREE CLUSTERS")) //$NON-NLS-1$
				{
					String[] lineElements = line.split("[ \t]"); //$NON-NLS-1$
					String number = lineElements[lineElements.length-1];
					freeClusters = parseNumeric(number);
				}
				if(line.startsWith("BYTES PER CLUSTER")) //$NON-NLS-1$
				{
					String[] lineElements = line.split("[ \t]"); //$NON-NLS-1$
					String number = lineElements[lineElements.length-1];
					bytesPerCluster = parseNumeric(number);
				}
			}
			
			return new DriveStatistics(String.valueOf(drive), freeClusters * bytesPerCluster, totalClusters * bytesPerCluster);
		}
		
		/**
		 * 
		 * @param numeric
		 * @return
		 */
		public long parseNumeric(String numeric)
		{
			long result;
			
			if(numeric.toLowerCase().startsWith("0x")) //$NON-NLS-1$
				result = Long.parseLong(numeric.substring(2), 16);
			else
				result = Long.parseLong(numeric);
			
			return result;
		}
	}
	
	public static void main(String[] argv)
	{
		DriveSpaceMonitor monitor = createOSSpecificMonitor();
		monitor.acquireDriveStatistics();
	}

}
