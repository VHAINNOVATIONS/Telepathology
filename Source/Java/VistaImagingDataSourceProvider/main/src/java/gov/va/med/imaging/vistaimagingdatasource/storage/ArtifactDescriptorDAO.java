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
import gov.va.med.imaging.exchange.business.storage.ArtifactDescriptor;
import gov.va.med.imaging.exchange.business.storage.RetentionPolicy;
import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.protocol.vista.DicomTranslatorUtility;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ArtifactDescriptorDAO extends StorageDAO<ArtifactDescriptor>
{
	//
	// RPC Names
	//
	private final static String RPC_GET_ALL_ADS = "MAGVA GET ALL ADS";			// 2
	private final static String RPC_SET_AD_RETPOL = "MAGVA SET AD RETPOL";		
	
	//
	// Artifact Descriptor table (2006.915) fields
	//
	private final static String AD_KEY = "PK";
	private final static String AD_RETPOL_FK = "RETENTION POLICY"; // was .. FK
	private final static String AD_AF_TYPE = "ARTIFACT TYPE";
	private final static String AD_AF_FORMAT = "ARTIFACT FORMAT";
	private final static String AD_FILE_EXT = "FILE EXTENSION";
	private final static String AD_IS_ACTIVE = "ACTIVE"; // was "IS ACTIVE";

	//
	// Constructor
	//
	public ArtifactDescriptorDAO(){}
	public ArtifactDescriptorDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	//
	// Retrieve All overrides
	//
	@Override
	public VistaQuery generateFindAllQuery() 
	{
		VistaQuery vm = new VistaQuery(RPC_GET_ALL_ADS);
		return vm;
	}

	@Override
	public List<ArtifactDescriptor> translateFindAll(String returnValue) throws RetrievalException
	{
		List<ArtifactDescriptor> list = new ArrayList<ArtifactDescriptor>();

		String[] resultLines = DicomTranslatorUtility.createResultsArray(returnValue);
		checkRetrievalStatus(resultLines);
		
		if (resultLines.length > 2)
		{
			// We have at least one result row. Start at the third line, 
			// skipping status and header row...
			for (int i=2; i<resultLines.length; i++)
			{
				String[] fields = StringUtils.Split(resultLines[i], STORAGE_FIELD_SEPARATOR);
				ArtifactDescriptor descriptor = new ArtifactDescriptor(
						Integer.parseInt(fields[0]),
						Integer.parseInt(fields[1]),
						fields[2],
						fields[3],
						fields[4],
						getBooleanValue(fields[5]));
				
				list.add(descriptor);
				
			}
		}
		
		return list;
	}

	//
	// Update Retention Policy
	//
	public ArtifactDescriptor updateRetentionPolicy(ArtifactDescriptor artifactDescriptor, RetentionPolicy retentionPolicy) throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateUpdateRetentionPolicyQuery(artifactDescriptor.getId(), retentionPolicy.getId());
		return translateUpdateRetentionPolicy(executeRPC(vm));

	}
	
	public VistaQuery generateUpdateRetentionPolicyQuery(int artifactDescriptorPK, int retentionPolicyPK) 
	{
		VistaQuery vm = new VistaQuery(RPC_SET_AD_RETPOL);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(AD_KEY, Integer.toString(artifactDescriptorPK));
		hm.put(AD_RETPOL_FK, Integer.toString(retentionPolicyPK));
		vm.addParameter(VistaQuery.ARRAY, hm);

		return vm;
	}

	public ArtifactDescriptor translateUpdateRetentionPolicy(String returnValue) 
	{
		throw new NotImplementedException();
	}

	
	
}