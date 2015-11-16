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
package gov.va.med.imaging.vistaimagingdatasource.storage;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.AbstractVersionableDataSource;
import gov.va.med.imaging.datasource.StorageDataSourceSpi;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.TimePeriod;
import gov.va.med.imaging.exchange.business.storage.*;
import gov.va.med.imaging.exchange.enums.ImagingSecurityContextType;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;
import java.io.IOException;
import java.util.List;

/**
 * An implementation of a DicomStorageDataSourceSpi that talks to VistA.
 * 
 * NOTE: 1.) public methods that do Vista access (particularly anything defined
 * in the DicomDataSourceSpi interface) must acquire a VistaSession instance
 * using getVistaSession(). 2.) private methods which are only called from
 * public methods that do Vista access must include a VistaSession parameter,
 * they should not acquire their own VistaSession 3.) Where a method is both
 * public and called from within this class, there should be a public version
 * following rule 1, calling a private version following rule 2.
 * 
 * @author vhaiswlouthj
 * 
 */
public class VistaImagingStorageDataSourceService
extends AbstractVersionableDataSource 
implements VistaSessionFactory, StorageDataSourceSpi 
{
	// The required version of VistA Imaging needed to execute the RPC calls for
	// this operation

	// TODO CHANGE TO 3.0P34
	public final static String MAG_REQUIRED_VERSION = "3.0P83";
	public final static int DEFAULT_PATIENT_SENSITIVITY_LEVEL = 2;
	public final static String SUPPORTED_PROTOCOL = "vistaimaging";

	/*
	 * =====================================================================
	 * Instance fields and methods
	 * =====================================================================
	 */
	private StorageServerDatabaseConfiguration dbConfig = null;

	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingStorageDataSourceService(
			ResolvedArtifactSource resolvedArtifactSource, String protocol) {
		super(resolvedArtifactSource, protocol);
	}

	/**
	 * The artifact source must be checked in the constructor to assure that it
	 * is an instance of ResolvedSite.
	 * 
	 * @return
	 */
	protected ResolvedSite getResolvedSite() {
		return (ResolvedSite) getResolvedArtifactSource();
	}

	protected Site getSite() {
		return getResolvedSite().getSite();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.va.med.imaging.vistaimagingdatasource.dicom.storage.SessionFactory
	 * #getVistaSession()
	 */
	public VistaSession getVistaSession() throws IOException, ConnectionException, MethodException 
	{
		TransactionContextFactory.get().setImagingSecurityContextType(ImagingSecurityContextType.DICOM_QR_CONTEXT.name());
		return VistaSession.getOrCreate(getMetadataUrl(), getSite());
	}

	//
	// ArtifactDescriptor
	//
	@Override
	public List<ArtifactDescriptor> findAllArtifactDescriptors()
	throws MethodException, ConnectionException 
	{
		ArtifactDescriptorDAO dao = new ArtifactDescriptorDAO(this);
		return dao.findAll();
	}

	@Override
	public ArtifactDescriptor setIntrinsicRetentionPolicy(ArtifactDescriptor artifactDescriptor, RetentionPolicy retentionPolicy) 
	throws MethodException, ConnectionException 
	{
		ArtifactDescriptorDAO dao = new ArtifactDescriptorDAO(this);
		return dao.updateRetentionPolicy(artifactDescriptor, retentionPolicy);
	}


	//
	// RetentionPolicy
	//
	@Override
	public List<RetentionPolicy> findAllRetentionPolicies()
	throws MethodException, ConnectionException 
	{
		RetentionPolicyDAO dao = new RetentionPolicyDAO(this);
		return dao.findAll();
	}


	//
	// RetentionPolicyProviderMap
	//
	@Override
	public RetentionPolicyProviderMapping createRetentionPolicyProviderMapping(RetentionPolicyProviderMapping retentionPolicyProviderMapping) 
	throws MethodException, ConnectionException 
	{
		RetentionPolicyProviderMapDAO dao = new RetentionPolicyProviderMapDAO(this);
		return dao.create(retentionPolicyProviderMapping);
	}

	@Override
	public List<RetentionPolicyProviderMapping> findAllRetentionPolicyProviderMappings()
	throws MethodException, ConnectionException 
	{
		RetentionPolicyProviderMapDAO dao = new RetentionPolicyProviderMapDAO(this);
		return dao.findAll();
	}

	@Override
	public RetentionPolicyProviderMapping updateRetentionPolicyProviderMapping(RetentionPolicyProviderMapping retentionPolicyProviderMap) 
	throws MethodException, ConnectionException 
	{
		RetentionPolicyProviderMapDAO dao = new RetentionPolicyProviderMapDAO(this);
		return dao.update(retentionPolicyProviderMap);
	}

	@Override
	public void deleteRetentionPolicyProviderMapping(int retentionPolicyProviderMapID) 
	throws MethodException, ConnectionException 
	{
		RetentionPolicyProviderMapDAO dao = new RetentionPolicyProviderMapDAO(this);
		dao.delete(retentionPolicyProviderMapID);
	}

	//
	// Provider
	//
	@Override
	public Provider createProvider(Provider provider)
	throws MethodException, ConnectionException 
	{
		ProviderDAO dao = new ProviderDAO(this);
		return dao.create(provider);
	}

	@Override
	public List<Provider> findAllProviders() 
	throws MethodException, ConnectionException 
	{
		ProviderDAO dao = new ProviderDAO(this);
		return dao.findAll();
	}
	
	
	@Override
	public NetworkLocationInfo getCurrentWriteLocation(Provider provider) 
	throws MethodException, ConnectionException 
	{
		NetworkLocationInfoDAO dao = new NetworkLocationInfoDAO(this);
		return dao.getCurrentWriteLocation(provider);
	}

	@Override
	public NetworkLocationInfo getCurrentJukeboxWriteLocation(Provider provider) 
	throws MethodException, ConnectionException 
	{
		NetworkLocationInfoDAO dao = new NetworkLocationInfoDAO(this);
		return dao.getCurrentJukeboxWriteLocation(provider);
	}
	
	@Override
	public NetworkLocationInfo getNetworkLocationDetails(String networkLocationIEN) 
	throws MethodException, ConnectionException 
	{
		NetworkLocationInfoDAO dao = new NetworkLocationInfoDAO(this);
		return dao.getNetworkLocationDetails(networkLocationIEN);
	}

	@Override
	public Provider updateProvider(Provider provider) 
	throws MethodException, ConnectionException 
	{
		ProviderDAO dao = new ProviderDAO(this);
		return dao.update(provider);
	}

	//
	// ProviderAvailability
	//
	@Override
	public ProviderAvailability createProviderAvailability(ProviderAvailability providerAvailability) 
	throws MethodException, ConnectionException 
	{
		ProviderAvailabilityDAO dao = new ProviderAvailabilityDAO(this);
		return dao.create(providerAvailability);
	}

	@Override
	public List<ProviderAvailability> findAllProviderAvailability()
	throws MethodException, ConnectionException 
	{
		ProviderAvailabilityDAO dao = new ProviderAvailabilityDAO(this);
		return dao.findAll();
	}

	@Override
	public ProviderAvailability updateProviderAvailability(ProviderAvailability providerAvailability)
	throws MethodException, ConnectionException 
	{
		ProviderAvailabilityDAO dao = new ProviderAvailabilityDAO(this);
		return dao.update(providerAvailability);
	}

	@Override
	public void deleteProviderAvailability(int providerAvailabilityID)
	throws MethodException, ConnectionException 
	{
		ProviderAvailabilityDAO dao = new ProviderAvailabilityDAO(this);
		dao.delete(providerAvailabilityID);
	}
	
	//
	// Artifact
	//
	@Override
	public Artifact createArtifact(Artifact artifact)
	throws MethodException, ConnectionException 
	{
		BaseArtifactDAO dao = new ArtifactDAO(this);
		return dao.create(artifact);
	}

	@Override
	public Artifact getArtifactByToken(String token) 
	throws MethodException, ConnectionException 
	{
		Artifact artifact = new Artifact();
		artifact.setArtifactToken(token);
		
		BaseArtifactDAO dao = new ArtifactDAO(this);
		return dao.getEntityByExample(artifact);
	}

	@Override
	public Artifact getArtifactAndInstancesByToken(String token)
	throws MethodException, ConnectionException 
	{
		Artifact artifact = new Artifact();
		artifact.setArtifactToken(token);
		
		ArtifactAndInstanceDAO dao = new ArtifactAndInstanceDAO(this);
		return dao.getEntityByExample(artifact);
	}

	@Override
	public Artifact getArtifactAndInstancesByKeyList(KeyList keyList)
	throws MethodException, ConnectionException 
	{
		Artifact artifact = new Artifact();
		artifact.setKeyList(keyList.getKeyList());
		
		ArtifactAndInstanceDAO dao = new ArtifactAndInstanceDAO(this);
		return dao.getEntityByExample(artifact);
	}

	@Override
	public Artifact getArtifactAndInstancesById(int artifactID)
	throws MethodException, ConnectionException 
	{
		ArtifactAndInstanceDAO dao = new ArtifactAndInstanceDAO(this);
		return dao.getEntityById(String.valueOf(artifactID));
	}

	@Override
	public Artifact getFullArtifactGraphByToken(String token)
	throws MethodException, ConnectionException 
	{
		Artifact artifact = new Artifact();
		artifact.setArtifactToken(token);
		
		ArtifactGraphDAO dao = new ArtifactGraphDAO (this);
		return dao.getEntityByExample(artifact);
	}

	@Override
	public Artifact getFullArtifactGraphById(String token)
	throws MethodException, ConnectionException 
	{
		Artifact artifact = new Artifact();
		artifact.setArtifactToken(token);
		
		ArtifactGraphDAO dao = new ArtifactGraphDAO (this);
		return dao.getEntityByExample(artifact);
	}

	//
	// ArtifactInstance
	//
	@Override
	public ArtifactInstance createArtifactInstance(ArtifactInstance artifactInstance) 
	throws MethodException, ConnectionException 
	{
		ArtifactInstanceDAO dao = new ArtifactInstanceDAO(this);
		return dao.create(artifactInstance);
	}

	@Override
	public Artifact updateArtifact(Artifact artifact)
	throws MethodException, ConnectionException 
	{
		ArtifactDAO dao = new ArtifactDAO(this);
		return dao.update(artifact);
	}


	@Override
	public ArtifactInstance updateArtifactInstanceLastAccessed(ArtifactInstance artifactInstance)
	throws MethodException, ConnectionException 
	{
		ArtifactInstanceDAO dao = new ArtifactInstanceDAO(this);
		return dao.updateLastAccessDateTime(artifactInstance);
	}

	@Override
	public ArtifactInstance updateArtifactInstanceUrl(ArtifactInstance artifactInstance)
	throws MethodException, ConnectionException 
	{
		ArtifactInstanceDAO dao = new ArtifactInstanceDAO(this);
		return dao.updateUrl(artifactInstance);
	}


	//
	// StorageTransaction
	//
	@Override
	public StorageTransaction createStorageTransaction(StorageTransaction storageTransaction) 
	throws MethodException, ConnectionException 
	{
		StorageTransactionDAO dao = new StorageTransactionDAO(this);
		return dao.create(storageTransaction);
	}

	@Override
	public int getStorageTransactionCountWithinTimePeriod(String type, String status, TimePeriod period) 
	throws MethodException, ConnectionException 
	{
		StorageTransactionDAO dao = new StorageTransactionDAO(this);
		return dao.getCountWithinTimePeriod(type, status, period);
	}
	
	
	//
	// ArtifactRetentionPolicy
	//
	@Override
	public ArtifactRetentionPolicy createArtifactRetentionPolicy(ArtifactRetentionPolicy artifactRetentionPolicy)
	throws MethodException, ConnectionException 
	{
		ArtifactRetentionPolicyDAO dao = new ArtifactRetentionPolicyDAO(this);
		return dao.create(artifactRetentionPolicy);
	}

	@Override
	public ArtifactRetentionPolicy updateArtifactRetentionPolicy(ArtifactRetentionPolicy artifactRetentionPolicy) 
	throws MethodException, ConnectionException 
	{
		ArtifactRetentionPolicyDAO dao = new ArtifactRetentionPolicyDAO(this);
		return dao.update(artifactRetentionPolicy);
	}


	//
	// RetentionPolicyFulfillment
	//
	@Override
	public RetentionPolicyFulfillment createRetentionPolicyFulfillment(RetentionPolicyFulfillment retentionPolicyFulfillment) 
	throws MethodException, ConnectionException 
	{
		RetentionPolicyFulfillmentDAO dao = new RetentionPolicyFulfillmentDAO(this);
		return dao.create(retentionPolicyFulfillment);
	}


	//
	// TransferStatistics
	//
	@Override
	public TransferStatistics createTransferStatistics(TransferStatistics transferStatistics)
	throws MethodException, ConnectionException 
	{
		TransferStatisticsDAO dao = new TransferStatisticsDAO(this);
		return dao.create(transferStatistics);
	}

	//
	// Configuration
	//
	@Override
	public StorageServerDatabaseConfiguration getStorageServerDatabaseConfiguration(ProviderFactory providerFactory) 
	throws MethodException, ConnectionException
	{
		// If the config is not yet created or initialized, or if it has errors, try to initialize it.
		if (dbConfig == null || !dbConfig.isInitialized() || !dbConfig.isValid())
		{
			// Create the DAOs
			ArtifactDescriptorDAO artifactDescriptorDAO = new ArtifactDescriptorDAO(this);
			RetentionPolicyDAO retentionPolicyDAO = new RetentionPolicyDAO(this);
			RetentionPolicyProviderMapDAO retentionPolicyProviderMapDAO = new RetentionPolicyProviderMapDAO(this);
			ProviderDAO providerDAO = new ProviderDAO(this);
			ProviderAvailabilityDAO providerAvailabilityDAO = new ProviderAvailabilityDAO(this);
			PlaceDAO placeDAO = new PlaceDAO(this);
			
			// Retrieve the data from the DB
			List<ArtifactDescriptor> artifactDescriptors = artifactDescriptorDAO.findAll();
			List<RetentionPolicy> retentionPolicies = retentionPolicyDAO.findAll();
			List<RetentionPolicyProviderMapping> retentionPolicyProviderMappings = retentionPolicyProviderMapDAO.findAll();
			List<Provider> providers = providerDAO.findAll();
			List<ProviderAvailability> providerAvailabilityTimes = providerAvailabilityDAO.findAll();
			List<Place> places = placeDAO.findAll();
	
			// Create and initialize the storage configuration
			dbConfig = new StorageServerDatabaseConfiguration();
			dbConfig.initializeConfiguration(providerFactory, artifactDescriptors, retentionPolicies, retentionPolicyProviderMappings, providers, providerAvailabilityTimes, places);
		}
		
		// Return the storage configuration
		return dbConfig;
		
//		// Return a clone of the storage configuration
//		StorageServerConfiguration clonedConfig = null;
//		try 
//		{
//			clonedConfig = (StorageServerConfiguration)(ObjectCloner.deepCopy(config));
//		} 
//		catch (Exception e) 
//		{
//			logger.error(e);
//		}
//		return clonedConfig;
	}


	
	@Override
	public boolean isVersionCompatible() {
		// TODO - Remove comments when able...
		return true;
		// VistaSession localVistaSession = null;
		// logger.info("isVersionCompatible searching for version [" +
		// MAG_REQUIRED_VERSION + "], TransactionContext (" +
		// TransactionContextFactory.get().getDisplayIdentity() + ").");
		// try
		// {
		// localVistaSession = getVistaSession();
		// VistaQuery magVersionsQuery =
		// VistaQueryFactory.createGetMagInstalledVersionsQuery();
		// String magVersions =
		// localVistaSession.call(magVersionsQuery);
		//
		// List<String> magVersionList =
		// VistaTranslator.convertVistaVersionsToVersionNumbers(magVersions);
		// for (int i = 0; i < magVersionList.size(); i++)
		// {
		// if (MAG_REQUIRED_VERSION.equalsIgnoreCase(magVersionList.get(i)))
		// {
		// logger.info("Vista database has required imaging version [" +
		// MAG_REQUIRED_VERSION + "] installed, ok to continue");
		// return true;
		// }
		// }
		// }
		// catch (MethodException mX)
		// {
		// logger.error("There was an error finding the installed Imaging
		// version from VistA", mX);
		// }
		// catch (ConnectionException cX)
		// {
		// logger.error("There was an error finding the installed Imaging
		// version from VistA", cX);
		// }
		// catch (VistaMethodException vmX)
		// {
		// logger.error("There was an error finding the installed Imaging
		// version from VistA", vmX);
		// }
		// catch (InvalidVistaCredentialsException icX)
		// {
		// logger.error("There was an error finding the installed Imaging
		// version from VistA", icX);
		// }
		// catch (IOException ioX)
		// {
		// logger.error("There was an error finding the installed Imaging
		// version from VistA", ioX);
		// }
		// finally
		// {
		// try
		// {
		// localVistaSession.close();
		// }
		// catch (Throwable t)
		// {
		// }
		// }
		//
		// logger.info("Vista database does NOT have required imaging version ["
		// + MAG_REQUIRED_VERSION + "], connection will not continue");
		// return false;
	}


}
