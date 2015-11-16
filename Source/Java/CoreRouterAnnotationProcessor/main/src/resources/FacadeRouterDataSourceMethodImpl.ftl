/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: May 10, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWWERFEJ
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
 package ${package};
 
 import java.util.Date;
 import org.apache.log4j.*;
 import gov.va.med.imaging.*;
 import gov.va.med.imaging.core.interfaces.exceptions.*;
 import gov.va.med.imaging.datasource.*;
 import gov.va.med.imaging.core.interfaces.router.Command;
 import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResultListener;
 import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
 import javax.annotation.Generated;
 
 @SuppressWarnings("unused")
 public class ${commandClassName}
 <#if baseDataSourceCommand = ""> 
 extends AbstractDataSourceCommandImpl<${type},${dataSourceSpi}>
 <#else>
 extends ${baseDataSourceCommand}<${type}>
 </#if>
 {
 	private final static long serialVersionUID = 1L;
 
 	<#list parameters as parameter>
 	private final ${parameter.type} ${parameter.name};
 	</#list>
 
 	public ${commandClassName}(<#assign firstParameter=true>
 		<#list parameters as parameter>
 			<#if !firstParameter>, </#if>${parameter.type} ${parameter.name}
 			<#assign firstParameter=false> 
 		</#list>)
 	{
 		<#list parameters as parameter>
	 	this.${parameter.name} = ${parameter.name};	 	
	 	</#list>
 	}
 	
 	@Override
	protected ${type} getCommandResult(${dataSourceSpi} spi)
	throws ConnectionException, MethodException
	{
		<#if spiParameterNames = "">
			<#if declaredType=="void">
				spi.${dataSourceMethod}(<#assign firstParameter=true>
				<#list factoryParameters as parameter>
		 			<#if !firstParameter>, </#if>${parameter.name}
		 			<#assign firstParameter=false> 
		 		</#list>);
		 		return (java.lang.Void)null;
			<#else>
			return spi.${dataSourceMethod}(<#assign firstParameter=true>
				<#list factoryParameters as parameter>
		 			<#if !firstParameter>, </#if>${parameter.name}
		 			<#assign firstParameter=false> 
		 		</#list>);
	 		</#if>
	 	<#else>
	 		<#if declaredType=="void">
				spi.${dataSourceMethod}(${spiParameterNames});
				return (java.lang.Void)null;
			<#else>
	 			return spi.${dataSourceMethod}(${spiParameterNames});
	 		</#if>
	 	</#if>
	}
	
	<#if baseDataSourceCommand = "">
	@Override
	public gov.va.med.RoutingToken getRoutingToken()
	{
		return ${routingTokenParameterName};	
	}

	@Override
	protected String getSiteNumber()
	{
		return getRoutingToken().getRepositoryUniqueId();
	}
	</#if> 	

	@Override
	protected Class<${dataSourceSpi}> getSpiClass()
	{
		return ${dataSourceSpi}.class;
	}
 
 	@Override
	protected String getSpiMethodName()
	{
		return "${dataSourceMethod}";
	}
	
	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{			
	 	<#if spiParameterNames = "">
		return new Class<?> [] {
		<#assign firstParameter=true>
			<#list factoryParameters as parameter>
	 			<#if !firstParameter>, </#if>${parameter.type}.class
	 			<#assign firstParameter=false> 
	 		</#list>
	 	};
	 	</#if>
	 	<#if spiParameterNames != "">
	 	return new Class<?> [] {${spiParameterTypes}};		
	 	</#if>
	}
	
	@Override
	protected Object[] getSpiMethodParameters()
	{
		<#if spiParameterNames = "">
		return new Object [] {
		<#assign firstParameter=true>
			<#list factoryParameters as parameter>
	 			<#if !firstParameter>, </#if>${parameter.name}
	 			<#assign firstParameter=false> 
	 		</#list>
	 	};	
	 	</#if>
	 	<#if spiParameterNames != "">
	 	return new Object [] {${spiParameterNames}};		
	 	</#if>
	}
	
	@Override
	protected ${type} postProcessResult(${type} result)
	{
		<#if postProcessingResultMethodName = "">
		return result;
		</#if>
		<#if postProcessingResultMethodName != "">
		return ${postProcessingResultMethodName}(result);
		</#if>
	}
 }
