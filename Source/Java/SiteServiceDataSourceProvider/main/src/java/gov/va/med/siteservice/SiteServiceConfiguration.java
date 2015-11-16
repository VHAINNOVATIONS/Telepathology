/**
 * 
 */
package gov.va.med.siteservice;

import gov.va.med.imaging.DateUtil;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;

/**
 * @author vhaiswbeckec
 * 
 */
public class SiteServiceConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;
	public static String defaultSiteService = "http://localhost/VistaWebSvcs/ImagingExchangeSiteService.asmx";
	public static URI defaultSiteServiceUri = null;
	private static final String defaultVhaSitesXmlFileName = "VhaSites.xml";

	static {
		try {
			defaultSiteServiceUri = new URI(
					"http://localhost/VistaWebSvcs/ImagingExchangeSiteService.asmx");
		} catch (URISyntaxException e) {
			Logger.getLogger(SiteServiceConfiguration.class).error(
					e.getMessage());
		}
	}

	/**
	 * @return
	 */
	public static SiteServiceConfiguration createDefault(
			File configurationDirectory) {
		SiteServiceConfiguration config = new SiteServiceConfiguration();

		config.setRefreshHour(Messages.getInt(
				"SiteResolutionProvider.defaultRefreshHour", 23));
		config.setRefreshMinimumDelay(Messages.getInt(
				"SiteResolutionProvider.defaultRefreshMinimumDelay", 1));
		config.setRefreshPeriod(Messages.getLong(
				"SiteResolutionProvider.defaultRefreshPeriod",
				DateUtil.MILLISECONDS_IN_DAY));
		config
				.setSiteServiceCacheFileName(configurationDirectory
						+ "/"
						+ Messages
								.getString("SiteResolutionProvider.siteServiceCacheFileName"));
		config
				.setRegionListCacheFileName(configurationDirectory
						+ "/"
						+ Messages
								.getString("SiteResolutionProvider.siteServiceRegionCacheFileName"));
		config
				.setVhaSitesXmlFileName(configurationDirectory
				+ "/"
				+ Messages
						.getString("SiteResolutionProvider.vhaSitesXmlFileName"));
		config.setSiteServiceUri(defaultSiteServiceUri);
		config.setForceLoadFromLocalCache(false);
		config.setUseV2Service(true);
		return config;
	}

	private URI siteServiceUri = defaultSiteServiceUri;
	private int refreshHour;
	private int refreshMinimumDelay;
	private long refreshPeriod;
	private String siteServiceCacheFileName;
	private String regionListCacheFileName;
	private String vhaSitesXmlFileName;
	private boolean forceLoadFromLocalCache;
	private boolean useV2Service;

	public URI getSiteServiceUri() {
		return this.siteServiceUri;
	}

	public int getRefreshHour() {
		return this.refreshHour;
	}

	public int getRefreshMinimumDelay() {
		return this.refreshMinimumDelay;
	}

	public long getRefreshPeriod() {
		return this.refreshPeriod;
	}

	public String getSiteServiceCacheFileName() {
		return this.siteServiceCacheFileName;
	}

	public String getRegionListCacheFileName() {
		return this.regionListCacheFileName;
	}
	
	public String getVhaSitesXmlFileName() {
		if (vhaSitesXmlFileName != null)
			return this.vhaSitesXmlFileName;
		else
			return System.getenv("vixconfig") + "/" + defaultVhaSitesXmlFileName;
	}

	public void setSiteServiceUri(URI siteServiceUri) {
		this.siteServiceUri = siteServiceUri;
	}

	public void setRefreshHour(int refreshHour) {
		this.refreshHour = refreshHour;
	}

	public void setRefreshMinimumDelay(int refreshMinimumDelay) {
		this.refreshMinimumDelay = refreshMinimumDelay;
	}

	public void setRefreshPeriod(long refreshPeriod) {
		this.refreshPeriod = refreshPeriod;
	}

	public void setSiteServiceCacheFileName(String siteServiceCacheFileName) {
		this.siteServiceCacheFileName = siteServiceCacheFileName;
	}

	public void setRegionListCacheFileName(String regionListCacheFileName) {
		this.regionListCacheFileName = regionListCacheFileName;
	}
	
	public void setVhaSitesXmlFileName(String vhaSitesFileName) {
		this.vhaSitesXmlFileName = vhaSitesFileName;
	}

	public boolean getForceLoadFromLocalCache() {
		return forceLoadFromLocalCache;
	}

	public void setForceLoadFromLocalCache(boolean forceLoadFromLocalCache) {
		this.forceLoadFromLocalCache = forceLoadFromLocalCache;
	}

	/**
	 * @return the useV2Service
	 */
	public boolean isUseV2Service()
	{
		return useV2Service;
	}

	/**
	 * @param useV2Service the useV2Service to set
	 */
	public void setUseV2Service(boolean useV2Service)
	{
		this.useV2Service = useV2Service;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.refreshHour;
		result = prime * result + this.refreshMinimumDelay;
		result = prime * result
				+ (int) (this.refreshPeriod ^ (this.refreshPeriod >>> 32));
		result = prime
				* result
				+ ((this.regionListCacheFileName == null) ? 0
						: this.regionListCacheFileName.hashCode());
		result = prime
				* result
				+ ((this.siteServiceCacheFileName == null) ? 0
						: this.siteServiceCacheFileName.hashCode());
		result = prime
				* result
				+ ((this.vhaSitesXmlFileName == null) ? 0
						: this.vhaSitesXmlFileName.hashCode());
		result = prime
				* result
				+ ((this.siteServiceUri == null) ? 0 : this.siteServiceUri
						.hashCode());
		result = prime
				* result
				+ (forceLoadFromLocalCache ? 0 : 1);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SiteServiceConfiguration other = (SiteServiceConfiguration) obj;
		if (this.refreshHour != other.refreshHour)
			return false;
		if (this.refreshMinimumDelay != other.refreshMinimumDelay)
			return false;
		if (this.refreshPeriod != other.refreshPeriod)
			return false;
		if (this.forceLoadFromLocalCache != other.forceLoadFromLocalCache)
			return false;
		if (this.regionListCacheFileName == null) {
			if (other.regionListCacheFileName != null)
				return false;
		} else if (!this.regionListCacheFileName
				.equals(other.regionListCacheFileName))
			return false;
		if (this.siteServiceCacheFileName == null) {
			if (other.siteServiceCacheFileName != null)
				return false;
		} else if (!this.siteServiceCacheFileName
				.equals(other.siteServiceCacheFileName))
			return false;
		if (this.vhaSitesXmlFileName == null) {
			if (other.vhaSitesXmlFileName != null)
				return false;
		} else if (!this.vhaSitesXmlFileName
				.equals(other.vhaSitesXmlFileName))
			return false;
		if (this.siteServiceUri == null) {
			if (other.siteServiceUri != null)
				return false;
		} else if (!this.siteServiceUri.equals(other.siteServiceUri))
			return false;
		return true;
	}
}
