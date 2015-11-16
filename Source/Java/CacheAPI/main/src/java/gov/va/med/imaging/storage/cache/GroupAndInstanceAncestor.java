/**
 * 
 */
package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * @author VHAISWBECKEC
 * 
 * A collection of methods on Group and Instance instances that are
 * generally passed through recursively until the parent of the
 * Group or Instance actually does the operation. 
 *
 * e.g. Region and Group instances pass calls to getInstance() until
 * the owner of the Instance is found, which then calls getChildInstance().
 */
public interface GroupAndInstanceAncestor 
{
	// ============================================================================
	// Group Related Methods
	// ============================================================================
	public Group getOrCreateGroup(String[] group)
	throws CacheException;

	public Group getGroup(String[] group) 
	throws CacheException;

	public void deleteGroup(String[] group, boolean forceDelete) 
	throws CacheException;

	// ============================================================================
	// Instance Related Methods
	// ============================================================================
	public Instance getOrCreateInstance(String[] group, String key)
	throws CacheException;

	public Instance getInstance(String[] group, String key) 
	throws CacheException;

	public void deleteInstance(String[] group, String key, boolean forceDelete) 
	throws CacheException;

}
