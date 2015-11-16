/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 15, 2010
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
package gov.va.med.imaging.router.commands;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.HomeCommunity;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.exceptions.OIDFormatException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.Series;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.util.ExchangeUtil;
import gov.va.med.imaging.router.commands.provider.ImagingCommandContext;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

import java.util.Collection;

import org.apache.log4j.Logger;

/**
 * @author vhaiswwerfej
 *
 */
public class CommonStudyCacheFunctions
{
	private final static Logger logger = Logger.getLogger(CommonStudyCacheFunctions.class);
	
	public static Logger getLogger()
	{
		return logger;
	}
	
	public static void updateImageInCache(ImagingCommandContext commandContext, Image image) 
	throws URNFormatException
	{
		if(image == null || commandContext == null)
			return;
		ImageURN imageUrn = image.getImageUrn();
		StudyURN studyUrn = imageUrn.getParentStudyURN();
		Study study = getStudyFromCache(commandContext, studyUrn);
		Image oldImage = null;
		if(study != null)
		{
			if(study.getStudyLoadLevel().isFullyLoaded())
			{
				for(Series series : study)
				{
					for(Image img : series)
					{
						if(img.equals(image))
						{
							oldImage = img;
							break;
						}
					}
					if(oldImage != null)
					{
						series.replaceImage(oldImage, image);
						cacheStudy(commandContext, study, studyUrn.getOriginatingSiteId());
						logger.info("Image '" + imageUrn.toString() + "' updated in cache");
						break;
					}
				}
				logger.debug("Did not find image '"+ imageUrn.toString() + "' in cache, cannot update image entry");
			}
			else
			{
				logger.debug("Study '" + studyUrn.toString() + "' is not fully loaded, will not update image entry");
			}
		}		
		else
		{
			logger.debug("Cannot find study '" + studyUrn.toString() + "' in cache.");
		}
	}
	
	public static Study getStudyFromCache(ImagingCommandContext commandContext, GlobalArtifactIdentifier gaid)
	{
		Study study = null;
		if(commandContext.isCachingEnabled()) {
			try 
			{
				study = 
					HomeCommunity.isWithinHomeCommunity(gaid) ? 					
					commandContext.getIntraEnterpriseCacheCache().getStudy(gaid) : 
					commandContext.getExtraEnterpriseCache().getStudy(gaid);
			}
			catch(CacheException cX) 
			{
				getLogger().warn("Unable to get study[" + gaid.toString() + "] from cache, exception message follows.", cX);
			}
			catch (OIDFormatException oidX)
			{
				getLogger().warn("Unable to get study[" + gaid.toString() + "] from cache, exception message follows.", oidX);
			}
		}
		return study;
	}

	/**
	 * Cache a list of Study instances into the appropriate cache.
	 * 
	 * @param resolvedSite
	 * @param studyList
	 * @return Returns true if all studies in the list were fully loaded and cached, if one or more study was not fully loaded, then false is returned
	 */
	public static boolean cacheStudyList(ImagingCommandContext commandContext, 
			String siteNumber, Collection<Study> studyList)
	{
		boolean allStudiesFullyLoaded = true; 
		if(commandContext.isCachingEnabled()) 
		{
			getLogger().info("Caching [" + studyList.size() + "] studies.");
			for(Study study : studyList)
			{		
				if(study.getStudyLoadLevel().isFullyLoaded())
				{
					cacheStudy(commandContext, study, siteNumber);
					/*
					try 
					{
						if(ExchangeUtil.isSiteDOD(siteNumber))
						{
							((CommandContextImpl)commandContext).getExtraEnterpriseCache().createStudy(study);
						}
						else
						{
							StudyURN studyUrn = study.getStudyUrn();//StudyURN.create(study.getSiteNumber(), study.getStudyIen(), study.getPatientIcn());
							((CommandContextImpl)commandContext).getIntraEnterpriseCacheCache().createStudy(study);
						}
					}
					catch(CacheException cX) 
					{
						getLogger().warn(cX);
					}
					/*
					catch(URNFormatException iurnfX)
					{
						getLogger().warn(iurnfX);
					}*/
				}
				else
				{
					getLogger().debug("Not caching study '" + study.getStudyIen() + "' because it is not fully loaded");
					allStudiesFullyLoaded = false;
				}
			}
		}
		return allStudiesFullyLoaded;
	}
	
	public static boolean cacheStudy(ImagingCommandContext commandContext, Study study, String siteNumber)
	{
		try 
		{
			if(ExchangeUtil.isSiteDOD(siteNumber))
			{
				commandContext.getExtraEnterpriseCache().createStudy(study);
			}
			else
			{
				commandContext.getIntraEnterpriseCacheCache().createStudy(study);
			}
			return true;
		}
		catch(CacheException cX) 
		{
			getLogger().warn(cX);
			return false;
		}
	}
}
