/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Nov, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
  Description: DICOM Study cache manager. Maintains the cache of study instances
  			   and expires old studies after 15 minutes. 

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
package gov.va.med.imaging.vistaimagingdatasource.common;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.exceptions.DaoMethodNotImplementedException;
import gov.va.med.imaging.exchange.business.PersistentEntity;
import gov.va.med.imaging.url.vista.VistaQuery;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public abstract class CacheableEntityDAO<T extends PersistentEntity> extends EntityDAO<T>
{
	// Template methods
	protected void cacheEntity(T t) throws MethodException { throw new DaoMethodNotImplementedException(); }
	protected T getEntityFromCache(String id) { return null; }
	
	protected void cacheEntityByCriteria(Object criteria, T t) throws MethodException { throw new DaoMethodNotImplementedException(); }
	protected T getEntityFromCacheByCriteria(Object criteria) { return null; }

	protected void cacheEntityByExample(T t) throws MethodException { throw new DaoMethodNotImplementedException(); }
	protected T getEntityFromCacheByExample(T t) { return null; }


	@Override
	public T create(T t) throws MethodException, ConnectionException
	{
		VistaQuery vm = generateCreateQuery(t);
		t = translateCreate(t, executeRPC(vm));

		// Add the result to the cache
		if (t != null)
		{
			cacheEntity(t);
		}

		// Return the result
		return t;

	}

	@Override
	public T getEntityById(String id) throws MethodException, ConnectionException
	{
		// Attempt to find the entity in the cache.
		T t = getEntityFromCache(id);

		// If not in the cache, get it from the DB and cache it.
		if (t==null)
		{
			// Item wasn't in the cache. Retrieve results from the database,
			// cache it, and then return it
			VistaQuery vm = generateGetEntityByIdQuery(id);
			t = translateGetEntityById(id, executeRPC(vm));
				
			if (t!=null)
			{
				cacheEntity(t);
			}
		}

		// Return the result
		return t;

	}	

	//
	// Get Entity by Criteria
	//
	@Override
	public T getEntityByCriteria(Object criteria) throws MethodException, ConnectionException
	{
		T t = null;
		
		t = getEntityFromCacheByCriteria(criteria);

		if (t==null)
		{
			// Item wasn't in the cache. Retrieve results from the database,
			// cache it, and then return it
			VistaQuery vm = generateGetEntityByCriteriaQuery(criteria);
			t = translateGetEntityByCriteria(criteria, executeRPC(vm));

			if (t!=null)
			{
				// Add the result to the cache
				cacheEntityByCriteria(criteria, t);
			}
		}

		// Return the result
		return t;

	}	
	

	//
	// Get Entity by Criteria
	//
	@Override
	public T getEntityByExample(T t) throws MethodException, ConnectionException
	{
		
		T cachedEntity = getEntityFromCacheByExample(t);
		if (cachedEntity!=null)
		{
			// Found the entity in the cache! Use the cached version...
			t = cachedEntity;
		}
		else
		{
			// Item wasn't in the cache. Retrieve results from the database,
			// cache it, and then return it
			VistaQuery vm = generateGetEntityByExampleQuery(t);
			t = translateGetEntityByExample(t, executeRPC(vm));

			if (t!=null)
			{
				// Add the result to the cache
				cacheEntityByExample(t);
			}
		}

		// Return the result
		return t;

	}	

}
