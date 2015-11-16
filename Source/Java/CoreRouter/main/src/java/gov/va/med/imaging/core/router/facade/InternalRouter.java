package gov.va.med.imaging.core.router.facade;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterface;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterfaceCommandTester;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterMethod;
import gov.va.med.imaging.core.interfaces.FacadeRouter;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.AuditEvent;
import gov.va.med.imaging.exchange.business.DurableQueue;
import gov.va.med.imaging.exchange.business.DurableQueueMessage;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.exchange.business.ServiceRegistration;
import gov.va.med.imaging.exchange.business.WelcomeMessage;
import gov.va.med.imaging.veins.ErrorTypeNotificationConfiguration;

import java.util.List;
import java.util.Set;

/**
 * 
 * @author vhaiswlouthj
 *
 */
@FacadeRouterInterface
@FacadeRouterInterfaceCommandTester
public interface InternalRouter 
extends FacadeRouter
{
	


	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetTreatingSitesCommand")
	public abstract List<ResolvedArtifactSource> getTreatingSites(RoutingToken routingToken, PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetTreatingSitesFromDataSourceCommand")
	public abstract List<String> getTreatingSitesFromDataSource(RoutingToken routingToken, PatientIdentifier patientIdentifier, 
			boolean includeTrailingCharactersForSite200)
	throws MethodException, ConnectionException;

	
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetResolvedArtifactSourceCommand")
	public abstract List<ResolvedArtifactSource> getResolvedArtifactSource(RoutingToken routingToken)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="DequeueDurableQueueMessageCommand", isChildCommand=true)
	public abstract DurableQueueMessage dequeueDurableQueueMessage(RoutingToken routingToken, int queueId)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="DequeueDurableQueueMessageCommand", isChildCommand=true)
	public abstract DurableQueueMessage dequeueDurableQueueMessage(RoutingToken routingToken, int queueId, String messageGroupId)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="EnqueueDurableQueueMessageCommand", isChildCommand=true)
	public abstract void enqueueDurableQueueMessage(RoutingToken routingToken, DurableQueueMessage message)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetDurableQueueMessageCommand", isChildCommand=true)
	public abstract DurableQueueMessage getDurableQueueMessage(RoutingToken routingToken, int queueId)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetDurableQueueMessageCommand", isChildCommand=true)
	public abstract DurableQueueMessage getDurableQueueMessage(RoutingToken routingToken, int queueId, String messageGroupId)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetDurableQueueMessageCountCommand", isChildCommand=true)
	public abstract Integer getDurableQueueMessageCount(RoutingToken routingToken, int queueId)
	throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, commandClassName="GetDurableQueueMessageCountCommand", isChildCommand=true)
	public abstract Integer getDurableQueueMessageCount(RoutingToken routingToken, int queueId, String messageGroupId)
	throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous = false, commandClassName = "GetDurableQueueMessagesCommand", isChildCommand = false)
	public abstract List<DurableQueueMessage> getDurableQueueMessages(
			RoutingToken routingToken, int queueId, int startIndex,
			int numRecords) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous = false, commandClassName = "GetDurableQueueMessagesCommand", isChildCommand = false)
	public abstract List<DurableQueueMessage> getDurableQueueMessages(
			RoutingToken routingToken, int queueId, String messageGroupId,
			int startIndex, int numRecords) throws MethodException,
			ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetAllDurableQueuesCommand", isChildCommand=true)
	public abstract List<DurableQueue> getAllDurableQueues(RoutingToken routingToken)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetDurableQueueByNameCommand", isChildCommand=true)
	public abstract DurableQueue getDurableQueueByName(RoutingToken routingToken, String queueName)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous = false, commandClassName = "PostMoveDurableQueueMessageCommand", isChildCommand = true)
	public abstract Boolean postMoveDurableQueueMessage(
			RoutingToken routingToken, int messageId, int targetQueueId)
			throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous = false, commandClassName = "PostMoveAllDurableQueueMessagesCommand", isChildCommand = true)
	public abstract Boolean postMoveAllDurableQueueMessages(
			RoutingToken routingToken, int sourceQueueId, int targetQueueId)
			throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous = false, commandClassName = "PostMoveAllDurableQueueMessagesCommand", isChildCommand = true)
	public abstract Boolean postMoveAllDurableQueueMessages(
			RoutingToken routingToken, int sourceQueueId, int targetQueueId, String messageGroupId)
			throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous = false, commandClassName = "PostDurableQueueCommand", isChildCommand = true)
	public abstract Boolean postDurableQueue(RoutingToken routingToken, DurableQueue queue) throws MethodException,
			ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostServiceRegistrationCommand", isChildCommand=true)
	public abstract Boolean postServiceRegistration(RoutingToken routingToken, ServiceRegistration registration)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PutServiceRegistrationCommand", isChildCommand=true)
	public abstract ServiceRegistration putServiceRegistration(RoutingToken routingToken, ServiceRegistration registration)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="DeleteServiceRegistrationCommand", isChildCommand=true)
	public abstract Boolean deleteServiceRegistration(RoutingToken routingToken, ServiceRegistration registration)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetServiceRegistrationCommand", isChildCommand=true)
	public abstract ServiceRegistration getServiceRegistration(RoutingToken routingToken, int id)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetServiceRegistrationByServiceIdCommand", isChildCommand=true)
	public abstract List<ServiceRegistration> getServiceRegistrationByServiceId(RoutingToken routingToken, String serviceId)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="ProcessServiceRegistrationCommand", isChildCommand=true)
	public abstract Boolean processServiceRegistration(RoutingToken routingToken, ServiceRegistration serviceRegistration)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetAllErrorTypeNotificationConfigurationCommand", isChildCommand=true)
	public abstract List<ErrorTypeNotificationConfiguration> getAllErrorTypeNotificationConfiguration(RoutingToken routingToken)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetErrorTypeNotificationConfigurationCommand", isChildCommand=true)
	public abstract ErrorTypeNotificationConfiguration getErrorTypeNotificationConfiguration(RoutingToken routingToken, String errorType)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostErrorTypeNotificationConfigurationCommand", isChildCommand=true)
	public abstract Boolean postErrorTypeNotificationConfiguration(RoutingToken routingToken, ErrorTypeNotificationConfiguration config)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=true, isChildCommand=true, commandClassName="ProcessSiteServiceCacheRefreshAsyncCommand")
	public abstract void processSiteServiceCacheRefresh()
	throws ConnectionException, MethodException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="PostAuditEventCommand")
	public abstract Boolean postAuditEvent(RoutingToken routingToken, AuditEvent auditEvent)
	throws ConnectionException, MethodException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	public abstract List<String> getUserKeys(RoutingToken routingToken)
	throws ConnectionException, MethodException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetPatientSetByNameFromDataSourceCommand")
	public abstract Set<Patient> getPatientsByName(RoutingToken routingToken, String searchName)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetWelcomeMessageBySiteCommand")
	public abstract WelcomeMessage getWelcomeMessageFromSite(RoutingToken routingToken)
	throws MethodException, ConnectionException;
}
