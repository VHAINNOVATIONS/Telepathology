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
package gov.va.med.imaging.core.interfaces.router;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.exchange.business.ArtifactResultError;
import gov.va.med.imaging.exchange.enums.ArtifactResultErrorCode;
import gov.va.med.imaging.exchange.enums.ArtifactResultErrorSeverity;

/**
 * @author vhaiswwerfej
 *
 */
public class CoreArtifactResultError
implements ArtifactResultError
{
	private String codeContext;
	private ArtifactResultErrorCode errorCode;
	private String location;
	private ArtifactResultErrorSeverity severity;
	
	public CoreArtifactResultError()
	{
		// default constructor to support serialization to disk (this error might get cached)
		super();
	}
	
	public CoreArtifactResultError(String codeContext, ArtifactResultErrorCode errorCode, 
			String location, ArtifactResultErrorSeverity severity)
	{
		this.codeContext = codeContext;
		this.severity = severity;
		this.errorCode = errorCode;
		this.location = location; 
	}
	
	public static ArtifactResultError createFromException(RoutingToken routingToken, Throwable t)
	{
		String codeContext = null;
		ArtifactResultErrorCode errorCode = ArtifactResultErrorCode.internalException;
		if(t != null)
		{
			codeContext = t.getMessage();
			if((codeContext == null) || (codeContext.length() <= 0))
			{
				codeContext = t.getClass().getName();
			}
			
			if((codeContext != null) && 
					(codeContext.contains("java.net.SocketTimeoutException: Read timed out")))
			{
				errorCode = ArtifactResultErrorCode.timeoutException;
			}
		}		
		
		ArtifactResultErrorSeverity severity = ArtifactResultErrorSeverity.error;
		String location = routingToken == null ? "<null>" : routingToken.getRepositoryUniqueId();
		return new CoreArtifactResultError(codeContext, errorCode, location, severity);
	}

	@Override
	public String getCodeContext()
	{
		return codeContext;
	}

	@Override
	public ArtifactResultErrorCode getErrorCode()
	{
		return errorCode;
	}

	@Override
	public String getLocation()
	{
		return location;
	}

	@Override
	public ArtifactResultErrorSeverity getSeverity()
	{
		return severity;
	}

	public void setCodeContext(String codeContext)
	{
		this.codeContext = codeContext;
	}

	public void setErrorCode(ArtifactResultErrorCode errorCode)
	{
		this.errorCode = errorCode;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public void setSeverity(ArtifactResultErrorSeverity severity)
	{
		this.severity = severity;
	}
}
