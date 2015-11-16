package gov.va.med.imaging.core.router.commands.storage;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.BeanUtils;
import gov.va.med.imaging.BusinessKey;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.facade.InternalContext;
import gov.va.med.imaging.core.router.storage.StorageContext;
import gov.va.med.imaging.exchange.business.DurableQueue;
import gov.va.med.imaging.exchange.business.DurableQueueMessage;
import gov.va.med.imaging.exchange.business.storage.Artifact;
import gov.va.med.imaging.exchange.business.storage.ArtifactInstance;
import gov.va.med.imaging.exchange.business.storage.ArtifactRetentionPolicy;
import gov.va.med.imaging.exchange.business.storage.ArtifactWriteResults;
import gov.va.med.imaging.exchange.business.storage.AsyncStorageRequest;
import gov.va.med.imaging.exchange.business.storage.Provider;
import gov.va.med.imaging.exchange.business.storage.ProviderAvailability;
import gov.va.med.imaging.exchange.business.storage.RetentionPolicyFulfillment;
import gov.va.med.imaging.exchange.business.storage.RetentionPolicyProviderMapping;
import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;
import gov.va.med.imaging.exchange.business.storage.StorageServerDatabaseConfiguration;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class ProcessAsyncStorageRequestCommandImpl extends
		AbstractStorageCommandImpl<Boolean> {

	private static final long serialVersionUID = 6234638942705L;
	private Logger logger = Logger
			.getLogger(ProcessAsyncStorageRequestCommandImpl.class);
	private AsyncStorageRequest request;

	public ProcessAsyncStorageRequestCommandImpl(AsyncStorageRequest request) {
		this.request = request;
	}

	private void addRetentionPolicyFullfilmentRecords(
			ArrayList<ArtifactInstance> archivedInstances)
			throws MethodException, ConnectionException {

		RetentionPolicyFulfillment retentionPolicyFulfillment;
		for (ArtifactInstance instance : archivedInstances) {
			for (ArtifactRetentionPolicy arp : getAssociatedArtifactRetentionPolicies(instance)) {
				retentionPolicyFulfillment = new RetentionPolicyFulfillment();
				retentionPolicyFulfillment.setArtifactInstance(instance);
				retentionPolicyFulfillment.setArtifactRetentionPolicy(arp);
				instance.getRetentionPolicyFulfillments().add(
						retentionPolicyFulfillment);
				arp.getRetentionPolicyFulfillments().add(
						retentionPolicyFulfillment);
				StorageContext.getDataSourceRouter()
						.postRetentionPolicyFulfillment(
								retentionPolicyFulfillment);
			}
		}
	}

	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj) {
		// Perform cast for subsequent tests
		final ProcessAsyncStorageRequestCommandImpl other = (ProcessAsyncStorageRequestCommandImpl) obj;
		// Check the studyUrn
		return areFieldsEqual(this.request, other.request);
	}

	private boolean areAllRetentionPoliciesSatisfied(Artifact artifact) {
		return this.getUnsatisfiedMaps(artifact).size() == 0;
	}

	@Override
	public Boolean callSynchronouslyInTransactionContext()
			throws MethodException, ConnectionException {
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getLocalSiteId());
		Artifact artifact = StorageContext.getDataSourceRouter()
				.getArtifactAsGraphByToken(request.getArtifactToken());
		ArrayList<RetentionPolicyProviderMapping> unsatisfiedMaps = getUnsatisfiedMaps(artifact);
		logger
				.info(unsatisfiedMaps.size()
						+ " unsatisfied maps detected for artifact "
						+ artifact.getId());
		if (unsatisfiedMaps.size() > 0) {
			ArtifactInstance currentInstance = artifact.getArtifactInstances()
					.get(0); // this needs to be smarter
			ArrayList<ArtifactInstance> newInstances = writeInstances(
					currentInstance, unsatisfiedMaps);
			addRetentionPolicyFullfilmentRecords(newInstances);
		}
		updateSatisfiedArtifactRetentionPolicies(artifact);
		if (!areAllRetentionPoliciesSatisfied(artifact)) {
			enqueueRetry(artifact);
		}
		return true;
	}

	private boolean canWriteToProvider(Provider provider) {
		ProviderAvailability availability = provider
				.getProviderAvailabilityByPlaceId(StorageServerDatabaseConfiguration
						.getConfiguration().getPlace(getLocalSiteId()).getId());
		if (availability == null) {
			return true;
		}
		return availability.isWithinAvailabilityWindow(Calendar.getInstance());
	}

	private void enqueueRetry(Artifact artifact) throws MethodException,
			ConnectionException {
		Calendar retryDate = getEarliestRetryDate(artifact);
		if (retryDate != null) {
			RoutingToken routingToken = getCommandContext().getLocalSite().getArtifactSource().createRoutingToken();
			DurableQueueMessage qMessage = new DurableQueueMessage();
			qMessage.setMinDeliveryDateTime(retryDate.getTime());
			DurableQueue queue = InternalContext.getRouter()
					.getDurableQueueByName(routingToken,
							AsyncQueueNames.asyncStorageRequestQueue);
			qMessage.setQueue(queue);
			qMessage.setMessageGroupId(Integer.toString(StorageServerDatabaseConfiguration
					.getConfiguration().getPlace(getLocalSiteId()).getId()));
			AsyncStorageRequest request = new AsyncStorageRequest(artifact
					.getArtifactToken());
			qMessage.setMessage(request.serializeUsingXStream());
			InternalContext.getRouter().enqueueDurableQueueMessage(
					routingToken, qMessage);
		}
	}

	private Calendar getEarliestRetryDate(Artifact artifact) {
		Calendar earliestRetryDate = null;
		Calendar availabilityDate;
		ProviderAvailability availability;
		int placeId = StorageServerDatabaseConfiguration.getConfiguration()
				.getPlace(getLocalSiteId()).getId();
		for (RetentionPolicyProviderMapping map : getUnsatisfiedMaps(artifact)) {
			availability = map.getProvider().getProviderAvailabilityByPlaceId(
					placeId);
			if (availability != null) {
				availabilityDate = availability
						.getNextAvailabilityStartDate(placeId);
				if (earliestRetryDate == null
						|| availabilityDate.compareTo(earliestRetryDate) < 0) {
					earliestRetryDate = availabilityDate;
				}
			}
		}
		return earliestRetryDate;
	}

	@Override
	public boolean equals(Object obj) {
		return BeanUtils.equals(this, obj);
	}

	private ArrayList<ArtifactRetentionPolicy> getAssociatedArtifactRetentionPolicies(
			ArtifactInstance instance) {
		ArrayList<ArtifactRetentionPolicy> policies = new ArrayList<ArtifactRetentionPolicy>();
		for (ArtifactRetentionPolicy arp : instance.getArtifact()
				.getArtifactRetentionPolicies()) {
			for (RetentionPolicyProviderMapping map : arp.getRetentionPolicy()
					.getRetentionPolicyProviderMappings()) {
				if (map.getProviderId() == instance.getProviderId()
						&& (!policies.contains(arp))) {
					policies.add(arp);
				}
			}
		}
		return policies;
	}

	@BusinessKey
	public AsyncStorageRequest getRequest() {
		return request;
	}

	private ArrayList<Provider> getTargetProviders(
			ArrayList<RetentionPolicyProviderMapping> unsatisfiedMaps) {
		ArrayList<Provider> providers = new ArrayList<Provider>();
		for (RetentionPolicyProviderMapping map : unsatisfiedMaps) {
			if (!providers.contains(map.getProvider())) {
				providers.add(map.getProvider());
			}
		}
		return providers;
	}

	private ArrayList<RetentionPolicyProviderMapping> getUnsatisfiedMaps(
			Artifact artifact) {
		ArrayList<RetentionPolicyProviderMapping> maps = new ArrayList<RetentionPolicyProviderMapping>();
		int serverPlaceId = StorageServerDatabaseConfiguration
				.getConfiguration().getPlace(getLocalSiteId()).getId();
		// get lists of RetentionPolicyProviderMappings
		for (ArtifactRetentionPolicy arp : artifact
				.getArtifactRetentionPolicies()) {
			for (RetentionPolicyProviderMapping rppm : arp.getRetentionPolicy()
					.getRetentionPolicyProviderMappings()) {
				if (rppm.getPlaceId() == serverPlaceId && !maps.contains(rppm))
					maps.add(rppm);
			}
		}

		// now find RetentionPolicyProviderMappings which don't have an
		// associated artifact instance for this artifact
		ArrayList<RetentionPolicyProviderMapping> unsatisfiedMaps = new ArrayList<RetentionPolicyProviderMapping>();
		boolean isMapSatisfied;
		for (RetentionPolicyProviderMapping map : maps) {
			isMapSatisfied = false;
			for (ArtifactInstance instance : artifact.getArtifactInstances()) {
				if (map.getProviderId() == instance.getProviderId()) {
					isMapSatisfied = true;
					break;
				}
			}
			if (isMapSatisfied == false) {
				unsatisfiedMaps.add(map);
			}
		}

		return unsatisfiedMaps;
	}

	@Override
	public int hashCode() {
		return BeanUtils.hashCode(this);
	}

	@Override
	protected String parameterToString() {
		return request.toString();
	}

	public void setRequest(AsyncStorageRequest request) {
		this.request = request;
	}

	private void updateSatisfiedArtifactRetentionPolicies(Artifact artifact)
			throws MethodException, ConnectionException {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd.HHmmss");
		String rpcDate = df.format(new Date());

		for (ArtifactRetentionPolicy arp : artifact
				.getArtifactRetentionPolicies()) {
			if (arp.checkIsSatisfied(getLocalSiteId())) {
				// arp.setIsSatisfied(true); -- altered 09/05/11
				arp.setSatisfiedDateTime(rpcDate);
				StorageContext.getDataSourceRouter()
						.putArtifactRetentionPolicy(arp);
			}
		}
	}

	private ArrayList<ArtifactInstance> writeInstances(
			ArtifactInstance currentInstance,
			ArrayList<RetentionPolicyProviderMapping> unsatisfiedMaps)
			throws MethodException, ConnectionException {
		ArrayList<Provider> targetProviders = getTargetProviders(unsatisfiedMaps);
		ArtifactWriteResults results;
		ArrayList<ArtifactInstance> newInstances = new ArrayList<ArtifactInstance>();
		String storageDeviceinfo = "";
		for (Provider provider : targetProviders) {
			if (canWriteToProvider(provider)) {
				try {
					storageDeviceinfo = "Writing artifact " // id (file path&name) --  added file path&name to log message
							+ currentInstance.getArtifactId()+ " (" + currentInstance.getFilePath() + currentInstance.getFileRef() + ")" + 
							" to provider "
							+ provider.getId() + " at " + provider.getPlace().getSiteName() +
							" (" + provider.getPlace().getSiteNumber() + ") "; // -- added site(site#) to log message
							logger.info(storageDeviceinfo);
					results = provider.writeArtifactStream(null,
							currentInstance.getProvider().getArtifactStream(currentInstance),
							currentInstance.getArtifact(),
							getLocalSiteId());
					newInstances.add(results.getArtifactInstance());
				} catch (FileNotFoundException fnfe) {
					throw new MethodException(storageDeviceinfo + "failed. "+ fnfe);
				} catch (MethodException me) {
					throw new MethodException(storageDeviceinfo + "failed. "+ me);
				} catch (ConnectionException ce) {
			throw new ConnectionException(storageDeviceinfo + "failed. "+ ce);
		}
			}
		}
		return newInstances;
	}
}
