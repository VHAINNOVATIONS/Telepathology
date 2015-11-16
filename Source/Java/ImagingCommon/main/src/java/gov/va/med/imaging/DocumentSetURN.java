/**
 * 
 */
package gov.va.med.imaging;

import gov.va.med.*;
import gov.va.med.exceptions.GlobalArtifactIdentifierFormatException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.utility.Base32ConversionUtility;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author VHAISWBECKEC
 * 
 *         Defines the syntax of a URN used to identify VA studies to the
 *         outside world. An StudyURN is defined to be in the following format:
 *         "urn" + ":" + "vadocset" + ":" + <site-id> + "-" + <assigned-id> +
 *         "-" + <patientICN> where: <site-id> is either the site from which the
 *         image originated or the string "va-imaging" <assigned-id> is the
 *         permanent, immutable ID as assigned by the originating site or a
 *         VA-domain unique ID (an Imaging GUID) <patientICN> is the VA
 *         Enterprise identifier for the patient of the study
 * 
 */
@URNType(namespace="vadocset")
public class DocumentSetURN
	extends AbstractImagingURN
	implements GlobalArtifactIdentifier
{
	private static final long serialVersionUID = 8014532894879837453L;

	/**
	 * All document sets are VA document sets by definition and must have the
	 * following home community ID.
	 */
	//private static final String REQUIRED_HOME_COMMUNITY_ID = WellKnownOID.VA_DOCUMENT.toString();

	private static final String namespace = "vadocset";
	private static NamespaceIdentifier namespaceIdentifier = null;

	public static synchronized NamespaceIdentifier getManagedNamespace()
	{
		if (namespaceIdentifier == null)
			namespaceIdentifier = new NamespaceIdentifier(namespace);
		return namespaceIdentifier;
	}

	private static final String namespaceSpecificStringRegex = "([^-]+)" + // the
																			// site
																			// ID
		URN.namespaceSpecificStringDelimiter + "([^-]+)" + // the group or study
															// ID
		URN.namespaceSpecificStringDelimiter + "([^-]+)"; // the patient ID
	private static final Pattern namespaceSpecificStringPattern = Pattern.compile(namespaceSpecificStringRegex);
	private static final int SITE_ID_GROUP = 1;
	private static final int GROUP_ID_GROUP = 2;
	private static final int PATIENT_ID_GROUP = 3;
	
	public static final int ADDITIONAL_IDENTIFIER_PATIENT_IDENTIFIER_TYPE_INDEX = 0;

	/**
	 * @param originatingSiteId
	 * @param assignedId
	 * @return
	 */
	public static DocumentSetURN create(String originatingSiteId, String assignedId, String patientId)
		throws URNFormatException
	{
		return new DocumentSetURN(originatingSiteId, assignedId, patientId);
	}

	/**
	 * Create a StudyURN instance from an DocumentSetURN. This provide the
	 * translation within the datasource, using the same RPCs as image retrieval
	 * and presenting results in Document semantics.
	 * 
	 * @param studyUrn
	 * @return
	 * @throws URNFormatException
	 */
	public static DocumentSetURN create(StudyURN studyUrn) throws URNFormatException
	{
		return create(studyUrn.getOriginatingSiteId(), studyUrn.getStudyId(), studyUrn.getPatientId());
	}

	/**
	 * 
	 * @param urnComponents
	 * @return
	 * @throws URNFormatException
	 */
	public static DocumentSetURN create(URNComponents urnComponents, SERIALIZATION_FORMAT serializationFormat) 
	throws URNFormatException
	{
		return new DocumentSetURN(urnComponents, serializationFormat);
	}

	/**
	 * 
	 * @param urnComponents
	 * @return
	 * @throws URNFormatException
	 */
	public static DocumentSetURN createFromBase32(URNComponents urnComponents) 
	throws URNFormatException
	{
		return new DocumentSetURN(urnComponents, SERIALIZATION_FORMAT.PATCH83_VFTP);
	}

	/**
	 * 
	 * @param homeCommunityId
	 * @param repositoryId
	 * @param documentId
	 * @return
	 * @throws URNFormatException
	 */
	public static DocumentSetURN createFromGlobalArtifactIdentifiers(
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
	public static DocumentSetURN createFromGlobalArtifactIdentifiers(
		String homeCommunityId, 
		String repositoryId,
		String documentId,
		String... additionalIdentifiers) 
	throws URNFormatException
	{
		if (DocumentSetURN.isApplicableHomeCommunityId(homeCommunityId, repositoryId, documentId))
			return new DocumentSetURN(repositoryId, documentId);
		else
			throw new URNFormatException("Home community ID '" + homeCommunityId
				+ "' cannot be used to create an ImageURN or its derivatives.");
	}

	/**
	 * Required static method, must return TRUE when this class can represent a
	 * global artifact ID with the given home community ID. NOTE: this class
	 * should not return true when the home community ID indicates VA_RADIOLOGY
	 * because that can be confused with the ImageURN which uses the same home
	 * community ID.
	 * 
	 * @param homeCommunityId
	 * @param repositoryId 
	 * @param documentId 
	 * @return
	 */
	public static boolean isApplicableHomeCommunityId(String homeCommunityId, String repositoryId, String documentId)
	{
		return DocumentSetURN.namespace.equalsIgnoreCase(homeCommunityId)
			|| WellKnownOID.VA_DOCUMENT.isApplicable(homeCommunityId);
	}

	// =======================================================================================
	// Constructors
	// =======================================================================================
	/**
	 * Pass-through for derived classes with different namespace.
	 * 
	 * @param namespaceIdentifier
	 * @throws URNFormatException
	 */
	protected DocumentSetURN(NamespaceIdentifier nid) throws URNFormatException
	{
		super(nid);
	}

	/**
	 * Provided only as a pass through for derived classes.
	 * 
	 */
	protected DocumentSetURN(NamespaceIdentifier namespaceIdentifier, String namespaceSpecificString,
		String... additionalIdentifiers) throws URNFormatException
	{
		super(namespaceIdentifier, namespaceSpecificString, additionalIdentifiers);
	}

	/**
	 * Used directly and a pass through for derived classes. The constructor
	 * called by the URN class when a URN derived class is being created from a
	 * String representation.
	 * 
	 * @param components
	 * @throws URNFormatException
	 */
	protected DocumentSetURN(URNComponents urnComponents, SERIALIZATION_FORMAT serializationFormat) 
	throws URNFormatException
	{
		super(urnComponents, serializationFormat);
	}

	/**
	 * 
	 * @param originatingSiteId
	 * @param studyId
	 * @param patientId
	 * @throws URNFormatException
	 */
	private DocumentSetURN(String originatingSiteId, String studyId, String patientId) throws URNFormatException
	{
		this(DocumentSetURN.getManagedNamespace(), originatingSiteId, studyId, patientId);
	}

	/**
	 * Defined to allow passthrough from derived classes
	 * 
	 * @param namespaceIdentifier
	 * @param originatingSiteId
	 * @param studyId
	 * @param patientId
	 * @throws URNFormatException
	 */
	protected DocumentSetURN(
		NamespaceIdentifier namespaceIdentifier, 
		String originatingSiteId, 
		String studyId,
		String patientId) 
	throws URNFormatException
	{
		super(namespaceIdentifier);

		if (!AbstractImagingURN.siteIdPattern.matcher(originatingSiteId).matches())
			throw new URNFormatException("The site ID '" + originatingSiteId + "' does not match the pattern '"
				+ AbstractImagingURN.siteIdPattern.pattern());
		this.originatingSiteId = originatingSiteId;

		if (!AbstractImagingURN.groupIdPattern.matcher(studyId).matches())
			throw new URNFormatException("The document set ID '" + studyId + "' does not match the pattern '"
				+ AbstractImagingURN.groupIdPattern.pattern());
		this.studyId = studyId;

		if (!AbstractImagingURN.patientIdPattern.matcher(patientId).matches())
			throw new URNFormatException("The patient ID '" + patientId + "' does not match the pattern '"
				+ AbstractImagingURN.patientIdPattern.pattern());
		this.patientId = patientId;
	}

	/**
	 * 
	 * @param repositoryUniqueId
	 * @param documentId
	 * @throws URNFormatException
	 */
	private DocumentSetURN(String repositoryUniqueId, String documentId) throws URNFormatException
	{
		this(DocumentSetURN.getManagedNamespace(), repositoryUniqueId, documentId);
	}

	/**
	 * Constructor from the GAI (IHE) identifiers
	 * 
	 * @param repositoryUniqueId
	 * @param documentId
	 * @throws URNFormatException
	 */
	protected DocumentSetURN(NamespaceIdentifier namespaceIdentifier, String repositoryUniqueId, String documentId)
		throws URNFormatException
	{
		super(namespaceIdentifier);
		try
		{
			this.originatingSiteId = repositoryUniqueId;
			parseDocumentUniqueId(documentId);
		}
		catch (GlobalArtifactIdentifierFormatException x)
		{
			throw new URNFormatException(x);
		}
	}

	// =====================================================================================================
	// 
	// =====================================================================================================

	/**
	 * Override the do-nothing version of this method to parse the namespace
	 * specific portion and find the component parts.
	 * 
	 * @param namespaceSpecificString
	 * @throws ImageURNFormatException
	 */
	@Override
	public void parseNamespaceSpecificString(NamespaceIdentifier namespace, String namespaceSpecificString, SERIALIZATION_FORMAT serializationFormat) 
	throws URNFormatException
	{
		if (namespaceSpecificString == null)
			throw new URNFormatException("The namespace specific string for a(n) " + this.getClass().getSimpleName()
				+ " cannot be null.");

		Matcher nssMatcher = namespaceSpecificStringPattern.matcher(namespaceSpecificString);

		if (!nssMatcher.matches())
		{
			String msg = "Namespace specific string '" + namespaceSpecificString + "' is not valid.";
			Logger.getAnonymousLogger().warning(msg);
			throw new URNFormatException(msg);
		}

		setOriginatingSiteId( nssMatcher.group(DocumentSetURN.SITE_ID_GROUP).trim() );
		setPatientIcn( nssMatcher.group(DocumentSetURN.PATIENT_ID_GROUP).trim() );
		
		String tmpGroupId = nssMatcher.group(DocumentSetURN.GROUP_ID_GROUP).trim();
		switch(serializationFormat)
		{
		case PATCH83_VFTP:
			setGroupId( Base32ConversionUtility.base32Decode(tmpGroupId) );
			break;
		case RFC2141:
			this.studyId = tmpGroupId;
			break;
		case CDTP:
			this.studyId = URN.FILENAME_TO_RFC2141_ESCAPING.escapeIllegalCharacters(tmpGroupId);
			break;
		case NATIVE:
			setGroupId(tmpGroupId);
			break;
		}
	}

	// ==================================================================================
	// Instance Members
	// ==================================================================================

	protected String originatingSiteId;
	protected String studyId;
	protected String patientId;

	@Override
	public String getHomeCommunityId()
	{
		// Images are always in the VA community
		return WellKnownOID.VA_DOCUMENT.getCanonicalValue().toString();
	}

	@Override
	public String getOriginatingSiteId()
	{
		return URN.RFC2141_ESCAPING.unescapeIllegalCharacters( this.originatingSiteId );
	}
	public void setOriginatingSiteId(String originatingSiteId)
	{
		this.originatingSiteId = URN.RFC2141_ESCAPING.escapeIllegalCharacters( originatingSiteId );
	}

	public String getDocumentSetId(){return getStudyId();}
	public String getStudyId(){return getGroupId();}
	public String getGroupId()
	{
		return URN.RFC2141_ESCAPING.unescapeIllegalCharacters( this.studyId );
	}
	
	public void setGroupId(String groupId)
	{
		this.studyId = URN.RFC2141_ESCAPING.escapeIllegalCharacters(groupId);
	}

	@Override
	public String getPatientId(){return getPatientIcn();}
	public String getPatientIcn()
	{
		return URN.RFC2141_ESCAPING.unescapeIllegalCharacters(this.patientId);
	}
	public void setPatientIcn(String patientId)
	{
		this.patientId = URN.RFC2141_ESCAPING.escapeIllegalCharacters(patientId);
	}

	// ===========================================================================
	// Serialization formats
	// ===========================================================================
	
	/* (non-Javadoc)
	 * @see gov.va.med.URN#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder ahnold = new StringBuilder();
		
		// build the scheme identifier
		ahnold.append(urnSchemaIdentifier);
		ahnold.append(urnComponentDelimiter);

		// build the namespace identifier
		ahnold.append(this.getNamespaceIdentifier());
		ahnold.append(urnComponentDelimiter);
		
		ahnold.append(this.getNamespaceSpecificString(SERIALIZATION_FORMAT.RFC2141));
		
		return ahnold.toString();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.URN#toStringAsNative()
	 */
	@Override
	public String toStringNative()
	{
		StringBuilder ahnold = new StringBuilder();
		
		// build the scheme identifier
		ahnold.append(urnSchemaIdentifier);
		ahnold.append(urnComponentDelimiter);

		// build the namespace identifier
		ahnold.append(this.getNamespaceIdentifier());
		ahnold.append(urnComponentDelimiter);
		
		ahnold.append(this.getNamespaceSpecificString(SERIALIZATION_FORMAT.NATIVE));
		
		return ahnold.toString();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.URN#toStringAsVAInternal()
	 */
	@Override
	public String toStringCDTP()
	{
		StringBuilder ahnold = new StringBuilder();
		
		// build the scheme identifier
		ahnold.append(urnSchemaIdentifier);
		ahnold.append(urnComponentDelimiter);

		// build the namespace identifier
		ahnold.append(this.getNamespaceIdentifier());
		ahnold.append(urnComponentDelimiter);
		
		ahnold.append(this.getNamespaceSpecificString(SERIALIZATION_FORMAT.CDTP));
		
		return ahnold.toString();
	}

	/**
	 * 
	 */
	@Override
	public String getNamespaceSpecificString(SERIALIZATION_FORMAT encoding)
	{
		StringBuilder ahnold = new StringBuilder();

		// build the namespace specific string
		ahnold.append( 
			encoding == SERIALIZATION_FORMAT.RFC2141 ? this.originatingSiteId : 
			encoding == SERIALIZATION_FORMAT.CDTP ? URN.FILENAME_ESCAPING.escapeIllegalCharacters(this.originatingSiteId) : 
			getOriginatingSiteId() 
		);
		ahnold.append( URN.namespaceSpecificStringDelimiter );
		ahnold.append( 
			encoding == SERIALIZATION_FORMAT.RFC2141 ? this.studyId : 
			encoding == SERIALIZATION_FORMAT.CDTP ? URN.FILENAME_ESCAPING.escapeIllegalCharacters(this.studyId) : 
			getGroupId() 
		);
		ahnold.append( URN.namespaceSpecificStringDelimiter );
		ahnold.append( 
			encoding == SERIALIZATION_FORMAT.RFC2141 ? this.patientId : 
			encoding == SERIALIZATION_FORMAT.CDTP ? URN.FILENAME_ESCAPING.escapeIllegalCharacters(this.patientId) : 
			getPatientIcn() 
		);

		return ahnold.toString();
	}

	// =====================================================================================
	// Global Artifact Identifier Implementation
	// =====================================================================================
	/**
	 * A special implementation of .equals() this is used with other
	 * GlobalArtifactIdentifier realizations.
	 * 
	 * @param that
	 * @return
	 */
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

	@Override
	public String getDocumentUniqueId()
	{
		return formatDocumentUniqueId(getStudyId(), getPatientId());
	}

	// formatting of the document unique ID, used for GlobalArtifactIdentifier
	// support
	private static final String documentUniqueIdRegex = "(" + GROUPID_REGEX + ")"
		+ URN.namespaceSpecificStringDelimiter + "(" + PATIENTID_REGEX + ")";
	private static final Pattern documentUniqueIdPattern = Pattern.compile(documentUniqueIdRegex);
	private static final int DOCUMENTUNIQUEID_STUDY_ID_GROUP = 1;
	private static final int DOCUMENTUNIQUEID_PATIENT_ID_GROUP = 3;

	/**
	 * 
	 * @param groupId
	 * @param patientId
	 * @return
	 */
	protected static String formatDocumentUniqueId(String groupId, String patientId)
	{
		StringBuilder sb = new StringBuilder();

		sb.append(groupId);
		if (patientId != null)
		{
			sb.append(URN.namespaceSpecificStringDelimiter);
			sb.append(patientId);
		}
		return sb.toString();
	}

	protected void parseDocumentUniqueId(String documentId) throws GlobalArtifactIdentifierFormatException
	{
		Matcher matcher = DocumentSetURN.documentUniqueIdPattern.matcher(documentId);
		if (!matcher.matches())
			throw new GlobalArtifactIdentifierFormatException("'" + documentId
				+ "' is not a valid document identifier for type '" + this.getClass().getSimpleName() + "'.");

		this.studyId = matcher.group(DOCUMENTUNIQUEID_STUDY_ID_GROUP);
		this.patientId = matcher.group(DOCUMENTUNIQUEID_PATIENT_ID_GROUP);
	}

	@Override
	public String getRepositoryUniqueId()
	{
		// for a VA document set, the sire ID is the repository ID
		return originatingSiteId;
	}

	@Override
	public NamespaceIdentifier getNamespaceIdentifier()
	{
		return DocumentSetURN.getManagedNamespace();
	}

	@Override
	public int compareTo(GlobalArtifactIdentifier o)
	{
		return GlobalArtifactIdentifierImpl.compareTo(this, o);
	}

	@Override
	public String getImagingIdentifier()
	{
		return getStudyId();
	}

	// ======================================================================================
	// Make a deep copy clone
	// ======================================================================================
	@Override
	public DocumentSetURN clone() throws CloneNotSupportedException
	{
		try
		{
			return create(getOriginatingSiteId(), getStudyId(), getPatientId());
		}
		catch (URNFormatException e)
		{
			throw new CloneNotSupportedException(e.getMessage());
		}
	}

	@Override
	protected int getPatientIdentifierTypeAdditionalIdentifierIndex()
	{
		return ADDITIONAL_IDENTIFIER_PATIENT_IDENTIFIER_TYPE_INDEX;
	}
}
