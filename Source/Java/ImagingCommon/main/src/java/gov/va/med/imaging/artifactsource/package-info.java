/**
 * <b>ArtifactSource versus ResolvedArtifactSource</b>
 * <p>
 * An ArtifactSource is the internal representation of any source of artifacts.  The artifact source includes the
 * protocols that the source speaks, where the protocol is that used internally by the VISA core to route
 * a request and is NOT the actual "line" protocol. For example, internally access to another VISA server may 
 * indicate the "VFTP" protocol as the means to contact it.  VFTP (VIX Federation Transfer Protocol) uses SOAP 
 * web services over HTTP as the actual line protocol and serialization mechanism.
 * 
 * A ResolvedArtifactSource must provide a sorted set of InterfaceURLs (a URL for metadata and for image data)
 * for the Provider to preferentially create data source providers.
 * 
 * The URLs in the ResolvedArtifactSource may be interpreted or modified by the DataSource that eventually
 * contacts the data source.
 * 
 * </p>
 * <b>Gateway versus Repository</b>
 * <p>
 * A gateway is a single point of access to multiple repositories.  In general a repository is accessible either through
 * a gateway or the repository but not both.  If a repository can be accessed either through a gateway or directly then
 * the routing will attempt the repository first and then the gateway because sorting of ArtifactSource and RoutingToken
 * are both done from most to least specific as the primary sort order.   
 * </p>
 */
package gov.va.med.imaging.artifactsource;
