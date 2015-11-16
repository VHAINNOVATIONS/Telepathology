package gov.va.med.imaging.datasource;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.exchange.business.TimePeriod;
import gov.va.med.imaging.exchange.business.storage.Artifact;
import gov.va.med.imaging.exchange.business.storage.ArtifactDescriptor;
import gov.va.med.imaging.exchange.business.storage.ArtifactInstance;
import gov.va.med.imaging.exchange.business.storage.ArtifactRetentionPolicy;
import gov.va.med.imaging.exchange.business.storage.KeyList;
import gov.va.med.imaging.exchange.business.storage.NetworkLocationInfo;
import gov.va.med.imaging.exchange.business.storage.Provider;
import gov.va.med.imaging.exchange.business.storage.ProviderAvailability;
import gov.va.med.imaging.exchange.business.storage.ProviderFactory;
import gov.va.med.imaging.exchange.business.storage.RetentionPolicy;
import gov.va.med.imaging.exchange.business.storage.RetentionPolicyFulfillment;
import gov.va.med.imaging.exchange.business.storage.RetentionPolicyProviderMapping;
import gov.va.med.imaging.exchange.business.storage.StorageServerDatabaseConfiguration;
import gov.va.med.imaging.exchange.business.storage.StorageTransaction;
import gov.va.med.imaging.exchange.business.storage.TransferStatistics;

import java.util.List;

/**
 * This class defines the Service Provider Interface (SPI) for the StorageDataSource. 
 * All the abstract methods in this class must be implemented by each 
 * data source service provider who wishes to supply the implementation of a 
 * StorageDataSource for a particular datasource type.
 * 
 * @since 1.0
 * @author VHAISWLOUTHJ
 *
 */
@SPI(description="Defines the interface to for the storage subsystem.")
public interface StorageDataSourceSpi
extends VersionableDataSourceSpi
{
	// ArtifactDescriptor
	public List<ArtifactDescriptor> findAllArtifactDescriptors() throws MethodException, ConnectionException;
	public ArtifactDescriptor setIntrinsicRetentionPolicy(ArtifactDescriptor artifactDescriptor, RetentionPolicy retentionPolicy) throws MethodException, ConnectionException;

	// RetentionPolicy
	public List<RetentionPolicy> findAllRetentionPolicies() throws MethodException, ConnectionException;

	// RetentionPolicyProviderMap
	public List<RetentionPolicyProviderMapping> findAllRetentionPolicyProviderMappings() throws MethodException, ConnectionException;
	public RetentionPolicyProviderMapping createRetentionPolicyProviderMapping(RetentionPolicyProviderMapping retentionPolicyProviderMapping) throws MethodException, ConnectionException;
	public RetentionPolicyProviderMapping updateRetentionPolicyProviderMapping(RetentionPolicyProviderMapping retentionPolicyProviderMap) throws MethodException, ConnectionException;
	public void deleteRetentionPolicyProviderMapping(int retentionPolicyProviderMapID) throws MethodException, ConnectionException;

	// Provider
	public List<Provider> findAllProviders() throws MethodException, ConnectionException;
	public NetworkLocationInfo getCurrentWriteLocation(Provider provider) throws MethodException, ConnectionException;
	public NetworkLocationInfo getCurrentJukeboxWriteLocation(Provider provider) throws MethodException, ConnectionException;
	public NetworkLocationInfo getNetworkLocationDetails(String networkLocationIEN) throws MethodException, ConnectionException;
	public Provider createProvider(Provider provider) throws MethodException, ConnectionException;
	public Provider updateProvider(Provider provider) throws MethodException, ConnectionException;

	// ProviderAvailability
	public List<ProviderAvailability> findAllProviderAvailability() throws MethodException, ConnectionException;
	public ProviderAvailability createProviderAvailability(ProviderAvailability providerAvailability) throws MethodException, ConnectionException;
	public ProviderAvailability updateProviderAvailability(ProviderAvailability providerAvailability) throws MethodException, ConnectionException;
	public void deleteProviderAvailability(int providerAvailabilityID) throws MethodException, ConnectionException;

	// Artifact
	public Artifact getArtifactByToken(String token) throws MethodException, ConnectionException;
	public Artifact getArtifactAndInstancesByToken(String token) throws MethodException, ConnectionException;
	public Artifact getFullArtifactGraphById(String id) throws MethodException, ConnectionException;
	public Artifact getFullArtifactGraphByToken(String token) throws MethodException, ConnectionException;
	public Artifact getArtifactAndInstancesByKeyList(KeyList keyList) throws MethodException, ConnectionException;
	public Artifact getArtifactAndInstancesById(int artifactID) throws MethodException, ConnectionException;
	public Artifact createArtifact(Artifact artifact) throws MethodException, ConnectionException;
	public Artifact updateArtifact(Artifact artifact) throws MethodException, ConnectionException;

	// ArtifactInstance
	public ArtifactInstance createArtifactInstance(ArtifactInstance artifactInstance) throws MethodException, ConnectionException;
	public ArtifactInstance updateArtifactInstanceLastAccessed(ArtifactInstance artifactInstance) throws MethodException, ConnectionException;
	public ArtifactInstance updateArtifactInstanceUrl(ArtifactInstance artifactInstance) throws MethodException, ConnectionException;

	// StorageTransaction
	public StorageTransaction createStorageTransaction(StorageTransaction storageTransaction) throws MethodException, ConnectionException;
	public int getStorageTransactionCountWithinTimePeriod(String type, String status, TimePeriod period) throws MethodException, ConnectionException;

	// ArtifactRetentionPolicy
	public ArtifactRetentionPolicy createArtifactRetentionPolicy(ArtifactRetentionPolicy artifactRetentionPolicy) throws MethodException, ConnectionException;
	public ArtifactRetentionPolicy updateArtifactRetentionPolicy(ArtifactRetentionPolicy artifactRetentionPolicy) throws MethodException, ConnectionException;

	// RetentionPolicyFulfillment
	public RetentionPolicyFulfillment createRetentionPolicyFulfillment(RetentionPolicyFulfillment retentionPolicyFulfillment) throws MethodException, ConnectionException;

	// TransferStatistics
	public TransferStatistics createTransferStatistics(TransferStatistics transferStatistics) throws MethodException, ConnectionException;

	// Configuration
	public StorageServerDatabaseConfiguration getStorageServerDatabaseConfiguration(ProviderFactory providerFactory) throws MethodException, ConnectionException;
}
