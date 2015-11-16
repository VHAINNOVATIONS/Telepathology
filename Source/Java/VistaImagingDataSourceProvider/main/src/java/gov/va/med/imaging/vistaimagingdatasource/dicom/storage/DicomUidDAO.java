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

package gov.va.med.imaging.vistaimagingdatasource.dicom.storage;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.WorkItem;
import gov.va.med.imaging.exchange.business.dicom.DicomUid;
import gov.va.med.imaging.exchange.business.dicom.OriginIndex;
import gov.va.med.imaging.exchange.business.dicom.importer.OrderingLocation;
import gov.va.med.imaging.exchange.business.dicom.importer.Procedure;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class DicomUidDAO extends EntityDAO<DicomUid> {
	private static String GENERATE_DICOM_UID = "MAGV GENERATE DICOM UID";
	
	private static String ACCESSION_NUMBER = "ACCESSION NUMBER";
	private static String SITE = "SITE";
	private static String INSTRUMENT = "INSTRUMENT";
	private static String TYPE = "TYPE";
	
	private static int counter=1;

	//
	// Constructor
	//
	public DicomUidDAO() {
	}

	public DicomUidDAO(VistaSessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
	}

	public DicomUid createNewDicomUid(String accessionNumber, String siteId, String instrument, String type) 
	throws MethodException, ConnectionException
	{
//		String uid = "1.2.3.4.5.6.111222.1112222." + counter++;
//		return new DicomUid(type, uid);

		// Call the RPC
		VistaQuery vm = new VistaQuery(GENERATE_DICOM_UID);

		HashMap <String, String> hm = new LinkedHashMap <String, String>();
		hm.put(ACCESSION_NUMBER, accessionNumber);
		hm.put(SITE, siteId);
		hm.put(INSTRUMENT, instrument);
		hm.put(TYPE, type);
		vm.addParameter(VistaQuery.ARRAY, hm);
		
		String result = executeRPC(vm); // Create the OrderingLocation list
		return translateCreateNewDicomUid(result, type);

	}

	/**
	 * @param result
	 * @return
	 * @throws MethodException 
	 */
	private DicomUid translateCreateNewDicomUid(String result, String type) throws MethodException 
	{
		// Split the result into lines
		String[] fields = StringUtils.Split(result, DB_OUTPUT_SEPARATOR1);
		
		if (fields[0].equals("0"))
		{
			String value = fields[1];
			return new DicomUid(type, value);
		}
		else
		{
			throw new MethodException(fields[1]);
		}
	}
}
