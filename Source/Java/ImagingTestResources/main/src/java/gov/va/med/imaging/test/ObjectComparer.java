/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 25, 2011
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
  Description: 

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
package gov.va.med.imaging.test;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author vhaiswwerfej
 *
 */
public class ObjectComparer
{
	public static <T extends Object> void compareObjects(T obj1, T obj2, String [] ignoreMethods)
	throws InvocationTargetException, IllegalAccessException
	{
		
		Method [] methods = obj1.getClass().getMethods();
		for(Method method : methods)
		{
			String methodName = method.getName();
			if((methodName.startsWith("get")) || (methodName.startsWith("is")))
			{
				Class<?> [] parameters = method.getParameterTypes();
				if(parameters.length > 0)
				{
					//System.out.println("Skipping method '" + methodName + "' because it contains '" + parameters.length + "' parameters");
				}
				else
				{
				
					boolean isIgnoreMethod = false;
					for(String ignoreMethod : ignoreMethods)
					{
						if(ignoreMethod.equalsIgnoreCase(methodName))
						{
							isIgnoreMethod = true;
							break;
						}
					}
					if(!isIgnoreMethod)
					{
						//System.out.println("Invoking method '" + methodName + "'.");
						Object study1Value = method.invoke(obj1, null);
						Object study2Value = method.invoke(obj2, null);
						assertEquals("Method '" + methodName + "' values do not match", study1Value, study2Value);
					}
					else
					{
						//System.out.println("Skipping method '" + methodName + "'");
					}
				}
			}
		}
	}
}
