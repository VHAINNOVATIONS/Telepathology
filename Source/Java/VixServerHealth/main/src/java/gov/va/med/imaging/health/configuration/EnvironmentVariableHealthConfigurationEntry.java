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

import gov.va.med.imaging.health.VixServerHealthProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Entry that contains information about an environment variable to be contained in the VIX Health report.
 * 
 * @author vhaiswwerfej
 *
 */
public class EnvironmentVariableHealthConfigurationEntry 
implements Serializable
{

	private static final long serialVersionUID = 719242308166309788L;
	private final static Logger logger = Logger.getLogger(EnvironmentVariableHealthConfigurationEntry.class);
	
	private String environmentVariableName;
	private String vixHealthKey;
	
	
	
	public EnvironmentVariableHealthConfigurationEntry()
	{
		environmentVariableName = "";
		vixHealthKey = "";
	}
	
	public EnvironmentVariableHealthConfigurationEntry(String environmentVariableName, String vixHealthKey)
	{
		this.environmentVariableName = environmentVariableName;
		this.vixHealthKey = vixHealthKey;
	}

	/**
	 * @return the environmentVariableName
	 */
	public String getEnvironmentVariableName() {
		return environmentVariableName;
	}

	/**
	 * @param environmentVariableName the environmentVariableName to set
	 */
	public void setEnvironmentVariableName(String environmentVariableName) {
		this.environmentVariableName = environmentVariableName;
	}

	/**
	 * @return the vixHealthKey
	 */
	public String getVixHealthKey() {
		return vixHealthKey;
	}

	/**
	 * @param vixHealthKey the vixHealthKey to set
	 */
	public void setVixHealthKey(String vixHealthKey) {
		this.vixHealthKey = vixHealthKey;
	}

	public static List<EnvironmentVariableHealthConfigurationEntry> 
		getDefaultEnvironmentVariableHealthConfigurationEntries()
	{
		List<EnvironmentVariableHealthConfigurationEntry> entries = 
			new ArrayList<EnvironmentVariableHealthConfigurationEntry>();
		logger.info("Loading default Environment Variable configuration");
		
		entries.add(new EnvironmentVariableHealthConfigurationEntry("vixconfig", 
				VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_CONFIG_DIR_KEY));
		entries.add(new EnvironmentVariableHealthConfigurationEntry("vixcache", 
				VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_CACHE_DIR_KEY));
		entries.add(new EnvironmentVariableHealthConfigurationEntry("SYSTEMDRIVE", 
				VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_SYSTEM_DRIVE));
		
		return entries;
	}
	
}
