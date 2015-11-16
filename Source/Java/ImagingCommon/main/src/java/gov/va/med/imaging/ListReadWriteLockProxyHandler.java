/**
 * 
 */
package gov.va.med.imaging;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class provides the locking for a ReadWriteLock List instance.
 * Calls to the underlying List are intercepted by a dynamic proxy, which
 * delegates to this class.  This class obtains the correct (read or write)
 * lock, invokes the underlying List instance, releases the lock and then
 * returns.
 * The required locking is determined by the ListMethodLocks enum found
 * later in this file.
 * The method(s) defined in the ReadWriteLockList is(are)
 * implemented in this class through reflection.
 * See gov.va.md.imaging.MapReadWriteLockProxyHandler for inter-line comments 
 * applicable to this class also.
 * 
 * @author vhaiswbeckec
 *
 * @param <T>
 */
class ListReadWriteLockProxyHandler<T>
implements InvocationHandler
{
	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private List<T> target;
	
	/**
	 * Create an instance, the target is the List instance to which
	 * we delegate the "real" functionality.  This class just provides
	 * locking around the List instance.
	 * 
	 * @param target
	 */
	public ListReadWriteLockProxyHandler(List<T> target)
	{
		this.target = target;
	}
	
	/**
	 * A List (or ReadWriteLockList) method has been called, intercepted by the proxy,
	 * and now passed to this method.  This methods acquires the necessary lock
	 * and then invokes the method on the underlying List.
	 * 
	 * @param proxy - The proxy instance which has invoked this code
	 * @param method - The method being called on the proxy
	 * @param args - The arguments to the method being called
	 */
	public Object invoke(Object proxy, Method method, Object[] args) 
	throws Throwable
	{
		String methodName = method.getName();
		Object result = null;
		
		ListMethodLocks listMethodLocks = ListMethodLocks.valueOf(methodName);
		
		if(listMethodLocks == null || listMethodLocks.requiresWriteLock)
			readWriteLock.writeLock().lock();
		else if(listMethodLocks.requiresReadLock)
			readWriteLock.readLock().lock();
		
		try
		{
			// special handling because no one actually implements this interface.
			// other than our proxy
			if("clearAndAddAll".equals(methodName) )
			{
				target.clear();
				result = target.addAll((Collection<? extends T>) args[0]);
			}
			else
				result = method.invoke(target, args);
		}
		finally
		{
			if(listMethodLocks == null || listMethodLocks.requiresWriteLock)
				readWriteLock.writeLock().unlock();
			else if(listMethodLocks.requiresReadLock)
				readWriteLock.readLock().unlock();
		}
		
		return result;
	}
	
	/**
	 * This class defines the reuired read or write lock for each method
	 * in the List and ReadWriteLockList interfaces.
	 * 
	 * @author vhaiswbeckec
	 */
	public enum ListMethodLocks
	{
		add(false, true),
		addAll(false, true),
		clear(false, true),
		clearAndAddAll(false, true),
		contains(true, false),
		containsAll(true, false),
		equals(true, false),
		get(true, false),
		indexOf(true, false),
		isEmpty(true, false),
		lastIndexOf(true, false),
		remove(false, true),
		removeAll(false, true),
		retainAll(false, true),
		set(true, false),
		size(true, false),
		subList(true, false),
		toArray(true, false);
		
		boolean requiresReadLock = false;
		boolean requiresWriteLock = false;
		
		private ListMethodLocks(boolean requiresReadLock, boolean requiresWriteLock)
		{
			this.requiresReadLock = requiresReadLock;
			this.requiresWriteLock = requiresWriteLock;
		}
	}
}