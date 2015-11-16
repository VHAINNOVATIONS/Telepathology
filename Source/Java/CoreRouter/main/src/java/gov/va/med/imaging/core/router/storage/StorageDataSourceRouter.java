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
package gov.va.med.imaging.core.router.storage;

import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterface;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterfaceCommandTester;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterMethod;
import gov.va.med.imaging.core.interfaces.FacadeRouter;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
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

@FacadeRouterInterface
@FacadeRouterInterfaceCommandTester
public interface StorageDataSourceRouter 
extends FacadeRouter
{
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	Artifact postArtifact(Artifact artifact) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	ArtifactInstance postArtifactInstance(ArtifactInstance artifactInstance) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	ArtifactRetentionPolicy postArtifactRetentionPolicy(ArtifactRetentionPolicy artifactRetentionPolicy) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)	
	ProviderAvailability postProviderAvailability(ProviderAvailability providerAvailability) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	Provider postProvider(Provider provider) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	RetentionPolicyFulfillment postRetentionPolicyFulfillment(RetentionPolicyFulfillment retentionPolicyFulfillment) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	RetentionPolicyProviderMapping postRetentionPolicyProviderMapping(RetentionPolicyProviderMapping mapping) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	StorageTransaction postStorageTransaction(StorageTransaction storageTransaction) throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	Integer getStorageTransactionsWithinTimePeriod(String type, String status, TimePeriod period) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	TransferStatistics postTransferStatistics(TransferStatistics transferStatistics) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	Boolean deleteProviderAvailability(Integer providerAvailabilityId) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	Boolean deleteRetentionPolicyProviderMapping(Integer mappingId) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	Artifact getArtifactAndInstancesById(Integer artifactId) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	Artifact getArtifactAndInstancesByKeyList(KeyList keyList) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	Artifact getArtifactAndInstancesByToken(String artifactToken) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	Artifact getArtifactAsGraphById(String artifactId) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	Artifact getArtifactAsGraphByToken(String artifactToken) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	Artifact getArtifactByToken(String artifactToken) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	NetworkLocationInfo getCurrentWriteLocation(Provider provider) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	NetworkLocationInfo getCurrentJukeboxWriteLocation(Provider provider) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	NetworkLocationInfo getNetworkLocationDetails(String networkLocationIEN) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	StorageServerDatabaseConfiguration getStorageServerDatabaseConfiguration(ProviderFactory providerFactory) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	Artifact putArtifact(Artifact artifact) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	ArtifactInstance putArtifactInstanceLastAccessed(ArtifactInstance artifactInstance) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	ArtifactInstance putArtifactInstanceUrl(ArtifactInstance artifactInstance) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	ArtifactRetentionPolicy putArtifactRetentionPolicy(ArtifactRetentionPolicy artifactRetentionPolicy) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	ProviderAvailability putProviderAvailability(ProviderAvailability providerAvailability) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	Provider putProvider(Provider provider) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	RetentionPolicy putRetentionPolicy(ArtifactDescriptor artifactDescriptor, RetentionPolicy retentionPolicy) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true)
	RetentionPolicyProviderMapping putRetentionPolicyProviderMapping(RetentionPolicyProviderMapping mapping) throws MethodException, ConnectionException;

}
