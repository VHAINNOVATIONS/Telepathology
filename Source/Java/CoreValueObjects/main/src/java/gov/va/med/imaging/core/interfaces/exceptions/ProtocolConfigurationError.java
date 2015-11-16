package gov.va.med.imaging.core.interfaces.exceptions;

import java.net.MalformedURLException;
import java.util.Map;

/**
 * This exception is thrown (or just created) when the URL Connection and/or
 * the URL connection handler cannot be found.
 * 
 * @author VHAISWBECKEC
 *
 */
public class ProtocolConfigurationError 
extends ConfigurationError
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param urlExceptions
	 * @return
	 */
	private static String createMessage(Map<String, MalformedURLException> urlExceptions)
	{
		StringBuilder sb = new StringBuilder();
		for(String protocol : urlExceptions.keySet())
			sb.append("A URLStreamHandler for the protocol '" + protocol + "' is not available, " + 
					"error is " + urlExceptions.get(protocol).getMessage() + "\n");
		
		sb.append("The protocol handler (URLStreamHandler derivations) packages should be referenced by the 'java.protocol.handler.pkgs' system property.\n");
		sb.append("See http://java.sun.com/developer/onlineTraining/protocolhandlers/ for an explanation of package and class naming requirements.");
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @param urlExceptions
	 */
	public ProtocolConfigurationError(Map<String, MalformedURLException> urlExceptions)
	{
		super(createMessage(urlExceptions));
	}
}
