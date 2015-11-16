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

import gov.va.med.imaging.SizedInputStream;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.PersistentEntity;
import gov.va.med.imaging.exchange.business.exceptions.BusinessMethodNotImplementedException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;

public class Provider implements PersistentEntity, Serializable
{
	private static final long serialVersionUID = 1L;

	//
	// Fields
	//
	private int id;
	private String providerType;
	private boolean isActive;
	private boolean isArchive;
	private boolean isPrimaryStorage;
	private boolean isWritable;
	private int placeId;
	private Place place;
	private Map<Integer, ProviderAvailability> providerAvailabilitiesByPlaceId;

	//
	// Default constructor
	//
	public Provider()
	{
		providerAvailabilitiesByPlaceId = new HashMap<Integer, ProviderAvailability>();
	}
	
	//
	// Additional constructors
	//
	public Provider(int id, int placeId, String providerType, boolean isActive,
			boolean isArchive, boolean isPrimaryStorage, boolean isWritable) 
	{
		this();
		this.id = id;
		this.placeId = placeId;
		this.providerType = providerType;
		this.isActive = isActive;
		this.isArchive = isArchive;
		this.isPrimaryStorage = isPrimaryStorage;
		this.isWritable = isWritable;
	}

	// Business methods
	public ArtifactWriteResults writeArtifactStream(
			ReadableByteChannel artifactChannel, 
			InputStream artifactStream, 
			Artifact artifact,
			String originatingSiteId) 
	throws MethodException, ConnectionException		{ 
		throw new BusinessMethodNotImplementedException("writeArtifactStream"); 
	}
	
	public InputStream getArtifactStream(ArtifactInstance artifactInstance) throws MethodException, ConnectionException, FileNotFoundException
	{ 
		throw new BusinessMethodNotImplementedException("getArtifactStream"); 
	}
	
	public boolean canRetrieveFromArtifactInstance(ArtifactInstance instance) throws MethodException, ConnectionException
	{ 
		return (instance.getProviderId() == this.getId());
	}
	
	
	//
	// Properties
	//
    public int getId() {
		return id;
	}

    public void setId(int id) {
		this.id = id;
	}

	public String getProviderType() {
		return providerType;
	}

	public void setProviderType(String providerType) {
		this.providerType = providerType;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isArchive() {
		return isArchive;
	}

	public void setArchive(boolean isArchive) {
		this.isArchive = isArchive;
	}

	public boolean isPrimaryStorage() {
		return isPrimaryStorage;
	}

	public void setPrimaryStorage(boolean isPrimaryStorage) {
		this.isPrimaryStorage = isPrimaryStorage;
	}

	public boolean isWritable() {
		return isWritable;
	}

	public void setWritable(boolean isWritable) {
		this.isWritable = isWritable;
	}

	public int getPlaceId() {
		return placeId;
	}

	public void setPlaceId(int placeId) {
		this.placeId = placeId;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
		this.placeId = place.getId();
	}
	
	public void addProviderAvailability(ProviderAvailability availability){
		providerAvailabilitiesByPlaceId.put(availability.getPlaceId(), availability);
	}

	public ProviderAvailability getProviderAvailabilityByPlaceId(int placeId){
		return providerAvailabilitiesByPlaceId.get(placeId);
	}
}
