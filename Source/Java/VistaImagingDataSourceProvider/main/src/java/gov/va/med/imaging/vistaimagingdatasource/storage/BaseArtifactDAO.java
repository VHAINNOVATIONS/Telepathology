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
import gov.va.med.imaging.exchange.business.storage.ArtifactDescriptor;
import gov.va.med.imaging.exchange.business.storage.ArtifactInstance;
import gov.va.med.imaging.exchange.business.storage.ArtifactRetentionPolicy;
import gov.va.med.imaging.exchange.business.storage.Key;
import gov.va.med.imaging.exchange.business.storage.Provider;
import gov.va.med.imaging.exchange.business.storage.RetentionPolicy;
import gov.va.med.imaging.exchange.business.storage.RetentionPolicyFulfillment;
import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;
import gov.va.med.imaging.exchange.business.storage.StorageServerDatabaseConfiguration;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.url.vista.StringUtils;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

public class BaseArtifactDAO extends StorageDAO<Artifact>
{
	//
	// RPC Names
	//
	protected final static String RPC_CREATE_A_W_KL = "MAGVA CREATE ARTIFACT W KL";				
	protected final static String RPC_GET_A_W_KL = "MAGVA GET ARTIFACT W KL";						
	protected final static String RPC_GET_A_W_KL_AND_AIS = "MAGVA GET A W KL AND AIS"; 			
	protected final static String RPC_GET_A_AIS_ARPS_AND_RPFFS = "MAGVA GET A AIS ARPS AND RPFFS";	
	protected final static String RPC_GET_A_W_KL_AND_AIS_BY_KL = "MAGVA GET A W KL AND AIS BY KL";	
	protected final static String RPC_GET_A_W_KL_AND_AIS_BY_PK = "MAGVA GET A W KL AND AIS BY PK";	
	protected final static String RPC_UPDATE_ARTIFACT = "MAGVA UPDATE ARTIFACT";				
	
	//
	// Artifact table (2006.916) fields
	//
	protected final static String A_PK = "PK";
	protected final static String A_TOKEN = "ARTIFACT TOKEN";
	protected final static String A_AD_FK = "ARTIFACT DESCRIPTOR";  // was .. FK
	protected final static String A_KL_FK = "KEYLIST";  // was .. FK
	protected final static String A_SIZE_IN_BYTES = "SIZE IN BYTES";
	protected final static String A_CRC = "CRC";
	protected final static String A_CREATED_BY = "CREATING APPLICATION"; // was CREATED BY
	protected final static String A_CREATION_DATETIME = "CREATED DATE/TIME"; // was "CREATION DATETIME"
	protected final static String A_LAST_ACCESS_DATIME = "LAST ACCESS DATE/TIME"; // was DATETIME
	protected final static String A_KL = "KLIST";
//	protected final static String A_IMG_INST_FILE_FK = "IMAGE INSTANCE FILE REFERENCE"; // newly added 09/05/11
	
	private StorageServerDatabaseConfiguration dbConfig = StorageServerDatabaseConfiguration.getConfiguration();
	
	protected Artifact translateArtifact(String returnValue) throws RetrievalException
	{
		//
		// Get the list of artifacts from the XML
		//
		List<Artifact> artifacts = translateArtifactList(returnValue);
		
		//
		// Return as appropriate based on the list contents
		//
		if (artifacts.size() > 1)
		{
			throw new RetrievalException("Expected one artifact, but found " + artifacts.size());
		}
		else if (artifacts.size() == 0)
		{
			return null;
		}
		else
		{
			return artifacts.get(0);
		}

	}

