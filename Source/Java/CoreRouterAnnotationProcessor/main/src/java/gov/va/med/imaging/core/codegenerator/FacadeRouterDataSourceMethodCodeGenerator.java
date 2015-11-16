/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 10, 2011
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.core.codegenerator;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.va.med.imaging.core.CommandClassSemantics;

/**
 * @author VHAISWWERFEJ
 *
 */
public class FacadeRouterDataSourceMethodCodeGenerator
{
	private final Configuration freemarkerConfig;
	private final Messager messager;
	
	private final Map<String, Object> dataModel = new HashMap<String, Object>();
	
	private List<ParameterDescription> routerMethodParameters;
	private List<ParameterDescription> commandFactoryMethodParameters;
	
	private String dataSourceSpi;
	private String dataSourceMethod;
	private String declaredType;
	private String routingTokenParameterName;
	private String baseDataSourceCommand;
	private String postProcessResultMethodName;
	private CommandClassSemantics canonicalCommandClass;
	private Writer writer;
	
	private final static String FACADE_ROUTER_DATA_sOURCE_METHOD_TEMPLATE = "FacadeRouterDataSourceMethodImpl.ftl";

	private static final String NAME = "name";
	private static final String TYPE = "type";
	private static final String DECLARED_TYPE = "declaredType";
	private static final String COLLECTION_TYPE = "collectionType";
	private static final String COLLECTION_MEMBER_TYPE = "collectionMemberType";
	private static final String MAP_KEY_TYPE = "mapKeyType";
	private static final String MAP_VALUE_TYPE = "mapValueType";
	private static final String DATA_SOURCE_SPI = "dataSourceSpi";
	private static final String DATA_SOURCE_METHOD = "dataSourceMethod";
	private static final String COMANDFACTORY_PARAMETERS = "factoryParameters";
	private static final String ROUTER_PARAMETERS = "parameters";
	private static final String PACKAGE = "package";
	private static final String COMMANDCLASSNAME = "commandClassName";
	private static final String ROUTING_TOKEN_PARAMETER_NAME = "routingTokenParameterName";
	private static final String BASE_DATASOURCE_COMMAND = "baseDataSourceCommand";
	private static final String POST_PROCESSING_RESULT_METHOD_NAME = "postProcessingResultMethodName";
	private static final String SPI_PARAMETER_NAMES = "spiParameterNames";
	private static final String SPI_PARAMETER_TYPES = "spiParameterTypes";
	
	
	/**
	 * Private constructor to handle the common construction operations.
	 * @param messager
	 */
	private FacadeRouterDataSourceMethodCodeGenerator(Messager messager) 
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
	public FacadeRouterDataSourceMethodCodeGenerator(
		Messager messager, 
		String resultType,		// the type of the result as far as the method declaration is concerned
		String resultTypeCollectionType,
		String resultTypeCollectionMemberType,
		boolean generateTypesafeCode,
		String dataSourceSpi,
		String dataSourceMethod,
		CommandClassSemantics canonicalCommandClass	// semantically correct command class name
	)
	{
		this(messager);
		
		setType( resultType );
		setCollectionType(resultTypeCollectionType);
		setCollectionMemberType(resultTypeCollectionMemberType);
		setMapKeyType(null);
		setMapValueType(null);
		setDataSourceMethod(dataSourceMethod);
		setDataSourceSpi(dataSourceSpi);
		setCanonicalCommandClass(canonicalCommandClass);

		this.routerMethodParameters = new ArrayList<ParameterDescription>();
		this.commandFactoryMethodParameters = new ArrayList<ParameterDescription>();
	}

	public FacadeRouterDataSourceMethodCodeGenerator(
		Messager messager, 
		String resultType,		// the type of the result as far as the method declaration is concerned
		String resultTypeCollectionType,
		String resultTypeMapKeyType,
		String resultTypeMapValueType,
		boolean generateTypesafeCode,
		String dataSourceSpi,
		String dataSourceMethod,
		CommandClassSemantics canonicalCommandClass	// semantically correct command class name
	)
	{
		this(messager);
		
		setType( resultType );
		setCollectionType(resultTypeCollectionType);
		setCollectionMemberType(null);
		setMapKeyType(resultTypeMapKeyType);
		setMapValueType(resultTypeMapValueType);
		setDataSourceMethod(dataSourceMethod);
		setDataSourceSpi(dataSourceSpi);
		setCanonicalCommandClass(canonicalCommandClass);

		this.routerMethodParameters = new ArrayList<ParameterDescription>();
		this.commandFactoryMethodParameters = new ArrayList<ParameterDescription>();
	}
	
