package gov.va.med.imaging.core.router.storage.providers;

import gov.va.med.imaging.StringUtil;
import gov.va.med.imaging.channels.ByteStreamPump;
import gov.va.med.imaging.core.interfaces.StorageCredentials;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.storage.StorageContext;
import gov.va.med.imaging.core.router.storage.StorageDataSourceRouter;
import gov.va.med.imaging.exchange.business.storage.Artifact;
import gov.va.med.imaging.exchange.business.storage.ArtifactInstance;
import gov.va.med.imaging.exchange.business.storage.ArtifactWriteResults;
import gov.va.med.imaging.exchange.business.storage.NetworkLocationInfo;
import gov.va.med.imaging.exchange.business.storage.Provider;
import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;
import gov.va.med.imaging.exchange.business.storage.StorageServerDatabaseConfiguration;
import gov.va.med.imaging.exchange.business.storage.StorageTransaction;
import gov.va.med.imaging.exchange.business.storage.TransferStatistics;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.vista.storage.SmbStorageUtility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Checksum;

import org.apache.log4j.Logger;

public abstract class StorageCIFSProvider extends Provider
{
	private static final long serialVersionUID = 1L;
	protected static final int FILE_NAME_LENGTH = 14;

	private static final Logger logger = Logger.getLogger(StorageCIFSProvider.class);
	private StorageServerConfiguration config = StorageServerConfiguration.getConfiguration();	
	
	public StorageCIFSProvider(Provider provider)
	{
		this.setActive(provider.isActive());
		this.setArchive(provider.isArchive());
		this.setId(provider.getId());
		this.setPlaceId(provider.getPlaceId());
		this.setPrimaryStorage(provider.isPrimaryStorage());
		this.setProviderType(provider.getProviderType());
		this.setWritable(provider.isWritable());
		this.setPlace(provider.getPlace());
	}

	// Business methods
	protected abstract NetworkLocationInfo getCurrentWriteLocation() throws MethodException, ConnectionException;
	
	@Override
	public ArtifactWriteResults writeArtifactStream(ReadableByteChannel artifactChannel,
			InputStream artifactStream,
			Artifact artifact,
			String originatingSiteId)
	throws MethodException, ConnectionException	
	{ 

		ArtifactWriteResults writeResults = null;

		// Pad the IEN out to the appropriate length for a filename
		String zeroPaddedIen = getZeroPaddedIen(artifact.getId());

		// Get the file extension
		String fileExtension = artifact.getArtifactDescriptor().getFileExtension();
		
		// Create the filename
		String filePathAndName = createFilePathAndName(originatingSiteId, zeroPaddedIen, fileExtension);

		// Get the current write location
		NetworkLocationInfo writeLocationInfo = this.getCurrentWriteLocation();

		// Write the file
		String fullFileSpec = writeLocationInfo.getPhysicalPath() + filePathAndName;
		Integer sizeInBytes=0;
		boolean writeSuccessful = true;
		long startTime = System.currentTimeMillis();
		Checksum checksum = null;
		
		String diagnosticMessage = ";  Error Writing Input Stream to " + fullFileSpec + ".";
		Exception writeException = null;
		CheckedOutputStream checkedOutputStream = null;
		SmbStorageUtility util = new SmbStorageUtility();
		try 
		{
			util = new SmbStorageUtility();
			OutputStream targetFileStream = util.openOutputStream(fullFileSpec, (StorageCredentials)writeLocationInfo);
			checkedOutputStream = new CheckedOutputStream(targetFileStream, new Adler32());

			// funnel in stream to out stream
			ByteStreamPump bSP = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.NetworkToFile);
			sizeInBytes = bSP.xfer(artifactStream, checkedOutputStream);
			checksum = checkedOutputStream.getChecksum();
			checkedOutputStream.flush();
			checkedOutputStream.close();
			logger.info("Wrote file: " + fullFileSpec);
		} 
		catch (IOException e) 
		{
		   	logger.error(this.getClass().getName()+"Failed to write stream to disk filename "+fullFileSpec);
			logger.error(e.getMessage() + diagnosticMessage, e);
		   	writeException = e;
		   	writeSuccessful = false;
			if(checkedOutputStream != null){
				try{
					checkedOutputStream.close();
				}
				catch(Exception X){
					//ignore.
				}
				try{
					util.deleteFile(fullFileSpec, (StorageCredentials)writeLocationInfo);
				}
				catch(Exception X){
					logger.error(this.getClass().getName()+": Failed to delete incomplete file "+fullFileSpec);
				}
			}
		} 
		catch (Exception e)
		{
		   	logger.error(this.getClass().getName()+"Failed to write stream to disk filename "+fullFileSpec);
		   	logger.error(e.getMessage() + diagnosticMessage, e);
		   	writeException = e;
		   	writeSuccessful = false;
			if(checkedOutputStream != null){
				try{
					checkedOutputStream.close();
				}
				catch(Exception X){
					//ignore.
				}
				try{
					util.deleteFile(fullFileSpec, (StorageCredentials)writeLocationInfo);
				}
				catch(Exception X){
					logger.error(this.getClass().getName()+": Failed to delete incomplete file "+fullFileSpec);
				}
			}
		}
		long durationInMilliseconds = System.currentTimeMillis() - startTime;
		
