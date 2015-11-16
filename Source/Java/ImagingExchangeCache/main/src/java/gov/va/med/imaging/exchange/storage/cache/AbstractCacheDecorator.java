package gov.va.med.imaging.exchange.storage.cache;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.OctetSequenceEscaping;
import gov.va.med.PatientArtifactIdentifier;
import gov.va.med.imaging.AbstractImagingURN;
import gov.va.med.imaging.BhieImageURN;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.exchange.business.documents.Document;
import gov.va.med.imaging.exchange.business.vistarad.ExamSite;
import gov.va.med.imaging.exchange.business.vistarad.PatientEnterpriseExams;
import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.Instance;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import org.apache.log4j.Logger;

/**
 * This class operates in the semantics of the cache (groups and instance keys) and business
 * objects (Study).  There must be no VA or DOD interface specific objects.
 * 
 * Each concrete Cache decorator (derived class) provides two regions, one for metadata
 * and one for instances.  In addition the concrete class must provide access to a wrapped
 * Cache instance. 
 * 
 * @author VHAISWBECKEC
 *
 */
public abstract class AbstractCacheDecorator
implements RealizedCache
{
	protected static OctetSequenceEscaping filepathEscapingEngine = 
		OctetSequenceEscaping.createFilepathLegalEscapeEngine(); 
	
	/**
	 * A simple hash function whose function is just to produce
	 * a String that can be included in a file path and does not
	 * include any special characters.
	 * 
	 * @param value
	 * @return
	 */
	public static OctetSequenceEscaping filenameOctetEscaping = 
		OctetSequenceEscaping.createFilenameLegalEscapeEngine();
	
	/**
	 * If the GAID is a PAID (a subclass of GAID) then the patient identifier should be accessible,
	 * otherwise use "UnknownPatient" as the patient identifier.
	 * 
	 * If the documentIdIsGroup flag is set then the document ID will be included in the groups,
	 * otherwise it will not be.  Studies, document metadata and a other metadata include the
	 * document ID as a group and the instance key is usually a constant identifying the type of data.
	 * 
	 * @param homeCommunityId
	 * @param repositoryId
	 * @param documentId
	 * @return
	 */
	protected static String[] createExternalInstanceGroupKeys(GlobalArtifactIdentifier gaid, boolean documentIdIsGroup )
	{
		if(gaid instanceof ImageURN)
			return documentIdIsGroup ?
				new String[]
		   		{ 
					filenameOctetEscaping.escapeIllegalCharacters(gaid.getHomeCommunityId()), 
					filenameOctetEscaping.escapeIllegalCharacters(gaid.getRepositoryUniqueId()), 
		   			filenameOctetEscaping.escapeIllegalCharacters( ((ImageURN)gaid).getPatientIdentifier() ),
		   			filenameOctetEscaping.escapeIllegalCharacters( ((ImageURN)gaid).getStudyId() ),
		   			filenameOctetEscaping.escapeIllegalCharacters( ((ImageURN)gaid).getImageId() ),
		   			filenameOctetEscaping.escapeIllegalCharacters(gaid.getDocumentUniqueId())
		   		} :
				new String[]
				{ 
					filenameOctetEscaping.escapeIllegalCharacters(gaid.getHomeCommunityId()), 
					filenameOctetEscaping.escapeIllegalCharacters(gaid.getRepositoryUniqueId()), 
		   			filenameOctetEscaping.escapeIllegalCharacters( ((ImageURN)gaid).getPatientIdentifier() ),
		   			filenameOctetEscaping.escapeIllegalCharacters( ((ImageURN)gaid).getStudyId() ),
		   			filenameOctetEscaping.escapeIllegalCharacters( ((ImageURN)gaid).getImageId() )
				};
		else
			return documentIdIsGroup ?
				new String[]
		   		{ 
					filenameOctetEscaping.escapeIllegalCharacters(gaid.getHomeCommunityId()), 
					filenameOctetEscaping.escapeIllegalCharacters(gaid.getRepositoryUniqueId()), 
		   			gaid instanceof PatientArtifactIdentifier ? 
		   				filenameOctetEscaping.escapeIllegalCharacters( ((PatientArtifactIdentifier)gaid).getPatientIdentifier() ) :
		   				"UnknownPatientIdentifier", 
		   			filenameOctetEscaping.escapeIllegalCharacters(gaid.getDocumentUniqueId())
		   		} :
				new String[]
				{ 
		   			filenameOctetEscaping.escapeIllegalCharacters(gaid.getHomeCommunityId()), 
		   			filenameOctetEscaping.escapeIllegalCharacters(gaid.getRepositoryUniqueId()), 
					gaid instanceof PatientArtifactIdentifier ? 
						filenameOctetEscaping.escapeIllegalCharacters( ((PatientArtifactIdentifier)gaid).getPatientIdentifier() ) :
						"UnknownPatientIdentifier" 
				};
	}

	protected static String[] createInternalInstanceGroupKeys(GlobalArtifactIdentifier gaid, boolean documentIdIsGroup )
	{
		if(gaid instanceof ImageURN)
		{
			if(documentIdIsGroup)
			{
				return new String[]
				   		{ 
							filenameOctetEscaping.escapeIllegalCharacters(gaid.getRepositoryUniqueId()), 
				   			filenameOctetEscaping.escapeIllegalCharacters( ((ImageURN)gaid).getThePatientIdentifier().toString() ),
				   			filenameOctetEscaping.escapeIllegalCharacters( ((ImageURN)gaid).getStudyId() )
				   		};
			}
			else
			{
				return new String[]
						{ 
						filenameOctetEscaping.escapeIllegalCharacters(gaid.getRepositoryUniqueId()), 
			   			filenameOctetEscaping.escapeIllegalCharacters( ((ImageURN)gaid).getThePatientIdentifier().toString() ),
			   			filenameOctetEscaping.escapeIllegalCharacters( ((ImageURN)gaid).getStudyId() ),
			   			filenameOctetEscaping.escapeIllegalCharacters( ((ImageURN)gaid).getImageId() )
					};
				
			}				
		}
		else
		{
			String patientIdentifier = "UnknownPatientIdentifier";
			if(gaid instanceof AbstractImagingURN)
			{
				patientIdentifier = filenameOctetEscaping.escapeIllegalCharacters( ((AbstractImagingURN)gaid).getThePatientIdentifier().toString());
			}
			else if(gaid instanceof PatientArtifactIdentifier)
			{
				patientIdentifier = filenameOctetEscaping.escapeIllegalCharacters( ((PatientArtifactIdentifier)gaid).getPatientIdentifier());
			}
			if(documentIdIsGroup)
			{
				return new String[]
				   		{ 
						filenameOctetEscaping.escapeIllegalCharacters(gaid.getRepositoryUniqueId()), 
						patientIdentifier, 
			   			filenameOctetEscaping.escapeIllegalCharacters(gaid.getDocumentUniqueId())
			   		};
				
			}
			else
			{
				return new String[]
						{ 
			   			filenameOctetEscaping.escapeIllegalCharacters(gaid.getRepositoryUniqueId()), 
			   			patientIdentifier
					};
			}
		}
	}
	
	/**
	 * 
	 * @param gaid
	 * @return
	 */
	protected static String[] createExternalInstanceGroupKeys(GlobalArtifactIdentifier gaid)
	{
		String[] groups = new String[]{
			filenameOctetEscaping.escapeIllegalCharacters(gaid.getHomeCommunityId()),
			filenameOctetEscaping.escapeIllegalCharacters(gaid.getRepositoryUniqueId()),
			filenameOctetEscaping.escapeIllegalCharacters(
				gaid instanceof ImageURN ? ((ImageURN)gaid).getStudyId() : "UnknownStudy"
			)
		};
		return groups;
	}

	/**
	 * 
	 * @param gaid
	 * @param quality TODO
	 * @param mediaType TODO
	 * @return
	 */
	protected static String createExternalImageKey(GlobalArtifactIdentifier gaid, String quality, String mediaType)
	{
		return
			filenameOctetEscaping.escapeIllegalCharacters(
				gaid instanceof ImageURN ? ((ImageURN)gaid).getImageId() : gaid.getDocumentUniqueId()
			) + "-" + 
			(quality == null || quality.length() == 0 ? "" : filenameOctetEscaping.escapeIllegalCharacters(quality)) + "-" +
			(mediaType == null || mediaType.length() == 0 ? "" : filenameOctetEscaping.escapeIllegalCharacters(mediaType));
	}
	
	protected static String createDocumentKey(GlobalArtifactIdentifier gaid)
	{
		return filenameOctetEscaping.escapeIllegalCharacters(gaid.getDocumentUniqueId());
	}
	
	protected static String createStudyKey(GlobalArtifactIdentifier gaid){return "study.xml";}
	protected static String createDocumentMetadataKey(GlobalArtifactIdentifier gaid){return "documentMetadata.xml";}

	protected static String createInternalImageKey(GlobalArtifactIdentifier gaid, String quality, String mediaType)
	{
		if(gaid instanceof BhieImageURN)
			return createExternalImageKey(gaid, quality, mediaType);
		if(gaid instanceof ImageURN)
			return  
				filenameOctetEscaping.escapeIllegalCharacters( ((ImageURN)gaid).getImageId() ) + "-" + 
				filenameOctetEscaping.escapeIllegalCharacters( quality ) + "-" + 
				filenameOctetEscaping.escapeIllegalCharacters( mediaType ) 
			;
		else
			return  
				filenameOctetEscaping.escapeIllegalCharacters( gaid.getDocumentUniqueId() ) + "-" + 
				filenameOctetEscaping.escapeIllegalCharacters( quality ) + "-" + 
				filenameOctetEscaping.escapeIllegalCharacters( mediaType ) 
			;
	}
	
	// =============================================================================================
	// Instance Members
	// =============================================================================================
	private Logger logger = Logger.getLogger(this.getClass());
	protected abstract Cache getWrappedCache();
	protected abstract String getImageRegionName();
	protected abstract String getMetadataRegionName();
	
	// ===============================================================================================
	// Image Content Caching
	// ===============================================================================================
	protected Instance createImage(String[] groups, String instanceKey)
	throws CacheException
	{
		return getWrappedCache().getOrCreateInstance(
				getImageRegionName(), 
				groups, 
				instanceKey );
	}
	
	protected Instance getImage(String[] groups, String instanceKey) 
	throws CacheException
	{
		return getWrappedCache().getInstance(
				getImageRegionName(), 
				groups, 
				instanceKey );
	}

	// ===============================================================================================
	// Study Metadata Caching
	// ===============================================================================================

	protected void createStudy(
		String[] groups,
		String groupKey, 
		gov.va.med.imaging.exchange.business.Study study) 
	throws CacheException
	{
		createMetadata(groups, groupKey, study);
		return;
	}

	/**
	 * 
	 * @param groups
	 * @param groupKey
	 * @return
	 * @throws CacheException
	 */
	protected gov.va.med.imaging.exchange.business.Study getStudy(String[] groups, String groupKey)
	throws CacheException
	{
		return getMetadata(gov.va.med.imaging.exchange.business.Study.class, groups, groupKey);
	}

	// ===============================================================================================
	// Enterprise Exams Metadata Caching
	// ===============================================================================================
	
	protected void createPatientEnterpriseExams(
			String[] groups,
			String groupKey, 
			PatientEnterpriseExams patientEnterpriseExams) 
	throws CacheException
	{
		createMetadata(groups, groupKey, patientEnterpriseExams);
		return;
	}
	
	protected void createExamSite(
			String[] groups,
			String groupKey, 
			ExamSite examSite) 
	throws CacheException
	{
		createMetadata(groups, groupKey, examSite);
		return;
	}

	/**
	 * 
	 * @param groups
	 * @param groupKey
	 * @return
	 * @throws CacheException
	 */
	protected PatientEnterpriseExams getPatientEnterpriseExams(String[] groups, String groupKey)
	throws CacheException
	{
		return getMetadata(PatientEnterpriseExams.class, groups, groupKey);
	}
	
	protected ExamSite getExamSite(String [] groups, String groupKey)
	throws CacheException
	{
		return getMetadata(ExamSite.class, groups, groupKey);
	}

	// ===============================================================================================
	// Document Metadata Caching
	// ===============================================================================================
	@Override
	public void createDocumentMetadata(GlobalArtifactIdentifier gaid, Document document)
	throws CacheException
	{
		String[] groups = AbstractCacheDecorator.createExternalInstanceGroupKeys(gaid, true);
		String instanceKey = AbstractCacheDecorator.createDocumentMetadataKey(gaid);
		
		createDocumentMetadata(groups, instanceKey, document);
	}
	
	@Override
	public Document getDocumentMetadata(GlobalArtifactIdentifier gaid) 
	throws CacheException
	{
		String[] groups = AbstractCacheDecorator.createExternalInstanceGroupKeys(gaid, true);
		String instanceKey = AbstractCacheDecorator.createDocumentMetadataKey(gaid);
		
		return getDocumentMetadata(groups, instanceKey);
	}
	
	// =======================================================================================
	// Document Caching
	// =======================================================================================
	@Override
	public ImmutableInstance createDocumentContent(GlobalArtifactIdentifier gaid) 
	throws CacheException
	{
		String[] groups = AbstractCacheDecorator.createExternalInstanceGroupKeys(gaid, false);
		String instanceKey = AbstractCacheDecorator.createDocumentKey(gaid);
		
		Instance instance = createImage(groups, instanceKey);
		return instance == null ? null : new ImmutableInstance( instance );
	}
	
	@Override
	public ImmutableInstance getDocumentContent(GlobalArtifactIdentifier gaid) 
	throws CacheException
	{
		String[] groups = AbstractCacheDecorator.createExternalInstanceGroupKeys(gaid, false);
		String instanceKey = AbstractCacheDecorator.createDocumentKey(gaid);
		
		Instance instance = getImage(groups, instanceKey);
		return instance == null ? null : new ImmutableInstance( instance );
	}
	
	protected void createDocumentMetadata(
		String[] groups, 
		String groupKey, 
		gov.va.med.imaging.exchange.business.documents.Document document) 
	throws CacheException
	{
		createMetadata(groups, groupKey, document);
		return;
	}
	
	/**
	 * @param groups
	 * @param groupKey
	 * @return
	 * @throws CacheException 
	 */
	protected gov.va.med.imaging.exchange.business.documents.Document getDocumentMetadata(String[] groups, String groupKey) 
	throws CacheException
	{
		return getMetadata(gov.va.med.imaging.exchange.business.documents.Document.class, groups, groupKey);
	}

	// ===============================================================================================
	// Genericized Global Artifact Identifier based methods
	// ===============================================================================================
	@Override
	public <T extends Serializable> void create(GlobalArtifactIdentifier gai, T object ) 
	throws CacheException
	{
		String[] groups = AbstractCacheDecorator.createExternalInstanceGroupKeys(gai, false);
		String keyIdentifier = AbstractCacheDecorator.createDocumentKey(gai);
		createMetadata(groups, keyIdentifier, object);
	}
	
	@Override
	public <T extends Serializable> T get(Class<T> expectedClass, GlobalArtifactIdentifier gai) 
	throws CacheException
	{
		String[] groups = AbstractCacheDecorator.createExternalInstanceGroupKeys(gai, false);
		String keyIdentifier = AbstractCacheDecorator.createDocumentKey(gai);
		
		return getMetadata(expectedClass, groups, keyIdentifier);
	}
	
	// ===============================================================================================
	// Genericized Metadata Caching Methods
	// ===============================================================================================
	
	/**
	 * @param groups
	 * @param groupKey
	 * @return
	 * @throws CacheException 
	 */
	protected <T> T getMetadata(Class<T> expectedResultClass, String[] groups, String groupKey) 
	throws CacheException
	{
		T result = null;
		
		ReadableByteChannel metadataReadable = null;
		ObjectInputStream metadataInStream = null;
		try
		{
			Instance studyMetadataInstance = getWrappedCache().getInstance(
					getMetadataRegionName(), 
					groups, 
					groupKey);
			
			if(studyMetadataInstance != null)
			{
				metadataReadable = studyMetadataInstance.getReadableChannel();
				
				metadataInStream = new ObjectInputStream(Channels.newInputStream(metadataReadable));
				
				return expectedResultClass.cast( metadataInStream.readObject() );
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{if(metadataInStream != null)metadataInStream.close();}
			catch(Throwable t){}
		}
		
		return result;	
	}

	/**
	 * 
	 * @param <T>
	 * @param groups
	 * @param groupKey
	 * @param document
	 * @throws CacheException
	 */
	protected <T> void createMetadata(
		String[] groups, 
		String groupKey, 
		T metadata) 
	throws CacheException
	{
		WritableByteChannel metadataWritable = null;
		ObjectOutputStream metadataOutStream = null;
		try
		{
			if( getWrappedCache().isEnabled().booleanValue() )
			{
				Instance metadataInstance = getWrappedCache().getOrCreateInstance(
						getMetadataRegionName(), 
						groups, 
						groupKey);
				if(metadataInstance != null)
				{
					metadataWritable = metadataInstance.getWritableChannel();
					metadataOutStream = new ObjectOutputStream(Channels.newOutputStream(metadataWritable));
					
					metadataOutStream.writeObject(metadata);
				}
				else
					logger.warn("Unable to write to cache and cache is enabled.  Application will continue to operate with reduced performance.");
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{if(metadataOutStream != null) metadataOutStream.close();}
			catch(Throwable t){}
		}
		
		return;
	}
}
