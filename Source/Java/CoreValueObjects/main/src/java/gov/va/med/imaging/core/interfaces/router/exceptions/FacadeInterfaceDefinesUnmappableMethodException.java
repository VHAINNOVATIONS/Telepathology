/**
 * 
 */
package gov.va.med.imaging.core.interfaces.router.exceptions;

import java.lang.reflect.Method;

/**
 * @author vhaiswbeckec
 *
 */
public class FacadeInterfaceDefinesUnmappableMethodException 
extends RouterInstantiationException 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static String methodNameToken = "%1";
	public final static String msg = "The method '%1' cannot be mapped to a router command.";
	
	public FacadeInterfaceDefinesUnmappableMethodException(Method method)
	{
		super( buildMessage(method) );
	}

	protected static String buildMessage(Method method) 
	{
		StringBuilder message = new StringBuilder();
		String methodName = method == null ? "<unknown>" : method.getName();
		
		int tokenIndex = msg.indexOf(methodNameToken);
		int tokenLength = methodNameToken.length();
		
		if(tokenIndex >= 0)
		{
			message.append(msg.substring(0, tokenIndex));
			message.append(methodName);
			if(tokenIndex + tokenLength <= msg.length())
				message.append(msg.substring(tokenIndex + tokenLength));
			
			return message.toString();
		}
		
		return msg;
	}
}
