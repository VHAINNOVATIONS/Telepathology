/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswtittoc
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

import gov.va.med.imaging.exchange.business.dicom.StorageCommitElement;
import gov.va.med.imaging.exchange.business.dicom.StorageCommitWorkItem;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SCWorkItemDAO extends EntityDAO<StorageCommitWorkItem>
{
	// Most DB RPCs take Name`Value pairs as input and return 
	//    >0 (positive value) for return value (typically IEN) and 
	//    <0 (negative values) for errors
	private static String CREATE_SC_WORK_ITEM = "MAGVC WI SUBMIT NEW";
	private static String PROCESS_OR_GET_SC_WORK_ITEM = "MAGVC WI GET";
	private static String LIST_SC_WORK_ITEMS = "MAGVC WI LIST";
	private static String DELETE_SC_WORK_ITEM = "MAGVC WI DELETE";
	private static String UPDATE_SC_WORK_ITEM_STATUS = "MAGVC WI UPDATE STATUS";
	
	private final static char inputSeparator = '`'; // name-value pair
	private final static char outputSeparator = '`';
	private final static char listItemSeparator = '|';
	private final static char inner_separator = '~';
	
	// Submit attribute tags
	private final static String SC_WI_APP_NAME = "ApplicationName"; // AE Sec. MX appName
	private final static String SC_WI_TA_ID = "TransactionID"; // UID, identifier for SC request, SUBMIT RPC must reject duplicates!
	private final static String SC_HOSTNAME = "HostName"; // tags the local node as the owner of the request for processing
	private final static String SC_WI_RESP_DT = "ResponseDateTime"; // millis
	private final static String SC_RETRIES_LEFT = "RetriesLeft"; // telling how many times N-EVENT-REPORT send can be retried
	private final static String SC_WI_PROCESS= "STAT"; // '0' or '1'
	private final static String SC_WI_STATUS = "scWIstatus"; // RECEIVED, IN-PROGRESS, SUCCESS, FAILURE, 
															 // SUCCESS SENT,  FAILURE SENT or  SENDING RESPONSE FAILED 
	private final static String SC_WI_ITEM_COUNT = "ItemCount"; // up to 5 digit decimal value
	private final static String SC_WI_ITEM_PREFIX = "Item"; // followed by maxSCItemDigits decimals with leading 0s
 	private final static int maxSCItemDigits = 5;
 	
 	// Process or get attribute tags
//	private final static String SC_WI_ID = "SC_WI_ID"; // unique SC WI id in persistence
//	private final static String SC_DO_PROCESS = "SC_DO_PROCESS"; // '0' or '1'
	
//	private static String WorkItemHeader = "WorkItemHeader";
//	private static String MessageLine = "Message";
//	private static String Tag = "Tag";
	
	//
	// Constructor
	//
	public SCWorkItemDAO(){}
	public SCWorkItemDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	//
	// Creation overrides
	//
	@Override
	public VistaQuery generateCreateQuery(StorageCommitWorkItem scWI) 
	{
		VistaQuery vm = new VistaQuery(CREATE_SC_WORK_ITEM);
		LinkedHashMap <String, String> lhm = new LinkedHashMap <String, String>();
		lhm.put("1", SC_WI_APP_NAME + inputSeparator + scWI.getApplicationName());
		lhm.put("2", SC_WI_TA_ID + inputSeparator + scWI.getTransactionUID()); 
		lhm.put("3", SC_HOSTNAME + inputSeparator + scWI.getHostName()); 
		lhm.put("4", SC_WI_RESP_DT + inputSeparator + scWI.getResponseTimeStamp().toString());
        lhm.put("5", SC_RETRIES_LEFT + inputSeparator + scWI.getRetriesLeft()); 
//        lhm.put("6", SC_WI_PROCESS + inputSeparator + (scWI.isDoProcess()?"1":"0"));
//		lhm.put("6", SC_WI_STATUS + inputSeparator + scWI.getStatus());

		// encode SC request list into linked hash map
		Integer next=6;
		int listSize=scWI.getStorageCommitElements().size();
		lhm.put(next.toString(), SC_WI_ITEM_COUNT + inputSeparator + Integer.toString(listSize));
		next++;
		for (int i=1; i <= listSize; i++) {
			// construct id tag
			String idTag=SC_WI_ITEM_PREFIX;
			int digits=Integer.valueOf(i).toString().length();
			for (int j=1; j <= (maxSCItemDigits-digits); j++) {
				idTag += "0";
			}
			idTag += Integer.valueOf(i).toString();
			String tagValue = scWI.getStorageCommitElements().get(i-1).getSopClassUid() + inner_separator +
							  scWI.getStorageCommitElements().get(i-1).getSopInstanceUID() + inner_separator +
							  scWI.getStorageCommitElements().get(i-1).getCommitStatus() + inner_separator +
							  scWI.getStorageCommitElements().get(i-1).getFailureReason();
			lhm.put(next.toString(), idTag + inputSeparator + tagValue);
			next++;
		}	
		
		vm.addParameter(VistaQuery.LIST, lhm);
		vm.addParameter(VistaQuery.LITERAL, scWI.isDoProcess()?"1":"0");
//		vm.addParameter(VistaQuery.LITERAL, workItem.getCreatingUser() + "");
//		vm.addParameter(VistaQuery.LITERAL, workItem.getCreatingApplication() + "");

		return vm;
	}
	@Override
	public StorageCommitWorkItem translateCreate(StorageCommitWorkItem scWI, String result) 
	throws RetrievalException 
	{
		String[] results = StringUtils.Split(result, StringUtils.CRLF);

		if (!results[0].startsWith("0"))
		{
			
			throw new RetrievalException(result);
		}
		else
		{
			decodeSCWI(scWI, result);
		}
		return scWI;
	}
	
	private String decodeSCWI(StorageCommitWorkItem scWI, String result) {

		String scWIID="-1";

		// decipher lines
		String[] lines = StringUtils.Split(result, StringUtils.CRLF);
		
		for (int i=0; i<lines.length; i++) {
			String[] tag = StringUtils.Split(lines[i], ""+outputSeparator);
			if (i==0) {
				scWIID=tag[1];
			}
			else if (tag[0].equals(SC_WI_APP_NAME)) {
				scWI.setApplicationName(tag[1]);
			}
			else if (tag[0].equals(SC_WI_TA_ID)) {
				scWI.setTransactionUID(tag[1]);
			} 
			else if (tag[0].equals(SC_WI_RESP_DT)) {
				scWI.setResponseTimeStamp(Long.getLong(tag[1]));
			} 
			else if (tag[0].equals(SC_WI_STATUS)) {
				scWI.setStatus(tag[1]);
			} 
			else if (tag[0].equals(SC_HOSTNAME)) {
				scWI.setHostName(tag[1]);
			} 
			else if (tag[0].equals(SC_RETRIES_LEFT)) {
				scWI.setRetriesLeft(Integer.parseInt(tag[1]));
			} 
			else if (tag[0].equals(SC_WI_ITEM_COUNT)) {
				scWI.getStorageCommitElements().clear();
			} 
			else {
				StorageCommitElement scE=new StorageCommitElement();
				
				String[] scElem = StringUtils.Split(tag[1], ""+inner_separator);
				
				scE.setSopClassUid(scElem[0]);
				scE.setSOPInstanceUID(scElem[1]);
				scE.setCommitStatus(scElem[2].charAt(0));
				if ((scElem.length<=3) || (scElem[3].isEmpty())) scE.setFailureReason('?');
				else scE.setFailureReason(scElem[3].charAt(0));
					
				scWI.getStorageCommitElements().add(scE);
			}
		}
		return scWIID;
	}

	//
	// Get overrides
	//
	@Override
	public VistaQuery generateGetEntityByExampleQuery(StorageCommitWorkItem scWI) 
	{
		
		VistaQuery vm = new VistaQuery(PROCESS_OR_GET_SC_WORK_ITEM);
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(scWI.getId()));
		vm.addParameter(VistaQuery.LITERAL, scWI.isDoProcess()?"1":"0");

		return vm;
	}
	
	@Override
	public StorageCommitWorkItem translateGetEntityByExample(StorageCommitWorkItem scWI, String result) 
	throws RetrievalException 
	{
		String[] results = StringUtils.Split(result, ""+outputSeparator);

		if (!results[0].startsWith("0"))
		{
			
			throw new RetrievalException(result);
		}
		else
		{
			decodeSCWI(scWI, result);
		}
		return scWI;
	}
	//
	// List (findByCriteria) override
	//
	@Override
	public VistaQuery generateFindByCriteriaQuery(Object hostname) 
	{
		VistaQuery vm = new VistaQuery(LIST_SC_WORK_ITEMS);
		String hostName=(String)hostname;
		vm.addParameter(VistaQuery.LITERAL, hostName);

		return vm;
	}
	@Override
	public List<StorageCommitWorkItem> translateFindByCriteria (Object hostname, String result) 
	throws RetrievalException 
	{
		String[] results = StringUtils.Split(result, ""+StringUtils.CRLF);
		List<StorageCommitWorkItem> scWIList = new ArrayList<StorageCommitWorkItem>();
		scWIList.clear();

		if (!results[0].startsWith("0")) // firstLine: 0`N or <error>`<error message>
		{
			throw new RetrievalException(result);
		}
		else
		{
			String[] firstLine = StringUtils.Split(results[0], ""+inputSeparator);
			StorageCommitWorkItem scWI=new StorageCommitWorkItem();
			for (int i=1; i<=Integer.parseInt(firstLine[1]); i++) {
				String[] info = StringUtils.Split(results[i], ""+listItemSeparator);
				if (info[4].equalsIgnoreCase((String)hostname)) { // TODO: remove check, after M side is fixed
					scWI.setId(Integer.parseInt(info[0]));
					scWI.setStatus(info[1]);
					scWI.setResponseTimeStamp(Long.valueOf(info[2]));
					scWI.setRetriesLeft(Integer.parseInt(info[3]));
					scWI.setHostName(info[4]);
					scWIList.add(scWI);
				}
			}
		}
		return scWIList;
	}

	//
	// Delete overrides
	//
	@Override
	public VistaQuery generateDeleteQuery(int scWIID) 
	{
		VistaQuery vm = new VistaQuery(DELETE_SC_WORK_ITEM);
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(scWIID));

		return vm;
	}
	@Override
	public void translateDelete(String result) 
	throws RetrievalException 
	{
		String[] results = StringUtils.Split(result, ""+StringUtils.CRLF);

		if (!results[0].startsWith("0"))
		{
			throw new RetrievalException(result);
		}
		return;
	}
	//
	// Update override
	//
	@Override
	public VistaQuery generateUpdateQuery(StorageCommitWorkItem scWI) 
	{
		VistaQuery vm = new VistaQuery(UPDATE_SC_WORK_ITEM_STATUS);
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(scWI.getId()));
		vm.addParameter(VistaQuery.LITERAL, scWI.getStatus());
//		vm.addParameter(VistaQuery.LITERAL, (scWI.getStatus().compareToIgnoreCase("SENDING RESPONSE FAILED")==0)?"1":"0");
		return vm;
	}

	@Override
	public StorageCommitWorkItem translateUpdate(StorageCommitWorkItem scWI, String result) 
	throws RetrievalException 
	{
		String[] results = StringUtils.Split(result, ""+StringUtils.CRLF);

		if (!results[0].startsWith("0"))
		{			
			throw new RetrievalException(result);
		}
		else
		{
			decodeSCWI(scWI, result);
		}

		return scWI;
	}
}
