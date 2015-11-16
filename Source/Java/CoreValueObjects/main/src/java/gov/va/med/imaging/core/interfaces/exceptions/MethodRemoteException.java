/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Mar 12, 2008
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
package gov.va.med.imaging.core.interfaces.exceptions;

/**
 * A MethodException derivation for exceptions that occurred on a remote
 * data source.  Exceptions in network connectivity or accessibility of a
 * remote data source should be thrown as ConnectionException, not using
 * this class.  this class should be used to wrap remote application
 * exceptions.
 * 
 * A MethodRemoteException should not be retried against another
 * protocol as the result will be the same.
 * 
 * @author VHAISWBECKEC
 *
 */
public class MethodRemoteException 
extends MethodException
{
	private static final long serialVersionUID = 1L;

	public MethodRemoteException()
	{
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MethodRemoteException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public MethodRemoteException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public MethodRemoteException(Throwable cause)
	{
		super(cause);
	}

}
