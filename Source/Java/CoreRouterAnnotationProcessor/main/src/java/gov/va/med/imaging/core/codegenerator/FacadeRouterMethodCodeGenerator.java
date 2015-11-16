/**
 * 
 */
package gov.va.med.imaging.core.codegenerator;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.va.med.imaging.core.CommandClassSemantics;
import gov.va.med.imaging.core.CommandFactoryMethodSemantics;
import gov.va.med.imaging.core.RouterMethodSemantics;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;

/**
 * 
 * 
 * @author vhaiswbeckec
 *
 */
public class FacadeRouterMethodCodeGenerator
{
	/**
	 * 
	 */
	private static final String SYNCHRONOUS_ROUTER_METHOD_TEMPLATE = "FacadeRouterSynchronousMethodImpl.ftl";
	private static final String SYNCHRONOUS_ROUTER_TEST_METHOD_TEMPLATE = "FacadeRouterSynchronousMethodTest.ftl";
	private static final String ASYNCHRONOUS_ROUTER_METHOD_TEMPLATE = "FacadeRouterAsynchronousMethodImpl.ftl";
	private static final String ASYNCHRONOUS_ROUTER_TEST_METHOD_TEMPLATE = "FacadeRouterAsynchronousMethodTest.ftl";
	private static final String SYNCHRONOUS_COLLECTION_ROUTER_METHOD_TEMPLATE = "FacadeRouterSynchronousCollectionMethodImpl.ftl";
	private static final String SYNCHRONOUS_COLLECTION_ROUTER_TEST_METHOD_TEMPLATE = "FacadeRouterSynchronousCollectionMethodTest.ftl";
	private static final String SYNCHRONOUS_MAP_COLLECTION_ROUTER_METHOD_TEMPLATE = "FacadeRouterSynchronousMapCollectionMethodImpl.ftl";
	private static final String SYNCHRONOUS_MAP_COLLECTION_ROUTER_TEST_METHOD_TEMPLATE = "FacadeRouterSynchronousMapCollectionMethodTest.ftl";
	
	private static final String SYNCHRONOUS_MOCKROUTER_METHOD_TEMPLATE = "MockFacadeRouterSynchronousMethodImpl.ftl";
	private static final String ASYNCHRONOUS_MOCKROUTER_METHOD_TEMPLATE = "MockFacadeRouterAsynchronousMethodImpl.ftl";
	private static final String SYNCHRONOUS_COLLECTION_MOCKROUTER_METHOD_TEMPLATE = "MockFacadeRouterSynchronousCollectionMethodImpl.ftl";
	private static final String SYNCHRONOUS_MAP_COLLECTION_MOCKROUTER_METHOD_TEMPLATE = "MockFacadeRouterSynchronousMapCollectionMethodImpl.ftl";

	private static final String ASYNCHRONOUS = "asynchronous";
	private static final String IS_CHILD_COMMAND = "isChildCommand";
	private static final String NAME = "name";
	private static final String TYPE = "type";
	private static final String DECLARED_TYPE = "declaredType";
	private static final String COLLECTION_TYPE = "collectionType";
	private static final String COLLECTION_MEMBER_TYPE = "collectionMemberType";
	private static final String MAP_KEY_TYPE = "mapKeyType";
	private static final String MAP_VALUE_TYPE = "mapValueType";
	private static final String COMMANDCLASSNAME = "commandClassName";
	private static final String ROUTER_PARAMETERS = "parameters";
	private static final String COMANDFACTORY_PARAMETERS = "factoryParameters";
	private static final String LISTENER = "listener";
	private static final String DELAY = "delay";
	private static final String PRIORITY = "priority";
	private static final String COMMANDPACKAGE = "commandPackage";
	private static final String PERIODIC = "periodic";
	private static final String PERIODIC_EXECUTION_DELAY = "periodicExecutionDelay";
	
	private final Configuration freemarkerConfig;
	private final Messager messager;
	
