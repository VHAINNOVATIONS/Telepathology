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
package gov.va.med.imaging.transactionlogger.configuration;

import java.io.Serializable;

/**
 * Configuration for the Transaction Logger Local Data Source
 * @author vhaiswwerfej
 *
 */
public class TransactionLoggerDataSourceProviderConfiguration 
implements Serializable 
{
	private static final long serialVersionUID = 8989330812721975203L;

	private Integer retentionPeriodDays;
	private Boolean purgeAtStartup = null;
	private Boolean periodicPurgeEnabled = null;
	private Boolean addTransactionLogToJmx = null;
	private Long maxPurgeRuntime = null;
	
	public TransactionLoggerDataSourceProviderConfiguration()
	{
		this.retentionPeriodDays = Integer.MAX_VALUE;
		this.purgeAtStartup = null;
		this.periodicPurgeEnabled = null;
		this.addTransactionLogToJmx = null;
		this.maxPurgeRuntime = null;
	}
	
	/**
	 * The number of days entries in the transaction log should be kept before being purged.
	 * 
	 * @return the retentionPeriodDays
	 */
	public Integer getRetentionPeriodDays() {
		return retentionPeriodDays;
	}

	/**
	 * @param retentionPeriodDays the retentionPeriodDays to set
	 */
	public void setRetentionPeriodDays(Integer retentionPeriodDays) {
		this.retentionPeriodDays = retentionPeriodDays;
	}
	
	/**
	 * @return the purgeAtStartup
	 */
	public Boolean getPurgeAtStartup() {
		return purgeAtStartup;
	}

	/**
	 * @param purgeAtStartup the purgeAtStartup to set
	 */
	public void setPurgeAtStartup(Boolean purgeAtStartup) {
		this.purgeAtStartup = purgeAtStartup;
	}

	/**
	 * @return the periodicPurgeEnabled
	 */
	public Boolean getPeriodicPurgeEnabled() {
		return periodicPurgeEnabled;
	}

	/**
	 * @return the addTransactionLogToJmx
	 */
	public Boolean getAddTransactionLogToJmx() {
		return addTransactionLogToJmx;
	}

	/**
	 * @param addTransactionLogToJmx the addTransactionLogToJmx to set
	 */
	public void setAddTransactionLogToJmx(Boolean addTransactionLogToJmx) {
		this.addTransactionLogToJmx = addTransactionLogToJmx;
	}

	/**
	 * @param periodicPurgeEnabled the periodicPurgeEnabled to set
	 */
	public void setPeriodicPurgeEnabled(Boolean periodicPurgeEnabled) {
		this.periodicPurgeEnabled = periodicPurgeEnabled;
	}

	/**
	 * @return the maxPurgeRuntime
	 */
	public Long getMaxPurgeRuntime()
	{
		return maxPurgeRuntime;
	}

	/**
	 * @param maxPurgeRuntime the maxPurgeRuntime to set
	 */
	public void setMaxPurgeRuntime(Long maxPurgeRuntime)
	{
		this.maxPurgeRuntime = maxPurgeRuntime;
	}

	public static TransactionLoggerDataSourceProviderConfiguration createDefaultConfiguration()
	{
		TransactionLoggerDataSourceProviderConfiguration configuration = 
			new TransactionLoggerDataSourceProviderConfiguration();
		configuration.setRetentionPeriodDays(90); // 90 days
		configuration.setPurgeAtStartup(false);
		configuration.setPeriodicPurgeEnabled(true);
		configuration.setAddTransactionLogToJmx(true);
		configuration.setMaxPurgeRuntime(600000L); // a value of 0 indicates the purge will run forever
		return configuration;
	}	
}
