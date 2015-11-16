/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Oct 17, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
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
package gov.va.med.imaging.core.interfaces.router;

import gov.va.med.imaging.core.interfaces.Router;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

import org.apache.log4j.Logger;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.ThrowsAdvice;

/**
 * @author VHAISWBECKEC
 *
 */
public class RouterStatisticsAdvice 
implements MethodBeforeAdvice, AfterReturningAdvice, ThrowsAdvice, DynamicMBean
{
	private Map<String, RouterMethodStatistics> methodStatistics;
	private Logger logger = Logger.getLogger(this.getClass());

	public RouterStatisticsAdvice()
    {
		logger = Logger.getLogger(this.getClass());
		
		getLogger().info("Registering monitored router methods.");
		methodStatistics = new HashMap<String, RouterMethodStatistics>();
		
		Class<Router> routerInterfaceClass = Router.class;
		for(Method method : routerInterfaceClass.getDeclaredMethods() )
		{
			methodStatistics.put(method.getName(), new RouterMethodStatistics() ); 
		}
		
		getLogger().info("Monitored router methods registered, registering managed bean.");
	}

	protected Logger getLogger()
	{
		return logger;
	}
	
	/**
	 * Return an iterator of all of the monitored method names
	 * @return
	 */
	public Iterator<String> getMonitoredMethodNames()
	{
		return methodStatistics.keySet().iterator();
	}

	/**
	 * Get the method statistics for the named, monitored, method since the last
	 * reset.
	 * 
	 * @param methodName
	 * @return
	 */
	public RouterMethodStatistics getMethodStatistics(String methodName)
	{
		return methodStatistics.get(methodName);
	}
	
	/**
	 * reset the statistics for the names method
	 * @param methodName
	 */
	public void resetMethodStatistics(String methodName)
	{
		methodStatistics.get(methodName).reset();
	}
	
	/**
	 * Reset all of the method statistics
	 */
	public void resetAllMethodStatistics()
	{
		for(String methodName : methodStatistics.keySet())
			methodStatistics.get(methodName).reset();
	}
	
	/**
	 * @see org.springframework.aop.MethodBeforeAdvice#before(java.lang.reflect.Method, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public void before(Method method, Object[] args, Object target) 
	throws Throwable
	{
		RouterMethodStatistics ms = methodStatistics.get(method.getName());
		if(ms != null)
		{
			getLogger().trace("Before calling '" + method.getName() + "(" + createArgLoggable(args) + ").");
			ms.incrementCalledCount();
		}
	}

	/**
	 * @see org.springframework.aop.AfterReturningAdvice#afterReturning(java.lang.Object, java.lang.reflect.Method, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public void afterReturning(Object result, Method method, Object[] args, Object target) 
	throws Throwable
	{
		RouterMethodStatistics ms = methodStatistics.get(method.getName());
		if(ms != null)
		{
			ms.incrementSuccessCount();
			
			if( ! "void".equals(method.getReturnType().toString()) && result == null )
			{
				getLogger().trace("After returning from '" + method.getName() + "(" + createArgLoggable(args) + 
						") with null value.");
				ms.incrementNullResultCount();
			}
			else
				getLogger().trace("After returning from '" + method.getName() + "(" + createArgLoggable(args) + 
						"), result is '" + createResultLoggable(result) + "'.");
				
		}
	}

	public void afterThrowing(Method method, Object[] args, Object target, Throwable throwable) 
	throws Throwable
	{
		RouterMethodStatistics ms = methodStatistics.get(method.getName());
		if(ms != null)
		{
			getLogger().trace("After returning from '" + method.getName() + "(" + createArgLoggable(args) + 
				") with exception '" + createThrowableLoggable(throwable) + "'.");
			ms.incrementThrowableCount();
		}
	}

	/**
	 * Format an array of Object (parameters) into a String.
	 * @param args
	 * @return
	 */
	private String createArgLoggable(Object[] args)
    {
		if(args == null || args.length == 0)
			return "";
		StringBuffer sb = new StringBuffer();
		for(Object arg : args)
		{
			if(sb.length() > 0)
				sb.append(',');
			sb.append(arg == null ? "<null>" : arg.getClass().getName());
		}
		
	    return sb.toString();
    }
	
	/**
	 * 
	 * @param result
	 * @return
	 */
	private String createResultLoggable(Object result)
    {
		if(result == null)
			return "null";
		
		if(result instanceof Collection<?>)
			return "collection of " + ((Collection<?>)result).size() + " entries";
		if(result instanceof Map<?, ?>)
			return "map of " + ((Map<?, ?>)result).size() + " entries";
		
		return result.getClass().getName();
    }

	/**
	 * NOTE: recursive method (follows getCause() chain)
	 * @param throwable
	 * @return
	 */
	private String createThrowableLoggable(Throwable throwable)
    {
		if(throwable == null)
			return "<null (this should not happen!>";
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(throwable.getClass().getName());
		sb.append(':');
		sb.append(throwable.getMessage());
		if(throwable.getCause() != null)
		{
			sb.append(", caused by ");
			sb.append( createThrowableLoggable(throwable.getCause()) );
		}
	    return sb.toString();
    }
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		String lineBreak = System.getProperty("line.seperator");
		
		for(String methodName : methodStatistics.keySet())
		{
			sb.append(methodName);
			sb.append('[');
			sb.append(methodStatistics.get(methodName).toString());
			sb.append(']');
			sb.append(lineBreak);
		}
		
		return sb.toString();
	}

	// ==============================================================================================
	// Dynamic MBean Implementation
	// ==============================================================================================

	/**
	 * Obtain the value of a specific attribute of the Dynamic MBean. 
	 */
	@Override
    public Object getAttribute(String attribute) 
	throws AttributeNotFoundException, MBeanException, ReflectionException
    {
		String methodName = parseMethodName(attribute);
		String dataPointName = parseDataPoint(attribute);
		
		if(methodName != null && dataPointName != null)
		{
			RouterMethodStatistics stats = this.methodStatistics.get(methodName);
			if(stats != null)
			{
				Method getterMethod = dataPointGetterMethod(dataPointName);
				try
                {
	                return getterMethod.invoke(stats, (Object[])null);
                } 
				catch (Exception e)
                {
					Logger.getLogger(RouterStatisticsAdvice.class).error("Exception '" + e.getMessage() + "', getting '" + attribute + "'.");
                }
			}
		}
		
	    return null;
    }

	/**
	 * Get the values of several attributes of the Dynamic MBean. 
	 * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
	 */
	@Override
    public AttributeList getAttributes(String[] attributes)
    {
		AttributeList result = new AttributeList();
		
		for(String attribute : attributes)
	        try
            {
	            result.add(new Attribute(attribute, getAttribute(attribute)));
            } 
			catch (Exception e)
            {
				Logger.getLogger(RouterStatisticsAdvice.class).error("Exception '" + e.getMessage() + "', getting '" + attribute + "'.");
            }
			
		return result;
    }

	/**
	 * Allows an action to be invoked on the Dynamic MBean. 
	 * @see javax.management.DynamicMBean#invoke(java.lang.String, java.lang.Object[], java.lang.String[])
	 */
	@Override
    public Object invoke(String actionName, Object[] params, String[] signature) 
	throws MBeanException, ReflectionException
    {
		if("resetAllMethodStatistics".equals(actionName) && (params == null || params.length == 0) )
			resetAllMethodStatistics();
		
	    return null;
    }

	/**
	 * Set the value of a specific attribute of the Dynamic MBean. 
	 * 
	 * @see javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
	 */
	@Override
    public void setAttribute(Attribute attribute) 
	throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
    {
	    
    }

	/**
	 * Sets the values of several attributes of the Dynamic MBean. 
	 * @see javax.management.DynamicMBean#setAttributes(javax.management.AttributeList)
	 */
	@Override
    public AttributeList setAttributes(AttributeList attributes)
    {
	    return null;
    }
	
	/**
	 * Provides the exposed attributes and actions of the Dynamic MBean using an MBeanInfo object. 
	 * @see javax.management.DynamicMBean#getMBeanInfo()
	 */
	@Override
    public MBeanInfo getMBeanInfo()
    {
		MBeanConstructorInfo[] constructors = null;
		MBeanAttributeInfo[] attributes = getMBeanAttributes();
		MBeanOperationInfo[] operations = getMBeanOperations();
		MBeanNotificationInfo[] notifications = null;
		
		MBeanInfo info = new MBeanInfo(
				this.getClass().getName(), 
				"Router Statistics", 
				attributes, 
				constructors, 
				operations, 
				notifications);
		
	    return info;
    }

	// this value must match the number of data points each method provides
	// which is the same as the number of getters in the RouterMethodStatistics class
	private static Class<?> routerMethodStatisticsClass;
	private static Method[] routerMethodDataPoints;
	
	static
	{
		try
		{
			routerMethodStatisticsClass = RouterMethodStatistics.class;
			routerMethodDataPoints = new Method[]
			{
				routerMethodStatisticsClass.getMethod("getCalledCount", (Class<?>[])null),
				routerMethodStatisticsClass.getMethod("getSuccessCount", (Class<?>[])null),
				routerMethodStatisticsClass.getMethod("getThrowableCount", (Class<?>[])null),
				routerMethodStatisticsClass.getMethod("getNullResultCount", (Class<?>[])null),
				routerMethodStatisticsClass.getMethod("getResetTime", (Class<?>[])null)
			};
		}
		catch(Exception x)
		{
			Logger.getLogger(RouterStatisticsAdvice.class).error("Exception '" + x.getMessage() + "', initializing router statistics advice.");
		}
	}
	
	private MBeanAttributeInfo[] getMBeanAttributes()
    {
		// each method on which we gather statistics provide five data points;
		// calls, success, null results, throwable and reset time
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[this.methodStatistics.size() * routerMethodDataPoints.length];
		int methodIndex = 0;
		for(String methodName : this.methodStatistics.keySet())
		{
			int dataPointIndex = 0;
			for(Method dataPointMethod : routerMethodDataPoints)
			{
				String attributeName = makeAttributeName(methodName, dataPointMethod.getName());
				MBeanAttributeInfo attributeInfo = new MBeanAttributeInfo(
						attributeName, 
						dataPointMethod.getReturnType().getName(), 
						"", true, false, false);
				attributes[(methodIndex * routerMethodDataPoints.length) + dataPointIndex] = attributeInfo;
				++dataPointIndex;
			}
			++methodIndex;
		}
		
	    return attributes;
    }

	private MBeanOperationInfo[] getMBeanOperations()
    {
		Method resetMethod;
        try
        {
	        resetMethod = this.getClass().getMethod("resetAllMethodStatistics", (Class<?>[])null);
	        
	        return new MBeanOperationInfo[]
	        {
	        	new MBeanOperationInfo("Reset all method counters.", resetMethod)
	        };
        } 
        catch (Exception e)
        {
        	Logger.getLogger(this.getClass()).error("Exception '" + e.getMessage() + "' finding operations.");
        	return null;
        }
    }

	// =========================================================================
	// Helper Methods to create and parse method and data points into attributes
	// =========================================================================
	private static final String attributeNameDelimiter = "_";
	
	private String makeAttributeName(String methodName, String dataPoint)
	{
		return methodName.indexOf(attributeNameDelimiter) >= 0 ? null :
			dataPoint.indexOf(attributeNameDelimiter) >= 0 ? null :
			methodName + attributeNameDelimiter + dataPoint;
	}
	
	private String parseMethodName(String attributeName)
	{
		String[] parts = attributeName.split(attributeNameDelimiter);
		if(parts.length == 2)
			return parts[0];
		
		return null;
	}
	
	private String parseDataPoint(String attributeName)
	{
		String[] parts = attributeName.split(attributeNameDelimiter);
		if(parts.length == 2)
			return parts[1];
		
		return null;
	}
	
	private Method dataPointGetterMethod(String dataPointName)
	{
		if(dataPointName != null)
			for(Method method : routerMethodDataPoints)
				if(dataPointName.equals(method.getName()))
					return method;
		
		return null;
	}
}
