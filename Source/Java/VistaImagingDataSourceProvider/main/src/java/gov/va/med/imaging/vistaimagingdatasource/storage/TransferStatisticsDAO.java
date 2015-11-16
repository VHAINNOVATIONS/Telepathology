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

import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;
import gov.va.med.imaging.exchange.business.storage.TransferStatistics;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class TransferStatisticsDAO extends StorageDAO<TransferStatistics>
{
	//
	// RPC Names
	//
	private final static String RPC_CREATE_TRF_STATS = "MAGVA CREATE TRF STATS";	// 12

	//
	// TransferStatistics table (2006.925) fields
	//
	private final static String TS_PROVIDER_FK = "STORAGE PROVIDER"; // FK removed // foreign key (IEN/pointer) to Provider file's record
	private final static String TS_ENDPOINT_PLACE_FK = "ENDPOINT PLACE"; // FK removed // foreign key (IEN/pointer) to Place file's record
	private final static String TS_START_DATETIME = "START DATE/TIME";  // / inserted
	private final static String TS_DURA_IN_MS = "DURATION IN MILLISECONDS";
	private final static String TS_SIZE_IN_BYTES = "SIZE IN BYTES";

	//
	// Constructor
	//
	public TransferStatisticsDAO(){}

	public TransferStatisticsDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	//
	// Create methods
	//
	@Override
	public VistaQuery generateCreateQuery(TransferStatistics storageTransaction) 
	{
		VistaQuery vm = new VistaQuery(RPC_CREATE_TRF_STATS);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(TS_PROVIDER_FK, Integer.toString(storageTransaction.getProviderId()));
		hm.put(TS_ENDPOINT_PLACE_FK, Integer.toString(storageTransaction.getPlaceId()));
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd.hhmmss");
		String rpcDate = df.format(storageTransaction.getStartDateTime());
		hm.put(TS_START_DATETIME, rpcDate);
		hm.put(TS_DURA_IN_MS, Long.toString(storageTransaction.getDurationInMilliseconds()));
		hm.put(TS_SIZE_IN_BYTES, Long.toString(storageTransaction.getSizeInBytes()));
		vm.addParameter(VistaQuery.ARRAY, hm);
		
		return vm;
	}
}