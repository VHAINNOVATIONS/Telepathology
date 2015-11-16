/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 22, 2009
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
package gov.va.med.imaging.exchange.enums;

/**
 * Contains the possible security context types available. This should be set in the facade and used by the data source
 * 
 * @author vhaiswwerfej
 *
 */
public enum ImagingSecurityContextType 
{
	MAG_WINDOWS("Security context used for Clinical Display, also used by VIX for DOD services", "MAG WINDOWS", true),
	MAGJ_VISTARAD("Security context used for VistARad", "MAGJ VISTARAD WINDOWS", true),
	DICOM_QR_CONTEXT("Security context used for DICOM Query Retrieve", "MAG DICOM VISA", true), // "MAG DICOM QUERY RETRIEVE"),
	CPRS_CONTEXT("Security context used for OR (CPRS) queries", "OR CPRS GUI CHART", false),
	DVBA_CAPRI_GUI("Security context used for CAPRI queries", "DVBA CAPRI GUI", false),
	MAGTP_WORKLIST_MGR("Security context used for TelePathology", "MAGTP WORKLIST MGR", true);
	
	
	private final String description;
	private final String securityContext;
	private final boolean imagingContext;
	
	ImagingSecurityContextType(String description, String securityContext, boolean imagingContext)
	{
		this.description = description;
		this.securityContext = securityContext;
		this.imagingContext = imagingContext;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the securityContext
	 */
	public String getSecurityContext() {
		return securityContext;
	}

	/**
	 * @return the imagingContext
	 */
	public boolean isImagingContext()
	{
		return imagingContext;
	}
}
