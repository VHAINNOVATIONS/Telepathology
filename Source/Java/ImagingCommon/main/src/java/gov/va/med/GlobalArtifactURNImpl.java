/**
 * 
 */
package gov.va.med;

import java.util.regex.Matcher;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.utility.Base32ConversionUtility;

/**
 * @author vhaiswbeckec
 *
 */
public class GlobalArtifactURNImpl
extends URN
implements GlobalArtifactURN, GlobalArtifactIdentifier
{
	private static final long serialVersionUID = 1L;
	
	private static final String namespace = "ihe";
	private static NamespaceIdentifier namespaceIdentifier = null;

	public static synchronized NamespaceIdentifier getManagedNamespace()
	{
		if(namespaceIdentifier == null)
			namespaceIdentifier = new NamespaceIdentifier(namespace);
		return namespaceIdentifier;
	}
	
	private String homeCommunityId;
	private String repositoryUniqueId;
	private String documentUniqueId;
	
	/**
	 * 
	 * @throws URNFormatException
	 */
	public GlobalArtifactURNImpl() 
	throws URNFormatException
	{
		super(getManagedNamespace());
	}
	/**
	 * @param namespaceIdentifier
	 * @throws URNFormatException
	 */
	public GlobalArtifactURNImpl(NamespaceIdentifier namespaceIdentifier) 
	throws URNFormatException
	{
		super(namespaceIdentifier);
		if(! GlobalArtifactURNImpl.namespaceIdentifier.equals(namespaceIdentifier))
			throw new URNFormatException("Namespace identifier '" + namespaceIdentifier + "' is not understood by " + this.getClass().getSimpleName());
	}

	/**
	 * @param urnComponents
	 * @throws URNFormatException
	 */
	public GlobalArtifactURNImpl(URNComponents urnComponents) 
	throws URNFormatException
	{
		super(urnComponents, SERIALIZATION_FORMAT.NATIVE);
		if(! GlobalArtifactURNImpl.namespaceIdentifier.equals(urnComponents.getNamespaceIdentifier()))
			throw new URNFormatException("Namespace identifier '" + urnComponents.getNamespaceIdentifier() + "' is not understood by " + this.getClass().getSimpleName());
		//parseNamespaceSpecificString(urnComponents.getNamespaceSpecificString());
	}

	/**
	 * 
	 * @param homeCommunityId
	 * @param repositoryUniqueId
	 * @param documentUniqueId
	 * @throws URNFormatException
	 */
	public GlobalArtifactURNImpl(String homeCommunityId, String repositoryUniqueId, String documentUniqueId) 
	throws URNFormatException
	{
		super(getManagedNamespace());
		
		if( !COMMUNITY_ID_PATTERN.matcher(homeCommunityId).matches() )
			throw new URNFormatException("Home community ID is not in the valid format.");
		if( !REPOSITORY_ID_PATTERN.matcher(repositoryUniqueId).matches() )
			throw new URNFormatException("Repository unique ID is not in the valid format.");
		if( !DOCUMENT_ID_PATTERN.matcher(documentUniqueId).matches() )
			throw new URNFormatException("Document unique ID is not in the valid format.");
		
		this.homeCommunityId = homeCommunityId;
		this.repositoryUniqueId = repositoryUniqueId;
		this.documentUniqueId = documentUniqueId;
	}

	@Override
	public String getNamespaceSpecificString(SERIALIZATION_FORMAT serializationFormat)
	{
		return this.homeCommunityId + NSS_DELIMITER + this.repositoryUniqueId + NSS_DELIMITER + this.documentUniqueId;
	}

	/**
	 * @param namespaceSpecificString
	 * @param base32Encoded - unused in this implementation
	 * @throws URNFormatException 
	 */
	@Override
	public void parseNamespaceSpecificString(NamespaceIdentifier namespace, String namespaceSpecificString, SERIALIZATION_FORMAT serializationFormat) 
	throws URNFormatException
	{
		Matcher nssMatcher = NSS_PATTERN.matcher(namespaceSpecificString);
		if(! nssMatcher.matches() )
			throw new URNFormatException("The namespace specific string '" + namespaceSpecificString + "' is not in the correct format.");
		
		this.homeCommunityId = nssMatcher.group(COMMUNITY_ID_GROUP);
		this.repositoryUniqueId = nssMatcher.group(REPOSITORY_ID_GROUP);
		
		String tmpDocumentId = nssMatcher.group(DOCUMENT_ID_GROUP);
		
		switch(serializationFormat)
		{
		case PATCH83_VFTP:
			setDocumentUniqueId( Base32ConversionUtility.base32Decode(tmpDocumentId) );
			break;
		case RFC2141:
			this.documentUniqueId = tmpDocumentId;
			break;
		case VFTP:
			super.parseNamespaceSpecificString(namespace, tmpDocumentId, serializationFormat);
			this.documentUniqueId = super.getNamespaceSpecificString();
			break;
		case CDTP:
			this.documentUniqueId= URN.FILENAME_TO_RFC2141_ESCAPING.escapeIllegalCharacters(tmpDocumentId);
			break;
		case NATIVE:
			setDocumentUniqueId(tmpDocumentId);
			break;
		}
	}

	/**
	 * @see gov.va.med.GlobalArtifactURN#getHomeCommunityId()
	 */
	@Override
	public String getHomeCommunityId()
	{
		return homeCommunityId;
	}

	/**
	 * @see gov.va.med.GlobalArtifactURN#getRepositoryUniqueId()
	 */
	@Override
	public String getRepositoryUniqueId()
	{
		return repositoryUniqueId;
	}

	@Override
	public String getDocumentUniqueId()
	{
		return URN.RFC2141_ESCAPING.unescapeIllegalCharacters(this.documentUniqueId);
	}

	/**
	 * @param documentUniqueId the documentUniqueId to set
	 */
	public void setDocumentUniqueId(String documentUniqueId)
	{
		this.documentUniqueId = URN.RFC2141_ESCAPING.escapeIllegalCharacters(documentUniqueId);
	}
	
	// ================================================================================================
	// GlobalArtifactIdentifier Implementation
	
	/**
	 * @see gov.va.med.GlobalArtifactIdentifier#equalsGlobalArtifactIdentifier(gov.va.med.GlobalArtifactIdentifier)
	 */
	@Override
	public boolean equalsGlobalArtifactIdentifier(GlobalArtifactIdentifier that)
	{
		return GlobalArtifactIdentifierImpl.equalsGlobalArtifactIdentifier(this, that);
	}
	/**
	 * @see gov.va.med.RoutingToken#isEquivalent(gov.va.med.RoutingToken)
	 */
	@Override
	public boolean isEquivalent(RoutingToken that)
	{
		return RoutingTokenImpl.isEquivalent(this, that);
	}
	
	/**
	 * @see gov.va.med.RoutingToken#isIncluding(gov.va.med.RoutingToken)
	 */
	@Override
	public boolean isIncluding(RoutingToken that)
	{
		return RoutingTokenImpl.isIncluding(this, that);
	}
	
	/**
	 * @see gov.va.med.RoutingToken#toRoutingTokenString()
	 */
	@Override
	public String toRoutingTokenString()
	{
		return RoutingTokenImpl.toRoutingTokenString(this);
	}
	
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(GlobalArtifactIdentifier o)
	{
		return RoutingTokenImpl.compare(this, o);
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public GlobalArtifactIdentifier clone() 
	throws CloneNotSupportedException
	{
		try
		{
			return new GlobalArtifactURNImpl(getHomeCommunityId(), getRepositoryUniqueId(), getDocumentUniqueId());
		}
		catch (URNFormatException x)
		{
			getLogger().error("");
			throw new CloneNotSupportedException(
				"URNFormat exception cloning (" +
				toString() + ") - " + x.getMessage()
			);
		}
	}
	
	
	// ================================================================================================
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.documentUniqueId == null) ? 0 : this.documentUniqueId.hashCode());
		result = prime * result + ((this.homeCommunityId == null) ? 0 : this.homeCommunityId.hashCode());
		result = prime * result + ((this.repositoryUniqueId == null) ? 0 : this.repositoryUniqueId.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final GlobalArtifactURNImpl other = (GlobalArtifactURNImpl) obj;
		if (this.getNamespaceIdentifier() == null)
		{
			if (other.getNamespaceIdentifier() != null)
				return false;
		}
		else if (!this.getNamespaceIdentifier().equals(other.getNamespaceIdentifier()))
			return false;
		
		if (this.documentUniqueId == null)
		{
			if (other.documentUniqueId != null)
				return false;
		}
		else if (!this.documentUniqueId.equals(other.documentUniqueId))
			return false;
		if (this.homeCommunityId == null)
		{
			if (other.homeCommunityId != null)
				return false;
		}
		else if (!this.homeCommunityId.equals(other.homeCommunityId))
			return false;
		if (this.repositoryUniqueId == null)
		{
			if (other.repositoryUniqueId != null)
				return false;
		}
		else if (!this.repositoryUniqueId.equals(other.repositoryUniqueId))
			return false;
		return true;
	}

}
