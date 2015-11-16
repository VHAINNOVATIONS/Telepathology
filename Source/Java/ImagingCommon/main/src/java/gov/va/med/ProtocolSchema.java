/**
 * 
 */
package gov.va.med;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * An enumeration of the protocols that are currently known.
 * There is no guarantee that because a schema is listed here that the
 * connection handlers are installed.
 * 
 * @author vhaiswbeckec
 *
 */
public enum ProtocolSchema
{
	HTTP("http"),
	VFTP("vftp"),
	CDTP("cdtp"),
	EXCHANGE("exchange"),
	VISTA("vista"),
	VISTAIMAGING("vistaimaging"),
	XCA("xca");
	
	private final String schema;
	
	ProtocolSchema(String schema)
	{
		this.schema = schema;
	}

	@Override
	public String toString()
	{
		return schema;
	}
	
	/**
	 * Returns true if the connection handler is installed for the given
	 * schema.
	 * NOTE: this will work with the currently known list of schema but may
	 * not work generically and will definitely not work on a non-IP based protocol.
	 * 
	 * @param schema
	 * @return
	 */
	public boolean isConnectionHandlerInstalled()
	{
		try
		{
			URL url = new URL(this.toString(), "127.0.0.1", "");
			return url != null;
		}
		catch (MalformedURLException x)
		{
			return false;
		}
	} 
}
