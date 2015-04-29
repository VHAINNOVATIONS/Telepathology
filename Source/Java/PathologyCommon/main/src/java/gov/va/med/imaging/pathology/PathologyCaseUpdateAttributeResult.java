/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 14, 2012
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
package gov.va.med.imaging.pathology;

/**
 * @author VHAISWWERFEJ
 *
 */
public class PathologyCaseUpdateAttributeResult
{
	private final boolean success;
	private final String errorMessage;
	
	private PathologyCaseUpdateAttributeResult(boolean success, String errorMessage)
	{
		this.success = success;
		this.errorMessage = errorMessage;
	}
	
	public static PathologyCaseUpdateAttributeResult createSuccessfulLockResult()
	{
		return new PathologyCaseUpdateAttributeResult(true, null);
	}
	
	public static PathologyCaseUpdateAttributeResult createFailedLockResult(String errorMessage)
	{
		return new PathologyCaseUpdateAttributeResult(false, errorMessage);
	}

	public boolean isSuccess()
	{
		return success;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

}
