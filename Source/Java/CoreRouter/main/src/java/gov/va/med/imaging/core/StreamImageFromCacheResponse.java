package gov.va.med.imaging.core;

import gov.va.med.imaging.exchange.business.ImageFormatQuality;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;

/**
 * On object to represent the result from receiving an image from the cache.
 * Contains the result message string and the number of bytes returned
 * 
 * @author VHAISWWERFEJ
 * 
 */
public class StreamImageFromCacheResponse
{
	private final String result;
	private final int bytesReturnedFromDataSource;
	private final ImageFormat imageFormat;
	private final ImageQuality imageQuality;

	/**
	 * @param result
	 * @param bytesReturnedFromDataSource
	 * @param imageFormat
	 * @param imageQuality
	 */
	public StreamImageFromCacheResponse(String result,
			int bytesReturnedFromDataSource, ImageFormat imageFormat,
			ImageQuality imageQuality)
	{
		super();
		this.result = result;
		this.bytesReturnedFromDataSource = bytesReturnedFromDataSource;
		this.imageFormat = imageFormat;
		this.imageQuality = imageQuality;
	}

	public StreamImageFromCacheResponse(String result,
			int bytesReturnedFromDataSource,
			ImageFormatQuality imageFormatQuality)
	{
		super();
		this.result = result;
		this.bytesReturnedFromDataSource = bytesReturnedFromDataSource;
		this.imageFormat = imageFormatQuality.getImageFormat();
		this.imageQuality = imageFormatQuality.getImageQuality();
	}

	/**
	 * @return the result
	 */
	public String getResult()
	{
		return this.result;
	}

	/**
	 * @return the bytesReturnedFromDataSource
	 */
	public int getBytesReturnedFromDataSource()
	{
		return this.bytesReturnedFromDataSource;
	}

	/**
	 * @return the imageFormat
	 */
	public ImageFormat getImageFormat()
	{
		return this.imageFormat;
	}

	/**
	 * @return the imageQuality
	 */
	public ImageQuality getImageQuality()
	{
		return this.imageQuality;
	}
}