package gov.va.med.imaging.protocol.vista;

import gov.va.med.OID;
import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.artifactsource.ArtifactSourceMemento;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.SiteConnection;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;

@Ignore
public class SLCSite implements Site {
	public int getAcceleratorPort() {
		return 8080;
	}

	public String getAcceleratorServer() {
		return "localhost";
	}

	public String getSiteAbbr() {
		return "SLC";
	}

	public String getSiteName() {
		return "Salt Lake City";
	}

	public String getSiteNumber() {
		return "660";
	}

	public int getVistaPort() {
		return 660;
	}

	public String getVistaServer() {
		return "localhost";
	}

	public boolean hasAcceleratorServer() {
		return true;
	}

	public String getRegionId() {
		return "";
	}

	@Override
	public Iterator<URL> artifactIterator() {
		return null;
	}

	@Override
	public int getArtifactServerCount() {
		return 0;
	}

	@Override
	public URL getAvailableArtifactServer(String protocol) {
		return null;
	}

	@Override
	public URL getAvailableMetadataServer(String protocol) {
		return null;
	}

	@Override
	public OID getHomeCommunityId() {
		return null;
	}

	@Override
	public String getIdentifier() {
		return null;
	}

	@Override
	public ArtifactSourceMemento getMemento() {
		return null;
	}

	@Override
	public int getMetadataServerCount() {
		return 0;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getRepositoryId() {
		return null;
	}

	@Override
	public boolean isRepresents(OID homeCommunityId, String repositoryId) {
		return false;
	}

	@Override
	public Iterator<URL> metadataIterator() {
		return null;
	}

	@Override
	public RoutingToken createRoutingToken() {
		try {
			return RoutingTokenImpl.createVARadiologySite(getSiteNumber());
		} catch (RoutingTokenFormatException x) {
			x.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean isDodDocument() {
		return false;
	}

	@Override
	public boolean isDodRadiology() {
		return false;
	}

	@Override
	public boolean isVaDocument() {
		return true;
	}

	@Override
	public boolean isVaRadiology() {
		return true;
	}

	@Override
	public List<URL> getArtifactUrls()
	{
		return null;
	}

	@Override
	public List<URL> getMetadataUrls()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Site#isSiteUserAuthenticatable()
	 */
	@Override
	public boolean isSiteUserAuthenticatable()
	{
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Site#isSitePatientLookupable()
	 */
	@Override
	public boolean isSitePatientLookupable()
	{
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Site#getSiteConnections()
	 */
	@Override
	public Map<String, SiteConnection> getSiteConnections()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
