/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 15, 2009
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

import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.conversion.ImageConversionFilePath;
import gov.va.med.imaging.conversion.ImageConversionUtility;
import gov.va.med.imaging.core.interfaces.exceptions.*;
import gov.va.med.imaging.datasource.VistaRadImageDataSourceSpi;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.vistarad.ExamImage;
import gov.va.med.imaging.exchange.business.vistarad.ExamImages;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.enums.StorageProximity;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedInputStream;
import gov.va.med.imaging.protocol.vista.VistaImagingTranslator;
import gov.va.med.imaging.protocol.vista.exceptions.InvalidVistaVistaRadVersionException;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.image.VistaRadSiteCredentials;
import gov.va.med.imaging.url.vista.storage.VistaImagingRadStorageManager;
import gov.va.med.imaging.url.vista.storage.VistaImagingVistaRadShortTermMetadataCache;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingVistaRadCommonUtilities;
import gov.va.med.imaging.vistaimagingdatasource.configuration.VistaImagingConfiguration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractBaseVistaRadImageDataSourceService 
extends AbstractVistaImagingVistaRadDataService
implements VistaRadImageDataSourceSpi 
{
	protected VistaImagingConfiguration vistaConfiguration = null;
	
	// Private member variables
	protected ImageConversionUtility imageConversionUtility;
	
	private final static VistaImagingVistaRadShortTermMetadataCache metadataCache = 
		new VistaImagingVistaRadShortTermMetadataCache();
	
	protected abstract String getDataSourceVersion();
	
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public AbstractBaseVistaRadImageDataSourceService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
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
	
	/**
	 * Return the required version of VistA Imaging necessary to use this service
	 * @return
	 */
	protected abstract String getRequiredVistaImagingVersion();
	
	/**
	 * Get HIS Updates from the data source for the specific image
	 * @param vistaSession
	 * @param image
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 * @throws IOException
	 */
	protected abstract HashMap<String, String> getImageHISUpdates(VistaSession vistaSession, ExamImage image)
	throws MethodException, ConnectionException, IOException; 	

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadImageDataSource#getImage(gov.va.med.imaging.exchange.business.vistarad.ExamImage, gov.va.med.imaging.exchange.business.ImageFormatQualityList)
	 */
	@Override
	public ImageStreamResponse getImage(ExamImage image,
		ImageFormatQualityList requestFormatQuality)
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getImage", getDataSourceVersion());
		getLogger().info(getClassSimpleName() + ".getImage(" + image.getImageId() + ") from ExamImage object TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		return getImageInternal(image, requestFormatQuality);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadImageDataSource#getImage(gov.va.med.imaging.ImageURN, gov.va.med.imaging.exchange.business.ImageFormatQualityList)
	 */
	@Override
	public ImageStreamResponse getImage(ImageURN imageUrn,
		ImageFormatQualityList requestFormatQuality)
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getImage", getDataSourceVersion());
		getLogger().info(getClassSimpleName() + ".getImage(" + imageUrn.toString() + ") from ImaegUrn object TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		ExamImage image = getExamImageFromImageUrn(imageUrn);
		return getImageInternal(image, requestFormatQuality);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadImageDataSource#getImageTXTFile(gov.va.med.imaging.exchange.business.vistarad.ExamImage, gov.va.med.imaging.exchange.business.ImageFormatQualityList)
	 */
	@Override
	public ByteBufferBackedInputStream getImageTXTFile(ExamImage image)
	throws MethodException, ConnectionException,
		ImageNotFoundException, ImageNearLineException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getImageTXTFile", getDataSourceVersion());
		getLogger().info(getClassSimpleName() + ".getImageTXTFile(" + image.getImageId() + ") from ExamImage object TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		return getImageTXTFileInternal(image);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadImageDataSource#getImageTXTFile(gov.va.med.imaging.ImageURN, gov.va.med.imaging.exchange.business.ImageFormatQualityList)
	 */
	@Override
	public ByteBufferBackedInputStream getImageTXTFile(ImageURN imageUrn)
	throws MethodException, ConnectionException,
		ImageNotFoundException, ImageNearLineException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getImageTXTFile", getDataSourceVersion());
		getLogger().info(getClassSimpleName() + ".getImageTXTFile(" + imageUrn.toString() + ") from ImageUrn object TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		ExamImage image = getExamImageFromImageUrn(imageUrn);
		return getImageTXTFileInternal(image);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadImageDataSource#isVersionCompatible()
	 */
	@Override
	public boolean isVersionCompatible() 
	throws SecurityCredentialsExpiredException
	{
		VistaSession localVistaSession = null;
		getLogger().info("isVersionCompatible searching for version [" + getRequiredVistaImagingVersion() + "], TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		try
		{
			localVistaSession = getVistaSession();
			getLogger().info("Successfully got VistaSession (" + localVistaSession.getSessionIndex() + ") for version [" + getRequiredVistaImagingVersion() + "].");
			return true;
		}
		catch(SecurityCredentialsExpiredException sceX)
		{
			// caught here to be sure it gets thrown as SecurityCredentialsExpiredException, not ConnectionException
			throw sceX;
		}
		catch(InvalidVistaVistaRadVersionException vvrvX)
		{
			// error already displayed
		}
		catch(MethodException mX)
		{
			getLogger().error("There was an error finding the installed Imaging version from VistA", mX);
			TransactionContextFactory.get().addDebugInformation("isVersionCompatible() failed, " + (mX == null ? "<null error>" : mX.getMessage()));
		}
		catch(ConnectionException cX)
		{
			getLogger().error("There was an error finding the installed Imaging version from VistA", cX);
			TransactionContextFactory.get().addDebugInformation("isVersionCompatible() failed, " + (cX == null ? "<null error>" : cX.getMessage()));
		}
		catch(IOException ioX)
		{
			getLogger().error("There was an error finding the installed Imaging version from VistA", ioX);
			TransactionContextFactory.get().addDebugInformation("isVersionCompatible() failed, " + (ioX == null ? "<null error>" : ioX.getMessage()));
		}
		finally
		{
			try{localVistaSession.close();}
			catch(Throwable t){}
		}		
		return false;	
	}
	
	protected VistaSession getVistaSession() 
    throws IOException, ConnectionException, MethodException, InvalidVistaVistaRadVersionException, SecurityCredentialsExpiredException
    {	
		return VistaImagingVistaRadCommonUtilities.getVistaSession(getMetadataUrl(), getSite(), getRequiredVistaImagingVersion());
    }
	
	private ImageStreamResponse getImageInternal(ExamImage examImage, ImageFormatQualityList requestFormatQuality)
	throws MethodException, ConnectionException, ImageNotFoundException
	{
		String serverShare = VistaImagingTranslator.extractServerShare(examImage.getDiagnosticFilePath());
		if(StringUtils.isEmpty(serverShare))
		{
			String msg = "ExamImage '"+ examImage.getImageId() + "' does not have file path specified - cannot access image without path.";
			getLogger().error(msg);
			throw new ImageNotFoundException(msg);
		}
		VistaRadSiteCredentials siteCredentials =
			VistaImagingRadStorageManager.getSiteCredentialsFromCache(examImage.getSiteNumber());
		// if the site credentials are null, do we have a good way to retrieve them? they come from the MAGJ USER2 
		// rpc call, can this call be made twice on a single connection? The only way the credentials will not
		// be in the cache is if they have been purged which means they were not accessed in a long period of time
		// for a session that has lasted longer than the purge time.  This is highly unlikely for a session to
		// last longer than the purge time since the purge time is refreshed when the value is requested.
		if(siteCredentials == null)
		{
			String msg = "Could not find site credentials for site '" + examImage.getSiteNumber() + "', this really should never happen!";
			getLogger().error(msg);
			
			throw new ImageNotFoundException(msg);
		}
		List<ImageConversionFilePath> filePaths = new ArrayList<ImageConversionFilePath>();
		filePaths.add(new ImageConversionFilePath(examImage.getDiagnosticFilePath(), ImageQuality.DIAGNOSTICUNCOMPRESSED, StorageProximity.ONLINE));
		HashMap<String, String> hisUpdate = null;
		getLogger().info("About to get HIS update for image [" + examImage.getImageId() + "]");
		VistaSession vistaSession = null;
		try
		{
			vistaSession = getVistaSession();
			hisUpdate = getImageHISUpdates(vistaSession, examImage);
		}
		catch(IOException ioX)
		{
			getLogger().error("Error getting image", ioX);
			throw new ConnectionException(ioX);
		}
		catch(InvalidVistaVistaRadVersionException ivvrvX)
		{
			throw new ConnectionException(ivvrvX);
		}
		finally
		{
			// put it in the session cache, if we still need a session it will come from the cache
			try{vistaSession.close();}catch(Throwable t){}
		}
		return imageConversionUtility.getImage(filePaths, requestFormatQuality, 
				requestFormatQuality, true, siteCredentials, hisUpdate);	
	}
	
	private ExamImage getCacheExamImageMetadata(ImageURN imageUrn)
	{
		ExamImages examImages = metadataCache.getCacheImages(imageUrn);
		if(examImages != null)
		{
			return examImages.get(imageUrn);
		}
		return null;
	}
	
	private ExamImage getExamImageFromImageUrn(ImageURN imageUrn)
	throws MethodException, ConnectionException 
	{
		getLogger().info("getExamImageFromImageUrn - searching for exam image metadata for image [" + imageUrn.toString() + "].");
		
		ExamImage cachedExamImage = getCacheExamImageMetadata(imageUrn);
		if(cachedExamImage != null)
		{
			getLogger().info("Found exam image '" + imageUrn.toString() + "' in short term metadata cache.");
			return cachedExamImage;
		}
		getLogger().info("Did not find image '" + imageUrn.toString() + "' in short term metadata cache, retrieving from VistA.");
		
		VistaSession localVistaSession = null; 
		try
		{
			localVistaSession = getVistaSession();
			
			// JMW 12/27/2012 imageUrn.getPatientId() is assuming patient ID is an ICN and not DFN - this could be an issue later...
			ExamImages images = 
				getExamImagesFromExamId(localVistaSession, imageUrn.getStudyId(), 
						imageUrn.getPatientId(), getSite(), true);
			getLogger().info("found exam with '" + images.size() + "' images from which should contain exam image.");
			metadataCache.cacheImages(imageUrn, images);
			ExamImage examImage = images.get(imageUrn);
			if(examImage == null)
			{
				String msg = "Did not find exam image [" + imageUrn.toString() + "]";
				getLogger().info(msg);
				throw new ImageNotFoundException(msg);
			}
			getLogger().info("Found exam image [" + examImage.getImageUrn().toString() + "].");
			return examImage;			
		}
		catch(InvalidVistaVistaRadVersionException ivvrvX)
		{
			throw new ConnectionException(ivvrvX);
		}
		catch(IOException ioX)
		{
			getLogger().error("Error getting Vista exam image", ioX);
			throw new ConnectionException(ioX);
		}
		/*
		catch (URNFormatException urnfX)
		{
			logger.error("Error getting Vista exam image", urnfX);
			throw new ConnectionException(urnfX);
		}*/
		finally
		{
			// put it in the session cache, if we still need a session it will come from the cache
			try{localVistaSession.close();}catch(Throwable t){}
		}
	}	
	
	private ByteBufferBackedInputStream getImageTXTFileInternal(ExamImage examImage)
	throws ConnectionException, MethodException, ImageNotFoundException
	{
		String serverShare = VistaImagingTranslator.extractServerShare(examImage.getDiagnosticFilePath());
		if(StringUtils.isEmpty(serverShare))
		{
			String msg = "ExamImage '"+ examImage.getImageId() + "' does not have file path specified - cannot access image without path.";
			getLogger().error(msg);
			throw new ImageNotFoundException(msg);
		}
		VistaRadSiteCredentials siteCredentials =
			VistaImagingRadStorageManager.getSiteCredentialsFromCache(examImage.getSiteNumber());
		// if the site credentials are null, do we have a good way to retrieve them? they come from the MAGJ USER2 
		// rpc call, can this call be made twice on a single connection? The only way the credentials will not
		// be in the cache is if they have been purged which means they were not accessed in a long period of time
		// for a session that has lasted longer than the purge time.  This is highly unlikely for a session to
		// last longer than the purge time since the purge time is refreshed when the value is requested.
		if(siteCredentials == null)
		{
			String msg = "Could not find site credentials for site '" + examImage.getSiteNumber() + "', this really should never happen!";
			getLogger().error(msg);
			
			throw new ImageNotFoundException(msg);
		}
		HashMap<String, String> hisUpdate = null;
		getLogger().info("About to get HIS updates");
		VistaSession vistaSession = null;
		try
		{
			vistaSession = getVistaSession();
			hisUpdate = getImageHISUpdates(vistaSession, examImage);
		}
		catch(IOException ioX)
		{
			getLogger().error("Error getting image", ioX);
			throw new ConnectionException(ioX);
		}
		catch(InvalidVistaVistaRadVersionException ivvrvX)
		{
			throw new ConnectionException(ivvrvX);
		}
		finally
		{
			// put it in the session cache, if we still need a session it will come from the cache
			try{vistaSession.close();}catch(Throwable t){}
		}
		getLogger().info("done getting HIS updates");
		
		ByteBufferBackedInputStream txtStream = 
			imageConversionUtility.GetTxtStream(examImage.getDiagnosticFilePath(), 
					siteCredentials, hisUpdate);
		return txtStream;
	}
	
	/**
	 * Return the simple name of the concrete class (not the abstract class) 
	 * @return
	 */
	private String getClassSimpleName()
	{
		return this.getClass().getSimpleName();
	}

	@Override
	protected VistaQuery getExamImagesQuery(String examId,
			boolean useTgaImages, boolean forceImagesFromJb)
	{
		return VistaImagingVistaRadQueryFactory.createMagJGetExamImages(examId, useTgaImages);
	}
}
