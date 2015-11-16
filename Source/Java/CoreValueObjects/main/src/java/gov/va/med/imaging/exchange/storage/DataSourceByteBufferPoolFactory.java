/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 12, 2010
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

/**
 * Factory for reading images into buffers. Replaces the DataSourceByteBufferPoolManager
 * 
 * @author vhaiswwerfej
 *
 */
public class DataSourceByteBufferPoolFactory 
{
	private final static Logger logger = Logger.getLogger(DataSourceByteBufferPoolFactory.class);
	
	private final static int defaultBufferSize = 1024 * 16; // 16K buffer seemed to give optimal performance
	
	public static KnownSizeByteBuffer readIntoBuffer(String identifier, InputStream inputStream, 
		int size)
	throws IOException
	{
		if(size > 0)
		{
			// size of desired buffer is known
			logger.info("Reading file [" + identifier + "] into buffer of size [" + size + "]");
			ByteBuffer storeBuffer = ByteBuffer.allocate(size);
			
			byte[] byteBuffer = new byte[defaultBufferSize];
			int len;
			int bytesRead = 0;
	        while ((len = inputStream.read(byteBuffer)) > 0) 
	        {
	        	storeBuffer.put(byteBuffer, 0, len);
	        	bytesRead += len;
	        }
	        logger.info("Done reading file [" + identifier + "] into buffer, read [" + bytesRead + "] bytes");
	        return new KnownSizeByteBuffer(identifier, storeBuffer, bytesRead);
		}
		else
		{
			return readIntoBuffer(identifier, inputStream);
		}
	}
	
	public static KnownSizeByteBuffer readIntoBuffer(String identifier, InputStream inputStream)
	throws IOException
	{
		logger.info("Reading file [" + identifier + "] into buffer of unknown size");
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] byteBuffer = new byte[defaultBufferSize];
		int len;
		int bytesRead = 0;
        while ((len = inputStream.read(byteBuffer)) > 0) 
        {
        	bytesRead += len;
        	buffer.write(byteBuffer, 0, len);
        }
        ByteBuffer storeBuffer = ByteBuffer.allocate(bytesRead);
        storeBuffer.put(buffer.toByteArray());
        logger.info("Done reading file [" + identifier + "] into buffer, read [" + bytesRead + "] bytes");
        return new KnownSizeByteBuffer(identifier, storeBuffer, bytesRead);
	}

}
