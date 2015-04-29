package gov.va.med.imaging.tomcat.vistarealm.broker;

import gov.va.med.imaging.tomcat.vistarealm.DataSource;
import gov.va.med.imaging.tomcat.vistarealm.StringUtils;
import gov.va.med.imaging.tomcat.vistarealm.VistaRealmPrincipal;
import gov.va.med.imaging.tomcat.vistarealm.VistaRealmSite;
import gov.va.med.imaging.tomcat.vistarealm.exceptions.ConnectionFailedException;
import gov.va.med.imaging.tomcat.vistarealm.exceptions.CredentialsExpiredException;
import gov.va.med.imaging.tomcat.vistarealm.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.tomcat.vistarealm.exceptions.MethodException;
import gov.va.med.imaging.tomcat.vistarealm.exceptions.RpcException;
import gov.va.med.imaging.tomcat.vistarealm.exceptions.UnableToConnectToBrokerException;
import gov.va.med.imaging.tomcat.vistarealm.exceptions.UnableToCreateContextException;
import gov.va.med.imaging.url.vista.VistaConnection;
import gov.va.med.imaging.url.vista.VistaQuery;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class NewRpcBroker 
implements Runnable
{
	private static final int MAX_DISCONNECT_THREAD = 32;
	private static final int DISCONNECT_THREAD_COUNT = 5;
	private static final int DISCONNECT_QUEUE_SIZE = 128;

	private static Logger logger = Logger.getLogger(NewRpcBroker.class);
	
	private static BlockingQueue<Runnable> disconnectQueue;
	private static ThreadPoolExecutor disconnectExecutor;

	private final static String MAG_WINDOWS_CONTEXT = "MAG WINDOWS";
	private final static String CPRS_CONTEXT = "OR CPRS GUI CHART";
	
	private final static String DATASOURCE_PROTOCOL = "VISTA";
	private final static String DATASOURCE_MODALITY = "HIS";
	
	private final static String RPC_SIGNON = "XUS SIGNON SETUP";
	private final static String RPC_AV_CODE = "XUS AV CODE";
	private final static String RPC_CREATE_CONTEXT = "XWB CREATE CONTEXT";
	private final static String RPC_GET_VARIABLE_VALUE = "XWB GET VARIABLE VALUE";
	private final static String RPC_GET_USER_INFO = "ORWU USERINFO";
	
	private final static String RPC_MAG_LOGOFF = "MAGG LOGOFF";
	
	private final static String RPC_XUS_SET_VISITOR = "XUS SET VISITOR";
	private final static String RPC_MAG_BROKER_SECURITY = "MAG BROKER SECURITY";
	
	private final static int FAILED_SIGNON_MAX_COUNT = 10;		// maximum number of times to rety an RPC_SIGNON
	private final static int FAILED_SIGNON_MIN_WAIT_TIME = 1000; // maximum number of ms to wait between retrying the RPC_SIGNON
	private final static int FAILED_SIGNIN_MAX_WAIT_TIME = 3000; // maximum number of ms to wait between retrying the RPC_SIGNON
	
	private final static String VIX_BSE_APP_NAME = "VISTA IMAGING VIX";

	// the disconnects are done in a worker thread pool
	static
	{
		disconnectQueue = new ArrayBlockingQueue<Runnable>(DISCONNECT_QUEUE_SIZE);
		disconnectExecutor = new ThreadPoolExecutor(
			DISCONNECT_THREAD_COUNT, MAX_DISCONNECT_THREAD, 10L, TimeUnit.SECONDS, disconnectQueue);
	}
	
	public static BlockingQueue<Runnable> getDisconnectQueue()
	{
		return disconnectQueue;
	}

	public static ThreadPoolExecutor getDisconnectExecutor()
	{
		return disconnectExecutor;
	}
	
	private static String workstationId = null;
	public static String getWorkstationId() {
		if(workstationId == null) {
			try {
				InetAddress addr = InetAddress.getLocalHost();
				workstationId = addr.getHostName();
			}
			catch(UnknownHostException uhX) {
				logger.warn("Unable to get local hostname, using default value");
				workstationId = "DOD-VA_WebService";
			}
			logger.info("Local host name set to [" + workstationId + "]");
			return workstationId;
		}
		return workstationId;
	}	
	
	private VistaRealmPrincipal credentials;
	
	private DataSource ds = null;
	//private IConnection connection = null;
	private VistaConnection connection = null;
	
	public NewRpcBroker()
	{
		super();
	}
	

	/**
	 * Initiates a connection to the database without using any VistA Imaging RPC calls
	 * @param site
	 * @param credentials
	 * @throws ConnectionFailedException
	 * @throws InvalidCredentialsException
	 * @throws MethodException
	 */
	public void localConnectWithoutImaging(VistaRealmSite site, VistaRealmPrincipal credentials) 
	throws ConnectionFailedException, InvalidCredentialsException, MethodException 
	{
		logger.info("localConnectWithoutImaging started");
		this.credentials = credentials;
		
		ds = new DataSource();
		ds.setSource(site.getVistaServer());
		ds.setPort(site.getVistaPort());
		ds.setProtocol(DATASOURCE_PROTOCOL);
		ds.setModality(DATASOURCE_MODALITY);
		ds.setUid(credentials.getAccessCode());
		ds.setPassword(credentials.getVerifyCode());
		ds.setContext(CPRS_CONTEXT);

		//connection = new VistaConnection(ds);
		
		
		String vistaResult = null;
		boolean success = false;
		try 
		{			
			URL url = new URL("vista://" + site.getVistaServer() + ":" + site.getVistaPort());
			connection = new VistaConnection(url);
			VistaQuery vm = new VistaQuery(RPC_SIGNON);
			
			connectAndSignon(vm);
			
			vm.clear();
			vm.setRpcName(RPC_AV_CODE);
			vm.addEncryptedParameter(VistaQuery.LITERAL, ds.getUid() + ';' + ds.getPassword());
			vistaResult = connection.call(vm);
			
			// validate user credentials login information
			String [] authResults = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
			int authValue = Integer.parseInt(authResults[0].trim());
			if(authValue <= 0) 
			{
				// failed authentication, disconnect broker
				connection.disconnectImmediately();
				if(authResults[2].trim().equals("1"))
					throw new CredentialsExpiredException("Expired Verify Code");
				else
					throw new InvalidCredentialsException("Invalid Access/Verify Codes");
			}
			
			setContext(CPRS_CONTEXT);
			setUserProperties();
			credentials.setSiteNumber(site.getSiteNumber());
			credentials.setSiteName(site.getSiteName());
			
			success = true;			
		}
		catch(UnableToCreateContextException utccX) 
		{
			logger.error(utccX);
			throw new MethodException(utccX);
		}
		catch(RpcException rpcX) 
		{
			logger.error(rpcX);
			throw new MethodException(rpcX);
		}
		catch(InvalidCredentialsException icX) 
		{
			logger.error(icX);
			throw icX;
		}
		catch(NumberFormatException nfX) 
		{
			logger.error( "Unable to properly parse Vista response.", nfX );
			throw new MethodException(nfX);
		}
		catch(ArrayIndexOutOfBoundsException aioobX) 
		{
			logger.error( "Unable to properly parse Vista response.", aioobX );
			throw new MethodException(aioobX);
		}
		catch(Exception ex) 
		{
			logger.error(ex);
			throw new MethodException(ex);
		}
		finally 
		{
			if( !success && connection != null ) 
			{
				try 
				{
					logger.warn( "Connection failed in RpcBroker.localConnect(), disconnecting..." );
					connection.disconnectImmediately();
				}
				catch(Exception eX) 
				{
					logger.warn( "Exception during error handling disconnect, continuing ....", eX );
				}
			}
			logger.info("localConnect() complete");
		}
	}
	
	/**
	 * 
	 * @param site
	 * @param credentials
	 * @throws ConnectionFailedException
	 * @throws InvalidCredentialsException
	 * @throws MethodException
	 */
	public void localConnect(VistaRealmSite site, VistaRealmPrincipal credentials) 
	throws ConnectionFailedException, InvalidCredentialsException, MethodException 
	{
		logger.info("LocalConnect started to " + site.getVistaServer() + ":" + site.getVistaPort());
		this.credentials = credentials;
		
		ds = new DataSource(
			site.getVistaServer(), 
			site.getVistaPort().intValue(), 
			DATASOURCE_PROTOCOL, 
			DATASOURCE_MODALITY,
			credentials.getAccessCode(),
			credentials.getVerifyCode(),
			MAG_WINDOWS_CONTEXT);

		
		
		String vistaResult = null;
		boolean success = false;
		try 
		{	
			URL url = new URL("vista://" + site.getVistaServer() + ":" + site.getVistaPort());
			connection = new VistaConnection(url);
			VistaQuery vm = new VistaQuery(RPC_SIGNON);
			
			connectAndSignon(vm);
			
			vm.clear();
			vm.setRpcName(RPC_AV_CODE);
			vm.addEncryptedParameter(VistaQuery.LITERAL, ds.getUid() + ';' + ds.getPassword());
			vistaResult = connection.call(vm);
			
			// validate user credentials login information
			String [] authResults = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
			
			// JMW 4/19/2014 Raptor - in some weird case the first value was the user SSN
			// caused an exception when trying to parse into an integer (too large)
			// just want to check for a 0 value
			
			if(authResults[2].trim().equals("1"))
			{
				connection.disconnectImmediately();
				throw new CredentialsExpiredException("Expired Verify Code");
			}
			else if(authResults[0].trim().equals("0"))
			{
				connection.disconnectImmediately();
				throw new InvalidCredentialsException("Invalid Access/Verify Codes");
			}
			
			/*
			int authValue = Integer.parseInt(authResults[0].trim());
			if(authValue <= 0) 
			{
				// failed authentication, disconnect broker
				connection.disconnectImmediately();
				if(authResults[2].trim().equals("1"))
					throw new CredentialsExpiredException("Expired Verify Code");
				else
					throw new InvalidCredentialsException("Invalid Access/Verify Codes");
			}*/
			
			setContext(CPRS_CONTEXT);
			setUserProperties();
			credentials.setSiteNumber(site.getSiteNumber());
			credentials.setSiteName(site.getSiteName());
		
			//setContext(ds.getContext());
			setContext(MAG_WINDOWS_CONTEXT);
			success = true;
			
		}
		catch(UnableToCreateContextException utccX) 
		{
			logger.error(utccX);
			throw new MethodException(utccX);
		}
		catch(RpcException rpcX) 
		{
			logger.error(rpcX);
			throw new MethodException(rpcX);
		}
		catch(InvalidCredentialsException icX) 
		{
			logger.error(icX);
			throw icX;
		}
		catch(NumberFormatException nfX) 
		{
			logger.error( "Unable to properly parse Vista response.", nfX );
			throw new MethodException(nfX);
		}
		catch(ArrayIndexOutOfBoundsException aioobX) 
		{
			logger.error( "Unable to properly parse Vista response.", aioobX );
			throw new MethodException(aioobX);
		}
		catch(Exception ex) 
		{
			logger.error(ex);
			throw new MethodException(ex);
		}
		finally 
		{
			if( !success && connection != null ) 
			{
				try 
				{
					logger.warn( "Connection failed in RpcBroker.localConnect(), disconnecting..." );
					connection.disconnect();
				}
				catch(Exception eX) 
				{
					logger.warn( "Exception during error handling disconnect, continuing ....", eX );
				}
			}
			logger.info("localConnect() complete");
		}
	}
	
	/**
	 * 
	 * @param site
	 * @param credentials
	 * @throws ConnectionFailedException
	 * @throws MethodException
	 */
	/*
	 // this method is not used in the realm (can't think of a reason it would be used)
	public void remoteConnect(VistaRealmSite site, VistaRealmPrincipal credentials) 
	throws ConnectionFailedException, MethodException 
	{
		this.credentials = credentials;
		boolean success = false;
		
		ds = new DataSource();
		ds.setSource(site.getVistaServer());
		ds.setPort(site.getVistaPort());
		ds.setProtocol(DATASOURCE_PROTOCOL);
		ds.setModality(DATASOURCE_MODALITY);
		
		connection = new VistaConnection(ds);
		
		// perform a login
		VistaQuery vm = new VistaQuery(RPC_SIGNON);
		vm.addParameter(
				VistaQuery.LITERAL, 
				"-31^DVBA_^" + credentials.getSsn() + 
				"^" + credentials.getFullName() + 
				"^" + credentials.getSiteName() + 
				"^" + credentials.getSiteNumber() + 
				"^" + credentials.getDuz() + "^No Phone");
		
		String remoteDUZ = "";
		try
		{
			connectAndSignon(vm);
			
			// can't run this unless has proper context 			
			try {
				setContext(MAG_WINDOWS_CONTEXT);
				remoteDUZ = getDUZ(credentials.getSsn());
				remoteDuz = remoteDUZ;
				((VistaConnection)connection).setUid(remoteDuz);
				success = true;
			}
			catch(UnableToCreateContextException utccX) {
				try {
					// user might not have MAG WINDOWS context assigned to them
					setContext(CAPRI_CONTEXT);
					String magWindowsContextIEN = getContextIEN(MAG_WINDOWS_CONTEXT);
					
					remoteDUZ = getDUZ(credentials.getSsn());
					remoteDuz = remoteDUZ;
					((VistaConnection)connection).setUid(remoteDuz);

					assignOption(MENU_SUBSCRIPT, magWindowsContextIEN);

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
						String capriMenuOptionNumber = getOptionIEN(MENU_SUBSCRIPT, capriIEN);
						if (!capriMenuOptionNumber.equals(""))
						{
							removeOption(MENU_SUBSCRIPT, capriMenuOptionNumber);
						}
					}
					setContext(MAG_WINDOWS_CONTEXT);				
					success = true;
				}
				catch(UnableToCreateContextException utccX2) {
					logger.warn("Unable to create context, " + utccX.toString());
					throw new MethodException(utccX2);
				}
			}
		}
		catch (Exception ex)
		{
			throw new MethodException(ex);
		}
		finally 
		{
			if(! success && connection != null) {
				try 
				{
					logger.warn("Unable to connect remotely, disconnecting");
					connection.disconnect();
				}
				catch(Exception eX) {}
			}
		}
	}
	*/

	/**
	 * @param vm
	 * @return
	 * @throws Exception
	 * @throws MethodException
	 */
	private String connectAndSignon(VistaQuery vm) 
	throws Exception, MethodException
	{
		String vistaResult = null;
		
		int retryCount;
		Exception signonException = null;
		for(retryCount=0; retryCount<FAILED_SIGNON_MAX_COUNT; ++retryCount)
		{
			connection.connect();
			
			try
			{
				vistaResult = connection.call(vm);
				if (vistaResult == null)
					throw new UnableToConnectToBrokerException("Null result returned from Vista in response to RPC_SIGNIN call.");
				
				break;
			}
			catch (Exception ex)
			{
				// remember the first exception
				signonException = signonException == null ? ex : signonException;
				
				// disconnect and ignore all errors
				try{connection.disconnect();}
				catch(Throwable t){}
				
				// delay for a random amount of time from 1 to 2 seconds
				long randomDelay = (long)(Math.random() * (FAILED_SIGNIN_MAX_WAIT_TIME - FAILED_SIGNON_MIN_WAIT_TIME) + FAILED_SIGNON_MIN_WAIT_TIME);
				logger.warn("RPC_SIGNON failed (" + retryCount + "), retrying in " + randomDelay + " milliseconds");
				try{Thread.sleep(randomDelay);}
				catch(InterruptedException iX){}
			}
		}
		
		if(retryCount >= FAILED_SIGNON_MAX_COUNT)
		{
			logger.error("RPC_SIGNON failed, retry count exceeded.");
			throw new MethodException(signonException);
		}
		return vistaResult;
	}
	
	
	private final static String RPC_MAG_USER_KEYS = "MAGGUSERKEYS";
	public String[] getUserKeys() 
	throws RpcException
	{
		VistaQuery vm = new VistaQuery(RPC_MAG_USER_KEYS);

		try
		{
			String rtn = connection.call(vm);

			String[] keys = StringUtils.Split(rtn, StringUtils.NEW_LINE);
			
			// trim whitespace, including CR and/or LF characters
			if(keys != null)
				for(int index=0; index<keys.length; ++index)
					keys[index] = keys[index].trim();

			return keys;
		}
		catch (Exception ex)
		{
			throw new RpcException(ex);
		}
	}	

	private void setContext(String context) throws UnableToCreateContextException {
        VistaQuery vm = new VistaQuery(RPC_CREATE_CONTEXT);
        vm.addEncryptedParameter(VistaQuery.LITERAL, context);
        String rtn = "";
        try {
        	rtn = connection.call(vm);
        }
        catch(Exception ex) {
        	throw new UnableToCreateContextException(ex);
        }
        if (!rtn.equals("1")) 
        {
        	throw new UnableToCreateContextException("Unable to set context [" + context + "]");
        }
	}
	
	private void setUserProperties() throws RpcException {
		VistaQuery vm = new VistaQuery(RPC_GET_USER_INFO);
        String rtn = null;
        try {
        	rtn = connection.call(vm);
        }
        catch(Exception ex) {
        	throw new RpcException(ex);
        }
        if (rtn == null) 
        {
        	throw new RpcException("Unable to get user info");
            //throw new DaoException("Unable to get user info");
        }
		String[] parts = StringUtils.Split(rtn,StringUtils.CARET);
        try 
		{
        	credentials.setDuz(parts[0]);
        	credentials.setFullName(parts[1]);
        } 
        catch (Exception ex) 
		{
        	throw new RpcException(ex);
            //throw new DaoException(ex);
        }
        vm = new VistaQuery(RPC_GET_VARIABLE_VALUE);
        String arg = "@\"^VA(200," + credentials.getDuz() + ",1)\"";
        vm.addParameter(VistaQuery.REFERENCE,arg);
        try {
        	rtn = connection.call(vm);
        }
        catch(Exception ex) {
        	throw new RpcException(ex);
        	
        }
        if (rtn.equals("")) 
        {
        	throw new RpcException("Ubable to get user SSN");
            //throw new DaoException("Unable to get user SSN");
        }
		parts = StringUtils.Split(rtn,StringUtils.CARET);
        try 
		{
        	credentials.setSsn(parts[8]);
        } 
        catch (Exception ex) 
		{
        	throw new RpcException(ex);
            //throw new DaoException(ex);
        }
	}

	boolean disconnecting = false;
	boolean disconnectWithImaging = true;
	
	public boolean isDisconnectWithImaging()
	{
		return this.disconnectWithImaging;
	}

	/**
	 * Submit the connection for an asynchronous disconnect
	 * @return
	 */
	public boolean disconnectWithoutImaging()
	{
		this.disconnecting = true;
		this.disconnectWithImaging = false;
		getDisconnectExecutor().submit(this);
		return true;
	}
	/**
	 * Submit the connection for an asynchronous disconnect
	 * @return
	 */
	public boolean disconnect()
	{
		this.disconnecting = true;
		this.disconnectWithImaging = true;
		getDisconnectExecutor().submit(this);
		return true;
	}
	
	/**
	 * Closes the connection to the VistA database without making any VistA Imaging RPC calls
	 * @return
	 */
	private boolean disconnectWithoutImagingImmediate()
	{
		try
		{
			connection.disconnect();
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	private boolean disconnectImmediate() 
	{
		VistaQuery vm = new VistaQuery(RPC_MAG_LOGOFF);
		boolean maggLogoffComplete = false;
		try
		{
			connection.call(vm);
			maggLogoffComplete = true;
			connection.disconnect();
			return true;
		}
		catch (Exception ex)
		{
			if (!maggLogoffComplete)
			{
				try
				{
					connection.disconnect();
					return true;
				}
				catch (Exception ex2)
				{
					// log exception
					ex2.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}
	
	public String getBrokerSecurityTokenWithoutImaging(String bseRealm)
	throws RpcException
	{
		VistaQuery vm = new VistaQuery(RPC_XUS_SET_VISITOR);

		try
		{
			String rtn = connection.call(vm);			
			return createFullVIXTokenString(rtn, bseRealm);		
		}
		catch (Exception ex)
		{
			throw new RpcException(ex);
		}
	}
	
	public String getBrokerSecurityTokenWithImaging(String bseRealm)
	throws RpcException
	{
		VistaQuery vm = new VistaQuery(RPC_MAG_BROKER_SECURITY);

		try
		{
			String rtn = connection.call(vm);			
			return createFullVIXTokenString(rtn, bseRealm);		
		}
		catch (Exception ex)
		{
			throw new RpcException(ex);
		}		
	}
	
	private String createFullVIXTokenString(String xwbToken, String bseRealm)
	{	
		if(bseRealm == null)
			bseRealm = this.credentials.getRealm();
		return VIX_BSE_APP_NAME + StringUtils.CARET + 
			xwbToken + StringUtils.CARET + 			
			bseRealm + StringUtils.CARET  + 
			this.connection.getURL().getPort();
	}

	/*
	 * Asynchronous disconnect
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		try
		{
			logger.info("Disconnecting RPCBroker " + this.toString());
			if(isDisconnectWithImaging())
				this.disconnectImmediate();
			else
				this.disconnectWithoutImagingImmediate();
		}
		catch(Throwable x)
		{
			logger.warn(x.getMessage() + "while disconnecting RPCBroker " + this.toString());
		}
	}
}