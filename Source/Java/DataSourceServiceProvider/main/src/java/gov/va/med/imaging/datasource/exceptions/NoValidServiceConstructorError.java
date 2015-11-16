package gov.va.med.imaging.datasource.exceptions;

import java.lang.reflect.InvocationTargetException;

/**
 * Thrown to indicate that the Provider was unable to create an instance of a
 * Service implementation because the Service implementation either did not
 * implement a valid constructor or the constructor was inaccessible.
 * Instances of this class will always have a Throwable cause of one of the
 * types:
 * SecurityException
 * NoSuchMethodException 
 * IllegalArgumentException
 * InstantiationException
 * IllegalAccessException
 * 
 * NOTE: this is an unchecked exception
 * 
 * @author VHAISWBECKEC
 *
 */
public class NoValidServiceConstructorError 
extends InvalidServiceImplementationError
{
	protected static final String ILLEGAL_ACCESS_MESSAGE = "The Service implementation must include a public constructor (with a single parameter of type URL).";
	protected static final String INSTANTIATION_EXCEPTION_MESSAGE = "The Service implementation could not be instantiated, see cause() for more details..";
	protected static final String INVALID_PARAMETERS_MESSAGE = "The Service implementation constructor failed because the provided parameter was not a URL.";
	protected static final String INVALID_CONSTRUCTOR_PARAMETERS_MESSAGE = "The Service implementation must include a (public) constructor with a single parameter of type URL.";
	protected static final String SECURITY_VIOLATION_MESSAGE = "Creating an instance of the Service failed due to a security violation.  Service implementations must be available on the classpath.";
	protected static final String CLASS_CAST_MESSAGE = "Creating an instance of the Service failed because the instantiated object did not implement the service provider interface.";
	protected static final String INVOCATION_TARGET_MESSAGE = "Creating an instance of the Service failed because the construction threw an invocation target exception.";
	
	protected static final long serialVersionUID = 1L;

	public NoValidServiceConstructorError(String componentName, SecurityException e)
	{
		super(
			componentName, 
			SECURITY_VIOLATION_MESSAGE, 
			e);
	}
	
	public NoValidServiceConstructorError(String componentName, NoSuchMethodException e)
	{
		super(
			componentName, 
			INVALID_CONSTRUCTOR_PARAMETERS_MESSAGE, 
			e);
	}
	
	public NoValidServiceConstructorError(String componentName, IllegalArgumentException e)
	{
		super(
			componentName, 
			INVALID_PARAMETERS_MESSAGE, 
			e);
	}
	
	public NoValidServiceConstructorError(String componentName, InstantiationException e)
	{
		super(
			componentName, 
			INSTANTIATION_EXCEPTION_MESSAGE, 
			e);
	}
	
	public NoValidServiceConstructorError(String componentName, IllegalAccessException e)
	{
		super(
			componentName, 
			ILLEGAL_ACCESS_MESSAGE, 
			e);
	}
	
	public NoValidServiceConstructorError(String componentName, ClassCastException e)
	{
		super(
			componentName, 
			CLASS_CAST_MESSAGE, 
			e);
	}
	
	public NoValidServiceConstructorError(String componentName, InvocationTargetException e)
	{
		super(
			componentName, 
			INVOCATION_TARGET_MESSAGE, 
			e);
	}
	
	protected NoValidServiceConstructorError(String componentName, Throwable e)
	{
		super(
			componentName, 
			ILLEGAL_ACCESS_MESSAGE, 
			e);
	}
	
}
