/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 16, 2009
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
package gov.va.med.imaging.federationdatasource;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.imaging.AbstractImagingURN;
import gov.va.med.WellKnownOID;
import gov.va.med.SERIALIZATION_FORMAT;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.conversion.ImageConversionFilePath;
import gov.va.med.imaging.conversion.ImageConversionUtility;
import gov.va.med.imaging.conversion.enums.ImageConversionSatisfaction;
import gov.va.med.imaging.core.interfaces.ImageStorageFacade;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNearLineException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.ImageDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.UnsupportedServiceMethodException;
import gov.va.med.imaging.exchange.business.*;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.storage.DataSourceInputStream;
import gov.va.med.imaging.federation.proxy.FederationProxyUtilities;
import gov.va.med.imaging.federation.proxy.IFederationProxy;
import gov.va.med.imaging.federation.storage.FederationStorageUtility;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractFederationImageDataSourceService
extends AbstractFederationDataSourceService
implements ImageDataSourceSpi 
{
	private ProxyServices federationProxyServices = null;
	private ImageStorageFacade storageFacade = null;
	private ImageConversionUtility imageConversionUtility = null;
	private final static String FEDERATION_PROXY_SERVICE_NAME = "Federation";
	
	public final static String SUPPORTED_PROTOCOL = "vftp";
	
	private final static Logger logger = Logger.getLogger(AbstractFederationImageDataSourceService.class);
	
	public abstract String getDataSourceVersion();
	
	protected abstract IFederationProxy getFederationProxy()
	throws ConnectionException;
	
	/**
	 * Determines if this data source can ever get a text file. If this returns false, then no matter
	 * what type of image is requested, don't bother getting a text file
	 * @return
	 */
	protected abstract boolean canGetTextFile();

	/**
	 * 
	 * @param resolvedArtifactSource
	 * @param protocol
	 * @throws UnsupportedOperationException if the ResolvedArtifactSource is not an instance of ResolvedSite
	 */
	public AbstractFederationImageDataSourceService(
		ResolvedArtifactSource resolvedArtifactSource,
		String protocol)
	throws UnsupportedOperationException
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
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSource#isVersionCompatible()
	 */
	@Override
	public boolean isVersionCompatible() 
	{
		if(getFederationProxyServices() == null)
			return false;		
		return true;
	}
	
	protected String getFederationProxyName()
	{
		return FEDERATION_PROXY_SERVICE_NAME;
	}
	
	/**
	 * Returns the proxy services available, if none are available then null is returned
	 */
	protected ProxyServices getFederationProxyServices()
	{
		if(federationProxyServices == null)
		{
			federationProxyServices = 
				FederationProxyUtilities.getFederationProxyServices(getSite(), 
						getFederationProxyName(), getDataSourceVersion());
		}
		return federationProxyServices;
	}
	
	private ImageConversionUtility getImageConversion()
	throws ConnectionException
	{
		if(imageConversionUtility == null)
		{
			imageConversionUtility = new ImageConversionUtility(getStorageFacade(), 
					ImageConversionSatisfaction.SATISFY_ANY_REQUEST, false);
		}
		return imageConversionUtility;
	}
	
	private ImageStorageFacade getStorageFacade()
	throws ConnectionException
	{
		if(storageFacade == null)
		{
			storageFacade = new FederationStorageUtility(getFederationProxy(), getSite());
		}
		return storageFacade;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSource#getImage(gov.va.med.imaging.exchange.business.Image, gov.va.med.imaging.exchange.business.ImageFormatQualityList)
	 */
	@Override
	public ImageStreamResponse getImage(Image image,
			ImageFormatQualityList requestFormatQualityList)
	throws MethodException, ConnectionException 
	{
		ImageURN imageUrn = image.getImageUrn();// ImageURN.create(image.getSiteNumber(), image.getIen(), image.getStudyIen(), image.getPatientICN());
		return getImage(imageUrn, requestFormatQualityList);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSource#getImage(gov.va.med.imaging.ImageURN, gov.va.med.imaging.exchange.business.ImageFormatQualityList)
	 */
	@Override
	public ImageStreamResponse getImage(GlobalArtifactIdentifier gai,
			ImageFormatQualityList requestFormatQualityList)
	throws MethodException, ConnectionException 
	{
		String imageId = gai.toString(SERIALIZATION_FORMAT.RAW);
		logger.info("getImage(" + imageId + ") from GlobalArtifactIdentifier TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");		
		
		ImageFormatQualityList queryFormatQualityList = new ImageFormatQualityList();
		queryFormatQualityList.addAll(requestFormatQualityList);
		FederationConfiguration configuration = FederationDataSourceProvider.getFederationConfiguration();			
		if(requestListNeedsCompression(queryFormatQualityList) && configuration.isAddCompressionForImageRequests() && isAllowAddFederationCompression())
		{
			logger.info("requested image does not include compressed formats but can include them, adding to request to improve performance.");
			// yes this will work for conversion because the conversion type is not in the list
			// so it will force the image to be converted (i think...)		
			
			ImageQuality quality = queryFormatQualityList.getFirstImageQuality();
			
			ImageFormatQuality dicomJ2kQuality = 
				new ImageFormatQuality(ImageFormat.DICOMJPEG2000, quality);
			ImageFormatQuality j2kQuality = 
				new ImageFormatQuality(ImageFormat.J2K, quality);
			
			// add the values to the front of the list
			queryFormatQualityList.add(0, j2kQuality);
			queryFormatQualityList.add(0, dicomJ2kQuality);			
		}

		// problem:  need to get the image and then determine if a conversion (frmo j2k to dicom)
		// is needed, but that needs to happen in image conversion
		// must send to storage facade with j2k, but can't send that to image conversion (crap)
		
		logger.info("Query Format List [" + queryFormatQualityList.getAcceptString(false, true) + "]");
		logger.info("Request Format List [" + requestFormatQualityList.getAcceptString(false, true) + "]");
		
		boolean downloadTxtFile = false; // only get the text file if it is a VA image
		
		// if this data source implementation supports getting a text file, then determine if the image
		// requested might have a text file (only VA images)
		if(canGetTextFile())
		{		
			// JMW 12/3/2010 P104 - since the VIX now gets DoD images from the CVIX using the Federation
			// data source, it can't rely on the actual data source (Exchange or XCA) to not get the text file
			// so Federation has to be smart and realize there is no text file to get from the DoD
			// so set this downloadTxtFile parameter to true only if the image retrieving is a VA image
			if((WellKnownOID.VA_RADIOLOGY_IMAGE.isApplicable(gai.getHomeCommunityId())) ||
					(WellKnownOID.VA_DOCUMENT.isApplicable(gai.getHomeCommunityId())))
			{
				TransactionContextFactory.get().addDebugInformation("Including request for text file in response (if applicable)");
				downloadTxtFile = true;
			}
		}
		
		List<ImageConversionFilePath> files = new ArrayList<ImageConversionFilePath>(1);
		files.add(new ImageConversionFilePath(imageId, null, null));

		ImageStreamResponse response = getImageConversion().getImage(files, 
			queryFormatQualityList, requestFormatQualityList, downloadTxtFile);
		return response;
	}	
	
	private boolean isAllowAddFederationCompression()
	{
		Boolean allowAddFederationCompression = TransactionContextFactory.get().isAllowAddFederationCompression();
		// if the value is missing then default to true since it was previously used
		if(allowAddFederationCompression == null)
			return true;
		return allowAddFederationCompression;
		
	}
	
	/**
	 * Determines if the requested list of formats already includes formats of compression. If not
	 * then J2K and DICOM J2K are added automatically.  This is rather kludgey and something better
	 * would be preferred (any ideas??). This function is necessary because requests from VistARad
	 * won't include compression but we want to use compression to move the image across the WAN
	 * 
	 * @param requestFormatQualityList
	 * @return
	 */
	private boolean requestListNeedsCompression(ImageFormatQualityList requestFormatQualityList)
	{
		if(requestFormatQualityList == null)
			return false;
		// JMW 2/12/2011, this is a special case - if the request format list only has 1 item and 
		// that item is ORIGINAL ImageFormat then do not add compression to the list
		// this will allow a requestor to always get the stored image without attempting any compression/conversion
		// This isn't necessary at this time, but it could be useful when compression/conversion is not desired
		if(requestFormatQualityList.size() == 1)
		{
			if(requestFormatQualityList.get(0).getImageFormat() == ImageFormat.ORIGINAL)
			{
				return false;
			}
		}
		for(ImageFormatQuality formatQuality : requestFormatQualityList)
		{
			// this is not optimal, adding formats when probably not needed (PDF, DOC, etc) but 
			// probably won't be a problem since can't convert from PDF into J2K anyway
			if((formatQuality.getImageFormat() == ImageFormat.J2K) || 
				(formatQuality.getImageFormat() == ImageFormat.DICOMJPEG2000))
			{
				return false;
			}
			// if the request is for thumbnail or uncompressed, don't add compression 
			else if((formatQuality.getImageQuality() == ImageQuality.THUMBNAIL)
				|| (formatQuality.getImageQuality() == ImageQuality.DIAGNOSTICUNCOMPRESSED))
			{
				return false;
			}
		}
		return true;		
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSource#getImageTXTFile(gov.va.med.imaging.exchange.business.Image)
	 */
	@Override
	public DataSourceInputStream getImageTXTFile(Image image)
	throws MethodException, ConnectionException, ImageNotFoundException, ImageNearLineException 
	{
		ImageURN imageUrn = image.getImageUrn();// ImageURN.create(image.getSiteNumber(), image.getIen(), image.getStudyIen(), image.getPatientICN());
		return getImageTXTFile(imageUrn);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSource#getImageTXTFile(gov.va.med.imaging.ImageURN)
	 */
	@Override
	public DataSourceInputStream getImageTXTFile(ImageURN imageURN)
	throws MethodException, ConnectionException, ImageNotFoundException, ImageNearLineException 
	{
		String imageId = imageURN.toString(SERIALIZATION_FORMAT.RAW);
		logger.info("getImageTXTFile(" + imageId + ") from Image URN TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		return getStorageFacade().openTXTStream(imageId, null, null);
	}
	
	protected Logger getLogger()
	{
		return logger;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSourceSpi#getImageDevFields(gov.va.med.imaging.AbstractImagingURN, java.lang.String)
	 */
	@Override
	public String getImageDevFields(AbstractImagingURN imagingUrn, String flags) throws MethodException,
		ConnectionException, ImageNotFoundException
	{
		throw new UnsupportedServiceMethodException(ImageDataSourceSpi.class, "getImageDevFields");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSourceSpi#getImageInformation(gov.va.med.imaging.AbstractImagingURN, boolean)
	 */
	@Override
	public String getImageInformation(AbstractImagingURN imagingUrn, boolean includeDeletedImages)
		throws MethodException, ConnectionException, ImageNotFoundException
	{
		throw new UnsupportedServiceMethodException(ImageDataSourceSpi.class, "getImageInformation");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ImageDataSourceSpi#getImageSystemGlobalNode(gov.va.med.imaging.AbstractImagingURN)
	 */
	@Override
	public String getImageSystemGlobalNode(AbstractImagingURN imagingUrn) throws MethodException, ConnectionException,
		ImageNotFoundException
	{
		throw new UnsupportedServiceMethodException(ImageDataSourceSpi.class, "getImageSystemGlobalNode");
	}

}
