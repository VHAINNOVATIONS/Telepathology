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
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.HashMap;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ArtifactAndInstanceDAO extends BaseArtifactDAO
{
	//
	// Constructor
	//
	public ArtifactAndInstanceDAO(){}
	public ArtifactAndInstanceDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	//
	// Retrieve By ID methods
	//
	@Override
	public VistaQuery generateGetEntityByIdQuery(String id) 
	{
		VistaQuery vm = new VistaQuery(RPC_GET_A_W_KL_AND_AIS_BY_PK);
		vm.addParameter(VistaQuery.LITERAL, id);
		
		return vm;
	}

	@Override
	public Artifact translateGetEntityById(String id, String returnValue) 
	{
		throw new NotImplementedException();
	}

	//
	// Retrieve By Criteria methods
	//
	@Override
	public VistaQuery generateGetEntityByExampleQuery(Artifact artifact) 
	{
		//
		// Get artifact and instances by artifactToken
		//

		VistaQuery vm = new VistaQuery(RPC_GET_A_W_KL_AND_AIS);
		vm.addParameter(VistaQuery.LITERAL, artifact.getArtifactToken());	
	
		return vm;
	}

	@Override
	public Artifact translateGetEntityByExample(Artifact artifact, String returnValue) throws RetrievalException
	{
		return translateArtifact(returnValue);
	}

	@Override
	public VistaQuery generateFindByExampleQuery(Artifact artifact)
	{
		//
		// This is a keylist query
		//
		VistaQuery vm = new VistaQuery(RPC_GET_A_W_KL_AND_AIS_BY_KL);
		HashMap <String, String> hm = new HashMap <String, String>();
		List<Key> keyList = artifact.getKeyList();
		for(int index=0; index<keyList.size(); ++index)
		{
			hm.put((String.valueOf(index+1)), keyList.get(index).getValue());
		}
		vm.addParameter(VistaQuery.ARRAY, hm);
		
		return vm;
	}

	@Override
	public List<Artifact> translateFindByExample(Artifact t, String returnValue) throws RetrievalException 
	{
		return translateArtifactList(returnValue);
	}

	

}