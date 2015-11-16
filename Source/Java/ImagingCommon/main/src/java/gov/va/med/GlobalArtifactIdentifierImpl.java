/**
 * 
 */
package gov.va.med;

import gov.va.med.exceptions.GlobalArtifactIdentifierFormatException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.utility.Base32ConversionUtility;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * This class contains a generic Global Artifact Identifier that may
 * also be represented as a URN.
 * 
 * @author vhaiswbeckec
 *
 */
@URNType(namespace="gaid")
public class GlobalArtifactIdentifierImpl
extends URN
implements GlobalArtifactIdentifier, Comparable<GlobalArtifactIdentifier>
{
	private static final long serialVersionUID = 1L;

	public static final String homeCommunityIdRegex = "[a-zA-Z0-9_\\.]+";
	public static final String repositoryIdRegex = "[a-zA-Z0-9_\\.]+";
	public static final String documentIdRegex = "[a-zA-Z0-9_:\\-\\.]+";
	public static final String additionalIdRegex = "(?:\\[([a-zA-Z0-9_:\\-\\.]+)\\])?";
	public static final String namespaceSpecificStringRegex = 
		"(" + homeCommunityIdRegex + ")" + 
		URN.namespaceSpecificStringDelimiter +
		"(" + repositoryIdRegex + ")" +
		URN.namespaceSpecificStringDelimiter +
		"(" + documentIdRegex + ")" +
		additionalIdRegex +
		additionalIdRegex +
		additionalIdRegex +
		additionalIdRegex +
		additionalIdRegex;
	public static final Pattern namespaceSpecificStringPattern = Pattern.compile(namespaceSpecificStringRegex);
	public static final int homeCommunityIdGroup = 1;
	public static final int repositoryIdGroup = 2;
	public static final int documentIdGroup = 3;
	public static final int additionalIdGroup0 = 4;
	public static final int additionalIdGroup1 = 5;
	public static final int additionalIdGroup2 = 6;
	public static final int additionalIdGroup3 = 7;
	public static final int additionalIdGroup4 = 8;

	static
	{
		Logger.getLogger(URN.class).info("GAI regex = '" + namespaceSpecificStringRegex + "'");
	}
	
	private static final String namespace = "gaid";
	private static NamespaceIdentifier namespaceIdentifier = null;
	public static synchronized NamespaceIdentifier getManagedNamespace()
	{
		if(namespaceIdentifier == null)
			namespaceIdentifier = new NamespaceIdentifier(namespace);
		return namespaceIdentifier;
	}
	
	/**
	 * This class can represent any home community ID.
	 * 
	 * @param homeCommunityId
	 * @param repositoryId 
	 * @param documentId 
	 * @return
	 */
	public static boolean isApplicableHomeCommunityId(String homeCommunityId, String repositoryId, String documentId)
	{
		return true;
	}
	
	/**
	 * 
	 * @param g1
	 * @param g2
	 * @return
	 */
	public static boolean equalsGlobalArtifactIdentifier(GlobalArtifactIdentifier g1, GlobalArtifactIdentifier g2)
	{
		return
			g1.getHomeCommunityId().equals(g2.getHomeCommunityId()) &&
			g1.getRepositoryUniqueId().equals(g2.getRepositoryUniqueId()) &&
			g1.getDocumentUniqueId().equals(g2.getDocumentUniqueId());
	}
	
	/**
	 * 
	 * @param g1
	 * @param g2
	 * @return
	 */
	public static int compareTo(GlobalArtifactIdentifier g1, GlobalArtifactIdentifier g2)
	{
		if(g1 == null)
			return 1;
		if(g2 == null)
			return -1;
		
		int relation = g1.getHomeCommunityId() == null ? 
			1 : 
			g1.getHomeCommunityId().compareTo(g2.getHomeCommunityId());
		if(relation != 0)
			return relation;
		
		relation = g1.getRepositoryUniqueId() == null ? 
			1 : 
			g1.getRepositoryUniqueId().compareTo(g2.getRepositoryUniqueId());
		if(relation != 0)
			return relation;
		
		return g1.getDocumentUniqueId() == null ? 
			1 : 
			g1.getDocumentUniqueId().compareTo(g2.getDocumentUniqueId());
		
	}
	
	/**
	 * 
	 * @param homeCommunityId
	 * @param repositoryUniqueId
	 * @param documentUniqueId
	 * @return
	 * @throws URNFormatException
	 */
	public static GlobalArtifactIdentifier createFromGlobalArtifactIdentifiers(
		String homeCommunityId, 
		String repositoryUniqueId, 
		String documentUniqueId,
		String... additionalIdentifiers) 
	throws URNFormatException
	{
		return new GlobalArtifactIdentifierImpl(homeCommunityId, repositoryUniqueId, documentUniqueId, additionalIdentifiers);
	}
	
	/**
	 * 
	 * @param urnComponents
	 * @return
	 * @throws URNFormatException
	 */
	public static GlobalArtifactIdentifier create(URNComponents urnComponents, SERIALIZATION_FORMAT serializationFormat) 
	throws URNFormatException
	{
		if( ! getManagedNamespace().equals(urnComponents.getNamespaceIdentifier()) )
			throw new URNFormatException("namespace identifier '" + urnComponents.getNamespaceIdentifier() + "' does not match managed namespace '" + getManagedNamespace().getNamespace() + "'.");
		return new GlobalArtifactIdentifierImpl(urnComponents, serializationFormat);
	}
	
	// ==========================================================================================
	//
	// ==========================================================================================
	private String homeCommunityId;
	private String repositoryUniqueId;
	private String documentUniqueId;
	
	/**
	 * Declared 'protected' so that derived classes can set their namespace.
	 * 
	 * @param namespaceIdentifier
	 * @throws URNFormatException
	 */
	protected GlobalArtifactIdentifierImpl(NamespaceIdentifier namespaceIdentifier) 
	throws URNFormatException
	{
		super(namespaceIdentifier);
	}

	/**
	 * Declared 'protected' so that derived classes can set their namespace.
	 * 
	 * @param namespaceIdentifier
	 * @param namespaceSpecificString
	 * @param additionalIdentifiers
	 * @throws URNFormatException
	 */
	protected GlobalArtifactIdentifierImpl(
		NamespaceIdentifier namespaceIdentifier, 
		String namespaceSpecificString) 
	throws URNFormatException
	{
		super(namespaceIdentifier, namespaceSpecificString);
	}

	/**
	 * Declared 'protected' so that derived classes can set their namespace.
	 * 
	 * @param homeCommunityId
	 * @param repositoryUniqueId
	 * @param documentUniqueId
	 * @throws URNFormatException
	 */
	protected GlobalArtifactIdentifierImpl(
		String homeCommunityId, 
		String repositoryUniqueId, 
		String documentUniqueId,
		String... additionalIdentifiers) 
	throws URNFormatException
	{
		this( 
			getManagedNamespace(),
			homeCommunityId,
			repositoryUniqueId,
			documentUniqueId,
			additionalIdentifiers
		);
	}

	protected GlobalArtifactIdentifierImpl(
		NamespaceIdentifier namespaceIdentifier,
		String homeCommunityId, 
		String repositoryUniqueId, 
		String documentUniqueId,
		String... additionalIdentifiers) 
	throws URNFormatException
	{
		super(namespaceIdentifier);
		setHomeCommunityId( homeCommunityId );
		setRepositoryUniqueId( repositoryUniqueId );
		setDocumentUniqueId( documentUniqueId );
		
		if(additionalIdentifiers != null)
			for(int index=0; index < additionalIdentifiers.length; ++index)
				this.setAdditionalIdentifier(index, additionalIdentifiers[index]);
	}
	
	/**
	 * Declared 'protected' so that derived classes can set their namespace.
	 * 
	 * @param urnComponents
	 * @throws URNFormatException 
	 */
	protected GlobalArtifactIdentifierImpl(URNComponents urnComponents, SERIALIZATION_FORMAT serializationFormat) 
	throws URNFormatException
	{
		super(urnComponents, serializationFormat);
		
	}

	/**
	 * @throws URNFormatException
	 */
	protected GlobalArtifactIdentifierImpl() 
	throws URNFormatException
	{
		super(getManagedNamespace());
	}
	
	/**
	 * @see gov.va.med.GlobalArtifactIdentifier#getHomeCommunityId()
	 */
	@Override
	public String getHomeCommunityId()
	{
		return homeCommunityId;
	}

	/**
	 * @see gov.va.med.GlobalArtifactIdentifier#getRepositoryUniqueId()
	 */
	@Override
	public String getRepositoryUniqueId()
	{
		return repositoryUniqueId;
	}

	/**
	 * @param homeCommunityId the homeCommunityId to set
	 */
	protected void setHomeCommunityId(String homeCommunityId)
	{
		this.homeCommunityId = homeCommunityId;
	}

	/**
	 * @param repositoryUniqueId the repositoryUniqueId to set
	 */
	protected void setRepositoryUniqueId(String repositoryUniqueId)
	{
		this.repositoryUniqueId = repositoryUniqueId;
	}

	/**
	 * @see gov.va.med.GlobalArtifactIdentifier#getDocumentUniqueId()
	 */
	@Override
	public String getDocumentUniqueId()
	{
		return getDocumentUniqueId(SERIALIZATION_FORMAT.NATIVE);
	}
	
	public String getDocumentUniqueId(SERIALIZATION_FORMAT serializationFormat)
	{
		switch(serializationFormat)
		{
		case RFC2141:
			return this.documentUniqueId;
		case CDTP:
			return URN.FILENAME_TO_RFC2141_ESCAPING.escapeIllegalCharacters(this.documentUniqueId);
		case PATCH83_VFTP:
			return Base32ConversionUtility.base32Encode( getDocumentUniqueId(SERIALIZATION_FORMAT.NATIVE) );
		case NATIVE:
		default:
			return URN.RFC2141_ESCAPING.unescapeIllegalCharacters(this.documentUniqueId);
		}
	}
	
	/**
	 * @param documentUniqueId the documentUniqueId to set
	 * @throws URNFormatException 
	 */
	public void setDocumentUniqueId(String documentUniqueId) 
	throws URNFormatException
	{
		this.documentUniqueId = URN.RFC2141_ESCAPING.escapeIllegalCharacters(documentUniqueId);
	}

	@Override
	public int compareTo(GlobalArtifactIdentifier o)
	{
		return GlobalArtifactIdentifierImpl.compareTo(this, o);
	}

	/**
	 * This class takes the entirety of the home community, repository and the document IDs
	 * and stores them in the NSS when represented as a URN
	 * 
	 * @param serializationFormat - determines the allowable character set and escaping
	 */
	@Override
	public void parseNamespaceSpecificString(
		NamespaceIdentifier namespace, 
		String nss, 
		SERIALIZATION_FORMAT serializationFormat)
	throws URNFormatException
	{
		Matcher matcher = GlobalArtifactIdentifierImpl.namespaceSpecificStringPattern.matcher(nss);
		if( ! matcher.matches() )
			throw new URNFormatException("The NSS '" + nss + "' is not valid for an instance of " + this.getClass().getSimpleName());

		boolean parseAdditionalIdentifiers = true;
		setHomeCommunityId( matcher.group(GlobalArtifactIdentifierImpl.homeCommunityIdGroup) );
		setRepositoryUniqueId( matcher.group(GlobalArtifactIdentifierImpl.repositoryIdGroup) );
		
		String tmpDocumentId = matcher.group(GlobalArtifactIdentifierImpl.documentIdGroup);
		switch(serializationFormat)
		{
		case PATCH83_VFTP:
			setDocumentUniqueId( Base32ConversionUtility.base32Decode(tmpDocumentId) );
			break;
		case RFC2141:
			this.documentUniqueId = tmpDocumentId;
			break;
		case CDTP:
			this.documentUniqueId= URN.FILENAME_TO_RFC2141_ESCAPING.escapeIllegalCharacters(tmpDocumentId);
			break;
		case VFTP:
			super.parseNamespaceSpecificString(namespace, tmpDocumentId, serializationFormat);
			this.documentUniqueId = super.getNamespaceSpecificString();
			parseAdditionalIdentifiers = false;
			break;
		case NATIVE:
		case RAW:
			setDocumentUniqueId(tmpDocumentId);
			break;
		}
		
		if(parseAdditionalIdentifiers)
		{
			if(matcher.group(additionalIdGroup0) != null)
				setAdditionalIdentifier(0, serializationFormat.deserialize(matcher.group(additionalIdGroup0)) );
			if(matcher.group(additionalIdGroup1) != null)
				setAdditionalIdentifier(1, serializationFormat.deserialize(matcher.group(additionalIdGroup1)) );
			if(matcher.group(additionalIdGroup2) != null)
				setAdditionalIdentifier(2, serializationFormat.deserialize(matcher.group(additionalIdGroup2)) );
			if(matcher.group(additionalIdGroup3) != null)
				setAdditionalIdentifier(3, serializationFormat.deserialize(matcher.group(additionalIdGroup3)) );
			if(matcher.group(additionalIdGroup4) != null)
				setAdditionalIdentifier(4, serializationFormat.deserialize(matcher.group(additionalIdGroup4)) );
		}
	}
	
	/**
	 * Build the namespace specific string.  Note that this method is called
	 * by the default toString methods so it must produce a String that is
	 * parsable by parseNamespaceSpecificString().
	 */
	@Override
	public String getNamespaceSpecificString(SERIALIZATION_FORMAT serializationFormat)
	{
		return 
			getHomeCommunityId() + URN.namespaceSpecificStringDelimiter +
			getRepositoryUniqueId() + URN.namespaceSpecificStringDelimiter +
			getDocumentUniqueId(serializationFormat);
	}

	@Override
	public String toStringRFC2141()
	{
		StringBuilder ahnold = new StringBuilder();
		
		// build the scheme identifier
		ahnold.append(urnSchemaIdentifier);
		ahnold.append(urnComponentDelimiter);

		// build the namespace identifier
		ahnold.append(this.getNamespaceIdentifier());
		ahnold.append(urnComponentDelimiter);
		
		ahnold.append( this.getNamespaceSpecificString(SERIALIZATION_FORMAT.RFC2141) );
		
		return ahnold.toString();
	}
	
	/**
	 * If the namespace specific string has all of the components of a GAI then this
	 * method will create a GAI of the type mapped to the home community ID and
	 * return it.
	 * 
	 * @return
	 */
	public GlobalArtifactIdentifier createFromEmbededGlobalArtifactIdentifier()
	{
		try
		{
			return GlobalArtifactIdentifierFactory.create(
				getHomeCommunityId(), 
				getRepositoryUniqueId(), 
				getDocumentUniqueId()
			);
		}
		catch (GlobalArtifactIdentifierFormatException x)
		{
			getLogger().info("Attempted to but failed to create a global artifact identifier from namespace specific string '" + getNamespaceSpecificString() + "'.");
			return null;
		}
	}
	
	// =====================================================================================
	// GlobalArtifactIdentifier
	// =====================================================================================
	
	@Override
	public boolean equalsGlobalArtifactIdentifier(GlobalArtifactIdentifier that)
	{
		return
		this.getHomeCommunityId().equals(that.getHomeCommunityId()) &&
		this.getRepositoryUniqueId().equals(that.getRepositoryUniqueId()) &&
		this.getDocumentUniqueId().equals(that.getDocumentUniqueId());
	}

	@Override
	public boolean isEquivalent(RoutingToken that)
	{
		return RoutingTokenImpl.isEquivalent(this, that);
	}

	@Override
	public boolean isIncluding(RoutingToken that)
	{
		return RoutingTokenImpl.isIncluding(this, that);
	}

	/**
	 * 
	 */
	@Override
	public GlobalArtifactIdentifierImpl clone()
	throws CloneNotSupportedException
	{
		try
		{
			return new GlobalArtifactIdentifierImpl(getHomeCommunityId(), getRepositoryUniqueId(), getDocumentUniqueId());
		}
		catch (URNFormatException x)
		{
			throw new CloneNotSupportedException(x.getMessage());
		}
	}
	
	@Override
	public String toRoutingTokenString() 
	{
		return getHomeCommunityId() + "," + getRepositoryUniqueId();
	}

	// =========================================================================================
	// Eclipse Generated Methods
	// =========================================================================================
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final GlobalArtifactIdentifierImpl other = (GlobalArtifactIdentifierImpl) obj;
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
