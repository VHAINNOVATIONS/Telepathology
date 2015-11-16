/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Jul 9, 2010
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

package gov.va.med.imaging.exchange.storage.cache;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.documents.Document;
import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is an early implementation of a cache decorator based only on the 
 * global artifact identifier components rather than enterprise specific
 * identifiers.
 * 
 * Currently it uses the VA and DoD cache decorators so as to not change the behavior
 * of the existing system.
 * 
 * This class implements the RealizedCache interface.  It delegates all operations to the
 * child RealizedCache implementations, the VA and DoD cache decorators.
 * 
 * @author vhaiswbeckec
 *
 */
public class CommonCache
implements RealizedCache
{
	private final Map<RoutingToken, RealizedCache> cacheMap;
	private final Cache wrappedCache;
	
	public CommonCache(Cache wrappedCache, RealizedCache intraEnterpriseCache, RealizedCache extraEnterpriseCache)
	{
		this.wrappedCache = wrappedCache;
		
		// use a LinkedHashMap to maintain insertion ordering
		cacheMap = new LinkedHashMap<RoutingToken, RealizedCache>();
		
		try
		{
			cacheMap.put(RoutingTokenImpl.createVADocumentSite("200"), (RealizedCache)intraEnterpriseCache);
			cacheMap.put(RoutingTokenImpl.createVARadiologySite("200"), (RealizedCache)intraEnterpriseCache);
			cacheMap.put(RoutingTokenImpl.createVADocumentSite(RoutingToken.ROUTING_WILDCARD), (RealizedCache)intraEnterpriseCache);
			cacheMap.put(RoutingTokenImpl.createVARadiologySite(RoutingToken.ROUTING_WILDCARD), (RealizedCache)intraEnterpriseCache);
		}
		catch (RoutingTokenFormatException x)
		{
			x.printStackTrace();
			throw new InstantiationError(x.getMessage()); 
		}
		
		cacheMap.put((RoutingToken)null, (RealizedCache)extraEnterpriseCache);
	}
	
	/**
	 * 
	 * @param homeCommunityId
	 * @return
	 */
	private RealizedCache getMappedCache(RoutingToken routingToken)
	{
		for(Map.Entry<RoutingToken, RealizedCache> cacheMapEntry : cacheMap.entrySet())
			if( cacheMapEntry.getKey().isIncluding(routingToken) )
				return cacheMapEntry.getValue();
		
		return cacheMap.get(null);
	}

	/**
	 * @return the wrappedCache
	 */
	public Cache getWrappedCache()
	{
		return this.wrappedCache;
	}

	/**
	 * @see gov.va.med.imaging.exchange.storage.cache.RealizedCache#createDocumentContent(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ImmutableInstance createDocumentContent(GlobalArtifactIdentifier gaid)
	throws CacheException
	{
		return getMappedCache(gaid).createDocumentContent(gaid);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.cache.CommonSourcedCache#createDocumentMetadata(java.lang.String, java.lang.String, java.lang.String, gov.va.med.imaging.exchange.business.documents.Document)
	 */
	public void createDocumentMetadata(GlobalArtifactIdentifier gaid, Document document)
		throws CacheException
	{
		getMappedCache(gaid).createDocumentMetadata(gaid, document);
	}

	/**
	 * 
	 * @param gaid
	 * @return
	 * @throws CacheException
	 */
	public ImmutableInstance getDocumentContent(GlobalArtifactIdentifier gaid)
		throws CacheException
	{
		return getMappedCache(gaid).createDocumentContent(gaid);
	}

	/**
	 * 
	 * @param gaid
	 * @return
	 * @throws CacheException
	 */
	public Document getDocumentMetadata(GlobalArtifactIdentifier gaid)
		throws CacheException
	{
		return getMappedCache(gaid).getDocumentMetadata(gaid);
	}

	/**
	 * 
	 * @param gaid
	 * @return
	 * @throws CacheException
	 */
	@Override
	public Study getStudy(GlobalArtifactIdentifier gaid) 
	throws CacheException
	{
		return getMappedCache(gaid).getStudy(gaid);
	}

	/**
	 * 
	 */
	@Override
	public void createStudy(Study study) 
	throws CacheException
	{
		GlobalArtifactIdentifier gaid = study.getStudyUrn();
		getMappedCache(gaid).createStudy(study);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.cache.RealizedCache#get(java.lang.Class, gov.va.med.GlobalArtifactIdentifier)
	 */
	public <T extends Serializable> T get(Class<T> expectedClass, GlobalArtifactIdentifier gaid) 
	throws CacheException
	{
		return getMappedCache(gaid).get(expectedClass, gaid);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.cache.RealizedCache#create(gov.va.med.GlobalArtifactIdentifier, T)
	 */
	public <T extends Serializable> void create(GlobalArtifactIdentifier gaid, T object) 
	throws CacheException
	{
		getMappedCache(gaid).create(gaid, object);
	}
}
