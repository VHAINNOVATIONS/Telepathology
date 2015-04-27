/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 21, 2013
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
package gov.va.med.imaging.federation.pathology.rest.types;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author VHAISWWERFEJ
 *
 */
@XmlRootElement
public class PathologyFederationElectronicSignatureNeedType
{
	private PathologyFederationElectronicSignatureNeedStatusType status;
	private String message;
	
	public PathologyFederationElectronicSignatureNeedType()
	{
		super();
	}
	
	/**
	 * @param status
	 * @param message
	 */
	public PathologyFederationElectronicSignatureNeedType(
			PathologyFederationElectronicSignatureNeedStatusType status,
			String message)
	{
		super();
		this.status = status;
		this.message = message;
	}
	
	public PathologyFederationElectronicSignatureNeedStatusType getStatus()
	{
		return status;
	}
	
	public void setStatus(
			PathologyFederationElectronicSignatureNeedStatusType status)
	{
		this.status = status;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}

}
