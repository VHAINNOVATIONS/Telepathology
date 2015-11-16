package gov.va.med.imaging.exchange.business;

import java.util.Map;

import gov.va.med.imaging.artifactsource.ArtifactSource;

/**
 * A Site is an encapsulation of data about a repository of
 * medical data that is listed in the VA site service.  It is, 
 * in this system, nearly synonymous with the VA definition of a 
 * Site except that "alien" sites, those outside the VA, are also 
 * included.
 * 
 * @author vhaiswbeckec
 *
 */
public interface Site
extends ArtifactSource
{
	public abstract String getRegionId();
	public abstract String getSiteAbbr();
	public abstract String getSiteName();
	public abstract String getSiteNumber();
	
	//public abstract void setRegionId(String regionId);
	//public abstract void setSiteNumber(String siteNumber);
	//public abstract void setSiteAbbr(String siteAbbr);
	//public abstract void setSiteName(String siteName);

	/**
	 * @return
	 */
	public abstract boolean hasAcceleratorServer();
	public abstract String getAcceleratorServer();
	public abstract int getAcceleratorPort();

	public abstract int getVistaPort();
	public abstract String getVistaServer();

	//public abstract void setAcceleratorPort(int acceleratorPort);
	//public abstract void setAcceleratorServer(String acceleratorServer);
	//public abstract void setVistaPort(int vistaPort);
	//public abstract void setVistaServer(String vistaServer);
	
	public abstract boolean isSiteUserAuthenticatable();
	public abstract boolean isSitePatientLookupable();
	
	/**
	 * A map of the connections provided by this site
	 * @return
	 */
	public abstract Map<String, SiteConnection> getSiteConnections();
}