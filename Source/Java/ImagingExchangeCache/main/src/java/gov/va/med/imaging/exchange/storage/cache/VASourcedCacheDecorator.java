package gov.va.med.imaging.exchange.storage.cache;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.GUID;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.vistarad.ExamSite;
import gov.va.med.imaging.exchange.business.vistarad.PatientEnterpriseExams;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.Instance;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import org.apache.log4j.Logger;

/**
 * 
 * 
 */
public class VASourcedCacheDecorator 
extends AbstractCacheDecorator
implements VASourcedCache
{
	private Logger logger = Logger.getLogger(this.getClass());
	private Cache wrappedCache;
	private String metadataRegionName = null;
	private String imageRegionName = null;

	public VASourcedCacheDecorator(
		Cache wrappedCache,
		String metadataRegionName, 
		String imageRegionName)
	{
		if(wrappedCache == null)
			throw new IllegalArgumentException(getClass().getSimpleName() + " was passed a null wrapped cache parameter.");
		if(metadataRegionName == null)
			throw new IllegalArgumentException(getClass().getSimpleName() + " was passed a null metadata region name parameter.");
		if(imageRegionName == null)
			throw new IllegalArgumentException(getClass().getSimpleName() + " was passed a null image region name parameter.");
		
		this.wrappedCache = wrappedCache;
		this.metadataRegionName = metadataRegionName;
		this.imageRegionName = imageRegionName;
		logger.info("VASourcedCacheDecorator <ctor> backed with cache '" + wrappedCache.getName() + "'.");
	}

	@Override
	public Cache getWrappedCache()
	{
		return wrappedCache;
	}
	
	@Override
	public String getImageRegionName()
	{
		return this.imageRegionName;
	}

	@Override
	public String getMetadataRegionName()
	{
		return this.metadataRegionName;
	}

	public ImmutableInstance createImage(GlobalArtifactIdentifier gaid, String quality, String mimeType) 
	throws CacheException
	{
		String[] groups = AbstractCacheDecorator.createInternalInstanceGroupKeys(gaid, true);
		String instanceKey = AbstractCacheDecorator.createInternalImageKey(gaid, quality, mimeType);
		
		return new ImmutableInstance( createImage(groups, instanceKey) );
	}

	public ImmutableInstance getImage(GlobalArtifactIdentifier gaid, String quality, String mimeType) 
	throws CacheException
	{
		String[] groups = AbstractCacheDecorator.createInternalInstanceGroupKeys(gaid, true);
		String instanceKey = AbstractCacheDecorator.createInternalImageKey(gaid, quality, mimeType);
		
		Instance instance = getImage(groups, instanceKey);
		if(instance == null)
			return null;
		return new ImmutableInstance(instance);
	}

	@Override
	public void createStudy(Study study) 
	throws CacheException
	{
		GlobalArtifactIdentifier gaid = study.getGlobalArtifactIdentifier();
		String[] groups = AbstractCacheDecorator.createInternalInstanceGroupKeys(gaid, true);
		String groupKey = AbstractCacheDecorator.createStudyKey(gaid);
		
		createStudy(groups, groupKey, study);
	}

	public Study getStudy(GlobalArtifactIdentifier gaid) 
	throws CacheException
	{
		String[] groups = AbstractCacheDecorator.createInternalInstanceGroupKeys(gaid, true);
		String groupKey = AbstractCacheDecorator.createStudyKey(gaid);
		
		return getStudy(groups, groupKey);
	}

	private String createPatientEnterpriseExamsKey(String patientICN)
	{
		return "patientEnterpriseExams.xml";
	}
	
	private String createExamSiteKey()
	{
		return "examSite.xml";
	}
	
	private String createPatientPhotoIdKey()
	{
		return "photo";
	}
	
	private String createROIReleaseKey(GUID guid)
	{
		return filenameOctetEscaping.escapeIllegalCharacters(guid.toLongString());
	}
	
	private String createAnnotatedImageKey(GlobalArtifactIdentifier gaid, String quality, String mimeType)
	{
		String instanceKey = AbstractCacheDecorator.createInternalImageKey(gaid, quality, mimeType);
		instanceKey = instanceKey + "_annotated";
		return instanceKey;
	}

	private String[] createPatientEnterpriseExamsGroupName(String patientICN)
	{
		return new String[] {patientICN};
	}
	
	private String [] createExamSiteGroupName(RoutingToken routingToken, String patientIcn)
	{
		return new String[] {routingToken.getHomeCommunityId(), routingToken.getRepositoryUniqueId(), patientIcn};
	}
	
	private String [] createPatientPhotoIdGroupName(String siteNumber, PatientIdentifier patientIdentifier)
	{
		return new String[] {
				siteNumber, 
				filenameOctetEscaping.escapeIllegalCharacters(patientIdentifier.toString())
				};		
	}

	private String [] createROIReleaseGroupName(PatientIdentifier patientIdentifier)
	{
		return new String [] 
				{
				filenameOctetEscaping.escapeIllegalCharacters("roi"),
				filenameOctetEscaping.escapeIllegalCharacters(patientIdentifier.toString()),
				filenameOctetEscaping.escapeIllegalCharacters("disclosure") 
				};
	}

	public void createPatientEnterpriseExams(PatientEnterpriseExams patientEnterpriseExams)
	throws CacheException
	{
		String[] groups = createPatientEnterpriseExamsGroupName(patientEnterpriseExams.getPatientIcn());
		String groupKey = createPatientEnterpriseExamsKey(patientEnterpriseExams.getPatientIcn());
		
		createPatientEnterpriseExams(groups, groupKey, patientEnterpriseExams);
	}

	public PatientEnterpriseExams getPatientEnterpriseExams(String patientIcn)
	throws CacheException
	{
		String[] groups = createPatientEnterpriseExamsGroupName(patientIcn);
		String groupKey = createPatientEnterpriseExamsKey(patientIcn);
		
		return getPatientEnterpriseExams(groups, groupKey);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.cache.VASourcedCache#createExamSite(gov.va.med.imaging.exchange.business.vistarad.ExamSite)
	 */
	@Override
	public void createExamSite(RoutingToken routingToken, String patientIcn, ExamSite examSite)
	throws CacheException 
	{
		String[] groups = createExamSiteGroupName(routingToken, patientIcn);
		String groupKey = createExamSiteKey();
		
		createExamSite(groups, groupKey, examSite);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.cache.VASourcedCache#getExamSite(java.lang.String, java.lang.String)
	 */
	@Override
	public ExamSite getExamSite(RoutingToken routingToken, String patientIcn)
	throws CacheException 
	{
		String[] groups = createExamSiteGroupName(routingToken, patientIcn);
		String groupKey = createExamSiteKey();
		
		return getExamSite(groups, groupKey);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.cache.VASourcedCache#createPatientPhotoId(java.lang.String, java.lang.String)
	 */
	@Override
	public ImmutableInstance createPatientPhotoId(String siteNumber,
			PatientIdentifier patientIdentifier) 
	throws CacheException 
	{
		String[] groups = createPatientPhotoIdGroupName(siteNumber, patientIdentifier);
		String instanceKey = createPatientPhotoIdKey();
		
		return new ImmutableInstance( createImage(groups, instanceKey) );
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.cache.VASourcedCache#getPatientPhotoId(java.lang.String, java.lang.String)
	 */
	@Override
	public ImmutableInstance getPatientPhotoId(String siteNumber,
			PatientIdentifier patientIdentifier) 
	throws CacheException 
	{
		String[] groups = createPatientPhotoIdGroupName(siteNumber, patientIdentifier);
		String instanceKey = createPatientPhotoIdKey();
		
		Instance instance = getImage(groups, instanceKey);
		if(instance == null)
			return null;
		return new ImmutableInstance(instance);
	}

	@Override
	public ImmutableInstance getROIRelease(PatientIdentifier patientIdentifier, GUID guid)
	throws CacheException
	{
		String [] groups = createROIReleaseGroupName(patientIdentifier);
		String instanceKey = createROIReleaseKey(guid);
		
		Instance instance = getImage(groups, instanceKey);
		if(instance == null)
			return null;
		return new ImmutableInstance(instance);
	}

	@Override
	public ImmutableInstance createROIRelease(PatientIdentifier patientIdentifier, GUID guid)
	throws CacheException
	{
		String [] groups = createROIReleaseGroupName(patientIdentifier);
		String instanceKey = createROIReleaseKey(guid);
		return new ImmutableInstance(createImage(groups, instanceKey));
	}

	@Override
	public ImmutableInstance getAnnotatedImage(GlobalArtifactIdentifier gaid,
			ImageQuality imageQuality, 
			ImageFormat imageFormat) 
	throws CacheException
	{
		String[] groups = AbstractCacheDecorator.createInternalInstanceGroupKeys(gaid, true);
		String instanceKey = createAnnotatedImageKey(gaid, imageQuality.name(), imageFormat.getMimeWithEnclosedMime());
		
		Instance instance = getImage(groups, instanceKey);
		if(instance == null)
			return null;
		return new ImmutableInstance(instance);
	}

	@Override
	public ImmutableInstance createAnnotatedImage(
			GlobalArtifactIdentifier gaid, ImageQuality imageQuality, 
			ImageFormat imageFormat)
	throws CacheException
	{
		String[] groups = AbstractCacheDecorator.createInternalInstanceGroupKeys(gaid, true);
		String instanceKey = createAnnotatedImageKey(gaid, imageQuality.name(), imageFormat.getMimeWithEnclosedMime());
		
		return new ImmutableInstance( createImage(groups, instanceKey) );
	}
}