	public void setSpiParameterNamesAndTypes(String spiParameterNames, String spiParameterTypes)
	{
		dataModel.put(SPI_PARAMETER_NAMES, spiParameterNames);
		dataModel.put(SPI_PARAMETER_TYPES, spiParameterTypes);
	}
	
	public String getPostProcessResultMethodName()
	{
		return postProcessResultMethodName;
	}

	public void setPostProcessResultMethodName(String postProcessResultMethodName)
	{
		this.postProcessResultMethodName = postProcessResultMethodName;
		dataModel.put(POST_PROCESSING_RESULT_METHOD_NAME, postProcessResultMethodName);
	}

	/**
	 * get/set the canonical command class name, this is the class name that is compliant
	 * with router semantics, not necessarily the one that the router interface implied
	 */
	void setCanonicalCommandClass(CommandClassSemantics canonicalCommandClass)
	{
		this.canonicalCommandClass = canonicalCommandClass;
		dataModel.put(COMMANDCLASSNAME, canonicalCommandClass.toStringAsImplementation());
		if(canonicalCommandClass.getCommandPackage() != null)
			setPackageName(canonicalCommandClass.getCommandPackage());
	}
	public CommandClassSemantics getCanonicalCommandClass()
	{
		return this.canonicalCommandClass;
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
	
	public String getRoutingTokenParameterName()
	{
		return routingTokenParameterName;
	}

	public void setRoutingTokenParameterName(String routingTokenParameterName)
	{
		this.routingTokenParameterName = routingTokenParameterName;
		dataModel.put(ROUTING_TOKEN_PARAMETER_NAME, routingTokenParameterName);
	}

	public String getBaseDataSourceCommand()
	{
		return baseDataSourceCommand;
	}

	public void setBaseDataSourceCommand(String baseDataSourceCommand)
	{
		this.baseDataSourceCommand = baseDataSourceCommand;
		dataModel.put(BASE_DATASOURCE_COMMAND, baseDataSourceCommand);
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

	public void setDataSourceSpi(String spi)
	{
		dataModel.put(DATA_SOURCE_SPI, spi);
		dataSourceSpi = spi;
	}
	
	public String getDataSourceSpi()
	{
		return dataSourceSpi;
	}

	public String getDataSourceMethod()
	{
		return dataSourceMethod;
	}

	public void setDataSourceMethod(String dataSourceMethod)
	{
		this.dataSourceMethod = dataSourceMethod;
		dataModel.put(DATA_SOURCE_METHOD, dataSourceMethod);
	}
	
	public void setPackageName(String packageName)
	{
		dataModel.put(PACKAGE, packageName);
	}
	public String getPackageName()
	{
		return (String)dataModel.get(PACKAGE);
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
		for(FacadeRouterDataSourceMethodCodeGenerator.ParameterDescription parameterElement : parameterDescriptions )
		{
			Map<String, String> parameter = new HashMap<String, String>();
			parameter.put(TYPE, parameterElement.getType());
			parameter.put(NAME, parameterElement.getName());
			parameters.add( parameter );
		}
		
		return parameters;
	}
	
	public void generate() 
	throws IOException, TemplateException
	{
		try
		{
			dataModel.put(ROUTER_PARAMETERS, getRouterMethodParameterDataModel());
			dataModel.put(COMANDFACTORY_PARAMETERS, getCommandFactoryMethodParametersDataModel());
			
			String templateName = FACADE_ROUTER_DATA_sOURCE_METHOD_TEMPLATE;
			
			Template template = freemarkerConfig.getTemplate(templateName);
	
			// a debugging tool that dumps the data model to the specified print stream
			//dumpDataModel(dataModel, System.out);
			
			template.process(dataModel, getWriter());
			writer.flush();
		}
		finally
		{
			try{getWriter().close();}catch(Throwable t){}
		}

	}
	
	public Writer getWriter()
	{
		return writer;
	}

	public void setWriter(Writer writer)
	{
		this.writer = writer;
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
