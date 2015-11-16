/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 12, 2012
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
package gov.va.med.imaging.federation.codegenerator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author VHAISWWERFEJ
 *
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface FederationGeneratedDataSourceProxyMethod
{
	
	/**
	 * The URI of the REST method to call
	 * @return
	 */
	public String federationMethodUri();
	
	public String translationResultMethodName() default "";
	
	public String translationResultMethodParameters() default "";
	
	/**
	 * Comma separated list of objects to output that describe the input parameters. This property is translated into Java code
	 * so a method may be called on one of these parameters. For example: imageId,routingToken.toRoutingTokenString() is valid
	 * 
	 * The parameters can also be separated by colons (:) to provide a description. For example: routingToken.toRoutingTokenString(),image identifier:imageId
	 * 
	 * @return
	 */
	public String inputParametersDescription() default "";
	
	/**
	 * The type of the object returned from the RESTful call
	 * @return
	 */
	public String federationReturnType();
	
	/**
	 * A comma separated list of property name:property values. The value may be simply the property or it may be a method called on the property
	 * 
	 * For example: routingToken:globalRoutingToken.toRoutingTokenString(),userId:userId
	 * @return
	 */
	public String restQueryParameters();
	
	public String postParameter() default "";
	
	/**
	 * A semicolon separated list of property name/translation method/output variables to convert input parameters before making
	 * the RESTful call.
	 * 
	 * If a method requires multiple input parameters they can be separated by a comma
	 * 
	 * For example: xml:RestCoreTranslator.translateStrings:translatedXml - this will call translate on the input xml input variable and return a variable called translatedXml
	 * 
	 * @return
	 */
	public String translateInputParametersNameAndMethod() default "";
	

}
