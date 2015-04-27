/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 15, 2012
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
package gov.va.med.imaging.tomcat.vistarealm;

import java.security.Principal;
import java.security.cert.X509Certificate;

import org.apache.catalina.Container;
import org.apache.catalina.Realm;
import org.apache.log4j.Logger;

/**
 * @author VHAISWWERFEJ
 *
 */
public class VistaRemoteSiteRemotelyAuthenticatedRealm
extends AbstractVistaRealmImpl
implements Realm, org.apache.catalina.Lifecycle, VistaRemoteSiteAccessVerifyRealmMBean
{
	
	private Container parentContainer = null;
	private Realm parentContainerRealm = null;
	
	private String claimsSiteNumber = null;
	private Boolean generateBseToken = Boolean.TRUE;

	private Logger logger = Logger.getLogger(this.getClass());
	
	public VistaRemoteSiteRemotelyAuthenticatedRealm()
	{
		logger.info(VistaRemoteSiteRemotelyAuthenticatedRealm.class.getCanonicalName() + " ctor()");
		this.setSiteNumber("<all>");// the site number must not be null (prevent exceptions) but this realm fronts
		
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealmImpl#getLogger()
	 */
	@Override
	protected Logger getLogger() 
	{
		return logger;
	}

	/**
	 * @return the claimsSiteNumber
	 */
	public String getClaimsSiteNumber()
	{
		return claimsSiteNumber;
	}

	/**
	 * @param claimsSiteNumber the claimsSiteNumber to set
	 */
	public void setClaimsSiteNumber(String claimsSiteNumber)
	{
		this.claimsSiteNumber = claimsSiteNumber;
	}

	/**
	 * @return the generateBseToken
	 */
	public Boolean getGenerateBseToken()
	{
		return generateBseToken;
	}

	/**
	 * @param generateBseToken the generateBseToken to set
	 */
	public void setGenerateBseToken(Boolean generateBseToken)
	{
		this.generateBseToken = generateBseToken;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealmImpl#isInitialized()
	 */
	@Override
	public boolean isInitialized() 
	{
		boolean result = true;
		//Container container = this.getContainer();
		//String containerName = container == null ? null : container.getName();					

		return result;
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Realm#authenticate(java.lang.String, byte[])
	 */
	@Override
	public Principal authenticate(String username, byte[] credentials) 
	{
		return authenticate(username, new String(credentials));
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Realm#authenticate(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Principal authenticate(String username, String clientDigest, String nOnce, String nc, 
			String cnonce, String qop, String realm, String md5a2) 
	{
		getLogger().info("authenticate (" + username + ", digest)");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Realm#authenticate(java.lang.String, java.lang.String)
	 */
	@Override
	public Principal authenticate(String username, String password) 
	{
		VistaRealmPrincipal principal = null;
		
		String siteNumber = username;

		getLogger().info("Realm '" + siteNumber + "'-authenticate");
		
		principal = new VistaRealmPrincipal(siteNumber, false,
		        VistaRealmPrincipal.AuthenticationCredentialsType.Password);		
		principal.setPreemptiveAuthorization(this);
		
		
		if(claimsSiteNumber != null && claimsSiteNumber.equals(siteNumber))
		{
			getLogger().info("Claims user authentication");

			principal.addRole("claims"); // add the claims role to allow the user to access without having the necessary keys
		}
		else
		{
			principal.addRole("remote-user");
		}

		return principal;
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Realm#authenticate(java.security.cert.X509Certificate[])
	 */
	@Override
	public Principal authenticate(X509Certificate certs[]) 
	{
		getLogger().debug("Authenticating using X509 certificate.");
		return null;
	}
	
	// ===========================================================================================================
	// Realm implementation
	// ===========================================================================================================

	/**
	 * A Container is an object that can execute requests received from a
	 * client, and return responses based on those requests. Engine -
	 * Representation of the entire Catalina servlet engine. Host -
	 * Representation of a virtual host containing a number of Contexts. Context -
	 * Representation of a single ServletContext, which will typically contain
	 * one or more Wrappers for the supported servlets. Wrapper - Representation
	 * of an individual servlet definition.
	 */

	@Override
	public void setContainer(Container container)
	{
		super.setContainer(container);

		// if the container has a parent then get its realm
		// this class will authenticate against that realm as well as its own
		// authentication repository
		if (getContainer() != null)
		{
			getLogger().info("Container name is '" + getContainer().getName() + "' container type is '" + getContainer().getClass().getName() + "'");
			
			parentContainer = getContainer().getParent();
			if (parentContainer != null)
			{
				getLogger().info("Parent container name is '" + parentContainer.getName() + 
					"' parent container type is '" + parentContainer.getClass().getName() + "'");
				parentContainerRealm = parentContainer.getRealm();
				getLogger().info("Parent container realm type is '" + 
					(parentContainerRealm == null ? "null" : parentContainerRealm.getClass().getName()) + 
					"'");
			}
			else
			{
				getLogger().info("Parent container is null.  Delegation to parent realm will be disabled.");
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealmImpl#toString()
	 */
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
    	
    	sb.append(this.getClass().getSimpleName());
    	
	    return sb.toString();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealmImpl#getSiteName()
	 */
	@Override
	public String getSiteName() 
	{
		return "<site name>";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealmImpl#getSiteAbbreviation()
	 */
	@Override
	public String getSiteAbbreviation() 
	{
		return "<site abbr>";
	}

}
