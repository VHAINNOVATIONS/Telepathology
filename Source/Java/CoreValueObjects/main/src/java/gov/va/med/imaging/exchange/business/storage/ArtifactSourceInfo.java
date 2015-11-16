/**
 * 
 */
package gov.va.med.imaging.exchange.business.storage;

import gov.va.med.imaging.core.interfaces.StorageCredentials;

import java.io.Serializable;

/**
 * @author vhaiswpeterb
 *
 */
public class ArtifactSourceInfo implements Serializable, StorageCredentials {

	private static final long serialVersionUID = 1L;
	
	private String type = null;
	private String identifier = null;
	private String username = null;
	private String password = null;
	
	/**
	 * Constructor
	 */
	public ArtifactSourceInfo(String type, String identifier, String username, String password){
		this.type = type;
		this.identifier = identifier;
		this.username = username;
		this.password = password;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}
	
}
