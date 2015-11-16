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

import gov.va.med.imaging.exchange.business.storage.Artifact;
import gov.va.med.imaging.exchange.business.storage.Key;
import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.HashMap;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ArtifactDAO extends BaseArtifactDAO
{
	//
	// Constructor
	//
	public ArtifactDAO(){}
	public ArtifactDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	//
	// Creation overrides
	//
	@Override
	public VistaQuery generateCreateQuery(Artifact artifact) 
	{
		VistaQuery vm = new VistaQuery(RPC_CREATE_A_W_KL);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(A_TOKEN, artifact.getArtifactToken());
		hm.put(A_AD_FK, Integer.toString(artifact.getArtifactDescriptorId()));
		hm.put(A_SIZE_IN_BYTES, Long.toString(artifact.getSizeInBytes()));
		hm.put(A_CRC, artifact.getCRC());
		hm.put(A_CREATED_BY, artifact.getCreatedBy());
		hm.put(A_CREATION_DATETIME, artifact.getCreatedDateTime()); // added 09/05/11

		List<Key> items = artifact.getKeyList();
		if(items != null)
		{
			for(int index=0; index<items.size(); ++index)
			{
				String num = String.valueOf(index+1);
				for (int i=num.length(); i<3; i++)
					  num ="0" + num;
				hm.put(A_KL + num, items.get(index).getValue());
			}
		}		
		vm.addParameter(VistaQuery.ARRAY, hm); // .LIST

		return vm;
	}

	//
	// Update overrides
	//
	@Override
	public VistaQuery generateUpdateQuery(Artifact artifact) 
	{
		VistaQuery vm = new VistaQuery(RPC_UPDATE_ARTIFACT);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(A_PK, Integer.toString(artifact.getId()));
		hm.put(A_CRC, artifact.getCRC());
		hm.put(A_SIZE_IN_BYTES, Long.toString(artifact.getSizeInBytes()));
		vm.addParameter(VistaQuery.ARRAY, hm);
		return vm;
	}

	@Override
	public Artifact translateUpdate(Artifact artifact, String returnValue) 
	{
		return artifact;
	}

	//
	// Retrieve By Criteria methods
	//
	@Override
	public VistaQuery generateGetEntityByExampleQuery(Artifact artifact) 
	{
		VistaQuery vm = new VistaQuery(RPC_GET_A_W_KL);
		vm.addParameter(VistaQuery.LITERAL, artifact.getArtifactToken());
		return vm;
	}

	@Override
	public Artifact translateGetEntityByExample(Artifact artifact, String returnValue) throws RetrievalException
	{
		return translateArtifact(returnValue);	
	}


	
}
