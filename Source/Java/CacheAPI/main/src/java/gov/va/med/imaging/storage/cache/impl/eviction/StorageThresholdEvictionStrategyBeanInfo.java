/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.eviction;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.SimpleBeanInfo;

/**
 * @author VHAISWBECKEC
 *
 */
public class StorageThresholdEvictionStrategyBeanInfo 
extends SimpleBeanInfo 
implements BeanInfo
{
	@Override
	public BeanDescriptor getBeanDescriptor()
	{
		BeanDescriptor descriptor = new BeanDescriptor(StorageThresholdEvictionStrategy.class);
		
		descriptor.setShortDescription("An eviction strategy that removes the oldest groups to increase free space to a set threshold.");
		
		return descriptor;
	}

}