	private final Map<String, Object> dataModel = new HashMap<String, Object>();
	
	private String declaredType;
	private RouterMethodSemantics canonicalRouterMethod;
	private CommandClassSemantics canonicalCommandClass;
	private boolean asynchronous;
	private String asynchListenerParameterName;
	private long delay;
	private int priority;
	private boolean isChildCommand;
	private CommandFactoryMethodSemantics canonicalCommandFactoryMethod;
	private List<ParameterDescription> routerMethodParameters;
	private List<ParameterDescription> commandFactoryMethodParameters;
	private final boolean generateMockImplementation;
	private final boolean generateTests;
	private String commandPackage;
	private boolean isPeriodic;
	private int periodicExecutionDelay;
	
	/**
	 * Private constructor to handle the common construction operations.
	 * @param messager
	 */
	private FacadeRouterMethodCodeGenerator(Messager messager, 
			boolean generateMockImplementation, boolean generateTests) 
	{
		super();
		freemarkerConfig = new Configuration();
		
		freemarkerConfig.setTemplateLoader( new ClassLoaderTemplateLoader(this.getClass().getClassLoader(), messager) );
		freemarkerConfig.setObjectWrapper(new DefaultObjectWrapper());

		this.messager = messager;
		this.generateMockImplementation = generateMockImplementation;
		this.generateTests = generateTests;
	}
	
	/**
	 * @param resultType
	 * @param methodName
	 * @param commandClassName
	 * @param commandSimpleClassName 
	 * @param asynchronous
	 */
	public FacadeRouterMethodCodeGenerator(
		Messager messager, 
		String resultType,		// the type of the result as far as the method declaration is concerned
		String resultTypeCollectionType,
		String resultTypeCollectionMemberType,
		boolean generateTypesafeCode,
		String declaredMethodName,		// the name as declared in the interface, may differ from routerMethod if it was mapped
		RouterMethodSemantics canonicalRouterMethod, // semantically-correct method name
		CommandClassSemantics canonicalCommandClass,	// semantically correct command class name
		boolean asynchronous,			// true if the annotation specified the asynchronous attribute equal true
		String asynchListenerParameterName,
		long delay,
		int priority,
		boolean childCommand,
		boolean generateMockImplementation,
		boolean generateTests,
		String commandPackage,
		boolean periodic,
		int periodicExcecutionDelay
	)
	{
		this(messager, generateMockImplementation, generateTests);
		
		setType( resultType );
		setCollectionType(resultTypeCollectionType);
		setCollectionMemberType(resultTypeCollectionMemberType);
		setMapKeyType(null);
		setMapValueType(null);
		setName( declaredMethodName );
		setCanonicalRouterMethod( canonicalRouterMethod );
		setCanonicalCommandClass( canonicalCommandClass );
		setAsynchronous(asynchronous);
		setAsynchListenerParameterName( asynchListenerParameterName );
		setDelay(delay);
		setPriority(priority);
		setChildCommand(childCommand);
		setCommandPackage(commandPackage);
		setPeriodic(periodic);
		setPeriodicExecutionDelay(periodicExcecutionDelay);

		this.routerMethodParameters = new ArrayList<ParameterDescription>();
		this.commandFactoryMethodParameters = new ArrayList<ParameterDescription>();
	}

