/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 7, 2008
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
package gov.va.med.imaging.vistadatasource.session;

import gov.va.med.imaging.core.interfaces.exceptions.CredentialsExpiredException;
import gov.va.med.imaging.core.interfaces.exceptions.InvalidUserCredentialsException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.enums.ImagingSecurityContextType;
import gov.va.med.imaging.protocol.vista.VistaCommonTranslator;
import gov.va.med.imaging.protocol.vista.VistaLoginMethod;
import gov.va.med.imaging.protocol.vista.exceptions.BseFailedException;
import gov.va.med.imaging.protocol.vista.exceptions.MissingCredentialsException;
import gov.va.med.imaging.protocol.vista.exceptions.UnableToCreateContextException;
import gov.va.med.imaging.protocol.vista.exceptions.VistaConnectionException;
import gov.va.med.imaging.protocol.vista.exceptions.VistaParsingException;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaConnection;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.VistaCommonQueryFactory;
import gov.va.med.imaging.vistadatasource.session.bse.VistaSessionBSECache;
import gov.va.med.imaging.vistadatasource.session.bse.VistaSiteBseStatus;
import gov.va.med.imaging.vistadatasource.session.configuration.VistaSessionConfiguration;

import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * Represents a logged-in session of a Vista user.
 * Implementation of the functions and process necessary to login to Vista.  
 * Implements the local connection and the remote connection methods
 * 
 * @author VHAISWWERFEJ
 *
 */
public class VistaSession
{
	private final static Logger logger = Logger.getLogger(VistaSession.class);
	
	//private final static String MAG_WINDOWS_CONTEXT = "MAG WINDOWS";
	private final static String CAPRI_CONTEXT = "DVBA CAPRI GUI";

	private final static int FAILED_SIGNON_MAX_COUNT = 10;		// maximum number of times to retry an RPC_SIGNON
	private final static int FAILED_SIGNON_MIN_WAIT_TIME = 1000; // maximum number of ms to wait between retrying the RPC_SIGNON
	private final static int FAILED_SIGNIN_MAX_WAIT_TIME = 3000; // maximum number of ms to wait between retrying the RPC_SIGNON
	
	// default context to use if none specified in transaction context
	private final static ImagingSecurityContextType defaultSecurityContextType = ImagingSecurityContextType.MAG_WINDOWS;
	
	// ======================================================================================
	// Instance Members
	// ======================================================================================
	
	private int failedSignonRetryMax = FAILED_SIGNON_MAX_COUNT;		// for the server, just leave this value at the default
	private int defaultSiteBrokerConncetionTimeout = 25; // 25 seconds as default
					
	// vistaLoginComplete will be true after the user has been authenticated
	private boolean vistaLoginComplete = false;
	private final VistaConnection vistaConnection;
	private final Site site;
	private long loginTime = 0L;
	private long lastUsedTime = 0L;
	private long lastInternalTime = 0L; // the last RPC call both internal and external calls
	private boolean localLogin = false;
	private boolean sessionCreated = false;
	private boolean vistaRadLoggedIn = false;
	private int siteBrokerConnectionTimeout = 0; // measured in seconds
	private long sessionIndex = 0L; // keeps a unique number representing this session, incremements as sessions are created

	private String securityContext;
	private String version = null;
	
	public static VistaSession getOrCreate(URL url, Site site) 
	throws IOException, ConnectionException, MethodException, SecurityCredentialsExpiredException
	{
		return getOrCreate(url, site, null, null);
	}
	
	public static VistaSession getOrCreate(URL url, Site site, ImagingSecurityContextType imagingSecurityContext) 
	throws IOException, ConnectionException, MethodException, SecurityCredentialsExpiredException
	{
		return getOrCreate(url, site, null, imagingSecurityContext);
	}
	
	public static VistaSession getOrCreate(URL url, Site site, String version) 
	throws IOException, ConnectionException, MethodException, SecurityCredentialsExpiredException
	{
		return getOrCreate(url, site, version, null);
	}
	
	/**
	 * 
	 * @param url
	 * @param site
	 * @return
	 * @throws IOException 
	 * @throws MethodException 
	 * @throws ConnectionException 
	 */
	public static VistaSession getOrCreate(URL url, Site site, String version, ImagingSecurityContextType imagingSecurityContext) 
	throws IOException, ConnectionException, MethodException, SecurityCredentialsExpiredException
	{
		String securityContextToSet = null;
		if(imagingSecurityContext != null)
			securityContextToSet = imagingSecurityContext.getSecurityContext();
		else
			securityContextToSet = getImagingSecurityContextType().getSecurityContext();
		logger.info("getOrCreate(" + url.toExternalForm() + ") with security Context (" + securityContextToSet + ") and version (" + version + ").");
		VistaSession session = VistaSessionCache.getSingleton().get(url, securityContextToSet, version);
		logger.info("getOrCreate(" + url.toExternalForm() + ") " + 
				(session == null ? "did not find" : "found") + " session in cache" );
		
		if(session == null)
		{
			session = new VistaSession(url, site, securityContextToSet, version);
		
			if(session != null)
			{
				logger.info("getOrCreate(" + url.toExternalForm() + ") connecting new VistaSession.");
				connectAndLogin(url, session);			
			}
		}
		
		logger.info("getOrCreate(" + url.toExternalForm() + ") " + 
				(session == null ? "did not instantiate" : "instantiated") + " VistaSession." );
		
		if(session != null)
			TransactionContextFactory.get().setVistaSessionIndex(session.getSessionIndex());		
		
		return session;
	}
	
	/**
	 * Return a welcome message for the specified site. This method does not require the user to be authenticated to VistA
	 * 
	 * @param url
	 * @param site
	 * @return The welcome message directly from VistA without modification
	 * @throws IOException
	 * @throws VistaMethodException
	 * @throws InvalidVistaCredentialsException
	 */
	public static String getWelcomeMessage(URL url, Site site)
	throws IOException, VistaMethodException, InvalidVistaCredentialsException
	{
		VistaSession session = null;
		try
		{
			session = new VistaSession(url, site, null, null);
			logger.info("getWelcomeMessage(" + url.toExternalForm() + ") connecting VistaConnection." );
			long startTime = System.currentTimeMillis();
			session.connect();
			logger.info("getWelcomeMessage(" + url.toExternalForm() + ") connected to Vista in '" + (System.currentTimeMillis() - startTime) + "' ms, now logging in...");
			try
	        {
				// another sacrifice to Vista, logging in too quickly after connecting sometimes causes problems
	            Thread.sleep(100);
	        } 
			catch (InterruptedException e){}
			
			// don't need to login, just want to call rpc
			VistaQuery welcomeMessageQuery = VistaCommonQueryFactory.createWelcomeMessageQuery();
			return session.call(welcomeMessageQuery);
		}
		finally
		{
			// right now we are disconnecting the session immediately, not reusing it in any way
			// this is not very efficient but it is simple
			if(session != null)
			{
				session.disconnect(true);				
			}
		}
	}
	
