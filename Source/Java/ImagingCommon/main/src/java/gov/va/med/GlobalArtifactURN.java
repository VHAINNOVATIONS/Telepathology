/**
 * 
 */
package gov.va.med;

import java.util.regex.Pattern;


/**
 * @author vhaiswbeckec
 *
 */
public interface GlobalArtifactURN
{
	public static final String NSS_DELIMITER = ":";
	public static final String COMMUNITY_ID_REGEX = "[a-zA-Z0-9_\\-\\.]+";		// may not contain colon character
	public static final String REPOSITORY_ID_REGEX = "[a-zA-Z0-9_\\-\\.]+";		// may not contain colon character
	public static final String DOCUMENT_ID_REGEX = "[a-zA-Z0-9:_\\-\\.]+";		// may contain colon character
	public static final String NSS_REGEX = 
		"("+COMMUNITY_ID_REGEX+")" + NSS_DELIMITER + 
		"("+REPOSITORY_ID_REGEX+")" + NSS_DELIMITER +
		"("+DOCUMENT_ID_REGEX+")";
	public static final int COMMUNITY_ID_GROUP = 1;
	public static final int REPOSITORY_ID_GROUP = 2;
	public static final int DOCUMENT_ID_GROUP = 3;

	public static final Pattern COMMUNITY_ID_PATTERN = Pattern.compile(COMMUNITY_ID_REGEX);
	public static final Pattern REPOSITORY_ID_PATTERN = Pattern.compile(REPOSITORY_ID_REGEX);
	public static final Pattern DOCUMENT_ID_PATTERN = Pattern.compile(DOCUMENT_ID_REGEX);
	public static final Pattern NSS_PATTERN = Pattern.compile(NSS_REGEX);
	
	/**
	 * 
	 * @return
	 */
	public abstract String getHomeCommunityId();

	/**
	 * 
	 * @return
	 */
	public abstract String getRepositoryUniqueId();
	
	/**
	 * 
	 * @return
	 */
	public abstract String getDocumentUniqueId();
	
}
