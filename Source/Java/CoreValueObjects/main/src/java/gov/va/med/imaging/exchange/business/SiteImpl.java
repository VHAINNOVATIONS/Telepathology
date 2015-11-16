package gov.va.med.imaging.exchange.business;

import gov.va.med.WellKnownOID;
import gov.va.med.imaging.artifactsource.ArtifactSourceImpl;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author vhaiswbeckec
 *
 */
public class SiteImpl
extends ArtifactSourceImpl
implements Site, Serializable
{
	public static final String VFTP_PROTOCOL = "vftp"; //$NON-NLS-1$
	public static final String VISTA_PROTOCOL = "vista"; //$NON-NLS-1$
	public static final String VISTAIMAGING_PROTOCOL = "vistaimaging"; //$NON-NLS-1$

	private static final long serialVersionUID = 2L;
	
	private final String regionId;
	// private final String siteNumber;  siteNumber is now the base "identifier" field
	private final String siteName;
	private final String siteAbbr;
	private final boolean sitePatientLookupable;
	private final boolean siteUserAuthenticatable;
	private final Map<String, SiteConnection> siteConnections;
	
	
	private static SiteImpl localTestSite = null;
	public static synchronized SiteImpl createLocalTestSite() 
	throws MalformedURLException
	{
		if(localTestSite == null)
		{
			Map<String, SiteConnection> siteConnections = new HashMap<String, SiteConnection>();
			siteConnections.put(SiteConnection.siteConnectionVista, 
					new SiteConnection(SiteConnection.siteConnectionVista, "localhost", 9300));
			siteConnections.put(SiteConnection.siteConnectionVix, 
					new SiteConnection(SiteConnection.siteConnectionVix, "localhost", 8080));
			localTestSite = new SiteImpl("42", "660", "Salt Lake City", "SLC",  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				true, true,
				siteConnections,
				new URL("vftp://localhost:8080/"), //$NON-NLS-1$
				new URL("vista://localhost:9300")  //$NON-NLS-1$
			);
		}
				
		return localTestSite;
	}
	
	public static String[] getDefaultSiteProtocols()
	{
		return Messages.getString("SiteImpl.SITE_PROTOCOLS").split(","); //$NON-NLS-1$
	}
	
	public static boolean isProtocolAccessingVista(String protocol)
	{
		String protocolConfiguration = Messages.getString("SiteImpl." + protocol + ".vistaServer");
		return protocolConfiguration != null && Boolean.parseBoolean(protocolConfiguration);
	}
	
	/**
	 * The default site protocols are defined in the messages.properties file.
	 * 
	 * @param vistaServer
	 * @param vistaPort
	 * @param acceleratorServer
	 * @param acceleratorPort
	 * @return
	 * @throws MalformedURLException
	 */
	public static URL[] createDefaultSiteURLs(		
		String vistaServer, 
		int vistaPort, 
		String acceleratorServer,
        int acceleratorPort) 
	throws MalformedURLException
	{
		List<URL> siteUrls = new ArrayList<URL>();
		
		for(String protocol : getDefaultSiteProtocols())
			siteUrls.add( 
				new URL(
					protocol, 
					isProtocolAccessingVista(protocol) ? vistaServer : acceleratorServer, 
					isProtocolAccessingVista(protocol) ? vistaPort : acceleratorPort, 
					"") 
			);

		return siteUrls.toArray(new URL[siteUrls.size()]);
	}
	
	public SiteImpl(
			String siteNumber, 
			String siteName, 
			String siteAbbr, 
			String vistaServer, 
			int vistaPort, 
			String acceleratorServer,
	        int acceleratorPort,
	        String regionId,
	        Map<String, SiteConnection> siteConnections) 
	throws MalformedURLException
	{
		this(siteNumber, 
				siteName, 
				siteAbbr, 
				vistaServer, 
				vistaPort, 
				acceleratorServer, 
				acceleratorPort, 
				regionId, 
				true, 
				true, 
				siteConnections);
	}
	
	public SiteImpl(
			String siteNumber, 
			String siteName, 
			String siteAbbr, 
			String vistaServer, 
			int vistaPort, 
			String acceleratorServer,
	        int acceleratorPort,
	        String regionId) 
	throws MalformedURLException
	{
		this(siteNumber, 
				siteName, 
				siteAbbr, 
				vistaServer, 
				vistaPort, 
				acceleratorServer, 
				acceleratorPort, 
				regionId, 
				true, 
				true, 
				translateSiteConnections(vistaServer, vistaPort, acceleratorServer, acceleratorPort));
	}
	
	/**
	 * 
	 * @param siteNumber
	 * @param siteName
	 * @param siteAbbr
	 * @param vistaServer
	 * @param vistaPort
	 * @param acceleratorServer
	 * @param acceleratorPort
	 * @param regionId
	 * @throws MalformedURLException 
	 */
	public SiteImpl(
		String siteNumber, 
		String siteName, 
		String siteAbbr, 
		String vistaServer, 
		int vistaPort, 
		String acceleratorServer,
        int acceleratorPort,
        String regionId,
        boolean sitePatientLookupable,
		boolean siteUserAuthenticatable,
		Map<String, SiteConnection> siteConnections) 
	throws MalformedURLException
    {
		this(
			regionId, 
			siteNumber, 
			siteName, siteAbbr, 
			sitePatientLookupable, siteUserAuthenticatable,
			siteConnections,
			createDefaultSiteURLs(vistaServer, vistaPort, acceleratorServer, acceleratorPort) 
		);
    }
	
	public SiteImpl(
			String siteNumber, 
			String siteName, 
			String siteAbbr, 
			String vistaServer, 
			int vistaPort, 
			String acceleratorServer,
	        int acceleratorPort,
	        String regionId,
	        boolean sitePatientLookupable,
			boolean siteUserAuthenticatable) 
		throws MalformedURLException
	    {
			this(
				regionId, 
				siteNumber, 
				siteName, siteAbbr, 
				sitePatientLookupable, siteUserAuthenticatable,
				translateSiteConnections(vistaServer, vistaPort, acceleratorServer, acceleratorPort),
				createDefaultSiteURLs(vistaServer, vistaPort, acceleratorServer, acceleratorPort) 
			);
	    }
	
	private static Map<String, SiteConnection> translateSiteConnections(String vistaServer, 
			int vistaPort, 
			String acceleratorServer,
	        int acceleratorPort)
    {
		Map<String, SiteConnection> siteConnections = new HashMap<String, SiteConnection>();
		siteConnections.put(SiteConnection.siteConnectionVista, 
				new SiteConnection(SiteConnection.siteConnectionVista, vistaServer, vistaPort));
		siteConnections.put(SiteConnection.siteConnectionVix, 
				new SiteConnection(SiteConnection.siteConnectionVix, acceleratorServer, acceleratorPort));
		return siteConnections;
    }
	
	/**
	 * This constructor makes a effort to convert the URLs into SiteConnections. This should not be used for production, only testing purposes
	 * @param regionId
	 * @param siteNumber
	 * @param siteName
	 * @param siteAbbr
	 * @param servers
	 */
	public SiteImpl(
			String regionId,
			String siteNumber, 
			String siteName, 
			String siteAbbr,
			URL... servers)
	{
		this(regionId, siteNumber, siteName, siteAbbr, true, true, translateUrlsToSiteConnections(servers), servers);
	}
	
	private static Map<String, SiteConnection> translateUrlsToSiteConnections(URL [] servers)
	{
		Map<String, SiteConnection> siteConnections = new HashMap<String, SiteConnection>();
		for(URL server : servers)
		{
			if(server.getProtocol().equals(VISTA_PROTOCOL))
			{
				siteConnections.put(SiteConnection.siteConnectionVista, 
						new SiteConnection(SiteConnection.siteConnectionVista, server.getHost(), server.getPort()));
			}
			else if(server.getProtocol().equals(VFTP_PROTOCOL))
			{
				siteConnections.put(SiteConnection.siteConnectionVix, 
						new SiteConnection(SiteConnection.siteConnectionVix, server.getHost(), server.getPort()));
			}
		}
		
		return siteConnections;
	}

	/**
	 * 
	 * @param regionId
	 * @param siteNumber
	 * @param siteName
	 * @param siteAbbr
	 * @param servers
	 */
	public SiteImpl(
		String regionId,
		String siteNumber, 
		String siteName, 
		String siteAbbr,
		boolean sitePatientLookupable,
		boolean siteUserAuthenticatable,
		Map<String, SiteConnection> siteConnections,
		URL... servers)
	{
	    super(WellKnownOID.VA_RADIOLOGY_IMAGE.getCanonicalValue(), siteNumber, servers, servers);
	    this.regionId = regionId;
	    this.siteName = siteName;
	    this.siteAbbr = siteAbbr;
	    this.sitePatientLookupable = sitePatientLookupable;
	    this.siteUserAuthenticatable = siteUserAuthenticatable; 
	    this.siteConnections = siteConnections;
	}
	
	public SiteImpl(
			String regionId,
			String siteNumber, 
			String siteName, 
			String siteAbbr,
			boolean sitePatientLookupable,
			boolean siteUserAuthenticatable,
			URL... servers)
		{
			this(regionId, siteNumber, siteName, siteAbbr, 
					sitePatientLookupable, siteUserAuthenticatable, 
					translateUrlsToSiteConnections(servers), servers);
		}
	
	
	
	public String getServerHost(String protocol)
	{
		URL server = getAvailableMetadataServer(protocol);
		return server == null ? null : server.getHost();
	}
	
	public int getServerPort(String protocol)
	{
		URL server = getAvailableMetadataServer(protocol);
		return server == null ? 0 : server.getPort();
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.configuration.ISite#hasAcceleratorServer()
	 */
	public boolean hasAcceleratorServer() {
		return 
			getAcceleratorPort() > 0 && 
			getAcceleratorServer() != null && 
			!getAcceleratorServer().equals(""); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.configuration.ISite#getAcceleratorPort()
	 */
	public int getAcceleratorPort() 
	{
		return getServerPort(VFTP_PROTOCOL);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.configuration.ISite#getAcceleratorServer()
	 */
	public String getAcceleratorServer() 
	{
		return getServerHost(VFTP_PROTOCOL);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.configuration.ISite#getSiteAbbr()
	 */
	public String getSiteAbbr() {
		return siteAbbr;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.configuration.ISite#getSiteName()
	 */
	public String getSiteName() {
		return siteName;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.configuration.ISite#getSiteNumber()
	 */
	public String getSiteNumber() 
	{
		return getRepositoryId();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.configuration.ISite#getVistaPort()
	 */
	public int getVistaPort() 
	{
		return getServerPort(VISTA_PROTOCOL);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.configuration.ISite#getVistaServer()
	 */
	public String getVistaServer() 
	{
		return getServerHost(VISTA_PROTOCOL);		// "vistajeni" ???
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Site#getRegionId()
	 */
	@Override
	public String getRegionId() {
		return regionId;
	}

	@Override
	public String toString() 
	{
		return 
			this.siteName + " [" + this.siteAbbr + "] - " + this.getSiteNumber() + //$NON-NLS-1$ //$NON-NLS-2$
			(getVistaServer() != null ? " Vista(" + getVistaServer() + "):" + getVistaPort() : "") + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			(getAcceleratorServer() != null ? " VixS(" + getAcceleratorServer() + "):" + getAcceleratorPort() : "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {		
		if(obj.getClass() == SiteImpl.class) {
			SiteImpl s = (SiteImpl)obj;
			return this.getSiteNumber().equalsIgnoreCase(s.getSiteNumber());
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Site#isSiteUserAuthenticatable()
	 */
	@Override
	public boolean isSiteUserAuthenticatable()
	{
		return siteUserAuthenticatable;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Site#isSitePatientLookupable()
	 */
	@Override
	public boolean isSitePatientLookupable()
	{
		return sitePatientLookupable;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Site#getSiteConnections()
	 */
	@Override
	public Map<String, SiteConnection> getSiteConnections()
	{
		return siteConnections;
	}
}
