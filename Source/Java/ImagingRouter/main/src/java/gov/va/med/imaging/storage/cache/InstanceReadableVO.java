package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.InstanceReadableByteChannel;

/**
 * A simple little value object to pass the checksum with the
 * byte channel.
 * 
 * @author VHAISWBECKEC
 *
 */
public class InstanceReadableVO
{
	private InstanceReadableByteChannel readByteChannel;
	private String checksumValue;
	
	public InstanceReadableVO(InstanceReadableByteChannel readByteChannel, String checksumValue)
	{
		this.readByteChannel = readByteChannel;
		this.checksumValue = checksumValue;
	}

	public String getChecksumValue()
	{
		return this.checksumValue;
	}

	public InstanceReadableByteChannel getReadByteChannel()
	{
		return this.readByteChannel;
	}
	
	
}