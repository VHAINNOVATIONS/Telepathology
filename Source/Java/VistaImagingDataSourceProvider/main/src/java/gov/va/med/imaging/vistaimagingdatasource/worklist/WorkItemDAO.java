/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Nov, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
  Description: DICOM Study cache manager. Maintains the cache of study instances
  			   and expires old studies after 15 minutes. 

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

package gov.va.med.imaging.vistaimagingdatasource.worklist;

import gov.va.med.imaging.StringUtil;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.InvalidWorkItemStatusException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.WorkItemNotFoundException;
import gov.va.med.imaging.exchange.business.WorkItem;
import gov.va.med.imaging.exchange.business.WorkItemCounts;
import gov.va.med.imaging.exchange.business.WorkItemFilter;
import gov.va.med.imaging.exchange.business.WorkItemTag;
import gov.va.med.imaging.exchange.business.WorkItemTags;
import gov.va.med.imaging.exchange.business.dicom.DicomServerConfiguration;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.exchange.business.storage.exceptions.DeletionException;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.exchange.business.storage.exceptions.UpdateException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class WorkItemDAO extends BaseWorkListDAO<WorkItem>
{
	private static String CREATE_WORK_ITEM = "MAGV CREATE WORK ITEM";
	private static String GET_AND_TRANSITION_WORK_ITEM = "MAGV GET WORK ITEM";
	private static String GET_AND_TRANSITION_NEXT_WORK_ITEM = "MAGV GET NEXT WORK ITEM";
	private static String FIND_WORK_ITEMS = "MAGV FIND WORK ITEM";
	private static String UPDATE_WORK_ITEM = "MAGV UPDATE WORK ITEM";
	private static String ADD_TAG_TO_EXISTING_WORK_ITEM = "MAGV ADD WORK ITEM TAGS";
	private static String DELETE_WORK_ITEM = "MAGV DELETE WORK ITEM";
	private static String DELETE_ALL_WORK_ITEMS = "MAGV DELETE WORK ITEMS FILE";
	private static String GET_WORK_ITEM_COUNTS = "MAGV WORK ITEMS COUNT";
	
	private static String WorkItemHeader = "WorkItemHeader";
	private static String MessageLine = "Message";
	private static String Tag = "Tag";
	
	//
	// Constructor
	//
	public WorkItemDAO(){}
	public WorkItemDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	//
	// Creation overrides
	//
	@Override
	public VistaQuery generateCreateQuery(WorkItem workItem) 
	{
		
		VistaQuery vm = new VistaQuery(CREATE_WORK_ITEM);
		vm.addParameter(VistaQuery.LITERAL, workItem.getType());
		vm.addParameter(VistaQuery.LITERAL, workItem.getSubtype());
		vm.addParameter(VistaQuery.LITERAL, workItem.getStatus());
		vm.addParameter(VistaQuery.LITERAL, workItem.getPlaceId());
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(workItem.getPriority()));

		// Add the message lines and tags to a hashmap parameter
		HashMap<String, String> hm = getMessageLinesAndTagsAsHashMap(workItem.getMessage(), workItem.getTags());
		vm.addParameter(VistaQuery.LIST, hm);
		
		vm.addParameter(VistaQuery.LITERAL, workItem.getCreatingUser() + "");
		vm.addParameter(VistaQuery.LITERAL, workItem.getCreatingApplication() + "");

		return vm;
	}

	private HashMap<String, String> getMessageLinesAndTagsAsHashMap(String message, WorkItemTags workItemTags) 
	{

		// Store in a LinkedHashMap so order is preserved
		LinkedHashMap<String, String> hm = new LinkedHashMap<String, String>();

		int counter=1;

		// Break message up and add pieces to the hashmap
		if (message != null)
		{
			
			String[] messageParts = StringUtil.breakString(message, MAX_M_STRING_LENGTH-4);
			for (int i = 0; i < messageParts.length; i++) {
				hm.put(Integer.toString(counter), "MSG" + StringUtils.BACKTICK + messageParts[i]);
				counter++;
			}
		}
		
		// Add workitems to the hashmap
		if(workItemTags != null && workItemTags.getTags() != null )
		{
			List<WorkItemTag> tags = workItemTags.getTags();
			for (WorkItemTag tag : tags)
			{
				hm.put(Integer.toString(counter), tag.getKey() + StringUtils.BACKTICK + tag.getValue());
				counter++;
			}
		}		
		
		return hm;
	}

	//
	// Get and Transition Work Item
	//
	public WorkItem getAndTransitionWorkItem(int id, String expectedStatus, String newStatus, String updatingUser, String updatingApplication) 
	throws MethodException, ConnectionException
	{
		VistaQuery vm = new VistaQuery(GET_AND_TRANSITION_WORK_ITEM);
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(id));
		vm.addParameter(VistaQuery.LITERAL, expectedStatus);
		vm.addParameter(VistaQuery.LITERAL, newStatus);
		vm.addParameter(VistaQuery.LITERAL, updatingUser);
		vm.addParameter(VistaQuery.LITERAL, updatingApplication);
	
		String result = executeRPC(vm);
		
		return translateGetAndTransitionWorkItem(result);
	}

	private WorkItem translateGetAndTransitionWorkItem(String result) throws MethodException 
	{

		WorkItem workItem = null;
		
		// Split the result into lines
		String[] lines = StringUtils.Split(result, LINE_SEPARATOR);
		
		// Split the first line into fields
		String[] fields = StringUtils.Split(lines[0], FIELD_SEPARATOR);
		int code = Integer.parseInt(fields[0]);
		// If the code is not 0, throw an exception with the message
		if (code>=0)
		{
			// Success
			workItem = parseSingleWorkItem(lines);
		}
		else if (code == -5)
		{
			throw new WorkItemNotFoundException(fields[1]);
		}
		else if (code == -6)
		{
			throw new InvalidWorkItemStatusException(fields[1]);
		}
		else
		{
			throw new MethodException(fields[1]);
		}

		return workItem;
	}
	
	//
	// Get and Transition Next Work Item
	//
	public WorkItem getAndTransitionNextWorkItem(String type, String expectedStatus, String newStatus, String updatingUser, String updatingApplication, String placeId) 
	throws MethodException, ConnectionException
	{
		VistaQuery vm = new VistaQuery(GET_AND_TRANSITION_NEXT_WORK_ITEM);
		vm.addParameter(VistaQuery.LITERAL, type);
		vm.addParameter(VistaQuery.LITERAL, expectedStatus);
		vm.addParameter(VistaQuery.LITERAL, newStatus);
		vm.addParameter(VistaQuery.LITERAL, updatingUser);
		vm.addParameter(VistaQuery.LITERAL, updatingApplication);
		vm.addParameter(VistaQuery.LITERAL, placeId);
			
		String result = executeRPC(vm);
		
		return translateGetAndTransitionNextWorkItem(result);
	}

	private WorkItem translateGetAndTransitionNextWorkItem(String result) throws MethodException 
	{

		// Split the result into lines
		String[] lines = StringUtils.Split(result, LINE_SEPARATOR);
		
		// Split the first line into fields
		String[] fields = StringUtils.Split(lines[0], FIELD_SEPARATOR);

		WorkItem workItem = null;

		// Get the status code
		int code = getStatusCode(fields[0]);

		// If the code is 0 (success) parse and set the work item
		if (code >= 0 && lines.length > 1)
		{
			workItem = parseSingleWorkItem(lines);
		}

		// Return the work item or null
		return workItem;
	}
	
	
	private int getStatusCode(String field) 
	{
		int code;
		
		try
		{
			code = Integer.parseInt(field);
		}
		catch (NumberFormatException e)
		{
			code = -1;
		}
		
		return code;
	}
	//
	// Get and Transition Next Work Item
	//
	@Override
	public VistaQuery generateFindByCriteriaQuery(Object criteria)
	{
		WorkItemFilter filter = (WorkItemFilter)criteria;
		
		String subtype = filter.getSubtype() != null ? filter.getSubtype() : "";
		String status = filter.getStatus() != null ? filter.getStatus() : "";
		String placeId = filter.getPlaceId() != null ? filter.getPlaceId() : "";
		String priority = filter.getItemPriority() != null ? filter.getItemPriority() : "";
		String shortCircuitTagName = filter.getShortCircuitTagName() != null ? filter.getShortCircuitTagName() : "";
		String maximumNumberOfItemsToReturn = filter.getMaximumNumberOfItemsToReturn() != null ? filter.getMaximumNumberOfItemsToReturn() : "";

		VistaQuery vm = new VistaQuery(FIND_WORK_ITEMS);
		vm.addParameter(VistaQuery.LITERAL, filter.getType());
		vm.addParameter(VistaQuery.LITERAL, subtype);
		vm.addParameter(VistaQuery.LITERAL, status);
		vm.addParameter(VistaQuery.LITERAL, placeId);
		vm.addParameter(VistaQuery.LITERAL, priority);
		vm.addParameter(VistaQuery.LITERAL, shortCircuitTagName);
		vm.addParameter(VistaQuery.LITERAL, maximumNumberOfItemsToReturn);
		
		// Add the message lines and tags to a hashmap parameter
		HashMap<String, String> hm = getMessageLinesAndTagsAsHashMap(null, filter.getTags());
		vm.addParameter(VistaQuery.LIST, hm);
	
		return vm;
	}

	@Override
	public List<WorkItem> translateFindByCriteria(Object criteria, String returnValue) 
	throws MethodException
	{

		// Split the result into lines
		String[] lines = StringUtils.Split(returnValue, LINE_SEPARATOR);
		
		// Split the first line into fields
		String[] fields = StringUtils.Split(lines[0], FIELD_SEPARATOR);
		
		// If the code is less than 0, throw an exception with the message
		int code = Integer.parseInt(fields[0]);
		
		if (code < 0)
		{
			throw new MethodException(fields[1]);
		}
		
		// We made it here, so create the list of work items
		List<WorkItem> workItems = parseWorkItems(lines);
		
		return workItems;
	}

	
	private WorkItem parseSingleWorkItem(String[] lines) 
	{
		List<WorkItem> workItems = parseWorkItems(lines);
		if (workItems != null && workItems.size() > 0)
		{
			return workItems.get(0);
		}

		return null;
	}
	
	private List<WorkItem> parseWorkItems(String[] lines) 
	{
		List<WorkItem> workItems = new ArrayList<WorkItem>();
		WorkItem workItem = null;
		StringBuilder message = new StringBuilder();
		WorkItemTags tags = new WorkItemTags();

		// Skip the first line (which was the record count)
		for (int i=1; i<lines.length; i++)
		{
			// First, figure out what kind of data is in this line. It will either be 
			String[] fields = StringUtils.Split(lines[i], StringUtils.BACKTICK);
			String lineType = fields[0];

			if (lineType.equals(WorkItemHeader))
			{
				// This is a header line. If it's not the first header line (i.e. workItem
				// is not null), we first have to finalize the current item and add it to the list
				// before we parse the new header and reset the message and tag variables.
				if (workItem != null)
				{
					// Flush current item and reset pieces
					workItem.setMessage(message.toString());
					workItem.setTags(tags);
					workItems.add(workItem);
				}

				// Parse the header and reset the message and tags.
				workItem = parseWorkItemFromHeader(fields[1]);
				message = new StringBuilder();
				tags = new WorkItemTags();
				
			}
			
			if (lineType.equals(MessageLine))
			{
				message.append(fields[1]);
			}
			
			if (lineType.equals(Tag))
			{
				if (fields[1].contains(StringUtils.STICK))
				{
					String[] tagFields = StringUtils.Split(fields[1], StringUtils.STICK);
					tags.addTag(tagFields[0], tagFields[1]);
				}
				else
				{
					tags.addTag(fields[1], fields[2]);
				}
			}
		}
		
		// Once we've finished the main loop, add the final workItem (if any)
		if (workItem != null)
		{
			// Flush current item and reset pieces
			workItem.setMessage(message.toString());
			workItem.setTags(tags);
			workItems.add(workItem);
		}
		
		return workItems;
	}

	private WorkItem parseWorkItemFromHeader(String workItemHeader) 
	{

		String[] fields = StringUtils.Split(workItemHeader, StringUtils.STICK);
		
		WorkItem workItem = new WorkItem();
		workItem.setId(Integer.parseInt(fields[0]));
		workItem.setCreatedDate(fields[1]);
		workItem.setType(fields[2]);
		workItem.setSubtype(fields[3]);
		workItem.setStatus(fields[4]);
		workItem.setPlaceId(fields[5]);
		workItem.setPriority(Integer.parseInt(fields[6]));
		workItem.setCreatingUser(replaceNullWithEmptyString(fields[7]));
		workItem.setCreatingUserDisplayName(fields[8]);
		workItem.setLastUpdateDate(fields[9]);
		workItem.setUpdatingUser(replaceNullWithEmptyString(fields[10]));
		workItem.setUpdatingUserDisplayName(fields[11]);
		workItem.setCreatingApplication(replaceNullWithEmptyString(fields[12]));
		workItem.setUpdatingApplication(replaceNullWithEmptyString(fields[13]));
		
		return workItem;
	}

	private String replaceNullWithEmptyString(String field) 
	{
		return field.equals("null") ? "" : field;
	}
	//
	// Update Work Item
	//
	public boolean updateWorkItem(int workItemId, String expectedStatus, String newStatus, String newMessage, String updatingUser, String updatingApplication) 
	throws MethodException, ConnectionException
	{
		VistaQuery vm = new VistaQuery(UPDATE_WORK_ITEM);
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(workItemId));
		vm.addParameter(VistaQuery.LITERAL, expectedStatus);
		vm.addParameter(VistaQuery.LITERAL, newStatus);
		
		HashMap<String, String> hm = getMessageLinesAndTagsAsHashMap(newMessage, null);
		vm.addParameter(VistaQuery.LIST, hm);
		vm.addParameter(VistaQuery.LITERAL, updatingUser + "");
		vm.addParameter(VistaQuery.LITERAL, updatingApplication + "");
	
		String result = executeRPC(vm);
		
		return translateUpdateWorkItem(result);
	}

	protected boolean translateUpdateWorkItem(String result) throws UpdateException, MethodException {
		String[] results = StringUtils.Split(result, FIELD_SEPARATOR);

		if (!results[0].equals("0"))
		{
			if(results[0].equals("-6") || results[0].equals("-9"))
			{			
				throw new UpdateException(results[1]);
			}
			else
			{
				throw new MethodException(results[1]);
			}
		}
		
		return true;
	}
	

	//
	// Add Tag to Existing Work Item
	//
	public boolean addTagsToExistingWorkItem(
			int workItemId, 
			List<String> allowedStatuses, 
			List<WorkItemTag> newTags, 
			String updatingUser, 
			String updatingApplication) 
	throws MethodException, ConnectionException
	{
		// Build a delimited string of statuses
		String statusParam="";
		for (String status : allowedStatuses)
		{
			// Add a delimiter if we've already got content
			if (!statusParam.equals(""))
				statusParam += "`";
			
			statusParam += status;
		}
		
		// Build hashmap for tags
		int counter = 1;
		HashMap<String, String> hmTags = new HashMap<String, String>();
		for (WorkItemTag tag : newTags)
		{
			hmTags.put(Integer.toString(counter), tag.getKey() + "`" + tag.getValue());
			counter++;
		}

		// Create the query
		VistaQuery vm = new VistaQuery(ADD_TAG_TO_EXISTING_WORK_ITEM);
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(workItemId));
		vm.addParameter(VistaQuery.LITERAL, statusParam);
		vm.addParameter(VistaQuery.LITERAL, updatingUser);
		vm.addParameter(VistaQuery.LITERAL, updatingApplication);
		vm.addParameter(VistaQuery.LIST, hmTags);
	
		// Call the RPC
		String result = executeRPC(vm);
		
		// Translate the result
		return translateAddTagToExistingWorkItem(result);
	}

	protected boolean translateAddTagToExistingWorkItem(String result) 
	throws UpdateException, MethodException 
	{
		String[] results = StringUtils.Split(result, FIELD_SEPARATOR);

		if (!results[0].equals("0"))
		{
			if(results[0].equals("-5") || results[0].equals("-9"))
			{			
				throw new UpdateException(results[1]);
			}
			else
			{
				throw new MethodException(results[1]);
			}
		}
		return true;
	}
	
	// Delete
	@Override 
	public VistaQuery generateDeleteQuery(int id)
	{
		// Create the query
		VistaQuery vm = new VistaQuery(DELETE_WORK_ITEM);
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(id));
		
		return vm;
	}


	//
	// Delete all workitems
	//
	public void deleteAllWorkItems() 
	throws MethodException, ConnectionException
	{
		VistaQuery vm = new VistaQuery(DELETE_ALL_WORK_ITEMS);
		executeRPC(vm);
	}
	
	public WorkItemCounts getWorkItemCounts(String workItemType) 
	throws MethodException, ConnectionException 
	{
		VistaQuery vm = new VistaQuery(GET_WORK_ITEM_COUNTS);
		vm.addParameter(VistaQuery.LITERAL, workItemType);
	
		String result = executeRPC(vm);
		
		return translateGetWorkItemCounts(result);
	}
	
	public WorkItemCounts translateGetWorkItemCounts(String result) throws MethodException 
	{
		// Split the result into lines
		String[] lines = StringUtils.Split(result, LINE_SEPARATOR);
		
		// Split the first line into fields
		String[] fields = StringUtils.Split(lines[0], FIELD_SEPARATOR);
		
		// If the code is less than 0, throw an exception with the message
		int code = Integer.parseInt(fields[0]);
		
		if (code < 0)
		{
			throw new MethodException(fields[1]);
		}

		// Create the workItem counts object, since we didn't throw an exception.
		WorkItemCounts counts = new WorkItemCounts();
		
		// If there are more than two lines returned (1 count line, 1 header line), we have results, so process them.
		// Otherwise, return the empty workItems count object.
		if (lines.length > 2)
		{
			for (int i=2; i<lines.length; i++)
			{
				fields = StringUtils.Split(lines[i], FIELD_SEPARATOR);
				counts.addCountForSubtypeAndStatus(fields[0], fields[1], Integer.parseInt(fields[2]));
			}
		}
		
		return counts;

			
		
	}


}
