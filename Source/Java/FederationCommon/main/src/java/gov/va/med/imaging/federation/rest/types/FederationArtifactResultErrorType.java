/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 29, 2010
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
package gov.va.med.imaging.federation.rest.types;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author vhaiswwerfej
 *
 */
@XmlRootElement
public class FederationArtifactResultErrorType
{
	private String codeContext;
	private FederationArtifactResultErrorSeverityType severity;
	private String location;
	private FederationArtifactResultErrorCodeType errorCode;
	
	public FederationArtifactResultErrorType()
	{
		super();
	}

	public String getCodeContext()
	{
		return codeContext;
	}

	public void setCodeContext(String codeContext)
	{
		this.codeContext = codeContext;
	}

	public FederationArtifactResultErrorSeverityType getSeverity()
	{
		return severity;
	}

	public void setSeverity(FederationArtifactResultErrorSeverityType severity)
	{
		this.severity = severity;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public FederationArtifactResultErrorCodeType getErrorCode()
	{
		return errorCode;
	}

	public void setErrorCode(FederationArtifactResultErrorCodeType errorCode)
	{
		this.errorCode = errorCode;
	}
}
