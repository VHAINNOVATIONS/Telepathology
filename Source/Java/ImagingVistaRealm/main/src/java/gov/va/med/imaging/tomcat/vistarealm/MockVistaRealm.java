package gov.va.med.imaging.tomcat.vistarealm;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This class provides a Realm that acts like the VistaREalm except that all of its
 * authentication is done against a set of defined (hard-coded) users.
 * 
 * @author VHAISWBECKEC
 *
 */
public class MockVistaRealm 
extends VistaAccessVerifyRealm
{
	private Logger logger = Logger.getLogger(this.getClass());
	public static final String usernameFilename = "MockVistaRealmUsers.xml";
	private List<VistaRealmPrincipal> userList = new ArrayList<VistaRealmPrincipal>();
	
	public MockVistaRealm() 
	throws Exception
	{
		// turn off Principal caching
		setUsingPrincipalCache(false);
		
		try
		{
			loadUserList();
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			throw new java.lang.Exception("Unable to read configuration file");
		}
	}

	private void loadUserList() 
	throws FileNotFoundException
	{
		InputStream inStream = getClass().getClassLoader().getResourceAsStream(usernameFilename);
		XMLDecoder decoder = new XMLDecoder(inStream);
		
		try
		{
			while(true)		// will escape on an ArrayIndexOutOfBoundsException
			{
				MockVistaRealmPrincipalDO principalDO = (MockVistaRealmPrincipalDO)decoder.readObject();
				VistaRealmPrincipal principal = createVistaRealmPrincipal(principalDO);
				logger.info("MockVistaRealm adding user '" + principal.getAccessCode() + "'" );
				userList.add(principal);
			}
		} 
		catch (ArrayIndexOutOfBoundsException e)
		{
			// we're done reading
		}
		decoder.close();
		
	}

	@Override
	public Principal authenticate(String username, String password)
	{
		logger.info("MockVistaRealm - authenticate (" + username + ", <password not shown>)");

		if(username == null)
			return null;
		
		for(VistaRealmPrincipal principal: userList)
			if(principal.getAccessCode().equals(username) && principal.getVerifyCode().equals(password))
			{
				logger.info("MockVistaRealm - authenticate (" + username + ", <password not shown>) authenticated !");
				return principal;
			}
		
		logger.info("MockVistaRealm - authenticate (" + username + ", <password not shown>) NOT authenticated !");
		return null;
	}

	/**
	 * Just a little utility to write out the users file.
	 * 
	 * @param argv
	 * @throws FileNotFoundException
	 */
	public static void main(String[] argv) 
	throws FileNotFoundException
	{
		FileOutputStream outStream = new FileOutputStream(usernameFilename);
		XMLEncoder encoder = new XMLEncoder(outStream);
		
		encoder.writeObject(new MockVistaRealmPrincipalDO("VistaRealm", "boating1", "boating1.", "655321", "Alex DeLarge", "111-22-3333", "660", "SLC", new String[]{VistaRealmRoles.VistaUserRole.getRoleName()}));
		encoder.writeObject(new MockVistaRealmPrincipalDO("VistaRealm", "doduser", "dodpassword", "655321", "DOD User", "222-33-4444", "660", "SLC", new String[]{VistaRealmRoles.VistaUserRole.getRoleName()}));
		encoder.writeObject(new MockVistaRealmPrincipalDO("VistaRealm", "alexdelarge", "655321", null, null, null, null, null, new String[]{VistaRealmRoles.ClinicalDisplayUser.getRoleName()}));
		encoder.writeObject(new MockVistaRealmPrincipalDO("VistaRealm", "vixs", "vixs", null, null, null, null, null, new String[]{VistaRealmRoles.PeerVixsRole.getRoleName()}));
		encoder.writeObject(new MockVistaRealmPrincipalDO("VistaRealm", "admin", "raptor", null, null, null, null, null, new String[]{VistaRealmRoles.AdministratorRole.getRoleName()}));

		encoder.close();
	}
	
	public static VistaRealmPrincipal createVistaRealmPrincipal(MockVistaRealmPrincipalDO principalDO)
	{
		List<String> roleList = new ArrayList<String>();
		
		for(String role: principalDO.getRoles())
			roleList.add(role);
		
		VistaRealmPrincipal principal = new VistaRealmPrincipal(
			principalDO.getRealm(), false, VistaRealmPrincipal.AuthenticationCredentialsType.Password, 
			principalDO.getAccess(), principalDO.getVerify(), 
			principalDO.getDuz(), principalDO.getFullname(), principalDO.getSsn(), 
			principalDO.getSiteNumber(), principalDO.getSitename(), 
			roleList, null);

		return principal;
	}
	
}

