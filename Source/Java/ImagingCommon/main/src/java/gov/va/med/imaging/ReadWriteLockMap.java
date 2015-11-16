/**
 * 
 */
package gov.va.med.imaging;

import java.util.Map;

/**
 * @author Administrator
 *
 */
public interface ReadWriteLockMap<K, V> extends Map<K, V>
{
	public void clearAndPutAll(Map<K, V> map);
}
