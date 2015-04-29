/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 12, 2012
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
package gov.va.med.imaging.pathology;

import java.io.Serializable;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.GlobalArtifactIdentifierImpl;
import gov.va.med.NamespaceIdentifier;
import gov.va.med.PatientArtifactIdentifier;
import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.SERIALIZATION_FORMAT;
import gov.va.med.URN;
import gov.va.med.URNComponents;
import gov.va.med.URNType;
import gov.va.med.WellKnownOID;
import gov.va.med.imaging.exceptions.URNFormatException;

/**
 * @author VHAISWWERFEJ
 *
 */
@URNType(namespace="vapathologycase")
public class PathologyCaseURN
extends URN
implements Serializable, PatientArtifactIdentifier
{

	private static final long serialVersionUID = 1L;
	
	protected String originatingSiteId;
	protected String pathologyType;
	protected String year;
	protected String number;
	protected PatientIdentifier patientId;
	
	
	private static final String namespace = "vapathologycase";
	public static final WellKnownOID DEFAULT_HOME_COMMUNITY_ID = WellKnownOID.VA_RADIOLOGY_IMAGE;
	
	private static final String namespaceSpecificStringRegex = 
		"([^-]+)" + 								// the site ID
		URN.namespaceSpecificStringDelimiter +
		"([^-]+)" +									// the pathology type 
		URN.namespaceSpecificStringDelimiter +
		"([^-]+)" +									// the year
		URN.namespaceSpecificStringDelimiter + 
		"([^-]+)" +									// the accession number
		URN.namespaceSpecificStringDelimiter + 
		"([^-]+)";									// the patient ID
	private static final Pattern namespaceSpecificStringPattern = Pattern.compile(namespaceSpecificStringRegex);
	private static final int SITE_ID_GROUP = 1;
	private static final int PATHOLOGY_TYPE_GROUP = 2;
	private static final int YEAR_GROUP = 3;
	private static final int NUMBER_GROUP = 4;
	private static final int PATIENT_ID_GROUP = 5;
	
	private static NamespaceIdentifier namespaceIdentifier = new NamespaceIdentifier(namespace);
	public static synchronized NamespaceIdentifier getManagedNamespace()
	{
		return namespaceIdentifier;
	}
	
	public static PathologyCaseURN create(String originatingSiteId, 
			String pathologyType, String year, String number, PatientIdentifier patientId)
	throws URNFormatException
	{	
		return new PathologyCaseURN(PathologyCaseURN.getManagedNamespace(),
				originatingSiteId, pathologyType, year, number, patientId);
	}
	
	public static PathologyCaseURN create(URNComponents urnComponents, 
			SERIALIZATION_FORMAT serializationFormat) 
	throws URNFormatException
	{
		return new PathologyCaseURN(urnComponents, serializationFormat);
	}
	
	/**
	 * Used directly and a pass through for derived classes.
	 * The constructor called by the URN class when a URN derived class
	 * is being created from a String representation.
	 * 
	 * @param components
	 * @throws URNFormatException
	 */
	protected PathologyCaseURN(URNComponents urnComponents, SERIALIZATION_FORMAT serializationFormat) 
	throws URNFormatException
	{
		super(urnComponents, serializationFormat);
	}
	
	protected PathologyCaseURN(NamespaceIdentifier namespaceIdentifier,
			String originatingSiteId, 
			String pathologyType, 
			String year, 
			String number, 
			PatientIdentifier patientIdentifier)
	throws URNFormatException
	{
		super(namespaceIdentifier);
		setOriginatingSiteId(originatingSiteId);
		setPathologyType(pathologyType);
		setYear(year);
		setNumber(number);			
		setPatientId(patientIdentifier);
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
		return formatDocumentUniqueId(getPathologyType(), getYear(), getNumber() );
	}

	@Override
	public boolean equalsGlobalArtifactIdentifier(GlobalArtifactIdentifier that)
	{
		return GlobalArtifactIdentifierImpl.equalsGlobalArtifactIdentifier(this, that);
	}

	@Override
	public String getPatientIdentifier()
	{
		return getPatientId().toString();
	}

	@Override
	public PathologyCaseURN clone() 
	throws CloneNotSupportedException
	{
		try
		{
			return create(getOriginatingSiteId(), getPathologyType(), getYear(), 
					getNumber(), getPatientId());
		} 
		catch (URNFormatException e)
		{
			throw new CloneNotSupportedException(e.getMessage());
		}
	}

	public PatientIdentifier getPatientId()
	{
		return patientId;
	}

	public void setPatientId(PatientIdentifier patientId)
	{
		this.patientId = patientId;
	}

	public String getPathologyType()
	{
		return pathologyType;
	}

	public void setPathologyType(String pathologyType)
	{
		this.pathologyType = pathologyType;
	}

	public String getYear()
	{
		return year;
	}

	public void setYear(String year)
	{
		this.year = year;
	}

	public String getNumber()
	{
		return number;
	}

	public void setNumber(String number)
	{
		this.number = number;
	}

	public String getOriginatingSiteId()
	{
		return originatingSiteId;
	}

	public void setOriginatingSiteId(String originatingSiteId)
	{
		this.originatingSiteId = originatingSiteId;
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
		ahnold.append(this.pathologyType);
		ahnold.append(URN.namespaceSpecificStringDelimiter);
		ahnold.append(this.year);
		ahnold.append(URN.namespaceSpecificStringDelimiter);
		ahnold.append(this.number);
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
			throw new URNFormatException(msg);
		}
	
		setOriginatingSiteId( nssMatcher.group(PathologyCaseURN.SITE_ID_GROUP).trim() );
		String tmpPatientId = nssMatcher.group(PathologyCaseURN.PATIENT_ID_GROUP).trim();
		setPatientId(PatientIdentifier.fromString(tmpPatientId));
		String tmpPathologyType = nssMatcher.group(PathologyCaseURN.PATHOLOGY_TYPE_GROUP).trim();
		String tmpYear = nssMatcher.group(PathologyCaseURN.YEAR_GROUP).trim();
		String tmpNumber= nssMatcher.group(PathologyCaseURN.NUMBER_GROUP).trim();
		switch(serializationFormat)
		{
		case PATCH83_VFTP:
		case RFC2141:
		case VFTP:
		case NATIVE:
		case RAW:
			setYear(tmpYear);
			setNumber(tmpNumber);
			setPathologyType(tmpPathologyType);
			break;
		case CDTP:
			this.year = URN.FILENAME_TO_RFC2141_ESCAPING.escapeIllegalCharacters(tmpYear);
			this.number = URN.FILENAME_TO_RFC2141_ESCAPING.escapeIllegalCharacters(tmpNumber);
			this.pathologyType = URN.FILENAME_TO_RFC2141_ESCAPING.escapeIllegalCharacters(tmpPathologyType);
			break;
		}
	}
	
	/**
	 * Returns the PathologyType Year Number which represents the accession number in VistA
	 * @return
	 */
	public String toStringAccessionNumber()
	{
		return getPathologyType() + " " + getYear() + " " + getNumber();
	}
}
