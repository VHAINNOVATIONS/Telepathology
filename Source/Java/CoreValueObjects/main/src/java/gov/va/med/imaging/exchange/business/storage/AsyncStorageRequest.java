package gov.va.med.imaging.exchange.business.storage;

import gov.va.med.imaging.BeanUtils;
import gov.va.med.imaging.BusinessKey;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

public class AsyncStorageRequest {
	private static final long serialVersionUID = 3238474128342L;
	private String artifactToken;
	private int numAttempts;
	private String lastError;

	public AsyncStorageRequest(String artifactToken) {
		super();
		this.artifactToken = artifactToken;
	}

	@Override
	public int hashCode() {
		return BeanUtils.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return BeanUtils.equals(this, obj);
	}

	@BusinessKey
	public String getArtifactToken() {
		return artifactToken;
	}

	public void setArtifactToken(String artifactToken) {
		this.artifactToken = artifactToken;
	}

	public int getNumAttempts() {
		return numAttempts;
	}

	public void setNumAttempts(int numAttempts) {
		this.numAttempts = numAttempts;
	}
	
	public static AsyncStorageRequest deserializeUsingXStream(String serializedXml)throws XStreamException{
		XStream xstream = new XStream();
		return (AsyncStorageRequest)xstream.fromXML(serializedXml);
	}
	
	public String serializeUsingXStream(){
		XStream xstream = new XStream();
		return xstream.toXML(this);
	}

	public String getLastError() {
		return lastError;
	}

	public void setLastError(String lastError) {
		this.lastError = lastError;
	}
}