	public static void authenticateUser(URL url, Site site)
	throws InvalidUserCredentialsException, CredentialsExpiredException, MethodException, IOException, ConnectionException
	{
		VistaSession session = null;
		try
		{
			ImagingSecurityContextType securityContext = getImagingSecurityContextType();
			session = new VistaSession(url, site, securityContext.getSecurityContext(), null);
			logger.info("authenticateUser(" + url.toExternalForm() + ") connecting VistaConnection." );
			long startTime = System.currentTimeMillis();
			session.connect();
			logger.info("authenticateUser(" + url.toExternalForm() + ") connected to Vista in '" + (System.currentTimeMillis() - startTime) + "' ms, now logging in...");
			try
	        {
				// another sacrifice to Vista, logging in too quickly after connecting sometimes causes problems
	            Thread.sleep(100);
	        } 
			catch (InterruptedException e){}
			
			session.localLogin();
			
			TransactionContext transactionContext = TransactionContextFactory.get();
			transactionContext.setSiteName(site.getSiteName());
			transactionContext.setSiteNumber(site.getSiteNumber());
			
			VistaQuery userInformationQuery = VistaCommonQueryFactory.createGetUserInformationQuery();
			try
			{
				String userInformation = session.call(userInformationQuery);
				if(userInformation == null)
					throw new MethodException("No user information retrieved");
				String[] userInformationParts = StringUtils.Split(userInformation,StringUtils.NEW_LINE);
				String duz = null;
				try 
				{
					duz = userInformationParts[0].trim();
					transactionContext.setDuz(duz);
					transactionContext.setFullName(userInformationParts[1].trim());
		        } 
		        catch (Exception ex) 
				{
		        	throw new MethodException("Unable to retrieve either user DUZ and/or Full Name. " + ex);
		        }
		        VistaQuery ssnQuery = VistaCommonQueryFactory.createGetUserSsnQuery(duz);
		        try 
		        {
		        	String rtn = null;
		        	rtn = session.call(ssnQuery);
		        	
		        	if (rtn == null || rtn.equals("")) 
		            {
		            	throw new MethodException("Unable to retrieve user SSN.");
		            }
		    		String [] ssnParts = StringUtils.Split(rtn, StringUtils.CARET);
		            try 
		    		{
		            	transactionContext.setSsn(ssnParts[8]);
		            } 
		            catch (Exception ex) 
		    		{
		            	throw new MethodException("Unable to retrieve user SSN. " + ex);
		            }
		        }
		        catch(Exception ex) 
		        {
		        	throw new MethodException(ex);		        	
		        }
			} 
			catch (InvalidVistaCredentialsException e)
			{
				throw new InvalidUserCredentialsException(e);
			} 
			
			catch (VistaMethodException e)
			{
				throw new MethodException(e);
			}
			session.vistaLoginComplete = true;
    		session.loginTime = System.currentTimeMillis();
    		session.localLogin = true;
    		transactionContext.setLoginMethod(VistaLoginMethod.local.toString());
    		logger.info("Local login successful." ); 
    		session.getSiteBrokerTimeout();
		}
		catch(MissingCredentialsException mcX)
		{
			throw new InvalidUserCredentialsException(mcX);
		}
		finally
		{
			// disconnect the session but leave it open to possibly be used later
			if(session != null)
			{
				session.close();				
			}
		}
	}
	
	private static void connectAndLogin(URL url, VistaSession session)
	throws IOException, MethodException, ConnectionException
	{
		logger.info("connectAndLogin(" + url.toExternalForm() + ") connecting VistaConnection." );
		long startTime = System.currentTimeMillis();
		session.connect();
		logger.info("connectAndLogin(" + url.toExternalForm() + ") connected to Vista in '" + (System.currentTimeMillis() - startTime) + "' ms, now logging in...");
		try
        {
			// another sacrifice to Vista, logging in too quickly after connecting sometimes causes problems
            Thread.sleep(100);
        } 
		catch (InterruptedException e){}
		logger.info("connectAndLogin(" + url.toExternalForm() + ") VistaConnection connected, logging in." );
		session.login();
		logger.info("connectAndLogin(" + url.toExternalForm() + ") VistaConnection connected, logged in." );	
	}
	
	private VistaSession(URL url, Site site, String securityContext, String version)
	{
		this.site = site;
		this.vistaConnection = new VistaConnection(url);
		this.lastUsedTime = getCreateTime();
		this.securityContext = securityContext;
		this.version = version;
		setSessionIndex();
		logger.info("Created VistaSession (" + getSessionIndex() + ") with security context (" + securityContext + ") and version (" + version + ")");
	}
	
	private VistaSession(URL url, Site site)
	{
		this(url, site, null, null);
	}
	
	// current index count, increments as new VistaSession objects are created
	private static long currentSessionIndex = 0L;	
	private synchronized void setSessionIndex()
	{				
		if(currentSessionIndex >= Long.MAX_VALUE)
			currentSessionIndex = 0L;
		else
			currentSessionIndex++;
		this.sessionIndex = currentSessionIndex;	
	}
	
	public final Site getSite() 
	{
		return site;
	}

	/**
	 * @return the securityContext
	 */
	public String getSecurityContext() {
		return securityContext;
	}

	/**
	 * Gets the version connected to VistA (the patch level)
	 * @return
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * Make this private because we want calling clients to use the
	 * methods that this class provides for VistaConnection access.
	 * @return
	 */
	private VistaConnection getVistaConnection() 
	{
		return vistaConnection;
	}
	
	public URL getURL()
	{
		return getVistaConnection().getURL();
	}

	public long getCreateTime()
    {
		return getVistaConnection().getCreateTime();
    }

	public long getConnectTime()
    {
    	return getVistaConnection() == null ? 0L : getVistaConnection().getConnectTime();
    }

	public long getLoginTime()
    {
    	return loginTime;
    }
	
	public long getLastUsedTime()
    {
    	return lastUsedTime;
    }

	public void setLastUsedTime()
    {
    	this.lastUsedTime = System.currentTimeMillis();
    }

	public boolean isLocalLogin()
    {
    	return localLogin;
    }

	/**
	 * The last time an RPC was made, either internal or external call
	 * 
	 * @return the lastInternalTime
	 */
	public long getLastInternalTime()
	{
		return lastInternalTime;
	}

