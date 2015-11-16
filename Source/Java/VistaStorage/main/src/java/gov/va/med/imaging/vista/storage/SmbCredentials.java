/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 6, 2008
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
package gov.va.med.imaging.vista.storage;

import gov.va.med.imaging.core.interfaces.StorageCredentials;
import gov.va.med.imaging.url.vista.StringUtils;

/**
 * Contains the credentials necessary to access an image share. Usually derived from the network location from VistA
 * 
 * @author VHAISWWERFEJ
 *
 */
public class SmbCredentials 
{
	private final SmbServerShare smbServerShare;
	private final String username;
	private final String password;
	private final String domain;	
	
	/**
	 * Creates the SMB credentials necessary to access the image share
	 * @param filename The filename to open
	 * @param storageCredentials The credentials to use to access the share
	 * @return
	 */
	public static SmbCredentials create(
			SmbServerShare smbServerShare, 
			StorageCredentials storageCredentials)
	{
		
		
		String username = storageCredentials.getUsername();
		username = username.replace('\\', '/');
		
		String usernameParts[] = StringUtils.Split(username, StringUtils.SLASH);
		String domain = "";
		if(usernameParts.length > 1) {
			//username = usernameParts[0] + ";" + usernameParts[1];
			domain = usernameParts[0];
			username = usernameParts[1];
		}
		String password = storageCredentials.getPassword();
		
		return new SmbCredentials(smbServerShare, username, password, domain);
		//return new SmbCredentials("SMB://" + server + ":" + port + path, username, password, domain);
		//return new SmbCredentials(createSmbUrlString(networkPath, 139), username, password, domain);
	}
	
	public SmbCredentials(
			SmbServerShare smbServerShare, 
			String username, 
			String password,
			String domain) 
	{
		super();
		this.smbServerShare = smbServerShare;
		this.username = username;
		this.password = password;
		this.domain = domain;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getDomain() {
		return domain;
	}

	public SmbServerShare getSmbServerShare()
	{
		return smbServerShare;
	}
}
