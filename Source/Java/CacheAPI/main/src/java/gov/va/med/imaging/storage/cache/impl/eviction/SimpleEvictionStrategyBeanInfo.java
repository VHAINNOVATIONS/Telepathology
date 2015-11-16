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
public class SimpleEvictionStrategyBeanInfo 
extends SimpleBeanInfo 
implements BeanInfo
{
	@Override
	public BeanDescriptor getBeanDescriptor()
	{
		BeanDescriptor descriptor = new BeanDescriptor(SimpleEvictionStrategy.class);
		
		descriptor.setShortDescription("A simple null eviction strategy for extension, testing or for immortal regions.");
		
		return descriptor;
	}


}
