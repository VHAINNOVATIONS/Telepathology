/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 01, 2007
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWLOUTHJ
  Description: 

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
package gov.va.med.imaging.exchange.business.storage;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.storage.exceptions.StorageConfigurationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class StorageServerDatabaseConfiguration 
{
	private static StorageServerDatabaseConfiguration storageServerConfiguration = null;
	final Logger logger = Logger.getLogger(StorageServerDatabaseConfiguration.class);

	private static ProviderFactory providerFactory;
	private static ConfigurationDataSource dataSource;
	private static StorageServerDatabaseConfiguration config;
	
	public static void setProviderFactory(ProviderFactory providerFactory)
	{
		StorageServerDatabaseConfiguration.providerFactory = providerFactory;
	}
	
	public static void setConfigurationDataSource(ConfigurationDataSource dataSource)
	{
		StorageServerDatabaseConfiguration.dataSource = dataSource;
	}
	
	public static StorageServerDatabaseConfiguration getConfiguration()
	{
		if (config == null)
		{
			config = dataSource.getConfigurationFromDataSource(providerFactory);
		}
		return config;
	}
	
	public StorageServerDatabaseConfiguration() 
	{
	}

	// Configuration entity lists
	private List<ArtifactDescriptor> artifactDescriptors;
	private List<RetentionPolicy> retentionPolicies;
	private List<RetentionPolicyProviderMapping> retentionPolicyProviderMappings;
	private List<Provider> providers;
	private List<ProviderAvailability> providerAvailabilityTimes;
	private List<Place> places;
	
	// "Indexes"
	private Map<String, ArtifactDescriptor> artifactDescriptorIndexByTypeAndFormat;
	private Map<Integer, ArtifactDescriptor> artifactDescriptorIndexById;
	private Map<Integer, RetentionPolicy> retentionPolicyIndexById;
	private Map<Integer, Provider> providerIndexById;
	private Map<Integer, Place> placeIndexById;
	private Map<String, Place> placeIndexBySiteNumber;

	private List<String> validationErrors = new ArrayList<String>();
	private boolean isInitialized = false;

	public boolean isInitialized()
	{
		return isInitialized;
	}
	
	public boolean isValid()
	{
		return (validationErrors.size() == 0);
	}
	
	public List<String> getValidationErrors()
	{
		return validationErrors;
	}
	
	public ArtifactDescriptor getArtifactDescriptorByTypeAndFormat(String type, String format)
	{
		return artifactDescriptorIndexByTypeAndFormat.get(getArtifactDescriptorIndexKey(type, format));
	}

	public ArtifactDescriptor getArtifactDescriptorById(int id)
	{
		return artifactDescriptorIndexById.get(id);
	}

	public RetentionPolicy getRetentionPolicyById(int id)
	{
		return retentionPolicyIndexById.get(id);
	}

	public Provider getProviderById(int id)
	{
		return providerIndexById.get(id);
	}

	public Place getPlace(int placeId)
	{
		return this.placeIndexById.get(placeId);
	}
	
	public Place getPlace(String siteNumber)
	{
		return this.placeIndexBySiteNumber.get(siteNumber);
	}

	public synchronized void initializeConfiguration(
			ProviderFactory providerFactory,
			List<ArtifactDescriptor> artifactDescriptors, 
			List<RetentionPolicy> retentionPolicies, 
			List<RetentionPolicyProviderMapping> retentionPolicyProviderMappings, 
			List<Provider> providers, 
			List<ProviderAvailability> providerAvailabilityTimes,
			List<Place> places) throws StorageConfigurationException
			
	{
		// Set the variables and create indexes
		this.places = places;
		createPlaceIndexById();
		createPlaceIndexBySiteNumber();

		this.artifactDescriptors = artifactDescriptors;
		createArtifactDescriptorIndexByTypeAndFormat();
		createArtifactDescriptorIndexById();

		this.retentionPolicies = retentionPolicies;
		createRetentionPolicyIndexById();

		createAndSortProviderList(providerFactory, providers);
		createProviderIndexById();

		this.retentionPolicyProviderMappings = retentionPolicyProviderMappings;
		this.providerAvailabilityTimes = providerAvailabilityTimes;

		//
		// Link up the objects into a connected object graph
		//
		linkArtifactDescriptorsToRetentionPolicies();
		linkRetentionPolicyProviderMappings();
		linkProviderAndProviderAvailability();
		
		//
		// Validate the configuration
		//
		validateStorageConfig();
		
		isInitialized = true;
		
	}

	private void createAndSortProviderList(ProviderFactory providerFactory, List<Provider> baseProviders)  throws StorageConfigurationException
	{
		List<Provider> primaryStorageProviders = new ArrayList<Provider>();
		List<Provider> archiveOnlyProviders = new ArrayList<Provider>();
		
		// Sort the providers, and create their subclasses
		for (Provider provider : baseProviders)
		{
			// Link the provider to a Place
			Place place = placeIndexById.get(provider.getPlaceId());
			provider.setPlace(place);
			
			if (provider.isPrimaryStorage())
			{
				primaryStorageProviders.add(providerFactory.createProvider(provider));
			}
			else
			{
				archiveOnlyProviders.add(providerFactory.createProvider(provider));
			}
		}
		
		// Clear the current provider list, and store the configured providers in primary storage first order.
		providers = new ArrayList<Provider>();
		providers.addAll(primaryStorageProviders);
		providers.addAll(archiveOnlyProviders);
		
		// Link each provider to its place record
		
	}

	private void createArtifactDescriptorIndexByTypeAndFormat() 
	{
		artifactDescriptorIndexByTypeAndFormat = new HashMap<String, ArtifactDescriptor>();
		for (ArtifactDescriptor descriptor : artifactDescriptors)
		{
			String key = getArtifactDescriptorIndexKey(descriptor);
			artifactDescriptorIndexByTypeAndFormat.put(key, descriptor);
		}
	}

	private void createArtifactDescriptorIndexById() 
	{
		artifactDescriptorIndexById = new HashMap<Integer, ArtifactDescriptor>();
		for (ArtifactDescriptor descriptor : artifactDescriptors)
		{
			String key = getArtifactDescriptorIndexKey(descriptor);
			artifactDescriptorIndexById.put(new Integer(descriptor.getId()), descriptor);
		}
	}

	private void createRetentionPolicyIndexById() 
	{
		retentionPolicyIndexById = new HashMap<Integer, RetentionPolicy>();
		for (RetentionPolicy retentionPolicy: retentionPolicies)
		{
			retentionPolicyIndexById.put(new Integer(retentionPolicy.getId()), retentionPolicy);
		}
	}

	private void createProviderIndexById() 
	{
		providerIndexById = new HashMap<Integer, Provider>();
		for (Provider provider: providers)
		{
			providerIndexById.put(new Integer(provider.getId()), provider);
		}
	}

	private void createPlaceIndexById() 
	{
		placeIndexById = new HashMap<Integer, Place>();
		for (Place place: places)
		{
			placeIndexById.put(new Integer(place.getId()), place);
		}
	}

	private void createPlaceIndexBySiteNumber() 
	{
		placeIndexBySiteNumber = new HashMap<String, Place>();
		for (Place place: places)
		{
			placeIndexBySiteNumber.put(place.getSiteNumber(), place);
		}
	}

	private void linkArtifactDescriptorsToRetentionPolicies() 
	{
		for (ArtifactDescriptor descriptor : artifactDescriptors)
		{
			RetentionPolicy intrinsicRetentionPolicy = retentionPolicyIndexById.get(new Integer(descriptor.getRetentionPolicyId()));
			descriptor.setRetentionPolicy(intrinsicRetentionPolicy);
		}
	}
	private void linkRetentionPolicyProviderMappings() 
	{
		// Link to correct retentionPolicy, Provider, and Place
		for (RetentionPolicyProviderMapping mapping : retentionPolicyProviderMappings)
		{
			RetentionPolicy retentionPolicy = retentionPolicyIndexById.get(new Integer(mapping.getRetentionPolicyId()));
			mapping.setRetentionPolicy(retentionPolicy);
			retentionPolicy.getRetentionPolicyProviderMappings().add(mapping);

			Provider provider = providerIndexById.get(mapping.getProviderId());
			mapping.setProvider(provider);
			
			Place place = placeIndexById.get(mapping.getPlaceId());
			mapping.setPlace(place);
			
		}
	}
	private void linkProviderAndProviderAvailability() 
	{
		// Link the providerAvailability to the correct provider and place
		for (ProviderAvailability availability : providerAvailabilityTimes)
		{
			Provider provider = providerIndexById.get(new Integer(availability.getProviderId()));
			availability.setProvider(provider);
			provider.addProviderAvailability(availability);
			
			Place place = placeIndexById.get(availability.getPlaceId());
			availability.setPlace(place);
			
		}
		
	}
	
	private List<String> validateStorageConfig() 
	{
		validationErrors = new ArrayList<String>();
		
		// Check to make sure no artifact descriptors have null retention policies
		for (ArtifactDescriptor descriptor : artifactDescriptors)
		{
			if (descriptor.getRetentionPolicy() == null)
			{
				validationErrors.add("ArtifactDescriptor with Id '" + descriptor.getId() + "' has no RetentionPolicy assigned.");
			}
		}
		
		// Check RetentionPolicyProviderMappings have null places
		for (RetentionPolicyProviderMapping mapping : retentionPolicyProviderMappings)
		{
			if (mapping.getPlaceId() == 0)
			{
				validationErrors.add("RetentionPolicyProviderMapping with Id '" + mapping.getId() + "' has no assigned PlaceId.");
			}
		}
		
		// Check Providers have null places
		for (Provider provider : providers)
		{
			if (provider.getPlaceId() == 0)
			{
				validationErrors.add("Provider with Id '" + provider.getId() + "' has no assigned PlaceId.");
			}
		}
		
		// Check ProviderAvailabilities have null places
		for (ProviderAvailability availability : providerAvailabilityTimes)
		{
			if (availability.getPlaceId() == 0)
			{
				validationErrors.add("ProviderAvailability with Id '" + availability.getId() + "' has no assigned PlaceId.");
			}
		}
		
		// Check to make sure retention policies are met by the assigned providers/mappings, and that at least 
		// one provider will by called synchronously
		for (RetentionPolicy retentionPolicy : retentionPolicies)
		{
			int numArchiveProviders = 0;
			int numOffsiteProviders = 0;
			boolean atLeastOneSynchronousProvider = false;
			
			for(RetentionPolicyProviderMapping mapping : retentionPolicyProviderMappings)
			{
				if (mapping.getProvider().isArchive())
					numArchiveProviders++;
				
				if (mapping.isOffsite())
					numOffsiteProviders++;
				
				if (mapping.isSynchronous())
					atLeastOneSynchronousProvider = true;
			}
			
			if (numArchiveProviders < retentionPolicy.getMinimumArchiveCopies())
			{
				StringBuffer buffer = new StringBuffer();
				buffer.append("RetentionPolicy with Id " + retentionPolicy + " requires at least ");
				buffer.append(retentionPolicy.getMinimumArchiveCopies() + " archive copies, but is");
				buffer.append(" only configured with " + numArchiveProviders + " archive providers.");
				validationErrors.add(buffer.toString());
			}

			if (numOffsiteProviders < retentionPolicy.getMinimumOffsiteCopies())
			{
				StringBuffer buffer = new StringBuffer();
				buffer.append("RetentionPolicy with Id " + retentionPolicy + " requires at least ");
				buffer.append(retentionPolicy.getMinimumOffsiteCopies() + " offsite copies, but is");
				buffer.append(" only configured with " + numOffsiteProviders + " offsite providers.");
				validationErrors.add(buffer.toString());
			}

			if (!atLeastOneSynchronousProvider)
			{
				StringBuffer buffer = new StringBuffer();
				buffer.append("RetentionPolicy with Id " + retentionPolicy + " has no synchronous providers assigned.");
				validationErrors.add(buffer.toString());
			}
		}
		
		return validationErrors;
	}

	private String getArtifactDescriptorIndexKey(ArtifactDescriptor descriptor) 
	{
		return getArtifactDescriptorIndexKey(descriptor.getArtifactType(), descriptor.getArtifactFormat());
	}

	private String getArtifactDescriptorIndexKey(String type, String format) 
	{
		return type + "_" + format;
	}

	public List<ArtifactInstanceProviderPair> getProvidersForRetrieval(Artifact artifact) throws MethodException, ConnectionException
	{
		List<ArtifactInstanceProviderPair> artifactInstanceProviderPairs = new ArrayList<ArtifactInstanceProviderPair>();

		// Get the artifactInstances for this artifact
		for (Provider provider : providers)
		{
			for (ArtifactInstance instance : artifact.getArtifactInstances())
			{
				if (provider.canRetrieveFromArtifactInstance(instance))
				{
					artifactInstanceProviderPairs.add(new ArtifactInstanceProviderPair(provider, instance));
				}
			}
		}

		return artifactInstanceProviderPairs;
	}
	
	public Provider getPrimaryStorageProviderForSite(String siteNumber)
	{
		for (Provider provider : providers)
		{
			if (siteNumber.equals(provider.getPlace().getSiteNumber()) && provider.isPrimaryStorage())
			{
				return provider;
			}
		}
		
		return null;
	}
}
