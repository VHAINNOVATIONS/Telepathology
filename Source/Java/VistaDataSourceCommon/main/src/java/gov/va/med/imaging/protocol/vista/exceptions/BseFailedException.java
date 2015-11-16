/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 22, 2010
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
package gov.va.med.imaging.protocol.vista.exceptions;

/**
 * This exception occurs if there is a failure using BSE that IS NOT the token expired.
 * <br><b>THIS EXCEPTION SHOULD NOT BE USED IF THE BSE TOKEN EXPIRED.</b>
 * 
 * <br>This exception should be used if the remote site does not have BSE installed or if there was a 
 * problem authenticating with BSE (such as the remote site doesn't recognize the site specified).
 * <br>
 * This exception does not indicate there was a communication problem and simply indicates something went wrong with BSE.
 * This exception may indicate another connection method (CAPRI) can be attempted if allowed.
 * 
 * @author vhaiswwerfej
 *
 */
public class BseFailedException
extends Exception
{
	private final static long serialVersionUID = 1L;
	
	public BseFailedException(Throwable t)
	{
		super(t);
	}
	
	public BseFailedException(String msg)
	{
		super(msg);
	}
	
	public BseFailedException(String msg, Throwable t)
	{
		super(msg, t);
	}
}
