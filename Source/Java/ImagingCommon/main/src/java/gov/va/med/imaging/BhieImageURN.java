package gov.va.med.imaging;

import gov.va.med.*;
import gov.va.med.imaging.exceptions.ImageURNFormatException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.utility.Base32ConversionUtility;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author VHAISWBECKEC
 *
 * Defines the syntax of a URN used to identify BHIE provided images.
 * An BHIE ImageURN is defined to be in the following format:
 * "urn" + ":" + "bhieimage" + ":" + <opaque namespace specific string>
 *   
 *   @see http://www.ietf.org/rfc/rfc2141.txt
 *   
 * NOTE: this class MUST implement GlobalArtifactIdentifier
 * NOTE: this class extends URN but it is not create-able using the URNFactory
 * like other URN derivations are because this class requires that the patient
 * identifier be carried as a discrete field.
 * NOTE: this class is only used for exchange specific transactions and should
 * not be used for any other purpose.
 * NOTE: this class implements GlobalArtifactIdentifier properly, however that
 * implementation is intended for routing purposes only.
 * 
 */
@URNType(namespace="bhieimage")
public class BhieImageURN 
extends ImageURN
implements Serializable, GlobalArtifactIdentifier
{
	private static final long serialVersionUID = 1L;
	private static final String namespace = "bhieimage";
	public static final String DEFAULT_REPOSITORY_ID = "200";
	public static final WellKnownOID DEFAULT_HOME_COMMUNITY_ID = WellKnownOID.BHIE_RADIOLOGY;
	
	public static final int ADDITIONAL_IDENTIFIER_PATIENT_INDEX = 0;
	public static final int ADDITIONAL_IDENTIFIER_STUDY_INDEX = 1;
	public static final int ADDITIONAL_IDENTIFIER_MODALITY_INDEX = 2;
	public static final int ADDITIONAL_IDENTIFIER_PATIENT_IDENTIFIER_TYPE_INDEX = 3;
	
	private static NamespaceIdentifier namespaceIdentifier = null;
	public static synchronized NamespaceIdentifier getManagedNamespace()
	{
		if(namespaceIdentifier == null)
			namespaceIdentifier = new NamespaceIdentifier(namespace);
		return namespaceIdentifier;
	}
	
	// haims1_haims1_cb4d8fc601184948b06bd67331a19a5c@d024cb23-95e1-40ec-b495-a75c2aee54ff:1
	
	protected final static String docIdRegex = "[^\\[\\]]+";
	protected final static String groupIdRegex = "[A-Za-z0-9\\._-]*";		// either study or document set
	protected final static String patientIdRegex = "[A-Za-z0-9\\._-]*";
	
	// the namespace specific string is in the form:
	// dddddddddd{gggggggg}{pppppppp}
	// where:
	// dddddddddd is the document (image) identifier
	// gggggggggg is the group (study) identifier (optional)
	// pppppppppp is the patient identifier (optional)
	protected static final String nssNormalModeRegex = "(" + docIdRegex + ")";
	protected static final Pattern nssNormalModePattern = Pattern.compile(nssNormalModeRegex);
	private final static int NORMAL_MODE_DOCUMENT_ID_GROUP = 1;
	
	//  note that we only match to site 200
	protected static final String nssImageUrnModeRegex = 
		"(" + "200" + ")" + 
		"(?:" + URN.namespaceSpecificStringDelimiter + ")" +
		"(" + docIdRegex + ")";
	protected static final Pattern nssImageUrnModePattern = Pattern.compile(nssImageUrnModeRegex);
	//private final static int IMAGEURN_MODE_SITE_ID_GROUP = 1;
	private final static int IMAGEURN_MODE_DOCUMENT_ID_GROUP = 2;

	// 200-<base32 encoded instance ID>-<base32 encoded group ID>-<patient ICN>[-<modality>]
	public static final String PATCH83_VFTP_NSS_REGEX = 
		"(" + "200" + ")" + 
		"(?:" + URN.namespaceSpecificStringDelimiter + ")" +
		"(" + BASE32_COMPONENT_REGEX + ")" +				// instance ID
		"(?:" + URN.namespaceSpecificStringDelimiter + ")" +
		"(" + BASE32_COMPONENT_REGEX + ")" +				// group ID
		"(?:" + URN.namespaceSpecificStringDelimiter + ")" +
		"(" + PATIENTID_REGEX + ")" +						// patient ID
		"((?:" + URN.namespaceSpecificStringDelimiter + ")" +
		"(" + MODALITY_REGEX + "))?";						// modality
	public static final Pattern PATCH83_VFTP_NSS_PATTERN = Pattern.compile(PATCH83_VFTP_NSS_REGEX); 
	public static final int  PATCH83_VFTP_NSS_REGEX_INSTANCE_INDEX = 2;
	public static final int  PATCH83_VFTP_NSS_REGEX_GROUP_INDEX = 3;
	public static final int  PATCH83_VFTP_NSS_REGEX_PATIENT_INDEX = 4;
	public static final int  PATCH83_VFTP_NSS_REGEX_MODALITY_INDEX = 7;
	
	static
	{
		System.out.println("BHIEImageURN normal mode regular expression is '" + nssNormalModeRegex + "'.");
		System.out.println("BHIEImageURN ImageURN mode regular expression is '" + nssImageUrnModeRegex + "'.");
	}
	
	// ===========================================================================
	// Factory Methods
	// ===========================================================================
	/**
	 * This constructor is special in that it tries to parse the opaqueIdentifier
	 * into constituent parts.
	 * 
	 * @param opaqueIdentifier
	 * @return
	 * @throws URNFormatException
	 */
	public static BhieImageURN create(String opaqueIdentifier) 
	throws URNFormatException
	{
		return create(opaqueIdentifier, null, null);
	}

	/**
	 * 
	 * @param originatingSiteId
	 * @param assignedId
	 * @param studyId
	 * @param patientIcn
	 * @param imageModality
	 * @return
	 * @throws URNFormatException
	 */
	public static BhieImageURN create(
		String originatingSiteId, 
		String opaqueIdentifier, 
		String studyId,
		String patientIdentifier, 
		String imageModality) 
	throws URNFormatException
	{
		if(opaqueIdentifier == null)
			throw new URNFormatException("The opaque identifier is null and must not be.");
		
		return new BhieImageURN(opaqueIdentifier, patientIdentifier, studyId, imageModality);		
	}
	
	/**
	 * 
	 * @param opaqueIdentifier
	 * @param patientIdentifier
	 * @param studyId
	 * @return
	 * @throws URNFormatException
	 */
	public static BhieImageURN create(
		String opaqueIdentifier, 
		String studyId, 
		String patientIdentifier) 
	throws URNFormatException
	{
		if(opaqueIdentifier == null)
			throw new URNFormatException("The opaque identifier is null and must not be.");
		
		return new BhieImageURN(opaqueIdentifier, patientIdentifier, studyId, null);		
	}

	/**
	 * 
	 * @param urnComponents
	 * @return
	 * @throws URNFormatException
	 */
	public static BhieImageURN create(URNComponents urnComponents, SERIALIZATION_FORMAT serializationFormat) 
	throws URNFormatException 
	{
		return new BhieImageURN(urnComponents, serializationFormat);
	}
	
	/**
	 * 
	 */
	public static BhieImageURN createFromGlobalArtifactIdentifiers(
		String homeCommunityId, 
		String repositoryId, 
		String documentId) 
	throws URNFormatException
	{
		return createFromGlobalArtifactIdentifiers(homeCommunityId, repositoryId, documentId, (String [])null);
	}
	
	/**
	 * 
	 * @param homeCommunityId
	 * @param repositoryId
	 * @param documentId
	 * @return
	 * @throws URNFormatException
	 */
	public static BhieImageURN createFromGlobalArtifactIdentifiers(
		String homeCommunityId, 
		String repositoryId, 
		String documentId,
		String... additionalIdentifiers)
	throws URNFormatException
	{
		if(homeCommunityId == null || documentId == null)
			throw new URNFormatException("Either home community ID or document ID are null and must not be.");
		
		return BhieImageURN.create(documentId);
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
			BhieImageURN.namespace.equalsIgnoreCase(homeCommunityId) ||
			WellKnownOID.BHIE_RADIOLOGY.isApplicable(homeCommunityId) &&
			DEFAULT_REPOSITORY_ID.equals(repositoryId);
	}
	
	// ===========================================================================
	// Constructors should be private
	// ===========================================================================

	// NOTES:
	// The opaque identifier of this class is stored as the namespace specific string.
	// The additional identifiers (study and patient) are stored as additional identifiers.
	// The namespace specific string and the additional identifiers are both member instances
	// of the URN class.
	
	/**
	 * The BHIE opaque identifier is the identifier from the BHIE/BIA, 
	 * and must be sent back to the BHIE/BIA exactly when an image is requested.
	 * @param opaqueIdentifier
	 * @throws URNFormatException
	 */
	private BhieImageURN(String opaqueIdentifier) 
	throws URNFormatException 
	{
		super(BhieImageURN.getManagedNamespace());
		setInstanceId(opaqueIdentifier);
	}
	
	/**
	 * 
	 * @param documentIdentifier
	 * @param patientIdentifier
	 * @param studyId
	 * @throws URNFormatException
	 */
	private BhieImageURN(String documentIdentifier, String patientIdentifier, String studyId, String modality) 
	throws URNFormatException 
	{
		super(BhieImageURN.getManagedNamespace());
		setInstanceId(documentIdentifier);
		setPatientId(patientIdentifier);
		setStudyId(studyId);
		setImageModality(modality);
		//setAdditionalIdentifier(ADDITIONAL_IDENTIFIER_PATIENT_INDEX, patientIdentifier);
		//setAdditionalIdentifier(ADDITIONAL_IDENTIFIER_STUDY_INDEX, studyId);
		//setAdditionalIdentifier(ADDITIONAL_IDENTIFIER_MODALITY_INDEX, modality);
	}
	
	/**
	 * 
	 * @param urnComponents
	 * @throws URNFormatException
	 */
	private BhieImageURN(URNComponents urnComponents, SERIALIZATION_FORMAT serializationFormat) 
	throws URNFormatException 
	{
		super( urnComponents, serializationFormat );
	}

	
	/**
	 * @see gov.va.med.URN#deserializeAdditionalIdentifiers(java.lang.String[], gov.va.med.URN.SERIALIZATION_FORMAT)
	 */
	@Override
	protected String[] deserializeAdditionalIdentifiers(
		String[] additionalIdentifers,
		SERIALIZATION_FORMAT serializationFormat)
	{
		if( additionalIdentifers != null && additionalIdentifers.length > BhieImageURN.ADDITIONAL_IDENTIFIER_PATIENT_INDEX)
			setPatientId( serializationFormat.deserialize(additionalIdentifers[BhieImageURN.ADDITIONAL_IDENTIFIER_PATIENT_INDEX]) );
		
		if( additionalIdentifers != null && additionalIdentifers.length > BhieImageURN.ADDITIONAL_IDENTIFIER_STUDY_INDEX)
			setStudyId( serializationFormat.deserialize(additionalIdentifers[BhieImageURN.ADDITIONAL_IDENTIFIER_STUDY_INDEX]) );
		
		if( additionalIdentifers != null && additionalIdentifers.length > BhieImageURN.ADDITIONAL_IDENTIFIER_MODALITY_INDEX)
			setImageModality( serializationFormat.deserialize( additionalIdentifers[BhieImageURN.ADDITIONAL_IDENTIFIER_MODALITY_INDEX]) );
		
		
		return additionalIdentifers;
	}

	/**
	 * Override the do-nothing version of this method to parse the
	 * namespace specific portion and find the component parts.
	 * 
	 * The namespace specific string for this URN type has two possible forms;
	 * the normal form follows the 
	 * 
	 * @param namespaceSpecificString
	 * @throws ImageURNFormatException 
	 */
	@Override
	public void parseNamespaceSpecificString(
		NamespaceIdentifier namespace, 
		String namespaceSpecificString, 
		SERIALIZATION_FORMAT serializationFormat)
	throws URNFormatException
	{
		if(namespaceSpecificString == null)
			throw new URNFormatException(this.getClass().getSimpleName() + "does not allow a null namespace specific string.");
		else
		{
			switch(serializationFormat)
			{
			case PATCH83_VFTP:
				parseNamespaceSpecificStringPatch83VFTP(namespaceSpecificString);
				break;
			case RFC2141:
				this.instanceId = parseInstanceIdFromNSS(namespace, namespaceSpecificString);
				break;
			case CDTP:
				String tmpInstanceId = parseInstanceIdFromNSS(namespace, namespaceSpecificString);
				if(tmpInstanceId.endsWith("-"))
					tmpInstanceId = tmpInstanceId.substring(0, tmpInstanceId.length()-1);
				setInstanceId( URN.CDTP_ESCAPING.unescapeIllegalCharacters(tmpInstanceId) );
				break;
			case VFTP:
				this.instanceId = parseInstanceIdFromNSS(namespace, namespaceSpecificString);
				break;
				
			case RAW:
				setInstanceId( parseInstanceIdFromNSS(namespace, namespaceSpecificString) );
				break;
				
			case NATIVE:
				setInstanceId(parseInstanceIdFromNSS(namespace, namespaceSpecificString));
				break;
			}
		}
	}

	/**
	 * Try to match the NSS using either the VAIMAGE mode or the normal mode matcher
	 * and pull the instance ID from it. 
	 * 
	 * @param namespace
	 * @param namespaceSpecificString
	 * @return
	 * @throws URNFormatException
	 */
	private String parseInstanceIdFromNSS(NamespaceIdentifier namespace, String namespaceSpecificString)
	throws URNFormatException
	{
		Matcher normalModeMatcher = BhieImageURN.nssNormalModePattern.matcher(namespaceSpecificString);
		Matcher imageUrnModeMatcher = BhieImageURN.nssImageUrnModePattern.matcher(namespaceSpecificString);
		
		String tmpInstanceId = null;
		if( imageUrnModeMatcher.matches() && ImageURN.getManagedNamespace().equals(namespace))
			tmpInstanceId = imageUrnModeMatcher.group(IMAGEURN_MODE_DOCUMENT_ID_GROUP);
		else if( normalModeMatcher.matches() )
			tmpInstanceId = normalModeMatcher.group(NORMAL_MODE_DOCUMENT_ID_GROUP);
		else
			throw new URNFormatException("The namespace specific string '" + namespaceSpecificString + "' is not valid.");
		
		return tmpInstanceId;
	}
	
	/**
	 * Stringified into the Patch83 VFTP format,
	 * i.e. urn:vaimage:200-<base32 encoded instance ID>-<base32 encoded group ID>-<patient ICN>[-<modality>]
	 * @param namespace
	 * @param namespaceSpecificString
	 */
	private void parseNamespaceSpecificStringPatch83VFTP(String namespaceSpecificString)
	throws URNFormatException
	{
		try
		{
			Matcher patch83Matcher = PATCH83_VFTP_NSS_PATTERN.matcher(namespaceSpecificString);
			if(patch83Matcher.matches())
			{
				setInstanceId( Base32ConversionUtility.base32Decode(patch83Matcher.group(PATCH83_VFTP_NSS_REGEX_INSTANCE_INDEX)) );
				setGroupId( Base32ConversionUtility.base32Decode(patch83Matcher.group(PATCH83_VFTP_NSS_REGEX_GROUP_INDEX)) );
				setPatientId( patch83Matcher.group(PATCH83_VFTP_NSS_REGEX_PATIENT_INDEX) );
				setImageModality( patch83Matcher.group(PATCH83_VFTP_NSS_REGEX_MODALITY_INDEX) );
			}
			else
				throw new URNFormatException("Error parsing NSS, '" + namespaceSpecificString + "' is not a valid NSS.");
		}
		catch(IllegalStateException isX)
		{
			throw new URNFormatException("Unexpected exception parsing NSS '" + namespaceSpecificString + "', " + isX.getMessage(), isX);
		}
	}

	@Override
	public String getNamespaceSpecificString(SERIALIZATION_FORMAT serializationFormat)
	{
		return getImageId();
	}

	// ===========================================================================
	// Property Accessors
	// ===========================================================================
	@Override
	public String getHomeCommunityId()
	{
		// Images are always in the DoD community
		return WellKnownOID.BHIE_RADIOLOGY.getCanonicalValue().toString();
	}

	@Override
	public String getRepositoryUniqueId()
	{
		return getOriginatingSiteId();
	}
	
	@Override
	public NamespaceIdentifier getNamespaceIdentifier()
	{
		return BhieImageURN.getManagedNamespace();
	}
	
	@Override
	public String getOriginatingSiteId()
	{
		return BhieImageURN.DEFAULT_REPOSITORY_ID;
	}
	
	@Override
	public boolean isOriginVA()
	{
		return false;
	}
	
	@Override
	public boolean isOriginDOD()
	{
		return true;
	}
	
//	@Override
//	public String getPatientIcn()
//	{
//		return 
//		this.getAdditionalIdentifiers() != null && 
//		this.getAdditionalIdentifiers().length > BhieImageURN.ADDITIONAL_IDENTIFIER_PATIENT_INDEX ? 
//			this.getAdditionalIdentifiers()[BhieImageURN.ADDITIONAL_IDENTIFIER_PATIENT_INDEX] :
//			null;
//	}
//	
//	/**
//	 * @see gov.va.med.imaging.ImageURN#setPatientIcn(java.lang.String)
//	 */
//	@Override
//	public void setPatientIcn(String patientId)
//	{
//		this.setAdditionalIdentifier(BhieImageURN.ADDITIONAL_IDENTIFIER_PATIENT_INDEX, patientId);
//	}
//
//
//	@Override
//	public String getGroupId()
//	{
//		return 
//		this.getAdditionalIdentifiers() != null && 
//		this.getAdditionalIdentifiers().length > BhieImageURN.ADDITIONAL_IDENTIFIER_STUDY_INDEX ? 
//			this.getAdditionalIdentifiers()[BhieImageURN.ADDITIONAL_IDENTIFIER_STUDY_INDEX] :
//			null;
//	}
//
//	/**
//	 * @see gov.va.med.imaging.ImageURN#setGroupId(java.lang.String)
//	 */
//	@Override
//	public void setGroupId(String groupId)
//	{
//		this.setAdditionalIdentifier(BhieImageURN.ADDITIONAL_IDENTIFIER_STUDY_INDEX, groupId);
//	}

	// ===========================================================================
	// Serialization formats
	// ===========================================================================
	
	/**
	 * Serialize the URN in a format that contains all of the keys
	 * and is compatible with the VA clients (Clinical Display and VistaRad)
	 * This form should not be used outside the VA because it is not legal
	 * RFC-2141.
	 * 
	 * @return
	 */
	@Override
	public String toStringCDTP()
	{
		StringBuilder ahnold = new StringBuilder();
		
		// build the scheme identifier
		ahnold.append(urnSchemaIdentifier);
		ahnold.append(urnComponentDelimiter);
		// e.g. now "urn:"
		
		// build the namespace identifier
		ahnold.append(ImageURN.getManagedNamespace().toString());
		ahnold.append(urnComponentDelimiter);
		// e.g. now "urn:vaimage:"
		
		ahnold.append(DEFAULT_REPOSITORY_ID);
		// e.g. now "urn:vaimage:200"
		ahnold.append(URN.namespaceSpecificStringDelimiter);
		// e.g. now "urn:vaimage:200-"
		//String nss = getNamespaceSpecificString();
		//Base32ConversionUtility.base32Encode(nss);
		ahnold.append( CDTP_ESCAPING.escapeIllegalCharacters(getImageId()) );
		// e.g. now "urn:vaimage:200-655321abbf"
		
		// append a dash character to tell Clinical Display where to stop parsing
		// for its cache file name
		ahnold.append( '-' );
		// e.g. now "urn:vaimage:200-655321abbf-"
		
		// add the additional identifiers
		ahnold.append( URN.buildAdditionalIdentifiersString(SERIALIZATION_FORMAT.CDTP, getAdditionalIdentifiersToUse()) );
		
		return ahnold.toString();
	}
	
	/**
	 * Return the URN (or portion of the URN) that identifies the image to the
	 * originating source.  For VA images this is the same as the toString(), for 
	 * BHIE images this does not include the patient and the study parts of the
	 * URN.
	 * 
	 * @return
	 */
	@Override
	public String toStringNative()
	{
		StringBuilder ahnold = new StringBuilder();
		
		// build the scheme identifier
		ahnold.append(urnSchemaIdentifier);
		ahnold.append(urnComponentDelimiter);

		// build the namespace identifier
		ahnold.append(BhieImageURN.getManagedNamespace().toString());
		ahnold.append(urnComponentDelimiter);
		
		// CTB 07Jun2010 - added unescaping of octet sequences
		ahnold.append( getNamespaceSpecificString() );
		
		return ahnold.toString();
	}
	
	@Override
	protected String toStringPatch83VFTP()
	{
		StringBuilder ahnold = new StringBuilder();
		
		// build the scheme identifier
		ahnold.append(urnSchemaIdentifier);
		ahnold.append(urnComponentDelimiter);

		// build the namespace identifier
		ahnold.append(ImageURN.getManagedNamespace().toString());
		ahnold.append(urnComponentDelimiter);
		ahnold.append(getOriginatingSiteId());
		ahnold.append(URN.namespaceSpecificStringDelimiter);
		ahnold.append( SERIALIZATION_FORMAT.PATCH83_VFTP.serialize(this.getInstanceId()) );
		ahnold.append(URN.namespaceSpecificStringDelimiter);
		if(this.getGroupId() != null)
			ahnold.append( SERIALIZATION_FORMAT.PATCH83_VFTP.serialize(this.getGroupId()) );
		ahnold.append(URN.namespaceSpecificStringDelimiter);
		if(getPatientId() != null)
			ahnold.append( getPatientId() );
		if(getImageModality() != null && getImageModality().length() > 0)
		{
			ahnold.append(URN.namespaceSpecificStringDelimiter);
			ahnold.append( this.getImageModality() );
		}
		return ahnold.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	protected String toStringRaw()
	{
		StringBuilder ahnold = new StringBuilder();
		
		// build the scheme identifier
		ahnold.append(urnSchemaIdentifier);
		ahnold.append(urnComponentDelimiter);

		// build the namespace identifier
		ahnold.append(this.getNamespaceIdentifier());
		ahnold.append(urnComponentDelimiter);
		
		ahnold.append( this.getNamespaceSpecificString(SERIALIZATION_FORMAT.RAW) );
		ahnold.append( URN.buildAdditionalIdentifiersString(SERIALIZATION_FORMAT.RAW, getAdditionalIdentifiersToUse()) );
	
		return ahnold.toString();
	}
	
	/**
	 * Helper method that returns the additional identifiers to use (patient ID, study ID, modality and if not the default the patient identifier type)
	 * @return
	 */
	private String [] getAdditionalIdentifiersToUse()
	{
		String [] additionalIdentifiers = null;
		if(isDefaultPatientIdentifierType())
		{
			additionalIdentifiers = new String[] 
					{
						getPatientId(),
						getStudyId(),
						getImageModality()
					};	
		}
		else
		{
			additionalIdentifiers = new String[] 
					{
						getPatientId(),
						getStudyId(),
						getImageModality(),
						getPatientIdentifierType().name()
					};
		}
		return additionalIdentifiers;
	}
	
	// ===========================================================================
	// Clonable Realization
	// ===========================================================================
	@Override
	public BhieImageURN clone() 
	throws CloneNotSupportedException
	{
		BhieImageURN clone;
		try
		{
			clone = new BhieImageURN(getInstanceId(), getPatientId(), getStudyId(), getImageModality());
			return clone;
		}
		catch (URNFormatException x)
		{
			throw new CloneNotSupportedException("Failed to create clone from '" + this.toStringCDTP() + "'.");
		}
	}
	
	@Override
	public BhieStudyURN getParentStudyURN() 
	throws URNFormatException
	{
		return StudyURNFactory.create(getOriginatingSiteId(), getStudyId(), getPatientId(), BhieStudyURN.class);
	}
	
	// ===========================================================================
	// Generated equals() and hashCode()
	// ===========================================================================
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof BhieImageURN) ? equals((BhieImageURN)obj) : false;
	}
	
	public boolean equals(BhieImageURN that)
	{
		if( this.getInstanceId() == null || that.getInstanceId() == null )	// not legal according to the URN rules but ...
			return false;
		if( (this.getGroupId() == null) ^ (that.getGroupId() == null) )		// quick check for one null and one not
			return false;
		if( (this.getPatientId() == null) ^ (that.getPatientId() == null) )	// quick check for one null and one not
			return false;
		if( ! this.getInstanceId().equals(that.getInstanceId()) )
			return false;
		if( this.getGroupId() != null && ! this.getGroupId().equals(that.getGroupId()) )
			return false;
		if( this.getPatientId() != null && ! this.getPatientId().equals(that.getPatientId()) )
			return false;
		
		return true;
	}
	
	@Override
	protected int getPatientIdentifierTypeAdditionalIdentifierIndex()
	{
		return ADDITIONAL_IDENTIFIER_PATIENT_IDENTIFIER_TYPE_INDEX;
	}
}
