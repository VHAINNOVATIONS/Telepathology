/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 20, 2008
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

import gov.va.med.*;
import gov.va.med.imaging.exceptions.URNFormatException;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Abstract Imaging URN that ImageURN and StudyURN extend. This is useful
 * for when the identifier from Display can be either a study URN or an 
 * image URN.
 * 
 * @author VHAISWWERFEJ
 *
 */
public abstract class AbstractImagingURN 
extends URN
implements Serializable, PatientArtifactIdentifier
{
	private static final long serialVersionUID = -3268947193110412282L;
	
	// /x5e is the carat '^' character
	// allow RFC2141 escaped octet sequences (e.g. %5e)
	//  	"([A-Za-z0-9\\x2e_]*|%[0-9a-fA-f]{2,2})*"
	// these patterns are valid once an identifier has been converted into
	// RFC2141 compliant form
	protected final static String SITEID_REGEX = "((?:[A-Za-z0-9\\x2e_]+)|(?:%[0-9a-fA-F]{2}))+";
	protected final static String GROUPID_REGEX = "((?:[A-Za-z0-9\\x2e_]+)|(?:%[0-9a-fA-F]{2}))+";		// either study or document set
	protected final static String PATIENTID_REGEX = "((?:[A-Za-z0-9\\x2e_]+)|(?:%[0-9a-fA-F]{2}))+";
	protected final static String IMAGEID_REGEX = "((?:[A-Za-z0-9\\x2e_]+)|(?:%[0-9a-fA-F]{2}))+";
	protected final static String MODALITY_REGEX = "((?:[A-Za-z0-9\\x2e_]+)|(?:%[0-9a-fA-F]{2}))+";
	
	public static Pattern siteIdPattern = Pattern.compile(SITEID_REGEX);
	public static Pattern groupIdPattern = Pattern.compile(GROUPID_REGEX);	// either study or document set
	public static Pattern studyUrnPatientIdPattern = Pattern.compile(PATIENTID_REGEX);
	public static Pattern patientIcnPattern = Pattern.compile(PATIENTID_REGEX);
	public static Pattern patientIdPattern = Pattern.compile(PATIENTID_REGEX);
	public static Pattern imageIdPattern = Pattern.compile(IMAGEID_REGEX);
	public static Pattern modalityPattern = Pattern.compile(MODALITY_REGEX);

	public static final String DOD_REPOSITORY_ID = "200"; 
	
	/**
	 * 
	 * @param namespaceIdentifier
	 * @throws URNFormatException
	 */
	protected AbstractImagingURN(NamespaceIdentifier namespaceIdentifier) 
	throws URNFormatException
	{
		super(namespaceIdentifier);
		assert namespaceIdentifier != null : "Namespace identifier cannot be a null value when building " + this.getClass().getSimpleName();
	}
	
	/**
	 * 
	 * @param namespaceIdentifier
	 * @param namespaceSpecificString
	 * @param additionalIdentifiers
	 * @throws URNFormatException
	 */
	protected AbstractImagingURN(
		NamespaceIdentifier namespaceIdentifier, 
		String namespaceSpecificString, 
		String... additionalIdentifiers) 
	throws URNFormatException
	{
		super(namespaceIdentifier, namespaceSpecificString, additionalIdentifiers);
	}
	
	/**
	 * 
	 * @param urnComponents
	 * @throws URNFormatException
	 */
	protected AbstractImagingURN(URNComponents urnComponents, SERIALIZATION_FORMAT serializationFormat) 
	throws URNFormatException
	{
		super(urnComponents, serializationFormat);
	}
	
	public abstract String getOriginatingSiteId();

	public boolean isOriginVA()
	{
		return ! DOD_REPOSITORY_ID.equals(getOriginatingSiteId());
	}
	
	public boolean isOriginDOD()
	{
		return DOD_REPOSITORY_ID.equals(getOriginatingSiteId());
	}
	
	@Override
	public String getPatientIdentifier(){return getPatientId();}
	
	public abstract String getPatientId();

	/**
	 * 
	 */
	@Override
	public abstract AbstractImagingURN clone()
	throws CloneNotSupportedException;
	
	/**
	 * 
	 * @return
	 */
	public abstract String getImagingIdentifier();
	
	/**
	 * 
	 */
	@Override
	public String toRoutingTokenString() 
	{
		return getHomeCommunityId() + "," + getRepositoryUniqueId();
	}
	
	/**
	 * Sets the PatientIdentifierType even if the new value is the same as the default value
	 * <br/>
	 * <b>WARNING</b>: Be sure this should be set, better to use setPatientIdentifierTypeIfNecessary which does not set
	 * anything if this value is the same as the default. In some cases setting this value, even when the same
	 * as  the default may cause unintended behavior (output including the patient identifier type when not desired).
	 *
	 * @param patientIdentifierType
	 */
	public void setPatientIdentifierType(PatientIdentifierType patientIdentifierType)
	{
		this.setAdditionalIdentifier(
				getPatientIdentifierTypeAdditionalIdentifierIndex(),
				patientIdentifierType.name()
			);
	}
	
	/**
	 * Only sets the PatientIdentifierType if it is different from the default PatientIdentifierType
	 * @param patientIdentifierType
	 */
	public void setPatientIdentifierTypeIfNecessary(PatientIdentifierType patientIdentifierType)
	{
		PatientIdentifierType defaultPatientIdentifierType = getDefaultPatientIdentifierType();
		if(defaultPatientIdentifierType != patientIdentifierType && patientIdentifierType != null)
			setPatientIdentifierType(patientIdentifierType);
	}
	
	/**
	 * Return the set PatientIdentifierType, if none is set this will return null
	 * @return
	 */
	public PatientIdentifierType getPatientIdentifierType()
	{
		String[] additionalIdentifiers = this.getAdditionalIdentifiers();
		if(additionalIdentifiers != null && additionalIdentifiers.length > getPatientIdentifierTypeAdditionalIdentifierIndex())
			return PatientIdentifierType.valueOf(additionalIdentifiers[getPatientIdentifierTypeAdditionalIdentifierIndex()]);
		return null;
	}
	
	/**
	 * Helper method to return the set PatientIdentifierType or the default PatientIdentifierType. This method should
	 * never return null
	 * @return
	 */
	public PatientIdentifierType getPatientIdentifierTypeOrDefault()
	{
		PatientIdentifierType pit = getPatientIdentifierType();
		if(pit == null)
			pit = getDefaultPatientIdentifierType();
		return pit;
	}
	
	/**
	 * Returns the default PatientIdentifierType for this type, this method can be overridden if necessary. This method
	 * should never return null
	 * @return
	 */
	public PatientIdentifierType getDefaultPatientIdentifierType()
	{
		return PatientIdentifierType.icn;
	}
	
	public boolean isDefaultPatientIdentifierType(PatientIdentifierType patientIdentifierType)
	{
		return (getDefaultPatientIdentifierType() == patientIdentifierType);
	}
	
	/**
	 * Returns true if the PatientIdentifierType assigned to this AbstractImagingURN is null or the same as the default PatientIdentifierType
	 * @return
	 */
	public boolean isDefaultPatientIdentifierType()
	{
		PatientIdentifierType pit = getPatientIdentifierType();
		if(pit == null || pit == getDefaultPatientIdentifierType())
			return true;
		return false;
	}
	
	/**
	 * The index location where the patient identifier type is stored in the additional identifier array. This must be implemented
	 * so the patient identifier type can be found
	 * @return
	 */
	protected abstract int getPatientIdentifierTypeAdditionalIdentifierIndex();
	
	/**
	 * Ugly name for method that returns a PatientIdentifier object based on the patient id and patient identifier type (or default)
	 * @return
	 */
	public PatientIdentifier getThePatientIdentifier()
	{
		return new PatientIdentifier(getPatientId(), getPatientIdentifierTypeOrDefault());
	}	
}
