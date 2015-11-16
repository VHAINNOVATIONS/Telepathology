package gov.va.med.imaging;

import gov.va.med.*;
import gov.va.med.exceptions.GlobalArtifactIdentifierFormatException;
import gov.va.med.imaging.exceptions.ImageURNFormatException;
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
 * Defines the syntax of a URN used to identify VA images to the outside world.
 * An ImageURN is defined to be in the following format:
 * "urn" + ":" + "vaimage" + ":" + <site-id> + "-" + <assigned-id> + "-" + <studyId> + "-" + <patientId> [+ "-" + <modality>]
 * where:
 *   <site-id> is either the site from which the image originated or the string "va-imaging"
 *   <assigned-id> is the permanent, immutable ID as assigned by the originating site or a 
 *   VA-domain unique ID (an Imaging GUID).
 *   <studyId> is the study IEN in VistA
 *   <imageIcn> is the unique VA identifier for the patient
 *   <modality> is the modality of the image
 *   
 * notes:
 *   Externally, the site-id should not be interpreted as a location component.  It should be 
 *   interpreted as no more than a specifier in a GUID (i.e. just a component that restricts 
 *   the required domain of uniqueness).  Initially and internally to the VA only, it may 
 *   be used as a location hint.  External code that relies on the site ID as a location
 *   may cease functioning without warning.
 *
 *   ImageURN follows the syntax of a URN as defined in W3C RFC2141 with the following additional
 *   rules on the namespace specific string:
 *   The character '-' is a reserved character, used to delimit portions of the namespace
 *   specific string.  Specifically it is used to delimit the originating site ID from the
 *   site-assigned image ID.
 *   The namespace specific string must consist of uppercase, lowercase and numeric characters
 *   with a '-' somewhere in the middle.
 *   
 *   @see http://www.ietf.org/rfc/rfc2141.txt
 *   
 * NOTE: both this class and DocumentURN MUST implement GlobalArtifactIdentifier
 * 
