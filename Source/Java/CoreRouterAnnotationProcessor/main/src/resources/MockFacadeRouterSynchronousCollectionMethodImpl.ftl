 	/*
 	* Generated method, modifications will be overwritten when project is built
 	* This is a MOCK implementation of a synchronous facade router call.  This
 	* should never be deployed in a production VIX.
 	* This method returns either randomly generated data, default data or throws
 	* an exception depending on the configured mode.
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
			return (${declaredType})createCollectionInstance( ${collectionType}.class, ${collectionMemberType}.class );
 		}
