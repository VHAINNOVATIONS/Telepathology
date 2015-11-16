/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Mar 24, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.siteservice;

import java.io.Serializable;

/**
 * This class contains the URL path (file) information for a
 * specific protocol.  Instances of this class are stored in a map
 * keyed to a protocol in the SiteResolutionProviderMemento class. 
 * @author VHAISWBECKEC
 *
 */
public class ProtocolServerConfiguration
implements Serializable
{
	private static final long serialVersionUID = 1L;

	private boolean vistaServer;
	private String application;
	private String metadataPath;
	private String imagePath;

	/**
	 * Required default constructor for XML serialization
	 */
	public ProtocolServerConfiguration()
	{
		
	}
	
	/**
	 * 
	 * @param siteServiceServer
	 * @param application
	 * @param metadataPath
	 * @param imagePath
	 */
	public ProtocolServerConfiguration(
		boolean vistaServer, 
		String application, 
		String metadataPath,
        String imagePath)
    {
        super();
        this.vistaServer = vistaServer;
        this.application = application;
        this.metadataPath = metadataPath;
        this.imagePath = imagePath;
    }

	public void setVistaServer(boolean vistaServer)
    {
    	this.vistaServer = vistaServer;
    }

	public void setApplication(String application)
    {
    	this.application = application;
    }

	public void setMetadataPath(String metadataPath)
    {
    	this.metadataPath = metadataPath;
    }

	public void setImagePath(String imagePath)
    {
    	this.imagePath = imagePath;
    }

	public boolean isVistaServer()
    {
    	return vistaServer;
    }

	public String getApplication()
    {
    	return application;
    }

	public String getMetadataPath()
    {
    	return metadataPath;
    }

	public String getImagePath()
    {
    	return imagePath;
    }

	public String getAbsoluteMetadataPath()
	{
		return application + metadataPath;
	}
	
	public String getAbsoluteImagePath()
	{
		return application + imagePath;
	}

	@Override
    public int hashCode()
    {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((application == null) ? 0 : application.hashCode());
	    result = prime * result + ((imagePath == null) ? 0 : imagePath.hashCode());
	    result = prime * result + ((metadataPath == null) ? 0 : metadataPath.hashCode());
	    result = prime * result + (vistaServer ? 1231 : 1237);
	    return result;
    }

	@Override
    public boolean equals(Object obj)
    {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    final ProtocolServerConfiguration other = (ProtocolServerConfiguration) obj;
	    if (application == null)
	    {
		    if (other.application != null)
			    return false;
	    } else if (!application.equals(other.application))
		    return false;
	    if (imagePath == null)
	    {
		    if (other.imagePath != null)
			    return false;
	    } else if (!imagePath.equals(other.imagePath))
		    return false;
	    if (metadataPath == null)
	    {
		    if (other.metadataPath != null)
			    return false;
	    } else if (!metadataPath.equals(other.metadataPath))
		    return false;
	    if (vistaServer != other.vistaServer)
		    return false;
	    return true;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.getClass().getSimpleName() + "\n");
		sb.append( (isVistaServer() ? "VISTA Server" : "NOT VISTA Server") + "\n" );
		sb.append("metadata path = '" + this.getMetadataPath() + "'\n");
		sb.append("image path = '" + this.getImagePath() + "'\n");
		sb.append("absolute metadata path = '" + this.getAbsoluteImagePath() + "'\n");
		sb.append("absolute image path = '" + this.getAbsoluteImagePath() + "'\n");
		
		return sb.toString();
	}
	
}