	/**
	 * The time the site has indicated a connection can stay alive between calls
	 * 
	 * @return the siteBrokerConnectionTimeout
	 */
	public int getSiteBrokerConnectionTimeout()
	{
		return siteBrokerConnectionTimeout;
	}

	/**
	 * 
	 * @throws IOException
	 */
	public void connect() 
	throws IOException
	{
		if(! getVistaConnection().isConnected())
			getVistaConnection().connect();
	}

	public boolean isConnected()
	{
		return getVistaConnection().isConnected();
	}
	
	public boolean isLoggedIn()
	{
		return vistaLoginComplete;
	}
	
	/**
	 * A close() just puts this instance in a cache
	 * It may be picked up again if the same user makes another request, or
	 * it will time out and really disconnect if they do not.
	 * This is the method that should be called after a completing a client
	 * request, not disconnect().
	 */
	public void close()
	{
		if(getVistaConnection().isFailedCall())
		{
			logger.warn("VistaSession (" + getSessionIndex() + ") had a failed call, disconnecting immediately");
			disconnect(true);
		}
		else
			VistaSessionCache.getSingleton().put(this);
	}
	
	/**
	 * Certain exceptions from M cause the broker to be in a failed state where no more RPC calls can be made on the
	 * connection. This method closes the current VistA connection and opens a new one
	 * 
	 * This procedure didn't work and I didn't have time to figure out what is wrong with it
	 */
	/*
	public void recoverConnectionAfterFailedMethod()
	throws MethodException, ConnectionException, IOException
	{
		logger.warn("Recovering VistaConnection after failed RPC method on VistaSession (" + getSessionIndex() + ").");
		try
		{
			disconnect(true);
		}
		catch(Exception ex) {}
		logger.warn("recoverConnectionAfterFailedMethod - reconnecting to VistA");
		connectAndLogin(this.getURL(), this);		
	}*/
	
	/**
	 * This method should be called internally on the business methods.
	 * It may be safely called multiple times, calls subsequent to the first are
	 * ignored.
	 * 
	 * @throws ConnectionException
	 * @throws MethodException
	 */
	public void login()
	throws ConnectionException, MethodException, SecurityCredentialsExpiredException
	{
		if(vistaLoginComplete)
			return;
		
		// get the Site that we are connected to
		Site site = getSite();
		String connectedSiteNumber = site.getSiteNumber();
		
		// Get the transaction context to determine where the user was authenticated
		TransactionContext transactionContext = TransactionContextFactory.get();

		String authenticatedSecurityRealm = transactionContext.getRealm();
		boolean authenticatedByVista = transactionContext.isAuthenticatedByVista();
		
		// if the site that we are connected to is the same site number as that of the
		// realm that the user authenticated against then try to do a local login
		logger.info("Determining local/remote login, Connected site number is '" + connectedSiteNumber + 
				"', authenticated security realm is '" + authenticatedSecurityRealm + 
				"', and authenticated by VistA is '" + authenticatedByVista  + "'.");
		if((connectedSiteNumber.equals(authenticatedSecurityRealm)) &&
			(authenticatedByVista))				
		{
			logger.info("Attempting local login for user '" + transactionContext.getName() +
					"' on site '" + connectedSiteNumber + "', authenticated to realm '" + authenticatedSecurityRealm + "'.");
			try
            {
				logger.info("Attempting local login for user '" + transactionContext.getName() + "'.");
	            localLogin();
	    		vistaLoginComplete = true;
	    		this.loginTime = System.currentTimeMillis();
	    		localLogin = true;
	    		transactionContext.setLoginMethod(VistaLoginMethod.local.toString());
	    		logger.info("Local login successful." ); 
	    		getSiteBrokerTimeout();
	    		return;
            } 
			catch(MissingCredentialsException mcX)
			{
				// the credentials were not included, don't worry about it and allow a remote logins
				logger.info("Credentials for a local login were not provided, will attempt remote login.");
			}
			catch(InvalidCredentialsException icX)
			{
				// if we get here then the credentials were tried and they failed, do not continue!				
				throw new InvalidUserCredentialsException(icX);
			}
			catch( Exception x )
            {
				// if the local login fails then default to a remote login
				logger.warn("Unable to login using local credentials for user '" + transactionContext.getName() +
						"', default to remote login.");
            }
		}
		else
			logger.info("Connected site number and authenticated security realm are not equal or not authenticated by Vista, doing remote login." ); 
		
		boolean bseLoginEnabled = getVistaSessionConfiguration().isBseRemoteLoginEnabled();
		boolean capriLoginEnabled = getVistaSessionConfiguration().isCapriRemoteLoginEnabled();
		
		if(bseLoginEnabled)
		{
			if(isBseCredentialsInContext(transactionContext))
			{
				logger.info("BSE credentials in context, BSE token '" + transactionContext.getBrokerSecurityToken() + "'.");
				if(isSiteBseTryable())
				{				
					logger.info("Attempting BSE remote login for user '" + transactionContext.getName() + "'.");
					try
					{
						this.remoteBseLogin();
						this.loginTime = System.currentTimeMillis();
						localLogin = false;
						vistaLoginComplete = true;
						transactionContext.setLoginMethod(VistaLoginMethod.bse.toString());
						logger.info("Remote BSE login successful." ); 
						// only gets here if everything worked properly - if BSE not installed or token exception, won't get here
						updateSiteBseStatus(VistaSiteBseStatus.bseAvailable);
						getSiteBrokerTimeout();
						return;
					}
					catch(InvalidVistaCredentialsException ivcX)
					{
						// don't do anything with the error, its already been logged
						// will want to continue with CAPRI login if possible
						updateSiteBseStatus(VistaSiteBseStatus.bseFailed);
					}
				}
				else
				{
					logger.info("BSE credentials available but site has been cached with status indicating should not try BSE, will not use BSE login.");
				}
			}			
			else
			{
				logger.info("BSE remote login enabled but context does not have security token, cannot continue BSE login.");
			}
		}
		
		if(capriLoginEnabled)
		{
			logger.info("Attempting remote CAPRI login for user '" + transactionContext.getName() + "'.");
			remoteLogin();
			this.loginTime = System.currentTimeMillis();
			localLogin = false;
			vistaLoginComplete = true;
			transactionContext.setLoginMethod(VistaLoginMethod.capri.toString());
			logger.info("Remote CAPRI login successful." ); 
			getSiteBrokerTimeout();
			return;
		}
		
		throw new MethodException("Unable to connect remotely or locally.");
	}
	
