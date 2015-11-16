 	/*
 	* Generated method, modifications will be overwritten when project is built
 	*
 	* The template for this method may be found in FacadeRouterSynchronousCollectionMethodImpl.ftl
 	*/
 	@SuppressWarnings("unchecked")
 	public ${declaredType} ${name}(
 		<#assign firstParameter=true>
 		<#list parameters as parameter>
 			<#if !firstParameter>, </#if>${parameter.type} ${parameter.name}
 			<#assign firstParameter=false> 
 		</#list> ) 
 		throws MethodException, ConnectionException
 		{
 			Command<${collectionType}> command = getCommandFactory().createMapCollectionCommand(
 				 ${collectionType}.class, 
 				 ${mapKeyType}.class, 
 				 ${mapValueType}.class, 
 				"${commandClassName}",
 				"${commandPackage}",
				new Class<?>[]{
				 	<#assign firstType=true><#list factoryParameters as parameter><#if !firstType>, </#if>${parameter.type}.class<#assign firstType=false></#list> 
	  			},
				new Object[]{
			 		<#assign firstParameter=true><#list factoryParameters as parameter><#if !firstParameter>,</#if>${parameter.name}<#assign firstParameter=false></#list>
	  			}
	  		);
  		
			gov.va.med.imaging.transactioncontext.TransactionContext transactionContext =
			  gov.va.med.imaging.transactioncontext.TransactionContextFactory.get();
			//transactionContext.setCommandClassName("${commandClassName}");
		
      <#if isChildCommand=="true">
      			//transactionContext.setParentCommandId(transactionContext.getCommandId());
      			command.setChildCommand(true);
      			command.setParentCommandIdString(transactionContext.getCommandId());
      </#if>
      <#if periodic =="true">
		command.setPriority(${priority});
		command.setPeriodicExecutionDelay(${periodicExecutionDelay});
		command.setPeriodic(true);
      </#if>
      
			${type} commandResult = getRouter().doSynchronously(command);
			return commandResult;
 		}
