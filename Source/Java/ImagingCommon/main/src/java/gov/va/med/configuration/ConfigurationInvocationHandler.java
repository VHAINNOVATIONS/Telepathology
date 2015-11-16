/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Jul 28, 2010
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

package gov.va.med.configuration;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import javax.naming.ConfigurationException;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import org.apache.log4j.Logger;

/**
 * This class is:
 * 1.) a proxy invocation handler that contains the child contexts and the properties
 * stored in a configuration
 * 2.) a factory to create instances of Configuration derived interface realizations,
 * which are instances of this class (as proxy invocation handlers)
 * 
 * This class does not implement the complete interface for a JNDI Context but it does follow
 * the naming conventions and semantics.  Future work may include a full implementation if
 * that is thought to be beneficial.
 * 
 * @author vhaiswbeckec
 *
 */
public class ConfigurationInvocationHandler
implements Configuration, ContextEventListener, InvocationHandler, Serializable
{
	private static final String IS_ACCESSOR_PREFIX = "is"; //$NON-NLS-1$
	private static final String GET_ACCESSOR_PREFIX = "get"; //$NON-NLS-1$
	private static final String SET_ACCESSOR_PREFIX = "set"; //$NON-NLS-1$
	private static final String DELETE_PREFIX = "delete"; //$NON-NLS-1$
	private static final String CLEAR_PREFIX = "clear"; //$NON-NLS-1$
	private static final String COPY_PREFIX = "copy"; //$NON-NLS-1$

	private static final long serialVersionUID = 1L;
	
	public final static String ROOT_CONTEXT = 
		Messages.getString("Configuration.ROOT_CONTEXT_NAME"); //$NON-NLS-1$
	public final static String CONFIGURATION_STORAGE_IMPLEMENTATION = 
		Messages.getString("Configuration.STORAGE_IMPLEMENTATION");  //$NON-NLS-1$
	public final static String CONFIGURATION_STORAGE_ROOT_URL = 
		Messages.getString("ConfigurationSTORAGE_ROOT_URL");  //$NON-NLS-1$
	
	// ====================================================================================
	// 
	// ====================================================================================
	private static Configuration rootContext;
	private static ClassLoader loader = ConfigurationInvocationHandler.class.getClassLoader();
	
	// The childContextProxyMap is a cache of references to already created proxy invocation
	// handlers.
	// The name is always the absolute name, from the "ROOT" context
	private final static Map<ConfigurationPropertyName, Configuration> contextProxyMap = 
		new HashMap<ConfigurationPropertyName, Configuration>();

	/**
	 * 
	 * @return
	 * @throws NamingException 
	 */
	public static synchronized Configuration getRootConfiguration()
	{
		if(rootContext == null)
		{
			ConfigurationContext rootConfigurationContext = new ConfigurationContext(null, ROOT_CONTEXT);
			rootContext = createContextProxy(rootConfigurationContext, Configuration.class); 
			
			try
			{
				rootContext.load();
			}
			catch (Throwable t)
			{
				if(t.getCause() != null && t.getCause() instanceof NamingException)
					Logger.getLogger(ConfigurationInvocationHandler.class).warn(t);
				else
					Logger.getLogger(ConfigurationInvocationHandler.class).error(t);
			}
		}
		
		return rootContext;
	}
	
	/**
	 * A static method to wrap the creation of new invocation handlers into
	 * a method that manages the casting.
	 * 
	 * @param <C>
	 * @param expectedClass
	 * @return
	 */
	private static <T extends Configuration> T createContextProxy(
		ConfigurationContext configurationContext, 
		Class<T> expectedClass)
	{
		ConfigurationInvocationHandler handler = new ConfigurationInvocationHandler(configurationContext);
		return expectedClass.cast( Proxy.newProxyInstance(loader, new Class<?>[]{expectedClass}, handler) );
	}

	/**
	 * 
	 * @param <T>
	 * @param expectedClass
	 * @param value
	 * @return
	 * @throws InvalidNameException
	 */
	@SuppressWarnings("unchecked")
	private static <T extends Configuration> T getOrCreateConfiguration(Class<T> expectedClass, ConfigurationContext context)
	throws InvalidNameException
	{
		synchronized(ConfigurationInvocationHandler.contextProxyMap)
		{
			Class<Configuration> expectedConfigurationClass = (Class<Configuration>) expectedClass;
			
			// if value is a ConfigurationContext then we return a reference
			// to a dynamic proxy, not to the ConfigurationContext itself.
			// The dynamic proxies are kept in a static map so that there is
			// always one representation of each configuration context.
			ConfigurationPropertyName contextName = context.getAbsoluteName();
			
			// if the child context has a proxy created for it already then it will be in
			// the childContextProxyMap
			Configuration config = ConfigurationInvocationHandler.contextProxyMap.get(contextName);
			
			// the child context does not have a proxy created for it yet, create one of the 
			// expected type and put it in the childContextProxyMap
			if(config == null)
			{
				config = createContextProxy(context, expectedConfigurationClass);
				ConfigurationInvocationHandler.contextProxyMap.put(contextName, config);
			}
			
			return (T)config;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private static ConfigurationStorage createStorageImplementation()
	throws NamingException
	{
		String className = null;
		
		try
		{
			// if the full classpath is specified (i.e. ther is a dot in the name) then just use that as is
			// otherwise prepend our package name 
			className = ConfigurationInvocationHandler.CONFIGURATION_STORAGE_IMPLEMENTATION.indexOf('.') >= 0 ?
				ConfigurationInvocationHandler.CONFIGURATION_STORAGE_IMPLEMENTATION :
				ConfigurationInvocationHandler.class.getPackage().getName() + "." +
					ConfigurationInvocationHandler.CONFIGURATION_STORAGE_IMPLEMENTATION;
			Class<?> storageImplementation = Class.forName(className);
			return (ConfigurationStorage)storageImplementation.newInstance();
		}
		catch (ClassNotFoundException x)
		{
			throw new ConfigurationException("The storage class '" + className + "' was not found, unable to load or store configuration.");
		}
		catch (InstantiationException x)
		{
			throw new ConfigurationException("The storage class '" + className + "' was found but did not allow instantiation with the default constructor, unable to load or store configuration.");
		}
		catch (IllegalAccessException x)
		{
			throw new ConfigurationException("The storage class '" + className + "' was found but not accessible, unable to load or store configuration.");
		}
	}
	
	// ================================================================================================
	// Instance Code
	// ================================================================================================
	
	// The list of observers, transient because the observers on the list won't be guaranteed to be available
	// post serialization
	private final transient List<ConfigurationObserver> configurationObservers = new ArrayList<ConfigurationObserver>();

	// The properties and sub-context stored in this context
	private final ConfigurationContext propertyMap;
	
	/**
	 * @param name
	 */
	private ConfigurationInvocationHandler(ConfigurationContext configurationContext)
	{
		super();
		this.propertyMap = configurationContext;
		this.propertyMap.addContextEventListener(this);
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return getPropertyMap().getName();
	}

	/**
	 * @return the propertyMap
	 */
	public ConfigurationContext getPropertyMap()
	{
		return this.propertyMap;
	}

	/**
	 * @param expectedClass
	 * @param boundNamePath
	 * @return
	 * @throws ConfigurationStructureException
	 */
	@SuppressWarnings("unchecked")
	public <T extends Object> T get(Class<T> expectedClass, String name) 
	throws NamingException
	{
		// the class that is stored in the ConfigurationContext
		// is not the same if the expected class is a Configuration derived class
		Class<?> storedClass = Configuration.class.isAssignableFrom(expectedClass) ?
			ConfigurationContext.class :
			expectedClass;
		
		Object value = this.propertyMap.lookup(storedClass, name);
		
		if(Configuration.class.isAssignableFrom(expectedClass))
			value = ConfigurationInvocationHandler.getOrCreateConfiguration(
				(Class<? extends Configuration>)expectedClass, (ConfigurationContext)value
			);
		
		return (T)value;
	}	
	
	/**
	 * @see gov.va.med.configuration.Configuration#bind(java.lang.String, java.lang.Object)
	 */
	public void put(String name, Object value) 
	throws NamingException
	{
		this.propertyMap.bind(name, value);
	}

	public Object delete(String name) 
	throws NamingException
	{
		Object oldValue = this.propertyMap.lookup(Object.class, name);
		this.propertyMap.unbind(name);
		return oldValue;
	}	
	
	/**
	 * @see gov.va.med.configuration.Configuration#createContext(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T extends Configuration> T createContext(Class<T> expectedClass, String name) 
	throws NamingException
	{
		ConfigurationContext subcontext = getPropertyMap().createSubcontext(name, false);
		return ConfigurationInvocationHandler.getOrCreateConfiguration(expectedClass, subcontext);
	}

	// ======================================================================================================
	// java.lang.reflect.InvocationHandler starts here
	// ======================================================================================================
	
	/**
	 * 
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) 
	throws Throwable
	{
		// if the method is declared in the Configuration interface, rather than a derivation
		// of the Configuration interface then we know that this class implements it because
		// this class implements Configuration, so just pass it through
		try
		{
			// Method configurationMethod = Configuration.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
			Method realizationMethod = this.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
			return realizationMethod.invoke(this, args);
		}
		catch (NoSuchMethodException nsmX)
		{
			// The exception doesn't mean anything except that the method is not declared in Configuration
			// so we'll have to handle it by looking at the method name, types, etc ...
			// If the method can be turned into a canonical accessor (i.e. is a Configuration.setXXX(), 
			// Configuration.getXXX(), or Configuration.isXXX(), etc ...)
			// then it is a property access, otherwise it is an error
			CanonicalAccessor accessor = CanonicalAccessor.create(method);
			if(accessor != null)
				return handleAccessor(accessor, method, args);
		}
		
		return null;
	}

	/**
	 * @param method
	 * @throws ConfigurationStructureException 
	 * @throws IllegalSubConfigurationAccessorException 
	 */
	private Object handleAccessor(CanonicalAccessor accessor, Method method, Object[] args) 
	throws NamingException
	{
		Object result = null;
		
		if(CanonicalAccessor.ACCESSTYPE.SET == accessor.getAccessType())
			handleSetAccessor(accessor, method, args);
		
		else if(CanonicalAccessor.ACCESSTYPE.GET == accessor.getAccessType())
			result = handleGetAccessor(accessor, method, args);
		
		else if(CanonicalAccessor.ACCESSTYPE.DELETE == accessor.getAccessType())
			handleDeleteAccessor(accessor, method);
		
		else if(CanonicalAccessor.ACCESSTYPE.CLEAR == accessor.getAccessType())
			handleClearAccessor(accessor, method);
		
		else if(CanonicalAccessor.ACCESSTYPE.COPY == accessor.getAccessType())
			handleCopyAccessor(accessor, method, args);
		
		return result;
	}

	/**
	 * Move from the semantics of the setXXX call to the semantics of a 
	 * naming context lookup() call.
	 * 
	 * @param propertyName
	 * @param method
	 * @param args
	 * @return
	 * @throws IllegalSubConfigurationAccessorException
	 * @throws ConfigurationStructureException 
	 */
	private void handleSetAccessor(CanonicalAccessor accessor, Method method, Object[] args)
	throws NamingException
	{
		// the only arg in a setter is the value to set the property to
		Class<?> propertyType = method.getParameterTypes()[0];
		
		// disallow setting of sub contexts  
		if(Configuration.class.isAssignableFrom(propertyType) || ConfigurationContext.class.isAssignableFrom(propertyType))
			throw new NamingException("Setting configuration sub-context '" + accessor.getPropertyName() + "' is explicitly disallowed."); //$NON-NLS-1$ //$NON-NLS-2$
		
		// try to get the old value but if it fails then it wasn't set before, just continue
		Object oldValue = null;
		try{oldValue = get(propertyType, accessor.getPropertyName());}
		catch (NamingException nX){/* ignore the error, just not bound yet */}
		
		// disallow overwriting a context with some other type
		if(Configuration.class.isInstance(oldValue) || ConfigurationContext.class.isInstance(oldValue))
			throw new NamingException("Over-writing configuration sub-context '" + accessor.getPropertyName() + "' is explicitly disallowed."); //$NON-NLS-1$ //$NON-NLS-2$

		if(CanonicalAccessor.SUBJECTTYPE.COLLECTION == accessor.getSubjectType())
		{
			if( oldValue != null  )
				if(oldValue instanceof ConfigurationList)
					((ConfigurationList)oldValue).add( args[0] );
				else
					throw new NamingException("Attempt to set a Collection member on a bound instance that is not a Collection");
			else
			{
				ConfigurationList list = createConfigurationList(accessor.getPropertyName());
				if(args.length > 1)
					list.add( ((Integer)args[1]).intValue(), args[0]);
				else
					list.add(args[0]);
			}
		}
		else if(CanonicalAccessor.SUBJECTTYPE.MAP == accessor.getSubjectType())
		{
			if( oldValue != null  )
				if(oldValue instanceof ConfigurationMap)
					((ConfigurationMap)oldValue).put( args[1], args[0] );
				else
					throw new NamingException("Attempt to set a Map member on a bound instance that is not a Map");
			else
			{
				ConfigurationMap map = createConfigurationMap(accessor.getPropertyName());
				map.put(args[0], args[1]);
			}
		}
		else if(CanonicalAccessor.SUBJECTTYPE.SINGULAR == accessor.getSubjectType())
		{
			createSubcontextPath(accessor.getPropertyName());
			put(accessor.getPropertyName(), args[0]);
		}
		
		return;
	}

	/**
	 * Move from the semantics of the getXXX or isXXX call to the semantics of a 
	 * naming context lookup() call.
	 * 
	 * @param propertyName
	 * @param method
	 * @return
	 * @throws ConfigurationStructureException 
	 */
	@SuppressWarnings("unchecked")
	private Object handleGetAccessor(CanonicalAccessor accessor, Method method, Object[] args) 
	throws NamingException
	{
		// the class expected by the calling client
		Class<?> expectedClass = method.getReturnType();
		Object value = null;

		if(CanonicalAccessor.SUBJECTTYPE.COLLECTION == accessor.getSubjectType())
		{
			ConfigurationList collection = this.get(ConfigurationList.class, accessor.getPropertyName());
			if(collection != null)
				value = collection.get( ((Integer)args[0]).intValue() );
		}
		
		else if(CanonicalAccessor.SUBJECTTYPE.MAP == accessor.getSubjectType())
		{
			ConfigurationMap map = this.get(ConfigurationMap.class, accessor.getPropertyName());
			if(map != null)
				value = map.get( args[0] );
		}
		
		// handles both the regular property accessors and the accessors getting child
		// contexts
		else if(CanonicalAccessor.SUBJECTTYPE.SINGULAR == accessor.getSubjectType())
		{
			// the class that is stored in the ConfigurationContext
			// is not the same if the expected class is a Configuration derived class
			Class<?> storedClass = Configuration.class.isAssignableFrom(expectedClass) ?
				ConfigurationContext.class :
				expectedClass;
			
			value = this.get(storedClass, accessor.getPropertyName());
			if(Configuration.class.isAssignableFrom(expectedClass))
				value = ConfigurationInvocationHandler.getOrCreateConfiguration(
					(Class<? extends Configuration>)expectedClass, (ConfigurationContext)value
				);
		}
		
		return value;
	}

	/**
	 * @param accessor 
	 * @param method  
	 */
	private void handleDeleteAccessor(CanonicalAccessor accessor, Method method)
	throws NamingException
	{
		Object oldValue = null;

		if(CanonicalAccessor.SUBJECTTYPE.COLLECTION == accessor.getSubjectType())
		{
			oldValue = this.get(ConfigurationList.class, accessor.getPropertyName());
			this.delete(accessor.getPropertyName());
		}
		
		else if(CanonicalAccessor.SUBJECTTYPE.MAP == accessor.getSubjectType())
		{
			oldValue = this.get(ConfigurationMap.class, accessor.getPropertyName());
			this.delete(accessor.getPropertyName());
		}
		
		// handles both the regular property accessors and the accessors getting child
		// contexts
		else if(CanonicalAccessor.SUBJECTTYPE.SINGULAR == accessor.getSubjectType())
		{
			oldValue = this.get(Object.class, accessor.getPropertyName());
			this.delete(accessor.getPropertyName());
		}
	}
	
	/**
	 * @param accessor 
	 * @param method  
	 */
	private void handleClearAccessor(CanonicalAccessor accessor, Method method)
	throws NamingException
	{
		if(CanonicalAccessor.SUBJECTTYPE.COLLECTION == accessor.getSubjectType())
		{
			ConfigurationList list = this.get(ConfigurationList.class, accessor.getPropertyName());
			list.clear();
		}
		
		else if(CanonicalAccessor.SUBJECTTYPE.MAP == accessor.getSubjectType())
		{
			ConfigurationMap map = this.get(ConfigurationMap.class, accessor.getPropertyName());
			map.clear();
		}
	}

	/**
	 * @param accessor 
	 * @param method  
	 * @param args  
	 */
	private Object handleCopyAccessor(CanonicalAccessor accessor, Method method, Object[] args)
	throws NamingException
	{
		return null;
	}
	
	// ====================================================================================
	// Helper Methods 
	// ====================================================================================
	/**
	 * Creates the subcontext instances in the given name.
	 * This does not create the property itself.
	 * 
	 * @param propertyName
	 * @throws NamingException 
	 */
	private ConfigurationContext createSubcontextPath(String propertyName) 
	throws NamingException
	{
		return getPropertyMap().createSubcontext(propertyName, true);
	}

	/**
	 * @param propertyName
	 * @return
	 * @throws NamingException 
	 */
	private ConfigurationList createConfigurationList(String propertyName) 
	throws NamingException
	{
		return getPropertyMap().createConfigurationList(propertyName, true);
	}

	/**
	 * @param propertyName
	 * @return
	 * @throws NamingException 
	 */
	private ConfigurationMap createConfigurationMap(String propertyName) 
	throws NamingException
	{
		return getPropertyMap().createConfigurationMap(propertyName, true);
	}

	// ====================================================================================
	// Standard Persistence Method Implementation 
	// ====================================================================================
	
	/**
	 * @throws NamingException 
	 * 
	 */
	@Override
	public void load() 
	throws NamingException
	{
		ConfigurationStorage storage = createStorageImplementation();
		String urlString = ConfigurationInvocationHandler.CONFIGURATION_STORAGE_ROOT_URL + "/" + getName();
		URL location;
		try
		{
			location = new URL(urlString);
		}
		catch (MalformedURLException x)
		{
			throw new ConfigurationException("Attempting to build the URL of the persistent configuration failed, URL is '" + urlString + "'.");
		}
		ConfigurationContext ctx = storage.load(location);
		this.propertyMap.load(ctx);
		
		notifyObservers( ConfigurationEvent.createLoadEvent(getName()) );
	}

	/**
	 * @throws NamingException 
	 * 
	 */
	@Override
	public void store() 
	throws NamingException
	{
		ConfigurationStorage storage = createStorageImplementation();
		String urlString = ConfigurationInvocationHandler.CONFIGURATION_STORAGE_ROOT_URL + "/" + getName();
		URL location;
		try
		{
			location = new URL(urlString);
		}
		catch (MalformedURLException x)
		{
			throw new ConfigurationException("Attempting to build the URL of the persistent configuration failed, URL is '" + urlString + "'.");
		}
		storage.store(location, this.getPropertyMap());
		
		notifyObservers( ConfigurationEvent.createStoreEvent(getName()) );
	}

	// ====================================================================================
	// Implementation of Notifications from the ConfigurationContext
	// ====================================================================================
	/* (non-Javadoc)
	 * @see gov.va.med.configuration.ContextEventListener#contextEventNotification(gov.va.med.configuration.ConfigurationContext.Event)
	 */
	@Override
	public void contextEventNotification(ConfigurationContextEvent event)
	{
		ConfigurationEvent.TYPE eventType = 
			ConfigurationContextEvent.TYPE.CREATE == event.getType() ? ConfigurationEvent.TYPE.CREATE :
			ConfigurationContextEvent.TYPE.READ == event.getType() ? ConfigurationEvent.TYPE.READ :
			ConfigurationContextEvent.TYPE.UPDATE == event.getType() ? ConfigurationEvent.TYPE.UPDATE :
			ConfigurationContextEvent.TYPE.DELETE == event.getType() ? ConfigurationEvent.TYPE.DELETE : null;
		
				
		ConfigurationEvent configEvent = new ConfigurationEvent(eventType, event.getName().toString(), event.getOldValue(), event.getNewValue() );
		notifyObservers(configEvent);
	}

	// ====================================================================================
	// Implementation of Observers 
	// ====================================================================================
	
	/**
	 * @param configurationObserver
	 */
	@Override
	public void addObserver(ConfigurationObserver configurationObserver)
	{
		this.configurationObservers.add(configurationObserver);
	}

	/**
	 * @param configurationObserver
	 */
	@Override
	public void deleteObserver(ConfigurationObserver configurationObserver)
	{
		this.configurationObservers.remove(configurationObserver);
	}

	private void notifyObservers(ConfigurationEvent event)
	{
		for(ConfigurationObserver configurationObserver : configurationObservers)
			configurationObserver.configurationEvent(event);
	}
	
	// ====================================================================================
	// Inner Class to represent accessor methods 
	// ====================================================================================
	static class CanonicalAccessor
	{
		enum ACCESSTYPE {GET, SET, DELETE, CLEAR, COPY}
		enum SUBJECTTYPE {SINGULAR, MAP, COLLECTION}
		
		private final ACCESSTYPE accessType;
		private final SUBJECTTYPE subjectType;
		private final String propertyName;
		
		static CanonicalAccessor create(Method method)
		{
			String methodName = method.getName();
			int argCount = method.getParameterTypes().length;
			
			// the property name is either the name of the accessor without the "get", "set", or "is"
			// or is the value of the ConfigurationPropertyName annotation if one exists
			// if the accessor is annotated then use the property name from the annotation
			CanonicalAccessor.ACCESSTYPE accessType = null;
			CanonicalAccessor.SUBJECTTYPE subjectType = null;
			String propertyName = null;
			
			if( methodName.startsWith(GET_ACCESSOR_PREFIX) )
			{
				propertyName = methodName.substring(GET_ACCESSOR_PREFIX.length());
				accessType = CanonicalAccessor.ACCESSTYPE.GET;
				subjectType = argCount == 0 ? SUBJECTTYPE.SINGULAR : 
					argCount == 1 ? 
						(int.class == method.getParameterTypes()[0] ? SUBJECTTYPE.COLLECTION :
							(!method.getParameterTypes()[0].isPrimitive() ? SUBJECTTYPE.MAP : null) ) : 
						null;
			}
			else if(methodName.startsWith(IS_ACCESSOR_PREFIX))
			{
				propertyName = methodName.substring(IS_ACCESSOR_PREFIX.length());
				accessType = CanonicalAccessor.ACCESSTYPE.GET;
				subjectType = argCount == 0 ? SUBJECTTYPE.SINGULAR : null; 
			}
			else if(methodName.startsWith(SET_ACCESSOR_PREFIX))
			{
				propertyName = methodName.substring(SET_ACCESSOR_PREFIX.length());
				accessType = CanonicalAccessor.ACCESSTYPE.SET;
				subjectType = argCount == 1 ? SUBJECTTYPE.SINGULAR : 
					argCount == 2 ? 
						(int.class == method.getParameterTypes()[0] ? SUBJECTTYPE.COLLECTION :
							(!method.getParameterTypes()[0].isPrimitive() ? SUBJECTTYPE.MAP : null) ) : 
						null;
			}
			else if(methodName.startsWith(CLEAR_PREFIX))
			{
				propertyName = methodName.substring(CLEAR_PREFIX.length());
				accessType = CanonicalAccessor.ACCESSTYPE.CLEAR;
				subjectType = argCount == 0 ? SUBJECTTYPE.SINGULAR : null; 
			}
			else if(methodName.startsWith(DELETE_PREFIX))
			{
				propertyName = methodName.substring(DELETE_PREFIX.length());
				accessType = CanonicalAccessor.ACCESSTYPE.DELETE;
				subjectType = argCount == 0 ? SUBJECTTYPE.SINGULAR : null; 
			}
			else if(methodName.startsWith(COPY_PREFIX))
			{
				propertyName = methodName.substring(COPY_PREFIX.length());
				accessType = CanonicalAccessor.ACCESSTYPE.COPY;
				subjectType = argCount == 1 ? SUBJECTTYPE.SINGULAR : null; 
			}
			
			ConfigurationProperty configurationPropertyName = method.getAnnotation(ConfigurationProperty.class);
			propertyName = configurationPropertyName == null ? propertyName : configurationPropertyName.value();

			// use subjectType to determine if the canonical accessor can be created because
			// determining it checks method arguments
			return subjectType == null ? null : new CanonicalAccessor(subjectType, accessType, propertyName);
		}

		private CanonicalAccessor(SUBJECTTYPE subjectType, ACCESSTYPE accessType, String propertyName)
		{
			super();
			this.subjectType = subjectType;
			this.accessType = accessType;
			this.propertyName = propertyName;
		}
		
		public SUBJECTTYPE getSubjectType(){return this.subjectType;}
		public ACCESSTYPE getAccessType(){return this.accessType;}
		public String getPropertyName(){return this.propertyName;}
	}
}
