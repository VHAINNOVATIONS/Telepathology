package gov.va.med.imaging;

import gov.va.med.*;
import gov.va.med.imaging.exceptions.ImageURNFormatException;
import gov.va.med.imaging.exceptions.StudyURNFormatException;
import gov.va.med.imaging.exceptions.URNFormatException;
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
 * @see http://www.ietf.org/rfc/rfc2141.txt
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
@URNType(namespace="bhiestudy")
public class BhieStudyURN 
extends StudyURN
implements Serializable, PatientArtifactIdentifier
{
	private static final long serialVersionUID = 1L;
	private static final String namespace = "bhiestudy";
	public static final WellKnownOID DEFAULT_HOME_COMMUNITY_ID = WellKnownOID.BHIE_RADIOLOGY;
	public static final String DEFAULT_REPOSITORY_ID = "200";
	
	public static final int ADDITIONAL_IDENTIFIER_PATIENT_INDEX = 0;
	public static final int ADDITIONAL_IDENTIFIER_PATIENT_IDENTIFIER_TYPE_INDEX = 1;
	
	private static NamespaceIdentifier namespaceIdentifier = null;
	public static synchronized NamespaceIdentifier getManagedNamespace()
	{
		if(namespaceIdentifier == null)
			namespaceIdentifier = new NamespaceIdentifier(namespace);
		return namespaceIdentifier;
	}
	
	// haims1_haims1_cb4d8fc601184948b06bd67331a19a5c@d024cb23-95e1-40ec-b495-a75c2aee54ff:1
	
	protected final static String docIdRegex = "[^\\[\\]]+";
	protected final static String patientIdRegex = "[A-Za-z0-9\\._-]*";
	
	// the namespace specific string is in the form:
	// dddddddddd{pppppppp}
	// where:
	// dddddddddd is the group (study) identifier
	// pppppppppp is the patient identifier (optional)
	protected static final String nssNormalModeRegex = "(" + docIdRegex + ")";
	protected static final Pattern nssNormalModePattern = Pattern.compile(nssNormalModeRegex);
	private final static int NORMAL_MODE_STUDY_ID_GROUP = 1;
	
	protected static final String nssStudyUrnModeRegex = 
		"(" + "200" + ")" + 
		"(?:" + URN.namespaceSpecificStringDelimiterRegex + ")" +
		"(" + docIdRegex + ")";
	protected static final Pattern nssStudyUrnModePattern = Pattern.compile(nssStudyUrnModeRegex);
	//private final static int STUDYURN_MODE_SITE_ID_GROUP = 1;
	private final static int STUDYURN_MODE_STUDY_ID_GROUP = 2;
	
	static
	{
		System.out.println("BHIEStudyURN normal mode regular expression is '" + nssNormalModeRegex + "'.");
		System.out.println("BHIEStudyURN StudyURN mode regular expression is '" + nssStudyUrnModeRegex + "'.");
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
	public static BhieStudyURN create(String opaqueIdentifier) 
	throws URNFormatException
	{
		return create(opaqueIdentifier, null);
	}
	
	/**
	 * Required method for calls through StudyURN.
	 * 
	 * @param originatingSiteId
	 * @param opaqueIdentifier
	 * @param patientIdentifier
	 * @return
	 * @throws URNFormatException
	 */
	public static BhieStudyURN create(String originatingSiteId, String opaqueIdentifier, String patientIdentifier) 
	throws URNFormatException
	{
		if(! BhieStudyURN.DEFAULT_REPOSITORY_ID.equals(originatingSiteId))
			throw new URNFormatException("Unable to create a BhieSTudyURN when the originating site ID is not '" + BhieStudyURN.DEFAULT_REPOSITORY_ID + "'.");
		
		return new BhieStudyURN(opaqueIdentifier, patientIdentifier);
	}
	/**
	 * 
	 * @param studyId
	 * @param patientIcn
	 * @return
	 * @throws ImageURNFormatException
	 */
	public static BhieStudyURN create(String opaqueIdentifier, String patientIdentifier) 
	throws URNFormatException
	{
		if(opaqueIdentifier == null)
			throw new URNFormatException("Creating a BHIEImageURN, the opaque identifier is null and must not be.");
		
		return new BhieStudyURN(opaqueIdentifier, patientIdentifier);		
	}
	
	/**
	 * 
	 * @param urnComponents
	 * @return
	 * @throws URNFormatException
	 */
	public static BhieStudyURN createFromBase32(URNComponents urnComponents) 
	throws URNFormatException 
	{
		//urnComponents.decodeNamespaceSpecificStringAsBase32();
		return new BhieStudyURN(urnComponents, SERIALIZATION_FORMAT.PATCH83_VFTP);
	}
	
	/**
	 * @param urnComponents
	 * @return
	 * @throws URNFormatException
	 */
	public static BhieStudyURN create(URNComponents urnComponents, SERIALIZATION_FORMAT serializationFormat) 
	throws URNFormatException 
	{
		return new BhieStudyURN(urnComponents, serializationFormat);
	}
	
	/**
	 * 
	 */
	public static BhieStudyURN createFromGlobalArtifactIdentifiers(
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
	public static BhieStudyURN createFromGlobalArtifactIdentifiers(
		String homeCommunityId, 
		String repositoryId, 
		String documentId,
		String... additionalIdentifiers)
	throws URNFormatException
	{
		if(homeCommunityId == null || documentId == null)
			throw new URNFormatException("Either home community ID or document ID are null and must not be.");
		
		return BhieStudyURN.create(documentId);
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
			BhieStudyURN.namespace.equalsIgnoreCase(homeCommunityId) ||
			WellKnownOID.BHIE_RADIOLOGY.isApplicable(homeCommunityId) &&
			DEFAULT_REPOSITORY_ID.equals(repositoryId);
	}
	
	/**
	 * The function of this method is simply to assure that the ordering of the
	 * patient and study ID are consistent in the array of additional identifiers.
	 * 
	 * @param patientIdentifier
	 * @param studyId
	 * @return
	 */
	private static String[] createAdditionalIdentifiersArray(String patientIdentifier)
	{
		return new String[]{patientIdentifier};
	}
	// ===========================================================================
	// Constructors should be private
	// ===========================================================================
	/**
	 * The BHIE opaque identifier is the identifier from the BHIE/BIA, 
	 * and must be sent back to the BHIE/BIA exactly when an image is requested.
	 * @param opaqueIdentifier
	 * @throws URNFormatException
	 */
	private BhieStudyURN(String opaqueIdentifier) 
	throws URNFormatException 
	{
		super(BhieStudyURN.getManagedNamespace());
		this.studyId = opaqueIdentifier;
	}
	
	/**
	 * 
	 * @param originatingSiteId
	 * @param imageId
	 * @param studyId
	 * @param patientIcn
	 * @param imageModality
	 * @throws URNFormatException
	 */
	private BhieStudyURN(String studyIdentifier, String patientIdentifier) 
	throws URNFormatException 
	{
		super(BhieStudyURN.getManagedNamespace());
		this.studyId = studyIdentifier;
		this.setAdditionalIdentifiers( createAdditionalIdentifiersArray(patientIdentifier) );
	}
	
	/**
	 * 
	 * @param urnComponents
	 * @throws URNFormatException
	 */
	public BhieStudyURN(URNComponents urnComponents) 
	throws URNFormatException 
	{
		this(urnComponents, SERIALIZATION_FORMAT.NATIVE);
	}
	
	/**
	 * 
	 * @param urnComponents
	 * @throws URNFormatException
	 */
	private BhieStudyURN(URNComponents urnComponents, SERIALIZATION_FORMAT serializationFormat) 
	throws URNFormatException 
	{
		super( urnComponents, serializationFormat );
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
			throw new URNFormatException(this.getClass().getSimpleName() + "does not allow a null namespace specific string.");
		else
		{
			if(serializationFormat.isNSSAtomicallyEscaped())
			{
				namespaceSpecificString = serializationFormat.deserialize(namespaceSpecificString);
				// once the NSS is deserialized then the serialization format is RAW
				serializationFormat = SERIALIZATION_FORMAT.RAW;
			}
			
			Matcher nssNormalModeMatcher = nssNormalModePattern.matcher(namespaceSpecificString);
			Matcher nssStudyUrnModeMatcher = nssStudyUrnModePattern.matcher(namespaceSpecificString);
		
			String tmpIdentifiers = null;
			// if the URN was serialized using the ImageURN mode then this
			// matcher should parse it correctly (i.e. this is the opposite of toStringAsImageURN())
			if(StudyURN.getManagedNamespace().equals(namespace) && nssStudyUrnModeMatcher.matches())
				tmpIdentifiers = nssStudyUrnModeMatcher.group(STUDYURN_MODE_STUDY_ID_GROUP);
			else if(BhieStudyURN.getManagedNamespace().equals(namespace) && nssNormalModeMatcher.matches())
				tmpIdentifiers = nssNormalModeMatcher.group(NORMAL_MODE_STUDY_ID_GROUP);
			else
				throw new ImageURNFormatException("Namespace specific string '" + namespaceSpecificString + "' is not valid.");

			// NOTE: the serializationFormat may have been modified previously within this method
			// if the serializationFormat indicated atomic serialization of the NSS 
			switch(serializationFormat)
			{
			case PATCH83_VFTP:
				String[] components = tmpIdentifiers.split( String.valueOf(URN.namespaceSpecificStringDelimiter) );
				setStudyId( SERIALIZATION_FORMAT.PATCH83_VFTP.deserialize(components[1]) );
				if(components.length > 2)
					setPatientId( SERIALIZATION_FORMAT.PATCH83_VFTP.deserialize(components[2]) );
				break;
			case RFC2141:
			case RAW:
			case VFTP:
				this.studyId = tmpIdentifiers;
				break;
			case CDTP:
				tmpIdentifiers = SERIALIZATION_FORMAT.CDTP.deserialize(tmpIdentifiers);
				setStudyId(tmpIdentifiers);
				//this.studyId = URN.FILENAME_TO_RFC2141_ESCAPING.escapeIllegalCharacters(tmpIdentifiers);
				break;
			case NATIVE:
				setStudyId(tmpIdentifiers);
				break;
			}
		}
	}
	
	@Override
	public String getNamespaceSpecificString(SERIALIZATION_FORMAT encoding)
	{
		if(encoding == null)
			return getStudyId();
		
		// build the namespace specific string
		switch(encoding)
		{
		case CDTP:
		case NATIVE:
		case PATCH83_VFTP:
		case RAW:
		case RFC2141:
		case VFTP:
		default:
			return encoding.serialize(getStudyId());
		}
//		return 
//			encoding == SERIALIZATION_FORMAT.RFC2141 ? 
//				SERIALIZATION_FORMAT.RFC2141.serialize(getStudyId()) : 
//				encoding == SERIALIZATION_FORMAT.CDTP ? 
//					URN.FILENAME_ESCAPING.escapeIllegalCharacters(this.studyId) : 
//					getStudyId() 
//		;
	}

	// =====================================================================================================
	// Member Accessors
	// =====================================================================================================
	@Override
	public String getHomeCommunityId()
	{
		// Images are always in the DoD community
		return DEFAULT_HOME_COMMUNITY_ID.getCanonicalValue().toString();
	}

	@Override
	public String getRepositoryUniqueId()
	{
		// for a BHIE study, the site ID is always 200
		return DEFAULT_REPOSITORY_ID;
	}
	
	@Override
	public NamespaceIdentifier getNamespaceIdentifier()
	{
		return BhieStudyURN.getManagedNamespace();
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

	/**
	 * Override to ignore formatting check
	 */
	@Override
	public void setGroupId(String studyId) 
	throws StudyURNFormatException
	{
		this.studyId = URN.RFC2141_ESCAPING.escapeIllegalCharacters(studyId);
	}
	
	@Override
	public String getDocumentUniqueId(){return getGroupId();}

	@Override
	public String getGroupId()
	{
		return URN.RFC2141_ESCAPING.unescapeIllegalCharacters( this.studyId );
	}
	
	@Override
	public String getPatientId()
	{
		String[] additionalIdentifiers = this.getAdditionalIdentifiers();
		if(additionalIdentifiers != null && additionalIdentifiers.length > BhieStudyURN.ADDITIONAL_IDENTIFIER_PATIENT_INDEX) 
			return URN.RFC2141_ESCAPING.unescapeIllegalCharacters( additionalIdentifiers[BhieStudyURN.ADDITIONAL_IDENTIFIER_PATIENT_INDEX] );
		return null;
	}
	
	// ===========================================================================
	// Serialization formats
	// ===========================================================================
	
	@Override
	public void setPatientId(String patientId) 
	throws StudyURNFormatException
	{
		this.setAdditionalIdentifier(
				BhieStudyURN.ADDITIONAL_IDENTIFIER_PATIENT_INDEX, 
				URN.RFC2141_ESCAPING.escapeIllegalCharacters(patientId)
			);
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
	public String toStringCDTP()
	{
		StringBuilder ahnold = new StringBuilder();
		
		// build the scheme identifier
		ahnold.append(urnSchemaIdentifier);
		ahnold.append(urnComponentDelimiter);
		// e.g. now "urn:"
		
		// build the namespace identifier
		ahnold.append(StudyURN.getManagedNamespace().toString());
		ahnold.append(urnComponentDelimiter);
		// e.g. now "urn:vaimage:"
		
		ahnold.append(DEFAULT_REPOSITORY_ID);
		// e.g. now "urn:vaimage:200"
		ahnold.append(URN.namespaceSpecificStringDelimiter);
		// e.g. now "urn:vaimage:200-"
		//String nss = getNamespaceSpecificString();
		//Base32ConversionUtility.base32Encode(nss);
		ahnold.append( getNamespaceSpecificString(SERIALIZATION_FORMAT.CDTP) );
		// e.g. now "urn:vaimage:200-655321abbf"
		if(getPatientId() != null)
		{
			ahnold.append(URN.appendedIdentifierStartDelimiter);
			ahnold.append( getPatientId() );
			ahnold.append(URN.appendedIdentifierEndDelimiter);
			// e.g. now "urn:vaimage:200-655321abbf[76635V7656]"
			
			// only relevant if patient ID is not null and the patient identifier type is not the default
			if(!isDefaultPatientIdentifierType())
			{
				ahnold.append(URN.appendedIdentifierStartDelimiter);
				ahnold.append( getPatientIdentifierType() );
				ahnold.append(URN.appendedIdentifierEndDelimiter);
			}
		}
		
				
		return ahnold.toString();
	}
	
	/**
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
		ahnold.append(BhieStudyURN.getManagedNamespace().toString());
		ahnold.append(urnComponentDelimiter);
		
		// CTB 07Jun2010 - added unescaping of octet sequences
		ahnold.append( getNamespaceSpecificString(SERIALIZATION_FORMAT.NATIVE) );
		
		return ahnold.toString();
	}

	// ===========================================================================
	// Clonable Realization
	// ===========================================================================
	@Override
	public BhieStudyURN clone()
	throws CloneNotSupportedException
	{
		try
		{
			return new BhieStudyURN(getStudyId(), getPatientId());
		}
		catch (URNFormatException x)
		{
			throw new CloneNotSupportedException(x.getMessage());
		}
	}
	
	// ===========================================================================
	// Generated hashCode and equals
	// ===========================================================================
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof BhieStudyURN) ? equals((BhieStudyURN)obj) : false;
	}
	
	public boolean equals(BhieStudyURN that)
	{
		if( this.studyId == null || that.studyId == null )	// not legal according to the URN rules but ...
			return false;
		if( (this.patientId == null) ^ (that.patientId == null) )	// quick check for one null and one not
			return false;
		if( ! this.studyId.equals(that.studyId) )
			return false;
		
		return true;
	}

	@Override
	protected int getPatientIdentifierTypeAdditionalIdentifierIndex()
	{
		return ADDITIONAL_IDENTIFIER_PATIENT_IDENTIFIER_TYPE_INDEX;
	}

}
