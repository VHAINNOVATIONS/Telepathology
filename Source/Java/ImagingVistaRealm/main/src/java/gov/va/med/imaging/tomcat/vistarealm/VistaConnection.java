/*
 * Created on Jan 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.tomcat.vistarealm;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;


/**
 * @author VHAANNGilloJ
 */
public class VistaConnection 
implements IConnection 
{
	private static final int READ_TIMEOUT = 30000;
	private static final long DEFAULT_POLLING_INTERVAL = 10L;
	
	public static int DEFAULT_PORT = 9200;
    public static int DEFAULT_TIMEOUT = 180000;
    public static int SOCKET_TIMEOUT = 20000;
    public static final String DEFAULT_CONTEXT = "OR CPRS GUI CHART";

    public static int CONNECTION_TIMEOUT = 30;		// wait for the server to respond
    public static TimeUnit CONNECTION_TIMEOUT_UNITS = TimeUnit.SECONDS;
    
	private static ExecutorService connectionWaiterExecutor;
	
	private static int connectionThreadSerialNumber = 0;
	static 
	{
		connectionWaiterExecutor = Executors.newCachedThreadPool(
			new ThreadFactory()
			{
				@Override
				public Thread newThread(Runnable r)
				{
					Thread newWorker = 
						new Thread(r, "VistaConnectionWaiter_" + connectionThreadSerialNumber++);
					newWorker.setDaemon(true);
					return newWorker;
				}
			}
		);
	}
    
    public boolean fConnected;

    private final DataSource dataSource;
    private String duz;
    private Socket socket;
    private int port;

    public VistaConnection(DataSource dataSource) 
    {
        if (dataSource.getContext() == null) 
        	dataSource.setContext(DEFAULT_CONTEXT);
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {return dataSource;}

	//public String getSiteID() 
	//{
	//    return this.dataSource.getSite().getID();
	//}

	//public void setPid(String dfn) {this.dfn = dfn;}
	//public String getPid() {return dfn;}
	//public String getPid(String icn) throws Exception
	//{
	//    return getDfn(icn);
	//}

	//public void setLocalPID(String mpiPID) throws Exception
	//{
	//    this.dfn = getDfn(mpiPID);
	//}

	//public void setPatient(Patient patient)
	//{
	//    this.patient = patient;
	//    this.dfn = patient.getLocalPID();
	//}

	//public Patient getPatient()
	//{
	//    return patient;
	//}

	public void setUid(String duz) {this.duz = duz;}
	public String getUid() {return duz;}

	//public void setRemoteFlag(boolean fRemote) {this.fRemote = fRemote;}
	//public boolean isRemote() {return fRemote;}

    public synchronized void connect() 
    throws Exception 
	{
        fConnected = false;

        String hostname = this.dataSource.getSource();
        if (hostname == null || hostname.equals("")) 
            throw new Exception("No domain name");

        this.port = this.dataSource.getPort();
        if (port <= 0) 
        	port = DEFAULT_PORT;

		String errMsg = "Tried " + hostname + ":" + port;

        ServerSocket listenerSocket;
		InetAddress inetAddr;

		try
		{
			InetAddress vistaAddr = InetAddress.getByName(hostname);
			socket = new Socket(vistaAddr,port);
			socket.setSoTimeout(SOCKET_TIMEOUT);
			//socket = new Socket(hostname,port);
			//@todo need to implement connect with IP address
			listenerSocket = new ServerSocket(0);
			// JMW 12/14/2009 - listener socket did not have a timeout set, was waiting forever to get a response.
			listenerSocket.setSoTimeout(SOCKET_TIMEOUT);
			inetAddr = InetAddress.getLocalHost();
		}
		catch (UnknownHostException uhe)
		{
			throw new Exception(errMsg,uhe);
		}
		catch (IOException ioe)
		{
			throw new Exception(errMsg,ioe);
		}
		String msg = "TCPconnect^" +
			inetAddr.getHostAddress() + '^' +
			listenerSocket.getLocalPort() + '^';
		msg = "{XWB}" + VistaQuery.strPack(msg,5);

		msg = call(msg);
		if (!msg.equals("accept"))
		{
			throw new Exception("Connection not accepted by " + hostname);
		}
		
		socket = waitForServerReply(listenerSocket, errMsg);
		
        fConnected = true;
    }

    /**
     * Delegates the wait for a reply from the server to a worker thread and waits
     * a limited time for a response.
     * 
     * @param listenerSocket
     * @param errMsg
     * @return
     * @throws Exception
     */
	private Socket waitForServerReply(ServerSocket listenerSocket, String errMsg) 
	throws Exception
	{
		ServerReplyWaiter waiterTask = new ServerReplyWaiter(listenerSocket, errMsg);
		Future<Socket> waitResult = connectionWaiterExecutor.submit(waiterTask);
		try
		{
			return waitResult.get(CONNECTION_TIMEOUT, CONNECTION_TIMEOUT_UNITS);
		}
		catch (TimeoutException tX)
		{
			throw new Exception(
				"Failed to receive a response from VistA server in " + SOCKET_TIMEOUT +
				" " + CONNECTION_TIMEOUT_UNITS.toString(), tX);
		}
	}
	
	/**
	 * A worker task to wait for a response from the server. 
	 */
	class ServerReplyWaiter
	implements Callable<Socket>
	{
		private ServerSocket listenerSocket;
		private String errMsg;
		
		ServerReplyWaiter(ServerSocket listenerSocket, String errMsg)
		{
			this.listenerSocket = listenerSocket;
			this.errMsg = errMsg;
		}

		@Override
		public Socket call() 
		throws Exception
		{
			try
			{
				Socket responseSocket = listenerSocket.accept();
				return responseSocket;
			} 
			catch (SocketException se) 
			{
				throw new Exception(errMsg,se);
			}
			catch (IOException ioe) 
			{
				throw new Exception(errMsg,ioe);
			}
		}
	}
	

    public String call(String request) 
    throws Exception
    {
        send(request);
        return recv(getCallReadTimeout());
    }

	private void send(String msg) throws Exception
    {
        try
        {
            Writer out = new OutputStreamWriter(socket.getOutputStream(),"ASCII");
            out.write(msg);
            out.flush();
            return;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    private String recv(int wait) 
    throws Exception
    {
		String sReply = "";
		
        InputStream inStream = socket.getInputStream();
        BufferedInputStream buffer = new BufferedInputStream(inStream);
        InputStreamReader in = new InputStreamReader(buffer,"ASCII");
        StringBuffer reply = new StringBuffer();
        int c = nonBlockingRead(in, wait);  // read #of chars in security error message
        if (c != 0)
        {
			while ((c = nonBlockingRead(in, wait)) != -1 && c != 4)
			{
				reply.append((char)c);
			}
			// Sometimes c is the first char and not # of chars
			if (reply.length() != c && StringUtils.isAlphaChar((char)c))
			{
				reply = reply.insert(0,c);
			}
			throw new Exception(reply.toString().trim());
        }
        c = nonBlockingRead(in, wait);  // read # of chars in application error message
        if (c != 0)
        {
			while ((c = nonBlockingRead(in, wait)) != -1 && c != 4)
			{
				reply.append((char)c);
			}
			// Sometimes c is the first char and not # of chars
			if (reply.length() != c && StringUtils.isAlphaChar((char)c))
			{
				reply = reply.insert(0,c);
			}
			throw new Exception(reply.toString().trim());
        }
        // Now finally read the message...
        while ((c = nonBlockingRead(in, wait)) != -1  && c != 4)
        {
            reply.append((char)c);
        }
        sReply = reply.toString().trim();
        if (sReply.indexOf("M  ERROR",0) != -1) 
        {
            throw new Exception(sReply);
        }
        
		return sReply;
	}

	public void disconnect() 
	throws Exception 
	{
		if (!fConnected) return;
		try
		{
			String msg = VistaQuery.strPack(VistaQuery.strPack("#BYE#",5),5);
	    	msg = call(msg);
	    	//@todo msg should contain "BYE" - do anything if it doesn't?
		}
		finally
		{
		    try
		    {
		    	if((socket != null) && (socket.isConnected()))
		    		socket.close();
		    }
		    catch(Exception ex) {}		    
		    fConnected = false;
		}
	}
	
	/**
	 * Waits up to wait milliseconds for a character.
	 * Throws an IOException if no char are available.
	 * 
	 * @param inStream
	 * @param wait
	 * @return
	 * @throws IOException
	 */
	public int nonBlockingRead(InputStreamReader inStream, long wait) 
	throws IOException
	{
		if(wait > 0)
		{
			long pollingInterval = getPollingInterval();
			long currentPollingInterval = 2L; // start with a 2 second polling interval in case the read will be ready soon
			long startWait = System.currentTimeMillis();
			long endWait = startWait + (wait > 0 ? wait : 0l);		// pre-calculate this to save processing on each loop
			boolean expired = false;
			while( ! inStream.ready() && !(expired = (System.currentTimeMillis() > endWait)) )
			{
				try
				{
					Thread.sleep(currentPollingInterval);
				}
				catch (InterruptedException x)
				{
					x.printStackTrace();
					throw new IOException("Interrupted waiting for input in recv() method.", x);
				}

				if(currentPollingInterval < pollingInterval)
				{
					// if the current polling interval is less than the polling interval
					// double the polling interval
					currentPollingInterval *= 2;
					if(currentPollingInterval > pollingInterval)
					{
						// ensure the polling interval is not above the desired polling interval
						currentPollingInterval = pollingInterval;
					}
				}
			}
			if(expired)
				throw new IOException("Timed out waiting " + wait + " milliseconds.");
		}
		
		return inStream.read();	// read a character
	}

	/**
	 * @return
	 */
	private long getPollingInterval()
	{
		return DEFAULT_POLLING_INTERVAL;
	}
	
    /**
	 * @return
	 */
	private int getCallReadTimeout()
	{
		return READ_TIMEOUT;
	}
}
