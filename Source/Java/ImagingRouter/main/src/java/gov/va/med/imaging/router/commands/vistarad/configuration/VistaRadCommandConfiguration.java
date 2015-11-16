/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 5, 2010
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
package gov.va.med.imaging.router.commands.vistarad.configuration;

import java.util.ArrayList;
import java.util.List;

import gov.va.med.imaging.exchange.business.ImageFormatQuality;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration;
import gov.va.med.imaging.facade.configuration.FacadeConfigurationFactory;
import gov.va.med.imaging.facade.configuration.exceptions.CannotLoadConfigurationException;

/**
 * @author vhaiswwerfej
 *
 */
public class VistaRadCommandConfiguration
extends AbstractBaseFacadeConfiguration
{
	
	private List<ImageFormatQualityPair> examImagePrefetchImageFormatQualityPairs = null;	
	private List<ImageFormatQuality> examImagePrefetchImageFormatQualities = null;
	private boolean cacheLocalExamMetadata = false;
	
	public static synchronized VistaRadCommandConfiguration getVistaRadCommandConfiguration()
	{
		try
		{
			return FacadeConfigurationFactory.getConfigurationFactory().getConfiguration(
					VistaRadCommandConfiguration.class);
			
		}
		catch(CannotLoadConfigurationException clcX)
		{
			return null;
		}
	}
	
	public VistaRadCommandConfiguration()
	{
		super();
	}

	/**
	 * @return the examImagePrefetchImageFormatQualities
	 */
	public List<ImageFormatQuality> getExamImagePrefetchImageFormatQualities()
	{
		return examImagePrefetchImageFormatQualities;
	}	

	/**
	 * @return the examImagePrefetchImageFormatQualityPairs
	 */
	public List<ImageFormatQualityPair> getExamImagePrefetchImageFormatQualityPairs()
	{
		return examImagePrefetchImageFormatQualityPairs;
	}

	/**
	 * @param examImagePrefetchImageFormatQualityPairs the examImagePrefetchImageFormatQualityPairs to set
	 */
	public void setExamImagePrefetchImageFormatQualityPairs(
		List<ImageFormatQualityPair> examImagePrefetchImageFormatQualityPairs)
	{
		this.examImagePrefetchImageFormatQualityPairs = examImagePrefetchImageFormatQualityPairs;
		
		examImagePrefetchImageFormatQualities = 
			new ArrayList<ImageFormatQuality>(examImagePrefetchImageFormatQualityPairs.size());
		for(ImageFormatQualityPair pair : examImagePrefetchImageFormatQualityPairs)
		{
			examImagePrefetchImageFormatQualities.add(
				new ImageFormatQuality(pair.getImageFormat(), pair.getImageQuality()));
		}
	}

	/**
	 * @return the cacheLocalExamMetadata
	 */
	public boolean isCacheLocalExamMetadata()
	{
		return cacheLocalExamMetadata;
	}

	/**
	 * @param cacheLocalExamMetadata the cacheLocalExamMetadata to set
	 */
	public void setCacheLocalExamMetadata(boolean cacheLocalExamMetadata)
	{
		this.cacheLocalExamMetadata = cacheLocalExamMetadata;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration#loadDefaultConfiguration()
	 */
	@Override
	public AbstractBaseFacadeConfiguration loadDefaultConfiguration()
	{
		examImagePrefetchImageFormatQualityPairs = 
			new ArrayList<ImageFormatQualityPair>();
		examImagePrefetchImageFormatQualityPairs.add(
			new ImageFormatQualityPair(ImageFormat.DICOM, ImageQuality.DIAGNOSTIC));
		examImagePrefetchImageFormatQualityPairs.add(
			new ImageFormatQualityPair(ImageFormat.TGA, ImageQuality.DIAGNOSTIC));
		examImagePrefetchImageFormatQualityPairs.add(
			new ImageFormatQualityPair(ImageFormat.ORIGINAL, ImageQuality.DIAGNOSTIC));

		cacheLocalExamMetadata = false;
		
		return this;
	}
	
	public static void main(String [] args)
	{
		VistaRadCommandConfiguration config = VistaRadCommandConfiguration.getVistaRadCommandConfiguration();
		if(args.length == 1)
		{
			if("-m".equalsIgnoreCase(args[0]))
			{
				config.setCacheLocalExamMetadata(true);
			}
			else
			{
				config.setCacheLocalExamMetadata(false);
			}
		}
		else
		{
			config.setCacheLocalExamMetadata(false);
		}
		config.storeConfiguration();
	}
}
