/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 21, 2008
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
package gov.va.med.imaging.exchange.enums;

/**
 * @author VHAISWWERFEJ
 *
 */
public enum SiteConnectivityStatus 
{
	
	VIX_READY(
		"VIX_READY",
		"Indicates the local VIX is able to service the request for the specified remote site (VIX, BIA or VistA)."),
	VIX_UNAVAILABLE(
		"VIX_UNAVAILABLE", 
		"Indicates the local ViX is not configured to communicate with the specified remote site (VIX, BIA or VistA)."),
	DATASOURCE_UNAVAILABLE(
		"DATASOURCE_UNAVAILABLE", 
		"Indicates the local ViX failed to communicate with the specified remote site (VIX, BIA or VistA), remote site may be inaccessible or down.");
	
	private String status;
	private String description;
	
	SiteConnectivityStatus(String status, String description)
	{
		this.status = status;
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public String getDescription() {
		return description;
	}

}
