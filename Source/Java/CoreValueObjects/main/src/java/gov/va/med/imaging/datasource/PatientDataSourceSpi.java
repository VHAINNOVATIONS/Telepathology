/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 10, 2008
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
package gov.va.med.imaging.datasource;

import gov.va.med.HealthSummaryURN;
import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.exchange.business.HealthSummaryType;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.exchange.business.PatientMeansTestResult;
import gov.va.med.imaging.exchange.business.PatientSensitiveValue;

import java.io.InputStream;
import java.util.List;
import java.util.SortedSet;

/**
 * @author VHAISWWERFEJ
 *
 */
@SPI(description="Interface to get patient information.")
public interface PatientDataSourceSpi 
extends VersionableDataSourceSpi 
{

	/**
	 * Return a List of Site where the specified patient has been treated.
	 * 
	 * @param globalRoutingToken
	 * @param patientIcn 
	 * @param includeTrailingCharactersForSite200 Determines if trailing characters of a site number for site 200 should be included
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public List<String> getTreatingSites(RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier, 
			boolean includeTrailingCharactersForSite200)
	throws MethodException, ConnectionException;
	
	/**
	 * Get a SortedSet of Patient instances that meet the
	 * search criteria.
	 * The searchName may be a fragment, following the Clinical Display format.
	 * 
	 * @param searchName
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public SortedSet<Patient> findPatients(RoutingToken globalRoutingToken, String searchName)
	throws MethodException, ConnectionException;
	
	/**
	 * Get an image that may be used for patient identification.
	 * 
	 * @param patientIcn
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public InputStream getPatientIdentificationImage(PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException;
	
	/**
	 * Checks the level of sensitivity for the patient.
	 * 
	 * @param patientIcn
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public PatientSensitiveValue getPatientSensitivityLevel(RoutingToken globalRoutingToken, PatientIdentifier PatientIdentifier)
	throws MethodException, ConnectionException;
	
	public boolean logPatientSensitiveAccess(RoutingToken globalRoutingToken, PatientIdentifier PatientIdentifier)
	throws MethodException, ConnectionException;

	/**
	 * Get the information about a specified patient.
	 * 
	 * @param globalRoutingToken
	 * @param patientIcn
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public Patient getPatientInformation(RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException;
	
	public PatientMeansTestResult getPatientMeansTest(RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException;
	
	public List<HealthSummaryType> getHealthSummaryTypes(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException;
	
	public String getHealthSummary(HealthSummaryURN healthSummaryUrn, PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException;
}
