/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 9, 2009
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
package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.ImageURNFactory;
import gov.va.med.PatientIdentifier;
import gov.va.med.imaging.AbstractImagingURN;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.conversion.ImageConversionFilePath;
import gov.va.med.imaging.conversion.ImageConversionUtility;
import gov.va.med.imaging.core.interfaces.exceptions.*;
import gov.va.med.imaging.datasource.ImageDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.*;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.enums.StorageProximity;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedImageInputStream;
import gov.va.med.imaging.exchange.storage.DataSourceImageInputStream;
import gov.va.med.imaging.exchange.storage.DataSourceInputStream;
import gov.va.med.imaging.protocol.vista.VistaImagingTranslator;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.url.vista.image.ImagingStorageCredentials;
import gov.va.med.imaging.url.vista.storage.VistaImagingShortTermMetadataCache;
import gov.va.med.imaging.url.vista.storage.VistaImagingStorageManager;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Abstract base VistA Image data source class for implementing the common functionality needed to retrieve an
 * image from VistA.  Most of the functionality necessary is common between versions of the interfaces, those common
 * functions are here.
 * 
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractBaseVistaImageService 
extends AbstractVistaImagingDataSourceService
implements ImageDataSourceSpi
{
	private final static Logger logger = Logger.getLogger(AbstractBaseVistaImageService.class);
	private final static String IMAGE_LOCATION_OFFLINE = "O";
	private final static String IMAGE_LOCATION_WORM = "W";	
	
	private static VistaImagingShortTermMetadataCache metadataCache = 
		new VistaImagingShortTermMetadataCache();
	
	// Private member variables
	protected ImageConversionUtility imageConversionUtility;
		
	/**
	 * Return the required version of VistA Imaging necessary to use this service
	 * @return
	 */
	protected abstract String getRequiredVistaImagingVersion();
	
	protected abstract String getDataSourceVersion();
	
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public AbstractBaseVistaImageService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
		if(! (resolvedArtifactSource instanceof ResolvedSite) )
			throw new UnsupportedOperationException("The artifact source must be an instance of ResolvedSite and it is a '" + resolvedArtifactSource.getClass().getSimpleName() + "'.");
	}
	
	/**
	 * The artifact source must be checked in the constructor to assure that it is an instance
	 * of ResolvedSite.
	 * 
	 * @return
	 */
	protected ResolvedSite getResolvedSite()
	{
		return (ResolvedSite)getResolvedArtifactSource();
	}
	
	protected Site getSite()
	{
		return getResolvedSite().getSite();
	}


	protected abstract HashMap<String, String> getImageHISUpdates(VistaSession vistaSession, Image image)
	throws MethodException, ConnectionException, IOException; 
		
	private VistaSession getVistaSession() 
    throws IOException, ConnectionException, MethodException, SecurityCredentialsExpiredException
    {
	    return VistaSession.getOrCreate(getMetadataUrl(), getSite());
    }

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSource#isVersionCompatible()
	 */
	@Override
	public boolean isVersionCompatible() 
	throws SecurityCredentialsExpiredException
	{		
		VistaSession localVistaSession = null;
		logger.info("isVersionCompatible searching for version [" + getRequiredVistaImagingVersion() + "], TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		try
		{
			localVistaSession = getVistaSession();	
			return VistaImagingCommonUtilities.isVersionCompatible(getRequiredVistaImagingVersion(), localVistaSession);			
		}
		catch(SecurityCredentialsExpiredException sceX)
		{
			// caught here to be sure it gets thrown as SecurityCredentialsExpiredException, not ConnectionException
			throw sceX;
		}
		catch(MethodException mX)
		{
			logger.error("There was an error finding the installed Imaging version from VistA", mX);
			TransactionContextFactory.get().addDebugInformation("isVersionCompatible() failed, " + (mX == null ? "<null error>" : mX.getMessage()));
		}
		catch(ConnectionException cX)
		{
			logger.error("There was an error finding the installed Imaging version from VistA", cX);
			TransactionContextFactory.get().addDebugInformation("isVersionCompatible() failed, " + (cX == null ? "<null error>" : cX.getMessage()));
		}
		catch(IOException ioX)
		{
			logger.error("There was an error finding the installed Imaging version from VistA", ioX);
			TransactionContextFactory.get().addDebugInformation("isVersionCompatible() failed, " + (ioX == null ? "<null error>" : ioX.getMessage()));
		}
		finally
		{
			try{localVistaSession.close();}
			catch(Throwable t){}
		}		
		return false;	
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSource#getImageDevFields(gov.va.med.imaging.AbstractBaseImagingURN, java.lang.String)
	 */
	@Override
	public String getImageDevFields(AbstractImagingURN imagingUrn,
			String flags) 
	throws MethodException, ConnectionException, ImageNotFoundException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getImageDevFields", getDataSourceVersion());
		// CTB 29Nov2009
		//String imageId = Base32ConversionUtility.base32Decode(imagingUrn.getImagingIdentifier());
		String imageId = imagingUrn.getImagingIdentifier();
		logger.info("getImageDevFields(" + imagingUrn.toString() + ", Base32{" + imageId + "}), TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		return getImageDevFields(imageId, flags);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSource#getImageInformation(gov.va.med.imaging.AbstractBaseImagingURN)
	 */
	@Override
	public String getImageInformation(AbstractImagingURN imagingUrn, boolean includeDeletedImages)
	throws MethodException, ConnectionException, ImageNotFoundException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getImageInformation", getDataSourceVersion());
		// CTB 29Nov2009
		//String imageId = Base32ConversionUtility.base32Decode(imagingUrn.getImagingIdentifier());
		String imageId = imagingUrn.getImagingIdentifier();
		logger.info("getImageInformation(" + imagingUrn.toString() + ", Base32{" + imageId + "}), TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		return getImageInformation(imageId, includeDeletedImages);
	}	

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSource#getImageSystemGlobalNode(gov.va.med.imaging.AbstractBaseImagingURN)
	 */
	@Override
	public String getImageSystemGlobalNode(AbstractImagingURN imagingUrn)
	throws MethodException, ConnectionException, ImageNotFoundException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getImageSystemGlobalNode", getDataSourceVersion());
		// CTB 29Nov2009
		//String imageId = Base32ConversionUtility.base32Decode(imagingUrn.getImagingIdentifier());
		String imageId = imagingUrn.getImagingIdentifier();
		logger.info("getImageSystemGlobalNode(" + imagingUrn.toString() + ", Base32{" + imageId + "}), TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");		
		return getSysGlobalNodes(imageId);
	}	

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSource#getImage(gov.va.med.imaging.exchange.business.Image, gov.va.med.imaging.exchange.business.ImageFormatQualityList)
	 */
	@Override
	public ImageStreamResponse getImage(Image image,
			ImageFormatQualityList requestFormatQualityList)
	throws MethodException, ConnectionException 
	{
		VistaSession localVistaSession = null;
		VistaCommonUtilities.setDataSourceMethodAndVersion("getImage", getDataSourceVersion());
		logger.info("getImage(" + image.getIen() + ") from Image object TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		try
		{
			String serverShare = VistaImagingTranslator.extractServerShare(image);
			// if for some reason there is no server share found, we are going to look in VistA to get updated
			// image metadata - I'm really not sure why this is happening, but it seems to be happening...
			// really shouldn't but was occuring in unit tests.  If query by Image URN and get image from VistA,
			// the image share was found and all worked properly.
			if((serverShare == null) || (serverShare.length() <= 0))
			{
				logger.info("Got an empty server share from the image object, trying to get new metadata from image Urn");
				ImageURN imageUrn = image.getImageUrn();
				image = getImageObjectFromImageUrn(imageUrn);
				if(image == null)
				{
					String msg = "Got null image metadata back from image Urn, image not found";
					logger.error(msg);
					throw new ImageNotFoundException(msg);
				}
				logger.info("Using new image metadata to extract server share from image [" + imageUrn.toString() + "].");
				serverShare = VistaImagingTranslator.extractServerShare(image);
			}
			ImagingStorageCredentials imagingStorageCredentials = null;
			
			logger.info("Searching for Imaging Storage Credentials for server share [" + serverShare + "]");
			imagingStorageCredentials = VistaImagingStorageManager.getImagingStorageCredentialsFromCache(image, getSite());
			if(imagingStorageCredentials == null)
			{
				logger.info("Imaging Storage Credentials for site '" + getSite().getSiteNumber() + "' does not exist in the network location cache, getting from VistA");				
				// note that we delay getting the vista session until we are sure we need it
				localVistaSession = getVistaSession();			
				imagingStorageCredentials = VistaImagingStorageManager.getImagingStorageCredentialsFromVista(localVistaSession, serverShare, getSite());					
			}
			else
			{
				logger.info("Imaging Storage Credentials for [" + serverShare + "] found in the network location cache");
			}
				
			if(imagingStorageCredentials == null)
			{
				String msg = "No Imaging Storage Credentials found for image share [" + serverShare + "] for image [" + image.getFullFilename() + "]";
				logger.error(msg);
				throw new ImageNotFoundException(msg);
			}				
			
			return getImageInternal(localVistaSession, image, requestFormatQualityList, imagingStorageCredentials);			
		}
		catch(IOException ioX)
		{
			logger.error("Error getting image", ioX);
			throw new ConnectionException(ioX);
		} 
		finally
		{
			try
			{
				if(localVistaSession != null)
					localVistaSession.close();
			}
			catch(Throwable t){}
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSource#getImage(gov.va.med.imaging.ImageURN, gov.va.med.imaging.exchange.business.ImageFormatQualityList)
	 */
	@Override
	public ImageStreamResponse getImage(GlobalArtifactIdentifier gai,
		ImageFormatQualityList requestFormatQualityList)
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getImage", getDataSourceVersion());
		if(!(gai instanceof ImageURN))
		{
			throw new MethodException("GlobalArtifactIdentifier '" + gai.toString() + "' is not instanceof ImageURN, cannot retrieve image from VistA.");
		}
		ImageURN imageUrn = (ImageURN)gai;
		logger.info("getImage(" + imageUrn.toString() + ") from ImageURN TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		Image image = getImageObjectFromImageUrn(imageUrn);
		if(image == null)
		{
			//logger.error("Got null image metadata from VistA for image [" + imageUrn.toString() + "], returning null");
			//return null;
			
			String msg = "Got null image metadata from VistA for image [" + imageUrn.toString() + "], image not found";
			logger.error(msg);
			throw new ImageNotFoundException(msg);
		}
		
		return getImage(image, requestFormatQualityList);
	}	

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSource#getImageTXTFile(gov.va.med.imaging.exchange.business.Image)
	 */
	@Override
	public DataSourceInputStream getImageTXTFile(Image image)
			throws MethodException, ConnectionException,
			ImageNotFoundException, ImageNearLineException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getImageTXTFile", getDataSourceVersion());
		VistaSession vistaSession = null;
		try {
			vistaSession = getVistaSession();
			return getImageTXTFileInternal(vistaSession, image);
		}
		catch(IOException ioX)
		{
			logger.error("Error getting image TXT file", ioX);
			throw new ConnectionException(ioX);
		}
		finally
		{
			try{vistaSession.close();}
			catch(Throwable t){}
		}		
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSource#getImageTXTFile(gov.va.med.imaging.ImageURN)
	 */
	@Override
	public DataSourceInputStream getImageTXTFile(ImageURN imageUrn)
			throws MethodException, ConnectionException,
			ImageNotFoundException, ImageNearLineException 
	{
		logger.info("getImageTXTFile(" + imageUrn.toString() + ") from ImageURN TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaCommonUtilities.setDataSourceMethodAndVersion("getImageTXTFile", getDataSourceVersion());
		
		Image image = getImageObjectFromImageUrn(imageUrn);
		if(image == null)
		{
			String msg = "Got null image metadata from VistA for TXT File [" + imageUrn.toString() + "], TXT File not found";
			logger.error(msg);
			throw new ImageNotFoundException(msg);
		}
		return getImageTXTFile(image);
	}
	
	protected abstract VistaQuery getImageInformationQuery(String identifier, boolean includeDeletedImages);
	/*
	{
		VistaQuery msg = VistaImagingQueryFactory.createGetImageInformationQuery(identifier);
		return msg;		
	}*/

	private String getImageInformation(String identifier, boolean includeDeletedImages)
	throws ConnectionException, MethodException
	{
		VistaSession vistaSession = null;
		VistaQuery msg = getImageInformationQuery(identifier, includeDeletedImages);
		String rtn = null;
		try
		{
			vistaSession = getVistaSession();
			rtn = vistaSession.call(msg);
			logger.info("getImageInformation(" + identifier + "), Got [" + rtn.length() + "] bytes back, TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
			TransactionContextFactory.get().setDataSourceBytesReceived(new Long(rtn.length()));
			return rtn;
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.error("Error getting image information", ivcX);
			throw new ConnectionException(ivcX);
		}
		catch(VistaMethodException vmX)
		{
			logger.error("Error getting image information", vmX);
			throw new ConnectionException(vmX);
		}
		catch(IOException ioX)
		{
			logger.error("Error getting image information", ioX);
			throw new ConnectionException(ioX);
		}
		finally
		{
			try
			{
				if(vistaSession != null)
					vistaSession.close();
			}
			catch(Throwable t){}
		}
	}
	
	private String getImageDevFields(String identifier, String flags)
	throws ConnectionException, MethodException
	{		
		VistaSession vistaSession = null;
		VistaQuery msg = VistaImagingQueryFactory.createGetDevFieldValues(identifier, flags);
		String rtn = null;
		try
		{
			vistaSession = getVistaSession();
			rtn = vistaSession.call(msg);
			logger.info("getImageDevFields(" + identifier + "), Got [" + rtn.length() + "] bytes back, TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
			TransactionContextFactory.get().setDataSourceBytesReceived(new Long(rtn.length()));
			return rtn;
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.error("Error getting dev fields", ivcX);
			throw new ConnectionException(ivcX);
		}
		catch(VistaMethodException vmX)
		{
			logger.error("Error getting dev fields", vmX);
			throw new ConnectionException(vmX);
		}
		catch(IOException ioX)
		{
			logger.error("Error getting dev fields", ioX);
			throw new ConnectionException(ioX);
		}
		finally
		{
			try
			{
				if(vistaSession != null)
					vistaSession.close();
			}
			catch(Throwable t){}
		}
	}
	
	private String getSysGlobalNodes(String identifier)
	throws ConnectionException, MethodException
	{
		VistaSession vistaSession = null;
		VistaQuery msg = VistaImagingQueryFactory.createGetSysGlobalNodesQuery(identifier);
		String rtn = null;
		try
		{
			vistaSession = getVistaSession();
			rtn = vistaSession.call(msg);
			logger.info("getSysGlobalNodes(" + identifier + "), Got [" + rtn.length() + "] bytes back, TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
			TransactionContextFactory.get().setDataSourceBytesReceived(new Long(rtn.length()));
			return rtn;
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.error("Error getting system global nodes", ivcX);
			throw new ConnectionException(ivcX);
		}
		catch(VistaMethodException vmX)
		{
			logger.error("Error getting system global nodes", vmX);
			throw new ConnectionException(vmX);
		}
		catch(IOException ioX)
		{
			logger.error("Error getting system global nodes", ioX);
			throw new ConnectionException(ioX);
		}
		finally
		{
			try
			{
				if(vistaSession != null)
					vistaSession.close();
			}
			catch(Throwable t){}
		}
	}	
	
	/**
	 * This function determines if the full image file path is actually the diagnostic image.
	 * Such is the case of CT images where there is no BIG entry, only the full entry.
	 * @param image
	 * @return
	 */
	private boolean isFullDiagnostic(Image image)
	{
		if(image == null)
			return false;
		// if there is a value for the big filename path, then the full path is not diagnostic
		if((image.getBigFilename() != null) && (image.getBigFilename().length() > 0))
			return false;
		return true;
	}
	
	private Image getCachedImageMetadata(ImageURN imageUrn)
	{
		List<Image> images = metadataCache.getCacheImages(imageUrn);
		if(images != null)
		{
			for(Image image : images)
			{
				if(image.getIen().equals(imageUrn.getImageId()))
				{
					return image;
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param imageUrn
	 * @return
	 * @throws ConnectionException
	 * @throws MethodException
	 */
	private Image getImageObjectFromImageUrn(ImageURN imageUrn)
	throws ConnectionException, MethodException	
	{
		logger.info("findImageFromImageUrn - searching for image metadata for image [" + imageUrn.toString() + "].");
		
		Image cachedImage = getCachedImageMetadata(imageUrn);
		if(cachedImage != null)
		{
			logger.info("Found image '" + imageUrn.toString() + "' in short term metadata cache.");
			return cachedImage;
		}
		logger.info("Did not find image '" + imageUrn.toString() + "' in short term metadata cache, retrieving from VistA.");
		VistaSession localVistaSession = null;
		// need to query VistA using the decoded study Ien value
		// CTB 29Nov2009
		//String decodedStudyIen = Base32ConversionUtility.base32Decode(imageUrn.getStudyId());
		String decodedStudyIen = imageUrn.getStudyId();
		String siteId = imageUrn.getOriginatingSiteId();
		PatientIdentifier patientIdentifier = imageUrn.getThePatientIdentifier();
		
		VistaQuery msg = VistaImagingQueryFactory.createGetStudyImagesVistaQuery(decodedStudyIen);
		try 
		{
			localVistaSession = getVistaSession();
			String imagesString = localVistaSession.call(msg);
			// if the response starts with an error then the ien for this study might be the actual image, and not a group IEN
			// we need to query for the study groups to get the image data
			if(imagesString.startsWith("0^ERROR"))
			{
				logger.info("Received error response when trying to get group of images, might indicate image is in a single image study");
				Image image = getImageFromGroup(localVistaSession, imageUrn);			
				try{localVistaSession.close();}catch(Throwable t){}
				if(image != null)
				{
					return image;
				}
			}
			else
			{
				// the 
				List<Image> images = VistaImagingTranslator.VistaImageStringListToImageList(imagesString, siteId, 
						decodedStudyIen, patientIdentifier);
				metadataCache.cacheImages(imageUrn, images);
				for(int i = 0; i < images.size(); i++)
				{
					Image image = images.get(i);
					// compare to the base 32 encoded Image id since that is what will be put into the image object by the translator
					if(image.getIen().equals(imageUrn.getImageId()))
					{
						//image.setStudyIen(imageUrn.getStudyId()); // set using the base32 encoded value					
						// put the local VistA connection back into the cache here in case it is not used again,
						// if it is needed by getImage then it will be in the cache and available
						
						// JMW 10/2/2008 - need to set the site number so the network location cache can be used properly. 
						//image.setSiteNumber(getSite().getSiteNumber());
						try{localVistaSession.close();}catch(Throwable t){}
						return image;
					}
				}
				logger.info("Did not find image '" + imageUrn.getImageId() + "' in group of images, might indicate image is first image in a multi-image group, getting groups for patient");
				Image image = getImageFromGroup(localVistaSession, imageUrn);			
				try{localVistaSession.close();}catch(Throwable t){}
				if(image != null)
				{
					return image;
				}
			}
		}
		catch (URNFormatException x)
		{
			logger.error("Error getting Vista txt file", x);
			throw new ConnectionException(x);
		}
		catch(IOException ioX)
		{
			logger.error("Error getting Vista image", ioX);
			throw new ConnectionException(ioX);
		}
		catch(VistaMethodException vmX)
		{
			logger.error("Error getting Vista image", vmX);
			throw new MethodException(vmX);
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.error("Error getting Vista image", ivcX);
			throw new ConnectionException(ivcX);
		}
		logger.error("Unable to find image metadata for URN [" + imageUrn.toString() + "]");
		return null;
	}
	
	private Image getImageFromGroup(VistaSession vistaSession, ImageURN imageUrn)
	throws MethodException, IOException, ConnectionException
	{
		// CTB 29Nov2009
		//String decodedStudyIen = Base32ConversionUtility.base32Decode(imageUrn.getStudyId());
		//String decodedImageIen = Base32ConversionUtility.base32Decode(imageUrn.getImageId());
		String decodedStudyIen = imageUrn.getStudyId();
		String decodedImageIen = imageUrn.getImageId();
		PatientIdentifier patientIdentifier = imageUrn.getThePatientIdentifier();
		logger.info("getImageFromGroup(" + decodedStudyIen + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		
		
		String patientDfn = getPatientDfn(vistaSession, patientIdentifier);
		VistaQuery vm = VistaImagingQueryFactory.createGetGroupsVistaQuery(patientDfn, null);
		String rtn = null;
		try
		{
			rtn = vistaSession.call(vm);
			if(rtn.charAt(0) == '1') 
			{			
				List<Image> images = VistaImagingTranslator.createImagesForFirstImagesFromVistaGroupList(rtn, patientIdentifier, 
						getSite().getSiteNumber());
				for(int i = 0; i < images.size(); i++)
				{
					Image image = images.get(i);
					// CTB 29Nov2009
					//String newDecodedId = Base32ConversionUtility.base32Decode(image.getIen());
					String newDecodedId = image.getIen();
					if(newDecodedId.equals(decodedImageIen))
					{
						//image.setStudyIen(imageUrn.getStudyId());
						return image;
					}
				}
			}
			else if(rtn.startsWith("0^No images for filter")) 
			{
				logger.info("0 response from MAG4 PAT GET IMAGES rpc, no images found, [" + rtn + "]");
				throw new ImageNotFoundException("No images found that match [" + decodedStudyIen + "]");
			}
			else if(rtn.startsWith("0^No Such Patient:")) 
			{
				logger.info("0 response from MAG4 PAT GET IMAGES rpc, [" + rtn + "]");
				throw new ImageNotFoundException("No patient [ "+ patientIdentifier + "] found in database");
			}
			else 
			{
				logger.info("0 response from MAG4 PAT GET IMAGES rpc, [" + rtn + "]");
				throw new ImageNotFoundException(rtn);
			}
		}
		catch (Exception ex)
		{
			logger.error(ex);
			throw new MethodException(ex);
		}
		
		return null;
	}	
	
	private ImageStreamResponse getImageInternal(VistaSession vistaSession, Image image, 
			ImageFormatQualityList requestFormatQualityList, ImagingStorageCredentials networkLocation)
	throws MethodException, ConnectionException, ImageNearLineException, IOException
	{
		boolean gotSession = false;
		try
		{			
			DataSourceImageInputStream imageStream = null;
			ImageQuality imageQuality = requestFormatQualityList == null ? null : requestFormatQualityList.getFirstImageQuality();
			List<ImageConversionFilePath> filePaths = getImageFilePaths(vistaSession, image, imageQuality);
			// if any of the file paths start with a '.' we want to use those canned paths if possible
			for(ImageConversionFilePath filePath : filePaths)
			{
				if(filePath.getFilePath().startsWith("."))
				{
					// get a canned image that represents this file
					try
					{
						imageStream = getCannedImage(image, filePath.getFilePath());
						ImageStreamResponse response = new ImageStreamResponse(imageStream);
						response.setImageQuality(filePath.getImageQuality());
						return response;
					}
					catch(FileNotFoundException fnfX)
					{
						logger.error("Error trying to open canned image", fnfX);
						throw new ImageNotFoundException(fnfX);
					}
				}
			}			
			HashMap<String, String> hisUpdate = null;
			if(imageQuality != ImageQuality.THUMBNAIL)
			{
				logger.info("About to get HIS update for image [" + image.getIen() + "]");
				if(vistaSession == null) 
				{
					vistaSession = getVistaSession();
					gotSession = true;
				}
				hisUpdate = getImageHISUpdates(vistaSession, image);
			}			
			return imageConversionUtility.getImage(filePaths, requestFormatQualityList, 
				requestFormatQualityList, true, networkLocation, hisUpdate);				
		}
		/*
		catch(ImageNotFoundException infX)
		{
			logger.error("Image [" + image.getIen() + "] not found", infX);
			return null;
		}
		*/
		catch(URNFormatException iurnfX)
		{
			logger.error("Error reading image [" + image.getIen() + "]", iurnfX);
			throw new MethodException(iurnfX);
		}
		catch(ConnectionException cX)
		{
			logger.error("Error reading image [" + image.getIen() + "]", cX);
			throw cX;
		}
		catch(IOException ioX)
		{
			logger.error("Error reading image [" + image.getIen() + "]", ioX);
			throw ioX;
		}
		finally
		{
			if((gotSession) && (vistaSession != null))
			{
				try{vistaSession.close();}
				catch(Throwable t){}
			}
		}
	}
	
	/**
	 * An imageQuality of null indicates that the 
	 * @param localVistaSession
	 * @param image
	 * @param imageQuality
	 * @return
	 * @throws ImageNearLineException
	 * @throws URNFormatException
	 * @throws ConnectionException
	 * @throws MethodException
	 * @throws IOException
	 * @throws ImageNotFoundException
	 */
	private List<ImageConversionFilePath> getImageFilePaths(VistaSession localVistaSession, Image image, ImageQuality imageQuality)
	throws ImageNearLineException, URNFormatException, ConnectionException, 
	MethodException, IOException, ImageNotFoundException
	{
		List<ImageConversionFilePath> filePaths = new ArrayList<ImageConversionFilePath>();
		boolean gotVistaSession = false;
		// CTB 29Nov2009
		//String decodedImageIen = Base32ConversionUtility.base32Decode(image.getIen());
		String decodedImageIen = image.getIen();
		StorageProximity imageProximity = StorageProximity.ONLINE;
		logger.info("Getting image filename for image [" + image.getIen() + "] with quality [" + imageQuality + "]");
		try
		{
			if(imageQuality == ImageQuality.THUMBNAIL)
			{
				if(image.getAbsFilename().startsWith("-1"))
				{
					logger.error("Image [" + image.getIen() + "] has thumbnail file path [" + image.getAbsFilename() + "], no image to respond with");
					throw new ImageNotFoundException("Image [" + image.getIen() + "] has thumbnail file path [" + image.getAbsFilename() + "], no image to respond with");
				}
				
				imageProximity = StorageProximity.ONLINE;
				if(IMAGE_LOCATION_OFFLINE.equals(image.getAbsLocation()))
				{
					imageProximity = StorageProximity.OFFLINE;
					if(localVistaSession == null)
					{
						localVistaSession = getVistaSession();
						gotVistaSession = true;
					}
					notifyArchiveOperator(localVistaSession, image.getAbsFilename(), decodedImageIen);
					throw new ImageNearLineException(
						ImageURNFactory.create(
						TransactionContextFactory.get().getRealm(), image.getIen(), image.getStudyIen(), image.getPatientId(), null, ImageURN.class) 
					);
				}
				else if(IMAGE_LOCATION_WORM.equals(image.getAbsLocation()))
					imageProximity = StorageProximity.NEARLINE;
				
				ImageConversionFilePath absPath = new ImageConversionFilePath(image.getAbsFilename(), ImageQuality.THUMBNAIL, imageProximity);
				filePaths.add(absPath);
			}
			// ref request cannot use big file
			else if((imageQuality == ImageQuality.REFERENCE) &&
					(!getResolvedSite().isLocalSite()))
			{
				if(image.getFullFilename().startsWith("-1"))
				{
					logger.error("Image [" + image.getIen() + "] has full file path [" + image.getFullFilename() + "], no image to respond with");
					throw new ImageNotFoundException("Image [" + image.getIen() + "] has full file path [" + image.getFullFilename() + "], no image to respond with");
				}				
				logger.info("Returning full image [" + image.getFullFilename() + "] because not local site or ref quality requested");
				
				imageProximity = StorageProximity.ONLINE;
				if(IMAGE_LOCATION_OFFLINE.equals(image.getFullLocation()))
				{
					imageProximity = StorageProximity.OFFLINE;
					if(localVistaSession == null)
					{
						localVistaSession = getVistaSession();
						gotVistaSession = true;
					}
					notifyArchiveOperator(localVistaSession, image.getFullFilename(), decodedImageIen);
					throw new ImageNearLineException(
						ImageURNFactory.create(
						TransactionContextFactory.get().getRealm(), image.getIen(), image.getStudyIen(), 
						image.getPatientId(), null, ImageURN.class) 
					);
				}
				else if(IMAGE_LOCATION_WORM.equals(image.getFullLocation()))
					imageProximity = StorageProximity.NEARLINE;
				
				ImageQuality actualImageQuality = imageQuality;
				
				if((image.getBigFilename() == null) || (image.getBigFilename().length() <= 0))
				{
					// in this case, the "Full" location is actually the diagnostic image (CT, PDF, DOC, HTML, etc)
					actualImageQuality = ImageQuality.DIAGNOSTICUNCOMPRESSED;
				}
				ImageConversionFilePath imgPath = new ImageConversionFilePath(image.getFullFilename(), actualImageQuality, imageProximity);
				filePaths.add(imgPath);
			}
			else if(imageQuality == ImageQuality.REFERENCE) // ref can use big file
			{
				boolean foundAFile = false;
				imageProximity = StorageProximity.ONLINE;
				if(IMAGE_LOCATION_OFFLINE.equals(image.getFullLocation()))
				{
					imageProximity = StorageProximity.OFFLINE;
					if(localVistaSession == null)
					{
						localVistaSession = getVistaSession();
						gotVistaSession = true;
					}
					notifyArchiveOperator(localVistaSession, image.getFullFilename(), decodedImageIen);
					if ((image.getBigFilename()!=null) && (image.getBigFilename().length() > 0))
					{
						if(localVistaSession == null)
						{
							localVistaSession = getVistaSession();
							gotVistaSession = true;
						}
						notifyArchiveOperator(localVistaSession, image.getBigFilename(), decodedImageIen);
					}
					throw new ImageNearLineException(
						ImageURN.create(
						TransactionContextFactory.get().getRealm(), image.getIen(), image.getStudyIen(), image.getPatientId()) 
					);
				}
				else if(IMAGE_LOCATION_WORM.equals(image.getFullLocation()))
					imageProximity = StorageProximity.NEARLINE;
				if((image.getBigFilename() != null) && (image.getBigFilename().length() > 0))
				{
					if(image.getBigFilename().startsWith("-1"))
					{
						logger.info("Big filename [" + image.getBigFilename() + "] starts with '-1', not using file path");
					}
					else
					{
						foundAFile = true;
						ImageConversionFilePath bigPath = new ImageConversionFilePath(image.getBigFilename(), ImageQuality.DIAGNOSTICUNCOMPRESSED, imageProximity);
						filePaths.add(bigPath);
					}					
				}
				if(image.getFullFilename().startsWith("-1"))
				{
					logger.info("Full filename [" + image.getFullFilename() + "] starts with '-1', not using file path");
				}
				else
				{
					ImageQuality actualQuality = ImageQuality.REFERENCE;
					if(isFullDiagnostic(image))
					{
						actualQuality = ImageQuality.DIAGNOSTICUNCOMPRESSED;
					}
					foundAFile = true;
					ImageConversionFilePath fullPath = new ImageConversionFilePath(image.getFullFilename(), actualQuality, imageProximity);
					filePaths.add(fullPath);
				}
				
				if(!foundAFile)
				{
					logger.error("Image [" + image.getIen() + "] has full file path [" + image.getFullFilename() + "], no image to respond with");
					throw new ImageNotFoundException("Image [" + image.getIen() + "] has full file path [" + image.getFullFilename() + "], no image to respond with");
				}
			}
			else // diagnostic request 
			{
				imageProximity = StorageProximity.ONLINE;
				if(IMAGE_LOCATION_OFFLINE.equals(image.getFullLocation()))
				{
					imageProximity = StorageProximity.OFFLINE;
					if(localVistaSession == null)
					{
						localVistaSession = getVistaSession();
						gotVistaSession = true;
					}
					notifyArchiveOperator(localVistaSession, image.getFullFilename(), decodedImageIen);
					if ((image.getBigFilename()!=null) && (image.getBigFilename().length() > 0))
					{
						if(localVistaSession == null)
						{
							localVistaSession = getVistaSession();
							gotVistaSession = true;
						}
						notifyArchiveOperator(localVistaSession, image.getBigFilename(), decodedImageIen);
					}
					throw new ImageNearLineException(
						ImageURN.create(
						TransactionContextFactory.get().getRealm(), image.getIen(), image.getStudyIen(), image.getPatientId()) 
					);
				}
				else if(IMAGE_LOCATION_WORM.equals(image.getFullLocation()))
					imageProximity = StorageProximity.NEARLINE;
				
				if(isFullDiagnostic(image))
				{
					if(image.getFullFilename().startsWith("-1"))
					{
						logger.error("Image [" + image.getIen() + "] has full file path [" + image.getFullFilename() + "], no image to respond with");
						throw new ImageNotFoundException("Image [" + image.getIen() + "] has full file path [" + image.getFullFilename() + "], no image to respond with");
					}
					ImageConversionFilePath filePath = new ImageConversionFilePath(image.getFullFilename(), ImageQuality.DIAGNOSTICUNCOMPRESSED, imageProximity);
					filePaths.add(filePath);
				}
				else
				{
					ImageConversionFilePath filePath = new ImageConversionFilePath(image.getBigFilename(), ImageQuality.DIAGNOSTICUNCOMPRESSED, imageProximity);
					filePaths.add(filePath);
				}
			}
		}
		finally
		{
			if((gotVistaSession) && (localVistaSession != null))
			{
				try{localVistaSession.close();}
				catch(Throwable t){}
			}
		}
		return filePaths;
	}	
	
	/**
	 * Determine the canned image to use to stream back to the user for requesting the thumbnail
	 * @param image
	 * @param filename
	 * @return
	 * @throws FileNotFoundException
	 */
	private DataSourceImageInputStream getCannedImage(Image image, String filename)
	throws FileNotFoundException
	{
		String fname = getVistaImagingConfiguration().getImageAbstract(image.getImgType());// "";	
		if((fname == null) || ("".equals(fname)))
		{
			// try to get the path from the filename
			fname = getVistaImagingConfiguration().getImageAbstract(filename);
		}
		if((fname == null) || ("".equals(fname)))
		{
			return null;
		}
		String vixConfigDir = System.getenv("vixconfig");
		vixConfigDir += File.separatorChar + "images" + File.separatorChar;
		fname = vixConfigDir + fname;
		
		File cannedImage = new File(fname);
		if(cannedImage.exists())
		{
			ByteBufferBackedImageInputStream input = 
				new ByteBufferBackedImageInputStream(new FileInputStream(cannedImage), 
						(int)cannedImage.length());
			return input;
		}		
		return null;
	}
	
	/**
	 * Runs the RPC necessary to notify the archive operator that a request for an offline image has occured
	 * 
	 * @param filename The filename of the image requested
	 * @param imageIen The internal entry number of the image requested
	 * @throws MethodException
	 * @throws ConnectionException
	 * @throws IOException
	 */
	private void notifyArchiveOperator(VistaSession vistaSession, String filename, String imageIen)
	throws MethodException, ConnectionException, IOException
	{
		VistaQuery archiveNotifyQuery = VistaImagingQueryFactory.createNotifyArchiveOperatorQuery(filename, imageIen);		
		try 
		{
			vistaSession.call(archiveNotifyQuery);
		}
		catch(VistaMethodException vmX)
		{
			logger.error("Error notifying Archive Operator", vmX);
			throw new MethodException(vmX);
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.error("Error notifying Archive Operator", ivcX);
			throw new InvalidCredentialsException(ivcX);
		}
	}	
	
	/**
	 * This implementation is necessary if the data source has a connection to VistA but not all of the credentials
	 * @param vistaSession
	 * @param image
	 * @return
	 * @throws UnsupportedOperationException
	 * @throws MethodException
	 * @throws ConnectionException
	 * @throws ImageNotFoundException
	 * @throws ImageNearLineException
	 * @throws IOException
	 */
	private DataSourceInputStream getImageTXTFileInternal(VistaSession vistaSession, Image image)
	throws UnsupportedOperationException, MethodException,
	ConnectionException, ImageNotFoundException, ImageNearLineException, IOException
	{
		boolean gotVistaSession = false;
		try
		{
			ImagingStorageCredentials imagingStorageCredentials = null;
			imagingStorageCredentials = VistaImagingStorageManager.getImagingStorageCredentialsFromCache(image, getSite());
			String serverShare = VistaImagingTranslator.extractServerShare(image);
			if(imagingStorageCredentials == null)
			{				
				logger.info("Imaging Storage Credentials for site '" + getSite().getSiteNumber() + "' does not exist in the network location cache, getting from VistA");				
				// note that we delay getting the vista session until we are sure we need it
				if(vistaSession == null)
				{
					vistaSession = getVistaSession();
					gotVistaSession = true;
				}
				imagingStorageCredentials = VistaImagingStorageManager.getImagingStorageCredentialsFromVista(vistaSession, serverShare, getSite());					
			}
			else
			{
				logger.info("Found Imaging Storage Credentials for share [" + serverShare + "] in the network location cache");
			}
				
			if(imagingStorageCredentials == null)
				throw new ImageNotFoundException("No Imaging Storage Credentials found for image share [" + serverShare + "] for txt file [" + image.getFullFilename() + "]");
			logger.info("Found Imaging Storage Credentials for network location [" + serverShare + "]");
			return getImageTXTFileInternal(vistaSession, image, imagingStorageCredentials, image.getFullFilename());
		}
		finally
		{
			// if this function is the one that got the connection, return it to the cache here
			if((gotVistaSession) && (vistaSession != null))
			{
				try{vistaSession.close();}
				catch(Throwable t){}
			}
		}	
	}
	
	private DataSourceInputStream getImageTXTFileInternal(VistaSession vistaSession, Image image, 
			ImagingStorageCredentials imageNetworkLocation, String imageFilename)
	throws UnsupportedOperationException, MethodException,
	ConnectionException, IOException
	{
		boolean gotVistaSession = false;
		try
		{			
			logger.info("About to get HIS updates");
			if(vistaSession == null) 
			{
				vistaSession = getVistaSession();
				gotVistaSession = true;
			}
			
			HashMap<String, String> hisUpdate = getImageHISUpdates(vistaSession, image);
			logger.info("done getting HIS updates");
			
			DataSourceInputStream txtStream = 
				imageConversionUtility.GetTxtStream(imageFilename, imageNetworkLocation, hisUpdate);
			return txtStream;
			
			//return appendTxtStreamWithHisUpdate(txtStream, hisUpdate);
		}
		finally
		{
			// if this function is the one that got the connection, return it to the cache here
			if((gotVistaSession) && (vistaSession != null))
			{
				try{vistaSession.close();}
				catch(Throwable t){}
			}
		}
	}

}
