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

import gov.va.med.imaging.datasource.Provider;
import gov.va.med.imaging.datasource.ProviderService;
import gov.va.med.imaging.datasource.TransactionLoggerDataSourceSpi;
import gov.va.med.imaging.transactionlogger.configuration.TransactionLoggerDataSourceProviderConfiguration;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.log4j.Logger;

/**
 * @author vhaiswwerfej
 *
 */
public class TransactionLoggerLocalDataSourceProvider 
extends Provider
{
	private static final String PROVIDER_NAME = "TransactionLoggerLocalDataSource";
	private static final double PROVIDER_VERSION = 1.0d;
	private static final String PROVIDER_INFO = 
		"Implements: \n" + 
		"TransactionLoggerDataSource SPI \n" + 
		"backed by a Local Sleepycat Transaction Log Database.";

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(TransactionLoggerLocalDataSourceProvider.class);
	
	private final SortedSet<ProviderService> services;
	private static TransactionLoggerDataSourceProviderConfiguration transactionLoggerConfiguration = null;	
	
	public TransactionLoggerLocalDataSourceProvider()
	{
		this(PROVIDER_NAME, PROVIDER_VERSION, PROVIDER_INFO);
	}
	
	/**
	 * A special constructor that is only used for creating a configuration
	 * file.
	 * 
	 * @param vistaConfiguration
	 */
	public TransactionLoggerLocalDataSourceProvider(TransactionLoggerDataSourceProviderConfiguration loggerConfiguration)
	{
		this();
		TransactionLoggerLocalDataSourceProvider.transactionLoggerConfiguration = loggerConfiguration;
	}
	
	public TransactionLoggerLocalDataSourceProvider(String name, double version, String info)
	{
		super(name, version, info);
		services = new TreeSet<ProviderService>();
		services.add(
			new ProviderService(
				this,
				TransactionLoggerDataSourceSpi.class,  
				(byte)0, 
				TransactionLoggerLocalDataSourceService.class)
		);
		
		// load the ExchangeConfiguration if it exists
		synchronized(TransactionLoggerLocalDataSourceProvider.class)
	    {
			try
			{
				if(transactionLoggerConfiguration == null)
					transactionLoggerConfiguration = (TransactionLoggerDataSourceProviderConfiguration)loadConfiguration();
			}
			catch(ClassCastException ccX)
			{
				logger.error("Unable to load configuration because the configuration file is invalid.", ccX);
			}
	    }
	}
	
	@Override
	public SortedSet<ProviderService> getServices()
	{
		return Collections.unmodifiableSortedSet(services);
	}
	
	/**
	 * 
	 */
	@Override
	public void storeConfiguration()
    {
	    storeConfiguration(getTransactionLoggerConfiguration());
    }
	
	/**
	 * A package level method for SPI implementation to get the
	 * Configuration.
	 * 
	 * @return
	 */
	static TransactionLoggerDataSourceProviderConfiguration getTransactionLoggerConfiguration()
	{
		if(transactionLoggerConfiguration == null)
			logger.error("TransactionLoggerDataSourceProviderConfiguration is null, possibly called before TransactionLoggerDataSourceProvider was instantiated.");
		
		return transactionLoggerConfiguration;
	}
	
	public static void main(String [] args)
	{
		System.out.println("Creating TransactionLoggerDataSourceProvider configuration file");
		TransactionLoggerDataSourceProviderConfiguration loggerConfiguration = null;
		
		
		if(args.length == 1)
		{
			int retentionPeriod = Integer.parseInt(args[0]);
			loggerConfiguration = new TransactionLoggerDataSourceProviderConfiguration();
			System.out.println("Creating configuration file with retention period [" + retentionPeriod + "].");
			loggerConfiguration.setRetentionPeriodDays(retentionPeriod);			
		}
		else if(args.length == 2)
		{
			int retentionPeriod = Integer.parseInt(args[0]);
			boolean purgeAtStartup = Boolean.parseBoolean(args[1]);
			loggerConfiguration = new TransactionLoggerDataSourceProviderConfiguration();
			System.out.println("Creating configuration file with retention period [" + retentionPeriod + "] and purgeAtStartup [" + purgeAtStartup + "].");
			loggerConfiguration.setRetentionPeriodDays(retentionPeriod);
			loggerConfiguration.setPurgeAtStartup(purgeAtStartup);
		}
		else if(args.length == 3)
		{
			int retentionPeriod = Integer.parseInt(args[0]);
			boolean purgeAtStartup = Boolean.parseBoolean(args[1]);
			boolean periodicPurgeEnabled = Boolean.parseBoolean(args[2]);
			loggerConfiguration = new TransactionLoggerDataSourceProviderConfiguration();
			System.out.println("Creating configuration file with retention period [" + retentionPeriod 
				+ "], purgeAtStartup [" + purgeAtStartup + "], and periodPurgeEnabled [" + periodicPurgeEnabled + "].");
			loggerConfiguration.setRetentionPeriodDays(retentionPeriod);
			loggerConfiguration.setPurgeAtStartup(purgeAtStartup);
			loggerConfiguration.setPeriodicPurgeEnabled(periodicPurgeEnabled);
		}
		else
		{
			System.out.println("Creating default configuration.");
			System.out.println("Available parameters are: <retention period days> <purgeAtStartup> <periodic purge enabled>");
			loggerConfiguration = TransactionLoggerDataSourceProviderConfiguration.createDefaultConfiguration();
		}
		TransactionLoggerLocalDataSourceProvider provider = 
			new TransactionLoggerLocalDataSourceProvider(loggerConfiguration);
		provider.storeConfiguration();
		System.out.println("Configuration file saved to '" + provider.getConfigurationFileName() + "'.");
	}
}
