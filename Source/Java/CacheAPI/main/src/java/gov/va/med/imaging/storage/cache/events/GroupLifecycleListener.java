/**
 * 
 */
package gov.va.med.imaging.storage.cache.events;

/**
 * @author VHAISWBECKEC
 *
 */
public interface GroupLifecycleListener
{
	public abstract void notify(GroupLifecycleEvent event);
}
