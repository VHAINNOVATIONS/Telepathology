package gov.va.med.asynchproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author VHAISWBECKEC
 *
 * @param <I>
 */
public class AsynchInvocationHandler<I> 
implements InvocationHandler, CompletionService<GenericAsynchResult>
{
	private static final int DEFAULT_THREAD_COUNT = 5;
	private I target;
	private CompletionService<GenericAsynchResult> completionService;
	private AsynchProxyListener listener;

	public AsynchInvocationHandler(I target)
	{
		this(target, null, null);
	}

	public AsynchInvocationHandler(I target, AsynchProxyListener listener)
	{
		this(target, null, listener);
	}

	public AsynchInvocationHandler(I target, Executor executor, AsynchProxyListener listener)
	{
		this.target = target;
		this.completionService = new ExecutorCompletionService<GenericAsynchResult>(
			executor != null ?
			executor :
			Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT, new AsynchInvocationHandlerThreadFactory()) 
		);
		this.listener = listener;
		
		if(listener != null)
		{
			Thread listenerThread = new ListenerNotificationThread(listener);
			listenerThread.setDaemon(true);
			listenerThread.start();
		}
	}

	/**
	 * 
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args) 
	throws Throwable
	{
		Callable<GenericAsynchResult> task = new AsynchTask(method, args);
		completionService.submit(task);

		Class<?> returnType = method.getReturnType();
		
		// return 0, false or null depending on the return type
		// this result is meaningless but always returning null will
		// result in an NPE for primitive types.
		if(returnType == Character.class) return new Character((char)0);
		if(returnType == Short.class) return new Short((short)0);
		if(returnType == Integer.class) return new Integer(0);
		if(returnType == Long.class) return new Long(0);
		if(returnType == Boolean.class) return Boolean.FALSE;
		if(returnType == Float.class) return new Float(0.0);
		if(returnType == Double.class) return new Double(0.0);
		
		return null;
	}

	
	/**
     * @see java.util.concurrent.CompletionService#submit(java.util.concurrent.Callable)
     */
    public Future<GenericAsynchResult> submit(Callable<GenericAsynchResult> task)
    {
	    return completionService.submit(task);

    }

	/**
     * @see java.util.concurrent.CompletionService#submit(java.lang.Runnable, java.lang.Object)
     */
    public Future<GenericAsynchResult> submit(Runnable task, GenericAsynchResult result)
    {
	    return completionService.submit(task, result);

    }

	/**
     * @see java.util.concurrent.CompletionService#poll()
	 */
	public Future<GenericAsynchResult> poll()
	{
		return completionService.poll();
	}

	/**
	 * @see java.util.concurrent.CompletionService#poll(long timeout, TimeUnit unit)
	 */
	public Future<GenericAsynchResult> poll(long timeout, TimeUnit unit) 
	throws InterruptedException
	{
		return completionService.poll(timeout, unit);
	}

	/**
	 * @see java.util.concurrent.CompletionService#take()
	 */
	public Future<GenericAsynchResult> take() 
	throws InterruptedException
	{
		return completionService.take();
	}

	/**
	 * 
	 * @author VHAISWBECKEC
	 *
	 */
	class AsynchInvocationHandlerThreadFactory 
	implements ThreadFactory
	{
		private int threadSerialNumber = 0;
		private String groupName = "AsynchInvocationHandler";
		
		private synchronized int getThreadSerialNumber()
		{
			return threadSerialNumber++;
		}
		
		public Thread newThread(Runnable r)
        {
			Thread thread = new Thread(r, groupName + "-" + getThreadSerialNumber());
			thread.setDaemon(true);
			
	        return thread;
        }
	}
	
	/**
	 * 
	 * @author VHAISWBECKEC
	 * 
	 */
	class AsynchTask 
	implements Callable<GenericAsynchResult>
	{
		Method method;
		Object[] args;

		AsynchTask(Method method, Object[] args)
		{
			this.method = method;
			this.args = args;
		}

		public GenericAsynchResult call() throws Exception
		{
			Object result = method.invoke(target, args);

			return new GenericAsynchResult(method, args, result);
		}
	}

	/**
	 * 
	 * @author VHAISWBECKEC
	 *
	 */
	class ListenerNotificationThread
	extends Thread
	{
		ListenerNotificationThread(AsynchProxyListener listener)
		{
		}

		@Override
        public void run()
        {
			while(true)
			{
				try
                {
	                Future<GenericAsynchResult> result = take();
	                listener.result(result.get());
                } 
				catch (InterruptedException e)
                {
					break;
                } 
				catch (ExecutionException e)
                {
	                e.printStackTrace();
                }
			}
        }
	}
}
