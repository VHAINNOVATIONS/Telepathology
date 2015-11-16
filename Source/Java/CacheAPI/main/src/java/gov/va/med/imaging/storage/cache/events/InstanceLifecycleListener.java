/**
 * 
 */
package gov.va.med.imaging.storage.cache.events;

/**
 * @author VHAISWBECKEC
 *
 */
public interface InstanceLifecycleListener
{
	public abstract void notify(InstanceLifecycleEvent event);
}
