package gov.va.med.imaging.core.annotations;

import freemarker.template.TemplateException;
import gov.va.med.imaging.core.*;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterface;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterMethod;
import gov.va.med.imaging.core.codegenerator.FacadeRouterCodeGenerator;
import gov.va.med.imaging.core.codegenerator.FacadeRouterDataSourceMethodCodeGenerator;
import gov.va.med.imaging.core.codegenerator.FacadeRouterMethodCodeGenerator;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleElementVisitor6;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * An annotation processor, used to examine and process the annotations of
 * program elements. An annotation processor may, for example, create new
 * source files and XML documents to be used in conjunction with the
 * original code.
 * 
 * The AnnotationProcessor for Command Factory methods. 
 * This AnnotationProcessor simply enforces the naming conventions defined 
 * for command factory methods.
 * 
 * 1. If an existing Processor object is not being used, to create an instance of a processor the tool 
 *    calls the no-arg constructor of the processor class.
 * 2. Next, the tool calls the init method with an appropriate ProcessingEnvironment.
 * 3. Afterwards, the tool calls getSupportedAnnotationTypes, getSupportedOptions, and getSupportedSourceVersion. 
 *    These methods are only called once per run, not on each round.
 * 4. As appropriate, the tool calls the process method on the Processor object; a new Processor object is not 
 *    created for each round. 
 * 
 * @author vhaiswbeckec
 * 
 */
