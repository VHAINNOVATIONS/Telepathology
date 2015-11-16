/**
 * 
 */
package gov.va.med.imaging.core.interfaces.exceptions;

import gov.va.med.imaging.ImageURN;

/**
 * An exception thrown when the checksum between a local and a transmitted
 * copies of a image fail to compare for equality.
 * 
 * @author vhaiswbeckec
 *
 */
public class ChecksumComparisonFailedException 
extends MethodException
{
	private static final long serialVersionUID = 8416491369129062879L;
	private final static String msgStart = "For the image '";
	private final static String msgEnd = "' the locally calculated checksum value differs froms the expected (transmitted) value.  Image may have been corrupted in transport.";
	
	/**
	 * 
	 */
	public ChecksumComparisonFailedException(ImageURN imageUrn)
	{
		super( msgStart + imageUrn.toString() + msgEnd );
	}
}
