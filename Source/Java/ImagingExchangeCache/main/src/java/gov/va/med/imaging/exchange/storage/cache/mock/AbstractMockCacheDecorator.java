/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Jan 20, 2011
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */

package gov.va.med.imaging.exchange.storage.cache.mock;

import java.io.Serializable;
import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.documents.Document;
import gov.va.med.imaging.exchange.enums.StudyDeletedImageState;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.exchange.storage.cache.ImmutableInstance;
import gov.va.med.imaging.exchange.storage.cache.RealizedCache;
import gov.va.med.imaging.storage.cache.Instance;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * @author vhaiswbeckec
 *
 */
public abstract class AbstractMockCacheDecorator
implements RealizedCache
{

	public void createStudy(Study study) throws CacheException
	{
		if( MockCacheConfigurator.getCreateStudyCacheException() != null)
			throw MockCacheConfigurator.getCreateStudyCacheException();
		
	}

	public Study getStudy(GlobalArtifactIdentifier gai) throws CacheException
	{
		if( MockCacheConfigurator.getGetStudyCacheException() != null)
			throw MockCacheConfigurator.getGetStudyCacheException();
		
		return Study.create(gai, StudyLoadLevel.STUDY_ONLY, 
				StudyDeletedImageState.cannotIncludeDeletedImages);
	}

	public ImmutableInstance createDocumentContent(GlobalArtifactIdentifier gaid) throws CacheException
	{
		if( MockCacheConfigurator.getCreateImageCacheException() != null)
			throw MockCacheConfigurator.getCreateImageCacheException();
	
		Instance instance = new MockInstance();
		ImmutableInstance immutableInstance = new ImmutableInstance(instance);
		return immutableInstance;
	}

	public ImmutableInstance getDocumentContent(GlobalArtifactIdentifier gaid) throws CacheException
	{
		if( MockCacheConfigurator.getCreateImageCacheException() != null)
			throw MockCacheConfigurator.getCreateImageCacheException();
	
		Instance instance = new MockInstance();
		ImmutableInstance immutableInstance = new ImmutableInstance(instance);
		return immutableInstance;
	}

	/**
	 * @see gov.va.med.imaging.exchange.storage.cache.RealizedCache#createDocumentMetadata(gov.va.med.GlobalArtifactIdentifier, gov.va.med.imaging.exchange.business.documents.Document)
	 */
	public void createDocumentMetadata(GlobalArtifactIdentifier gaid, Document document) throws CacheException
	{
		if( MockCacheConfigurator.getCreateStudyCacheException() != null)
			throw MockCacheConfigurator.getCreateStudyCacheException();
	}

	/**
	 * @see gov.va.med.imaging.exchange.storage.cache.RealizedCache#getDocumentMetadata(gov.va.med.GlobalArtifactIdentifier)
	 */
	public Document getDocumentMetadata(GlobalArtifactIdentifier gaid) throws CacheException
	{
		if( MockCacheConfigurator.getGetStudyCacheException() != null)
			throw MockCacheConfigurator.getGetStudyCacheException();
		
		return Document.create(gaid); 
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.cache.RealizedCache#create(gov.va.med.GlobalArtifactIdentifier, java.io.Serializable)
	 */
	@Override
	public <T extends Serializable> void create(GlobalArtifactIdentifier gai, T object) 
	throws CacheException
	{
		
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.cache.RealizedCache#get(java.lang.Class, gov.va.med.GlobalArtifactIdentifier)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> T get(Class<T> expectedClass, GlobalArtifactIdentifier gai) 
	throws CacheException
	{
		if( MockCacheConfigurator.getGetStudyCacheException() != null)
			throw MockCacheConfigurator.getGetStudyCacheException();
		
		return (T)Document.create(gai); 
	}

}
