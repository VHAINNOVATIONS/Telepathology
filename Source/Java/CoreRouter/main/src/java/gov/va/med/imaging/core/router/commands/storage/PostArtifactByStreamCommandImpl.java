/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr, 2010
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
package gov.va.med.imaging.core.router.commands.storage;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.GUID;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.facade.InternalContext;
import gov.va.med.imaging.core.router.storage.StorageContext;
import gov.va.med.imaging.core.router.storage.StorageDataSourceRouter;
import gov.va.med.imaging.exchange.business.DurableQueue;
import gov.va.med.imaging.exchange.business.DurableQueueMessage;
import gov.va.med.imaging.exchange.business.storage.Artifact;
import gov.va.med.imaging.exchange.business.storage.ArtifactDescriptor;
import gov.va.med.imaging.exchange.business.storage.ArtifactInstance;
import gov.va.med.imaging.exchange.business.storage.ArtifactRetentionPolicy;
import gov.va.med.imaging.exchange.business.storage.ArtifactWriteResults;
import gov.va.med.imaging.exchange.business.storage.AsyncStorageRequest;
import gov.va.med.imaging.exchange.business.storage.KeyList;
import gov.va.med.imaging.exchange.business.storage.Place;
import gov.va.med.imaging.exchange.business.storage.RetentionPolicy;
import gov.va.med.imaging.exchange.business.storage.RetentionPolicyFulfillment;
import gov.va.med.imaging.exchange.business.storage.RetentionPolicyProviderMapping;
import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;
import gov.va.med.imaging.exchange.business.storage.StorageServerDatabaseConfiguration;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This command is the driver for storing a DICOM instance. It performs validation
 * of the patient and imaging service request, check UIDs and coerces them if necessary,
 * determines whether the instance is an "old" or "new" SOP class, and stores the
 * instance appropriately.
 * 
 * @author vhaiswlouthj
 * 
 */
public class PostArtifactByStreamCommandImpl extends AbstractStorageCommandImpl<String>
{
	private static final long serialVersionUID = -4963797794965394068L;
	private static Logger logger = Logger.getLogger(PostArtifactByStreamCommandImpl.class);
    private static final StorageDataSourceRouter dataSourceRouter = StorageContext.getDataSourceRouter();
    private static final StorageServerConfiguration config = StorageServerConfiguration.getConfiguration();

    private final InputStream artifactStream;
    private final ReadableByteChannel artifactChannel;
    private final ArtifactDescriptor artifactDescriptor;
    private final String place;
    private final String createdBy;
    private final KeyList keyList;

	/**
	 * @param router
	 * @param asynchronousMethodProcessor
	 */
	public PostArtifactByStreamCommandImpl(InputStream artifactStream, ReadableByteChannel artifactChannel, ArtifactDescriptor artifactDescriptor, String place, KeyList keyList, String createdBy) throws MethodException, ConnectionException
	{
		super();
		this.artifactStream = artifactStream;
		this.artifactChannel = artifactChannel;
		this.artifactDescriptor = artifactDescriptor;
		this.place = place;
		this.keyList = keyList;
		this.createdBy= createdBy; 
	}
	