	public FacadeRouterMethodCodeGenerator(
		Messager messager, 
		String resultType,		// the type of the result as far as the method declaration is concerned
		String resultTypeCollectionType,
		String resultTypeMapKeyType,
		String resultTypeMapValueType,
		boolean generateTypesafeCode,
		String declaredMethodName,		// the name as declared in the interface, may differ from routerMethod if it was mapped
		RouterMethodSemantics canonicalRouterMethod, // semantically-correct method name
		CommandClassSemantics canonicalCommandClass,	// semantically correct command class name
		boolean asynchronous,			// true if the annotation specified the asynchronous attribute equal true
		String asynchListenerParameterName,
		long delay,
		int priority,
		boolean childCommand,
		boolean generateMockImplementation,
		boolean generateTests,
		String commandPackage,
		boolean periodic,
		int periodicExcecutionDelay
	)
	{
		this(messager, generateMockImplementation, generateTests);
		
		setType( resultType );
		setCollectionType(resultTypeCollectionType);
		setCollectionMemberType(null);
		setMapKeyType(resultTypeMapKeyType);
		setMapValueType(resultTypeMapValueType);
		setName( declaredMethodName );
		setCanonicalRouterMethod( canonicalRouterMethod );
		setCanonicalCommandClass( canonicalCommandClass );
		setAsynchronous(asynchronous);
		setAsynchListenerParameterName( asynchListenerParameterName );
		setDelay(delay);
		setPriority(priority);
		setChildCommand(childCommand);
		setCommandPackage(commandPackage);
		setPeriodic(periodic);
		setPeriodicExecutionDelay(periodicExcecutionDelay);

		this.routerMethodParameters = new ArrayList<ParameterDescription>();
		this.commandFactoryMethodParameters = new ArrayList<ParameterDescription>();
	}
	
	protected boolean isGenerateMockImplementation()
	{
		return this.generateMockImplementation;
	}

	protected boolean isGenerateTests() {
		return generateTests;
	}

	/**
	 * 
	 * @param type
	 */
	public void setType(String type)
	{
		this.declaredType = type;
		dataModel.put(DECLARED_TYPE, type);
		dataModel.put(TYPE, gov.va.med.imaging.core.RouterSemanticsUtility.mapPrimitiveToClassType(type));
	}
	public String getType()
	{
		return (String)dataModel.get(TYPE);
	}

	/**
	 * 
	 * @param collectionType
	 */
	public void setCollectionType(String collectionType)
	{
		dataModel.put(COLLECTION_TYPE, collectionType);
	}
	public String getCollectionType()
	{
		return (String)dataModel.get(COLLECTION_TYPE);
	}
	
	/**
	 * 
	 * @param collectionMemberType
	 */
	public void setCollectionMemberType(String collectionMemberType)
	{
		dataModel.put(COLLECTION_MEMBER_TYPE, collectionMemberType);
	}
	public String getCollectionMemberType()
	{
		return (String)dataModel.get(COLLECTION_MEMBER_TYPE);
	}
	
	public void setMapKeyType(String mapKeyType)
	{
		dataModel.put(MAP_KEY_TYPE, mapKeyType);
	}
	public String getMapKeyType()
	{
		return (String)dataModel.get(MAP_KEY_TYPE);
	}
	public void setMapValueType(String mapValueType)
	{
		dataModel.put(MAP_VALUE_TYPE, mapValueType);
	}
	public String getMapValueType()
	{
		return (String)dataModel.get(MAP_VALUE_TYPE);
	}
	
	/**
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		dataModel.put(NAME, name);
	}
	public String getName()
	{
		return (String)dataModel.get(NAME);
	}

	/**
	 * get/set the canonical method name, this is the method name that is compliant
	 * with router semantics, not necessarily the one that the router interface declared
	 */
	void setCanonicalRouterMethod(RouterMethodSemantics canonicalRouterMethod)
	{
		this.canonicalRouterMethod = canonicalRouterMethod;
	}
	protected RouterMethodSemantics getCanonicalRouterMethod()
	{
		return this.canonicalRouterMethod;
	}

	/**
	 * get/set the canonical command class name, this is the class name that is compliant
	 * with router semantics, not necessarily the one that the router interface implied
	 */
	void setCanonicalCommandClass(CommandClassSemantics canonicalCommandClass)
	{
		this.canonicalCommandClass = canonicalCommandClass;
		dataModel.put(COMMANDCLASSNAME, canonicalCommandClass.toString() );
	}
	protected CommandClassSemantics getCanonicalCommandClass()
	{
		return this.canonicalCommandClass;
	}

