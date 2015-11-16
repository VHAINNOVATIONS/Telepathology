/**
 * 
 */
package gov.va.med.imaging.federation.codegenerator.annotation.processor;

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
public class FederationDataSourceProxyMethodCodeGenerator
{
	private static final String NAME = "name";
	private static final String TYPE = "type";
	private static final String VALUE = "value";
	private static final String METHOD = "method";
	private static final String DECLARED_TYPE = "declaredType";
	private static final String COLLECTION_TYPE = "collectionType";
	private static final String COLLECTION_MEMBER_TYPE = "collectionMemberType";
	private static final String MAP_KEY_TYPE = "mapKeyType";
	private static final String MAP_VALUE_TYPE = "mapValueType";
	private static final String ROUTER_PARAMETERS = "parameters";
	private static final String COMANDFACTORY_PARAMETERS = "factoryParameters";
	private static final String FEDERATION_METHOD_URI = "federationMethodUri";
	private static final String TRANSLATOR_METHOD = "translatorMethod";
	private static final String TRANSLATOR_METHOD_PARAMETERS = "translatorMethodParameters";
	private static final String INPUT_PARAMETERS_DESCRIPTION = "inputParametersDescription";
	private static final String FEDERATION_RETURN_TYPE = "federationReturnType";
	private static final String REST_QUERY_PARAMETERS = "restQueryParameters";
	private static final String POST_PARAMETER = "postParameter";
	private static final String TRANSLATION_INPUT_PARAMETERS = "translationInputParameters";
	
	private final Configuration freemarkerConfig;
	private final Messager messager;
	
	private final Map<String, Object> dataModel = new HashMap<String, Object>();
	
	private String declaredType;
	private List<ParameterDescription> routerMethodParameters;
	private List<ParameterDescription> commandFactoryMethodParameters;
	private List<RestParameter> restParameters;
	private List<InputTranslationParameter> translationInputParameters;
	private String federationMethodUri;
	private String translatorMethod;
	private String translatorMethodParameters;
	private String inputParametersDescription;
	private String federationReturnType;
	private String postParameter = null;
	
	/**
	 * Private constructor to handle the common construction operations.
	 * @param messager
	 */
	private FederationDataSourceProxyMethodCodeGenerator(Messager messager) 
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
	public FederationDataSourceProxyMethodCodeGenerator(
		Messager messager, 
		String resultType,		// the type of the result as far as the method declaration is concerned
		String resultTypeCollectionType,
		String resultTypeCollectionMemberType,
		boolean generateTypesafeCode,
		String federationMethodUri,
		String declaredMethodName,		// the name as declared in the interface, may differ from routerMethod if it was mapped
		String translatorMethod,
		String translatorMethodParameters,
		String inputParametersDescription,
		String federationReturnType,
		String postParameter
	)
	{
		this(messager);
		setName(declaredMethodName);
		setType( resultType );
		setCollectionType(resultTypeCollectionType);
		setCollectionMemberType(resultTypeCollectionMemberType);
		setMapKeyType(null);
		setMapValueType(null);
		setFederationMethodUri(federationMethodUri);
		setTranslatorMethod(translatorMethod);
		if(translatorMethodParameters != null)
			setTranslatorMethodParameters(translatorMethodParameters);
		if(inputParametersDescription != null)
			setInputParametersDescription(inputParametersDescription);
		
		setFederationReturnType(federationReturnType);
		if(postParameter != null)
			setPostParameter(postParameter);

		this.routerMethodParameters = new ArrayList<ParameterDescription>();
		this.commandFactoryMethodParameters = new ArrayList<ParameterDescription>();
		this.restParameters = new ArrayList<RestParameter>();
		this.translationInputParameters = new ArrayList<InputTranslationParameter>();
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
	
	public String getFederationMethodUri()
	{
		return federationMethodUri;
	}

	public void setFederationMethodUri(String federationMethodUri)
	{
		this.federationMethodUri = federationMethodUri;
		dataModel.put(FEDERATION_METHOD_URI, federationMethodUri );
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

	public String getFederationReturnType()
	{
		return federationReturnType;
	}

	public void setFederationReturnType(String federationReturnType)
	{
		this.federationReturnType = federationReturnType;
		dataModel.put(FEDERATION_RETURN_TYPE, federationReturnType );
	}

	public String getPostParameter()
	{
		return postParameter;
	}

	public void setPostParameter(String postParameter)
	{
		this.postParameter = postParameter;
		dataModel.put(POST_PARAMETER, postParameter );
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
	
	public void addRestParameter(String name, String value)
	{
		this.restParameters.add(new RestParameter(name, value));
	}
	
	public List<Map<String, String>> getRestParametersDataModel()
	{
		List<Map<String, String>> parameters = new ArrayList<Map<String, String>>();
		for(RestParameter parameterElement : restParameters )
		{
			Map<String, String> parameter = new HashMap<String, String>();
			parameter.put(VALUE, parameterElement.getValue());
			parameter.put(NAME, parameterElement.getName());
			parameters.add( parameter );
		}
		
		return parameters;
	}
	
	public void addInputTranslationParameter(String parameterName, String methodName, String resultPropertyName)
	{
		this.translationInputParameters.add(new InputTranslationParameter(parameterName, 
				methodName, resultPropertyName));
	}
	
	public List<Map<String, String>> getInputTranslationParametersDataModel()
	{
		List<Map<String, String>> parameters = new ArrayList<Map<String, String>>();
		for(InputTranslationParameter parameterElement : translationInputParameters )
		{
			Map<String, String> parameter = new HashMap<String, String>();
			parameter.put(VALUE, parameterElement.getResultPropertyName());
			parameter.put(NAME, parameterElement.getParameterName());
			parameter.put(METHOD, parameterElement.getMethodName());
			parameters.add( parameter );
		}
		
		return parameters;
	}
	
	/*
	 * 
	 */
	private List<Map<String, String>> getParameterDataModel(List<ParameterDescription> parameterDescriptions)
	{
		List<Map<String, String>> parameters = new ArrayList<Map<String, String>>();
		for(FederationDataSourceProxyMethodCodeGenerator.ParameterDescription parameterElement : parameterDescriptions )
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
		dataModel.put(REST_QUERY_PARAMETERS, getRestParametersDataModel());
		dataModel.put(TRANSLATION_INPUT_PARAMETERS, getInputTranslationParametersDataModel());
		
		String templateName = "FederationDataSourceProxyMethod.tfl";
		if(postParameter != null && postParameter.length() > 0)
			templateName = "FederationDataSourceProxyPostMethod.tfl";
		else
			templateName = "FederationDataSourceProxyGetMethod.tfl";
		
		
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
	
	class RestParameter
	{
		private final String name;
		private final String value;
		
		public RestParameter(String name, String value)
		{
			super();
			this.name = name;
			this.value = value;
		}

		public String getName()
		{
			return name;
		}

		public String getValue()
		{
			return value;
		}
	}
	
	class InputTranslationParameter
	{
		private final String parameterName;
		private final String methodName;
		private final String resultPropertyName;
		
		public InputTranslationParameter(String parameterName,
				String methodName, String resultPropertyName)
		{
			super();
			this.parameterName = parameterName;
			this.methodName = methodName;
			this.resultPropertyName = resultPropertyName;
		}

		public String getParameterName()
		{
			return parameterName;
		}

		public String getMethodName()
		{
			return methodName;
		}

		public String getResultPropertyName()
		{
			return resultPropertyName;
		}
	}
}