	@Override
	public String callSynchronouslyInTransactionContext() throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getLocalSiteId());
		
		// Create the token
		String artifactToken = "Z"+(new GUID().toShortString());

		// Look up RetentionPolicy
		RetentionPolicy retentionPolicy = artifactDescriptor.getRetentionPolicy();

		// Get providers to write to at this location
		int acquiringPlaceId = StorageServerDatabaseConfiguration.getConfiguration().getPlace(getLocalSiteId()).getId();
		List<RetentionPolicyProviderMapping> mappings = retentionPolicy.getRetentionPolicyProviderMappingsForAcquiringPlace(acquiringPlaceId);
		
		// Create artifact record
		Artifact artifact = createArtifact(artifactToken);
		
		// Create ArtifactRetentionPolicy record
		ArtifactRetentionPolicy artifactRetentionPolicy = createArtifactRetentionPolicy(artifact, retentionPolicy);

		ArtifactWriteResults writeResults = null;
		int writeCount = 0;
		// Loop over the provider mappings and write as necessary
		for (RetentionPolicyProviderMapping mapping : mappings)
		{
			if (mapping.isSynchronous())
			{
				// Allow provider to store the stream and create the artifact instance record
				writeResults = mapping.getProvider().writeArtifactStream(artifactChannel, artifactStream, artifact, getLocalSiteId());
				
				// Create RetentionPolicyFulfillment records
				createRetentionPolicyFulfillment(artifactRetentionPolicy, writeResults.getArtifactInstance());
				
				writeCount++;
			}
		}
		
		// Update the artifact record with the file size and crc
		artifact = updateArtifactRecord(artifact, writeResults);

		// If we wrote to all providers, mark as done.
		// Otherwise, queue up an asynchronous archiving request
		if (writeCount == mappings.size())
		{
			// We wrote to all configured providers. Mark the artifact retention policy as satisfied
//			artifactRetentionPolicy.setIsSatisfied(true); -- altered 09/05/11
			artifactRetentionPolicy.setSatisfiedDateTime((new Date()).toString());
			dataSourceRouter.putArtifactRetentionPolicy(artifactRetentionPolicy);
		}
		else
		{
			// Queue up an asynchronous archiving process
			RoutingToken routingToken = getCommandContext().getLocalSite().getArtifactSource().createRoutingToken();
			DurableQueue queue = InternalContext.getRouter().getDurableQueueByName(routingToken, AsyncQueueNames.asyncStorageRequestQueue);
			Place place = StorageServerDatabaseConfiguration.getConfiguration().getPlace(getLocalSiteId());
			AsyncStorageRequest request = new AsyncStorageRequest(artifact.getArtifactToken());
			DurableQueueMessage message = new DurableQueueMessage(queue, Integer.toString(place.getId()), request.serializeUsingXStream());
			InternalContext.getRouter().enqueueDurableQueueMessage(routingToken, message);
		}
		
		return artifactToken;
	}

	protected Artifact createArtifact(String artifactToken) throws MethodException, ConnectionException 
	{
		Artifact artifact = new Artifact();
		artifact.setArtifactDescriptor(artifactDescriptor);
		artifact.setArtifactToken(artifactToken);
		artifact.setKeyList(keyList.getKeyList());
		artifact.setCreatedBy(createdBy);
		artifact = dataSourceRouter.postArtifact(artifact);
		return artifact;
	}

	protected ArtifactRetentionPolicy createArtifactRetentionPolicy( Artifact artifact, RetentionPolicy retentionPolicy) throws MethodException, ConnectionException 
	{
		ArtifactRetentionPolicy artifactRetentionPolicy = new ArtifactRetentionPolicy();
		artifactRetentionPolicy.setArtifact(artifact);
		artifactRetentionPolicy.setRetentionPolicy(retentionPolicy);
//		artifactRetentionPolicy.setIsSatisfied(false);
		artifactRetentionPolicy.setSatisfiedDateTime("");
		artifactRetentionPolicy = dataSourceRouter.postArtifactRetentionPolicy(artifactRetentionPolicy);
		return artifactRetentionPolicy;
	}

	protected void createRetentionPolicyFulfillment(ArtifactRetentionPolicy artifactRetentionPolicy,ArtifactInstance artifactInstance)  throws MethodException, ConnectionException 
	{
		RetentionPolicyFulfillment fulfillment = new RetentionPolicyFulfillment();
		fulfillment.setArtifactInstance(artifactInstance);
		fulfillment.setArtifactRetentionPolicy(artifactRetentionPolicy);
		dataSourceRouter.postRetentionPolicyFulfillment(fulfillment);
	}

	protected Artifact updateArtifactRecord(Artifact artifact, ArtifactWriteResults writeResults)  throws MethodException, ConnectionException 
	{
		artifact.setSizeInBytes(writeResults.getSizeInBytes());
		artifact.setCRC(writeResults.getCRC());
		artifact = dataSourceRouter.putArtifact(artifact);
		return artifact;
	}



	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj) {
		// Perform cast for subsequent tests
		final PostArtifactByStreamCommandImpl other = (PostArtifactByStreamCommandImpl) obj;

		// Check the studyUrn
		boolean areFieldsEqual = areFieldsEqual(this.artifactChannel, other.artifactChannel);
		areFieldsEqual = areFieldsEqual && areFieldsEqual(this.artifactDescriptor, other.artifactDescriptor);
		areFieldsEqual = areFieldsEqual && areFieldsEqual(this.place, other.place);
		areFieldsEqual = areFieldsEqual && areFieldsEqual(this.keyList, other.keyList);

		return areFieldsEqual;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();

		sb.append(this.artifactChannel.toString());
		sb.append(this.artifactDescriptor.toString());
		sb.append(this.place.toString());
		sb.append(this.keyList.toString());

		return sb.toString();
	}

}
