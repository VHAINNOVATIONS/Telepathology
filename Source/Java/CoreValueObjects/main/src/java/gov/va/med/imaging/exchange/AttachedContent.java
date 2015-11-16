/**
 * 
 */
package gov.va.med.imaging.exchange;

import gov.va.med.MediaType;
import gov.va.med.imaging.channels.ChecksumValue;
import java.nio.channels.ByteChannel;

/**
 * @author vhaiswbeckec
 * 
 * Defines an interface for business objects that include
 * large binary content that is stored externally from the object.
 * This includes all of the Image and Document types, where the 
 * metadata is included in the object and the content is stored separately.
 *  
 */
public abstract class AttachedContent
{
	private String storageKey;
	private MediaType mediaType;
	private long contentLength;
	private ChecksumValue checksumValue;
	private String compressionType;
	
	/**
	 * 
	 */
	protected AttachedContent()
	{
		
	}
	
    /**
	 * @param storageKey
	 * @param mediaType
	 * @param contentLength
	 * @param checksumValue
	 * @param compressionType
	 */
	protected AttachedContent(String storageKey, MediaType mediaType,
			long contentLength, ChecksumValue checksumValue,
			String compressionType)
	{
		super();
		this.storageKey = storageKey;
		this.mediaType = mediaType;
		this.contentLength = contentLength;
		this.checksumValue = checksumValue;
		this.compressionType = compressionType;
	}

	public String getStorageKey()
    {
    	return storageKey;
    }
    
	public MediaType getMediaType()
	{
		return mediaType;
	}
	
	public ByteChannel getContent()
	{
		return null;
	}
	
	public long getContentLength()
	{
		return contentLength;
	}
	
	public ChecksumValue getChecksumValue()
	{
		return checksumValue;
	}

	public String getCompressionType()
	{
		return compressionType;
	}
	
}
