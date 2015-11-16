/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 19, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.imaging.exchange.business;

/**
 * Represents a connection that can be made by the site
 * 
 * @author VHAISWWERFEJ
 *
 */
public class SiteConnection
{
	public final static String siteConnectionVista = "VISTA";
	public final static String siteConnectionVix = "VIX"; // non-SSL connection to VIX
	public final static String siteConnectionVixs = "VIXS"; // SSL secure connection to the VIX
	
	private final String protocol;
	private final String server;
	private final int port;
	/**
	 * @param protocol
	 * @param server
	 * @param port
	 */
	public SiteConnection(String protocol, String server, int port)
	{
		super();
		this.protocol = protocol;
		this.server = server;
		this.port = port;
	}
	/**
	 * @return the protocol
	 */
	public String getProtocol()
	{
		return protocol;
	}
	/**
	 * @return the server
	 */
	public String getServer()
	{
		return server;
	}
	/**
	 * @return the port
	 */
	public int getPort()
	{
		return port;
	}
}
