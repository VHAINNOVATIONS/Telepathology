package gov.va.med.siteservice;

import java.io.Serializable;
import gov.va.med.OID;
import gov.va.med.imaging.artifactsource.ArtifactSource;
import gov.va.med.siteservice.siteprotocol.ProtocolPreference;

/**
 * Realizations must also realize Serializable, this will be used in lieu
 * of mementos for persisting and restoring instance of realizing classes.
 * 
 * @author vhaiswbeckec
 *
 */
public interface SiteProtocolPreferenceFactory
extends Serializable
{
	/**
	 * Find the SiteNumber specific preferred protocols, looking first in the map
	 * of local, then exceptional sites and then returning the default id the site is not
	 * exceptional.
	 * 
	 * @param siteNumber
	 * @return
	 */
	public abstract String[] getPreferredProtocols(String siteNumber);

	/**
	 * Return true if the Site is listed as an exceptional site.
	 * 
	 * @param siteNumber
	 * @return
	 */
	public abstract boolean isSiteAlien(String siteNumber);

	/**
	 * Return true if the Site is listed as a local site.
	 * 
	 * @param siteNumber
	 * @return
	 */
	public abstract boolean isSiteLocal(String siteNumber);
	
	/**
	 * 
	 * @param artifactSource
	 * @return
	 */
	public abstract String[] getPreferredProtocols(ArtifactSource artifactSource);
	public abstract String[] getPreferredProtocols(OID homeCommunityId, String repositoryId);

	/**
	 * @param ppm
	 */
	public abstract boolean add(ProtocolPreference ppm);

	/**
	 * 
	 */
	public abstract void clear();

	/**
	 * @param homeCommunityId
	 * @param repositoryId
	 */
	public abstract void remove(OID homeCommunityId, String repositoryId);
	
	/**
	 * Return true if the Site is enabled
	 * 
	 * @param siteNumber
	 * @return
	 */
	public abstract boolean isSiteEnabled(String siteNumber);
}