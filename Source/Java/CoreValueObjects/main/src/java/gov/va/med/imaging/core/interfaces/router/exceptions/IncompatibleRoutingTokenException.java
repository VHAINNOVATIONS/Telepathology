/**
 * 
 */
package gov.va.med.imaging.core.interfaces.router.exceptions;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;

/**
 * @author vhaiswbeckec
 *
 */
public class IncompatibleRoutingTokenException
extends MethodException
{
	private static final long serialVersionUID = 1L;
	private Class<?> clazz;
	private String msg;
	
	private static final String NULL_TOKEN_MESSAGE = "The routing token was null and this command/SPI must have a non-null value.";
	private static final String NULL_HOME_TOKEN_MESSAGE = "The routing token included a null for the home community ID.  All routing tokens must have non-null home community and repository values.";
	private static final String NULL_REPOSITORY_TOKEN_MESSAGE = "The routing token included a null for the repository ID.  All routing tokens must have non-null home community and repository values.";
	private static final String WILDCARD_HOME_TOKEN_MESSAGE = "The routing token included a wildcard for the home community ID and this command/SPI must have a singular value.";
	private static final String WILDCARD_REPOSITORY_TOKEN_MESSAGE = "The routing token included a wildcard for the repository community ID and this command/SPI must have a singular value.";
	
	/**
	 * 
	 */
	private IncompatibleRoutingTokenException(Class<?> clazz, String msg)
	{
		this.clazz = clazz;
		this.msg = msg;
	}

	/**
	 * @param class1
	 * @return
	 */
	public static IncompatibleRoutingTokenException createInvalidNull(Class<?> clazz)
	{
		return new IncompatibleRoutingTokenException(clazz, NULL_TOKEN_MESSAGE);
	}

	/**
	 * @param class1
	 * @return
	 */
	public static IncompatibleRoutingTokenException createInvalidHomeCommunityWildcard(Class<?> clazz)
	{
		return new IncompatibleRoutingTokenException(clazz, WILDCARD_HOME_TOKEN_MESSAGE);
	}

	/**
	 * @param class1
	 * @return
	 */
	public static IncompatibleRoutingTokenException createInvalidRepositoryWildcard(Class<?> clazz)
	{
		return new IncompatibleRoutingTokenException(clazz, WILDCARD_REPOSITORY_TOKEN_MESSAGE);
	}

}
