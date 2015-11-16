/**
 * 
 */
package gov.va.med.imaging.exchange.translation.exceptions;

import java.lang.reflect.Method;
import java.util.List;

/**
 * An exception that is thrown when the code is unable to find a translation method from
 * the given source and destination class.
 * 
 * @author vhaiswbeckec
 *
 */
public class MultipleTranslatorFoundException
extends TranslationException
{
	private static final long serialVersionUID = -1334131696647631667L;

	
	private static String createMessage(Class<?>[] sourceClasses, Class<?> destinationClass, List<Method> candidateMethods)
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "Multiple translators found to convert " );
		for(int index =0; index < sourceClasses.length; ++index)
		{
			if(index > 0) sb.append(',');
			sb.append( sourceClasses[index].getName() );
		}
		sb.append( " to " );
		sb.append( destinationClass );
		sb.append( ".  " );
		
		sb.append("Potential translators are: ");
		boolean firstMethod = true;
		for(Method method : candidateMethods)
		{
			if(! firstMethod) sb.append(',');
			firstMethod = false;
			sb.append( method.getDeclaringClass().getName() );
			sb.append(".");
			sb.append( method.getName() );
		}
		sb.append( "." );
		
		return sb.toString();
	}
	
	public MultipleTranslatorFoundException(Class<?>[] sourceClass, Class<?> destinationClass, List<Method> candidateMethods)
	{
		this(createMessage(sourceClass, destinationClass, candidateMethods));
	}

	/**
	 * 
	 */
	public MultipleTranslatorFoundException()
	{
	}

	/**
	 * @param message
	 */
	public MultipleTranslatorFoundException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public MultipleTranslatorFoundException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MultipleTranslatorFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
