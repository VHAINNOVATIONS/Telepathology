/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Dec 7, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */

package gov.va.med;

import gov.va.med.imaging.exceptions.URNFormatException;

/**
 * @author vhaiswbeckec
 *
 */
@URNType(namespace="paid")
public class PatientArtifactIdentifierImpl
extends GlobalArtifactIdentifierImpl
implements PatientArtifactIdentifier
{
	private static final long serialVersionUID = 1L;
	
	private static final String namespace = "paid";
	private static NamespaceIdentifier namespaceIdentifier = null;
	public static synchronized NamespaceIdentifier getManagedNamespace()
	{
		if(namespaceIdentifier == null)
			namespaceIdentifier = new NamespaceIdentifier(namespace);
		return namespaceIdentifier;
	}
	private static final int PATIENT_ID_INDEX = 0;
	
	/**
	 * 
	 * @param homeCommunityId
	 * @param repositoryUniqueId
	 * @param documentUniqueId
	 * @return
	 * @throws URNFormatException
	 */
	public static PatientArtifactIdentifierImpl createFromGlobalArtifactIdentifiers(
		String homeCommunityId, 
		String repositoryUniqueId, 
		String documentUniqueId,
		String... additionalIdentifiers) 
	throws URNFormatException
	{
		return new PatientArtifactIdentifierImpl(homeCommunityId, repositoryUniqueId, documentUniqueId, additionalIdentifiers);
	}

	/**
	 * 
	 * @param gai
	 * @param additionalIdentifiers
	 * @return
	 * @throws URNFormatException
	 */
	public static PatientArtifactIdentifierImpl create(
		GlobalArtifactIdentifier gai, 
		String... additionalIdentifiers) 
	throws URNFormatException
	{
		return new PatientArtifactIdentifierImpl(gai.getHomeCommunityId(), gai.getRepositoryUniqueId(), gai.getDocumentUniqueId(), additionalIdentifiers);
	}
	
	/**
	 * 
	 * @param urnComponents
	 * @return
	 * @throws URNFormatException
	 */
	public static PatientArtifactIdentifierImpl create(URNComponents urnComponents, SERIALIZATION_FORMAT serializationFormat) 
	throws URNFormatException
	{
		if( ! getManagedNamespace().equals(urnComponents.getNamespaceIdentifier()) )
			throw new URNFormatException("namespace identifier '" + urnComponents.getNamespaceIdentifier() + "' does not match managed namespace '" + getManagedNamespace().getNamespace() + "'.");
		return new PatientArtifactIdentifierImpl(urnComponents, serializationFormat);
	}
	
	// =================================================================================================================
	//
	// =================================================================================================================
	
	/**
	 * @param homeCommunityId
	 * @param repositoryUniqueId
	 * @param documentUniqueId
	 * @param additionalIdentifiers
	 * @throws URNFormatException
	 */
	public PatientArtifactIdentifierImpl(
		String homeCommunityId, 
		String repositoryUniqueId, 
		String documentUniqueId,
		String... additionalIdentifiers) 
	throws URNFormatException
	{
		super(getManagedNamespace(), homeCommunityId, repositoryUniqueId, documentUniqueId, additionalIdentifiers);
	}

	/**
	 * @param urnComponents
	 * @param serializationFormat
	 * @throws URNFormatException
	 */
	public PatientArtifactIdentifierImpl(URNComponents urnComponents, SERIALIZATION_FORMAT serializationFormat)
		throws URNFormatException
	{
		super(urnComponents, serializationFormat);
	}

	public PatientArtifactIdentifierImpl() 
	throws URNFormatException
	{
		super(getManagedNamespace());
	}

	/**
	 * @return the patientIdentifier
	 */
	@Override
	public String getPatientIdentifier()
	{
		return getAdditionalIdentifier(PATIENT_ID_INDEX);
	}

	/**
	 * @param patientIdentifier the patientIdentifier to set
	 */
	public void setPatientIdentifier(String patientIdentifier)
	{
		setAdditionalIdentifier(PATIENT_ID_INDEX, patientIdentifier);
	}
}
