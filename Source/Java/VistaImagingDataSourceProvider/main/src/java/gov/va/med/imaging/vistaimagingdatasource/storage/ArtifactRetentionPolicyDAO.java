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

import gov.va.med.imaging.exchange.business.storage.ArtifactRetentionPolicy;
import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ArtifactRetentionPolicyDAO extends StorageDAO<ArtifactRetentionPolicy>
{
	//
	// RPC Names
	//
	private final static String RPC_CREATE_ARETPOL = "MAGVA CREATE ARETPOL";	
	private final static String RPC_UPDATE_ARETPOL = "MAGVA UPDATE ARETPOL";	

	//
	// Artifact Retention Policy table (2006.921) fields
	//
	private final static String ARP_PK = "PK";
	private final static String ARP_ARTIFACT_FK = "ARTIFACT"; // foreign key (IEN/pointer) to Artifact file's record
	private final static String ARP_RETPOL_FK = "RETENTION POLICY"; // foreign key (IEN/pointer) to RetentionPolicy file's record
	private final static String ARP_CREATED_DATIME = "CREATED DATE/TIME"; //
	private final static String ARP_IS_ACTIVE = "ACTIVE"; // 0 or 1 
//	private final static String ARP_IS_SATISFIED = "SATISFIED"; // 0 or 1 -- removed 09/05/11
	private final static String ARP_SATISFIED_DATETIME = "SATISFIED DATE/TIME"; // YYYYMMDD.HHMISS

	//
	// Constructor
	//
	public ArtifactRetentionPolicyDAO(){}
	public ArtifactRetentionPolicyDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	//
	// Create methods
	//
	@Override
	public VistaQuery generateCreateQuery(ArtifactRetentionPolicy artifactRetentionPolicy) 
	{
		VistaQuery vm = new VistaQuery(RPC_CREATE_ARETPOL);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(ARP_ARTIFACT_FK, Integer.toString(artifactRetentionPolicy.getArtifactId()));
		hm.put(ARP_RETPOL_FK, Integer.toString(artifactRetentionPolicy.getRetentionPolicyId()));
//		hm.put(ARP_IS_SATISFIED, artifactRetentionPolicy.isSatisfied()?"1":"0");
		hm.put(ARP_SATISFIED_DATETIME, artifactRetentionPolicy.getSatisfiedDateTime().toString());
		vm.addParameter(VistaQuery.ARRAY, hm);
		
		return vm;
	}

	//
	// Update overrides
	//
	@Override
	public VistaQuery generateUpdateQuery(ArtifactRetentionPolicy artifactRetentionPolicy) 
	{
		VistaQuery vm = new VistaQuery(RPC_UPDATE_ARETPOL);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(ARP_PK, Integer.toString(artifactRetentionPolicy.getId()));
		//hm.put(ARP_IS_ACTIVE, artifactRetentionPolicy.isActive()?"1":"0");
//		hm.put(ARP_IS_SATISFIED, artifactRetentionPolicy.isSatisfied()?"1":"0"); // removed 09/05/11
		hm.put(ARP_SATISFIED_DATETIME, artifactRetentionPolicy.getSatisfiedDateTime().toString()); // added 09/05/11
		vm.addParameter(VistaQuery.ARRAY, hm);
		
		return vm;
	}

}