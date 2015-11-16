package gov.va.med.imaging;

import gov.va.med.*;
import gov.va.med.exceptions.GlobalArtifactIdentifierFormatException;
import gov.va.med.imaging.exceptions.ImageURNFormatException;
import gov.va.med.imaging.exceptions.StudyURNFormatException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.utility.Base32ConversionUtility;
import java.io.Serializable;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author VHAISWBECKEC
 *
 * Defines the syntax of a URN used to identify VA studies to the outside world.
 * An StudyURN is defined to be in the following format:
 * "urn" + ":" + "vastudy" + ":" + <site-id> + "-" + <assigned-id> + "-" + <patientICN>
 * where:
 *   <site-id> is either the site from which the image originated or the string "va-imaging"
 *   <assigned-id> is the permanent, immutable ID as assigned by the originating site or a
 *   VA-domain unique ID (an Imaging GUID) 
 *   <patientICN> is the VA Enterprise identifier for the patient of the study
 *   
 */
@URNType(namespace="vastudy")
public class StudyURN 
extends AbstractImagingURN
implements Serializable, PatientArtifactIdentifier
{
	private static final long serialVersionUID = 1L;
	
	private static final String namespace = "vastudy";
	public static final WellKnownOID DEFAULT_HOME_COMMUNITY_ID = WellKnownOID.VA_RADIOLOGY_IMAGE;
	
	private static NamespaceIdentifier namespaceIdentifier = new NamespaceIdentifier(namespace);
	public static synchronized NamespaceIdentifier getManagedNamespace()
	{
		return namespaceIdentifier;
	}

	private static final String namespaceSpecificStringRegex = 
		"([^-]+)" + 								// the site ID
		URN.namespaceSpecificStringDelimiter +
		"([^-]+)" +									// the group or study ID 
		URN.namespaceSpecificStringDelimiter + 
		"([^-]+)";									// the patient ID 
	private static final Pattern namespaceSpecificStringPattern = Pattern.compile(namespaceSpecificStringRegex);
	private static final int SITE_ID_GROUP = 1;
	private static final int GROUP_ID_GROUP = 2;
	private static final int PATIENT_ID_GROUP = 3;
	
	public static final int ADDITIONAL_IDENTIFIER_PATIENT_IDENTIFIER_TYPE_INDEX = 0;

	static
	{
		System.out.println("StudyURN regular expression is '" + namespaceSpecificStringRegex + "'.");
	}
	
	/**
	 * @param originatingSiteId
	 * @param assignedId
	 * @return
	 */
	public static StudyURN create(String originatingSiteId, String assignedId, String patientId)
	throws URNFormatException
	{
		if(BhieStudyURN.DEFAULT_REPOSITORY_ID.equals(originatingSiteId))
			return BhieStudyURN.create(assignedId, patientId);
		else
			return new StudyURN(originatingSiteId, assignedId, patientId);
	}

	/**
	 * Create a StudyURN instance from an DocumentSetURN.
	 * This provide the translation within the datasource, using the same
	 * RPCs as image retrieval and presenting results in Document semantics.
	 * 
	 * @param studyUrn
	 * @return
	 * @throws URNFormatException
	 */
	public static StudyURN create(DocumentSetURN documentSetUrn)
	throws URNFormatException
	{
		return create(documentSetUrn.getOriginatingSiteId(), documentSetUrn.getDocumentSetId(), documentSetUrn.getPatientId());
	}

	/**
	 * 
	 * @param urnComponents
	 * @return
	 * @throws URNFormatException
	 */
	public static StudyURN create(URNComponents urnComponents, SERIALIZATION_FORMAT serializationFormat) 
	throws URNFormatException
	{
		try
		{
			return new StudyURN(urnComponents, serializationFormat);
		}
		catch(URNFormatException urnfX)
		{
			// if the study URN originating Site ID is site 200 then this is really a BHIE URN,
			// ClinicalDisplay does not recognize anything but VA URNs, this kludge insulates
			// the rest of the system from that.
			// If this isn't a BHIE study URN then throw the original exception.
			try{return BhieStudyURN.create(urnComponents, serializationFormat);}
			catch(URNFormatException urnfX2){throw urnfX;}
		}
	}

	/**
	 * 
	 * @param urnComponents
	 * @return
	 * @throws URNFormatException
	 */
	public static StudyURN createFromBase32(URNComponents urnComponents) 
	throws URNFormatException
	{
		try
		{
			return new StudyURN(urnComponents, SERIALIZATION_FORMAT.PATCH83_VFTP);
		}
		catch(URNFormatException urnfX)
		{
			// if the study URN originating Site ID is site 200 then this is really a BHIE URN,
			// ClinicalDisplay does not recognize anything but VA URNs, this kludge insulates
			// the rest of the system from that.
			return BhieStudyURN.createFromBase32(urnComponents);
		}
	}
	
	/**
	 * 
	 * @param homeCommunityId
	 * @param repositoryId
	 * @param documentId
	 * @return
	 * @throws URNFormatException
	 */
	public static StudyURN createFromGlobalArtifactIdentifiers(
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
	public static StudyURN createFromGlobalArtifactIdentifiers(
		String homeCommunityId, 
		String repositoryId, 
		String documentId,
		String... additionalIdentifiers)
	throws URNFormatException
	{
		if( BhieStudyURN.isApplicableHomeCommunityId(homeCommunityId, repositoryId, documentId) )
			return BhieStudyURN.createFromGlobalArtifactIdentifiers(homeCommunityId, repositoryId, documentId);
		else if( StudyURN.isApplicableHomeCommunityId(homeCommunityId, repositoryId, documentId) )
			return new StudyURN( repositoryId, documentId );
		else
			throw new URNFormatException("Home community ID '" + homeCommunityId + "' cannot be used to create an ImageURN or its derivatives.");
	}

	/**
	 * Required static method, must return TRUE when this class can represent 
	 * a global artifact ID with the given home community ID.
	 * NOTE: this class should not return true when the home community ID
	 * indicates VA_RADIOLOGY because that can be confused with the ImageURN
	 * which uses the same home community ID.
	 * 
	 * @param homeCommunityId
	 * @param repositoryId 
	 * @param documentId 
	 * @return
	 */
	public static boolean isApplicableHomeCommunityId(String homeCommunityId, String repositoryId, String documentId)
	{
		return 
			StudyURN.namespace.equalsIgnoreCase(homeCommunityId) ||
			WellKnownOID.VA_RADIOLOGY_IMAGE.isApplicable(homeCommunityId);
	}
	
	// ==================================================================================
	// Instance Members
	// ==================================================================================
	protected String originatingSiteId;
	protected String studyId;
	protected String patientId;
	
	// =======================================================================================
	// Constructors
	// =======================================================================================
	/**
	 * Pass-through for derived classes with different namespace.
	 * 
	 * @param namespaceIdentifier
	 * @throws URNFormatException
	 */
	protected StudyURN(NamespaceIdentifier nid)
	throws URNFormatException
	{
		super(nid);
	}
	
	/**
	 * Provided only as a pass through for derived classes.
	 * 
	 */
	protected StudyURN(
		NamespaceIdentifier namespaceIdentifier, 
		String namespaceSpecificString, 
		String... additionalIdentifiers) 
	throws URNFormatException
	{
		super(namespaceIdentifier, namespaceSpecificString, additionalIdentifiers);
	}
	
	/**
	 * Used directly and a pass through for derived classes.
	 * The constructor called by the URN class when a URN derived class
	 * is being created from a String representation.
	 * 
	 * @param components
	 * @throws URNFormatException
	 */
	protected StudyURN(URNComponents urnComponents, SERIALIZATION_FORMAT serializationFormat) 
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
	private StudyURN(
		String originatingSiteId, 
		String studyId, 
		String patientId)
	throws URNFormatException
	{
		this(StudyURN.getManagedNamespace(), originatingSiteId, studyId, patientId);
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
	protected StudyURN(
		NamespaceIdentifier namespaceIdentifier,
		String originatingSiteId, 
		String studyId, 
		String patientId)
	throws URNFormatException
	{
		super(namespaceIdentifier);
		
		setOriginatingSiteId(originatingSiteId);
		//this.originatingSiteId = originatingSiteId;
		
		setStudyId(studyId);
		//this.studyId = studyId;
		
		setPatientId(patientId);
		//this.patientId = patientId;
	}

	/**
	 * 
	 * @param repositoryUniqueId
	 * @param documentId
	 * @throws URNFormatException
	 */
	private StudyURN(String repositoryUniqueId, String documentId) 
	throws URNFormatException
	{
		this(StudyURN.getManagedNamespace(), repositoryUniqueId, documentId);
	}
	
	/**
	 * Constructor from the GAI (IHE) identifiers
	 * 
	 * @param repositoryUniqueId
	 * @param documentId
	 * @throws URNFormatException 
	 */
	protected StudyURN(NamespaceIdentifier namespaceIdentifier, String repositoryUniqueId, String documentId) 
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

	/**
	 * Override the do-nothing version of this method to parse the
	 * namespace specific portion and find the component parts.
	 * 
	 * @param namespaceSpecificString
	 * @throws ImageURNFormatException 
	 */
	@Override
	public void parseNamespaceSpecificString(NamespaceIdentifier namespace, String namespaceSpecificString, SERIALIZATION_FORMAT serializationFormat)
	throws URNFormatException
	{
		if(namespaceSpecificString == null)
			throw new URNFormatException("The namespace specific string for a(n) " + this.getClass().getSimpleName() + " cannot be null.");
		
		Matcher nssMatcher = namespaceSpecificStringPattern.matcher(namespaceSpecificString);
		
		if(! nssMatcher.matches())
		{
			String msg = "Namespace specific string '" + namespaceSpecificString + "' is not valid.";
			Logger.getAnonymousLogger().warning(msg);
			throw new ImageURNFormatException(msg);
		}
	
		setOriginatingSiteId( nssMatcher.group(SITE_ID_GROUP).trim() );
		setPatientId( nssMatcher.group(StudyURN.PATIENT_ID_GROUP).trim() );
		String tmpStudyId = nssMatcher.group(StudyURN.GROUP_ID_GROUP).trim();
		switch(serializationFormat)
		{
		case PATCH83_VFTP:
			setStudyId( Base32ConversionUtility.base32Decode(tmpStudyId) );
			break;
		case RFC2141:
		case VFTP:
		case NATIVE:
		case RAW:
			setStudyId(tmpStudyId);
			break;
		case CDTP:
			this.studyId = URN.FILENAME_TO_RFC2141_ESCAPING.escapeIllegalCharacters(tmpStudyId);
			break;
		}
	}
	
	@Override
	public String getHomeCommunityId()
	{
		// Images are always in the VA community
		return WellKnownOID.VA_RADIOLOGY_IMAGE.getCanonicalValue().toString();
	}

	@Override
	public String getOriginatingSiteId()
	{
		return URN.RFC2141_ESCAPING.unescapeIllegalCharacters(this.originatingSiteId);
	}
	public void setOriginatingSiteId(String originatingSiteId) 
	throws StudyURNFormatException
	{
		this.originatingSiteId = URN.RFC2141_ESCAPING.escapeIllegalCharacters(originatingSiteId);
		if( !AbstractImagingURN.siteIdPattern.matcher(this.originatingSiteId).matches() )
			throw new StudyURNFormatException("The site ID '" + this.originatingSiteId + "' does not match the pattern '" + AbstractImagingURN.siteIdPattern.pattern());
	}

	public String getGroupId()
	{
		return URN.RFC2141_ESCAPING.unescapeIllegalCharacters(this.studyId);
	}
	public String getStudyId(){return getGroupId();}
	
	public void setGroupId(String studyId) 
	throws StudyURNFormatException
	{
		this.studyId = URN.RFC2141_ESCAPING.escapeIllegalCharacters(studyId);
		if( !AbstractImagingURN.groupIdPattern.matcher(this.studyId).matches() )
			throw new StudyURNFormatException("The study ID '" + this.studyId + "' does not match the pattern '" + AbstractImagingURN.groupIdPattern.pattern());
	}
	public void setStudyId(String studyId) 
	throws StudyURNFormatException
	{setGroupId(studyId);}

	@Override
	public String getPatientId()
	{
		return URN.RFC2141_ESCAPING.unescapeIllegalCharacters(this.patientId);
	}
	
	public void setPatientId(String patientId) 
	throws StudyURNFormatException
	{
		this.patientId = URN.RFC2141_ESCAPING.escapeIllegalCharacters(patientId);
		if( !AbstractImagingURN.patientIdPattern.matcher(this.patientId).matches() )
			throw new StudyURNFormatException("The patient ID '" + this.patientId + "' does not match the pattern '" + AbstractImagingURN.patientIdPattern.pattern());
	}
	
	// ===========================================================================
	// Serialization formats
	// ===========================================================================
	/**
	 * Stringified into the Patch83 VFTP format,
	 * i.e. urn:vaimage:200-<base32 encoded instance ID>-<base32 encoded group ID>-<patient ICN>-<modality>
	 * The instance ID and group ID are base 32 encoded,
	 * the patient ICN and modality, if it exists, are not BASE32 encoded 
	 * the namespace is "vaimage" and 
	 * the site ID is the originating site ID.
	 * 
	 * @see gov.va.med.URN#toStringPatch83VFTP()
	 */
	@Override
	protected String toStringPatch83VFTP()
	{
		StringBuilder ahnold = new StringBuilder();
		
		// build the scheme identifier
		ahnold.append(urnSchemaIdentifier);
		ahnold.append(urnComponentDelimiter);

		// build the namespace identifier
		ahnold.append(this.getNamespaceIdentifier().getNamespace());
		ahnold.append(urnComponentDelimiter);
		ahnold.append(getOriginatingSiteId());
		ahnold.append(URN.namespaceSpecificStringDelimiter);
		ahnold.append( SERIALIZATION_FORMAT.PATCH83_VFTP.serialize(this.getGroupId()) );
		if(getPatientId() != null)
		{
			ahnold.append(URN.namespaceSpecificStringDelimiter);
			ahnold.append( this.getPatientId() );
		}
		
		return ahnold.toString();
	}	
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
		
		ahnold.append(this.getNamespaceSpecificString());
		
		return ahnold.toString();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.URN#toStringAsNative()
	 */
	@Override
	protected String toStringNative()
	{
		StringBuilder ahnold = new StringBuilder();
		
		// build the scheme identifier
		ahnold.append(urnSchemaIdentifier);
		ahnold.append(urnComponentDelimiter);

		// build the namespace identifier
		ahnold.append(this.getNamespaceIdentifier());
		ahnold.append(urnComponentDelimiter);
		
		// restore any RFC2141 illegal characters
		ahnold.append( RFC2141_ESCAPING.unescapeIllegalCharacters(this.getNamespaceSpecificString()) );
		
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
		
		String nss = this.getNamespaceSpecificString();
		// escape any filename illegal characters
		nss = FILENAME_ESCAPING.escapeIllegalCharacters(nss);
		ahnold.append(nss);
		
		String additionalIdentifiers = this.getAdditionalIdentifiersString();
		// escape any filename illegal characters
		additionalIdentifiers = FILENAME_ESCAPING.escapeIllegalCharacters(additionalIdentifiers);
		ahnold.append(additionalIdentifiers);
		
		return ahnold.toString();	
	}

	/**
	 * 
	 */
	@Override
	public String getNamespaceSpecificString(SERIALIZATION_FORMAT serializationFormat)
	{
		StringBuilder ahnold = new StringBuilder();
		
		// build the namespace specific string
		ahnold.append(this.originatingSiteId);
		ahnold.append(URN.namespaceSpecificStringDelimiter);
		ahnold.append(this.studyId);
		ahnold.append(URN.namespaceSpecificStringDelimiter);
		ahnold.append(this.patientId);
		
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
	
	// formatting of the document unique ID, used for GlobalArtifactIdentifier support
	private static final String documentUniqueIdRegex =
		"(" + GROUPID_REGEX + ")" +
		URN.namespaceSpecificStringDelimiter + 
		"(" + PATIENTID_REGEX + ")";
	private static final Pattern documentUniqueIdPattern = Pattern.compile(documentUniqueIdRegex);
	private static final int DOCUMENTUNIQUEID_STUDY_ID_GROUP = 1;
	private static final int DOCUMENTUNIQUEID_PATIENT_ID_GROUP = 3;
	
	@Override
	public String getDocumentUniqueId()
	{
		return formatDocumentUniqueId( getStudyId(), getPatientId() );
	}

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
		if(patientId != null)
		{
			sb.append(URN.namespaceSpecificStringDelimiter);
			sb.append(patientId);
		}		
		return sb.toString();
	}
	
	protected void parseDocumentUniqueId(String documentId)
	throws GlobalArtifactIdentifierFormatException
	{
		Matcher matcher = StudyURN.documentUniqueIdPattern.matcher(documentId);
		if(!matcher.matches())
			throw new GlobalArtifactIdentifierFormatException("'" + documentId + "' is not a valid document identifier for type '" + this.getClass().getSimpleName() + "'.");
		
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
		return StudyURN.namespaceIdentifier;
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
	public StudyURN clone() 
	throws CloneNotSupportedException
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
