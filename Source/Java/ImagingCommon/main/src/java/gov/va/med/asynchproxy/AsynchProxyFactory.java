package gov.va.med.asynchproxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * 
 * @author VHAISWBECKEC
 *
 * @param <I>
 */
public class AsynchProxyFactory<I>
{
	public AsynchProxyFactory()
	{
		
	}
	
	/**
	 * Creates a dynamic proxy which queues the call and immediately returns from every call.
	 * From an Executor, the proxy invocation will take place asynchronously, the result
	 * is made available from the results queue.
	 * 
	 * @param interfaceClass
	 * @param target
	 * @return
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public I createAsynchProxy(Class<I> interfaceClass, I target, AsynchProxyListener listener) 
	throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		InvocationHandler handler = new AsynchInvocationHandler<I>(target, null, listener);
		Class<?> proxyClass = Proxy.getProxyClass(interfaceClass.getClassLoader(), new Class[]{ interfaceClass });
		Constructor<I> proxyConstructor = (Constructor<I>)proxyClass.getConstructor(new Class[]{ InvocationHandler.class });
		I proxy = (I) proxyConstructor.newInstance(new Object[]{ handler });

		return proxy;
	}
}
