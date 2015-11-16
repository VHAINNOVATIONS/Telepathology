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

import gov.va.med.imaging.StringUtil;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.storage.Artifact;
import gov.va.med.imaging.exchange.business.storage.ArtifactInstance;
import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.io.IOException;
import java.util.HashMap;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ArtifactInstanceDAO extends StorageDAO<ArtifactInstance>
{
	//
	// RPC Names
	//
	private final static String RPC_CREATE_AI = "MAGVA CREATE AINSTANCE";					// 8
	private final static String RPC_UPDATE_LAST_ACCESS_DT = "MAGVA UPDATE LAST ACCESS DT";	// 8
//	private final static String RPC_UPDATE_URL = "MAGVA UPDATE URL";	// 8
	
	//
	// Artifact Instance (8) table (2006.918) fields
	//
	private final static String AI_PK = "PK";
	private final static String AI_PROVIDER_FK = "STORAGE PROVIDER"; // FK removed // foreign key (IEN/pointer) to Provider file's record
	private final static String AI_ARTIFACT_FK = "ARTIFACT"; // FK removed // foreign key (IEN/pointer) to Artifact file's record
	private final static String AI_URL = "MAGURL";
	private final static String AI_DISK_VOLUME = "DISK VOLUME"; // FK removed // network location entry IEN
	private final static String AI_FILEPATH = "FILEPATH";
	private final static String AI_FILEREF = "FILEREF";
	private final static String AI_CREATED_DATIME = "CREATED DATE/TIME"; // / inserted
	private final static String AI_LAST_ACCESS_DATIME = "LAST ACCESS DATE/TIME"; // / inserted

	//
	// Constructor
	//
	public ArtifactInstanceDAO(){}
	public ArtifactInstanceDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	//
	// Creation overrides
	//
	@Override
	public VistaQuery generateCreateQuery(ArtifactInstance artifactInstance) 
	{
		
		VistaQuery vm = new VistaQuery(RPC_CREATE_AI);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(AI_ARTIFACT_FK, Integer.toString(artifactInstance.getArtifactId()));
		hm.put(AI_PROVIDER_FK, Integer.toString(artifactInstance.getProviderId()));
		hm.put(AI_DISK_VOLUME, Integer.toString(artifactInstance.getDiskVolume()));
		hm.put(AI_FILEPATH, artifactInstance.getFilePath());
		hm.put(AI_FILEREF, artifactInstance.getFileRef());
		if (artifactInstance.getUrl() != null && !artifactInstance.getUrl().equals(""))
			stringToHashMap2(hm, AI_URL, artifactInstance.getUrl());
//		String lineId;
//		String[] urlParts = StringUtil.breakString(artifactInstance.getUrl(), MAX_M_STRING_LENGTH);
//		for (int i = 0; i < urlParts.length; i++) {
//			lineId = String.format(AI_URL + "%03d", i + 1);
//			hm.put(lineId, urlParts[i]);
//		}
		vm.addParameter(VistaQuery.ARRAY, hm); // .LIST
		return vm;
	}

	//
	// Update last access date 
	//
	public ArtifactInstance updateLastAccessDateTime(ArtifactInstance artifactInstance) throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateUpdateLastAccessDateTimeQuery(artifactInstance);
		return translateUpdateLastAccessDateTime(artifactInstance, executeRPC(vm));
	}

	public VistaQuery generateUpdateLastAccessDateTimeQuery(ArtifactInstance artifactInstance) 
	{
		VistaQuery vm = new VistaQuery(RPC_UPDATE_LAST_ACCESS_DT);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(AI_PK, Integer.toString(artifactInstance.getId()));
		vm.addParameter(VistaQuery.ARRAY, hm);
		
		return vm;
	}

	public ArtifactInstance translateUpdateLastAccessDateTime(ArtifactInstance artifactInstance, String returnValue) 
	{
		return artifactInstance;
	}

	//
	// Update last access date 
	//
	public ArtifactInstance updateUrl(ArtifactInstance artifactInstance) throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateUpdateLastAccessDateTimeQuery(artifactInstance);
		return translateUpdateLastAccessDateTime(artifactInstance, executeRPC(vm));
	}

//	public VistaQuery generateUpdateUrlQuery(ArtifactInstance artifactInstance) 
//	{
//		VistaQuery vm = new VistaQuery(RPC_UPDATE_URL);
//		HashMap <String, String> hm = new HashMap <String, String>();
//		hm.put(AI_PK, Integer.toString(artifactInstance.getId()));
//		hm.put(AI_URL, artifactInstance.getUrl());
//		vm.addParameter(VistaQuery.ARRAY, hm);
//		
//		return vm;
//	}
//
//	public ArtifactInstance translateUpdateUrl(ArtifactInstance artifactInstance, String returnValue) 
//	{
//		return artifactInstance;
//	}
//

}