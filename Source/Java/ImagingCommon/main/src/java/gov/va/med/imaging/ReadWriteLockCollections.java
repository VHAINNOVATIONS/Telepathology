package gov.va.med.imaging;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

/**
 * This class is a group of static methods that provides similar functionality to that which the 
 * java.util.Collections class provides for synchronized sets, lists and maps.  This class provides 
 * Set, List and Map instances that synchronize using ReadWriteLock implementations so that 
 * multiple read operations may occur simultaneously.  Write operations must be executed serially
 * with respect to both read and write operations.
 * This class creates proxy instances that provide the correct locking and then delegate to the 
 * underlying Set, List or Map instance.
 * 
 * 
 * @author vhaiswbeckec
 * @since 1.0
 * @version 1.0
 */
public class ReadWriteLockCollections
{
	private static Class<?>[] listInterfaces = new Class[]{ReadWriteLockList.class};
	
	/**
	 * 
	 * @param target
	 * @return
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Object> ReadWriteLockList<T> readWriteLockList(List<T> target) 
	throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		InvocationHandler handler = new ListReadWriteLockProxyHandler<T>(target);
		
	    Class<?> proxyClass = Proxy.getProxyClass(
	      ReadWriteLockCollections.class.getClassLoader(), 
	      listInterfaces
	    );
	    
	    Constructor<?> proxyConstructor = proxyClass.getConstructor(new Class[] { InvocationHandler.class });
	    ReadWriteLockList<T> lockList = (ReadWriteLockList<T>) proxyConstructor.newInstance( new Object[] { handler } );
	     
	    return lockList;
	}

	private static Class<?>[] mapInterfaces = new Class[]{ReadWriteLockMap.class};
	
	/**
	 * 
	 * @param target
	 * @return
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings("unchecked")
	public static <K extends Object, V extends Object> ReadWriteLockMap<K, V> readWriteLockMap(Map<K, V> target) 
	throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		InvocationHandler handler = new MapReadWriteLockProxyHandler<K,V>(target);
		
	    Class<?> proxyClass = Proxy.getProxyClass(
	      ReadWriteLockCollections.class.getClassLoader(), 
	      mapInterfaces 
	    );
	    
	    Constructor<?> proxyConstructor = proxyClass.getConstructor(new Class[] { InvocationHandler.class }); 
	    ReadWriteLockMap<K,V> lockMap = (ReadWriteLockMap<K,V>) proxyConstructor.newInstance(new Object[] { handler });
	     
	    return lockMap;
	}
}
