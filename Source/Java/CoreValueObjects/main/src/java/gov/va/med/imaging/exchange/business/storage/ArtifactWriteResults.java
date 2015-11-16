package gov.va.med.imaging.exchange.business.storage;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;

public class ArtifactWriteResults 
{
	//
	// Fields
	//
	private long sizeInBytes;
	private String CRC;
	private ArtifactInstance artifactInstance;
	
	//
	// Constructor
	//
	public ArtifactWriteResults(long sizeInBytes, String cRC, ArtifactInstance artifactInstance) 
	{
		this.sizeInBytes = sizeInBytes;
		CRC = cRC;
		this.artifactInstance = artifactInstance;
	}
	
	//
	// Properties
	//
	
	public String getCRC() {
		return CRC;
	}
	public long getSizeInBytes() {
		return sizeInBytes;
	}

	public void setSizeInBytes(long sizeInBytes) {
		this.sizeInBytes = sizeInBytes;
	}

	public void setCRC(String cRC) {
		CRC = cRC;
	}
	
	public ArtifactInstance getArtifactInstance() {
		return artifactInstance;
	}

	public void setArtifactInstance(ArtifactInstance artifactInstance) {
		this.artifactInstance = artifactInstance;
	}

}
