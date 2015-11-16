 	/*
 	* Generated method, modifications will be overwritten when project is built
 	*
 	* The template for this method may be found in FacadeRouterSynchronousMethodImpl.ftl
 	*/
 	@SuppressWarnings("unchecked")
 	public void ${name}(
 		<#assign firstParameter=true>
 		<#list parameters as parameter>
 			<#if !firstParameter>, </#if>${parameter.type} ${parameter.name}
 			<#assign firstParameter=false> 
 		</#list> ) 
 		throws ValidationException
 		{
 			getLogger().info("Searching for command '${commandClassName}' and parameters '<#assign firstType=true><#list factoryParameters as parameter><#if !firstType>, </#if>${parameter.type}<#assign firstType=false></#list>'.");
			<#if declaredType=="void">
				boolean result = getCommandFactory().isCommandSupported(
	 				 ${type}.class, 
	 				"${commandClassName}",
	 				"${commandPackage}",
 					new Class<?>[]{
				 		 <#assign firstType=true><#list factoryParameters as parameter><#if !firstType>, </#if>${parameter.type}.class<#assign firstType=false></#list> 
		  			},
 					new Object[]{
				 		<#assign firstParameter=true><#list factoryParameters as parameter><#if !firstParameter>,</#if>${parameter.name}<#assign firstParameter=false></#list>
		  			}
		  		);
		  		if(!result)
		  			throw new ValidationException("Cannot create command '${commandClassName}'.");		  		
			<#else>
	 			boolean result = getCommandFactory().isCommandSupported(
	 				 ${type}.class, 
	 				"${commandClassName}",
	 				"${commandPackage}",
 					new Class<?>[]{
				 		 <#assign firstType=true><#list factoryParameters as parameter><#if !firstType>, </#if>${parameter.type}.class<#assign firstType=false></#list> 
		  			},
 					new Object[]{
				 		<#assign firstParameter=true><#list factoryParameters as parameter><#if !firstParameter>,</#if>${parameter.name}<#assign firstParameter=false></#list>
		  			}
		  		);
		  		if(!result)
		  			throw new ValidationException("Cannot create command '${commandClassName}' in method '${name}' with parameters '<#assign firstType=true><#list factoryParameters as parameter><#if !firstType>, </#if>${parameter.type}<#assign firstType=false></#list>'.");
		  	</#if>		  	
 		}
