/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Aug 2, 2010
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

import gov.va.med.ClassTranslator;
import java.io.Serializable;
import java.util.*;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingException;
import org.apache.log4j.Logger;

/**
 * This class implements something like a JNDI naming context targeted toward
 * application configuration.  
 * 
 * @author vhaiswbeckec
 *
 */
public class ConfigurationContext
implements Serializable, ContextEventListener
{
	private static final long serialVersionUID = 1L;
	
	// ================================================================================================
	// Instance Implementation
	// ================================================================================================
	private ConfigurationContext parent;
	private String name;
	private final Map<String, Object> propertyMap = new HashMap<String, Object>();
	
	/**
	 * @param name
	 */
	ConfigurationContext(ConfigurationContext parent, String name)
	{
		super();
		this.name = name;
		this.parent = parent;
	}

	/**
	 * @return the parent
	 */
	public ConfigurationContext getParent()
	{
		return this.parent;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Clear and overwrite the contents of this ConfigurationContext
	 * with the values from the given ConfigurationContext.
	 * This is used to load and to restore the ConfigurationContext
	 * from the persisted state.
	 * NOTE: this will restore the references and so may affect the 
	 * structure of the hierarchy.
	 * 
	 * @param ctx
	 */
	void load(ConfigurationContext ctx)
	{
		this.name = ctx.getName();
		this.propertyMap.clear();
		for( String propertyName : ctx.propertyMap.keySet() )
		{
			Object propertyValue = propertyMap.get(propertyName);
			if(propertyValue instanceof ConfigurationContext)
			{
				ConfigurationContext childCtx = new ConfigurationContext(this, propertyName);
				childCtx.load((ConfigurationContext) propertyValue);
				this.propertyMap.put(propertyName, childCtx);
			}
			else
				this.propertyMap.put(propertyName, propertyValue);
		}
	}
	
	/**
	 * Returns an Iterator over the ancestors of this COnfigurationContext.
	 * The iterator starts with the parent of this, and works up to the root.
	 * 
	 * @return
	 */
	Iterator<ConfigurationContext> getAncestorIterator()
	{
		return new Iterator<ConfigurationContext>()
		{
			private ConfigurationContext next = ConfigurationContext.this.getParent();
			
			@Override
			public boolean hasNext()
			{
				return next != null;
			}

			@Override
			public ConfigurationContext next()
			{
				ConfigurationContext current = next;
				next = next.getParent();
				return current;
			}

			@Override
			public void remove(){}
		};
	}
	
	ConfigurationPropertyName getAbsoluteName() 
	throws InvalidNameException
	{
		ConfigurationPropertyName configName = new ConfigurationPropertyName(this.getName());
	
		for(Iterator<ConfigurationContext> iter = this.getAncestorIterator(); iter.hasNext(); )
		{
			ConfigurationContext ancestor = iter.next();
			configName.add(0, ancestor.getName());
		}
		return configName;
	}
	
	/**
	 * @throws ConfigurationStructureException 
	 * @throws InvalidNameException 
	 * @see gov.va.med.configuration.Configuration#bind(java.lang.String, java.lang.Object)
	 */
	<T> void bind(String propertyName, T value) 
	throws NamingException
	{
		if(propertyName == null || propertyName.length() == 0)
			return;
		
		if(! (value instanceof Serializable) )
			throw new NamingException(
				"Properties bound into a ConfigurationContext must implement Serializable, '" + 
				value.getClass().getName() + "' does not.");
		
		ConfigurationPropertyName configurationName = new ConfigurationPropertyName(propertyName);
		bind(configurationName, value);
	}
	
	/**
	 * The bind() implementation that all other bind methods must call
	 * 
	 * @param <T>
	 * @param propertyName
	 * @param value
	 * @throws ConfigurationStructureException
	 */
	<T> void bind(Name propertyName, T value) 
	throws NamingException
	{
		String childName = propertyName.get(0);
		
		// if the compound name is 1 element long then this is the right context to bind to
		if(propertyName.size() == 1)
		{
			Object oldValue = this.propertyMap.get(childName);
			boolean exists = oldValue != null;
			boolean removing = exists && (value == null);

			if(removing)
			{
				this.propertyMap.remove(childName);
			}
			else
			{
				this.propertyMap.put(childName, value);
			}
			
			notifyContextEventListeners(
				new ConfigurationContextEvent(exists ? (removing ? ConfigurationContextEvent.TYPE.DELETE : ConfigurationContextEvent.TYPE.UPDATE) : ConfigurationContextEvent.TYPE.CREATE, 
					ConfigurationContextEvent.OBJECT_TYPE.PROPERTY, 
					this, 
					new ConfigurationPropertyName(childName),
					oldValue,
					value)
			);
		}
		else
		{
			Object child = this.propertyMap.get(childName);
			
			if(child == null)
				throw new NamingException("Invalid path to context, '" + childName + "' does not exist.");
			else if(! (child instanceof ConfigurationContext) )
				throw new NamingException("The path to '" + childName + "' exists but it is not a context.");
			else
			{
				Name descendantName = propertyName.getSuffix(1);
				((ConfigurationContext)child).bind(descendantName, value);
			}
		}
	}

	/**
	 * @throws ConfigurationStructureException 
	 * @throws NamingException 
	 * @see gov.va.med.configuration.Configuration#lookup(java.lang.Class, java.lang.String)
	 */
	<T> T lookup(Class<T> expectedClass, String propertyName) 
	throws NamingException
	{
		if(propertyName == null || propertyName.length() == 0)
			return (T)null;

		ConfigurationPropertyName configurationName = new ConfigurationPropertyName(propertyName);
		return lookup(expectedClass, configurationName);
	}

	/**
	 * 
	 * @param <T>
	 * @param expectedClass
	 * @param propertyName
	 * @return
	 * @throws ConfigurationStructureException 
	 * @throws NamingException 
	 */
	@SuppressWarnings("unchecked")
	<T> T lookup(Class<T> expectedClass, Name propertyName) 
	throws NamingException
	{
		if(propertyName.size() > 1)
		{
			Name contextName = propertyName.getPrefix(propertyName.size()-1);
			Name simplePropertyName = propertyName.getSuffix(propertyName.size()-1);
			ConfigurationContext cc = lookupDescendantContext(contextName, false);	//
			if(cc==null)
				throw new NamingException(
					"Unable to follow path '" + propertyName.toString() + 
					"', context '" + contextName.toString() + "' does not exist."
				);
			return cc.lookup(expectedClass, simplePropertyName);
		}
		else
		{
			// the raw value is the value from the property map, all we know is that it is an Object
			String childName = propertyName.get(0);
			Object rawValue = this.propertyMap.get(childName);
			T value = (T)null;
			
			// null is null, just cast it and return
			if(rawValue != null)
			{
				// if the class that the client is expecting is the same as what we have then just return it
				// the cast is just to satisfy the compiler
				if(expectedClass.isInstance(rawValue))
					value =(T)rawValue;
				else
				{
					// finally, if the raw value and the expected type are not of the same
					// or are derived then translate the raw value into the expected type
					Class<?> rawValueClass = rawValue.getClass();
					ClassTranslator<?, ?> translator = ClassTranslator.create(rawValueClass, expectedClass);
					value = translator == null ? (T)null : (T)( translator.convert(rawValue) );
				}
			}
			notifyContextEventListeners(
				new ConfigurationContextEvent(ConfigurationContextEvent.TYPE.READ, ConfigurationContextEvent.OBJECT_TYPE.PROPERTY, this, new ConfigurationPropertyName(childName),
					value, null)
			);
			
			return value;
		}
	}

	/**
	 * Given a Name, recursively follow the path defined by the name to the last
	 * simple name and then return the bound value, if it is a 
	 * ConfigurationContext
	 * 
	 * @param name
	 * @return
	 * @throws NamingException 
	 */
	private ConfigurationContext lookupDescendantContext(Name name, boolean createAsNeeded) 
	throws NamingException
	{
		if(name.size() == 0)
			return null;

		String childBoundName = name.get(0);
		Object child = this.propertyMap.get(childBoundName);

		if(child == null && createAsNeeded)
		{
			child = new ConfigurationContext(this, childBoundName);
			this.propertyMap.put(childBoundName, child);
			notifyContextEventListeners(
				new ConfigurationContextEvent(ConfigurationContextEvent.TYPE.CREATE, ConfigurationContextEvent.OBJECT_TYPE.CONTEXT, this, new ConfigurationPropertyName(childBoundName),
					null, child)
			);
		}

		if(child != null)
		{
			if(child instanceof ConfigurationContext)
			{
				// normal, happy-path, recursive escape clause
				if(name.size() == 1)
					return (ConfigurationContext)child;
				else
					return ((ConfigurationContext)child).lookupDescendantContext(name.getSuffix(1), createAsNeeded);
			}
			else
				throw new NamingException(
					"The bound name '" + childBoundName + 
					"' is being referenced and a ConfigurationContext and it is of type '" + child.getClass().getName() + "'");
		}
		else
			return null;
	}
	
	/**
	 * 
	 * @param name
	 * @param lastElementIsProperty - true if the last element should be interpreted as a property and not a subcontext
	 * @return
	 * @throws NamingException
	 */
	public ConfigurationContext createSubcontext(String name, boolean lastElementIsProperty) 
	throws NamingException
	{
		ConfigurationPropertyName configurationName = new ConfigurationPropertyName(name);
		return createSubcontext(configurationName, lastElementIsProperty);
	}

	/**
	 * 
	 * @param name
	 * @param lastElementIsProperty - true if the last element should be interpreted as a property and not a subcontext
	 * @return
	 * @throws NamingException
	 */
	public ConfigurationContext createSubcontext(Name name, boolean lastElementIsProperty) 
	throws NamingException
	{
		if(lastElementIsProperty)
			name = name.getPrefix(name.size()-1);
		return lookupDescendantContext(name, true);
	}

	/**
	 * 
	 * @param name
	 * @return
	 * @throws NamingException
	 */
	public ConfigurationList createConfigurationList(String name, boolean lastElementIsProperty) 
	throws NamingException
	{
		ConfigurationPropertyName propertyName = new ConfigurationPropertyName(name);
		return createConfigurationList(propertyName, lastElementIsProperty);
	}
	
	/**
	 * @param propertyName
	 * @return
	 * @throws NamingException 
	 */
	public ConfigurationList createConfigurationList(Name name, boolean lastElementIsProperty) 
	throws NamingException
	{
		if( name.size() == 1)
		{
			ConfigurationList configList = new ConfigurationList( this, name );
			this.bind(name, configList);
			
			return configList;
		}
		else
		{
			 ConfigurationContext ctx = lookupDescendantContext(name, lastElementIsProperty);
			 Name propertyName = name.getSuffix(name.size()-1);
			 return ctx.createConfigurationList(propertyName, lastElementIsProperty);
		}
	}
	
	public ConfigurationMap createConfigurationMap(String name, boolean lastElementIsProperty) 
	throws NamingException
	{
		ConfigurationPropertyName propertyName = new ConfigurationPropertyName(name);
		return createConfigurationMap(propertyName, lastElementIsProperty);
	}
	/**
	 * 
	 * @param name
	 * @return
	 * @throws NamingException
	 */
	public ConfigurationMap createConfigurationMap(Name name, boolean lastElementIsProperty) 
	throws NamingException
	{
		if( name.size() == 1)
		{
			ConfigurationMap configList = new ConfigurationMap( this, name );
			this.bind(name, configList);
			
			return configList;
		}
		else
		{
			 ConfigurationContext ctx = lookupDescendantContext(name, lastElementIsProperty);
			 Name propertyName = name.getSuffix(name.size()-1);
			 return ctx.createConfigurationMap(propertyName, lastElementIsProperty);
		}
	}
	
	public void unbind(String name) 
	throws NamingException
	{
		Name propertyName = new ConfigurationPropertyName(name);
		
		unbind(propertyName);
	}
	
	public void unbind(Name name) 
	throws NamingException
	{
		if( name.size() == 1)
		{
			this.propertyMap.remove(name);
		}
		else
		{
			 ConfigurationContext ctx = lookupDescendantContext(name, true);
			 Name descendantName = name.getSuffix(name.size()-1);
			 ctx.unbind(descendantName);
		}
	}
	
	// =======================================================================
	//
	// =======================================================================
	private transient List<ContextEventListener> contextEventListeners = new ArrayList<ContextEventListener>();
	
	/**
	 * Receive a notification of a change in a child context.
	 * Propagate a new event up to our ancestors, changing the name of
	 * the property modified to reflect our context.
	 */
	@Override
	public void contextEventNotification(ConfigurationContextEvent sourceEvent)
	{
		Name contextName;
		try
		{
			contextName = sourceEvent.getName().add(sourceEvent.getSource().getName());
			// rebuild the notification with ourselves as the source
			ConfigurationContextEvent event = new ConfigurationContextEvent(
				sourceEvent.getType(), 
				sourceEvent.getObjectType(),
				this, 
				contextName,
				sourceEvent.getOldValue(),
				sourceEvent.getNewValue());
			notifyContextEventListeners(event);
		}
		catch (InvalidNameException x)
		{
			Logger.getLogger(Configuration.class).error("Internal error building Name when propogating context change event.");
		}
	}
	
	/*
	 * An event from a child collection property.  List, Set and Map properties are
	 * handled slightly special because they generate the context events,
	 * pass them to their parent (here) which passes them up the hierarchy.
	 */
	void childCollectionEventNotification(ConfigurationContextEvent sourceEvent)
	{
		try
		{
			ConfigurationContextEvent event = null;
			
			// rebuild the notification with ourselves as the source
			event = new ConfigurationContextEvent(
				sourceEvent.getType(), 
				sourceEvent.getObjectType(),
				this, 
				this.getAbsoluteName().addAll( sourceEvent.getName().getSuffix(sourceEvent.getName().size()-1) ),
				sourceEvent.getOldValue(),
				sourceEvent.getNewValue());
			notifyContextEventListeners(event);
		}
		catch (InvalidNameException x)
		{
			Logger.getLogger(Configuration.class).error("Internal error building Name when propogating child change event.");
		}
		
	}
	
	/**
	 * 
	 * @param sourceEvent
	 */
	void passAlong(ConfigurationContextEvent sourceEvent)
	{
		Name contextName;
		try
		{
			contextName = sourceEvent.getName().add(sourceEvent.getSource().getName());
			// rebuild the notification with ourselves as the source
			ConfigurationContextEvent event = new ConfigurationContextEvent(
				sourceEvent.getType(), 
				sourceEvent.getObjectType(),
				this, 
				contextName,
				sourceEvent.getOldValue(),
				sourceEvent.getNewValue());
			notifyContextEventListeners(event);
		}
		catch (InvalidNameException x)
		{
			Logger.getLogger(Configuration.class).error("Internal error building Name when propogating context change event.");
		}
	}
	
	/**
	 * @param event
	 */
	public void notifyContextEventListeners(ConfigurationContextEvent event)
	{
		// the parent is always notified
		if(getParent() != null)
			getParent().notifyContextEventListeners(event);
		
		// notify any other listeners that have been added
		for(ContextEventListener listener : contextEventListeners)
			listener.contextEventNotification(event);
	}
	
	public void addContextEventListener(ContextEventListener listener)
	{
		contextEventListeners.add(listener);
	}
	
	public void removeContextEventListener(ContextEventListener listener)
	{
		contextEventListeners.remove(listener);
	}
}
