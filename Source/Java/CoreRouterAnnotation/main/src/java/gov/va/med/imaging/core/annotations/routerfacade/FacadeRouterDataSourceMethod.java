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
package gov.va.med.imaging.core.annotations.routerfacade;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation allows for the auto generation of code to implement data source commands. This should only be used for data source
 * commands and this annotation should be used in addition to (not instead of) the @FacadeRouterMethod annotation.
 * 
 * This annotation will cause the generation of actual command code.  Not implementations to call commands but actual commands
 * themselves
 * 
 * <b>Note:</b> Some of the parameters listed below duplicate the parameters in @FacadeRouterMethod.  This is unfortunate but required. 
 * The parameters must be specified (and the same) in both annotations
 * 
 * @author VHAISWWERFEJ
 *
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface FacadeRouterDataSourceMethod
{
	/**
	 * The name of the spi to call. If this value is omitted then baseDataSourceCommand MUST be included and it MUST provide the 
	 * SPI. 
	 * @return
	 */
	public String dataSourceSpi();
	
	/**
	 * the name of the method to call
	 * @return
	 */
	public String methodName();
	
	/**
	 * Optional parameter to specify the name of the RoutingToken parameter
	 * @return
	 */
	public String routingTokenParameterName() default "routingToken";
	
	/**
	 * Optional parameter to specify a method to call to get the routing token. If this value is provided, 
	 * then routingTokenParameterName is ignored (regardless of its value).
	 * @return
	 */
	//public String routingTokenMethod() default "";
	
	/**
	 * Optional parameter to specify a base datasource command to extend.  This base must descend from AbstractDataSourceCommandImpl.
	 * If this parameter is provided then routingTokenParameterName is ignored (regardless of its value) and it is assumed this
	 * base command implements the getRoutingToken() and getSiteNumber() methods. 
	 */
	public String baseDataSourceCommand() default "";
	
	/**
	 * The name of the command to create (not including the Impl portion of the command name)
	 * @return
	 */
	public String commandClassName() default "";
	
	/**
	 * The package to create the command in
	 * @return
	 */
	public String commandPackage() default "";
	
	/**
	 * Specify an available method to call to do post processing on the result. This method must already exist at compile time
	 * and it must take the same parameter type as this command returns and this method must return that type
	 * @return
	 */
	public String postProcessResultMethodName() default "";
	
	/**
	 * If the command parameters do not match the parameters for the SPI, specify the names of the parameters (in order)
	 * using this property. The parameter names listed here must be the same names as the command parameters	
	 * 
	 * @return
	 */
	public String spiParameterNames() default "";

}
