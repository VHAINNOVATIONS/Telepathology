package gov.va.med.imaging.core.interfaces;

import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudyFilter;

import java.util.List;

public interface ImageMetadataAccessorFacade
{
	public abstract List<Study> getPatientStudyList (Site site, String patientId, StudyFilter filter);
	public abstract List<Image> getStudyImageList (StudyURN studyUrn);

//	public abstract boolean getStudyByGuid(GUID studyGuid, String subType, ImageQuality iQ,  ImageFormat conversionTargetFormat, OutputStream outStream)
//	throws IOException;
	
//	public abstract boolean getPatientByGuid(GUID patientGuid, String subType, ImageQuality iQ,  ImageFormat conversionTargetFormat, OutputStream outStream)
//	throws IOException;

//	public abstract boolean getSeriesByGuid(GUID seriesGuid, String subType, ImageQuality iQ,  ImageFormat conversionTargetFormat, OutputStream outStream)
//	throws IOException;

}
