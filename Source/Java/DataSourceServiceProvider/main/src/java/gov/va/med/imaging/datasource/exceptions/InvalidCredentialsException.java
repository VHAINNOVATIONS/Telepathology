/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Jan 7, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.datasource.exceptions;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;

/**
 * @author VHAISWBECKEC
 *
 */
public class InvalidCredentialsException 
extends ConnectionException
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public InvalidCredentialsException()
	{
	}

	/**
	 * Construct an error message that assumes that UID and PWD must be non-null and greater
	 * than zero-length.
	 * 
	 * @param uid
	 * @param pwd
	 */
	public InvalidCredentialsException(String uid, String pwd)
	{
		super(
			uid == null ? "User identification was null and must be provided." :
			uid.length() < 1 ? "User identification was a zero-length string and must be provided." :
			pwd == null ? "Password was null and must be provided." :
			pwd.length() < 1 ? "Password was a zero-length string and must be provided." :
			"Credentials provided were not acceptable to the data source provider, no further details available."
		);
	}
	
	/**
	 * @param message
	 */
	public InvalidCredentialsException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidCredentialsException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidCredentialsException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
