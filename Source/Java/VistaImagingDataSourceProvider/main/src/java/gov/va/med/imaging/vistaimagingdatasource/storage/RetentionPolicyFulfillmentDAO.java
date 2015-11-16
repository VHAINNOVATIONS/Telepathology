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

import java.util.HashMap;

import gov.va.med.imaging.exchange.business.storage.RetentionPolicyFulfillment;
import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class RetentionPolicyFulfillmentDAO extends StorageDAO<RetentionPolicyFulfillment>
{
	//
	// RPC Names
	//
	private final static String RPC_CREATE_RETPOLFF = "MAGVA CREATE RETPOLFF";	
	
	//
	// Retention Policy Fulfillment table (2006.922) fields
	//
	private final static String RPFF_PK = "PK";
	private final static String RPFF_ARETPOL_FK = "ARTIFACT RETENTION POLICY"; // FK removed // foreign key (IEN/pointer) to RetRol file's record
	private final static String RPFF_AI_FK = "ARTIFACT INSTANCE"; // FK removed // foreign key (IEN/pointer) to Artifact Instance file's record
	private final static String RPFF_CREATED_DATIME = "CREATED DATE/TIME"; // / inserted

	//
	// Constructor
	//
	public RetentionPolicyFulfillmentDAO(){}
	public RetentionPolicyFulfillmentDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	//
	// Create methods
	//
	@Override
	public VistaQuery generateCreateQuery(RetentionPolicyFulfillment retentionPolicyFulfillment) 
	{
		VistaQuery vm = new VistaQuery(RPC_CREATE_RETPOLFF);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(RPFF_ARETPOL_FK, Integer.toString(retentionPolicyFulfillment.getArtifactRetentionPolicyId()));
		hm.put(RPFF_AI_FK, Integer.toString(retentionPolicyFulfillment.getArtifactInstanceId()));
		vm.addParameter(VistaQuery.ARRAY, hm);
		
		return vm;
	}

}