	/**
	 * get/set whether to generate a synchronous or asynchronous router call
	 */
	void setAsynchronous(boolean asynchronous)
	{
		this.asynchronous = asynchronous;
		dataModel.put(ASYNCHRONOUS, asynchronous ? "true" : "false");
	}
	public boolean isAsynchronous()
	{
		return this.asynchronous;
	}

	/**
	 * 
	 * @return
	 */
	public String getAsynchListenerParameterName()
	{
		return this.asynchListenerParameterName;
	}
	public void setAsynchListenerParameterName(String asynchListenerParameterName)
	{
		this.asynchListenerParameterName = asynchListenerParameterName;
		if(asynchListenerParameterName != null)
			dataModel.put(LISTENER, asynchListenerParameterName);
		else
			dataModel.remove(LISTENER);
	}

	/**
	 * 
	 * @return
	 */
	public long getDelay()
	{
		return this.delay;
	}
	public void setDelay(long delay)
	{
		this.delay = Math.max(0L, delay);
		dataModel.put(DELAY, Long.toString(delay));
	}

	/**
	 * 
	 * @return
	 */
	public int getPriority()
	{
		return this.priority;
	}
	public void setPriority(int priority)
	{
		this.priority = Math.max(1, priority);
		this.priority = Math.min(3, priority);
		dataModel.put(PRIORITY, Integer.toString(priority));
	}

	/**
	 * get/set whether to generate a synchronous child router call
	 */
	void setChildCommand(boolean isChildCommand)
	{
		this.isChildCommand = isChildCommand;
		dataModel.put(IS_CHILD_COMMAND, isChildCommand ? "true" : "false");
	}
	public boolean isChildCommand()
	{
		return this.isChildCommand;
	}
	
	public String getCommandPackage() {
		return commandPackage;
	}

	public void setCommandPackage(String commandPackage) {
		this.commandPackage = commandPackage;
		dataModel.put(COMMANDPACKAGE, commandPackage );
	}

	public boolean isPeriodic()
	{
		return isPeriodic;
	}

	public void setPeriodic(boolean isPeriodic)
	{
		this.isPeriodic = isPeriodic;
		dataModel.put(PERIODIC, isPeriodic ? "true" : "false");
	}

	public int getPeriodicExecutionDelay()
	{
		return periodicExecutionDelay;
	}

	public void setPeriodicExecutionDelay(int periodicExecutionDelay)
	{
		this.periodicExecutionDelay = periodicExecutionDelay;
		dataModel.put(PERIODIC_EXECUTION_DELAY, Integer.toString(periodicExecutionDelay));
	}

	/**
	 * 
	 * @param name
	 * @param type
	 */
	public void addRouterMethodParameter(String name, String type)
	{
		this.routerMethodParameters.add(new ParameterDescription(name, type));
	}
	public List<ParameterDescription> getRouterMethodParameters()
	{
		return this.routerMethodParameters;
	}
	private List<Map<String, String>> getRouterMethodParameterDataModel()
	{
		return getParameterDataModel(getRouterMethodParameters());
	}
	
	/**
	 * 
	 * @param name
	 * @param type
	 */
	public void addCommandFactoryMethodParameter(String name, String type)
	{
		this.commandFactoryMethodParameters.add(new ParameterDescription(name, type));
	}
	public List<ParameterDescription> getCommandFactoryMethodParameters()
	{
		return this.commandFactoryMethodParameters;
	}
	public List<Map<String, String>> getCommandFactoryMethodParametersDataModel()
	{
		return getParameterDataModel(getCommandFactoryMethodParameters());
	}
	
	/*
	 * 
	 */
	private List<Map<String, String>> getParameterDataModel(List<ParameterDescription> parameterDescriptions)
	{
		List<Map<String, String>> parameters = new ArrayList<Map<String, String>>();
		for(FacadeRouterMethodCodeGenerator.ParameterDescription parameterElement : parameterDescriptions )
		{
			Map<String, String> parameter = new HashMap<String, String>();
			parameter.put(TYPE, parameterElement.getType());
			parameter.put(NAME, parameterElement.getName());
			parameters.add( parameter );
		}
		
		return parameters;
	}
	
