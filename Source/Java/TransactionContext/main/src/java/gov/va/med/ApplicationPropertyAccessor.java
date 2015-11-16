package gov.va.med;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

/**
 * A little helper that wraps up the ugly casting and typing into a
 * templated class.  To use this class create an instance with the target
 * type specified for the template, e.g.
 * ApplicationPropertyAccessor<Short> shortPropertyAccessor = 
 *   new ApplicationPropertyAccessor<Short>("shortValue", "parseShort");
 * The method names should be the method to convert from other base types to the
 * target type and from a String to the target type.
 * 
 * For an example of usage, see 
 * @see gov.va.med.imaging.transactioncontext.TransactionContextProxyInvocationHandler
 * 
 * @author VHAISWBECKEC
 *
 * @param <D> - the type of Object that we are converting to.
 */
public class ApplicationPropertyAccessor<D>
{
	// the instance logger 
	private Logger logger = Logger.getLogger(this.getClass());

	private final String conversionMethodName;
	private final Method parseMethod;		
	// the name of a static method on the target class that will take a String and return an Object of the target class
	
	/**
	 * 
	 */
	public ApplicationPropertyAccessor(String conversionMethodName, Method parseMethod)
	{
		this.conversionMethodName = conversionMethodName;
		this.parseMethod = parseMethod;
	}
	
	@SuppressWarnings("unchecked")
	public D getValueAs(Object value)
	{
		if(value == null)
			return (D)null;

		// if there is no conversion or parsing method available, just return the object cast
		// to the correct type
		if(this.parseMethod == null && this.conversionMethodName == null)
			return (D)value;
		
		// the type of the object we are converting from
		Class<?> valueClass = value.getClass();
		
		try
        {
			// converting from String to the target class usually means calling a parse method
			if(valueClass == String.class && parseMethod != null)
			{
		        D convertedValue = (D)parseMethod.invoke(null, new Object[]{value});
		        return convertedValue;
			}
			
			// converting anything else to the target type requires that the object being converted from
			// has a method with the expected name (something like intValue, floatValue, etc ...)
			else if(conversionMethodName != null)
			{
		        Method conversionMethod = valueClass.getMethod(conversionMethodName, new Class[]{});
		        D convertedValue = (D)conversionMethod.invoke(value, new Object[]{});
		        return convertedValue;
			}
        } 
		catch (Exception e)
        {
			logger.warn(e);
        } 
        return null;
	}
}