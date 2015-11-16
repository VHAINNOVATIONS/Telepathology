/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 8, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
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

import gov.va.med.RoutingToken;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.exchange.business.vistarad.ActiveExams;
import gov.va.med.imaging.exchange.business.vistarad.Exam;
import gov.va.med.imaging.exchange.business.vistarad.ExamImages;
import gov.va.med.imaging.exchange.business.vistarad.ExamListResult;
import gov.va.med.imaging.exchange.business.vistarad.PatientRegistration;

/**
 * This class defines the Service Provider Interface (SPI) for the VistaRadSource class. 
 * All the abstract methods in this class must be implemented by each 
 * data source service provider who wishes to supply the implementation of a 
 * VistaRadDataSource for a particular datasource type.
 * 
 * @since 1.0
 * @author vhaiswlouthj
 *
 */
@SPI(description="Defines the interface for radiology workstation required data.")
public interface VistaRadDataSourceSpi
extends VersionableDataSourceSpi
{

	/**
	 * Returns the active exams from a site.  The result is the raw output from the data source and should be
	 * cached or retained in any way.
	 * 
	 * @param examStatus Input status to specify the type of exams to return
	 * @param modalities Modalities included in the result
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract ActiveExams getActiveExams(RoutingToken globalRoutingToken, String listDescriptor)
    throws MethodException, ConnectionException;

	/**
	 * Retrieve the exams for a patient. 
	 * @param globalRoutingToken The final source of the data (such as the BIA), not an intermediate location (such as the CVIX)
	 * @param patientICN Unique identifier for a patient
	 * @param fullyLoadExams Determines if the resulting exams are fully or partially loaded.
	 * @param forceRefresh Force a refresh from the source, not the cache
	 * @param forceImagesFromJb If true the images can be moved from JB to HD, don't move images if false
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract ExamListResult getExamsForPatient(RoutingToken globalRoutingToken, String patientICN, 
			boolean fullyLoadExams, boolean forceRefresh, boolean forceImagesFromJb)
    throws MethodException, ConnectionException;

	/**
	 * Return an array of CPT codes that are considered relevant to the specified CPT code
	 * @param cptCode
	 * @return Array of relevant CPT Codes
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract String[] getRelevantPriorCptCodes(RoutingToken globalRoutingToken, String cptCode)
    throws MethodException, ConnectionException;

	/**
	 * Return the next patient registration
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract PatientRegistration getNextPatientRegistration(RoutingToken globalRoutingToken)
    throws MethodException, ConnectionException;
	
	/**
	 * Return the report for a specified VistARad Exam
	 * @param studyUrn
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract String getExamReport(StudyURN studyUrn)
	throws MethodException, ConnectionException;
	
	/**
	 * Return the requisition report for a specified VistARad Exam
	 * @param studyUrn StudyURN representing a VistARad Exam
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract String getExamRequisitionReport(StudyURN studyUrn)
	throws MethodException, ConnectionException;
	
	/**
	 * Return the images contained in a specified VistARad Exam
	 * @param studyUrn StudyURN representing a VistARad Exam
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 * @deprecated This method should not be used anymore - use getExam instead which returns a fully loaded exam including the images
	 */
	public abstract ExamImages getExamImagesForExam(StudyURN studyUrn)
	throws MethodException, ConnectionException;
	
	/**
	 * Returns a fully loaded exam
	 * @param studyUrn StudyURN representing a VistARad Exam
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract Exam getExam(StudyURN studyUrn)
	throws MethodException, ConnectionException;
	
	/**
	 * Post access to a VistARad exam
	 * 
	 * @param inputParameter opaque input parameter, should be passed to data source without modification
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract boolean postExamAccessEvent(RoutingToken globalRoutingToken, String inputParameter)
	throws MethodException, ConnectionException;
	
	/**
	 * Tell the data source to initiate a prefetch operation for the specified exam. 
	 * @param studyUrn
	 * @param targetSiteNumber The site to initiate the prefetch operation
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	/*
	public abstract boolean initiatePrefetchOperation(StudyURN studyUrn, String targetSiteNumber)
	throws MethodException, ConnectionException;
	 */
}
