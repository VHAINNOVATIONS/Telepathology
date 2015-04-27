/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Jul 6, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
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

package gov.va.med.imaging.tomcat.vistarealm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Special ThreadLocal methods that let us attach and inspect ThreadLocal instances
 * on other threads.
 * 
 * These pretty obviously violate some encapsulation principals and should only be used
 * in carefully selected cases.
 * 
 * @author vhaiswbeckec
 *
 */
public class ThreadLocalWeaselUtility
{
	/**
     * Returns the value in the current thread's copy of this
     * thread-local variable.  If the variable has no value for the
     * current thread, it is first initialized to the value returned
     * by an invocation of the {@link #initialValue} method.
     *
     * @return the current thread's value of this thread-local
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws NoSuchFieldException 
     */
    @SuppressWarnings("unchecked")
	public static <T extends Object> T get(Thread thread, ThreadLocal<T> threadLocal) 
    throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException 
    {
        Object map = getMap(thread);		// ThreadLocalMap
        if (map != null) 
        {
        	// calling getEntry returns a ThreadLocal.Entry instance, which is a
        	// mapping from a ThreadLocal to an Entry, and an Entry is an 
        	// extension of WeakReference.
            // ThreadLocalMap.Entry e = map.getEntry(this);
            Method getEntryMethod = map.getClass().getDeclaredMethod("getEntry", new Class[]{ThreadLocal.class});
            getEntryMethod.setAccessible(true);
            Object entry = getEntryMethod.invoke(map, new Object[]{threadLocal});		// ThreadLocalMap.Entry
            
            if (entry != null)
            {
                Field valueField = entry.getClass().getDeclaredField("value");
                valueField.setAccessible(true);
                return (T)valueField.get(entry);
            }
        }
        return setInitialValue(thread, threadLocal);
    }

    /**
     * Variant of set() to establish initialValue. Used instead
     * of set() in case user has overridden the set() method.
     *
     * @return the initial value
     * @throws NoSuchMethodException 
     * @throws SecurityException 
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws NoSuchFieldException 
     */
    @SuppressWarnings("unchecked")
	private static <T extends Object> T setInitialValue(Thread thread, ThreadLocal<T> threadLocal) 
    throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException 
    {
        T value = null;
        // value = threadLocal.initialValue();
        Method initialValueMethod = ThreadLocal.class.getDeclaredMethod("initialValue", (Class<?>[])null);
        initialValueMethod.setAccessible(true);
        value = (T)initialValueMethod.invoke(threadLocal, (Object[])null);
        
        Object map = getMap(thread);
        if (map != null)
        {
            // map.set(this, value);
        	Method setMethod = map.getClass().getDeclaredMethod("set", new Class<?>[]{ThreadLocal.class, Object.class});
        	setMethod.setAccessible(true);
        	setMethod.invoke(map, new Object[]{threadLocal, value});
        }
        else
        {
            //createMap(t, value);
        	createMap(thread, threadLocal, value);
        }
        return value;
    }

    /**
     * Sets the current thread's copy of this thread-local variable
     * to the specified value.  Most subclasses will have no need to 
     * override this method, relying solely on the {@link #initialValue}
     * method to set the values of thread-locals.
     *
     * @param value the value to be stored in the current thread's copy of
     *        this thread-local.
     * @throws IllegalAccessException 
     * @throws NoSuchFieldException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     * @throws InvocationTargetException 
     */
    public static <T extends Object> void set(Thread thread, ThreadLocal<T> threadLocal, T value) 
    throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException 
    {
        //ThreadLocalMap map = getMap(t);
        Object map = getMap(thread);
        if (map != null)
        {
            //map.set(this, value);
        	Method setMethod = map.getClass().getDeclaredMethod("set", new Class<?>[]{ThreadLocal.class, Object.class});
        	setMethod.setAccessible(true);
        	setMethod.invoke(map, new Object[]{threadLocal, value});
        }
        else
            createMap(thread, threadLocal, value);
    }

    /**
     * Removes the current thread's value for this thread-local
     * variable.  If this thread-local variable is subsequently
     * {@linkplain #get read} by the current thread, its value will be
     * reinitialized by invoking its {@link #initialValue} method,
     * unless its value is {@linkplain #set set} by the current thread
     * in the interim.  This may result in multiple invocations of the
     * <tt>initialValue</tt> method in the current thread.
     * @throws IllegalAccessException 
     * @throws NoSuchFieldException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     * @throws InvocationTargetException 
     *
     * @since 1.5
     */
     public static <T extends Object> void remove(Thread thread, ThreadLocal<T> threadLocal) 
     throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException 
     {
         //ThreadLocalMap m = getMap(Thread.currentThread());
    	 Object map = getMap(thread);
         if( map != null )
         {
             //map.remove(this);
             Method removeMethod = map.getClass().getDeclaredMethod("remove", new Class<?>[]{ThreadLocal.class});
             removeMethod.setAccessible(true);
             removeMethod.invoke(map, threadLocal);
         }
     }
     
     /**
      * Returns a reference to the thread's threadLocals field
      * 
      * @param t
      * @return
     * @throws NoSuchFieldException 
     * @throws SecurityException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
      */
     private static Object getMap(Thread t) 
     throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException 
     {
    	 Field threadLocalField = Thread.class.getDeclaredField("threadLocals");
    	 threadLocalField.setAccessible(true);
    	 return threadLocalField.get(t);
     }

     /**
      * 
      * @param <T>
      * @param thread
      * @param threadLocal
      * @param value
      * @throws NoSuchMethodException
      * @throws IllegalAccessException
      * @throws InvocationTargetException
      */
 	private static <T extends Object> void createMap(Thread thread, ThreadLocal<T> threadLocal, T value) 
 	throws NoSuchMethodException,
 		IllegalAccessException, InvocationTargetException
 	{
 		Method createMapMethod = ThreadLocal.class.getDeclaredMethod("createMap", new Class<?>[]{Thread.class, Object.class});
 		createMapMethod.setAccessible(true);
 		createMapMethod.invoke(threadLocal, new Object[]{thread, value});
 	}
}
