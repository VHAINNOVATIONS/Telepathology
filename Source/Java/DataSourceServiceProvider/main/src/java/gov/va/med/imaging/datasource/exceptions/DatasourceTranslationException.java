/**
 * 
 */
package gov.va.med.imaging.datasource.exceptions;

/**
 * The root exception thrown when a DataSource is unable to translate
 * from its external format to the business object format or vice-versa.
 * 
 * @author vhaiswbeckec
 *
 */
public class DatasourceTranslationException
extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DatasourceTranslationException()
	{
		super();
	}

	/**
	 * @param message
	 */
	public DatasourceTranslationException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public DatasourceTranslationException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DatasourceTranslationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