	private void getSiteBrokerTimeout()
	{
		if(getVistaSessionConfiguration().isBrokerKeepAliveEnabled())
		{
			logger.info("Getting broker timeout from site '" + getSite().getSiteNumber() + "'");
			
			try
			{
				VistaQuery query = VistaCommonQueryFactory.createGetVistaTimeout();
				
				String msg = getVistaConnection().call(query);
				logger.info("Got broker timeout value of '" + msg + "', setting value.");
				int brokerTime = Integer.parseInt(msg);
				// take half the value to ensure the keep alive event occurs before the timeout
				this.siteBrokerConnectionTimeout = (brokerTime / 2);
				logger.info("Set broker timeout time to '" + this.siteBrokerConnectionTimeout + "'.");
			}
			catch(IOException ioX)
			{
				logger.warn("IOException getting broker timeout, using default '" + defaultSiteBrokerConncetionTimeout + "'." + ioX);
				this.siteBrokerConnectionTimeout = defaultSiteBrokerConncetionTimeout;
			}
			catch(InvalidVistaCredentialsException ivcX)
			{
				logger.warn("InvalidVistaCredentialsException getting broker timeout, using default '" + defaultSiteBrokerConncetionTimeout + "'." + ivcX);
				this.siteBrokerConnectionTimeout = defaultSiteBrokerConncetionTimeout;	
			}
			catch(VistaMethodException vmX)
			{
				logger.warn("VistaMethodException getting broker timeout, using default '" + defaultSiteBrokerConncetionTimeout + "'." + vmX);
				this.siteBrokerConnectionTimeout = defaultSiteBrokerConncetionTimeout;
			}
			catch(Exception ex)
			{
				logger.warn("Exception getting broker timeout, using default '" + defaultSiteBrokerConncetionTimeout + "'." + ex);
				this.siteBrokerConnectionTimeout = defaultSiteBrokerConncetionTimeout;
			}
		}
	}
	
	private void updateSiteBseStatus(VistaSiteBseStatus status)
	{
		String siteNumber = getSite().getSiteNumber();
		logger.debug("Updating the site status for site '" + siteNumber + "' to status '" + status + "'.");
		VistaSessionBSECache.getVistaSessionBseCache().updateSiteStatus(siteNumber, status);
	}
	
	/**
	 * Checks the VistaSessionBSECache to determine if the site connecting to has been connected to before with BSE.
	 * If so and there was a failure, this will return false to indicate it failed and should not be attempted again.
	 * If a BSE connection was previously made or not tried yet, then this will return true to indicate BSE should be attempted
	 * 
	 * <br>
	 * <b>Note:</b> This method does not determine if credentials are available to make the BSE attempt (that is done elsewhere).
	 * 
	 * @return True if BSE should be attempted, false if it has previously failed.
	 */
	private boolean isSiteBseTryable()
	{
		String siteNumber = getSite().getSiteNumber();
		logger.info("Determining cached BSE status for site '" + siteNumber + "'.");
		VistaSiteBseStatus siteStatus = VistaSessionBSECache.getVistaSessionBseCache().getSiteStatus(siteNumber);
		String statusMsg = "";
		boolean result = true;
		if(siteStatus == VistaSiteBseStatus.bseFailed)
		{
			statusMsg = "BSE was previously attempted to this site and failed, will not attempt again.";
			result = false;
		}
		else
		{
			statusMsg = "BSE was previously successful or has not been attempted to this site, will attempt BSE.";
			result = true;
		}
		logger.info("Cached BSE status for site '" + siteNumber + "' is '" + siteStatus + "'. " + statusMsg);
		return result;
	}
	
	private VistaSessionConfiguration getVistaSessionConfiguration()
	{
		return VistaSessionConfiguration.getConfiguration();
	}
	
	/**
	 * Call an RPC on the Vista instance to which we are connected.
	 * 
	 * @throws IOException 
	 * @throws InvalidVistaCredentialsException 
	 * @throws VistaMethodException 
	 * @throws VistaException 
	 */
	public synchronized String call(VistaQuery vistaQuery) 
	throws IOException, InvalidVistaCredentialsException, VistaMethodException
	{
		displayVistaQuery(vistaQuery);
		String result = getVistaConnection().call(vistaQuery);
		displayVistaQueryResult(vistaQuery, result);
		this.lastUsedTime = System.currentTimeMillis();
		this.lastInternalTime = System.currentTimeMillis();
		return result;
	}
	
	/**
	 * Checks to see if the user for the session is fully signed on.  If an RPC call was made that caused an exception on
	 * the M side, this could kill the session on the VistA database which would mean no RPC calls can be made.  This 
	 * method calls an RPC that should only work if the session on the VistA side is still good.  If the session is still 
	 * good this will return true, if there was a problem this will return false. 
	 * 
	 * If the result is false then the session should be thrown away since it is no longer usable.
	 * 
	 * All necessary exception handling is done in this method, they only result is true or false. 
	 * @return
	 */
	public boolean isUserFullySignedOn()
	{
		try
		{
			logger.debug("Checking if user for VistA session (" + getSessionIndex() + ") is still signed on to site '" + getSite().getSiteNumber() + "'.");
			VistaQuery keepAliveQuery = VistaCommonQueryFactory.createGetUserInformationQuery();
			getVistaConnection().call(keepAliveQuery);
			this.lastInternalTime = System.currentTimeMillis();
			//logger.debug("Result from keep alive call '" + rtn + "'.");
			logger.debug("User is fully signed on, connection can be used");
			return true;
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.warn("InvalidVistaCredentialsException checking user session (" + getSessionIndex() + ") still signed on", ivcX);
			return false;
		}
		catch(IOException ioX)
		{
			logger.warn("IOException checking user session (" + getSessionIndex() + ") still signed on", ioX);
			return false;
		}
		catch(VistaMethodException vmX)
		{
			logger.warn("VistaMethodException checking user session (" + getSessionIndex() + ") still signed on", vmX);
			return false;
		}
		catch(Exception ex)
		{
			logger.warn("Exception checking user session (" + getSessionIndex() + ") still signed on", ex);
			return false;
		}
	}
	
	/**
	 * Make the appropriate RPC call to keep the broker connection alive. If there is any sort of 
	 * exception, this method will return false
	 * 
	 * @return True if the keep alive was successful, false if there was an error (should force a disconnect immediately)
	 */
	public boolean keepAlive()
	{
		long identityHashcode = getSessionIndex();
		try
		{
			logger.debug("Keeping VistA session (" + identityHashcode + ") alive to site '" + getSite().getSiteNumber() + "'.");
			VistaQuery keepAliveQuery = VistaCommonQueryFactory.createKeepAliveQuery();
			String rtn = getVistaConnection().call(keepAliveQuery);
			this.lastInternalTime = System.currentTimeMillis();
			logger.debug("Result from keep alive call '" + rtn + "'.");
			logger.debug("VistA session still alive");
			return true;
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.warn("InvalidVistaCredentialsException keeping VistA session (" + identityHashcode + ") alive", ivcX);
			return false;
		}
		catch(IOException ioX)
		{
			logger.warn("IOException keeping VistA session (" + identityHashcode + ") alive", ioX);
			return false;
		}
		catch(VistaMethodException vmX)
		{
			logger.warn("VistaMethodException keeping VistA session (" + identityHashcode + ") alive", vmX);
			return false;
		}
		catch(Exception ex)
		{
			logger.warn("Exception keeping VistA session (" + identityHashcode + ") alive", ex);
			return false;
		}
	}
	
