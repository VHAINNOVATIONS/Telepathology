/**
 * 
 */
package gov.va.med;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * An InvocationHandler that passes all calls and parameters through to the
 * proxied object.  This class may be used to work around class loader issues.
 * 
 * @author vhaiswbeckec
 *
 */
public class IdentityProxyInvocationHandler <T>
implements InvocationHandler
{
	private T proxiedObject;
	private Class<T> proxiedObjectClass;
	
	/**
	 * @param proxiedObject
	 */
	@SuppressWarnings("unchecked")
	public IdentityProxyInvocationHandler(Object proxiedObject, Class<?> proxiedObjectClass)
	{
		super();
		this.proxiedObject = (T)proxiedObject;
		this.proxiedObjectClass = (Class<T>)proxiedObjectClass;
	}

	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
	throws Throwable
	{
		Method proxiedMethod = proxiedObjectClass.getMethod(method.getName(), method.getParameterTypes());

		return proxiedMethod.invoke(proxiedObject, args);
	}
}
