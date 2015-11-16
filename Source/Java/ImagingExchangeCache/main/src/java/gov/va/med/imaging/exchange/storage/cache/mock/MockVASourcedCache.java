package gov.va.med.imaging.exchange.storage.cache.mock;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.GUID;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.documents.Document;
import gov.va.med.imaging.exchange.business.vistarad.ExamSite;
import gov.va.med.imaging.exchange.business.vistarad.PatientEnterpriseExams;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.enums.ObjectOrigin;
import gov.va.med.imaging.exchange.enums.StudyDeletedImageState;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.exchange.storage.cache.ImmutableInstance;
import gov.va.med.imaging.exchange.storage.cache.VASourcedCache;
import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.Instance;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.exceptions.InvalidGroupNameException;

@SuppressWarnings({ "unused", "deprecation" })
public class MockVASourcedCache 
extends AbstractMockCacheDecorator
implements VASourcedCache
{

	@Override
	public ImmutableInstance createImage(
		GlobalArtifactIdentifier gaid, 
		String quality, 
		String mimeType) 
	throws CacheException
	{
		if( MockCacheConfigurator.getCreateImageCacheException() != null)
			throw MockCacheConfigurator.getCreateImageCacheException();

		Instance instance = new MockInstance();
		ImmutableInstance immutableInstance = new ImmutableInstance(instance);
		return immutableInstance;
	}

	@Override
	public ImmutableInstance getImage(
		GlobalArtifactIdentifier gaid, 
		String quality, 
		String mimeType) 
	throws CacheException
	{
		if( MockCacheConfigurator.getGetImageCacheException() != null)
			throw MockCacheConfigurator.getGetImageCacheException();
		
		Instance instance = new MockInstance();
		ImmutableInstance immutableInstance = new ImmutableInstance(instance);
		return immutableInstance;
	}

	@Override
	public void createPatientEnterpriseExams(PatientEnterpriseExams patientEnterpriseExams) 
	throws CacheException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public PatientEnterpriseExams getPatientEnterpriseExams(String patientIcn) 
	throws CacheException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.cache.VASourcedCache#createPatientPhotoId(java.lang.String, java.lang.String)
	 */
	@Override
	public ImmutableInstance createPatientPhotoId(String siteNumber, PatientIdentifier patientIdentifier) 
	throws CacheException 
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.cache.VASourcedCache#getPatientPhotoId(java.lang.String, java.lang.String)
	 */
	@Override
	public ImmutableInstance getPatientPhotoId(String siteNumber, PatientIdentifier patientIdentifier) 
	throws CacheException 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createExamSite(RoutingToken routingToken, String patientIcn, ExamSite examSite) 
	throws CacheException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public ExamSite getExamSite(RoutingToken routingToken, String patientIcn) 
	throws CacheException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImmutableInstance getROIRelease(PatientIdentifier patientIdentifier, GUID guid)
			throws CacheException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImmutableInstance createROIRelease(PatientIdentifier patientIdentifier, GUID guid)
			throws CacheException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImmutableInstance getAnnotatedImage(GlobalArtifactIdentifier gaid,
			ImageQuality imageQuality, 
			ImageFormat imageFormat) throws CacheException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImmutableInstance createAnnotatedImage(
			GlobalArtifactIdentifier gaid, ImageQuality imageQuality, 
			ImageFormat imageFormat)
			throws CacheException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cache getWrappedCache()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getImageRegionName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMetadataRegionName()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
