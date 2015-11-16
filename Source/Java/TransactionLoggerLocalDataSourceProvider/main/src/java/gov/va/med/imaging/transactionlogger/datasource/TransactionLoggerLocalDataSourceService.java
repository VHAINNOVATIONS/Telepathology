/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 18, 2009
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
package gov.va.med.imaging.transactionlogger.datasource;

import gov.va.med.imaging.ImagingMBean;
import gov.va.med.imaging.access.TransactionLogEntry;
import gov.va.med.imaging.access.TransactionLogWriter;
import gov.va.med.imaging.access.je.BDBRecoverer;
import gov.va.med.imaging.access.je.TransactionLogEntryImpl;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.AbstractLocalDataSource;
import gov.va.med.imaging.datasource.TransactionLoggerDataSourceSpi;
import gov.va.med.imaging.exchange.TaskScheduler;
import gov.va.med.imaging.exchange.enums.DatasourceProtocol;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.monitorederrors.MonitoredErrors;
import gov.va.med.imaging.transactionlogger.configuration.TransactionLoggerDataSourceProviderConfiguration;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.log4j.Logger;
import com.sleepycat.je.*;
import com.sleepycat.je.util.DbSpace;
import com.sleepycat.persist.*;
import com.sleepycat.persist.evolve.Converter;
import com.sleepycat.persist.evolve.Deleter;
import com.sleepycat.persist.evolve.Mutations;

/**
 * Transaction logger implementation part 5 (I think). This version logs the transaction to a local Sleepycat DB instance.
 * 
 * @author vhaiswwerfej
 *
 */
