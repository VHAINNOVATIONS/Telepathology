package gov.va.med.imaging.core.router.worklist;

import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterface;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterfaceCommandTester;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterMethod;
import gov.va.med.imaging.core.interfaces.FacadeRouter;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.WorkItem;
import gov.va.med.imaging.exchange.business.WorkItemFilter;
import gov.va.med.imaging.exchange.business.WorkItemTag;

import java.util.List;

/**
 * 
 * @author vhaiswlouthj
 *
 */
@FacadeRouterInterface
@FacadeRouterInterfaceCommandTester
public interface WorkListRouter 
extends FacadeRouter
{
	@FacadeRouterMethod
	public abstract WorkItem createWorkItem(WorkItem workItem)
	throws MethodException, ConnectionException;

	@FacadeRouterMethod
	public abstract List<WorkItem> getWorkItemList(WorkItemFilter filter) 
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod
	public abstract Boolean updateWorkItem(
			int workItemId, 
			String expectedStatus, 
			String newStatus, 
			String newMessage, 
			String updatingUser, 
			String updatingApplication) 
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod
	public abstract Boolean postWorkItemTags(
			int workItemId, 
			List<String> allowedStatuses, 
			List<WorkItemTag> newTags, 
			String updatedBy,
			String updatingApplication) 
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod
	public abstract Boolean deleteWorkItem(int id) 
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod
	public abstract WorkItem getAndTransitionWorkItem(
			int id, 
			String expectedStatus, 
			String newStatus, 
			String updatingUser, 
			String updatingApplication)
	throws MethodException, ConnectionException;

	@FacadeRouterMethod
	public abstract WorkItem getAndTransitionNextWorkItem(
			String type, 
			String expectedStatus, 
			String newStatus, 
			String updatingUser, 
			String updatingApplication)
	throws MethodException, ConnectionException;
}
