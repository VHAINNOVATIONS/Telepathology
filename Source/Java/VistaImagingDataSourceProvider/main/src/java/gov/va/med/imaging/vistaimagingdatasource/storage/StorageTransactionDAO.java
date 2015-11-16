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

package gov.va.med.imaging.vistaimagingdatasource.storage;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.TimePeriod;
import gov.va.med.imaging.exchange.business.storage.StorageTransaction;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class StorageTransactionDAO extends StorageDAO<StorageTransaction> {
	private final static String FAILURE = "0";
	private final static String DELIMITER = "^";

	//
	// RPC Names
	//
	private final static String RPC_CREATE_STORAGE_TA = "MAGVA CREATE STORAGE TA";
	private final static String RPC_GET_TA_WITHIN_TF = "MAGV ENS GET STORAGE TRANS";
	private final static String RPC_GET_TA_COUNT_WITHIN_TF = "MAGV ENS GET STORAGE TRANS CNT";

	//
	// Storage Transactions table (2006.926) fields
	//
	private final static String ST_PK = "PK";
	private final static String ST_A_FK = "ARTIFACT"; // FK removed // foreign key
															// (IEN/pointer) to
															// Artifact Instance
															// file's record
	private final static String ST_P_FK = "STORAGE PROVIDER"; // FK removed // foreign key
															// (IEN/pointer) to
															// Artifact
															// Provider's record
	private final static String ST_TA_TYPE = "TRANSACTION TYPE";
	private final static String ST_STATUS = "SUCCEEDED";
	private final static String ST_TA_DATATIME = "TRANSACTION DATA/TIME";
	private final static String ST_INITIATING_APP = "INITIATING APPLICATION"; // was "USER NAME";
	private final static String ST_MESSAGE = "MAGMSG";
	private final static String ST_START_DATETIME = "START DATE/TIME";
	private final static String ST_END_DATETIME = "END DATE/TIME";
	private final static String ST_PREV_IEN = "IEN";
	private final static String ST_NUM_RECORDS = "NUM_RECORDS";

	//
	// Constructor
	//
	public StorageTransactionDAO() {
	}

	public StorageTransactionDAO(VistaSessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
	}

	//
	// Create methods
	//
	@Override
	public VistaQuery generateCreateQuery(StorageTransaction storageTransaction) {
		VistaQuery vm = new VistaQuery(RPC_CREATE_STORAGE_TA);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(ST_A_FK, Integer.toString(storageTransaction.getArtifactId()));
		hm.put(ST_P_FK, Integer.toString(storageTransaction.getProviderId()));
		hm.put(ST_STATUS, storageTransaction.getStatus()); // "1"(success)/"0"(failure)
		hm.put(ST_TA_TYPE, storageTransaction.getTransactionType()); // R(ead),
																		// W(rite),
																		// C(onfigure)
//		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd.hhmmss"); 
//		hm.put(ST_TA_DATATIME, df.format(storageTransaction.getTransactionDateTime().getTime())); // added 09/06/11
		hm.put(ST_INITIATING_APP, storageTransaction.getInitApp());
		stringToHashMap2(hm, ST_MESSAGE, storageTransaction.getMessage());
		vm.addParameter(VistaQuery.ARRAY, hm); // .LIST

		return vm;
	}

	public int getCountWithinTimePeriod(String type, String status, TimePeriod period)
			throws MethodException, ConnectionException {
		VistaQuery vm = new VistaQuery(RPC_GET_TA_COUNT_WITHIN_TF);
		HashMap<String, String> hm = new HashMap<String, String>();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd.hhmmss");
		hm.put(ST_TA_TYPE, type);
		hm.put(ST_STATUS, status);
		hm.put(ST_START_DATETIME, df.format(period.getStartCalendar().getTime()));
		hm.put(ST_END_DATETIME, df.format(period.getEndCalendar().getTime()));
		vm.addParameter(VistaQuery.ARRAY, hm); // .LIST
		String result = executeRPC(vm);
		String[] lineParts = result.split(DELIMITER);
		if (lineParts[0].equals(FAILURE)) {
			throw new MethodException(lineParts[1]);
		}
		return Integer.parseInt(lineParts[2]);
	}

//	public List<StorageTransaction> getWithinTimeframe(String type, Date startDate, Date endDate)
//			throws MethodException, ConnectionException {
//		VistaQuery vm = new VistaQuery(RPC_GET_TA_WITHIN_TF);
//		HashMap<String, String> hm = new HashMap<String, String>();
//		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd.hhmmss");
//		hm.put(ST_TA_TYPE, type);
//		hm.put(ST_START_DATETIME, df.format(startDate));
//		hm.put(ST_END_DATETIME, df.format(endDate));
//		vm.addParameter(VistaQuery.ARRAY, hm); // .LIST
//		String result = executeRPC(vm);
//		String[] lineParts = result.split(DELIMITER);
//		if (lineParts[0].equals(FAILURE)) {
//			throw new MethodException(lineParts[1]);
//		}
//		return Integer.parseInt(lineParts[2]);
//	}
}