public class TransactionLoggerLocalDataSourceService
extends AbstractLocalDataSource
implements TransactionLoggerDataSourceSpi
{
	private final static Logger logger = Logger.getLogger(TransactionLoggerLocalDataSourceService.class);

	private static final int MAX_DEADLOCK_RETRIES = 3;
	private static final String fileSep = System.getProperty ("file.separator");

	// the DB values get created by either the createDatabase() or the openDatabase()
	private static Environment logdbEnvironment = null;
	private static EntityStore logdbEntityStore = null;
	private static PrimaryIndex<Long, TransactionLogEntryImpl> transactionLogBySequence = null;
	private static SecondaryIndex<Long, Long, TransactionLogEntryImpl> transactionByDate = null; 
	private static SecondaryIndex<String, Long, TransactionLogEntryImpl> transactionByTransactionId = null;
	private static SecondaryIndex<String, Long, TransactionLogEntryImpl> transactionByMachineName = null;
	private static TransactionLoggerStatistics statistics = null;

	// Transaction Log Retention Period Before Purging, In Days.
	// Default to one year.
	private int transactionLogRetentionPeriod = Integer.MAX_VALUE;

	// Scheduled Maintenance - Once A Day At 2AM.
	private static final int MAINTENANCE_HOUR = 2;
	private static final long MAINTENANCE_CYCLE = 60 * 60 * 24 * 1000;

	private static TransactionLogMaintenance transactionLogMaintenance = null;
	
	private static List<TransactionLogEntryImpl> cachedDuringPurgeEntries = new ArrayList<TransactionLogEntryImpl>();
	private static boolean purgeRunning = false;
	
	public static TransactionLoggerLocalDataSourceService create()
	throws InvocationTargetException
	{
		return new TransactionLoggerLocalDataSourceService();
	}
	
	
	public TransactionLoggerLocalDataSourceService()
	throws InvocationTargetException
	{
		super ();
		if (logdbEnvironment != null) return;
		try {init ();}
		catch (Exception x) {throw new InvocationTargetException (x);}

	}


	public void init() 
	throws MethodException, ConnectionException
	{

		if (logdbEnvironment != null) return;

		logger.info ("Opening Transaction Log database.");

		String vixConfigPath = System.getenv ("VIXCONFIG");
		if (vixConfigPath == null) vixConfigPath = fileSep + "VixConfig";
		File dbDir = new File (vixConfigPath + fileSep + "logs");
		if (!dbDir.exists ()) dbDir.mkdirs ();

		try
		{
			EnvironmentConfig envConfig = new EnvironmentConfig ();
			envConfig.setAllowCreate (true);
			envConfig.setTransactional (true);
			envConfig.setConfigParam ("je.lock.nLockTables", "7");
			envConfig.setConfigParam ("je.lock.timeout", "1000000");

//			If BDB Environment initialization fails, run some recovery
//			routines on it and then try one more time.
			try
			{
				logdbEnvironment = new Environment (dbDir, envConfig);
				logger.info("DB Environment created");
			}

			catch (Exception xEnv)
			{
				logger.error (xEnv.getClass ().getName () + ": " + xEnv.getMessage ());
				new BDBRecoverer (dbDir).recover ();
				logdbEnvironment = new Environment (dbDir, envConfig);
			}

//			install mutations to handle versioning
			Mutations mutations = new Mutations ();

			if (TransactionLogEntryImpl.getConverters () != null)
			{
				for (Converter converter : TransactionLogEntryImpl.getConverters ())
					mutations.addConverter (converter);
			}

			if (TransactionLogEntryImpl.getDeleters () != null)
			{
				for (Deleter deleter : TransactionLogEntryImpl.getDeleters ())
					mutations.addDeleter (deleter);
			}

			StoreConfig storeConfig = new StoreConfig ();
			storeConfig.setAllowCreate (true);
			storeConfig.setTransactional (true);
			if (mutations != null && !mutations.isEmpty ())
				storeConfig.setMutations (mutations);

			logdbEntityStore = new EntityStore (logdbEnvironment, "transactionLog", storeConfig);
			logger.info("DB entity store created");

			transactionLogBySequence = logdbEntityStore.getPrimaryIndex (Long.class, TransactionLogEntryImpl.class);
			transactionByDate = logdbEntityStore.getSecondaryIndex (transactionLogBySequence, Long.class, "startTime");
			transactionByTransactionId = logdbEntityStore.getSecondaryIndex (transactionLogBySequence, String.class, "transactionId");
			transactionByMachineName = logdbEntityStore.getSecondaryIndex (transactionLogBySequence, String.class, "machineName");
			logger.info("Indexes created");	
			displayDBSpace();
		} 

		catch (DatabaseException dx) 
		{
			dx.printStackTrace ();
			throw new MethodException (dx);
		}

		try
		{

			setTransactionLogRetentionPeriod (getConfiguration().getRetentionPeriodDays());
		}

		catch (Exception x)
		{
			x.printStackTrace ();
			setTransactionLogRetentionPeriod (Integer.MAX_VALUE);
		}
				
		Boolean purge = getConfiguration().getPurgeAtStartup();
		if((purge != null) && (purge == true))
		{
			// purge the old log entries at initialization
			purgeLogEntries(getTransactionLogRetentionPeriod());
		}
		
		Boolean periodicPurgeEnabled = getConfiguration().getPeriodicPurgeEnabled();
		if((periodicPurgeEnabled != null) && (periodicPurgeEnabled == true))
		{					
			logger.info("Scheduling daily purge of transaction log for " + MAINTENANCE_HOUR + " AM");
	//		Scheduled Maintenance Daily At 2AM.
			Calendar cal = new GregorianCalendar ();
			if (cal.get (Calendar.HOUR_OF_DAY) >= MAINTENANCE_HOUR) cal.add (Calendar.DAY_OF_YEAR, 1);
			cal.set (Calendar.HOUR_OF_DAY, MAINTENANCE_HOUR);
			cal.set (Calendar.MINUTE, 0);
			cal.set (Calendar.SECOND, 0);
	//		setTransactionLogRetentionPeriod (30);
			transactionLogMaintenance = new TransactionLogMaintenance (this);
			TaskScheduler.getTaskScheduler ().scheduleAtFixedRate (transactionLogMaintenance,
					new Date (cal.getTimeInMillis ()), MAINTENANCE_CYCLE);
	//		new Date (new Date ().getTime () + 30000), 30000);
		}
		statistics = new TransactionLoggerStatistics();
		if((getConfiguration().getAddTransactionLogToJmx() != null) && (getConfiguration().getAddTransactionLogToJmx() == true))
		{		
			registerMBeanServer();
		}
		

	} // init
	
	private static ObjectName transactionLoggerManagerMBeanName = null;
	
	private static synchronized void registerMBeanServer()
	{
		if(transactionLoggerManagerMBeanName == null)
		{
			logger.info("Registering Transaction Log with JMX");
			try
			{
				/*
				JEMonitor monitor = new JEMonitor(logdbEnvironment.getHome().getAbsolutePath());
				MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
				Hashtable<String, String> mBeanProperties = new Hashtable<String, String>();
				mBeanProperties.put( "type", "TransactionLogDatabase" );
				//mBeanProperties.put( "name", "Manager-" + Integer.toHexString(bufferManager.hashCode()) );
				mBeanProperties.put( "name", "Manager");
				transactionLoggerManagerMBeanName = new ObjectName(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, mBeanProperties);
	            mBeanServer.registerMBean(monitor, transactionLoggerManagerMBeanName);
	            */
	            // add statistics
				MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
				Hashtable<String, String> mBeanProperties = new Hashtable<String, String>();
				mBeanProperties.put( "type", "TransactionLogDatabase" );
				//mBeanProperties.put( "name", "Manager-" + Integer.toHexString(bufferManager.hashCode()) );
				mBeanProperties.put( "name", "Statistics");
				transactionLoggerManagerMBeanName = new ObjectName(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, mBeanProperties);
				mBeanServer.registerMBean(statistics, transactionLoggerManagerMBeanName);
	            
	            
			}
			catch(Exception ex)
			{
				logger.error("Error registering transaction log with JMX", ex);
			}
		}
	}

	private TransactionLoggerDataSourceProviderConfiguration getConfiguration()
	{
		return TransactionLoggerLocalDataSourceProvider.getTransactionLoggerConfiguration();
	}
	
	private void displayDBSpace()
	{
		try
		{
			DbSpace space = new DbSpace(logdbEnvironment, false, true, true);
			space.print(System.out);
		}
		catch(Exception ex)
		{
			logger.error("Error displaying DB space", ex);
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.TransactionLoggerDataSource#getAllLogEntries()
	 */
	@Override
	public void getAllLogEntries(TransactionLogWriter writer) 
	throws MethodException, ConnectionException 
	{
		logger.info ("getAllLogEntries From Local Host");

		EntityCursor<TransactionLogEntryImpl> cursor = null;

		try
		{
			cursor = transactionLogBySequence.entities ();

			TransactionLogEntryImpl tlei = cursor.first ();
			while (tlei != null)
			{
				writer.writeTransactionLogEntry(tlei);
				tlei = cursor.next ();
			}

			cursor.close ();
			cursor = null;
		}

		catch (DatabaseException dx)
		{
			statistics.incrementTransactionReadErrors();
			dx.printStackTrace ();
			throw new MethodException (dx);
		}

		finally
		{
			try {if (cursor != null) cursor.close ();}
			catch (DatabaseException dxClose) {}
		}
	}

	@Override
	public void getLogEntries(TransactionLogWriter writer, Date startDate,
			Date endDate, ImageQuality imageQuality, String user,
			String modality, DatasourceProtocol datasourceProtocol,
			String errorMessage, String imageUrn, String transactionId,
			Boolean bForward, Integer startIndex, Integer endIndex)
			throws MethodException, ConnectionException
	{
		logger.info ("getLogEntries (TransactionLogWriter writer, beaucoup criteria) From Local Host");

		EntityCursor<TransactionLogEntryImpl> cursor = null;
		int entryListCount = 0; 
		boolean forward = true;
		if(bForward != null)
			forward = bForward.booleanValue ();

		Pattern userPattern;
		Pattern modalityPattern;
		Pattern errorMessagePattern;
		Pattern imageUrnPattern;
		Pattern transactionIdPattern;

		try
		{
			Date fromKey = startDate == null ? new Date (0L) : startDate;
			Date toKey = endDate == null ? new Date (Long.MAX_VALUE) : endDate;

			if(logger.isInfoEnabled())
			{
				DateFormat df = new SimpleDateFormat("dd-MM-yyyy:hh:mm:ss");
				logger.info( "Generating transaction log cursor from " + df.format(fromKey) + " to " + df.format(toKey) +
						"ImageQuality=" + (imageQuality == null ? "<null>" : imageQuality.name()) + " " +
						"User=" + (user == null ? "<null>" : user) + " " + 
						"Modality=" + (modality == null ? "<null>" : modality) + " " +
						"DatasourceProtocol=" + (datasourceProtocol == null ? "<null>" : datasourceProtocol.toString()) + " " +
						"ErrorMessage=" + (errorMessage == null ? "<null>" : errorMessage) + " " +
						"ImageURN=" + (imageUrn == null ? "<null>" : imageUrn) + " " +
						"TransactionID=" + (transactionId == null ? "<null>" : transactionId) + " " +
						"Iteration=" + (forward ? "forward" : "backward")
				);
			}

			userPattern = user == null || user.length() == 0 ? null : Pattern.compile(user);
			modalityPattern = modality == null || modality.length() == 0 ? null : Pattern.compile(modality);
			errorMessagePattern = errorMessage == null || errorMessage.length() == 0 ? null : Pattern.compile(errorMessage);
			imageUrnPattern = imageUrn == null || imageUrn.length() == 0 ? null : Pattern.compile(imageUrn);
			transactionIdPattern = transactionId == null || transactionId.length() == 0 ? null : Pattern.compile(transactionId);			
			
			cursor = transactionByDate.entities (fromKey.getTime (), true, toKey.getTime (), true);			
			logger.info("Got entries cursor from database.");
			
			TransactionLogEntryImpl tlei = null;		
			
			if (forward)
				tlei = cursor.first ();
			else
				tlei = cursor.last ();			
			
			logger.debug("set cursor position, getting items.");

			while (tlei != null)
			{
				boolean imageQualityMatches = false;
				try
				{
					imageQualityMatches = 
						imageQuality == null ||
						tlei.getQuality() != null && imageQuality == ImageQuality.valueOf(tlei.getQuality());
				} 
				catch (IllegalArgumentException iaX)
				{
					imageQualityMatches = true;
				} 

				boolean modalityMatches = 
					modalityPattern == null ? true : 
						tlei.getModality() == null ? false : 
							modalityPattern.matcher(tlei.getModality()).matches();

				boolean userMatches = 
					userPattern == null ? true : 
						tlei.getUser() == null ? false :
							userPattern.matcher(tlei.getUser()).matches();

				boolean errorMessageMatches = 
					errorMessagePattern == null ? true : 
						tlei.getErrorMessage() == null ? false :
							errorMessagePattern.matcher(tlei.getErrorMessage()).matches();

				boolean imageUrnMatches = 
					imageUrnPattern == null ? true : 
						tlei.getUrn() == null ? false :
							imageUrnPattern.matcher(tlei.getUrn()).matches();

				boolean transactionIdMatches = 
					transactionIdPattern == null ? true : 
						tlei.getTransactionId() == null ? false :
							transactionIdPattern.matcher(tlei.getTransactionId()).matches();

				boolean datasourceProtocolMatches = false;
				try
				{
					datasourceProtocolMatches = 
						datasourceProtocol == null ||
						tlei.getDatasourceProtocol() != null &&
						datasourceProtocol.equals( DatasourceProtocol.valueOf(tlei.getDatasourceProtocol().toUpperCase()) );
				} 
				catch (RuntimeException e)
				{
					datasourceProtocolMatches = true;
				}

				if( imageQualityMatches &&
						userMatches &&
						modalityMatches &&
						errorMessageMatches &&
						imageUrnMatches &&
						transactionIdMatches &&
						datasourceProtocolMatches )
				{
					// check to see if current index is in requested range
					if(isEntryIncludedInRange(startIndex, endIndex, entryListCount))
					{
						writer.writeTransactionLogEntry(tlei);
					}
											
					++entryListCount; // increment the count no matter what
					// if the end index is specified and the current index is above that, then break - never getting smaller
					// direction doesn't matter here since the first/last item is always the first index item
					if((endIndex != null) && (entryListCount > endIndex))
					{
						break;
					}
				}
				if (forward)
					tlei = cursor.next ();
				else
					tlei = cursor.prev ();
			}
			
			// don't need to reverse the results since if wanted oldest first then the last item is added to the list first
			
			cursor.close ();
			cursor = null;
			statistics.incrementTransactionsQueried();
		}

		catch (DatabaseException dx)
		{
			statistics.incrementTransactionReadErrors();
			dx.printStackTrace ();
			throw new MethodException (dx);
		}

		finally
		{
			try {if (cursor != null) cursor.close ();}
			catch (DatabaseException dxClose) {}
			logger.info("Entry list count is '" + entryListCount + "'.");
		}
	}
	
	/**
	 * Determine if the current cursor index is within the specified range of start and end.  This method handles the
	 * start and end possibly being null.
	 * 
	 * @param startIndex
	 * @param endIndex
	 * @param cursorIndex
	 * @return True if the entry at the cursorIndex position should be included
	 */
	private boolean isEntryIncludedInRange(Integer startIndex, Integer endIndex, int cursorIndex)
	{
		if(startIndex != null)
		{
			if(cursorIndex < startIndex)
				return false;
		}
		if(endIndex != null)
			if(cursorIndex > endIndex)
				return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.TransactionLoggerDataSource#getLogEntries(java.lang.String, java.lang.String)
	 */
	@Override
	public void getLogEntries(TransactionLogWriter writer, String fieldName,
			String fieldValue) 
	throws MethodException, ConnectionException 
	{
		logger.info ("getLogEntries [" + fieldName + " = " + fieldValue +"] From Local Host");

		EntityCursor<TransactionLogEntryImpl> cursor = null;

		try
		{
			if ("transactionId".equalsIgnoreCase (fieldName))
			{
				cursor = transactionByTransactionId.entities (fieldValue, true, fieldValue, true);
			}
			else if ("machineName".equalsIgnoreCase (fieldName))
			{
				cursor = transactionByMachineName.entities(fieldValue, true, fieldValue, true);
			}
			else
			{
				throw new MethodException ("Unsupported Field Name: " + fieldName + "[" + fieldValue + "]");
			}

			TransactionLogEntryImpl tlei = cursor.first ();
			while (tlei != null)
			{
				writer.writeTransactionLogEntry(tlei);
				tlei = cursor.next ();
			}

			cursor.close ();
			cursor = null;
			statistics.incrementTransactionsQueried();
		}

		catch (DatabaseException dx)
		{
			statistics.incrementTransactionReadErrors();
			dx.printStackTrace ();
			throw new MethodException (dx);
		}
		finally
		{
			try {if (cursor != null) cursor.close ();}
			catch (DatabaseException dxClose) {}
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.TransactionLoggerDataSource#purgeLogEntries(java.lang.Integer)
	 */
	@Override
	public void purgeLogEntries(Integer maxDaysAllowed) 
	throws MethodException, ConnectionException 
	{
		if(isPurgeRunning())
		{
			logger.warn("purge initiated but currently running, probably shouldn't happen.  Not starting purge again");
			return;
		}
		
		logger.info ("purgeLogEntries [Older Than " + maxDaysAllowed + " Days] From Local Host");

		int numDeleted = 0;
		int notDeletedCount = 0;
		Transaction txn = null;
		EntityCursor<TransactionLogEntryImpl> cursor = null;
		
		try
		{
			setPurgeRunning(true);
			logger.info("Disabling checkpointer and cleaner during purge");
			
			EnvironmentMutableConfig mutableConfig = logdbEnvironment.getMutableConfig();			
			mutableConfig.setConfigParam("je.env.runCheckpointer", "false");
			mutableConfig.setConfigParam("je.env.runCleaner", "false");
			logdbEnvironment.setMutableConfig(mutableConfig);
			
			String checkpointerValue = logdbEnvironment.getMutableConfig().getConfigParam("je.env.runCheckpointer");
			String cleanerValue = logdbEnvironment.getMutableConfig().getConfigParam("je.env.runCleaner");
			logger.info("Checkpointer enabled '" + checkpointerValue + "', cleaner enabled '" + cleanerValue + "'.");
			
			long purgeRuntime = 0L; // 0 means run forever
			Long purgeRuntimeValue = getConfiguration().getMaxPurgeRuntime();
			if(purgeRuntimeValue != null)
				purgeRuntime = purgeRuntimeValue;
			logger.info("Purge will run for a maximum of '" + (purgeRuntime <= 0 ? "until complete'" : purgeRuntime + "' ms") + ".");
			
			Calendar cal = new GregorianCalendar ();
			cal.add (Calendar.DAY_OF_YEAR, -1 * maxDaysAllowed.intValue ());

			logger.info ("Deleting Local Transaction Log Records Prior To: " + new Date (cal.getTimeInMillis ()));
			long startTime = System.currentTimeMillis();
			long purgeEndTime = 0L;
			if(purgeRuntime > 0)
			{
				purgeEndTime = startTime + purgeRuntime;
			}
			// set the transaction configuration to read uncommitted so we don't lock the database
			/*
			TransactionConfig config = new TransactionConfig();
			config.setReadCommitted(true);
			txn = logdbEnvironment.beginTransaction (null, config);
			CursorConfig cursorConfig = new CursorConfig();
			cursorConfig.setReadCommitted(true);			
			cursor = transactionByDate.entities (txn, Long.MIN_VALUE, true, cal.getTimeInMillis (), false, cursorConfig);
			*/
			txn = logdbEnvironment.beginTransaction (null, null);
			cursor = transactionByDate.entities (txn, Long.MIN_VALUE, true, cal.getTimeInMillis (), false, null);
			
			Object obj = cursor.first ();
			while (obj != null)
			{
				if(purgeEndTime > 0)
				{
					if(System.currentTimeMillis() >= purgeEndTime)
					{
						logger.info("Purging has run past the purge end time, purging will stop.");
						break;
					}
				}				
				
				if(!cursor.delete ())
				{
					notDeletedCount++;
				}
				numDeleted++;				
				if((numDeleted % 10000) == 0)
				{
					logger.info("Deleted '" + numDeleted + "' transaction log entries so far...");
				}
				obj = cursor.next ();
			}
			cursor.close ();
			cursor = null;

			txn.commit ();
			long endTime = System.currentTimeMillis();
			logger.info ("Deleted " + numDeleted + " Local Transaction Log Records in '" + (endTime - startTime) + "' ms.");
			logger.info( "Failed to delete '" + notDeletedCount + "' entries.");
			statistics.increaseTransactionsPurged(numDeleted);
			logger.info("About to compress");
			logdbEnvironment.compress();
			logger.info("Compress is done");
			displayDBSpace();
			cleanLog();
			displayDBSpace();
		
		}
		catch (DatabaseException dx)
		{
			numDeleted = -1;
			try {txn.abort ();}
			catch (DatabaseException dxAbort) {}
			logger.error("Error purging log entries", dx);
			throw new MethodException (dx);
		}
		catch(Exception ex)
		{
			numDeleted = -1;
			try {txn.abort ();}
			catch (DatabaseException dxAbort) {}
			logger.error("Error purging log entries", ex);
			throw new MethodException (ex);
		}
		finally
		{
			try {if (cursor != null) cursor.close ();}
			catch (DatabaseException dxClose) {}
			catch(Exception ex) {}
			setPurgeRunning(false);
			try
			{
				logger.info("Re-enabling checkpointer and cleaner after purge.");
				EnvironmentMutableConfig mutableConfig = logdbEnvironment.getMutableConfig();							
				mutableConfig.setConfigParam("je.env.runCheckpointer", "true");
				mutableConfig.setConfigParam("je.env.runCleaner", "true");
				logdbEnvironment.setMutableConfig(mutableConfig);
				
				String checkpointerValue = logdbEnvironment.getMutableConfig().getConfigParam("je.env.runCheckpointer");
				String cleanerValue = logdbEnvironment.getMutableConfig().getConfigParam("je.env.runCleaner");
				logger.info("Checkpointer enabled '" + checkpointerValue + "', cleaner enabled '" + cleanerValue + "'.");
			}
			catch(Exception ex)
			{
				logger.fatal("Exception turning the checkpointer and cleaner back on, " + ex.getMessage(), ex);
			}
			
			writeCachedEntries(this);
		}
	}
	
	private void cleanLog()
	{
		try
		{
			logger.info("About to clean Log");
			boolean anyCleaned = false;						
			while(logdbEnvironment.cleanLog() > 0)
			{
				logger.info("Clean log > 0, repeating.");
				anyCleaned = true;
			}
			logger.info("clean Log is done, anyClean=" + anyCleaned);
			if(anyCleaned)
			{
				logger.info("Creating checkpoint.");
				CheckpointConfig cpc = new CheckpointConfig();
				cpc.setForce(true);
				logdbEnvironment.checkpoint(cpc);
				logger.info("Checkpoint created.");
			}
		}
		catch(DatabaseException dbX)
		{
			dbX.printStackTrace();
		}
		catch(Exception ex)
		{
			logger.error("Error cleaning log", ex);
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.TransactionLoggerDataSource#writeLogEntry(gov.va.med.imaging.access.TransactionLogEntry)
	 */
	@Override
	public void writeLogEntry(TransactionLogEntry entry)
	throws MethodException, ConnectionException 
	{
		writeLogEntry(translateTransactionLogEntry(entry));
	}
	
	private TransactionLogEntryImpl translateTransactionLogEntry(TransactionLogEntry entry)
	{
		if(entry == null)
		{
			return null;
		}
		TransactionLogEntryImpl result = new TransactionLogEntryImpl(
			entry.getStartTime (),
			entry.getElapsedTime (),
			entry.getPatientIcn (),
			entry.getQueryType (),
			entry.getQueryFilter (),
			entry.getCommandClassName (),
			entry.getItemCount (),
			entry.getFacadeBytesSent (),
			entry.getFacadeBytesReceived (),
			entry.getDataSourceBytesSent (),
			entry.getDataSourceBytesReceived (),
			entry.getQuality (),
			entry.getMachineName (),
			entry.getRequestingSite (),
			entry.getOriginatingHost (),
			//entry.getFullName (),
			entry.getUser (),
			entry.getTransactionId (),
			entry.getUrn (),
			entry.isCacheHit (),
			entry.getErrorMessage (),
			entry.getModality (),
			entry.getPurposeOfUse (),
			entry.getDatasourceProtocol (),
			entry.getResponseCode (),
			entry.getExceptionClassName (),
			entry.getRealmSiteNumber (),
			entry.getTimeToFirstByte (),
			entry.getVixSoftwareVersion (),
			entry.getRespondingSite (),
			entry.getDataSourceItemsReceived(),
			entry.isAsynchronousCommand(),
			entry.getCommandId(),
			entry.getParentCommandId(),
			entry.getRemoteLoginMethod(),
			entry.getFacadeImageFormatSent(),
			entry.getFacadeImageQualitySent(),
			entry.getDataSourceImageFormatReceived(),
			entry.getDataSourceImageQualityReceived(),
			entry.getClientVersion(),
			entry.getDataSourceVersion(),
			entry.getDataSourceMethod(),
			entry.getDebugInformation(),
			entry.getDataSourceResponseServer(),
			entry.getThreadId(),
			entry.getVixSiteNumber(),
			entry.getRequestingVixSiteNumber()
		);
		
		return result;
	}
	
	private void writeLogEntry(TransactionLogEntryImpl entry)
	throws MethodException, ConnectionException 
	{
		if(entry == null)
		{
			logger.warn("transaction log entry is null and cannot be logged, ignoring...");
			return;
		}
		
		// if the purge is running, then add the entry to 
		if(isPurgeRunning())
		{
			addEntryToPurgeQueue(entry);
		}
		else
		{
			logger.info("Writing transaction (" + entry.getTransactionId() + ") with command ID (" + entry.getCommandId() + ") to log with execution time of '" + entry.getElapsedTime() + "' ms. Ran on thread (" + entry.getThreadId() + ")");
			int numTries = 0;
			Transaction txn = null;
	
			while (numTries < MAX_DEADLOCK_RETRIES)
			{
				try
				{
					//TransactionConfig config = new TransactionConfig();
					//config.setReadCommitted(true);
					//txn = logdbEnvironment.beginTransaction (null, config);
					txn = logdbEnvironment.beginTransaction (null, null);
					transactionLogBySequence.put (txn, (TransactionLogEntryImpl) entry);
					txn.commit ();
					statistics.incrementTransactionWritten();
					if(entry.getErrorMessage() != null && entry.getErrorMessage().length() > 0)
					{
						statistics.incrementTransactionErrors();
						try
						{
							MonitoredErrors.addIfMonitored(entry.getErrorMessage());
						}
						catch(Exception ex) {}
					}
					break;
				}
				//catch (LockConflictException lcX)
				catch (DeadlockException dlx)
				{
					statistics.incrementTransactionWriteErrors();
					try {txn.abort ();}
					catch (DatabaseException dlxAbort) {}
	
					numTries++;
					if (numTries >= MAX_DEADLOCK_RETRIES)
					{
						dlx.printStackTrace ();
						throw new MethodException (dlx);
					}
					else
					{
						logger.warn ("writeLogEntry: Deadlocked On Transaction Id [" + entry.getTransactionId () + "], current attempt '" + numTries + "', - Will Retry.");
					}
				}
				catch (DatabaseException dx)
				{
					statistics.incrementTransactionWriteErrors();
					try 
					{
						txn.abort ();
					}
					catch (DatabaseException dxAbort) 
					{
						logger.warn("DatabaseException aborting transaction, " + dxAbort.getMessage());
					}
					catch(Exception ex)
					{
						logger.warn("Exception aborting transaction, " + ex.getMessage());
					}
					logger.error("DatabaseException writing transaction, " + dx.getMessage(), dx);
					throw new MethodException (dx);
				}
				catch(Exception ex)
				{
					statistics.incrementTransactionWriteErrors();
					try
					{
						txn.abort();
					}
					catch (DatabaseException dxAbort) 
					{
						logger.warn("DatabaseException aborting transaction, " + dxAbort.getMessage());
					}
					catch(Exception exAbort)
					{
						logger.warn("Exception aborting transaction, " + exAbort.getMessage());
					}
					logger.error("Exception writing transaction, " + ex.getMessage(), ex);
					throw new MethodException (ex);
				}
			}
		}
	}

	/**
	 * Set the Transaction Log Retention Period in Days before records are purged.
	 * @param transactionLogRetentionPeriod The number of days of storage allowed before TransactionLog records are purged.
	 */
	public void setTransactionLogRetentionPeriod (int transactionLogRetentionPeriod)
	{
		this.transactionLogRetentionPeriod = transactionLogRetentionPeriod;
		logger.info ("Transaction Log Retention Period Set To " + this.transactionLogRetentionPeriod + " Days.");
	} 

	/**
	 * Get the Transaction Log Retention Period in Days after which records are purged.
	 * @return the number of days of storage allowed before TransactionLog records are purged.
	 */
	public int getTransactionLogRetentionPeriod()
	{
		return transactionLogRetentionPeriod;
	}
	
	

	/**
	 * Wake up every once and awhile and perform some TransactionLog maintenance operations.
	 */
	class TransactionLogMaintenance 
	extends TimerTask
	{
		private final TransactionLoggerLocalDataSourceService owner;

		/**
		 * Construct with:
		 * @param owner The LocalTransactionLogger implementation that I perform maintenance for.
		 */
		public TransactionLogMaintenance(TransactionLoggerLocalDataSourceService owner)
		{
			this.owner = owner;

		} // TransactionLogMaintenance


		/**
		 * Perform the TransactionLog maintenance operations by scheduled timer.
		 */
		public void run ()
		{
			try 
			{				
				Thread t = new Thread()
				{

					/* (non-Javadoc)
					 * @see java.lang.Thread#run()
					 */
					@Override
					public void run()
					{
						try
						{
							final int transactionLogRetentionPeriod = owner.getTransactionLogRetentionPeriod();
							owner.purgeLogEntries (new Integer (transactionLogRetentionPeriod));
						}
						catch(Exception ex)
						{
							logger.error("Error creating seperate thread to purge transaction log entries, " + ex.getMessage(), ex);
						}						
					}
				};
				t.start();
			}
			catch (Exception x) 
			{
				logger.error("Error purging log entries", x);
			}
		}
	}



	/**
	 * @return the purgeRunning
	 */
	private synchronized static boolean isPurgeRunning()
	{
		return purgeRunning;
	}


	/**
	 * @param purgeRunning the purgeRunning to set
	 */
	private synchronized static void setPurgeRunning(boolean purgeRunning)
	{
		TransactionLoggerLocalDataSourceService.purgeRunning = purgeRunning;
	}
	
	private static void addEntryToPurgeQueue(TransactionLogEntryImpl entry)
	{
		logger.info("Adding transaction log entry '" + entry.getTransactionId() + "' to list to be written to log when purge is completed.");
		synchronized(cachedDuringPurgeEntries)
		{
			cachedDuringPurgeEntries.add(entry);
		}
	}
	
	private static synchronized void writeCachedEntries(TransactionLoggerLocalDataSourceService service)
	{
		synchronized(cachedDuringPurgeEntries)
		{
			logger.info("Writing '" + cachedDuringPurgeEntries.size() + "' cached log entries cached during purge.");
			for(TransactionLogEntryImpl entry : cachedDuringPurgeEntries)
			{
				try
				{
					service.writeLogEntry(entry);
				}
				catch(ConnectionException cX)
				{
					logger.error("ConnectionException delayed writing of transaction (" + entry.getTransactionId()+ ")", cX);			
				}
				catch(MethodException mX)
				{
					logger.error("MethodException delayed writing of transaction (" + entry.getTransactionId()+ ")", mX);
				}
				catch(Exception x)
				{
					logger.error("Exception delayed writing of transaction (" + entry.getTransactionId()+ ")", x);
				}
			}
			cachedDuringPurgeEntries.clear();
		}
	}
}
