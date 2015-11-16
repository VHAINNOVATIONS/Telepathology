 	/*
 	* Generated method, modifications will be overwritten when project is built
 	*
 	* The template for this method may be found in FacadeRouterAsynchronousMethodImpl.ftl
 	*/
 	@SuppressWarnings("unchecked")
 	public void ${name}(
 		<#assign firstParameter=true>
 		<#list parameters as parameter>
 			<#if !firstParameter>, </#if>${parameter.type} ${parameter.name}
 			<#assign firstParameter=false> 
 		</#list>
 		) {
 			Command<?> command = null;
 			
	 		command = getCommandFactory().createCommand(
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
		  		
			<#if listener??>command.addListener(${listener});</#if>
			<#if accessibilityDate??>command.setAccessibilityDate(${accessibilityDate});</#if>
			<#if priority??>command.setPriority(${priority});</#if>
			<#if delay??>command.setAccessibilityDate( new Date(System.currentTimeMillis() + ${delay}) );</#if>
			<#if processingDurationEstimate??>command.setProcessingDurationEstimate(${processingDurationEstimate});</#if>
			gov.va.med.imaging.transactioncontext.TransactionContext transactionContext =
				  gov.va.med.imaging.transactioncontext.TransactionContextFactory.get();
	      <#if isChildCommand=="true">
	      			command.setChildCommand(true);
		    command.setParentCommandIdString(transactionContext.getCommandId());
	      </#if>
	      <#if periodic =="true">
			command.setPeriodicExecutionDelay(${periodicExecutionDelay});
			command.setPeriodic(true);
	      </#if>
			
			getRouter().doAsynchronously(command);
			return;
			
 		}
