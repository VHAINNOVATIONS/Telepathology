/**
 * 
 */
package gov.va.med.imaging;

import gov.va.med.*;
import gov.va.med.exceptions.GlobalArtifactIdentifierFormatException;
import gov.va.med.imaging.exceptions.DocumentURNFormatException;
import gov.va.med.imaging.exceptions.ImageURNFormatException;
import gov.va.med.imaging.exceptions.URNFormatException;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A DocumentURN uniquely identifies a document in the VA
 * domain.  A DocumentURN is an alias for a ImageURN, though 
 * the namespace is different and the classes are siblings,
 * not derivations.
 * 
 * NOTE: both this class and ImageURN MUST implement GlobalArtifactURN!
 * 
 * @see gov.va.med.imaging.ImageURN
 * 
 * @author vhaiswbeckec
 *
 */
@URNType(namespace="vadoc")
public class DocumentURN
extends ImageURN
implements Serializable, GlobalArtifactIdentifier
{
	private static final long serialVersionUID = 1L;
	
	private static final String namespace = "vadoc";
	private static NamespaceIdentifier namespaceIdentifier = null;
	public static final WellKnownOID DEFAULT_HOME_COMMUNITY_ID = WellKnownOID.VA_DOCUMENT;
	
	public static synchronized NamespaceIdentifier getManagedNamespace()
	{
		if(namespaceIdentifier == null)
			namespaceIdentifier = new NamespaceIdentifier(namespace);
		return namespaceIdentifier;
	}
	
	/**
	 * 
	 * @param originatingSiteId
	 * @param assignedId
	 * @return
	 */
	public static DocumentURN create(
		String originatingSiteId, 
		String documentId, 
		String documentSetId, 
		String patientIcn)
	throws URNFormatException
	{
		String msg = 
			"originating site '" + originatingSiteId + 
			"', assigned id '" + documentId + 
			"', studyId '" + documentSetId +
			"', patientIcn '" + patientIcn + "'.";
		if(originatingSiteId == null || ! siteIdPattern.matcher(originatingSiteId).matches() )
			throw new DocumentURNFormatException("SiteID [" + originatingSiteId + "] is not valid.  " + msg );
		if(documentId == null || ! imageIdPattern.matcher(documentId).matches() )
			throw new DocumentURNFormatException("Image ID [" + documentId + "] is not valid.  " + msg);
		if(documentSetId == null || ! groupIdPattern.matcher(documentSetId).matches() || documentSetId.indexOf('-') >= 0)
			throw new DocumentURNFormatException("Study ID [" + documentSetId + "] is not valid.  " + msg);
		if(patientIcn == null || ! patientIcnPattern.matcher(patientIcn).matches()) 
			throw new DocumentURNFormatException("Patient ICN [" + patientIcn + "] is not valid.  " + msg);		
		
		return new DocumentURN(originatingSiteId, documentId, documentSetId, patientIcn);
	}

	/**
	 * Create a DocumentURN instance from an ImageURN.
	 * This provide the translation within the datasource, using the same
	 * RPCs as image retrieval and presenting results in Document semantics.
	 * @param imageUrn
	 * @return
	 * @throws URNFormatException
	 */
	public static DocumentURN create(ImageURN imageUrn)
	throws URNFormatException
	{
		DocumentURN documentUrn = create( 
			imageUrn.getOriginatingSiteId(),
			imageUrn.getInstanceId(),
			imageUrn.getStudyId(), 
			imageUrn.getPatientId());
		documentUrn.setPatientIdentifierTypeIfNecessary(imageUrn.getPatientIdentifierType());
		return documentUrn;
	}
	
	/**
	 * 
	 * @return
	 */
	public static DocumentURN create(URNComponents urnComponents, SERIALIZATION_FORMAT serializationFormat)
	throws URNFormatException
	{
		return new DocumentURN(urnComponents, serializationFormat);
	}
	
	public static DocumentURN createFromBase32Encoded(URNComponents urnComponents)
	throws URNFormatException
	{
		return new DocumentURN(urnComponents, SERIALIZATION_FORMAT.PATCH83_VFTP);
	}
	
	/**
	 * 
	 * @param homeCommunityId
	 * @param repositoryId
	 * @param documentId
	 * @return
	 * @throws URNFormatException
	 */
	public static DocumentURN createFromGlobalArtifactIdentifiers(
		String homeCommunityId, 
		String repositoryId,
		String documentId) 
	throws URNFormatException
	{
		return createFromGlobalArtifactIdentifiers( homeCommunityId, repositoryId, documentId, (String[])null);
	}
	
	/**
	 * 
	 * @param homeCommunityId
	 * @param repositoryId
	 * @param documentId
	 * @return
	 * @throws URNFormatException
	 */
	public static DocumentURN createFromGlobalArtifactIdentifiers(
		String homeCommunityId, 
		String repositoryId, 
		String documentId,
		String... additionalIdentifiers)
	throws URNFormatException
	{
		try
		{
			if( ! DEFAULT_HOME_COMMUNITY_ID.isApplicable(homeCommunityId) )
				throw new URNFormatException("Home community ID '" + homeCommunityId + "' is not valid for type '" + DocumentURN.class.getSimpleName() + "'.");
			
			return new DocumentURN(repositoryId, documentId);
		}
		catch (URNFormatException x)
		{
			throw new URNFormatException(
				"Unable to create URN of type '" + DocumentURN.class.getSimpleName() + "' from " +
				homeCommunityId + ":" +
				repositoryId + ":" + 
				documentId );
		}
	}

	/**
	 * Required static method, must return TRUE when this class can represent 
	 * a global artifact ID with the given home community ID.
	 * 
	 * @param homeCommunityId
	 * @return
	 */
	public static boolean isApplicableHomeCommunityId(String homeCommunityId, String repositoryId, String documentId)
	{
		return 
			DocumentURN.namespace.equalsIgnoreCase(homeCommunityId) ||
			WellKnownOID.VA_DOCUMENT.isApplicable(homeCommunityId);
	}
	
	// ==================================================================================
	// Instance Members
	// ==================================================================================
	/**
	 * @param urnComponents
	 * @throws URNFormatException
	 */
	private DocumentURN(URNComponents urnComponents, SERIALIZATION_FORMAT serializationFormat) 
	throws URNFormatException
	{
		super(urnComponents, serializationFormat);
	}

	/**
	 * This constructor is used to create an ImageURN instance
	 * from its constituent parts (i.e. from scratch).
	 * 
	 * @param originatingSiteId
	 * @param documentId
	 * @param documentSetId
	 * @param patientId
	 * @param imageModality
	 * @throws URNFormatException 
	 * @throws ImageURNFormatException
	 */
	private DocumentURN(
		String originatingSiteId, 
		String documentId, 
		String documentSetId, 
		String patientId) 
	throws URNFormatException
	{
		super(DocumentURN.getManagedNamespace());
		this.originatingSiteId = originatingSiteId; 
		this.instanceId = documentId; 
		this.groupId = documentSetId; 
		this.patientId= patientId; 
	}
	
	/**
	 * @param repositoryId
	 * @param documentId
	 * @throws GlobalArtifactIdentifierFormatException 
	 */
	private DocumentURN(String repositoryId, String documentId)
	throws URNFormatException
	{
		super(DocumentURN.getManagedNamespace());
		this.originatingSiteId = repositoryId;
		parseDocumentUniqueIdIntoFields(documentId);
	}

	// ==============================================================================
	// Instance Members
	// ==============================================================================
	
	@Override
	public NamespaceIdentifier getNamespaceIdentifier()
	{
		return DocumentURN.getManagedNamespace();
	}

	// ======================================================================
	// Semantic, alias methods
	// These just make documents look like documents, rather than generic
	// instances
	// ======================================================================
	
	public String getDocumentSetId()
	{
		return getGroupId();
	}
	
	public String getDocumentId()
	{
		return getInstanceId();
	}
	
	// =================================================================
	// Implementations that make this a GlobalArtifactIdentifier
	// =================================================================
	
	@Override
	public String getHomeCommunityId()
	{
		// DocumentURN are always in the VA community
		return DEFAULT_HOME_COMMUNITY_ID.getCanonicalValue().toString();
	}

	// formatting of the document unique ID, used for GlobalArtifactIdentifier support
	private static final String documentUniqueIdRegex =
		"(" + IMAGEID_REGEX + ")" +
		URN.namespaceSpecificStringDelimiter +
		"(" + GROUPID_REGEX + ")" +
		URN.namespaceSpecificStringDelimiter + 
		"(" + PATIENTID_REGEX + ")";
	private static final Pattern documentUniqueIdPattern = Pattern.compile(documentUniqueIdRegex);
	private static final int DOCUMENTUNIQUEID_DOCUMENT_ID_GROUP = 1;
	private static final int DOCUMENTUNIQUEID_GROUP_ID_GROUP = 3;
	private static final int DOCUMENTUNIQUEID_PATIENT_ID_GROUP = 5;
	
	@Override
	public String getDocumentUniqueId()
	{
		return 
		getDocumentId() +
		URN.namespaceSpecificStringDelimiter +
		getDocumentSetId() + 
		URN.namespaceSpecificStringDelimiter +
		patientId;
	}

	private void parseDocumentUniqueIdIntoFields(String documentId)
	throws URNFormatException
	{
		Matcher matcher = DocumentURN.documentUniqueIdPattern.matcher(documentId);
		if(!matcher.matches())
			throw new URNFormatException("'" + documentId + "' is not a valid document identifier for type '" + this.getClass().getSimpleName() + "'.");
		
		instanceId = matcher.group(DOCUMENTUNIQUEID_DOCUMENT_ID_GROUP);
		groupId = matcher.group(DOCUMENTUNIQUEID_GROUP_ID_GROUP);
		patientId = matcher.group(DOCUMENTUNIQUEID_PATIENT_ID_GROUP);
	}
	
	// =================================================================
	// Implementations of clone, Comparable
	// =================================================================
	
	@Override
	public DocumentURN clone() 
	throws CloneNotSupportedException
	{
		try
		{
			return create(getOriginatingSiteId(), getInstanceId(), getGroupId(), getPatientId());
		} 
		catch (URNFormatException e)
		{
			throw new CloneNotSupportedException(e.getMessage());
		}
	}
	
	@Override
	public int compareTo(GlobalArtifactIdentifier o)
	{
		int relation = this.getHomeCommunityId().compareTo(o.getHomeCommunityId());
		if(relation != 0)
			return relation;
		relation = this.getRepositoryUniqueId().compareTo(o.getRepositoryUniqueId());
		if(relation != 0)
			return relation;
		return this.getDocumentUniqueId().compareTo(o.getDocumentUniqueId());
	}

	/**
	 * A special implementation of .equals() this is used with other
	 * GlobalArtifactIdentifier realizations.
	 * 
	 * @param that
	 * @return
	 */
	@Override
	public boolean equalsGlobalArtifactIdentifier(GlobalArtifactIdentifier that)
	{
		return GlobalArtifactIdentifierImpl.equalsGlobalArtifactIdentifier(this, that);
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
}
