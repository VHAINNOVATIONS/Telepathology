package gov.va.med.imaging.datasource;

// import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterMethod;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.exchange.business.WorkItem;
import gov.va.med.imaging.exchange.business.WorkItemFilter;
import gov.va.med.imaging.exchange.business.WorkItemTag;
// import gov.va.med.imaging.exchange.business.WorkItemTags;
import gov.va.med.imaging.exchange.business.dicom.StorageCommitWorkItem;

import java.util.List;

/**
 * This class defines the Service Provider Interface (SPI) for the DicomImporterDataSource class. 
 * All the abstract methods in this class must be implemented by each 
 * data source service provider who wishes to supply the implementation of a 
 * DicomDataSource for a particular datasource type.
 * 
 * @since 1.0
 * @author vhaiswlouthj
 *
 */
@SPI(description="The service provider interface for DICOM storage")
public interface WorkListDataSourceSpi 
extends VersionableDataSourceSpi
{
	/**
	 * Creates a new work item
	 * @param workItem
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract WorkItem createWorkItem(WorkItem workItem)
	throws MethodException, ConnectionException;

	/**
	 * Gets the list of work items that match the provided filter
	 * @param filter
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract List<WorkItem> getWorkItemList(WorkItemFilter filter) 
	throws MethodException, ConnectionException;

	/**
	 * Updates a work item
	 * @param workItemId
	 * @param expectedStatus
	 * @param newStatus
	 * @param newMessage
	 * @param updatingUser
	 * @param updatingApplication
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract boolean updateWorkItem(
			int workItemId, 
			String expectedStatus, 
			String newStatus, 
			String newMessage, 
			String updatingUser, 
			String updatingApplication) 
	throws MethodException, ConnectionException;
	
	/**
	 * Adds work item tags to an existing work item, as long as the work item is still
	 * in one of the allowed statuses
	 * @param workItemId
	 * @param allowedStatuses
	 * @param newTags
	 * @param updatedBy
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract boolean postWorkItemTags(
			int workItemId, 
			List<String> allowedStatuses, 
			List<WorkItemTag> newTags, 
			String updatingUser, 
			String updatingApplication) 
	throws MethodException, ConnectionException;
	
	/**
	 * Deletes a work item by id
	 * @param id
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract boolean deleteWorkItem(int id) 
	throws MethodException, ConnectionException;
	
	/**
	 * Gets a work item by ID and transitions it to the new status, but only if it
	 * is still in the expected status. Otherwise, throws an exception.
	 * @param id
	 * @param expectedStatus
	 * @param newStatus
	 * @param updatingUser
	 * @param updatingApplication
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract WorkItem getAndTransitionWorkItem(
			int id, 
			String expectedStatus, 
			String newStatus, 
			String updatingUser, 
			String updatingApplication)
	throws MethodException, ConnectionException;

	/**
	 * Gets the oldest work item of a given type in the specified status (if any exist), and 
	 * updates it to the new status. 
	 * @param type
	 * @param expectedStatus
	 * @param newStatus
	 * @param updatingUser
	 * @param updatingApplication
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract WorkItem getAndTransitionNextWorkItem(
			String type, 
			String expectedStatus, 
			String newStatus, 
			String updatingUser, 
			String updatingApplication,
			String placeId)
	throws MethodException, ConnectionException;

	/**
	 * Creates a new storage commit work item
	 * @param StorageCommitWorkItem input SC WI structure
	 * @return StorageCommitWorkItem stored SC WI structure
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract StorageCommitWorkItem createSCWorkItem(StorageCommitWorkItem scWI)
	throws MethodException, ConnectionException;

	/**
	 * Lists existing storage commit work items for given host name. If host name is empty,
	 * returns all SC Work Items.
	 * @param hostname node name of local machine
	 * @return List<StorageCommitWorkItem>
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract List<StorageCommitWorkItem> listSCWorkItems(String hostname) 
	throws MethodException, ConnectionException; 

	/**
	 * gets an existing storage commit work item. Optionally processes it before return
	 * @param scWIID the Work Item ID of the SC WI
	 * #param doProcess flag to request processing of WI before return
	 * @return StorageCommitWorkItem
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract StorageCommitWorkItem getSCWorkItem(String scWIID, boolean doProcess) 
	throws MethodException, ConnectionException; 

	/**
	 * update an existing storage commit work item's status.
	 * @param scWIID the Work Item ID of the SC WI
	 * #param status the existing SC WI status to be updated with
	 * @return boolean
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract boolean updateSCWorkItemStatus(String scWIID, String status)
	throws MethodException, ConnectionException; 

	/**
	 * gets an existing storage commit work item. Optionally processes it before return
	 * @param scWIID the Work Item ID of the SC WI
	 * #param doProcess flag to request processing of WI before return
	 * @return boolean
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract boolean deleteSCWorkItem(String scWIID) 
	throws MethodException, ConnectionException; 
}
