package gov.va.med.imaging.exchange.storage.cache;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.GUID;
import gov.va.med.imaging.exchange.business.vistarad.ExamSite;
import gov.va.med.imaging.exchange.business.vistarad.PatientEnterpriseExams;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * The interface definition for the caching managing VA
 * artifacts
 * 
 */
public interface VASourcedCache
extends RealizedCache
{
	/**
	 * @param imageURN
	 * @return
	 */
	public ImmutableInstance createImage(
		GlobalArtifactIdentifier gaid, 
		String quality, 
		String mimeType)
	throws CacheException;

	/**
	 * @param imageURN
	 * @return
	 */
	public ImmutableInstance getImage(
		GlobalArtifactIdentifier gaid, 
		String quality, 
		String mimeType)
	throws CacheException;
	

	
	/**
	 * @param patientICN
	 * @param patientEnterpriseExams
	 * @throws CacheException
	 */
	public void createPatientEnterpriseExams(PatientEnterpriseExams patientEnterpriseExams)
	throws CacheException;

	/**
	 * 
	 * @param patientICN
	 * @return
	 * @throws CacheException
	 */
	public PatientEnterpriseExams getPatientEnterpriseExams(String patientIcn)
	throws CacheException;
	
	/**
	 * 
	 * @param routingToken
	 * @param patientIcn
	 * @return
	 * @throws CacheException
	 */
	public ExamSite getExamSite(RoutingToken routingToken, String patientIcn)
	throws CacheException;
	
	public void createExamSite(RoutingToken routingToken, String patientIcn, ExamSite examSite)
	throws CacheException;
	
	/**
	 * 
	 * @param siteNumber
	 * @param patientIcn
	 * @return
	 * @throws CacheException
	 */
	public ImmutableInstance getPatientPhotoId(String siteNumber, PatientIdentifier patientIdentifier)
	throws CacheException;
	
	public ImmutableInstance createPatientPhotoId(String siteNumber, PatientIdentifier patientIdentifier)
	throws CacheException;
	
	public ImmutableInstance getROIRelease(PatientIdentifier patientIdentifier, GUID guid)
	throws CacheException;
	
	public ImmutableInstance createROIRelease(PatientIdentifier patientIdentifier, GUID guid)
	throws CacheException;
	
	public ImmutableInstance getAnnotatedImage(GlobalArtifactIdentifier gaid, ImageQuality imageQuality, 
		ImageFormat imageFormat)
	throws CacheException;
	
	public ImmutableInstance createAnnotatedImage(GlobalArtifactIdentifier gaid, ImageQuality imageQuality, 
			ImageFormat imageFormat)
		throws CacheException;
	
	public Cache getWrappedCache();	
	
	public String getImageRegionName();
	
	public String getMetadataRegionName();
	
}