	@SuppressWarnings("unchecked")
	protected List<Artifact> translateArtifactList(String returnValue) throws RetrievalException
	{
		List<Artifact> artifacts = new ArrayList<Artifact>();
		int index = returnValue.indexOf('<');

		
		if (index > 0 && returnValue.startsWith("0^"))
		{
			// Since the return value starts with "0`", the 
			// result was OK, and we have XML...
			String xml = returnValue.substring(index);
			
			try {
				XStream xstream = getConfiguredXStream();
				artifacts = (List<Artifact>)xstream.fromXML(xml);
			} catch (XStreamException xsX) {
				throw new RetrievalException(xsX.getMessage());
			}
			postProcessArtifactList(artifacts);
		}
		else
		{
			// Return value did not start with "0`", or there was no XML in the
			// result. There was a problem. Throw exception with error message.
			String status = returnValue.substring(0, index);
			throw new RetrievalException(StringUtils.Split(status, STORAGE_FIELD_SEPARATOR)[1]);
		}
		return artifacts;
	}

	private void postProcessArtifactList(List<Artifact> artifacts) 
	{
		for (Artifact artifact : artifacts)
		{
			postProcessArtifact(artifact);
		}
	}

	private void postProcessArtifact(Artifact artifact) 
	{
		
		// Link Artifact to ArtifactDescriptor
		ArtifactDescriptor descriptor = dbConfig.getArtifactDescriptorById(artifact.getArtifactDescriptorId());
		artifact.setArtifactDescriptor(descriptor);

		// Postprocess ArtifactInstances
		if (artifact.getArtifactInstances() != null)
		{
			for (ArtifactInstance instance : artifact.getArtifactInstances())
			{
				// "Backlink" ArtifactInstances to Artifact
				instance.setArtifact(artifact);
				
				// Link ArtifactInstances to Providers
				Provider provider = dbConfig.getProviderById(instance.getProviderId());
				instance.setProvider(provider);
				
				// Link and Backlink ArtifactInstances with RetentionPolicyFulfillments
				List<RetentionPolicyFulfillment> fulfillmentsForInstance = getFulfillmentsForArtifactInstance(artifact, instance.getId());
				for (RetentionPolicyFulfillment fulfillment : fulfillmentsForInstance)
				{
					if (instance.getRetentionPolicyFulfillments() == null){
						instance.setRetentionPolicyFulfillments(new ArrayList<RetentionPolicyFulfillment>());
					}
					instance.getRetentionPolicyFulfillments().add(fulfillment);
					fulfillment.setArtifactInstance(instance);
				}
			}
		}
		
		// Postprocess ArtifactRetentionPolicies
		List<ArtifactRetentionPolicy> artifactRetentionPolicies = artifact.getArtifactRetentionPolicies();
		if (artifact.getArtifactRetentionPolicies() != null)
		{
			for (ArtifactRetentionPolicy artifactRetentionPolicy : artifactRetentionPolicies)
			{
				// Link ArtifactRetentionPolicy to RetentionPolicy
				int retentionPolicyId = artifactRetentionPolicy.getRetentionPolicyId();
				RetentionPolicy retentionPolicy = dbConfig.getRetentionPolicyById(retentionPolicyId);
				artifactRetentionPolicy.setRetentionPolicy(retentionPolicy);

				// "Backlink" ArtifactRetentionPolicies to Artifact
				artifactRetentionPolicy.setArtifact(artifact);
				
				// "Backlink" RetentionPolicyFulfillments to ArtifactRetentionPolicy
				List<RetentionPolicyFulfillment> retentionPolicyFulfillments = artifactRetentionPolicy.getRetentionPolicyFulfillments();
				if (retentionPolicyFulfillments != null)
				{
					for (RetentionPolicyFulfillment fulfillment : retentionPolicyFulfillments)
					{
						fulfillment.setArtifactRetentionPolicy(artifactRetentionPolicy);
					}
				}
				
			}
		}
	}
	