	/**
	 * Write the complete method definition to the given writer.
	 * 
	 * @param writer
	 * @throws IOException
	 * @throws TemplateException
	 */
	public void generate(Writer writer) 
	throws IOException, TemplateException
	{
		dataModel.put(ROUTER_PARAMETERS, getRouterMethodParameterDataModel());
		dataModel.put(COMANDFACTORY_PARAMETERS, getCommandFactoryMethodParametersDataModel());
		
		String templateName = null;
		
		if(isAsynchronous())	// asynchronous router facade methods do not return anything
			templateName = isGenerateMockImplementation() ? 
				ASYNCHRONOUS_MOCKROUTER_METHOD_TEMPLATE :
					isGenerateTests() ? ASYNCHRONOUS_ROUTER_TEST_METHOD_TEMPLATE :
						ASYNCHRONOUS_ROUTER_METHOD_TEMPLATE;
		
		else		// synchronous router facade methods may return collections
			if(getCollectionType() != null)
				if(getMapKeyType() != null)
					templateName = isGenerateMockImplementation() ? 
						SYNCHRONOUS_MAP_COLLECTION_MOCKROUTER_METHOD_TEMPLATE :
							isGenerateTests() ? SYNCHRONOUS_MAP_COLLECTION_ROUTER_TEST_METHOD_TEMPLATE :
								SYNCHRONOUS_MAP_COLLECTION_ROUTER_METHOD_TEMPLATE;
				else
					templateName = isGenerateMockImplementation() ?
						SYNCHRONOUS_COLLECTION_MOCKROUTER_METHOD_TEMPLATE :
							isGenerateTests() ? SYNCHRONOUS_COLLECTION_ROUTER_TEST_METHOD_TEMPLATE : 
								SYNCHRONOUS_COLLECTION_ROUTER_METHOD_TEMPLATE;
			else
				templateName = isGenerateMockImplementation() ?
					SYNCHRONOUS_MOCKROUTER_METHOD_TEMPLATE :
						isGenerateTests() ? SYNCHRONOUS_ROUTER_TEST_METHOD_TEMPLATE : 
							SYNCHRONOUS_ROUTER_METHOD_TEMPLATE;
		
		Template template = freemarkerConfig.getTemplate(templateName);

		// a debugging tool that dumps the data model to the specified print stream
		//dumpDataModel(dataModel, System.out);
		
		template.process(dataModel, writer);
		writer.flush();
	}
	
	
	/**
	 * @param dataModel2
	 * @param out
	 */
	@SuppressWarnings("unused")
	private void dumpDataModel(Map<String, Object> dataModel, PrintStream out)
	{
		out.println("Begin Data Model =============================================");

		if(dataModel == null)
			out.println("<null>");
		else
		{
			for(Map.Entry<String, Object> entry : dataModel.entrySet())
			{
				String key = entry.getKey();
				Object value = entry.getValue();
				String valueString = value == null ? "<null>" : value.toString();
				
				out.print( key + " => " );
				if(value == null)
					out.println("<null>");
				else
					out.println(
						valueString.length() >= 50 ? 
							valueString.substring(0, 50) + "..." :
							valueString );
			}
		}
		
		out.println("End Data Model ===============================================");
	}

	/**
	 * 
	 * @author vhaiswbeckec
	 *
	 */
	class ParameterDescription
	{
		private final String name;
		private final String type;
		
		/**
		 * @param name
		 * @param className
		 */
		public ParameterDescription(
				String name,
				String type)
		{
			super();
			this.name = name;
			this.type = type;
		}

		public String getName()
		{
			return this.name;
		}

		public String getType()
		{
			return this.type;
		}

		@Override
		public String toString()
		{
			return getType() + " " + getName();
		}
	}
}
