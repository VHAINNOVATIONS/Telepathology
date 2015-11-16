/**
 * 
 */
package gov.va.med.imaging.core.codegenerator;

import freemarker.template.TemplateException;
import gov.va.med.imaging.core.CommandClassSemantics;
import gov.va.med.imaging.core.CoreRouterSemanticsException;
import gov.va.med.imaging.core.RouterMethodSemantics;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.*;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaCompiler.CompilationTask;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 * 
 */
public class TestFacadeRouterMethodCodeGenerator
	extends TestCase
{
	private Messager messager = new MockMessager();

	/**
	 * Test method for
	 * {@link gov.va.med.imaging.core.codegenerator.FacadeRouterMethodCodeGenerator#FacadeRouterMethodCodeGenerator(javax.annotation.processing.Messager, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String, gov.va.med.imaging.core.RouterMethodSemantics, gov.va.med.imaging.core.CommandClassSemantics, boolean, java.lang.String, long, int, boolean)}
	 * .
	 */
	public void testFacadeRouterSynchronousMethodCodeGenerator()
	{

		try
		{
			FacadeRouterMethodCodeGenerator generator = new FacadeRouterMethodCodeGenerator(messager,
				"gov.va.med.imaging.exchange.business.Study", null, null, true, "getStudy", RouterMethodSemantics
					.create("getStudy"), CommandClassSemantics.create("getStudyCommand", null), false, "asynchListener", 10L,
				1, false, false, false, "", false, 100);

			generator.addCommandFactoryMethodParameter("parm1", "java.lang.String");
			generator.addRouterMethodParameter("parm1", "java.lang.String");
			generator.addRouterMethodParameter("asynchListener",
				"gov.va.med.imaging.core.router.AsynchronousMethodCompletionListener");

			StringWriter writer = new StringWriter(2048);
			generator.generate(writer);

			System.out.println("======================== Start Generated Synchronous Method ========================");
			System.out.println(writer.toString());
			System.out.println("======================== End Generated Synchronous Method ========================");
		}
		catch (Exception x)
		{
			x.printStackTrace();
			fail(x.getMessage());
		}
	}

	public void testFacadeRouterSynchronousVoidMethodCodeGenerator()
	{

		try
		{
			FacadeRouterMethodCodeGenerator generator = new FacadeRouterMethodCodeGenerator(messager, "void", null,
				null, true, "getStudy", RouterMethodSemantics.create("getStudy"), CommandClassSemantics
					.create("getStudyCommand", null), false, "asynchListener", 10L, 1, false, false, false, "", false, 100);

			generator.addCommandFactoryMethodParameter("parm1", "java.lang.String");
			generator.addRouterMethodParameter("parm1", "java.lang.String");
			generator.addRouterMethodParameter("asynchListener",
				"gov.va.med.imaging.core.router.AsynchronousMethodCompletionListener");

			StringWriter writer = new StringWriter(2048);
			generator.generate(writer);

			System.out.println("======================== Start Generated Synchronous Method ========================");
			System.out.println(writer.toString());
			System.out.println("======================== End Generated Synchronous Method ========================");
		}
		catch (Exception x)
		{
			x.printStackTrace();
			fail(x.getMessage());
		}
	}

	/**
	 * 
	 */
	public void testFacadeRouterAsynchronousMethodCodeGenerator()
	{
		try
		{
			FacadeRouterMethodCodeGenerator generator = new FacadeRouterMethodCodeGenerator(messager,
				"gov.va.med.imaging.exchange.business.Study", null, null, true, "getStudy", RouterMethodSemantics
					.create("getStudy"), CommandClassSemantics.create("getStudyCommand", null), true, "asynchListener", 10L,
				1, false, false, false, "", false, 100);

			generator.addCommandFactoryMethodParameter("parm1", "java.lang.String");
			generator.addRouterMethodParameter("parm1", "java.lang.String");
			generator.addRouterMethodParameter("asynchListener",
				"gov.va.med.imaging.core.router.AsynchronousMethodCompletionListener");

			StringWriter writer = new StringWriter(2048);
			generator.generate(writer);

			System.out.println("======================== Start Generated Asynchronous Method ========================");
			System.out.println(writer.toString());
			System.out.println("======================== End Generated Asynchronous Method ========================");
		}
		catch (Exception x)
		{
			x.printStackTrace();
			fail(x.getMessage());
		}
	}

	/**
	 * 
	 */
	public void testFacadeMockRouterSynchronousMethodCodeGenerator()
	{
		try
		{
			FacadeRouterMethodCodeGenerator generator = new FacadeRouterMethodCodeGenerator(messager,
				"gov.va.med.imaging.exchange.business.Study", null, null, true, "getStudy", RouterMethodSemantics
					.create("getStudy"), CommandClassSemantics.create("getStudyCommand", null), false, "asynchListener", 10L,
				1, false, true, false, "", false, 100);

			generator.addCommandFactoryMethodParameter("parm1", "java.lang.String");
			generator.addRouterMethodParameter("parm1", "java.lang.String");
			generator.addRouterMethodParameter("asynchListener",
				"gov.va.med.imaging.core.router.AsynchronousMethodCompletionListener");

			StringWriter writer = new StringWriter(2048);
			generator.generate(writer);

			System.out
				.println("======================== Start Generated Mock Synchronous Method ========================");
			System.out.println(writer.toString());
			System.out
				.println("======================== End Generated Mock Synchronous Method ========================");
		}
		catch (Exception x)
		{
			x.printStackTrace();
			fail(x.getMessage());
		}
	}

	/**
	 * 
	 */
	public void testFacadeMockRouterSynchronousVoidMethodCodeGenerator()
	{
		try
		{
			FacadeRouterMethodCodeGenerator generator = new FacadeRouterMethodCodeGenerator(messager, "void", null,
				null, true, "getStudy", RouterMethodSemantics.create("getStudy"), CommandClassSemantics
					.create("getStudyCommand", null), false, "asynchListener", 10L, 1, false, true, false, "", false, 100);

			generator.addCommandFactoryMethodParameter("parm1", "java.lang.String");
			generator.addRouterMethodParameter("parm1", "java.lang.String");
			generator.addRouterMethodParameter("asynchListener",
				"gov.va.med.imaging.core.router.AsynchronousMethodCompletionListener");

			StringWriter writer = new StringWriter(2048);
			generator.generate(writer);

			System.out
				.println("======================== Start Generated Mock Synchronous Void Method ========================");
			System.out.println(writer.toString());
			System.out
				.println("======================== End Generated Mock Synchronous Void Method ========================");
		}
		catch (Exception x)
		{
			x.printStackTrace();
			fail(x.getMessage());
		}
	}

	/**
	 * 
	 */
	public void testFacadeMockRouterAsynchronousMethodCodeGenerator()
	{
		try
		{
			FacadeRouterMethodCodeGenerator generator = new FacadeRouterMethodCodeGenerator(messager,
				"gov.va.med.imaging.exchange.business.Study", null, null, true, "getStudy", RouterMethodSemantics
					.create("getStudy"), CommandClassSemantics.create("getStudyCommand", null), true, "asynchListener", 10L,
				1, false, true, false, "", false, 100);

			generator.addCommandFactoryMethodParameter("parm1", "java.lang.String");
			generator.addRouterMethodParameter("parm1", "java.lang.String");
			generator.addRouterMethodParameter("asynchListener",
				"gov.va.med.imaging.core.router.AsynchronousMethodCompletionListener");

			StringWriter writer = new StringWriter(2048);
			generator.generate(writer);

			System.out
				.println("======================== Start Generated Mock Asynchronous Method ========================");
			System.out.println(writer.toString());
			System.out
				.println("======================== End Generated Mock Asynchronous Method ========================");
		}
		catch (Exception x)
		{
			x.printStackTrace();
			fail(x.getMessage());
		}
	}

	public void testFacadeMockRouterSynchronousListMethodCodeGenerator()
	{
		try
		{
			FacadeRouterMethodCodeGenerator generator = new FacadeRouterMethodCodeGenerator(messager,
				"java.util.List<gov.va.med.imaging.exchange.business.Study>", "java.util.List",
				"gov.va.med.imaging.exchange.business.Study", true, "getStudy", RouterMethodSemantics
					.create("getStudy"), CommandClassSemantics.create("getStudyCommand", null), false, "asynchListener", 10L,
				1, false, true, false, "", false, 100);

			generator.addCommandFactoryMethodParameter("parm1", "java.lang.String");
			generator.addRouterMethodParameter("parm1", "java.lang.String");
			generator.addRouterMethodParameter("asynchListener",
				"gov.va.med.imaging.core.router.AsynchronousMethodCompletionListener");

			StringWriter writer = new StringWriter(2048);
			generator.generate(writer);

			System.out
				.println("======================== Start Generated Mock Synchronous Method ========================");
			System.out.println(writer.toString());
			System.out
				.println("======================== End Generated Mock Synchronous Method ========================");
		}
		catch (Exception x)
		{
			x.printStackTrace();
			fail(x.getMessage());
		}
	}

	public void testFacadeMockRouterSynchronousSetMethodCodeGenerator()
	{
		try
		{
			FacadeRouterMethodCodeGenerator generator = new FacadeRouterMethodCodeGenerator(messager,
				"java.util.Set<gov.va.med.imaging.exchange.business.Study>", "java.util.Set",
				"gov.va.med.imaging.exchange.business.Study", true, "getStudy", RouterMethodSemantics
					.create("getStudy"), CommandClassSemantics.create("getStudyCommand", null), false, "asynchListener", 10L,
				1, false, true, false, "", false, 100);

			generator.addCommandFactoryMethodParameter("parm1", "java.lang.String");
			generator.addRouterMethodParameter("parm1", "java.lang.String");
			generator.addRouterMethodParameter("asynchListener",
				"gov.va.med.imaging.core.router.AsynchronousMethodCompletionListener");

			StringWriter writer = new StringWriter(2048);
			generator.generate(writer);

			System.out
				.println("======================== Start Generated Mock Synchronous Method ========================");
			System.out.println(writer.toString());
			System.out
				.println("======================== End Generated Mock Synchronous Method ========================");
		}
		catch (Exception x)
		{
			x.printStackTrace();
			fail(x.getMessage());
		}
	}

	public void testFacadeMockRouterSynchronousMapMethodCodeGenerator()
	{
		try
		{
			FacadeRouterMethodCodeGenerator generator = new FacadeRouterMethodCodeGenerator(messager,
				"java.util.Map<java.lang.String, gov.va.med.imaging.exchange.business.Study>", "java.util.Map",
				"java.lang.String", "gov.va.med.imaging.exchange.business.Study", true, "getStudy",
				RouterMethodSemantics.create("getStudy"), CommandClassSemantics.create("getStudyCommand", null), false,
				"asynchListener", 10L, 1, false, true, false, "", false, 100);

			generator.addCommandFactoryMethodParameter("parm1", "java.lang.String");
			generator.addRouterMethodParameter("parm1", "java.lang.String");
			generator.addRouterMethodParameter("asynchListener",
				"gov.va.med.imaging.core.router.AsynchronousMethodCompletionListener");

			StringWriter writer = new StringWriter(2048);
			generator.generate(writer);

			System.out
				.println("======================== Start Generated Mock Synchronous Method ========================");
			System.out.println(writer.toString());
			System.out
				.println("======================== End Generated Mock Synchronous Method ========================");
		}
		catch (Exception x)
		{
			x.printStackTrace();
			fail(x.getMessage());
		}
	}

	public void testRouterFacadeGeneration() 
	throws CoreRouterSemanticsException, IOException, TemplateException
	{
		// creates the code using the code generator
		StringWriter interfaceWriter = generateTestRouterFacade();
		StringWriter implementationWriter = generateTestRouterFacadeImpl();
		
		System.out.println("======================== Start Generated Test Interface ========================");
		System.out.println(interfaceWriter.toString());
		System.out.println("======================== End Generated Test Interface ========================");
		System.out.println("======================== Start Generated Test Implementation ========================");
		System.out.println(implementationWriter.toString());
		System.out.println("======================== End Generated Test Implementation ========================");
		
		CompilerWrapper memCompiler = new CompilerWrapper(
			new CharSequenceCompilationUnit("TestRouterFacade", interfaceWriter.toString()), 
			new CharSequenceCompilationUnit("TestRouterFacadeImpl", implementationWriter.toString())
		);
		if( memCompiler.isCompilable() )
			System.out.println("COMPILATION IS SUCCESSFUL");
		else
			System.out.println("COMPILATION FAILED");
	}

	/**
	 * 
	 * @return
	 */
	private StringWriter generateTestRouterFacade() 
	{
		StringWriter writer = new StringWriter(9192);

		writer.append("package gov.va.med.imaging.awiv; \n");

		writer.append("import gov.va.med.RoutingToken; \n");
		writer.append("import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterface; \n");
		writer.append("import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterMethod; \n");
		writer.append("import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException; \n");
		writer.append("import gov.va.med.imaging.core.interfaces.exceptions.MethodException; \n");

		writer.append("@FacadeRouterInterface(extendsClassName=\"gov.va.med.imaging.BaseWebFacadeRouterImpl\") \n");
		writer.append("public interface TestRouterFacade \n");
		writer.append("{ \n");
			writer.append("@FacadeRouterMethod(asynchronous=false, commandClassName=\"GetStudyCommand\") \n");
			writer.append("public abstract gov.va.med.imaging.exchange.business.Study getStudy(String parm1) \n");
			writer.append("throws MethodException, ConnectionException; \n");
		writer.append("} \n");
		
		return writer;
	}
	
	
	/**
	 * 
	 * @return
	 * @throws CoreRouterSemanticsException
	 * @throws IOException
	 * @throws TemplateException
	 */
	private StringWriter generateTestRouterFacadeImpl() 
	throws CoreRouterSemanticsException, IOException, TemplateException
	{
		FacadeRouterCodeGenerator codeGenerator = new FacadeRouterCodeGenerator(this.messager, false, false);
		codeGenerator.setPackageName("biz.happycat.med");
		codeGenerator.setInterfaceName("TestRouterFacade");
		
		FacadeRouterMethodCodeGenerator methodGenerator = new FacadeRouterMethodCodeGenerator(
			messager,
			"gov.va.med.imaging.exchange.business.Study", null, null, true, "getStudy", 
			RouterMethodSemantics.create("getStudy"), 
			CommandClassSemantics.create("getStudyCommand", null), 
			false, "asynchListener", 10L,
			1, false, false, false, "", false, 100);

		methodGenerator.addCommandFactoryMethodParameter("parm1", "java.lang.String");
		methodGenerator.addRouterMethodParameter("parm1", "java.lang.String");

		codeGenerator.addMethod(methodGenerator);
		
        //@FacadeRouterMethod(asynchronous=false)
        //public Boolean postToEmailQueue(List<InternetAddress> recepients, String subjectLine, String messageBody, String scpContext)
        //            throws MethodException, ConnectionException;

		FacadeRouterMethodCodeGenerator listParameterMethodGenerator = new FacadeRouterMethodCodeGenerator(
			messager,
			"java.lang.Boolean", null, null, true, "postToEmailQueue", 
			RouterMethodSemantics.create("postToEmailQueue"), 
			CommandClassSemantics.create("postToEmailQueueCommand", null), 
			false, "asynchListener", 10L,
			1, false, false, false, "", false, 100
		);

		listParameterMethodGenerator.addCommandFactoryMethodParameter("recepients", "java.util.List");
		listParameterMethodGenerator.addRouterMethodParameter("recepients", "List<InternetAddress>");
		
		listParameterMethodGenerator.addCommandFactoryMethodParameter("subjectLine", "java.lang.String");
		listParameterMethodGenerator.addRouterMethodParameter("subjectLine", "java.lang.String");

		listParameterMethodGenerator.addCommandFactoryMethodParameter("messageBody", "java.lang.String");
		listParameterMethodGenerator.addRouterMethodParameter("messageBody", "java.lang.String");
		
		listParameterMethodGenerator.addCommandFactoryMethodParameter("scpContext", "java.lang.String");
		listParameterMethodGenerator.addRouterMethodParameter("scpContext", "java.lang.String");
		
		codeGenerator.addMethod(listParameterMethodGenerator);
		
		StringWriter writer = new StringWriter(9192);
		codeGenerator.generate(writer);
		return writer;
	}

	/**
	 * 
	 * @author vhaiswbeckec
	 * 
	 */
	class MockMessager
		implements Messager
	{
		@Override
		public void printMessage(Kind kind, CharSequence msg)
		{
			this.printMessage(kind, msg, null, null, null);
		}

		@Override
		public void printMessage(Kind kind, CharSequence msg, Element e)
		{
			this.printMessage(kind, msg, e, null, null);
		}

		@Override
		public void printMessage(Kind kind, CharSequence msg, Element e, AnnotationMirror a)
		{
			this.printMessage(kind, msg, e, a, null);
		}

		@Override
		public void printMessage(Kind kind, CharSequence msg, Element e, AnnotationMirror a, AnnotationValue v)
		{
			System.err.println("[" + (kind == null ? "<null kind>" : kind.toString()) + "] - " + msg + " @ "
				+ (e == null ? "<null element name>" : e.getSimpleName().toString()) + ", "
				+ (a == null ? "<null annotation mirror>" : a.toString()) + ", "
				+ (v == null ? "<null annotation value>" : v.toString()));
		}
	}

	/**
	 *
	 */
	public class CompilerWrapper
	{
		private final CharSequenceCompilationUnit[] compilationUnits;
		public CompilerWrapper(CharSequenceCompilationUnit... compilationUnits)
		{
			this.compilationUnits = compilationUnits;
		}
		
		public CharSequenceCompilationUnit[] getCompilationUnits()
		{
			return this.compilationUnits;
		}

		public boolean isCompilable() 
		throws IOException
		{
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

			List<String> options = new ArrayList<String>();
			options.add("-classpath");
			StringBuilder sbClasspath = new StringBuilder();
			sbClasspath.append("./;");
			URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
			for (URL url : urlClassLoader.getURLs())
				sbClasspath.append(url.getFile()).append(File.pathSeparator);
			options.add(sbClasspath.toString());
			
			for(String option : options)
				System.out.println(option);
			
			Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(getCompilationUnits());
			CompilationTask task = compiler.getTask(null, null, diagnostics, null, null, compilationUnits);

			Boolean success = task.call();
			for( Diagnostic<?> diagnostic : diagnostics.getDiagnostics() )
			{
				StringBuilder sb = new StringBuilder();
				
				sb.append(diagnostic.getKind());
				sb.append('(');
				sb.append(diagnostic.getCode());
				sb.append(')');
				sb.append(diagnostic.getSource());
				sb.append('[');
				sb.append(diagnostic.getStartPosition());
				sb.append(" to ");
				sb.append(diagnostic.getEndPosition());
				sb.append(']');
				sb.append(diagnostic.getMessage(null));
				
				System.out.println(sb.toString());
			}
			
			if(success.booleanValue())
				System.out.println("Compilation Successful");
			else
				System.out.println("Compilation Failed");

			return success.booleanValue();
		}
	}

	class CharSequenceCompilationUnit
	extends SimpleJavaFileObject
	implements JavaFileObject
	{
		final CharSequence code;

		CharSequenceCompilationUnit(String name, CharSequence code)
		{
			super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors)
		{
			return code;
		}
	}
}
