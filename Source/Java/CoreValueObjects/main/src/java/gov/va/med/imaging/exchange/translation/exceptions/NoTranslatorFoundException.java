/**
 * 
 */
package gov.va.med.imaging.exchange.translation.exceptions;

/**
 * An exception that is thrown when the code is unable to find a translation method from
 * the given source and destination class.
 * 
 * @author vhaiswbeckec
 *
 */
public class NoTranslatorFoundException
extends TranslationException
{
	private static final long serialVersionUID = -1334131696647631667L;

	
	private static String createMessage(Class<?>[] sourceClasses, Class<?> destinationClass)
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "No translator found to convert " );
		for(int index =0; index < sourceClasses.length; ++index)
		{
			if(index > 0) sb.append(',');
			sb.append( sourceClasses[index].getName() );
		}
		sb.append( " to " );
		sb.append( destinationClass );
		sb.append( "." );
		
		return sb.toString();
	}
	
	public NoTranslatorFoundException(Class<?>[] sourceClass, Class<?> destinationClass)
	{
		this(createMessage(sourceClass, destinationClass));
	}

	/**
	 * 
	 */
	public NoTranslatorFoundException()
	{
	}

	/**
	 * @param message
	 */
	public NoTranslatorFoundException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public NoTranslatorFoundException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NoTranslatorFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
