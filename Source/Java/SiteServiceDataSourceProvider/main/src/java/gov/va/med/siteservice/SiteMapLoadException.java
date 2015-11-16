package gov.va.med.siteservice;

public class SiteMapLoadException 
extends Exception
{
	private static final long serialVersionUID = 1L;
	private final boolean remoteLoad;
	
	public SiteMapLoadException(String message, boolean remoteLoad)
	{
		super(message);
		this.remoteLoad = remoteLoad;
	}

	public SiteMapLoadException(Throwable cause, boolean remoteLoad)
	{
		super(cause);
		this.remoteLoad = remoteLoad;
	}

	/**
	 * 
	 * @return true if the exception occurred when doing a load from a remote source,
	 * else exception occurred when loading from a local source.
	 */
	public boolean isRemoteLoad()
    {
    	return remoteLoad;
    }
}