*/
@URNType(namespace="vaimage")
public class ImageURN 
extends AbstractImagingURN
implements Serializable, GlobalArtifactIdentifier
{
	private static final long serialVersionUID = 1L;
	public static final String NAMESPACE = "vaimage";
	public static final WellKnownOID DEFAULT_HOME_COMMUNITY_ID = WellKnownOID.VA_RADIOLOGY_IMAGE;
	
	private static NamespaceIdentifier namespaceIdentifier = null;
	public static synchronized NamespaceIdentifier getManagedNamespace()
	{
		if(namespaceIdentifier == null)
			namespaceIdentifier = new NamespaceIdentifier(NAMESPACE);
		return namespaceIdentifier;
	}
	
	// the additional plus '+' makes the component regular expressions into possessive quantifiers
	// and thereby improves performance (to the point of usability)
	// see http://www.regular-expressions.info/possessive.html for an explanation
	private static final String namespaceSpecificStringRegex = 
		"([^-]+)" + 								// the site ID
		URN.namespaceSpecificStringDelimiter +
		"([^-]+)" +									// the image ID 
		URN.namespaceSpecificStringDelimiter +
		"([^-]+)" +									// the group or study ID 
		URN.namespaceSpecificStringDelimiter + 
		"([^-]+)" +									// the patient ID 
		"(?:" + URN.namespaceSpecificStringDelimiter + ")?" + 
		"([^-]+)?";									// the optional modality
	private static final Pattern namespaceSpecificStringPattern = Pattern.compile(namespaceSpecificStringRegex);
	private static final int SITE_ID_GROUP = 1;
	private static final int INSTANCE_ID_GROUP = 2;
	private static final int GROUP_ID_GROUP = 3;
	private static final int PATIENT_ID_GROUP = 4;
	private static final int MODALITY_GROUP = 5;
	
	public static final int ADDITIONAL_IDENTIFIER_PATIENT_IDENTIFIER_TYPE_INDEX = 0;

	static
	{
		System.out.println("ImageURN NSS regular expression is '" + namespaceSpecificStringRegex + "'.");
	}
	
	/**
	 * 
	 * @param originatingSiteId - if the site ID is '200' then the returned instance will be
	 *                            a BhieImageURN
	 * @param assignedId
	 * @param studyId
	 * @param patientIcn
	 * @return
	 * @throws URNFormatException
	 */
	public static ImageURN create(
		String originatingSiteId, 
		String assignedId, 
		String studyId, 
		String patientIcn)
	throws URNFormatException
	{
		if(BhieImageURN.DEFAULT_REPOSITORY_ID.equals(originatingSiteId))
			return BhieImageURN.create(assignedId, studyId, patientIcn);
		else
			return create(originatingSiteId, assignedId, studyId, patientIcn, null);
	}

	/**
	 * 
	 * @param originatingSiteId - if the site ID is '200' then the returned instance will be
	 *                            a BhieImageURN
	 * @param assignedId
	 * @param studyId
	 * @param patientIcn
	 * @param imageModality
	 * @return
	 * @throws ImageURNFormatException
	 */
	public static ImageURN create(
		String originatingSiteId, 
		String assignedId, 
		String studyId,
		String patientIcn, 
		String imageModality) 
	throws URNFormatException
	{
		if(BhieImageURN.DEFAULT_REPOSITORY_ID.equals(originatingSiteId))
			return BhieImageURN.create(originatingSiteId, assignedId, studyId, patientIcn, imageModality);
		else
			return new ImageURN(originatingSiteId, assignedId, studyId, patientIcn, imageModality);		
	}
	

	/**
	 * Create a DocumentURN instance from an ImageURN.
	 * This provide the translation within the datasource, using the same
	 * RPCs as image retrieval and presenting results in Document semantics.
	 * @param documentUrn
	 * @return
	 * @throws URNFormatException
	 */
	public static ImageURN create(DocumentURN documentUrn)
	throws URNFormatException
	{
		return create(
			documentUrn.getOriginatingSiteId(),
			documentUrn.getDocumentId(),
			documentUrn.getDocumentSetId(), 
			documentUrn.getPatientId()
		);
	}
	
	/**
	 * 
	 * @param urnComponents
	 * @return
	 * @throws URNFormatException
	 */
	public static ImageURN create(URNComponents urnComponents, SERIALIZATION_FORMAT serializationFormat) 
	throws URNFormatException
	{
		ImageURN imageUrn = null;

		// if this probably a BHIE Image URN
		if( BhieImageURN.getManagedNamespace().equals(urnComponents.getNamespaceIdentifier()) ||
			(ImageURN.getManagedNamespace().equals(urnComponents.getNamespaceIdentifier()) &&
			 urnComponents.getNamespaceSpecificString().startsWith(BhieImageURN.DEFAULT_REPOSITORY_ID + URN.namespaceSpecificStringDelimiter))
		)
		{
			imageUrn =  BhieImageURN.create(urnComponents, serializationFormat);
		}
		else
		{
			imageUrn = new ImageURN(urnComponents, serializationFormat);
			
			if( !ImageURN.getManagedNamespace().equals(urnComponents.getNamespaceIdentifier()) )
				throw new URNFormatException("The namespace '" + urnComponents.getNamespaceIdentifier().toString() + "' is not valid for an ImageURN.");
			if( imageUrn.getGroupId() == null )
				throw new URNFormatException("The group ID is null, which is not valid for an ImageURN.");
			if( imageUrn.getInstanceId() == null )
				throw new URNFormatException("The group ID is null, which is not valid for an ImageURN.");
			
			// JMW 1/3/13 P130 - ImageURNs can now contain additional identifiers to hold the PatientIdentifierType			
			//if(urnComponents.getAdditionalIdentifers() != null && urnComponents.getAdditionalIdentifers().length > 0)
			//	throw new URNFormatException("The urn components include additional identifiers, which is not valid for an ImageURN.");
			
				
			// Most of the time a BhieImageURN will fail to parse correctly when treated as an ImageURN
			// and an exception will be thrown already.
			// On the off chance that does not happen then this will catch it
			if( BhieImageURN.DEFAULT_REPOSITORY_ID.equals(imageUrn.getOriginatingSiteId()) )
				imageUrn =  BhieImageURN.create(urnComponents, serializationFormat);
		}		
		return imageUrn;
	}
	
//	/**
//	 * 
//	 * @param urnComponents
//	 * @return
//	 * @throws URNFormatException
//	 */
//	public static ImageURN createFromBase32(URNComponents urnComponents) 
//	throws URNFormatException 
//	{
//		ImageURN imageUrn = null;
//		try
//		{
//			imageUrn = new ImageURN(urnComponents, SERIALIZATION_FORMAT.PATCH83_VFTP);
//			
//			if( !ImageURN.getManagedNamespace().equals(urnComponents.getNamespaceIdentifier()) )
//				throw new URNFormatException("The namespace '" + urnComponents.getNamespaceIdentifier().toString() + "' is not valid for an ImageURN.");
//			if( imageUrn.getGroupId() == null )
//				throw new URNFormatException("The group ID is null, which is not valid for an ImageURN.");
//			if( imageUrn.getInstanceId() == null )
//				throw new URNFormatException("The group ID is null, which is not valid for an ImageURN.");
//			if(urnComponents.getAdditionalIdentifers() != null && urnComponents.getAdditionalIdentifers().length > 0)
//				throw new URNFormatException("The urn components include additional identifiers, which is not valid for an ImageURN.");
//				
//			// Most of the time a BhieImageURN will fail to parse correctly when treated as an ImageURN
//			// and an exception will be thrown already.
//			// On the off chance that does not happen then this will catch it
//			if( BhieImageURN.DEFAULT_REPOSITORY_ID.equals(imageUrn.getOriginatingSiteId()) )
//				throw new URNFormatException("The originating Site indicates a DoD site, this error should be caught by a factory method and a BhieImageURN created instead.");
//		}
//		catch(URNFormatException urnfX)
//		{
//			// if the image URN originating Site ID is site 200 then this is really a BHIE URN,
//			// ClinicalDisplay does not recognize anything but VA URNs, this kludge insulates
//			// the rest of the system from that.
//			imageUrn =  BhieImageURN.createFromBase32(urnComponents);
//		}
//		
//		return imageUrn;
//	}
	

	/**
	 * 
	 */
	public static ImageURN createFromGlobalArtifactIdentifiers(
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
	public static ImageURN createFromGlobalArtifactIdentifiers(
		String homeCommunityId, 
		String repositoryId, 
		String documentId,
		String... additionalIdentifiers)
	throws URNFormatException
	{
		if( BhieImageURN.isApplicableHomeCommunityId(homeCommunityId, repositoryId, documentId) )
			return BhieImageURN.createFromGlobalArtifactIdentifiers(homeCommunityId, repositoryId, documentId);
		else if( ImageURN.isApplicableHomeCommunityId(homeCommunityId, repositoryId, documentId) )
			return new ImageURN( repositoryId, documentId );
		else
			throw new URNFormatException("Home community ID '" + homeCommunityId + "' cannot be used to create an ImageURN or its derivatives.");
	}

	/**
	 * Required static method, must return TRUE when this class can represent 
	 * a global artifact ID with the given home community ID.
	 * 
	 * @param homeCommunityId
	 * @param repositoryId 
	 * @param documentId 
	 * @return
	 */
	public static boolean isApplicableHomeCommunityId(String homeCommunityId, String repositoryId, String documentId)
	{
		return 
			ImageURN.NAMESPACE.equalsIgnoreCase(homeCommunityId) ||
			WellKnownOID.VA_RADIOLOGY_IMAGE.isApplicable(homeCommunityId);
	}
	
	
	public static Matcher getNamespaceSpecificStringMatcher(CharSequence input)
	{
		return ImageURN.namespaceSpecificStringPattern.matcher(input);
	}
	
	// =======================================================================================
	// Instance Members
	// =======================================================================================
	protected String originatingSiteId;
	protected String instanceId;
	protected String groupId;
	protected String patientId;
	protected String imageModality;
	
	// ===================================================================================================
	// Instance Constructors
	// ===================================================================================================
	
	/**
	 * Provided only as a pass through for derived classes.
	 * 
	 * @param nid
	 * @throws URNFormatException
	 */
	protected ImageURN(NamespaceIdentifier nid) 
	throws URNFormatException
	{
		super(nid);
	}
	
	/**
	 * Provided only as a pass through for derived classes.
	 * 
	 */
	protected ImageURN(
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
	protected ImageURN(URNComponents urnComponents, SERIALIZATION_FORMAT serialFormat) 
	throws URNFormatException
	{
		super(urnComponents, serialFormat);
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
	private ImageURN(
		String originatingSiteId, 
		String assignedId, 
		String studyId, 
		String patientId, 
		String imageModality) 
	throws URNFormatException 
	{
		super(ImageURN.getManagedNamespace());
		
		originatingSiteId = URN.RFC2141_ESCAPING.escapeIllegalCharacters(originatingSiteId);
		assignedId = URN.RFC2141_ESCAPING.escapeIllegalCharacters(assignedId);
		studyId = URN.RFC2141_ESCAPING.escapeIllegalCharacters(studyId);
		patientId = URN.RFC2141_ESCAPING.escapeIllegalCharacters(patientId);
		imageModality = URN.RFC2141_ESCAPING.escapeIllegalCharacters(imageModality);
		
		if( !AbstractImagingURN.siteIdPattern.matcher(originatingSiteId).matches() )
			throw new URNFormatException("The site ID '" + originatingSiteId + "' does not match the pattern '" + AbstractImagingURN.siteIdPattern.pattern());
		this.originatingSiteId = originatingSiteId;
		
		if( !AbstractImagingURN.imageIdPattern.matcher(assignedId).matches() )
			throw new URNFormatException("The image ID '" + assignedId + "' does not match the pattern '" + AbstractImagingURN.imageIdPattern.pattern());
		this.instanceId = assignedId;
		
		if( !AbstractImagingURN.groupIdPattern.matcher(studyId).matches() )
			throw new URNFormatException("The group ID '" + studyId + "' does not match the pattern '" + AbstractImagingURN.groupIdPattern.pattern());
		this.groupId = studyId;
		
		if( !AbstractImagingURN.patientIdPattern.matcher(patientId).matches() )
			throw new URNFormatException("The patient ID '" + patientId + "' does not match the pattern '" + AbstractImagingURN.patientIdPattern.pattern());
		this.patientId = patientId;
		
		if(imageModality != null && imageModality.length() <= 0)
			imageModality = null;
		if( imageModality != null && !AbstractImagingURN.modalityPattern.matcher(imageModality).matches() )
			throw new URNFormatException("The modality '" + imageModality + "' does not match the pattern '" + AbstractImagingURN.modalityPattern.pattern());
		
		this.imageModality = imageModality;
	}
	
	/**
	 * Constructor from the GAI (IHE) identifiers
	 * 
	 * @param repositoryUniqueId
	 * @param documentId
	 * @throws URNFormatException 
	 */
	private ImageURN(String repositoryUniqueId, String assignedId) 
	throws URNFormatException
	{
		super(ImageURN.getManagedNamespace());
		repositoryUniqueId = URN.RFC2141_ESCAPING.escapeIllegalCharacters(repositoryUniqueId);
		assignedId = URN.RFC2141_ESCAPING.escapeIllegalCharacters(assignedId);
		
		try
		{
			parseDocumentUniqueIdIntoFields(assignedId);
			this.originatingSiteId = repositoryUniqueId;
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
	
		this.originatingSiteId = nssMatcher.group(SITE_ID_GROUP).trim();
		String tmpInstanceId = nssMatcher.group(INSTANCE_ID_GROUP).trim();
		String tmpGroupId = nssMatcher.group(GROUP_ID_GROUP).trim();

		switch(serializationFormat)
		{
		case PATCH83_VFTP:
			setGroupId( Base32ConversionUtility.base32Decode(tmpGroupId) );
			setInstanceId( Base32ConversionUtility.base32Decode(tmpInstanceId) );
			break;
		case RFC2141:
			this.groupId = tmpGroupId;
			this.instanceId = tmpInstanceId;
			break;
		case VFTP:
			this.groupId = tmpGroupId;
			this.instanceId = tmpInstanceId;
			break;
		case RAW:
			this.groupId = tmpGroupId;
			this.instanceId = tmpInstanceId;
			break;
		case CDTP:
			this.groupId = URN.FILENAME_TO_RFC2141_ESCAPING.escapeIllegalCharacters(tmpGroupId);
			this.instanceId = URN.FILENAME_TO_RFC2141_ESCAPING.escapeIllegalCharacters(tmpInstanceId);
			break;
		case NATIVE:
			setGroupId(tmpGroupId);
			setInstanceId(tmpInstanceId);
			break;
		}

		this.patientId = URN.RFC2141_ESCAPING.escapeIllegalCharacters( nssMatcher.group(PATIENT_ID_GROUP).trim() );
		if(nssMatcher.group(MODALITY_GROUP) != null)
			this.imageModality = URN.RFC2141_ESCAPING.escapeIllegalCharacters( 
				nssMatcher.group(MODALITY_GROUP).trim().length() > 0 ? nssMatcher.group(MODALITY_GROUP).trim() : null );
		else
			this.imageModality = null;
	}
	
	@Override
	public String getHomeCommunityId()
	{
		// Images are always in the VA community
		return WellKnownOID.VA_RADIOLOGY_IMAGE.getCanonicalValue().toString();
	}

	@Override
	public NamespaceIdentifier getNamespaceIdentifier()
	{
		return ImageURN.getManagedNamespace();
	}
	
	@Override
	public String getOriginatingSiteId()
	{
		return this.originatingSiteId;
	}

	public String getImageId(){return this.getInstanceId();}
	public String getInstanceId()
	{
		return RFC2141_ESCAPING.unescapeIllegalCharacters(this.instanceId);
	}

	public void setInstanceId(String instanceId)
	{
		this.instanceId = RFC2141_ESCAPING.escapeIllegalCharacters(instanceId);
	}

	public String getStudyId(){return getGroupId();}
	/**
	 * The study ID may have escape sequences in it if they were included
	 * in the string from which the URN was constructed.
	 * Unescape the string to get the original value.
	 * 
	 * @return
	 */
	public String getGroupId()
	{
		return this.groupId == null ? null : URN.RFC2141_ESCAPING.unescapeIllegalCharacters(this.groupId);
	}
	public void setStudyId(String groupId){setGroupId(groupId);}
	public void setGroupId(String groupId)
	{
		this.groupId = RFC2141_ESCAPING.escapeIllegalCharacters(groupId);
	}

	@Override
	public String getPatientId()
	{
		return RFC2141_ESCAPING.unescapeIllegalCharacters(this.patientId);
	}
	
	public void setPatientId(String patientId)
	{
		this.patientId = RFC2141_ESCAPING.escapeIllegalCharacters(patientId);
	}

	public String getImageModality()
	{
		return this.imageModality == null ? null : RFC2141_ESCAPING.unescapeIllegalCharacters(this.imageModality);
	}
	public void setImageModality(String imageModality)
	{
		this.imageModality = RFC2141_ESCAPING.escapeIllegalCharacters(imageModality);
	}
	
	// ===========================================================================
	// Generated hashCode() and equals(), the modality should not be included
	// in the comparison.
	// Modified equals() to exclude super.equals()
	// ===========================================================================

	/**
	 * 
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.groupId == null) ? 0 : this.groupId.hashCode());
		result = prime * result + ((this.instanceId == null) ? 0 : this.instanceId.hashCode());
		result = prime * result + ((this.originatingSiteId == null) ? 0 : this.originatingSiteId.hashCode());
		result = prime * result + ((this.patientId == null) ? 0 : this.patientId.hashCode());
		return result;
	}

	/**
	 * 
	 * 	Modified equals() to exclude super.equals()
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		ImageURN other = (ImageURN) obj;
		if (this.groupId == null)
		{
			if (other.groupId != null)
				return false;
		}
		else if (!this.groupId.equals(other.groupId))
			return false;
		if (this.instanceId == null)
		{
			if (other.instanceId != null)
				return false;
		}
		else if (!this.instanceId.equals(other.instanceId))
			return false;
		if (this.originatingSiteId == null)
		{
			if (other.originatingSiteId != null)
				return false;
		}
		else if (!this.originatingSiteId.equals(other.originatingSiteId))
			return false;
		if (this.patientId == null)
		{
			if (other.patientId != null)
				return false;
		}
		else if (!this.patientId.equals(other.patientId))
			return false;
		return true;
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
		ahnold.append(ImageURN.getManagedNamespace().toString());
		ahnold.append(urnComponentDelimiter);
		ahnold.append(getOriginatingSiteId());
		ahnold.append(URN.namespaceSpecificStringDelimiter);
		ahnold.append( SERIALIZATION_FORMAT.PATCH83_VFTP.serialize(this.getInstanceId()) );
		ahnold.append(URN.namespaceSpecificStringDelimiter);
		ahnold.append( SERIALIZATION_FORMAT.PATCH83_VFTP.serialize(this.getGroupId()) );
		if(getPatientId() != null)
		{
			ahnold.append(URN.namespaceSpecificStringDelimiter);
			ahnold.append( getPatientId() );
		}
		if(getImageModality() != null && getImageModality().length() > 0)
		{
			ahnold.append(URN.namespaceSpecificStringDelimiter);
			ahnold.append( this.getImageModality() );
		}
		return ahnold.toString();
	}

	/**
	 * 
	 */
	@Override
	public String getNamespaceSpecificString(SERIALIZATION_FORMAT serializationFormat)
	{
		serializationFormat = serializationFormat == null ? SERIALIZATION_FORMAT.RFC2141 : serializationFormat;
		StringBuilder ahnold = new StringBuilder();
		
		// build the namespace specific string
		ahnold.append( this.getOriginatingSiteId() );
		ahnold.append(URN.namespaceSpecificStringDelimiter);
		ahnold.append( this.getInstanceId() );
		ahnold.append(URN.namespaceSpecificStringDelimiter);
		ahnold.append( serializationFormat.serialize(this.getGroupId()) );
		ahnold.append(URN.namespaceSpecificStringDelimiter);
		ahnold.append( this.getPatientId() );
		if(getImageModality() != null)		// if no modality, leave it off, no "null"
		{
			ahnold.append(URN.namespaceSpecificStringDelimiter);
			ahnold.append(getImageModality());
		}
		
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
		return buildDocumentUniqueId( getInstanceId(), getStudyId(), getPatientId() );
	}
	
	private void parseDocumentUniqueIdIntoFields(String documentId)
	throws GlobalArtifactIdentifierFormatException
	{
		String[] components = parseDocumentUniqueId(documentId);
		instanceId = components[0];
		groupId = components[1];
		patientId = components[2];
	}
	
	/**
	 * This method and parseUniqueId() are declared static to make testing easier.
	 * These two methods are reflexive, so parseUniqueID( buildDocumentUniqueId(A,B,C) ) = {A,B,C}
	 * @param instanceId
	 * @param studyId
	 * @param patientId
	 * @return
	 */
	public static String buildDocumentUniqueId(String instanceId, String studyId, String patientId)
	{
		return 
			instanceId +
			URN.namespaceSpecificStringDelimiter +
			studyId + 
			URN.namespaceSpecificStringDelimiter +
			patientId;
	}
	
	public static String[] parseDocumentUniqueId(String documentId)
	throws GlobalArtifactIdentifierFormatException
	{
		Matcher matcher = ImageURN.documentUniqueIdPattern.matcher(documentId);
		if(!matcher.matches())
			throw new GlobalArtifactIdentifierFormatException("'" + documentId + "' is not a valid document identifier for type 'ImageURN'.");
		
		return new String[]{
			matcher.group(DOCUMENTUNIQUEID_DOCUMENT_ID_GROUP),
			matcher.group(DOCUMENTUNIQUEID_GROUP_ID_GROUP), 
			matcher.group(DOCUMENTUNIQUEID_PATIENT_ID_GROUP)};
		
	}

	@Override
	public int compareTo(GlobalArtifactIdentifier o)
	{
		return GlobalArtifactIdentifierImpl.compareTo(this, o);
	}

	@Override
	public String getImagingIdentifier()
	{
		return getImageId();
	}

	@Override
	public String getRepositoryUniqueId()
	{
		return this.originatingSiteId;
	}
	
	@Override
	public ImageURN clone() 
	throws CloneNotSupportedException
	{
		try
		{
			return create(getOriginatingSiteId(), getImageId(), getStudyId(), getPatientId(), getImageModality());
		} 
		catch (URNFormatException e)
		{
			throw new CloneNotSupportedException(e.getMessage());
		}
	}
	
	public StudyURN getParentStudyURN() 
	throws URNFormatException
	{
		StudyURN studyUrn =
				StudyURNFactory.create(getOriginatingSiteId(), getStudyId(), getPatientId(), StudyURN.class);
		studyUrn.setPatientIdentifierTypeIfNecessary(getPatientIdentifierType());
		return studyUrn;
	}

	@Override
	protected int getPatientIdentifierTypeAdditionalIdentifierIndex()
	{
		return ADDITIONAL_IDENTIFIER_PATIENT_IDENTIFIER_TYPE_INDEX;
	}
}
