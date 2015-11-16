/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 17, 2011
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.GlobalArtifactIdentifierImpl;
import gov.va.med.NamespaceIdentifier;
import gov.va.med.PatientArtifactIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.SERIALIZATION_FORMAT;
import gov.va.med.URN;
import gov.va.med.URNComponents;
import gov.va.med.URNType;
import gov.va.med.WellKnownOID;
import gov.va.med.imaging.exceptions.ImageURNFormatException;
import gov.va.med.imaging.exceptions.URNFormatException;

import java.io.Serializable;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Annotation layer identifier. This identifier is globally unique.  It contains the site, patient, image 
 * and annotation layer ID.  The image ID specified is not a fully image URN and insufficient information
 * to create a full image URN but it is sufficient to identify the image at a site.
 * 
 * @author VHAISWWERFEJ
 *
 */
@URNType(namespace="vaannotation")
public class ImageAnnotationURN
extends URN
implements Serializable, PatientArtifactIdentifier
{
	private static final long serialVersionUID = 1L;
	
	protected String patientId;
	protected String annotationId;
	protected String imageId;
	protected String originatingSiteId;
	
	private static final String namespace = "vaannotation";
	public static final WellKnownOID DEFAULT_HOME_COMMUNITY_ID = WellKnownOID.VA_RADIOLOGY_IMAGE;
	
	private static final String namespaceSpecificStringRegex = 
		"([^-]+)" + 								// the site ID
		URN.namespaceSpecificStringDelimiter +
		"([^-]+)" +									// the annotation ID 
		URN.namespaceSpecificStringDelimiter +
		"([^-]+)" +									// the image ID
		URN.namespaceSpecificStringDelimiter + 
		"([^-]+)";									// the patient ID 
	private static final Pattern namespaceSpecificStringPattern = Pattern.compile(namespaceSpecificStringRegex);
	private static final int SITE_ID_GROUP = 1;
	private static final int ANNOTATION_ID_GROUP = 2;
	private static final int IMAGE_ID_GROUP = 3;
	private static final int PATIENT_ID_GROUP = 4;
	
	private static NamespaceIdentifier namespaceIdentifier = new NamespaceIdentifier(namespace);
	public static synchronized NamespaceIdentifier getManagedNamespace()
	{
		return namespaceIdentifier;
	}
	
	public static ImageAnnotationURN create(String originatingSiteId, 
			String annotationId, String imageId, String patientId)
	throws URNFormatException
	{	
		return new ImageAnnotationURN(ImageAnnotationURN.getManagedNamespace(),
				originatingSiteId, annotationId, imageId, patientId);
	}
	
	public static ImageAnnotationURN create(URNComponents urnComponents, 
			SERIALIZATION_FORMAT serializationFormat) 
	throws URNFormatException
	{
		return new ImageAnnotationURN(urnComponents, serializationFormat);
	}
	
	/**
	 * Used directly and a pass through for derived classes.
	 * The constructor called by the URN class when a URN derived class
	 * is being created from a String representation.
	 * 
	 * @param components
	 * @throws URNFormatException
	 */
	protected ImageAnnotationURN(URNComponents urnComponents, SERIALIZATION_FORMAT serializationFormat) 
	throws URNFormatException
	{
		super(urnComponents, serializationFormat);
	}
	
	protected ImageAnnotationURN(NamespaceIdentifier namespaceIdentifier,
			String originatingSiteId, 
			String annotationId, 
			String imageId,
			String patientId)
	throws URNFormatException
	{
		super(namespaceIdentifier);
		setOriginatingSiteId(originatingSiteId);
		setAnnotationId(annotationId);
		setImageId(imageId);
		setPatientId(patientId);
	}

	@Override
	public String getHomeCommunityId()
	{
		// Images are always in the VA community
		return WellKnownOID.VA_RADIOLOGY_IMAGE.getCanonicalValue().toString();
	}

	@Override
	public String getRepositoryUniqueId()
	{
		return originatingSiteId;
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
	public String toRoutingTokenString()
	{
		return getHomeCommunityId() + "," + getRepositoryUniqueId();
	}

	@Override
	public int compareTo(GlobalArtifactIdentifier o)
	{
		return GlobalArtifactIdentifierImpl.compareTo(this, o);
	}

	@Override
	public String getDocumentUniqueId()
	{
		return formatDocumentUniqueId( getAnnotationId(), getImageId(), getPatientId() );
	}

	@Override
	public boolean equalsGlobalArtifactIdentifier(GlobalArtifactIdentifier that)
	{
		return GlobalArtifactIdentifierImpl.equalsGlobalArtifactIdentifier(this, that);
	}

	@Override
	public String getPatientIdentifier()
	{
		return getPatientId();
	}

	@Override
	public ImageAnnotationURN clone() 
	throws CloneNotSupportedException
	{
		try
		{
			return create(getOriginatingSiteId(), getAnnotationId(), 
					getImageId(), getPatientId());
		} 
		catch (URNFormatException e)
		{
			throw new CloneNotSupportedException(e.getMessage());
		}
	}

	public String getPatientId()
	{
		return patientId;
	}

	public void setPatientId(String patientId)
	{
		this.patientId = patientId;
	}

	public String getAnnotationId()
	{
		return annotationId;
	}

	public void setAnnotationId(String annotationId)
	{
		this.annotationId = annotationId;
	}

	public String getOriginatingSiteId()
	{
		return originatingSiteId;
	}

	public void setOriginatingSiteId(String originatingSiteId)
	{
		this.originatingSiteId = originatingSiteId;
	}

	public String getImageId()
	{
		return imageId;
	}

	public void setImageId(String imageId)
	{
		this.imageId = imageId;
	}

	protected static String formatDocumentUniqueId(String annotationId, String imageId, String patientId)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(annotationId);
		sb.append(URN.namespaceSpecificStringDelimiter);
		sb.append(imageId);
		sb.append(URN.namespaceSpecificStringDelimiter);
		sb.append(patientId);
		
		return sb.toString();
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
		ahnold.append(this.annotationId);
		ahnold.append(URN.namespaceSpecificStringDelimiter);
		ahnold.append(this.imageId);
		ahnold.append(URN.namespaceSpecificStringDelimiter);
		ahnold.append(this.patientId);
		
		return ahnold.toString();
	}

	@Override
	public void parseNamespaceSpecificString(NamespaceIdentifier namespace,
			String namespaceSpecificString,
			SERIALIZATION_FORMAT serializationFormat) 
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
		setPatientId( nssMatcher.group(ImageAnnotationURN.PATIENT_ID_GROUP).trim() );
		String tmpAnnotationId = nssMatcher.group(ImageAnnotationURN.ANNOTATION_ID_GROUP).trim();
		String tmpImageId = nssMatcher.group(ImageAnnotationURN.IMAGE_ID_GROUP).trim();
		switch(serializationFormat)
		{
		case PATCH83_VFTP:
		case RFC2141:
		case VFTP:
		case NATIVE:
		case RAW:
			setAnnotationId(tmpAnnotationId);
			setImageId(tmpImageId);
			break;
		case CDTP:
			this.annotationId = URN.FILENAME_TO_RFC2141_ESCAPING.escapeIllegalCharacters(tmpAnnotationId);
			this.imageId = URN.FILENAME_TO_RFC2141_ESCAPING.escapeIllegalCharacters(tmpImageId);
			break;
		}
	}

}