	private void displayVistaQuery(VistaQuery vistaQuery)
	{
		logger.debug("RPC Request: "+ StringUtils.displayEncodedChars(vistaQuery.toString()));
	}
	
	private void displayVistaQueryResult(VistaQuery vistaQuery, String result)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("RPC [" + vistaQuery.getRpcName() + "] result:\n");
		sb.append(result);		
		logger.debug(sb.toString());
	}
	
	// ============================================================================================
	// Internal methods
	// ============================================================================================
	
	void disconnect(boolean disconnectImmediately)
	{
		// if a session was created on the VistA database for the user, close the session
		if(isSessionCreated() && !disconnectImmediately)
		{
			magLogoff();
		}
		if(disconnectImmediately)
			getVistaConnection().disconnectImmediately();
		else
			getVistaConnection().disconnect();
	}
	
	/**
	 * Close the Imaging session on VistA
	 */
	private void magLogoff()
	{
		logger.info("Disconnecting VistA connection, closing Imaging session");
		VistaQuery magLogoffQuery = VistaCommonQueryFactory.createMagLogoffQuery();
		try 
		{
			getVistaConnection().call(magLogoffQuery);
		}
		catch(IOException ioX)
		{
			logger.warn("Exception closing Imaging session during logoff, ignoring problem", ioX);
		}
		catch(VistaMethodException vmX)
		{
			logger.warn("Exception closing Imaging session during logoff, ignoring problem", vmX);
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.warn("Exception closing Imaging session during logoff, ignoring problem", ivcX);
		}
	}
	
	/**
	 * Creates a "local" broker connection to the supplied site. 
	 * A local connection is one that is established with the users
	 * access and verify codes rather than duz/site number/site name
	 * other than that the connections are the same.
	 */
	private void localLogin()
	throws ConnectionException, MethodException, MissingCredentialsException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		logger.info("localConnect TransactionContext (" + 
				(transactionContext != null ? transactionContext.getDisplayIdentity() : "null transaction context") + 
				").");
		
		boolean success = false;
		try 
		{
			// create the loginQuery first because it will validate that we have the
			// necessary fields in the transaction context to log in
			VistaQuery signonQuery = VistaCommonQueryFactory.createLocalSignonVistaQuery(transactionContext);
			
			// connect to the Vista server and attempt to call the signon RPC
			// this method may retry both the connection and the signon call
			// multiple times.
			connectAndSignon( signonQuery );

			// Send the Access and Verify codes, this method will throw InvalidCredentialsException
			// if the AV codes were not accepted.  
			presentAccessVerifyCredentials(transactionContext);
			
			//
			//setContext(MAG_WINDOWS_CONTEXT);
			setContext(securityContext);
			
			// if we get all the way to here then we have successfully connected
			success = true;
		}
		catch(MissingCredentialsException mcX)
		{
			logger.error(mcX);
			throw mcX;
		}
		catch(InvalidCredentialsException icX) 
		{
			logger.error(icX);
			throw icX;
		}
		catch(NumberFormatException nfX) 
		{
			logger.error( "Unable to properly parse Vista response.", nfX );
			throw new VistaParsingException(nfX);
		}
		catch(ArrayIndexOutOfBoundsException aioobX) 
		{
			logger.error( "Unable to properly parse Vista response.", aioobX );
			throw new VistaParsingException(aioobX);
		}
		catch(Exception ex) 
		{
			logger.error(ex);
			throw new VistaParsingException(ex);
		}
		finally 
		{
			if( !success && getVistaConnection() != null) 
			{
				try 
				{
					logger.info( "Connection failed in RpcBroker.localConnect(), disconnecting..." );
					// if the VistaConnection is still connected then the error occurred after the connectAndSignon and it needs to be disconnected
					// although maybe not immediately.  If there was an error during the connectAndSignon then it is disconnected immediately in there
					// and this disconnect should not be called on a VistaConnection that is not connected
					if(getVistaConnection().isConnected())
						getVistaConnection().disconnect();
				}
				catch(Exception eX) 
				{
					logger.warn( "Exception during handling disconnect, continuing ....", eX );
				}
			}
			logger.info("localConnect() complete");
		}
	}
	
	private void remoteBseLogin()
	throws ConnectionException, MethodException, InvalidVistaCredentialsException, SecurityCredentialsExpiredException
	{
		boolean success = false;			// will be true only when the connection and login RPCs have completed successfully
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("remoteBseLogin TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		// perform a login
		VistaQuery vm = VistaCommonQueryFactory.createBseSignonVistaQuery(transactionContext);
		
		try
		{
			// connect to the Vista server and attempt to call the signon RPC
			// this method may retry both the connection and the signon call
			// multiple times.
			String vistaResult = connectAndSignon(vm);
			validateRemoteLoginResponse(vistaResult);			
			// set the division for the user (if they have one assigned)
				
			// can't run this unless has proper context 			
			try 
			{
				//setContext(MAG_WINDOWS_CONTEXT);
				setContext(securityContext);
				
				setUserDivision(transactionContext.getUserDivision());		
				// JMW 4/22/09 - don't need to get DUZ here, if context set ok don't need DUZ
				//remoteDuz = getDUZ(transactionContext.getSsn());
				
				success = true;
			}
			catch(UnableToCreateContextException utccX) 
			{
				logger.warn("UnableToCreateContextException '" + getVistaConnection().getURL() + "', BSE does not allow adding context, cannot continue connection");
				throw new MethodException(utccX);				
			}
		}
		catch(BseFailedException bsefX)
		{
			// some sort of BSE error that was not expected, NOT a token expired exception
			throw new InvalidVistaCredentialsException(bsefX);
		}
		catch(SecurityCredentialsExpiredException sceX)
		{
			logger.error(sceX);
			throw sceX;
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.warn("Invalid credentials when doing BSE login", ivcX);
			throw ivcX;
		}
		catch (Exception ex)
		{
			logger.error(ex);
			throw new MethodException(ex);
		}
		finally 
		{
			if( ! success && getVistaConnection() != null ) 
				try 
				{
					logger.info("Unable to connect remotely with BSE, disconnecting");
					// if the VistaConnection is still connected then the error occurred after the connectAndSignon and it needs to be disconnected
					// although maybe not immediately.  If there was an error during the connectAndSignon then it is disconnected immediately in there
					// and this disconnect should not be called on a VistaConnection that is not connected
					if(getVistaConnection().isConnected())
						getVistaConnection().disconnect();
				}
				catch(Exception eX) {}
		}
	}
	
	/**
	 * Creates a "remote" broker connection to the supplied site. Credentials must include DUZ, SSN, Name, and Site.
	 * 
	 * @param site VistA site to connect to. Must include valid VistA server and VistA port information.
	 * @param credentials Credentials to use to connect to. This object must include the DUZ, SSN, name and site
	 * @throws ConnectionFailedException Occurs if the system cannot connect to the VistA Server 
	 * @throws MethodException Occurs if there is an exception executing an rpc during the connection
	 */
	private void remoteLogin() 
	throws ConnectionException, MethodException
	{
		boolean success = false;			// will be true only when the connection and login RPCs have completed successfully
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("remoteConnect TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		String remoteDuz = null;
		
		// perform a login		
		// When doing a CAPRI login, an empty XUS SIGNON SETUP rpc must be called first to prime the
		// system.  Not really sure why, but thats how it is - don't apply logic where it doesn't belong!
		VistaQuery [] queries = new VistaQuery[2];
		queries[0] = VistaCommonQueryFactory.createRemoteSignonEmptyVistaQuery();;
		queries[1] = VistaCommonQueryFactory.createRemoteSignonVistaQuery(transactionContext);
		
		try
		{
			// connect to the Vista server and attempt to call the signon RPC
			// this method may retry both the connection and the signon call
			// multiple times.
			String vistaResult = connectAndSignon(queries);
			validateRemoteLoginResponse(vistaResult);
			// set the division for the user (if they have one assigned)
			
			// setting the division doesn't seem to work with CAPRI login method... not sure if that is intentional or true, but always got errors about context not being set
			//setUserDivision();
			// can't run this unless has proper context 			
			try 
			{
				//setContext(MAG_WINDOWS_CONTEXT);
				setContext(securityContext);
				// JMW 4/22/09 - don't need to get DUZ here, if context set ok don't need DUZ
				//remoteDuz = getDUZ(transactionContext.getSsn());
				
				success = true;
			}
			catch(UnableToCreateContextException utccX) 
			{
				logger.warn("UnableToCreateContextException '" + getVistaConnection().getURL() + "'.");
				try 
				{
					// user might not have MAG WINDOWS context assigned to them
					setContext(CAPRI_CONTEXT);
					String desiredContextIEN = getContextIEN(securityContext);
					
					remoteDuz = getDUZ(transactionContext.getSsn());
					//getVistaConnection().setUid(remoteDuz);

					assignOption(VistaCommonQueryFactory.MENU_SUBSCRIPT, desiredContextIEN, remoteDuz);

					try
					{
						Thread.sleep(2000);
					}
					catch (InterruptedException ie)
					{
						throw new MethodException(ie);
					}

					String capriIEN = getContextIEN(CAPRI_CONTEXT);
					if (!capriIEN.equals(""))
					{
						String capriMenuOptionNumber = getOptionIEN(VistaCommonQueryFactory.MENU_SUBSCRIPT, capriIEN, remoteDuz);
						if( ! "".equals(capriMenuOptionNumber) )
							removeOption(VistaCommonQueryFactory.MENU_SUBSCRIPT, capriMenuOptionNumber, remoteDuz);
					}
					setContext(securityContext);				
					success = true;
				}
				catch(UnableToCreateContextException utccX2) 
				{
					logger.warn("Unable to create context, " + utccX.toString());
					throw new MethodException(utccX2);
				}
			}
		}
		catch (Exception ex)
		{
			logger.error(ex);
			throw new MethodException(ex);
		}
		finally 
		{
			if( ! success && getVistaConnection() != null ) 
				try 
				{
					logger.info("Unable to connect remotely, disconnecting");
					// if the VistaConnection is still connected then the error occurred after the connectAndSignon and it needs to be disconnected
					// although maybe not immediately.  If there was an error during the connectAndSignon then it is disconnected immediately in there
					// and this disconnect should not be called on a VistaConnection that is not connected
					if(getVistaConnection().isConnected())
						getVistaConnection().disconnect();
				}
				catch(Exception eX) {}
		}
	}
	
	/**
	 * If doing a remote login (BSE or CAPRI), the 6th piece should be a '1' to indicate a silent login was done properly.
	 * @param vistaResponse
	 * @throws InvalidVistaCredentialsException
	 */
	private void validateRemoteLoginResponse(String vistaResult)
	throws InvalidVistaCredentialsException
	{
		if(vistaResult == null)
			throw new InvalidVistaCredentialsException("Result of remote login RPC was null, indicates failed remote login attempt.");
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		if(lines == null)
			throw new InvalidVistaCredentialsException("Result of remote login RPC was null array of lines, indicates failed remote login attempt.");
		if(lines.length < 6)
			throw new InvalidVistaCredentialsException("Result of remote login RPC does not contain at least 6 lines of response, indicates failed remote login attempt.");
		if(!("1".equals(lines[5].trim())))
			throw new InvalidVistaCredentialsException("Result of 6th line of remote login RPC '" + lines[5].trim() + "', not '1', indicates failed remote login attempt.");
	}
	
	private void removeOption(String subscript, String optNum, String remoteDuz) 
	throws Exception 
	{
		logger.info("removeOption TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		VistaQuery msg = VistaCommonQueryFactory.createRemoveOptionVistaQuery(subscript, optNum, remoteDuz);
		String rtn = getVistaConnection().call(msg);
		if (!rtn.equals("[Data]")) {
			throw new Exception("ERROR: " + rtn);
		}
	}
	
	/*
	private void setUserDivision()
	throws MethodException // not sure if should be connection or method exception
	{		
		// get the Site that we are connected to
		Site site = getSite();
		String connectedSiteNumber = site.getSiteNumber();
		logger.info("Setting user division, local site number is '" + connectedSiteNumber + "', TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaQuery vm = VistaCommonQueryFactory.createGetDivisionQuery();
		String rtn = "";
		try
		{
			rtn = getVistaConnection().call(vm);
			String [] lines = StringUtils.Split(rtn, StringUtils.NEW_LINE);
			if((lines != null) && (lines.length > 0))
			{
				int divisionCount = Integer.parseInt(lines[0].trim());
				if(divisionCount > 0)
				{
					for(int lineNumber = 1; lineNumber < lines.length; lineNumber++)
					{
						String line = lines[lineNumber];
						
						String [] pieces = StringUtils.Split(line, StringUtils.CARET);
						String divisionNumber = pieces[2];
						boolean defaultDivision = false;
						
						if(pieces.length > 3)
						{
							if("1".equals(pieces[3]))
								defaultDivision = true;
						}
						
						
//						if(connectedSiteNumber.equals(divisionNumber))
//						{
//							setUserDivision(line.trim());
//							break;
//						}
						
						if(defaultDivision)
						{
							setUserDivision(divisionNumber);
							break;
						}
					}
				}
				logger.info("Did not find assigned division for site '" + connectedSiteNumber + "' for user, not explicitly setting division.");
			}
			else
			{
				// this should use the kernel default division
				logger.info("User is not assigned at least 1 division, not setting division.");
			}
		}
		catch(Exception ex)
		{
			logger.error("Error setting user division", ex);
			throw new MethodException("Error setting user division", ex);			
		}
	}*/
	
	private void setUserDivision(String division)
	throws IOException, InvalidVistaCredentialsException, VistaMethodException, ConnectionException
	{
		if((division != null) && (division.length() > 0))
		{
			logger.info("Setting user division to '" + division + "'.");
			VistaQuery vm = VistaCommonQueryFactory.createSetDivisionQuery(division);
			String result = getVistaConnection().call(vm);
			if((result == null) || (!result.equals("1")))
			{
				throw new ConnectionException("Attempted to set user division to '" + division + "', result was not expected value of '1' but was value '" + result + "'.");
			}
		}
		else
		{
			logger.debug("User division is null or empty, not setting.");
		}		
	}

	/**
	 * 
	 * @param context
	 * @throws UnableToCreateContextException
	 */
	private void setContext(String context) 
	throws UnableToCreateContextException 
	{
		logger.info(
			"setContext(" + context + 
			") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ")."
		);
		
        VistaQuery vm = VistaCommonQueryFactory.createSetContextVistaQuery(context);
        String rtn = "";
        try 
        {
        	rtn = getVistaConnection().call(vm);
        }
        catch(Exception ex) 
        {
        	throw new UnableToCreateContextException(ex);
        }
        if( !rtn.equals("1") ) 
        	throw new UnableToCreateContextException("Unable to set context [" + context + "]");
	}
	
	/**
	 * Create and call an RPC to get the users DUZ (account ID) from VistA.
	 * 
	 * @param userSSN
	 * @return
	 * @throws VistaMethodException 
	 * @throws InvalidVistaCredentialsException 
	 * @throws Exception
	 */
	private String getDUZ(String userSSN) 
	throws VistaConnectionException, IOException, InvalidVistaCredentialsException, VistaMethodException
	{
		logger.info("getDUZ TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		VistaQuery msg = VistaCommonQueryFactory.createGetDUZVistaQuery(userSSN);
		String rtn = call(msg);
		if (!StringUtils.isNumeric(rtn))
			throw new VistaConnectionException("Non-numeric DUZ");
		
		return rtn;
	}

	/**
	 * 
	 * @param context
	 * @return
	 * @throws VistaMethodException 
	 * @throws InvalidVistaCredentialsException 
	 * @throws Exception
	 */
	private String getContextIEN(String context) 
	throws VistaConnectionException, IOException, InvalidVistaCredentialsException, VistaMethodException
	{
		logger.info("getContextIEN TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		VistaQuery msg = VistaCommonQueryFactory.createGetContextIENVistaQuery(context);
		String rtn = call(msg);
		if( !StringUtils.isNumeric(rtn) )
			throw new VistaConnectionException("Non-numeric context IEN for " + context);
		return rtn;
	}
	
	/**
	 * @param vm
	 * @return
	 * @throws Exception
	 * @throws MethodException
	 */
	private String connectAndSignon(VistaQuery ... signonQueries) 
	throws ConnectionException, SecurityCredentialsExpiredException, BseFailedException
	{
		String vistaResult = null;
		
		logger.info("connectAndSignon TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		int retryCount;
		Exception signonException = null;
		for(retryCount=0; retryCount<this.failedSignonRetryMax; ++retryCount)
		{
			try
			{
				long startConnect = System.currentTimeMillis();
				logger.info("Connecting to Vista ...");
				getVistaConnection().connect();
				logger.info("Connected to Vista in " + (System.currentTimeMillis() - startConnect) + " milliseconds, signing on...");
				
				long startSignon = System.currentTimeMillis();
				
				if(signonQueries != null)
				{
					int queryCount = 0;
					logger.info("Calling '" + signonQueries.length + "' signon queries.");
					for(VistaQuery signonQuery : signonQueries)
					{
						vistaResult = getVistaConnection().call(signonQuery);
						logger.debug("Result of VistA signon query [" + queryCount + "]: " + vistaResult);
						queryCount++;
					}
				}
				else
				{
					logger.info("Signon queries are null, should NOT happen - signon probably won't work!");
				}
				
				
				
				//System.out.println("Vista Result:\n" + vistaResult);
				if (vistaResult == null)
					throw new VistaConnectionException("Null result returned from Vista in response to RPC_SIGNON call.");
				logger.info("Signed on to Vista in " + (System.currentTimeMillis() - startSignon) + " milliseconds.");
				
				break;
			}
			catch(InvalidVistaCredentialsException ivcX)
			{
				// remember the first exception
				signonException = signonException == null ? ivcX : signonException;
				
				// disconnect and ignore all errors
				try{getVistaConnection().errorDisconnect();}
				catch(Throwable t){}
				
				// InvalidVistaCredentialsException might occur if the BSE token is bad, if so then 
				// don't retry (since it still won't work)
				if((ivcX.getMessage() != null) && 
					(ivcX.getMessage().startsWith("BSE ERROR - BSE TOKEN EXPIRED")))
				{
					String msg = "RPC_SIGNON Broker signon failed, BSE TOKEN EXPIRED";
					logger.error(msg, ivcX);
					throw new SecurityCredentialsExpiredException(msg);
				}
				else if((ivcX.getMessage() != null)&& 
					(ivcX.getMessage().startsWith("BSE ERROR")))
				{
					// if there is another type of error for BSE, throw an exception
					String msg = "RPC_SIGNON Broker signon failed, " + ivcX.getMessage();
					logger.error(msg, ivcX);
					throw new BseFailedException(msg);
				}
				// delay for a random amount of time from 1 to 2 seconds
				long randomDelay = (long)(Math.random() * (FAILED_SIGNIN_MAX_WAIT_TIME - FAILED_SIGNON_MIN_WAIT_TIME) + FAILED_SIGNON_MIN_WAIT_TIME);
				logger.warn(
					"RPC_SIGNON failed [" + ivcX.getClass().getSimpleName() + ": " + ivcX.getMessage() + 
					"] (" + retryCount + "), retrying in " + randomDelay + " milliseconds."
				);
				try{Thread.sleep(randomDelay);}
				catch(InterruptedException iX){}
			}
			catch (Exception ex)
			{
				// remember the first exception
				signonException = signonException == null ? ex : signonException;
				
				// disconnect and ignore all errors
				try{getVistaConnection().errorDisconnect();}
				catch(Throwable t){}
				
				// delay for a random amount of time from 1 to 2 seconds
				long randomDelay = (long)(Math.random() * (FAILED_SIGNIN_MAX_WAIT_TIME - FAILED_SIGNON_MIN_WAIT_TIME) + FAILED_SIGNON_MIN_WAIT_TIME);
				logger.warn(
					"RPC_SIGNON failed [" + ex.getClass().getSimpleName() + ": " + ex.getMessage() + 
					"] (" + retryCount + "), retrying in " + randomDelay + " milliseconds."
				);
				try{Thread.sleep(randomDelay);}
				catch(InterruptedException iX){}
			}
		}
		
		if(retryCount >= FAILED_SIGNON_MAX_COUNT)
		{
			logger.error("RPC_SIGNON failed, retry count exceeded.");
			throw new VistaConnectionException(signonException);
		}
		return vistaResult;
	}
	
	/**
	 * Send the access and verify codes from the TransactionContext to Vista and
	 * parse the results string.
	 * 
	 * @param transactionContext
	 * @throws InvalidCredentialsException
	 * @throws IOException
	 * @throws VistaMethodException 
	 * @throws VistaException
	 */
	private void presentAccessVerifyCredentials(TransactionContext transactionContext) 
	throws InvalidCredentialsException, IOException, VistaMethodException, MissingCredentialsException
    {
	    VistaQuery avCodeQuery = VistaCommonQueryFactory.createAVCodeVistaQuery(transactionContext);
	    String vistaResult;
	    
	    try
        {
	        vistaResult = getVistaConnection().call(avCodeQuery);
        } 
	    catch (InvalidVistaCredentialsException e)
        {
	    	throw new InvalidCredentialsException(e.getMessage());
        }
	    
	    // validate user credentials login information
	    String [] authResults = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
	    int authValue = Integer.parseInt(authResults[0].trim());
	    if(authValue <= 0) 
	    {
	    	logger.debug("Authentication failed [" + vistaResult + "]");
	    	if(authResults.length >= 4 && authResults[3] != null)
	    		transactionContext.addDebugInformation("Authentication failed with message [" + authResults[3].trim() + "]");
	    	else
	    		transactionContext.addDebugInformation("Authentication failed with failure Code [" + authResults[2].trim() + "]");
	    	// failed authentication, throw an InvalidCredentialsException
	    	// NOTE: connection will be disconnected later in the finally block
	    	if(authResults[2].trim().equals("1"))
	    		throw new InvalidCredentialsException("Expired Verify Code");
	    	else
	    	{
	    		// JMW 7/25/2012 use the real message from VistA for the exception
	    		// explanation for authentication failure is in authResults[3]
	    		if(authResults.length >= 4 && authResults[3] != null)
	    		{
	    			throw new InvalidCredentialsException(authResults[3].trim());
	    		}
	    		else
	    		{
	    			throw new InvalidCredentialsException("Invalid Access/Verify Codes");
	    		}
	    	}
	    }
    }
	
	/**
	 * 
	 * @param subscript
	 * @param contextIEN
	 * @return
	 * @throws Exception
	 */
	private String assignOption(String subscript, String contextIEN, String remoteDuz)
    throws Exception
	{
		logger.info("assignOption TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		VistaQuery msg = VistaCommonQueryFactory.createAssignOptionVistaquery(subscript, contextIEN, remoteDuz);
		String rtn = getVistaConnection().call(msg);
		return VistaCommonTranslator.parseOptionNumber(rtn);
	}
	
	/**
	 * 
	 * @param subscript
	 * @param contextIen
	 * @return
	 * @throws Exception
	 */
	private String getOptionIEN(String subscript, String contextIen, String remoteDuz) 
	throws Exception 
	{
		logger.info("getOptionIEN TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		VistaQuery msg = VistaCommonQueryFactory.createGetOptionVistaQuery(subscript, contextIen, remoteDuz);
		String rtn = getVistaConnection().call(msg);
		if (!StringUtils.isNumeric(rtn)) {
			throw new Exception("Non-numeric option IEN for " + subscript);
		}
		return rtn;
	}

	/**
	 * Determines if an Imaging session has been created for this VistA Connection
	 * @return
	 */
	public boolean isSessionCreated() {
		return sessionCreated;
	}

	/**
	 * Sets the session created to indicate if an Imaging session has been created during this VistA connection
	 * @param sessionCreated
	 */
	public void setSessionCreated(boolean sessionCreated) {
		this.sessionCreated = sessionCreated;
	}

	/**
	 * @return the vistaRadLoggedIn
	 */
	public boolean isVistaRadLoggedIn()
	{
		return vistaRadLoggedIn;
	}

	/**
	 * @param vistaRadLoggedIn the vistaRadLoggedIn to set
	 */
	public void setVistaRadLoggedIn(boolean vistaRadLoggedIn) 
	{
		this.vistaRadLoggedIn = vistaRadLoggedIn;
	}	
	
	public static ImagingSecurityContextType getImagingSecurityContextType()
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		String imagingSecurityContextString = transactionContext.getImagingSecurityContextType();
		if((imagingSecurityContextString == null) || (imagingSecurityContextString.length() == 0))
		{
			logger.debug("No imaging security context found in TransactionContext, using default '" + defaultSecurityContextType + "'");
			return defaultSecurityContextType;
		}
		try
		{
			return ImagingSecurityContextType.valueOf(imagingSecurityContextString);
		}
		catch(IllegalArgumentException irX)
		{
			logger.error("Error parsing security context from TransactionContext, " + irX.getMessage());
			return defaultSecurityContextType;	
		}
	}
	
	private boolean isBseCredentialsInContext(TransactionContext transactionContext)
	{
		if(transactionContext == null)
			return false;
		
		if(transactionContext.getBrokerSecurityToken() == null)
			return false;
		
		if(transactionContext.getBrokerSecurityToken().length() <= 0)
			return false;
		
		return true;
	}

	/**
	 * @return the sessionIndex
	 */
	public long getSessionIndex()
	{
		return sessionIndex;
	}

	/**
	 * @param sessionIndex the sessionIndex to set
	 */
	public void setSessionIndex(long sessionIndex)
	{
		this.sessionIndex = sessionIndex;
	}
}
