/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 18, 2009
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
package gov.va.med.imaging.exchange.enums;

/**
 * Indicates the properties of a Study which should be loaded from a data source.
 * 
 * 
 * @author vhaiswwerfej
 *
 */
public enum StudyLoadLevel 
{	
	STUDY_ONLY("Only load the study, no report or images", false, false),
	STUDY_AND_REPORT("Load the study and report, no images", true, false),	
	FULL("Fully load the study including the radiology report and the series and images", true, true),
	STUDY_AND_IMAGES("Load the study and the images, no report", false, true);
	
	final String description;
	final boolean includeReport;
	final boolean includeImages;
	
	StudyLoadLevel(String description, boolean includeReport, boolean includeImages)
	{
		this.description = description;
		this.includeImages = includeImages;
		this.includeReport = includeReport;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the includeReport
	 */
	public boolean isIncludeReport() {
		return includeReport;
	}

	/**
	 * @return the includeImages
	 */
	public boolean isIncludeImages() {
		return includeImages;
	}
	
	public boolean isFullyLoaded()
	{
		return includeImages && includeReport;
	}
	
	/**
	 * Updates the study load level to include the report (if not already included). Has no impact on images
	 */
	public static StudyLoadLevel promoteWithReport(StudyLoadLevel studyLoadLevel)
	{
		// if report already included, do nothing
		if(studyLoadLevel.includeReport)
			return studyLoadLevel;
		if(studyLoadLevel == StudyLoadLevel.STUDY_ONLY)
			return StudyLoadLevel.STUDY_AND_REPORT;
		if(studyLoadLevel == StudyLoadLevel.STUDY_AND_IMAGES)
			return StudyLoadLevel.FULL;
		
		// shouldn't be here but just in case
		return studyLoadLevel;
	}
	
	/**
	 * Updates the study load level to include the images (if not already included). Has no impact on the report
	 */
	public static StudyLoadLevel promoteWithImages(StudyLoadLevel studyLoadLevel)
	{
		// if report already included, do nothing
		if(studyLoadLevel.includeImages)
			return studyLoadLevel;
		if(studyLoadLevel == StudyLoadLevel.STUDY_ONLY)
			return StudyLoadLevel.STUDY_AND_IMAGES;
		if(studyLoadLevel == StudyLoadLevel.STUDY_AND_REPORT)
			return StudyLoadLevel.FULL;
		
		// shouldn't be here but just in case
		return studyLoadLevel;
	}
}
