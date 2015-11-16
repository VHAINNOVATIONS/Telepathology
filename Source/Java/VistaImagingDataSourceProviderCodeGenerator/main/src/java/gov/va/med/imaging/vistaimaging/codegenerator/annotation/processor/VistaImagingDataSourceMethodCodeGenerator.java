/**
 * 
 */
package gov.va.med.imaging.vistaimaging.codegenerator.annotation.processor;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

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
public class VistaImagingDataSourceMethodCodeGenerator
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
	private static final String SIMPLE_NAME = "simpleName";
	private static final String TRANSLATOR_METHOD = "translatorMethod";
	private static final String QUERY_FACTORY_METHOD = "queryFactoryMethod";
	private static final String QUERY_FACTORY_PARAMETERS = "queryFactoryParameters";
	private static final String TRANSLATOR_METHOD_PARAMETERS = "translatorMethodParameters";
	private static final String INPUT_PARAMETERS_DESCRIPTION = "inputParametersDescription";
	
	private final Configuration freemarkerConfig;
	private final Messager messager;
	
	private final Map<String, Object> dataModel = new HashMap<String, Object>();
	
	private String declaredType;
	private List<ParameterDescription> routerMethodParameters;
	private List<ParameterDescription> commandFactoryMethodParameters;
	private String simpleName;
	private String translatorMethod;
	private String queryFactoryMethod;
	private String queryFactoryParameters;
	private String translatorMethodParameters;
	private String inputParametersDescription;
	
	/**
	 * Private constructor to handle the common construction operations.
	 * @param messager
	 */
	private VistaImagingDataSourceMethodCodeGenerator(Messager messager) 
	{
		super();
		freemarkerConfig = new Configuration();
		
		freemarkerConfig.setTemplateLoader( new ClassLoaderTemplateLoader(this.getClass().getClassLoader(), messager) );
		freemarkerConfig.setObjectWrapper(new DefaultObjectWrapper());

		this.messager = messager;
	}
	
	/**
	 * @param resultType
	 * @param methodName
	 * @param commandClassName
	 * @param commandSimpleClassName 
	 * @param asynchronous
	 */
	public VistaImagingDataSourceMethodCodeGenerator(
		Messager messager, 
		String resultType,		// the type of the result as far as the method declaration is concerned
		String resultTypeCollectionType,
		String resultTypeCollectionMemberType,
		boolean generateTypesafeCode,
		String simpleName,
		String declaredMethodName,		// the name as declared in the interface, may differ from routerMethod if it was mapped
		String translatorMethod,
		String queryFactoryMethod,
		String queryFactoryParameters,
		String translatorMethodParameters,
		String inputParametersDescription
	)
	{
		this(messager);
		setName(declaredMethodName);
		setType( resultType );
		setCollectionType(resultTypeCollectionType);
		setCollectionMemberType(resultTypeCollectionMemberType);
		setMapKeyType(null);
		setMapValueType(null);
		setSimpleName(simpleName);
		setTranslatorMethod(translatorMethod);
		setQueryFactoryMethod(queryFactoryMethod);
		if(queryFactoryParameters != null)
			setQueryFactoryParameters(queryFactoryParameters);
		if(translatorMethodParameters != null)
			setTranslatorMethodParameters(translatorMethodParameters);
		if(inputParametersDescription != null)
			setInputParametersDescription(inputParametersDescription);

		this.routerMethodParameters = new ArrayList<ParameterDescription>();
		this.commandFactoryMethodParameters = new ArrayList<ParameterDescription>();
	}
	
	/**
	 * 
	 * @param type
	 */
	public void setType(String type)
	{
		this.declaredType = type;
		dataModel.put(DECLARED_TYPE, type);
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
	
	public String getSimpleName()
	{
		return simpleName;
	}

	public void setSimpleName(String simpleName)
	{
		this.simpleName = simpleName;
		dataModel.put(SIMPLE_NAME, simpleName );
	}
	
	public String getTranslatorMethod()
	{
		return translatorMethod;
	}

	public void setTranslatorMethod(String translatorMethod)
	{
		this.translatorMethod = translatorMethod;
		dataModel.put(TRANSLATOR_METHOD, translatorMethod );
	}

	public String getQueryFactoryMethod()
	{
		return queryFactoryMethod;
	}

	public void setQueryFactoryMethod(String queryFactoryMethod)
	{
		this.queryFactoryMethod = queryFactoryMethod;
		dataModel.put(QUERY_FACTORY_METHOD, queryFactoryMethod );
	}

	public String getQueryFactoryParameters()
	{
		return queryFactoryParameters;
	}

	public void setQueryFactoryParameters(String queryFactoryParameters)
	{
		this.queryFactoryParameters = queryFactoryParameters;
		dataModel.put(QUERY_FACTORY_PARAMETERS, queryFactoryParameters );
	}

	public String getTranslatorMethodParameters()
	{
		return translatorMethodParameters;
	}

	public void setTranslatorMethodParameters(String translatorMethodParameters)
	{
		this.translatorMethodParameters = translatorMethodParameters;
		dataModel.put(TRANSLATOR_METHOD_PARAMETERS, translatorMethodParameters );
	}

	public String getInputParametersDescription()
	{
		return inputParametersDescription;
	}

	public void setInputParametersDescription(String inputParametersDescription)
	{
		this.inputParametersDescription = inputParametersDescription;
		dataModel.put(INPUT_PARAMETERS_DESCRIPTION, inputParametersDescription );
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
		for(VistaImagingDataSourceMethodCodeGenerator.ParameterDescription parameterElement : parameterDescriptions )
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
		
		String templateName = "VistaImagingDataSourceMethod.tfl";
		
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
