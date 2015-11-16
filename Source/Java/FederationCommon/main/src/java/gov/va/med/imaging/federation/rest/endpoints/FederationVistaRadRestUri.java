/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 24, 2010
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
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
package gov.va.med.imaging.federation.rest.endpoints;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationVistaRadRestUri 
{
	/**
	 * Service application
	 */
	public final static String vistaradServicePath = "vistarad"; 
	
	public final static String vistaradGetExamPath = "exam/{examId}";
	public final static String vistaradGetExamsPath = "exams/{routingToken}/{patientIcn}/{fullyLoaded}/{forceRefresh}/{forceImagesFromJb}";
	public final static String vistaradGetExamReportPath = "exam/report/{examId}";
	public final static String vistaradGetExamRequisitionReportPath = "exam/requisitionreport/{examId}";
	public final static String vistaradGetActiveExamsPath = "exam/active/{routingToken}/{listDescriptor}";
	public final static String vistaradPostImageAccess = "imageaccess/{routingToken}";
	public final static String vistaradCptCodes = "cpt/{routingToken}/{cptCode}";
	public final static String vistaradGetExamImages = "exam/images/{examId}";
	public final static String vistaradGetNextPatientRegistration= "patient/registration/{routingToken}";
}
