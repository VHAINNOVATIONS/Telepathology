/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 3, 2010
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.exchange.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import gov.va.med.imaging.channels.ChecksumValue;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.exchange.FileTypeIdentifierStream;
import gov.va.med.imaging.exchange.enums.ImageFormat;

/**
 * Extends ByteBufferBackedInputStream, wraps the input stream in a FileTypeIdentifierStream so it can
 * determine the image format.  This type should be used when dealing with 'image' types (binary) objects
 * where the format of the image might be necessary.
 * 
 * @author vhaiswwerfej
 *
 */
public class ByteBufferBackedImageInputStream 
extends ByteBufferBackedInputStream 
implements DataSourceImageInputStream
{
	private ImageFormat imageFormat = null;
	private boolean emptyStream = false;
	
	public ByteBufferBackedImageInputStream(InputStream inputStream, int size, boolean readIntoBuffer, 
			ChecksumValue providedChecksum)
	throws IOException
	{
		super(new FileTypeIdentifierStream(inputStream), size, readIntoBuffer, providedChecksum);
		calculateImageFormat();
	}
	
	public ByteBufferBackedImageInputStream(InputStream inputStream, int size, boolean readIntoBuffer, 
			String providedChecksum)
	throws IOException
	{
		super(new FileTypeIdentifierStream(inputStream), size, readIntoBuffer, providedChecksum);
		calculateImageFormat();
	}
	
	public ByteBufferBackedImageInputStream(InputStream inputStream, int size, boolean readIntoBuffer)
	throws IOException
	{		
		super(new FileTypeIdentifierStream(inputStream), size, readIntoBuffer);
		calculateImageFormat();
	}
	
	public ByteBufferBackedImageInputStream(InputStream inputStream, int size)
	{
		super(new FileTypeIdentifierStream(inputStream), size);
		calculateImageFormat();
	}
	
	public ByteBufferBackedImageInputStream(ByteBuffer byteBuffer, int size)
	{
		super(byteBuffer, size);
		calculateImageFormat();
	}
	
	public ByteBufferBackedImageInputStream(ByteBufferBackedObject object)
	{
		super(object.getBuffer(), object.getSize());
		calculateImageFormat();
	}
	
	/**
	 * 
	 * @param inputStream
	 * @param size
	 * @param providedChecksum
	 */
	public ByteBufferBackedImageInputStream(InputStream inputStream, int size, ChecksumValue providedChecksum)	
	{
		super(new FileTypeIdentifierStream(inputStream), size, providedChecksum);
		calculateImageFormat();
	}
	
	public ByteBufferBackedImageInputStream(InputStream inputStream, int size, 
			String providedChecksum, boolean detectEmptyStream)	
	{
		super(new FileTypeIdentifierStream(inputStream), size, providedChecksum);
		calculateImageFormat(detectEmptyStream);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.DataSourceImageInputStream#getImageFormat()
	 */
	public ImageFormat getImageFormat()
	{
		return imageFormat;
	}
	
	private void calculateImageFormat()
	{
		calculateImageFormat(false);
	}
	
	/**
	 * Calculate the image format, either from the input stream or from the buffer
	 */
	private void calculateImageFormat(boolean detectEmptyStream)
	{
		if(isInputStreamReadable())
		{
			if(inputStream instanceof FileTypeIdentifierStream)
			{
				getLogger().info("Determining image format from stream");
				FileTypeIdentifierStream ftis = (FileTypeIdentifierStream)inputStream;
				if(detectEmptyStream)
				{
					try
					{
						this.imageFormat = ftis.getImageFormat(true);
					}
					catch(ImageNotFoundException infX)
					{
						// this stream is empty, we are marking it as such and not getting the image format
						// this would cause an issue if anything tried to use this input stream object since we 
						// don't have an ImageFormat specified but the exception should be thrown very soon
						this.imageFormat = null;
						emptyStream = true;
					}
				}
				else
				{
					this.imageFormat = ftis.getImageFormat();
				}
			}
		}
		else if(buffer != null)
		{
			InputStream inStream = null;
			try
			{
				getLogger().debug("Determining image format from buffer");
				inStream = getInputStreamFromBuffer();
				FileTypeIdentifierStream ftis = new FileTypeIdentifierStream(inStream);
				imageFormat = ftis.getImageFormat();
			}
			catch(IOException ioX)
			{
				getLogger().error("IOException while opening input stream to buffer to determine image format.", ioX);				
			}
			finally
			{
				// not really necessary since stream to memory, but here for good measure
				if(inStream != null)
				{
					try
					{
						inStream.close();
					}
					catch(Exception ex) {}
				}
			}			
		}
	}

	public boolean isEmptyStream()
	{
		return emptyStream;
	}
}
