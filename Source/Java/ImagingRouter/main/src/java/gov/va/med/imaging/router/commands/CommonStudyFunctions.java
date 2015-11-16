/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Nov 17, 2010
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

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.CommandContext;
import gov.va.med.imaging.exchange.business.ArtifactResultError;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.Series;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudySetResult;
import gov.va.med.imaging.exchange.enums.ArtifactResultStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * @author vhaiswwerfej
 *
 */
public class CommonStudyFunctions
{
	private final static Logger logger = Logger.getLogger(CommonStudyFunctions.class);
	
	protected static Logger getLogger()
	{
		return logger;
	}
	
	public static StudySetResult updateConsolidatedSitesInStudySetResult(StudySetResult studySetResult, 
			CommandContext commandContext)
	{
		//return studySetResult;
		
		List<ArtifactResultError> errors = studySetResult.getArtifactResultErrors();
		ArtifactResultStatus status = studySetResult.getArtifactResultStatus();
		SortedSet<Study> updatedStudies = null;
		if(studySetResult != null)
		{
			SortedSet<Study> studies = studySetResult.getArtifacts();
			if(studies != null)
			{
				updatedStudies = new TreeSet<Study>();
				for(Study study : studies)
				{
					Study updatedStudy = null;
					if(study.containsConsolidatedSiteNumber())						
					{						
						Site site = getConsolidatedSite(study.getConsolidatedSiteNumber(), commandContext);
						if(site != null)
						{
							updatedStudy = study.cloneWithConsolidatedSiteNumber(site);
							Image firstImage = updatedStudy.getFirstImage();
							if(firstImage.containsConsolidatedSiteNumber())
							{
								Site imageSite = getConsolidatedSite(firstImage.getConsolidatedSiteNumber(), commandContext);
								if(imageSite != null)
								{
									Image newFirstImage = firstImage.cloneWithConsolidatedSiteNumber();
									updatedStudy.setFirstImage(newFirstImage);
									// image ien does not change
								}
							}
						}
						else
						{
							updatedStudy = study;
						}
					}
					else
					{
						updatedStudy = study;
					}
					updatedStudies.add(updatedStudy);
					// loop through all the series in the "new" study
					for(Series series : updatedStudy)
					{
						List<SeriesImageReplacementPair> seriesReplacementPairs = 
							new ArrayList<SeriesImageReplacementPair>();
						
						for(Image image : series)
						{
							if(image.containsConsolidatedSiteNumber())
							{
								Site site = getConsolidatedSite(image.getConsolidatedSiteNumber(), commandContext);
								if(site != null)
								{
									Image newImage = image.cloneWithConsolidatedSiteNumber();
									
									//series.replaceImage(image, newImage);
									seriesReplacementPairs.add(new SeriesImageReplacementPair(image, newImage));
								}
							}
						}
						for(SeriesImageReplacementPair sirp : seriesReplacementPairs)
						{
							series.replaceImage(sirp.getImage(), sirp.getReplacementImage());
						}
					}
				}
			}
			return StudySetResult.create(updatedStudies, status, errors);
		}
		else
		{
			return null;
		}
	}
	
	private static Site getConsolidatedSite(String consolidatedSiteNumber, CommandContext commandContext)
	{
		try
		{
			return commandContext.getSiteResolver().getSite(consolidatedSiteNumber);					
		}
		catch(ConnectionException cX)
		{
			getLogger().warn("ConnectionException finding resolved site for consolidated site '" + consolidatedSiteNumber + ", " + cX.getMessage(), cX);
		}
		catch(MethodException mX)
		{
			getLogger().warn("MethodException finding resolved site for consolidated site '" + consolidatedSiteNumber + ", " + mX.getMessage(), mX);			
		}
		return null;
	}
	
	static class SeriesImageReplacementPair
	{
		final Image image;
		final Image replacementImage;
		
		public SeriesImageReplacementPair(Image image, Image replacementImage)
		{
			super();
			this.image = image;
			this.replacementImage = replacementImage;
		}
		
		public Image getImage()
		{
			return image;
		}
		
		public Image getReplacementImage()
		{
			return replacementImage;
		}
	}
}