		if (writeSuccessful)
		{
			String fileName = createFileName(originatingSiteId, zeroPaddedIen, fileExtension);
			String filePath = createFilePath(originatingSiteId, zeroPaddedIen);
			
			// Create the ArtifactInstance record. 
			ArtifactInstance artifactInstance = createArtifactInstance(artifact,
					Integer.parseInt(writeLocationInfo.getNetworkLocationIEN()), fileName, filePath);
			artifact.getArtifactInstances().add(artifactInstance);
			
			// Create the TransferStatistics record
			createTransferStatistics(durationInMilliseconds, sizeInBytes);

			// Create the TransferStatistics record
			createStorageTransaction(StorageTransaction.WRITE_TRANSACTION, StorageTransaction.SUCCESS, "", artifact);

			// Create the write results
			writeResults = new ArtifactWriteResults(sizeInBytes, getChecksumAsString(checksum), artifactInstance);
		}
		else
		{
			createStorageTransaction(StorageTransaction.WRITE_TRANSACTION,
					StorageTransaction.FAILURE,
					writeException.getMessage(),
					artifact);
			throw new MethodException(writeException);
		}
		
		return writeResults;

	}

	private String getChecksumAsString(Checksum checksum) 
	{
		if (checksum != null)
		{
			return String.valueOf(checksum.getValue());
		}
		else
		{
			return "";
		}
	}

	private void createStorageTransaction(String transactionType, String status, String message, Artifact artifact)
	throws MethodException, ConnectionException
	{
		StorageTransaction storageTransaction = new StorageTransaction();
		
		storageTransaction.setTransactionType(transactionType);
		storageTransaction.setStatus(status);
		storageTransaction.setMessage(message);
		storageTransaction.setTransactionDateTime(new Date());
		storageTransaction.setInitApp(artifact.getCreatedBy());
		storageTransaction.setArtifact(artifact);
		storageTransaction.setProvider(this);
		StorageContext.getDataSourceRouter().postStorageTransaction(storageTransaction);

	}

	@Override
	public InputStream getArtifactStream(ArtifactInstance artifactInstance)
	throws MethodException, ConnectionException, FileNotFoundException
	{ 
		StorageDataSourceRouter router = StorageContext.getDataSourceRouter();

		// Get the network location IEN and the filename from the URL
		int networkLocationId = artifactInstance.getDiskVolume();
		String filePath = artifactInstance.getFilePath() + artifactInstance.getFileRef();
		
		// Get the physical path using the network location IEN, and create the fully qualified
		// filename
		NetworkLocationInfo info = router.getNetworkLocationDetails(Integer.toString(networkLocationId));
		String fullFileSpec = info.getPhysicalPath() + filePath;

		SmbStorageUtility util = new SmbStorageUtility();
		return util.openFileInputStream(fullFileSpec, (StorageCredentials)info).getInputStream();
	}
	
	@Override
	public boolean canRetrieveFromArtifactInstance(ArtifactInstance instance) throws MethodException
	{ 
		return (instance.getProviderId() == this.getId());		
	}

	protected ArtifactInstance createArtifactInstance(Artifact artifact, int networkLocationId,
			String fileName, String filePath)
	throws MethodException, ConnectionException 
	{
		ArtifactInstance artifactInstance = new ArtifactInstance();
		artifactInstance.setArtifact(artifact);
		artifactInstance.setProvider(this);
		artifactInstance.setDiskVolume(networkLocationId);
		artifactInstance.setFilePath(filePath);
		artifactInstance.setFileRef(fileName);
		artifactInstance = StorageContext.getDataSourceRouter().postArtifactInstance(artifactInstance);
		return artifactInstance;
	}
	
	protected ArtifactInstance updateArtifactInstanceLocation(ArtifactInstance artifactInstance,
			int networkLocationId, String fileName, String filePath)
	throws MethodException, ConnectionException 
	{
		artifactInstance.setDiskVolume(networkLocationId);
		artifactInstance.setFilePath(filePath);
		artifactInstance.setFileRef(fileName);
		artifactInstance = StorageContext.getDataSourceRouter().putArtifactInstanceUrl(artifactInstance);
		return artifactInstance;
	}
	
	protected TransferStatistics createTransferStatistics(long durationInMilliseconds, long sizeInBytes)
	throws MethodException, ConnectionException 
	{
		TransferStatistics transferStatistics = new TransferStatistics(
				new Date(), 
				durationInMilliseconds, 
				sizeInBytes, 
				this, 
				getPlace());
		
		transferStatistics = StorageContext.getDataSourceRouter().postTransferStatistics(transferStatistics);
		return transferStatistics;
	}
	
	public static String createFilePathAndName(String divisionNumber, String zeroPaddedIen, String fileExtension) 
	{
		// Create the filename
		String fileName = createFileName(divisionNumber, zeroPaddedIen, fileExtension);

		// Create the filepath
		String filePath = createFilePath(divisionNumber, zeroPaddedIen);
		
		return filePath + fileName;
	}

	
	public static String createFileName(String divisionNumber, String zeroPaddedIen, String fileExtension) 
	{

		// The full filename is the division number from config, plus an
		// underscore, plus the zero-padded ien, plus the extension, if any...
		String fileName = divisionNumber + "_" + zeroPaddedIen;
		
		if (fileExtension != null && !fileExtension.trim().equals(""))
		{
			if (!fileExtension.startsWith("."))
			{
				fileName += ".";
			}
			
			fileName += fileExtension;
		}
		
		return fileName;
	}


	public static String createFilePath(String divisionNumber, String zeroPaddedIen) 
	{
		// First, hash the padded ien into directories
		String hashedIenPortionOfPath = hashPaddedIenIntoDirectory(zeroPaddedIen);
		
		// The full path is the division number from config followed by the 
		// hashed filename(ien) portion
		return divisionNumber + "\\" + hashedIenPortionOfPath;
	}

	public static String getZeroPaddedIen(int id) 
	{
		return String.format("%0" + FILE_NAME_LENGTH + "d", id);
	}

	public static String hashPaddedIenIntoDirectory(String paddedIen) 
	{
		// Only hash the first 12 characters of the filename. This will leave a max of 100
		// files per directory
		paddedIen = paddedIen.substring(0,12);
		String[] directoryNames = StringUtil.breakString(paddedIen, 2);
		
		StringBuffer buffer = new StringBuffer();
		for (int i=0; i< directoryNames.length; i++)
		{
			buffer.append(directoryNames[i]);
			buffer.append("\\");
		}
		
		return buffer.toString();
	}
}
