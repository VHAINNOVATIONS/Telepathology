package gov.va.med.imaging.protocol.vista;

import gov.va.med.OID;
import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.WellKnownOID;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.artifactsource.ArtifactSourceMemento;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.SiteConnection;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.junit.Ignore;

@Ignore
public class SLCSite2 implements Site 
{
	private static final long serialVersionUID = 1L;
	public int getAcceleratorPort(){return 8080;}
    public String getAcceleratorServer(){return "localhost";}
    public String getSiteAbbr(){return "SLC";}
    public String getSiteName(){return "Salt Lake City";}
    public String getSiteNumber(){return "660";}
    public int getVistaPort(){return 660;}
    public String getVistaServer(){return "localhost";}
    public boolean hasAcceleratorServer(){return true;}
	public String getRegionId() { return ""; }
	
	private List<URL> artifactUrls = null;
	private List<URL> metadataUrls = null;

	@Override
	public synchronized List<URL> getArtifactUrls()
	{
		if(artifactUrls == null)
		{
			artifactUrls = new ArrayList<URL>();
			try
			{
				artifactUrls.add(new URL("vftp://localhost:8080"));
				artifactUrls.add(new URL("vista://localhost:1903"));
			}
			catch (MalformedURLException x)
			{
				x.printStackTrace();
				return null;
			}
		}
		
		return artifactUrls;
	}
	
	@Override
	public synchronized List<URL> getMetadataUrls()
	{
		if(metadataUrls == null)
		{
			metadataUrls = new ArrayList<URL>();
			try
			{
				metadataUrls.add(new URL("vftp://localhost:8080"));
				metadataUrls.add(new URL("vista://localhost:1903"));
			}
			catch (MalformedURLException x)
			{
				x.printStackTrace();
				return null;
			}
		}
		return metadataUrls;
	}
	
	@Override
	public int getArtifactServerCount()
	{
		return 2;
	}
	@Override
	public int getMetadataServerCount()
	{
		return 2;
	}
	@Override
	public String getIdentifier(){return "TestSite" + getName();}
	@Override
	public OID getHomeCommunityId()
	{
		return WellKnownOID.VA_RADIOLOGY_IMAGE.getCanonicalValue();
	}
	
	@Override
	public boolean isRepresents(OID homeCommunityId, String repositoryId)
	{
		return WellKnownOID.VA_DOCUMENT.equals(homeCommunityId) || WellKnownOID.VA_RADIOLOGY_IMAGE.equals(homeCommunityId);
	}
	
	@Override
	public URL getAvailableArtifactServer(String protocol)
	{
		try
		{
			return "vftp".equals(protocol)? 
				new URL("vftp://localhost:8080") :
				new URL("vista://localhost:1903");
		}
		catch (MalformedURLException x)
		{
			x.printStackTrace();
			return null;
		}
	}
	@Override
	public URL getAvailableMetadataServer(String protocol)
	{
		try
		{
			return "vftp".equals(protocol)? 
				new URL("vftp://localhost:8080") :
				new URL("vista://localhost:1903");
		}
		catch (MalformedURLException x)
		{
			x.printStackTrace();
			return null;
		}
	}
	@Override
	public String getRepositoryId()
	{
		return "199";
	}
	@Override
	public String getName()
	{
		return getRepositoryId();
	}
	@Override
	public Iterator<URL> artifactIterator()
	{
		return artifactUrls.iterator();
	}
	
	@Override
	public Iterator<URL> metadataIterator()
	{
		return metadataUrls.iterator();
	}
	
	@Override
	public ArtifactSourceMemento getMemento()
	{
		return null;
	}
	@Override
	public RoutingToken createRoutingToken()
	{
		try
		{
			return RoutingTokenImpl.createVARadiologySite(getSiteNumber());
		}
		catch (RoutingTokenFormatException x)
		{
			x.printStackTrace();
			return null;
		}
	}
	@Override
	public boolean isDodDocument()
	{
		return false;
	}
	@Override
	public boolean isDodRadiology()
	{
		return false;
	}
	@Override
	public boolean isVaDocument()
	{
		return true;
	}
	@Override
	public boolean isVaRadiology()
	{
		return true;
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
