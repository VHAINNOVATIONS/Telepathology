/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 5, 2009
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
package gov.va.med.imaging.health.configuration;

import gov.va.med.imaging.health.VixSite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * VIX Health configuration. Contains the configuration values loaded as part of the VIX Health report.
 * 
 * @author vhaiswwerfej
 *
 */
public class VixHealthConfiguration 
implements Serializable
{
	private static final long serialVersionUID = 2423941560837449129L;
	
	private List<JMXHealthConfigurationEntry> jmxHealthConfiguration = 
		new ArrayList<JMXHealthConfigurationEntry>();
	private List<EnvironmentVariableHealthConfigurationEntry> environmentVariableHealthConfiguration = 
		new ArrayList<EnvironmentVariableHealthConfigurationEntry>();	
	private List<VixSite> vixLocalSites = 
		new ArrayList<VixSite>();
	private List<String> excludedSiteNumbers = 
		new ArrayList<String>();
	
	private Long healthTimeoutMs = null;
	
	public VixHealthConfiguration()
	{
		super();
	}

	/**
	 * @return the jmxHealthConfiguration
	 */
	public List<JMXHealthConfigurationEntry> getJmxHealthConfiguration() {
		return jmxHealthConfiguration;
	}

	/**
	 * @param jmxHealthConfiguration the jmxHealthConfiguration to set
	 */
	public void setJmxHealthConfiguration(
			List<JMXHealthConfigurationEntry> jmxHealthConfiguration) {
		this.jmxHealthConfiguration = jmxHealthConfiguration;
	}

	/**
	 * @return the environmentVariableHealthConfiguration
	 */
	public List<EnvironmentVariableHealthConfigurationEntry> getEnvironmentVariableHealthConfiguration() {
		return environmentVariableHealthConfiguration;
	}

	/**
	 * @param environmentVariableHealthConfiguration the environmentVariableHealthConfiguration to set
	 */
	public void setEnvironmentVariableHealthConfiguration(
			List<EnvironmentVariableHealthConfigurationEntry> environmentVariableHealthConfiguration) {
		this.environmentVariableHealthConfiguration = environmentVariableHealthConfiguration;
	}	

	/**
	 * @return the healthTimeoutMs
	 */
	public Long getHealthTimeoutMs() {
		return healthTimeoutMs;
	}

	/**
	 * @param healthTimeoutMs the healthTimeoutMs to set
	 */
	public void setHealthTimeoutMs(Long healthTimeoutMs) {
		this.healthTimeoutMs = healthTimeoutMs;
	}

	/**
	 * @return the vixLocalSites
	 */
	public List<VixSite> getVixLocalSites()
	{
		return vixLocalSites;
	}

	/**
	 * @param vixLocalSites the vixLocalSites to set
	 */
	public void setVixLocalSites(List<VixSite> vixLocalSites)
	{
		this.vixLocalSites = vixLocalSites;
	}

	public List<String> getExcludedSiteNumbers()
	{
		return excludedSiteNumbers;
	}

	public void setExcludedSiteNumbers(List<String> excludedSiteNumbers)
	{
		this.excludedSiteNumbers = excludedSiteNumbers;
	}

	/**
	 * Load the default values
	 * @param realmSiteNumber
	 */
	public void loadDefaultConfigurations(String realmSiteNumber, 
			boolean listThreadPoolData, boolean listThreadProcessingTime, 
			boolean includeAwiv, boolean includeHdig)
	{
		jmxHealthConfiguration = 
			JMXHealthConfigurationEntry.getDefaultJmxHealthConfigurationEntries(realmSiteNumber,
					listThreadPoolData, listThreadProcessingTime, includeAwiv, includeHdig);	
		environmentVariableHealthConfiguration = 
			EnvironmentVariableHealthConfigurationEntry.getDefaultEnvironmentVariableHealthConfigurationEntries();
		this.healthTimeoutMs = 1000L * 60 * 10; // 10 minutes
		this.vixLocalSites.add(VixSite.createLocalVix("0001", "Example Server", "EXM", "servername", 8080, false));
		this.excludedSiteNumbers.add("200");
		this.excludedSiteNumbers.add("2002");
		this.excludedSiteNumbers.add("2003");
		this.excludedSiteNumbers.add("2004");
	}
}
