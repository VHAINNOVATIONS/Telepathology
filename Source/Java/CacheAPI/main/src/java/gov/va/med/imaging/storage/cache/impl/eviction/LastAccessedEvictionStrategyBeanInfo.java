/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.eviction;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.MethodDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * @author VHAISWBECKEC
 *
 */
public class LastAccessedEvictionStrategyBeanInfo 
extends SimpleBeanInfo
implements BeanInfo
{
	@Override
	public BeanDescriptor getBeanDescriptor()
	{
		BeanDescriptor descriptor = new BeanDescriptor(LastAccessedEvictionStrategy.class);
		
		descriptor.setShortDescription("An eviction strategy that removes groups that are greater than a set age.");
		
		return descriptor;
	}
}
