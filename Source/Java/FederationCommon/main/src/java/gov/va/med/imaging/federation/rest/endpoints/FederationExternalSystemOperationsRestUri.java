/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 16, 2010
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
public class FederationExternalSystemOperationsRestUri
{
	/**
	 * Service application
	 */
	public final static String externalSystemOperationsServicePath = "externalsystem"; 
	
	public final static String prefetchExam = "exam/{examId}";
	
	public final static String refreshSiteServiceCache = "siteServiceRefresh";
	
	/**
	 * Path to prefetch study metadata
	 */
	public final static String prefetchStudiesPath = "studies/{routingToken}/{patientIcn}/{authorizedSensitiveLevel}/{studyLoadLevel}";
	
	public final static String prefetchImage = "image/{imageUrn}";
	
	public final static String prefetchExamImage = "examImage/{imageUrn}/{includeTextFile}";
	
	public final static String prefetchGai = "gai/{gai}";
}