	private List<RetentionPolicyFulfillment> getFulfillmentsForArtifactInstance(Artifact artifact, int artifactInstanceId)
	{
		List<RetentionPolicyFulfillment> fulfillmentsForInstance = new ArrayList<RetentionPolicyFulfillment>();
		
		List<ArtifactRetentionPolicy> artifactRetentionPolicies = artifact.getArtifactRetentionPolicies();
		if (artifactRetentionPolicies != null)
		{
			for (ArtifactRetentionPolicy retentionPolicy : artifactRetentionPolicies)
			{
				List<RetentionPolicyFulfillment> fulfillmentsForRetentionPolicy = retentionPolicy.getRetentionPolicyFulfillments();
				if (fulfillmentsForRetentionPolicy != null)
				{
					for (RetentionPolicyFulfillment fulfillment : fulfillmentsForRetentionPolicy)
					{
						if (fulfillment.getArtifactInstanceId() == artifactInstanceId)
						{
							fulfillmentsForInstance.add(fulfillment);
						}
					}
				}
			}
		}
		
		return fulfillmentsForInstance;
		
	}
	protected XStream getConfiguredXStream() 
	{
		XStream xstream = new XStream();
		
		// Aliases for Artifact
		xstream.alias("ARTIFACTS", ArrayList.class);

		xstream.alias("ARTIFACT", Artifact.class);
		xstream.aliasAttribute(Artifact.class, "id",  "PK");
		xstream.useAttributeFor(Artifact.class, "id");

		xstream.aliasAttribute(Artifact.class, "artifactToken",  "ARTIFACTTOKEN");
		xstream.useAttributeFor(Artifact.class, "artifactToken");

		xstream.aliasAttribute(Artifact.class, "artifactDescriptorId",  "ARTIFACTDESCRIPTOR");
		xstream.useAttributeFor(Artifact.class, "artifactDescriptorId");

		xstream.aliasAttribute(Artifact.class, "keyListId",  "KEYLIST");
		xstream.useAttributeFor(Artifact.class, "keyListId");

		xstream.aliasAttribute(Artifact.class, "sizeInBytes",  "SIZEINBYTES");
		xstream.useAttributeFor(Artifact.class, "sizeInBytes");

		xstream.aliasAttribute(Artifact.class, "CRC",  "CRC");
		xstream.useAttributeFor(Artifact.class, "CRC");

		xstream.aliasAttribute(Artifact.class, "createdBy",  "CREATINGAPPLICATION");
		xstream.useAttributeFor(Artifact.class, "createdBy");

		xstream.aliasAttribute(Artifact.class, "createdDateTime",  "CREATEDDATETIME");
		xstream.useAttributeFor(Artifact.class, "createdDateTime");

		xstream.aliasAttribute(Artifact.class, "lastAccessDateTime",  "LASTACCESSDATETIME");
		xstream.useAttributeFor(Artifact.class, "lastAccessDateTime");


		// Aliases for KeyList
		xstream.aliasField("KEYS", Artifact.class, "keyList");
		xstream.alias("KEY", Key.class);

		xstream.aliasAttribute(Key.class, "level",  "KLEVEL");
		xstream.useAttributeFor(Key.class, "level");

		xstream.aliasAttribute(Key.class, "value",  "KVALUE");
		xstream.useAttributeFor(Key.class, "value");

		// Aliases for ArtifactInstance
		xstream.aliasField("ARTIFACTINSTANCES", Artifact.class, "artifactInstances");
		xstream.alias("ARTIFACTINSTANCE", ArtifactInstance.class);
		
		xstream.aliasAttribute(ArtifactInstance.class, "id",  "PK");
		xstream.useAttributeFor(ArtifactInstance.class, "id");

		xstream.aliasAttribute(ArtifactInstance.class, "artifactId",  "ARTIFACT");
		xstream.useAttributeFor(ArtifactInstance.class, "artifactId");

		xstream.aliasAttribute(ArtifactInstance.class, "providerId",  "STORAGEPROVIDER");
		xstream.useAttributeFor(ArtifactInstance.class, "providerId");

		xstream.aliasAttribute(ArtifactInstance.class, "createdDateTime",  "CREATEDDATETIME");
		xstream.useAttributeFor(ArtifactInstance.class, "createdDateTime");

		xstream.aliasAttribute(ArtifactInstance.class, "lastAccessDateTime",  "LASTACCESSDATETIME");
		xstream.useAttributeFor(ArtifactInstance.class, "lastAccessDateTime");

		xstream.aliasAttribute(ArtifactInstance.class, "url",  "URL");
		xstream.useAttributeFor(ArtifactInstance.class, "url");
		
		xstream.aliasAttribute(ArtifactInstance.class, "fileRef",  "FILEREF");
		xstream.useAttributeFor(ArtifactInstance.class, "fileRef");
		
		xstream.aliasAttribute(ArtifactInstance.class, "filePath",  "FILEPATH");
		xstream.useAttributeFor(ArtifactInstance.class, "filePath");
		
		xstream.aliasAttribute(ArtifactInstance.class, "diskVolume",  "DISKVOLUME");
		xstream.useAttributeFor(ArtifactInstance.class, "diskVolume");

		// Aliases for ArtifactRetentionPolicy
		xstream.aliasField("ARTIFACTRETENTIONPOLICYS", Artifact.class, "artifactRetentionPolicies");
		xstream.alias("ARTIFACTRETENTIONPOLICY", ArtifactRetentionPolicy.class);
		
		xstream.aliasAttribute(ArtifactRetentionPolicy.class, "id",  "PK");
		xstream.useAttributeFor(ArtifactRetentionPolicy.class, "id");

		xstream.aliasAttribute(ArtifactRetentionPolicy.class, "artifactId",  "ARTIFACT");
		xstream.useAttributeFor(ArtifactRetentionPolicy.class, "artifactId");

		xstream.aliasAttribute(ArtifactRetentionPolicy.class, "retentionPolicyId",  "RETENTIONPOLICY");
		xstream.useAttributeFor(ArtifactRetentionPolicy.class, "retentionPolicyId");

		xstream.aliasAttribute(ArtifactRetentionPolicy.class, "createdDateTime",  "CREATEDDATETIME");
		xstream.useAttributeFor(ArtifactRetentionPolicy.class, "createdDateTime");

		xstream.aliasAttribute(ArtifactRetentionPolicy.class, "isActive",  "ACTIVE");
		xstream.useAttributeFor(ArtifactRetentionPolicy.class, "isActive");

//		xstream.aliasAttribute(ArtifactRetentionPolicy.class, "isSatisfied",  "SATISFIED");
//		xstream.useAttributeFor(ArtifactRetentionPolicy.class, "isSatisfied");

		xstream.aliasAttribute(ArtifactRetentionPolicy.class, "satisfiedDateTime",  "SATISFIEDDATETIME");
		xstream.useAttributeFor(ArtifactRetentionPolicy.class, "satisfiedDateTime");

		
		// Aliases for RetentionPolicyFulfillment
		xstream.aliasField("RETENTIONPOLICYFULFILLMENTS", ArtifactRetentionPolicy.class, "retentionPolicyFulfillments");
		xstream.alias("RETENTIONPOLICYFULFILLMENT", RetentionPolicyFulfillment.class);
		
		xstream.aliasAttribute(RetentionPolicyFulfillment.class, "id",  "PK");
		xstream.useAttributeFor(RetentionPolicyFulfillment.class, "id");

		xstream.aliasAttribute(RetentionPolicyFulfillment.class, "artifactRetentionPolicyId",  "ARTIFACTRETENTIONPOLICY");
		xstream.useAttributeFor(RetentionPolicyFulfillment.class, "artifactRetentionPolicyId");

		xstream.aliasAttribute(RetentionPolicyFulfillment.class, "artifactInstanceId",  "ARTIFACTINSTANCE");
		xstream.useAttributeFor(RetentionPolicyFulfillment.class, "artifactInstanceId");

		xstream.aliasAttribute(RetentionPolicyFulfillment.class, "createdDateTime",  "CREATEDDATETIME");
		xstream.useAttributeFor(RetentionPolicyFulfillment.class, "createdDateTime");

		return xstream;
	}

}
