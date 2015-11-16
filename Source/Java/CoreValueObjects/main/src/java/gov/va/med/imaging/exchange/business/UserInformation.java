/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 21, 2011
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
package gov.va.med.imaging.exchange.business;

import java.util.List;

/**
 * This object contains user information including details about the user and they keys the
 * user holds
 * 
 * @author VHAISWWERFEJ
 *
 */
public class UserInformation
{
	private final User user;
	private final List<String> keys;
	private final boolean userCanAnnotate;
	
	public UserInformation(User user, List<String> keys, boolean userCanAnnotate)
	{
		this.user = user;
		this.keys = keys;
		this.userCanAnnotate = userCanAnnotate;
	}

	/**
	 * Details about the user
	 * @return
	 */
	public User getUser()
	{
		return user;
	}

	/**
	 * Return the security keys the user holds
	 * @return
	 */
	public List<String> getKeys()
	{
		return keys;
	}

	public boolean isUserCanAnnotate()
	{
		return userCanAnnotate;
	}
}
