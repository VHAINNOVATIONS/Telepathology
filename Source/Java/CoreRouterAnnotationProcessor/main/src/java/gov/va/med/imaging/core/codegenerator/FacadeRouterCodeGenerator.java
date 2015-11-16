/**
 * 
 */
package gov.va.med.imaging.core.codegenerator;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author vhaiswbeckec
 *
 */
public class FacadeRouterCodeGenerator
{
	/**
	 * 
	 */
	private static final String FACADE_ROUTER_TEMPLATE = "FacadeRouterImpl.ftl";
	private static final String FACADE_ROUTER_TEST_TEMPLATE = "FacadeRouterTest.ftl";
	private static final String MOCKFACADE_ROUTER_TEMPLATE = "MockFacadeRouterImpl.ftl";
	
	/**
	 * The names of data model used in template processing
	 */
	private static final String IMPORTS = "imports";
	
	private static final String EXTENDSCLASSNAME = "extendsClassName"; 
	private static final String INTERFACENAME = "interfaceName";
	private static final String IMPLEMENTATIONNAME = "implementationName";
	private static final String TESTERNAME = "testerName";
	private static final String PACKAGE = "package";
	private static final String METHODS = "methods";
	private static final String METHOD = "method";
	
	private final Configuration freemarkerConfig;
	private final Messager messager;
	
	private final Map<String, Object> dataModel = new HashMap<String, Object>();
	private final List<FacadeRouterMethodCodeGenerator> methodList = new ArrayList<FacadeRouterMethodCodeGenerator>();
	private final List<FacadeRouterDataSourceMethodCodeGenerator> dataSourceMethodList = new ArrayList<FacadeRouterDataSourceMethodCodeGenerator>();
	
	private final boolean generateMockImplementation;
	private final boolean generateTests;

	/**
	 * A publicly accessible static method to consistently create a facade implementation name from
	 * a facade interface name.
	 * 
	 * @param facadeInterfaceName
	 * @return
	 */
	public static String generateFacadeImplementationSimpleClassName(String facadeInterfaceName)
	{
		return facadeInterfaceName + "Impl";
	}
	
	public static String generateFacadeTesterSimpleClassName(String facadeInterfaceName)
	{
		return facadeInterfaceName + "Test";
	}

	/**
	 * 
	 * @param messager - the compiler message to print messages to
	 * @param generateMockImplementation 
	 * @param packageName - the package name of the generated class (and the interface to implement)
	 * @param interfaceName - the (simple) name of the interface to implement
	 */
	public FacadeRouterCodeGenerator(Messager messager, 
			boolean generateMockImplementation,
			boolean generateTests)
	{
		freemarkerConfig = new Configuration();
		
		freemarkerConfig.setTemplateLoader( new ClassLoaderTemplateLoader(this.getClass().getClassLoader(), messager) );
		freemarkerConfig.setObjectWrapper(new DefaultObjectWrapper());

		dataModel.put(METHODS, new ArrayList<Map<String, Object>>());
		dataModel.put(IMPORTS, new ArrayList<String>());
		
		this.messager = messager;
		this.generateMockImplementation = generateMockImplementation;
		this.generateTests = generateTests;
	}
	
	public Messager getMessager()
	{
		return this.messager;
	}

	protected boolean isGenerateMockImplementation()
	{
		return this.generateMockImplementation;
	}

	protected boolean isGenerateTests() {
		return generateTests;
	}

	public void setExtendsClassName(String extendsClassName)
	{
		dataModel.put(EXTENDSCLASSNAME, extendsClassName);
	}
	public String getExtendsClassName()
	{
		return (String)dataModel.get(EXTENDSCLASSNAME);
	}
	
	public void setPackageName(String packageName)
	{
		dataModel.put(PACKAGE, packageName);
	}
	public String getPackageName()
	{
		return (String)dataModel.get(PACKAGE);
	}
	
	public void addImportPackage(String packageName)
	{
		getImports().add(packageName);
	}

	public List<String> getImports()
	{
		return (List<String>)dataModel.get(IMPORTS);
	}
	
	public void setInterfaceName(String interfaceName)
	{
		dataModel.put(INTERFACENAME, interfaceName);
		dataModel.put(IMPLEMENTATIONNAME, generateFacadeImplementationSimpleClassName(interfaceName));
		dataModel.put(TESTERNAME, generateFacadeTesterSimpleClassName(interfaceName));
	}
	public String getInterfaceName()
	{
		return (String)dataModel.get(INTERFACENAME);
	}
	public String getImplementationName()
	{
		return (String)dataModel.get(IMPLEMENTATIONNAME);
	}
	
	public String getTesterName()
	{
		return (String)dataModel.get(TESTERNAME);
	}

	/**
	 * 
	 * @param methodGenerator
	 */
	public void addMethod(FacadeRouterMethodCodeGenerator methodGenerator)
	{
		methodList.add(methodGenerator);
	}
	
	public void addDataSourceMethod(FacadeRouterDataSourceMethodCodeGenerator dataSourceMethodGenerator)
	{
		dataSourceMethodList.add(dataSourceMethodGenerator);
	}
	
	/**
	 * 
	 * @param writer
	 * @param generatableMethods 
	 * @throws IOException
	 * @throws TemplateException 
	 */
	public void generate(Writer writer) 
	throws IOException, TemplateException
	{
		for(FacadeRouterMethodCodeGenerator methodGenerator : methodList)
		{
			StringWriter methodWriter = new StringWriter();
			methodGenerator.generate(methodWriter);
			methodWriter.flush();

			((List<String>)dataModel.get(METHODS)).add(methodWriter.toString());
		}
		for(FacadeRouterDataSourceMethodCodeGenerator dataSourceMethodGenerator : dataSourceMethodList)
		{
			System.out.println("Calling generate for method '" + dataSourceMethodGenerator.getCanonicalCommandClass().toString() + "'.");
			dataSourceMethodGenerator.generate();
			
		}
		
		Template template = freemarkerConfig.getTemplate(
			isGenerateMockImplementation() ? MOCKFACADE_ROUTER_TEMPLATE : isGenerateTests() ? FACADE_ROUTER_TEST_TEMPLATE : FACADE_ROUTER_TEMPLATE
		);
		
		template.process(dataModel, writer);
		writer.flush();
	}

	/**
	 * A test driver
	 * @param argv
	 */
	public static void main(String[] argv)
	{
		FacadeRouterCodeGenerator generator = new FacadeRouterCodeGenerator(
				new Messager()
				{
					@Override
					public void printMessage(Kind kind, CharSequence msg)
					{
						printMessage(kind, msg, null);
					}

					@Override
					public void printMessage(Kind kind, CharSequence msg,
							Element e)
					{
						printMessage(kind, msg, e, null);
					}

					@Override
					public void printMessage(Kind kind, CharSequence msg,
							Element e, AnnotationMirror a)
					{
						printMessage(kind, msg, e, a, null);
					}

					@Override
					public void printMessage(Kind kind, CharSequence msg,
							Element e, AnnotationMirror a, AnnotationValue v)
					{
						System.out.println(kind.toString() + ": " + msg + " @ " + e.toString());
					}
					
				},
				false,
				false);
		
		generator.setPackageName("package.name");
		generator.setInterfaceName("InterfaceName");
		
		Writer writer = new java.io.PrintWriter(System.out);
		try
		{
			generator.generate(writer);
		} 
		catch (IOException x)
		{
			x.printStackTrace();
		} 
		catch (TemplateException x)
		{
			x.printStackTrace();
		}
		finally
		{
			try{writer.close();} catch(Exception x){}
		}
	}
	
}