@SupportedAnnotationTypes("gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterface")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedOptions("gov.va.med.imaging.mock")
public class FacadeRouterAnnotationProcessor
extends AbstractProcessor
implements Processor
{
	public static final String MOCK_OPTION = "gov.va.med.imaging.mock";
	
	

	// --------------------------------------------------------------------------------
	//
	// --------------------------------------------------------------------------------
	
	private int roundIndex;
	private boolean generateTests = false;
	/**
	 * 
	 * @param env
	 */
	public FacadeRouterAnnotationProcessor() 
	{
		super();
		roundIndex = 0;
	}

	/**
	 * 
	 * @return
	 */
	protected ProcessingEnvironment getEnv()
	{
		return this.processingEnv;
	}

	/**
	 * 
	 * @return
	 */
	protected Filer getFiler()
	{
		return getEnv() == null ? null : getEnv().getFiler();
	}
	
	/**
	 * @return
	 */
	protected Messager getMessager()
	{
		return getEnv() == null ? null : getEnv().getMessager();
	}
	
	/**
	 * Return whether or not the option to generate a mock router
	 * implementation is true or not.
	 * 
	 * @return
	 */
	protected boolean isGeneratingMockImplementation()
	{
		Map<String, String> optionsMap = getEnv().getOptions();
		String value = optionsMap.get(MOCK_OPTION);
		return value == null ? false : Boolean.parseBoolean(value);
	}
	
	public boolean isGenerateTests() {
		return generateTests;
	}

	public void setGenerateTests(boolean generateTests) {
		this.generateTests = generateTests;
	}

	/**
	 * Processes the FacadeRouterInterface annotation, generating the router proxy source
	 * code.  This processor processes the FacadeRouterInterface annotations and looks for the
	 * FacadeRouterMethod annotations within the marked interfaces.  The FacadeRouterMethod
	 * annotation is not processed by this class.
	 * 
	 * Processes a set of annotation types on type elements originating from the prior round and 
	 * returns whether or not these annotations are claimed by this processor. If true is returned, 
	 * the annotations are claimed and subsequent processors will not be asked to process them; if 
	 * false  is returned, the annotations are unclaimed and subsequent processors may be asked to 
	 * process them. A processor may always return the same boolean value or may vary the result 
	 * based on chosen criteria.
	 * 
	 * The input set will be empty if the processor supports "*" and the root elements have no annotations. 
	 * A Processor must gracefully handle an empty set of annotations.
	 */
	@Override
	public boolean process(
		Set<? extends TypeElement> annotations,
		RoundEnvironment roundEnv)
	{
		boolean generateMockFacade = isGeneratingMockImplementation();
		String msg = generateMockFacade ?
			"Processing router facade interfaces, generating mock router facade." : 
			"Processing router facade interfaces, generating production router facade.";
		getMessager().printMessage(
			generateMockFacade ? javax.tools.Diagnostic.Kind.WARNING : javax.tools.Diagnostic.Kind.NOTE,  
			msg
		);
		System.out.println("Generating test option: " + isGenerateTests());
		//System.out.println(msg);
		
		// we must have a Set of annotations (not null and not empty) and we must
		// only generate code on the first round of processing.
		if(annotations != null && !annotations.isEmpty() && roundIndex == 0 )
		{
			//getEnv().getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, 
			//	this.getClass().getSimpleName() + ", processing " + (roundEnv.processingOver() ? "is over." : "is not over.") 
			//);

			FacadeRouterInterfaceVisitor interfaceVisitor = 
				new FacadeRouterInterfaceVisitor(generateMockFacade, isGenerateTests());
			
			// there should be one and only one annotation in the given Set
			for(TypeElement annotation : annotations)
			{
				//String annotationName = annotation.getQualifiedName().toString();
				
				//ElementKind.INTERFACE 
				//getEnv().getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Processing '" + annotation.toString() + "' annotations." );

				// get all of the elements marked with the FacadeRouterInterface annotation
				// these should only be on Type elements, but we will validate that
				Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
				for(Element element : elements)
				{
					//getEnv().getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Evaluating element '" + element.toString() + "' for processing." );
					if( element instanceof TypeElement && ElementKind.INTERFACE == element.getKind() )
					{
						//getEnv().getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Element '" + element.toString() + "' is an interface and is being processed." );
						TypeElement type = (TypeElement)element;
						Name elementTypeName = type.getQualifiedName();
						//getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Processing element '" + element.toString() + "'." );
						
						element.accept(interfaceVisitor, elementTypeName);
					}
					else
						getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, "Element '" + element.toString() + "' is annotated with FacadeRouterInterface but is not an interface." );
						
				}
			}
		}
		
		++roundIndex;
	    return true;		// "claim" the annotation, do not allow other processors to act on it
	}

	/**
	 * 
	 * @author vhaiswbeckec
	 *
	 */
	class FacadeRouterInterfaceVisitor
	extends SimpleElementVisitor6<Void, Name>
	implements ElementVisitor<Void, Name>
	{
		private FacadeRouterMethodVisitor routerMethodVisitor = new FacadeRouterMethodVisitor();
		private FacadeRouterDataSourceMethodVisitor routerDataSourceMethodVisitor = 
			new FacadeRouterDataSourceMethodVisitor(getMessager());
		private boolean generateMockImplementation = false;
		private boolean generateTests = false;
		
		FacadeRouterInterfaceVisitor(boolean generateMockImplementation, boolean generateTests)
		{
			this.generateMockImplementation = generateMockImplementation;
			this.generateTests = generateTests;
		}

		@Override
		public Void visitType(TypeElement e, Name p)
		{
			//getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Type element '" + e.toString() + "' is being visited." );
			// the router interface definition must be public
			if( ! e.getModifiers().contains(Modifier.PUBLIC) )
			{
				getMessager().printMessage(
					javax.tools.Diagnostic.Kind.ERROR, 
					"The definition of an interface marked as a FacadeRouterInterface must include the 'public' modifier.", 
					e);
				return null;
			}
			
			String routerInterfaceName = e.getSimpleName().toString();
			
			String extendsClassName = null;
			List<? extends AnnotationMirror> childElementAnnotationMirrors = e.getAnnotationMirrors();
			for( AnnotationMirror childElementAnnotationMirror : childElementAnnotationMirrors )
			{
				DeclaredType annotationType = childElementAnnotationMirror.getAnnotationType();
				
				if( FacadeRouterInterface.class.getName().equals(annotationType.toString()) )
				{
					Map<? extends ExecutableElement, ? extends AnnotationValue> annotationProperties = 
						childElementAnnotationMirror.getElementValues();
					if(annotationProperties != null)
					{
						for( ExecutableElement annotationProperty : annotationProperties.keySet() )
						{
							if( "extendsClassName()".equals(annotationProperty.toString()) )
							{
								extendsClassName = annotationProperties.get(annotationProperty).toString();
								if(extendsClassName != null )
								{
									extendsClassName = FacadeRouterAnnotationProcessor.stripSuperfluosAnnotationGarbage(extendsClassName);
									if(extendsClassName.trim().length() < 1)
										extendsClassName = null;
								}
							}
						}
					}
				}
			}
			
			// we need the enclosing element, which should be a package, to get the
			// package name of the router implementation to generate
			Element parentElement = e.getEnclosingElement();
			
			if( parentElement.getKind() != ElementKind.PACKAGE )
			{
				getMessager().printMessage(
					javax.tools.Diagnostic.Kind.ERROR, 
					"The enclosing element of the '" + routerInterfaceName + "' interface should be a package element and it is not.", 
					e);
				return null;
			}
			
			String packageName = parentElement.toString();
			
			Writer proxySourceWriter = null;
			try
			{
				//getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Generating code from element '" + e.toString() + "'." );
				//getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Parent package name is '" + packageName + "' and router interface name is '" + routerInterfaceName + "'." );
				FacadeRouterCodeGenerator codeGenerator = 
					new FacadeRouterCodeGenerator(getMessager(), 
							generateMockImplementation, generateTests);
				codeGenerator.setInterfaceName(routerInterfaceName);
				codeGenerator.setPackageName(packageName);
				
				if(extendsClassName != null)
					codeGenerator.setExtendsClassName(extendsClassName);
				
				/*
				// JMW 5/3/2011 P104 - no longer including all possible packages for commands - it wasn't needed for anything and caused problems
				for(Iterator<String> commandVocabularyIterator = CommandVocabulary.packageNames(); commandVocabularyIterator.hasNext(); )
					codeGenerator.addImportPackage(commandVocabularyIterator.next());
					*/
				
				for(Iterator<String> objectVocabularyIterator = ObjectVocabulary.packageNames(); objectVocabularyIterator.hasNext(); )
					codeGenerator.addImportPackage(objectVocabularyIterator.next());
				
				JavaFileObject proxySource = null;
				if(isGenerateTests())					
					proxySource = getFiler().createSourceFile(packageName + "." + codeGenerator.getTesterName(), e);
				else
					proxySource = getFiler().createSourceFile(packageName + "." + codeGenerator.getImplementationName(), e);
				//getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Generating code to '" + proxySource.getName() + "'." );
				proxySourceWriter = proxySource.openWriter();
				
				// build the template data model for the interface methods
				// every method in the interface MUST be annotated with a 
				// FacadeRouterMethod annotation, else a compile error is generated
				List<? extends Element> childElements = e.getEnclosedElements();
				for(Element childElement : childElements)
				{
					if( childElement.getKind() == ElementKind.METHOD )
					{
						ExecutableElement methodElement = (ExecutableElement)childElement;
						FacadeRouterMethodCodeGenerator methodDescription = methodElement.accept(routerMethodVisitor, null);
						if(methodDescription != null)
						{
							codeGenerator.addMethod(methodDescription);
						}
						else
							getMessager().printMessage(
								javax.tools.Diagnostic.Kind.ERROR, 
								"The method '" + methodElement.toString() + "' was not annotated with FacadeRouterMethod and MUST be.", 
								e);
						// don't want to create annotations if generating tests facades
						if(!isGenerateTests())
						{
							FacadeRouterDataSourceMethodCodeGenerator dataSourceMethodDescription =
								methodElement.accept(routerDataSourceMethodVisitor, null);
							if(dataSourceMethodDescription != null)
							{
								if(dataSourceMethodDescription.getPackageName() == null || dataSourceMethodDescription.getPackageName().length() < 0)
									dataSourceMethodDescription.setPackageName(packageName);
								JavaFileObject dataSourceMethodSource = getFiler().createSourceFile(dataSourceMethodDescription.getPackageName() + "." + dataSourceMethodDescription.getCanonicalCommandClass().toStringAsImplementation(), e);
								Writer dataSourceMethodWriter = dataSourceMethodSource.openWriter(); 
								
								dataSourceMethodDescription.setWriter(dataSourceMethodWriter);
								codeGenerator.addDataSourceMethod(dataSourceMethodDescription);
							}
						}
					}
				}
				
				codeGenerator.generate(proxySourceWriter);
			} 
			catch (IOException x)
			{
				x.printStackTrace();
				getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, "IO Exception occured generating router facade proxy source.", e);
			} 
			catch (TemplateException x)
			{
				x.printStackTrace();
				getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, "IO Exception occured generating router facade proxy source.", e);
			}
			finally
			{
				try{proxySourceWriter.close();}catch(Throwable t){}
			}
			
			return (Void)null;
		}

	}
	
	// =============================================================================================
	// Router Method Related processing
	// =============================================================================================
	/**
	 * 
	 * @author vhaiswbeckec
	 *
	 */
	class FacadeRouterMethodVisitor
	extends AbstractFacadeRouterMethodVisitor<FacadeRouterMethodCodeGenerator, Void>
	{
		FacadeRouterMethodVisitor()
		{
		}

		/**
		 * This method should be called when the annotation processor has found a
		 * method in the router facade interface.
		 * This method finds the FacadeRouterMethod annotation and creates a Map
		 * used to generate the method implementation.
		 */
		@Override
		public FacadeRouterMethodCodeGenerator visitExecutable(ExecutableElement methodElement, Void p)
		{
			// ex: methodElement.toString() is something like:
			// "public abstract SiteConnectivityStatus isSiteAvailable(String siteNumber);"
			
			getMessager().printMessage(
				javax.tools.Diagnostic.Kind.NOTE, 
				"Creating code generator for element " + methodElement.toString(), 
				methodElement);
			
			// the data map that contains the needed elements to generate the method implementation
			FacadeRouterMethodCodeGenerator methodDescription = null;
			
			List<? extends AnnotationMirror> childElementAnnotationMirrors = methodElement.getAnnotationMirrors();
			for( AnnotationMirror childElementAnnotationMirror : childElementAnnotationMirrors )
			{
				DeclaredType annotationType = childElementAnnotationMirror.getAnnotationType();
				// The following line produces output like this (line breaks added for clarity):
				// ClinicalDisplayRouter.java:36: 
				// Note: The method 'isSiteAvailable(java.lang.String)' is annotated with 
				// 'gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterMethod'.
				// public abstract SiteConnectivityStatus isSiteAvailable(String siteNumber);
				//getMessager().printMessage(
				//	javax.tools.Diagnostic.Kind.NOTE, 
				//	"The method '" + methodElement.toString() + "' is annotated with '" + annotationType.toString() + "'.", 
				//	methodElement);
				
				if( FacadeRouterMethod.class.getName().equals(annotationType.toString()) )
				{
					Map<? extends ExecutableElement, ? extends AnnotationValue> annotationProperties = 
						childElementAnnotationMirror.getElementValues();
					
					String resultType = null;
					String resultTypeCollectionType = null;
					String resultTypeCollectionMemberType = null;
					String resultTypeMapCollectionKeyType = null;
					String resultTypeMapCollectionValueType = null;
					boolean generateTypesafeCode = true;
					
					// methodName is the simple method name, ex: "isSiteAvailable"
					String methodName = methodElement.getSimpleName().toString();
					// get the declaration of the result type, if the result type includes type parameters (i.e. is a genericized class)
					// and the type is a Collection or an Iterator then we provide additional information
					// to the code generation
					TypeMirror resultTypeMirror = methodElement.getReturnType();
					resultType = resultTypeMirror.toString();
					
					// if the result is a declared type (i.e. not a primitive) then it could
					// be a collection type, which requires special code generation
					TypeKind resultTypeKind = resultTypeMirror.getKind();
					if(TypeKind.DECLARED == resultTypeKind)
					{
						// The declared result type is the type of the result, including any type params
						// if the result is a generic type
						// ex: if the annotated method returns List<Site> then the declared type
						// is List<Site>, the declaration (i.e. asElement()) is List<T>
						DeclaredType declaredResultType = (DeclaredType)resultTypeMirror;
						resultType = declaredResultType.toString();
						
						getMessager().printMessage(Diagnostic.Kind.NOTE, "Annotated router facade method result type is '" + declaredResultType.toString() + "'.");
						List<? extends TypeMirror> resultTypeArguments = declaredResultType.getTypeArguments();
						
						// these two variables will contain what gets written into the generated code
						// 1.) where the result type is not a genericized type the resultType is
						// all that is needed.
						// 2.) where the result type is a genericized type and is of a known collection type
						// then the resultType is the collection type and resultTypeCollectionMemberType is
						// the type argument
						// 3.) where the result type is a genericized type and is not a known collection type
						// we generate non-typesafe code, the result type contains the entire declaration
						
						// The Element of the declared result type is the result type class without the
						// type parameters, ex: List
						if(resultTypeArguments != null && resultTypeArguments.size() > 0)
						{
							Element resultTypeElement = declaredResultType.asElement();
							
							CollectionTypeVocabulary ctv = CollectionTypeVocabulary.find(resultTypeElement);
							if(ctv != null && resultTypeArguments.size() == ctv.getTypeArgumentsCount())
							{
								// the happy path for results that are collections
								resultTypeCollectionType = resultTypeElement.toString();
								if(ctv.getTypeArgumentsCount() == 1)
								{
									resultTypeCollectionMemberType = resultTypeArguments.get(0).toString();
									getMessager().printMessage(Diagnostic.Kind.NOTE, "Generating typesafe code for a collection type method of type " +
										resultTypeCollectionType + "<" + resultTypeCollectionMemberType + ">." );
								}
								else if(ctv.getTypeArgumentsCount() == 2)
								{
									resultTypeMapCollectionKeyType = resultTypeArguments.get(0).toString();
									resultTypeMapCollectionValueType = resultTypeArguments.get(1).toString();
									getMessager().printMessage(Diagnostic.Kind.NOTE, "Generating typesafe code for a collection type method of type " +
										resultTypeCollectionType + "<" + resultTypeMapCollectionKeyType + "," + resultTypeMapCollectionValueType + ">." );
								}
							}
							else
							{
								// the result type is a generic but it is not one of our known collection
								// types so just pass it through and hope that the caller knows what they are doing
								generateTypesafeCode = false;
								getMessager().printMessage(Diagnostic.Kind.NOTE, "Generating non-typesafe code for a method of type " +
										resultType + ".  The collection type '" + resultTypeElement + "' is not a known collection type." );
							}
						}
						else
							getMessager().printMessage(Diagnostic.Kind.NOTE, "Generating typesafe code for a method of type " +
									resultType + "." );
					}
					else
						getMessager().printMessage(Diagnostic.Kind.NOTE, "Generating typesafe code for a (primitive) method of type " +
								resultType + "." );
					
					String annotationCommandClassName = null;	// default value
					boolean annotationAsynchronous = false;	// default value
					String asynchListenerParameterName = null;
					boolean isChildCommand = false;	// default value
					long delay = 0L;
					int priority = 2;
					boolean isPeriodic = false;
					int periodicExecutionDelay = 60000;
					String commandPackage = "";
					
					// look through the annotation properties and move the values
					// to the method description properties
					for( ExecutableElement annotationProperty : annotationProperties.keySet() )
					{
						//getMessager().printMessage(
						//		javax.tools.Diagnostic.Kind.NOTE, 
						//		methodName + "@FacadeRouterMethod (" + annotationProperty.toString() + "=" + annotationProperties.get(annotationProperty).toString() + ")", 
						//		methodElement);
						// will be a reference to the Class of the command
						if( "commandClassName()".equals(annotationProperty.toString()) )
						{
							annotationCommandClassName = annotationProperties.get(annotationProperty).toString();
							annotationCommandClassName = stripSuperfluosAnnotationGarbage(annotationCommandClassName);
						}
						if( "asynchronous()".equals(annotationProperty.toString()) )
							annotationAsynchronous = Boolean.valueOf( annotationProperties.get(annotationProperty).toString() );
						
						if( "asynchronousCommandResultListenerParameterName()".equals(annotationProperty.toString()) )
						{
							asynchListenerParameterName = annotationProperties.get(annotationProperty).toString();
							asynchListenerParameterName = 
								FacadeRouterAnnotationProcessor.stripSuperfluosAnnotationGarbage(asynchListenerParameterName);
						}
						if( "delay()".equals(annotationProperty.toString()) )
						{
							//AnnotationValue av = annotationProperties.get(annotationProperty);
							//System.out.println(av.getClass() + " - " +  av.toString() + " - " + av.getValue().toString());
							
							delay = Long.valueOf( annotationProperties.get(annotationProperty).getValue().toString() );
						}
						
						if( "priority()".equals(annotationProperty.toString()) )
							priority = Integer.valueOf( annotationProperties.get(annotationProperty).toString() );
						
						if( "isChildCommand()".equals(annotationProperty.toString()) )
							isChildCommand = Boolean.valueOf( annotationProperties.get(annotationProperty).toString() );
						if("commandPackage()".equals(annotationProperty.toString()))
						{
							commandPackage = annotationProperties.get(annotationProperty).toString();
							commandPackage = stripSuperfluosAnnotationGarbage(commandPackage);
						}
						if("isPeriodic()".equals(annotationProperty.toString()))
						{
							isPeriodic = Boolean.valueOf(annotationProperties.get(annotationProperty).toString() );
						}
						if("periodicExecutionDelay()".equals(annotationProperty.toString()))
						{
							periodicExecutionDelay = Integer.valueOf(annotationProperties.get(annotationProperty).toString() );
						}
						
					}

					// methods defined previous to the semantic-router must be mapped to the
					// semantics defined for the semantic router
					// if the method name is not mapped then findMappedMethodName simply returns
					// the method name passed
					String mappedMethodName = findMappedMethodName(methodName);
					RouterMethodSemantics normalizedMethod = null;
					CommandClassSemantics normalizedCommandClass = null;
					
					try
					{
						if(annotationCommandClassName == null || annotationCommandClassName.length() < 1)
						{
							//getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Normalizing method name '" + mappedMethodName + "'.");
							normalizedMethod = RouterMethodSemantics.create(mappedMethodName);
							//getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Method name '" + mappedMethodName + "' normalized to '" + normalizedMethod.toString() + "'.");
							normalizedCommandClass = CommandClassSemantics.transformFrom(normalizedMethod);
							//getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Normalized method '" + normalizedMethod.toString() + "' transformed to '" + normalizedCommandClass.toString() + "'.");
						}
						else
						{
							//getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Method name '" + mappedMethodName + "' annotated with commandClassName '" + annotationCommandClassName + "'.");
							normalizedMethod = null;	// this doesn't do anything except remind us that the normalized method is null
							normalizedCommandClass = CommandClassSemantics.create(annotationCommandClassName, null);
							//getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Annotation commandClassName '" + annotationCommandClassName + "' used to create '" + normalizedCommandClass.toString() + "'.");
						}
						
						//getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Normalized command class '" + normalizedCommandClass.toString() + "' transformed to factory method '" + normalizedCommandFactoryMethod.toString() + "'.");
						
						// if the method returns a Map then the code generation is slightly different
						methodDescription = resultTypeMapCollectionKeyType != null && resultTypeMapCollectionValueType != null ?
							new FacadeRouterMethodCodeGenerator( 
								getMessager(),
								resultType,
								resultTypeCollectionType,
								resultTypeMapCollectionKeyType,
								resultTypeMapCollectionValueType,
								generateTypesafeCode,
								methodName, 
								normalizedMethod, 
								normalizedCommandClass, 
								annotationAsynchronous,
								asynchListenerParameterName,
								delay,
								priority,
								isChildCommand,
								isGeneratingMockImplementation(),
								generateTests,
								commandPackage,
								isPeriodic,
								periodicExecutionDelay)
							:
							new FacadeRouterMethodCodeGenerator( 
								getMessager(),
								resultType,
								resultTypeCollectionType,
								resultTypeCollectionMemberType,
								generateTypesafeCode,
								methodName, 
								normalizedMethod, 
								normalizedCommandClass, 
								annotationAsynchronous,
								asynchListenerParameterName,
								delay,
								priority,
								isChildCommand,
								isGeneratingMockImplementation(),
								generateTests,
								commandPackage, 
								isPeriodic, 
								periodicExecutionDelay)
						;
						
						// 
						for(VariableElement parameterElement : methodElement.getParameters() )
						{
							String parameterName = parameterElement.getSimpleName().toString();
							String parameterType = parameterElement.asType().toString(); 
							
							methodDescription.addRouterMethodParameter(
								parameterName, 
								parameterType
							);
							
							// the asynchListenerParameterName parameter does not get added to the parameter list
							// of the command factory call
							if(asynchListenerParameterName == null || !asynchListenerParameterName.equals(parameterName))
							{
								// The command factor type name does not include the type of a 
								// generic class (e.g. List, not List<String>
								// The generated code will look like:
								// new Class<?>[]{java.util.List.class}, which is legal.
								// NOT like:
								// new Class<?>[]{java.util.List.class<java.lang.String>} which is not legal
								int typeVariableStartIndex = parameterType.indexOf('<');
								if(typeVariableStartIndex >= 0)
									parameterType = parameterType.substring(0, typeVariableStartIndex);
								methodDescription.addCommandFactoryMethodParameter(parameterName, parameterType);
							}
						}
					} 
					catch (CoreRouterSemanticsException x)
					{
						getMessager().printMessage(
							javax.tools.Diagnostic.Kind.ERROR, 
							x.getMessage(), 
							methodElement);
					}

					getMessager().printMessage(
						javax.tools.Diagnostic.Kind.WARNING, 
						"Method '" + (methodDescription == null ? "<null>" : methodDescription.toString()) + "' should be generated as shown.", 
						methodElement);

					break;
				}
			}
			
			return methodDescription;
		}
	}
	
	/**
	 * @param annotationCommandClassName
	 * @return
	 */
	public static String stripSuperfluosAnnotationGarbage(String attributeValue)
	{
		attributeValue = attributeValue.replace('"', ' ');
		
		return attributeValue.trim();
	}
}