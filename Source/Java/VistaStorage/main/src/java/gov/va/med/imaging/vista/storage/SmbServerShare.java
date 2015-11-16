/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 20, 2010
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
package gov.va.med.imaging.vista.storage;

import java.net.MalformedURLException;

/**
 * Represents a server share to access an image file. Makes it easier to get the server host, file path and the port to access the image
 * 
 * @author vhaiswwerfej
 *
 */
public class SmbServerShare
{
	private final String filename;
	private int port; // allowed to be changed
	private final String server;
	private final String filePath;
	
	public final static int netbiosPort = 139;
	public final static int smbPort = 445;
	
	/**
	 * Sorted array of the possible ports used to connect to SMB shares
	 */
	public final static int [] possibleConnectionPorts = 
		new int[] { smbPort, netbiosPort };
	
	public final static int defaultServerSharePort = smbPort;
	
	public SmbServerShare(String filename, int port)
	throws MalformedURLException
	{
		this.filename = filename;
		this.port = port;
		
		String networkPath = filename;
		if (networkPath.startsWith("\\")) {
			networkPath = networkPath.substring(2);
		}
		networkPath = networkPath.replace('\\', '/');
		
		int loc = networkPath.indexOf("/");
		if(loc >= 0)
		{
			this.server = networkPath.substring(0, loc);
			this.filePath = networkPath.substring(loc);
		}
		else
		{
			throw new MalformedURLException("MethodException, Filename '" + filename + "' does not contain a '\\' character and cannot be parsed into a SmbServerShare object.");		
		}		
	}
	
	public SmbServerShare(String filename)
	throws MalformedURLException
	{
		this(filename, defaultServerSharePort);		
	}
	
	public String getSmbPath()
	{
		return "SMB://" + server + ":" + port + filePath;
	}

	public String getFilename()
	{
		return filename;
	}

	public int getPort()
	{
		return port;
	}

	public String getServer()
	{
		return server;
	}

	public String getFilePath()
	{
		return filePath;
	}

	public void setPort(int port)
	{
		this.port = port;
	}
}
