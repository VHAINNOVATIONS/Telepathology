/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 6, 2008
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.imaging.vista.storage;

import gov.va.med.imaging.core.interfaces.ImageStorageFacade;
import gov.va.med.imaging.core.interfaces.StorageCredentials;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNearLineException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.exchange.enums.StorageProximity;
import gov.va.med.imaging.exchange.storage.AbstractBufferedImageStorageFacade;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedImageInputStream;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedImageStreamResponse;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedInputStream;
import gov.va.med.imaging.exchange.storage.DataSourceImageInputStream;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.vista.storage.configuration.VistaStorageConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;
import jcifs.util.transport.TransportException;

import org.apache.log4j.Logger;

/**
 * Utility functions to open image shares and retrieve files
 * 
 * @author VHAISWWERFEJ
 *
 */
public class SmbStorageUtility
extends AbstractBufferedImageStorageFacade
implements ImageStorageFacade
{
	private final static Logger logger = Logger.getLogger(SmbStorageUtility.class);

	public final static int DEFAULT_MAX_RETRIES = 3;
	public final static long DEFAULT_RETRY_DELAY = 2000L;

	// parameters that affect retry logic for off-line images.
	private int maxNearLineRetries = DEFAULT_MAX_RETRIES;
	private long nearLineRetryDelay = DEFAULT_RETRY_DELAY;
	
	//private int defaultBufferSize = 1024 * 16; // 16K buffer seemed to give optimal performance	
	
	private int getNearLineRetries()
    {
	    return maxNearLineRetries;
    }

	public long getNearLineRetryDelay()
    {
    	return nearLineRetryDelay;
    }
	
	static
	{
		 logger.info("JCIFS Configuration - jcifs.netbios.cachePolicy = " + jcifs.Config.getProperty("jcifs.netbios.cachePolicy") );
         logger.info("JCIFS Configuration - jcifs.smb.client.soTimeout = " + jcifs.Config.getProperty("jcifs.smb.client.soTimeout") );
         logger.info("JCIFS Configuration - jcifs.smb.client.responseTimeout = " + jcifs.Config.getProperty("jcifs.smb.client.responseTimeout") );
	}

//	static {
//		Properties props = new Properties();
//		props.put("jcifs.netbios.cachePolicy", "3600"); //  cache the network names for 1 hour (value in seconds, 0 is no caching, -1 is forever)
//		// JMW 1/3/2008
//		// Set the timeout values, these are supposed to be the default values but for some reason they were not set by default properly.
//		// We should do some experimenting with these values to be sure they are sufficient to access images across the WAN
//		props.put("jcifs.smb.client.soTimeout", "35000");
//		props.put("jcifs.smb.client.responseTimeout", "35000");
//		jcifs.Config.setProperties(props);
//	}
	
	/**
	 * Change the extension of a file
	 * @param filename The filename to change
	 * @param newExtension The new extension for the filename (do not include the '.')
	 * @return
	 */
	private String changeFileExtension(String filename, String newExtension) {
		String fname = filename;
		int loc = filename.lastIndexOf(".");
		if(loc >= 0) {
			fname = filename.substring(0, loc);
			fname += "." + newExtension;
		}
		return fname;
	}
	
	private ByteBufferBackedImageStreamResponse openFileStream(SmbCredentials smbCredentials, 
			NtlmPasswordAuthentication ntPassAuth, StorageProximity imageProximity)
	throws ImageNotFoundException, ImageNearLineException, SmbException, IOException
	{
		SmbServerShare smbServerShare = smbCredentials.getSmbServerShare();
		logger.info("Opening image with URL '" + smbServerShare.getSmbPath() + "'.");
		ByteBufferBackedImageStreamResponse response = null;
		for(int nearlineRetry=0; nearlineRetry < getNearLineRetries(); ++nearlineRetry)
		{			
			SmbFile imageFile = new SmbFile(smbServerShare.getSmbPath(), ntPassAuth);
			// logger.info("Image opened");
			if( imageFile.canRead() ) 
			{
				int fileLength = (int)imageFile.length();
				if(fileLength > 0)
				{
					logger.info("File '" + smbServerShare.getSmbPath() + "' has fileLength=" + fileLength + ", reading image into buffer.");
					response = new ByteBufferBackedImageStreamResponse(
							new ByteBufferBackedImageInputStream(imageFile.getInputStream(), 
									(int)imageFile.length(), true));
					logger.info("File '" + smbServerShare.getSmbPath() + "' read into buffer.");
					return response;
				}
				else
				{
					// was throwing FileNotFoundException (extends IOException) - changed to ImageNotFoundException and improved error message
					throw new ImageNotFoundException("File [" + smbServerShare.getSmbPath() + "] has length of [" + fileLength + "], not greated than 0 therefore no image data");
				}
			}
			// if we cannot read the file and the problem is not that the image is
			// probably near-line then bug out
			else if( imageProximity == null || imageProximity != StorageProximity.NEARLINE ) 
				throw new ImageNotFoundException("Cannot read image file " + smbServerShare.getSmbPath() + ", indicates file does not exist on storage system.");
		}
		// if we get to here then the all nearline retries have been exhausted
		// throw a near-line exception
		throw new ImageNearLineException("Cannot read Near-Line image file '" + smbServerShare.getSmbPath() + "' yet, retry later.");						
	}
	
	private List<Integer> getSortedConnectionPorts(SmbServerShare smbServerShare)
	{
		List<Integer> connectionPorts = new ArrayList<Integer>();
		SmbConnectionInformationManager smbConnectionInformationManager = getSmbConnectionInformationManager();
		int firstTryPort = 
			smbConnectionInformationManager.getSuccessfulPort(smbServerShare.getServer(), 
					SmbServerShare.defaultServerSharePort);
		connectionPorts.add(firstTryPort);
		
		for(int port : SmbServerShare.possibleConnectionPorts)
		{
			// don't add the initial port again
			if(port != firstTryPort)
				connectionPorts.add(port);
		}		
		return connectionPorts;
	}
	
	private SmbConnectionInformationManager getSmbConnectionInformationManager()
	{
		return SmbConnectionInformationManager.getSmbConnectionInformationManager();
	}
	
	private void updateSuccessfulPort(SmbServerShare smbServerShare)
	{
		if(smbServerShare != null)
		{
			SmbConnectionInformationManager smbConnectionInformationManager = 
				getSmbConnectionInformationManager();
			smbConnectionInformationManager.updateSuccessfulPort(smbServerShare.getServer(), 
					smbServerShare.getPort());
		}
	}
	
	/**
	 * Open the input stream for the file and create a SizedInputStream that contains the filesize
	 * @param filename The filename to open (full UNC path)
	 * @param storageCredentials The network location that stores the file (with credentials set)
	 * @param imageProximity The current location of the image (magnetic, worm, offline)
	 * @return The SizedInputStream with the input stream open and set to the desired file and the number of bytes from the file set
	 * @throws ImageNearLineException Occurs if the image is on a jukebox and is not readable
	 * @throws ImageNotFoundException Occurs if the image does not exist (cannot be read and is on magnetic)
	 */
	private ByteBufferBackedImageStreamResponse openFileStream(
			String filename, 
			StorageCredentials storageCredentials, 
			StorageProximity imageProximity)
	throws ImageNearLineException, ImageNotFoundException, MethodException
	{
		SmbServerShare smbServerShare = null;
		try
		{
			smbServerShare = new SmbServerShare(filename);
		}
		catch(MalformedURLException murlX)
		{
			String msg = "MalformedURLException creating smb server share, " + murlX.getMessage();
			logger.error(msg, murlX);
			throw new MethodException(murlX);			
		}
		List<Integer> connectionPorts = getSortedConnectionPorts(smbServerShare);
		
		NtlmPasswordAuthentication ntPassAuth = null;
		
		Iterator<Integer> portIterator = connectionPorts.iterator();
		while(portIterator.hasNext())
		{
			try
			{
				int connectionPort = portIterator.next();
				smbServerShare.setPort(connectionPort);
				SmbCredentials smbCredentials = SmbCredentials.create(smbServerShare, 
						storageCredentials);
				// ntPassAuth doesn't change based on connection port, so only create it once
				if(ntPassAuth == null)
				{
					ntPassAuth = 
						new NtlmPasswordAuthentication(smbCredentials.getDomain(), smbCredentials.getUsername(), 
								smbCredentials.getPassword());
				}
				ByteBufferBackedImageStreamResponse response = 
					openFileStream(smbCredentials, ntPassAuth, imageProximity);
				updateSuccessfulPort(smbServerShare);
				return response;
			}
			catch(SmbException smbX)
			{
				// if the exception is a Connection Timeout, the root cause will be a TransportException
				logger.error(smbX);
				boolean includesRootCause = false;
				String msg = smbX.getMessage();
				if((msg == null) || (msg.length() <= 0))
				{
					if(smbX.getRootCause() != null)
					{
						msg = smbX.getRootCause().getMessage();
						includesRootCause = true;
					}
				}
				boolean throwException = true;
				if((smbX.getRootCause() != null) && (smbX.getRootCause() instanceof TransportException))
				{
					logger.warn("SmbException rootCause is TransportException, will attempt to use next port to connect. Error='" + smbX.getRootCause().getMessage() + "'.");
					// if it is a transport exception then it could be a connection timeout exception which
					// indicates the VIX is not connecting on the right port, want to try the next available port
					if(portIterator.hasNext())
					{
						
						// if there is another port to try, don't throw the exception just yet
						throwException = false;
					}
					else
					{
						logger.warn("No more available ports to connect to share with, will throw exception");
					}
				}
				else if((!includesRootCause) && (smbX.getRootCause() != null) && (smbX.getRootCause() instanceof UnknownHostException))
				{
					// if JCIFS has an UnknownHostException, it is caught here
					// just want to make sure unknownhost gets into the exception message
					msg += ", " + smbX.getRootCause().toString();
				}						
				
				if(throwException)
				{
					throw new ImageNotFoundException("SMBException opening SMB file '" + filename + "', NT status [" + smbX.getNtStatus() + "], " + msg, smbX);					
				}
			}
			catch(UnknownHostException uhX)
			{
				// this doesn't ever seem to be triggered, caught as SmbException
				logger.error(uhX);
				//throw new ImageNotFoundException("UnknownHostException opening SMB file '" + filename + "', " + uhX.getMessage(), uhX);
				// if there is an UnknownHostException, throw this as a MethodException since the VIX cannot resolve the host name - this is a problem which should be corrected!
				throw new MethodException("UnknownHostException opening SMB file '" + filename + "', " + uhX.getMessage(), uhX);
			}
			catch(IOException ioX)
			{
				logger.error(ioX);
				throw new ImageNotFoundException("IOException opening SMB file '" + filename + "', " + ioX.getMessage(), ioX);
			}
		}
		// if we've gotten here then we've run out of possible ports to try to connect to image shares on
		
		
		return null;
	}	
	
	/**
	 * This method reads in SMB file data to a byte buffer and returns an 
	 * InputStream over the in memory byte array.
	 * @param inStream SMB input stream to be read in memory
	 * @param streamLength in bytes
	 * @return InputStream fetchable inputstream of in memory byte aray
	 * @throws ImageNotFoundException
	 */
	/*
	private InputStream openSmbFileInStream(InputStream inStream, int streamLength)
	throws ImageNotFoundException
	{		 	
		// JMW 3/15/2010 
		// For patch 83, the streaming does NOT work properly because we don't properly close the input
		// stream in all cases.  Under heavy load this caused 0xC0000001 exceptions from JCIFS
		// this should be fixed for a future version of the VIX, for now the configuration file property
		// is ignored and will always read the image into the buffer.
		
		
		// if not reading full image into buffer, then open BufferedInputStream to file
		//if(!getVistaStorageConfiguration().isReadFileIntoBuffer())
		//{
		//	return new BufferedInputStream(inStream, defaultBufferSize);
		//}
		
		ByteArrayInputStream binStream = null;
		int len = 0;
		int bytesRead = 0;
		int iterationCount = 0;
		try
		{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(streamLength);			
			byte[] buffer = new byte[16 * 1024];
			long startTime = System.currentTimeMillis();
	        while ((len = inStream.read(buffer)) > 0) 
	        {
	        	iterationCount++;
	        	bytesRead += len;
	        	outputStream.write(buffer, 0, len);
	        }
	        long endTime = System.currentTimeMillis();
	        binStream = new ByteArrayInputStream(outputStream.toByteArray());
	        logger.info("Loaded file into memory with '" + iterationCount + "' iterations in '" + (endTime - startTime) + "' ms.");
	        outputStream = null;
	        if(bytesRead < streamLength)
	        {
	        	String msg = "Only read '" + bytesRead + "' of expected '" + streamLength + "', could mean did not read full stream!"; 
	        	logger.error(msg);
	        	throw new ImageNotFoundException(msg);
	        }
		}
		catch (IOException exc) {
			throw new ImageNotFoundException("IOException during SMB buffered data read = "
					+ exc);
		}
		finally {
			try {
				if (inStream != null)
					inStream.close();
			} 
			catch (IOException exc) {
			}
		}
		return (binStream);
	}*/

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.AbstractImageStorageFacade#openImageStreamInternal(java.lang.String, gov.va.med.imaging.core.interfaces.StorageCredentials, gov.va.med.imaging.exchange.enums.StorageProximity, gov.va.med.imaging.exchange.business.ImageFormatQualityList)
	 */
	@Override
	protected ByteBufferBackedImageStreamResponse openImageStreamInternal(String imageIdentifier,
		StorageCredentials imageCredentials,
		StorageProximity imageProximity,
		ImageFormatQualityList requestFormatQualityList)
	throws ImageNearLineException, ImageNotFoundException,
		ConnectionException, MethodException
	{
		ByteBufferBackedImageStreamResponse response = 
			openFileStream(imageIdentifier, imageCredentials, imageProximity);
		// set the data source response value, not needed in image conversion since set by http client
		// put in here so not changed by TXT file request
		if((response != null) && (response.getImageStream() != null))
		{					
			TransactionContext context = TransactionContextFactory.get();
			context.setDataSourceBytesReceived(new Long(response.getImageStream().getSize()));
		}
		return response;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.AbstractImageStorageFacade#openTXTStreamInternal(java.lang.String, gov.va.med.imaging.core.interfaces.StorageCredentials, gov.va.med.imaging.exchange.enums.StorageProximity)
	 */
	@Override
	protected ByteBufferBackedInputStream openTXTStreamInternal(String imageIdentifier,
		StorageCredentials imageCredentials, StorageProximity imageProximity)
	throws ImageNearLineException, ImageNotFoundException,
		ConnectionException, MethodException 
	{
		String txtFilename = changeFileExtension(imageIdentifier, "txt");
		ByteBufferBackedImageStreamResponse response = 
			openFileStream(txtFilename, imageCredentials, imageProximity);
		if(response != null)
			return response.getImageStream();
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.ImageStorageFacade#openPhotoId(java.lang.String, gov.va.med.imaging.core.interfaces.StorageCredentials)
	 */
	@Override
	public ByteBufferBackedInputStream openPhotoId(String imageIdentifier,
			StorageCredentials imageCredentials) 
	throws ImageNotFoundException, ConnectionException, MethodException 
	{
		try
		{
			ByteBufferBackedImageStreamResponse response = openFileStream(imageIdentifier, 
					imageCredentials, StorageProximity.ONLINE);
			if(response != null)
			{				
				return response.getImageStream();
			}
			throw new ConnectionException("Image stream response is null");
		}
		catch(ImageNearLineException inlX)
		{
			logger.error("Nearline exception getting photo id", inlX);
			throw new ImageNotFoundException(inlX);
		}
	}
	
	public DataSourceImageInputStream openFileInputStream(String uncFilePath,
			StorageCredentials imageCredentials) 
	throws ImageNotFoundException, ConnectionException, MethodException 
	{
		try
		{
			ImageStreamResponse response = openFileStream(uncFilePath, 
					imageCredentials, StorageProximity.ONLINE);
			if(response != null)
			{				
				return response.getImageStream();
			}
			throw new ConnectionException("Image stream response is null");
		}
		catch(ImageNearLineException inlX)
		{
			logger.error("Nearline exception getting photo id", inlX);
			throw new ImageNotFoundException(inlX);
		}
	}
	
	private VistaStorageConfiguration getVistaStorageConfiguration()
	{
		return VistaStorageConfiguration.getVistaStorageConfiguration();
	}
	
	public OutputStream openOutputStream(String filename, StorageCredentials storageCredentials) 
	throws  IOException
	{
		logger.info("Opening output stream to file [" + filename + "]");
		SmbFile file = getSmbFile(filename, storageCredentials);
		file.createNewFile();
		return file.getOutputStream();	
		
	}

	public void deleteFile(String filename, StorageCredentials storageCredentials) 
	throws  IOException
	{
		logger.info("Deleting file [" + filename + "]");
		SmbFile file = getSmbFile(filename, storageCredentials);
		file.delete();
	}

	public void copyFile(String sourcePath, String destinationPath, StorageCredentials storageCredentials) 
	throws  IOException
	{
		logger.info("Copying file [" + sourcePath + "] to file [" + destinationPath+ "]");
		SmbFile sourceFile = getSmbFile(sourcePath, storageCredentials);
		SmbFile destinationFile = getSmbFile(destinationPath, storageCredentials);
		sourceFile.copyTo(destinationFile);
	}

	public void copyRemoteFileToLocalFile(String remotePath, String localPath, StorageCredentials storageCredentials) 
	throws  IOException
	{
		logger.info("Copying file [" + remotePath + "] to file [" + localPath+ "]");
		SmbFile remoteFile = getSmbFile(remotePath, storageCredentials);
		SmbFileInputStream in = new SmbFileInputStream(remoteFile); 
		
		
		File localFile = new File(localPath);
		if(!localFile.exists()) {
			localFile.getParentFile().mkdirs();
			localFile.createNewFile();
		} 
		FileOutputStream out = new FileOutputStream(localFile, false); 

		byte[] buffer = new byte[16904]; 
		int read = 0; 
		while ((read = in.read(buffer)) > 0) 
		    out.write(buffer, 0, read); 
		
		in.close(); 
		out.close(); 
	}

	public void renameFile(String filename, String newFilename, StorageCredentials storageCredentials) 
	throws  IOException
	{
		logger.info("renaming file [" + filename + "]");
		SmbFile oldFile = getSmbFile(filename, storageCredentials);
		SmbFile newFile = getSmbFile(newFilename, storageCredentials);
		oldFile.renameTo(newFile);
	}

	public boolean fileExists(String filename, StorageCredentials storageCredentials) 
	throws  IOException
	{
		logger.info("Checking to see if file exists [" + filename + "]");
		SmbFile file = getSmbFile(filename, storageCredentials);
		return file.exists();
	}
	
	public String readFileAsString (String filename, StorageCredentials storageCredentials) 
	throws  IOException
	{
		logger.info("Reading file [" + filename + "] as string");
		SmbFile file = getSmbFile(filename, storageCredentials);
		
	    StringBuilder text = new StringBuilder();
	    String NL = System.getProperty("line.separator");
	    Scanner scanner = new Scanner(new SmbFileInputStream(file));
	    try {
	      while (scanner.hasNextLine()){
	        text.append(scanner.nextLine() + NL);
	      }
	    }
	    finally{
	      scanner.close();
	    }
	    
		return text.toString();
	}
	
	public void writeStringToFile(String fileContents, String filename, StorageCredentials storageCredentials) 
	throws  IOException
	{
		logger.info("Writing to file [" + filename + "]");
		
	    Writer out = new OutputStreamWriter(openOutputStream(filename, storageCredentials));
	    try {
	      out.write(fileContents);
	    }
	    finally {
	      out.close();
	    }	    

	}
	
	private SmbFile getSmbFile(String filename, StorageCredentials storageCredentials)
	throws MalformedURLException, SmbException 
	{
		SmbCredentials fileCredentials = SmbCredentials.create(new SmbServerShare(filename), 
				storageCredentials);
		SmbCredentials directoryCredentials = SmbCredentials.create(new SmbServerShare(this.getDirectory(filename)),
				storageCredentials);
		SmbFile file = null;
		SmbFile directory = null;

		NtlmPasswordAuthentication ntPassAuth = 
			new NtlmPasswordAuthentication(fileCredentials.getDomain(),
					fileCredentials.getUsername(),
					fileCredentials.getPassword());
		directory = new SmbFile(directoryCredentials.getSmbServerShare().getSmbPath(), ntPassAuth);
		if (!directory.exists()){
			directory.mkdirs();
		}
		file = new SmbFile(fileCredentials.getSmbServerShare().getSmbPath(), ntPassAuth);
		return file;
	}
	
	protected String getDirectory(String fullFileSpec){
		int endIndex = fullFileSpec.lastIndexOf("\\");
		return fullFileSpec.substring(0, endIndex);
	}
}
