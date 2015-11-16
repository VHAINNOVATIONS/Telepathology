/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.filesystem;

import gov.va.med.imaging.storage.cache.exceptions.RegionNotInitializedException;
import gov.va.med.imaging.storage.cache.impl.PersistentRegionMBean;

import java.io.File;

/**
 * @author VHAISWBECKEC
 *
 */
public interface FileSystemRegionMBean
extends PersistentRegionMBean
{
	/**
	 * Get the directory that this region is rooted at.
	 */
	public abstract File getRegionDirectory() throws RegionNotInitializedException;

	/**
	 * Get the available free space on the device where this region is persisted.
	 * Return -1 if the freespace cannot be calculated.
	 * NOTEL under JRE 1.5 and before this will always return -1, for JRE 1.6
	 * and later this should work correctly.
	 */
	public abstract long getFreeSpace();

}