package gov.va.med.configuration;

import javax.naming.Name;

/**
 * These events are sent only by ConfigurationContext
 * instances.  They are used in propagating changes up the
 * context hierarchy and then to notify listeners of each
 * context. 
 */
class ConfigurationContextEvent
{
	enum TYPE{CREATE, READ, UPDATE, DELETE}
	enum OBJECT_TYPE{PROPERTY, CONTEXT}
	
	private final ConfigurationContextEvent.TYPE type;
	private final ConfigurationContextEvent.OBJECT_TYPE objectType;
	private final ConfigurationContext source;
	private final Name name;
	private final Object key;
	private final int index;
	private final Object oldValue;
	private final Object newValue;
	
	// for singular properties, use this constructor
	ConfigurationContextEvent(
		ConfigurationContextEvent.TYPE type, 
		ConfigurationContextEvent.OBJECT_TYPE objectType, 
		ConfigurationContext source, 
		Name name, 
		Object oldValue, 
		Object newValue)
	{
		this.type = type;
		this.objectType = objectType;
		this.source = source;
		this.name = name;
		this.key = null;
		this.index = -1;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	// for Map derived properties, use this constructor
	ConfigurationContextEvent(
		ConfigurationContextEvent.TYPE type, 
		ConfigurationContextEvent.OBJECT_TYPE objectType, 
		ConfigurationContext source, 
		Name name,
		Object key,
		Object oldValue, 
		Object newValue)
	{
		this.type = type;
		this.objectType = objectType;
		this.source = source;
		this.name = name;
		this.key = key;
		this.index = -1;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	// for Collection derived properties, use this constructor
	ConfigurationContextEvent(
		ConfigurationContextEvent.TYPE type, 
		ConfigurationContextEvent.OBJECT_TYPE objectType, 
		ConfigurationContext source, 
		Name name,
		int index,
		Object oldValue, 
		Object newValue)
	{
		this.type = type;
		this.objectType = objectType;
		this.source = source;
		this.name = name;
		this.key = null;
		this.index = index;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public ConfigurationContextEvent.TYPE getType(){return this.type;}
	public ConfigurationContextEvent.OBJECT_TYPE getObjectType(){return this.objectType;}
	public ConfigurationContext getSource(){return this.source;}
	public Name getName(){return this.name;}
	public Object getKey(){return this.key;}
	public int getIndex(){return this.index;}
	public Object getOldValue(){return this.oldValue;}
	public Object getNewValue(){return this.newValue;}
}