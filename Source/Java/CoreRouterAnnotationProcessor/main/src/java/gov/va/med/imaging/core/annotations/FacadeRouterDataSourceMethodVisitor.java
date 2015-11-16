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
package gov.va.med.imaging.core.annotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gov.va.med.imaging.core.CollectionTypeVocabulary;
import gov.va.med.imaging.core.CommandClassSemantics;
import gov.va.med.imaging.core.CoreRouterSemanticsException;
import gov.va.med.imaging.core.RouterMethodSemantics;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterDataSourceMethod;
import gov.va.med.imaging.core.codegenerator.FacadeRouterDataSourceMethodCodeGenerator;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * @author VHAISWWERFEJ
 *
 */
public class FacadeRouterDataSourceMethodVisitor
extends AbstractFacadeRouterMethodVisitor<FacadeRouterDataSourceMethodCodeGenerator, Void>
{
	private final Messager messager;
	
	public FacadeRouterDataSourceMethodVisitor(Messager messager)
	{
		this.messager = messager;
	}

	public Messager getMessager()
	{
		return messager;
	}

	@Override
	public FacadeRouterDataSourceMethodCodeGenerator visitExecutable(
			ExecutableElement methodElement, Void p)
	{
		getMessager().printMessage(
				javax.tools.Diagnostic.Kind.NOTE, 
				"Creating code generator for element " + methodElement.toString(), 
				methodElement);
			
			// the data map that contains the needed elements to generate the method implementation
		FacadeRouterDataSourceMethodCodeGenerator methodDescription = null;
			
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
				
				if( FacadeRouterDataSourceMethod.class.getName().equals(annotationType.toString()) )
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
					
					String dataSourceSpi = null;	// default value
					String dataSourceMethod = null;
					String annotationCommandClassName = null;	// default value
					String routingTokenParameterName = "routingToken"; // defaultValue
					String commandPackage = null;
					String postProcessResultMethodName = "";
					String spiParameterNames = "";		
					String baseDataSourceCommand = "";
					
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
						if( "dataSourceSpi()".equals(annotationProperty.toString()) )
						{
							dataSourceSpi = annotationProperties.get(annotationProperty).toString();
							dataSourceSpi = stripSuperfluosAnnotationGarbage(dataSourceSpi);
						}
						if( "methodName()".equals(annotationProperty.toString()) )
						{
							dataSourceMethod = annotationProperties.get(annotationProperty).toString();
							dataSourceMethod = stripSuperfluosAnnotationGarbage(dataSourceMethod);
						}	
						if( "routingTokenParameterName()".equals(annotationProperty.toString()) )
						{
							routingTokenParameterName = annotationProperties.get(annotationProperty).toString();
							routingTokenParameterName = stripSuperfluosAnnotationGarbage(routingTokenParameterName);
						}	
						if( "commandPackage()".equals(annotationProperty.toString()) )
						{
							commandPackage = annotationProperties.get(annotationProperty).toString();
							commandPackage = stripSuperfluosAnnotationGarbage(commandPackage);
						}
						if( "postProcessResultMethodName()".equals(annotationProperty.toString()) )
						{
							postProcessResultMethodName = annotationProperties.get(annotationProperty).toString();
							postProcessResultMethodName = stripSuperfluosAnnotationGarbage(postProcessResultMethodName);
						}	
						if( "spiParameterNames()".equals(annotationProperty.toString()) )
						{
							spiParameterNames = annotationProperties.get(annotationProperty).toString();
							spiParameterNames = stripSuperfluosAnnotationGarbage(spiParameterNames);
						}	
						if( "baseDataSourceCommand()".equals(annotationProperty.toString()) )
						{
							baseDataSourceCommand = annotationProperties.get(annotationProperty).toString();
							baseDataSourceCommand = stripSuperfluosAnnotationGarbage(baseDataSourceCommand);
						}
					}

					try
					{
						String mappedMethodName = findMappedMethodName(methodName);
						RouterMethodSemantics normalizedMethod = null;
						CommandClassSemantics normalizedCommandClass = null;
						if(annotationCommandClassName == null || annotationCommandClassName.length() < 1)
						{
							//getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Normalizing method name '" + mappedMethodName + "'.");
							normalizedMethod = RouterMethodSemantics.create(mappedMethodName);
							//getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Method name '" + mappedMethodName + "' normalized to '" + normalizedMethod.toString() + "'.");
							normalizedCommandClass = CommandClassSemantics.transformFrom(normalizedMethod, commandPackage);
							//getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Normalized method '" + normalizedMethod.toString() + "' transformed to '" + normalizedCommandClass.toString() + "'.");
						}
						else
						{
							//getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Method name '" + mappedMethodName + "' annotated with commandClassName '" + annotationCommandClassName + "'.");
							normalizedMethod = null;	// this doesn't do anything except remind us that the normalized method is null
							normalizedCommandClass = CommandClassSemantics.create(annotationCommandClassName, commandPackage);
							//getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Annotation commandClassName '" + annotationCommandClassName + "' used to create '" + normalizedCommandClass.toString() + "'.");
						}
							
						//getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Normalized command class '" + normalizedCommandClass.toString() + "' transformed to factory method '" + normalizedCommandFactoryMethod.toString() + "'.");
						
						// if the method returns a Map then the code generation is slightly different
						methodDescription = resultTypeMapCollectionKeyType != null && resultTypeMapCollectionValueType != null ?
							new FacadeRouterDataSourceMethodCodeGenerator( 
								getMessager(),
								resultType,
								resultTypeCollectionType,
								resultTypeMapCollectionKeyType,
								resultTypeMapCollectionValueType,
								generateTypesafeCode,
								dataSourceSpi, 
								dataSourceMethod,
								normalizedCommandClass)
							:
							new FacadeRouterDataSourceMethodCodeGenerator( 
								getMessager(),
								resultType,
								resultTypeCollectionType,
								resultTypeCollectionMemberType,
								generateTypesafeCode,
								dataSourceSpi, 
								dataSourceMethod,
								normalizedCommandClass);
					
						methodDescription.setRoutingTokenParameterName(routingTokenParameterName);
						methodDescription.setPostProcessResultMethodName(postProcessResultMethodName);
						methodDescription.setBaseDataSourceCommand(baseDataSourceCommand);
						
						List<ParameterDescription> specifiedParameters = null;
						if(spiParameterNames != null && spiParameterNames.length() > 0)
						{
							// must figure out parameter types from the ordered parameter names
							specifiedParameters = new ArrayList<FacadeRouterDataSourceMethodVisitor.ParameterDescription>();
							String [] parameterNames = spiParameterNames.split(",");
							for(String parameterName : parameterNames)
							{
								specifiedParameters.add(new ParameterDescription(parameterName.trim()));
							}
						}
												
						// 
						for(VariableElement parameterElement : methodElement.getParameters() )
						{
							String parameterName = parameterElement.getSimpleName().toString();
							String parameterType = parameterElement.asType().toString(); 
							
							methodDescription.addRouterMethodParameter(
								parameterName, 
								parameterType
							);
							
							// The command factor type name does not include the type of a 
							// generic class (e.g. List, not List<String>
							// The generated code will look like:
							// new Class<?>[]{java.util.List.class}, which is legal.
							// NOT like:
							// new Class<?>[]{java.util.List.class<java.lang.String>} which is not legal
							int typeVariableStartIndex = parameterType.indexOf('<');
							if(typeVariableStartIndex >= 0)
								parameterType = parameterType.substring(0, typeVariableStartIndex);
							methodDescription.addCommandFactoryMethodParameter(parameterName, 
									parameterType);
							
							
							if(specifiedParameters != null)
							{
								//System.out.println("Attempting to set parameter type for parameter '" + parameterName + "'.");
								for(ParameterDescription pd : specifiedParameters)
								{
									//System.out.println("Comparing parameter name to '" + pd.getParameterName() + "'.");
									if(pd.getParameterName().equals(parameterName))
									{
										pd.setParameterType(parameterType);
										//System.out.println("Setting parameter type to '" + parameterType + "'.");
										break;
									}
								}
							}							
						}
						
						StringBuilder spiParameterTypes = new StringBuilder();
						String prefix = "";
						if(specifiedParameters != null)
						{
							for(ParameterDescription pd : specifiedParameters)
							{
								spiParameterTypes.append(prefix);
								spiParameterTypes.append(pd.getParameterType());
								spiParameterTypes.append(".class");
								prefix = ",";
							}
						}
						
						methodDescription.setSpiParameterNamesAndTypes(spiParameterNames, spiParameterTypes.toString());
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
	
	/**
	 * @param annotationCommandClassName
	 * @return
	 */
	public static String stripSuperfluosAnnotationGarbage(String attributeValue)
	{
		attributeValue = attributeValue.replace('"', ' ');
		
		return attributeValue.trim();
	}
	
	class ParameterDescription
	{
		private final String parameterName;
		private String parameterType;
		
		public ParameterDescription(String parameterName)
		{
			this.parameterName = parameterName;
		}
		
		public String getParameterName()
		{
			return parameterName;
		}

		public String getParameterType()
		{
			return parameterType;
		}
		
		public void setParameterType(String parameterType)
		{
			this.parameterType = parameterType;
		}
	}
}
