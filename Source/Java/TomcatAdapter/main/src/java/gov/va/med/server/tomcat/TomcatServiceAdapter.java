/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date May 12, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
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

package gov.va.med.server.tomcat;

import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.log4j.Logger;

/**
 * from http://tomcat.apache.org/tomcat-5.5-doc/catalina/docs/api/index.html
 * 
 * A Service is a group of one or more Connectors that share a single Container
 * to process their incoming requests. This arrangement allows, for example, a 
 * non-SSL and SSL connector to share the same population of web apps.
 * 
 * A given JVM can contain any number of Service instances; however, they are 
 * completely independent of each other and share only the basic JVM facilities 
 * and classes on the system class path.
 *  
 * @author vhaiswbeckec
 *
 */
public class TomcatServiceAdapter
implements org.apache.catalina.Service, org.apache.catalina.Lifecycle 
{
	private String info;
	private String name;
	private Container container;
	private Server server;
	
	private Logger logger = Logger.getLogger(TomcatServiceAdapter.class);
	
	/**
	 * 
	 */
	public TomcatServiceAdapter()
	{
		super();
		logger.info("<ctor> TomcatServiceAdapter '" + getName() + "'.");
	}

	/**
	 * @see org.apache.catalina.Service#getInfo()
	 */
	@Override
	public String getInfo()
	{
		return info;
	}

	/**
	 * @see org.apache.catalina.Service#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/**
	 * @see org.apache.catalina.Service#setName(java.lang.String)
	 */
	@Override
	public void setName(String name)
	{
		this.name = name;
	}


	/**
	 * @see org.apache.catalina.Service#getContainer()
	 */
	@Override
	public Container getContainer()
	{
		return this.container;
	}
	/**
	 * @see org.apache.catalina.Service#setContainer(org.apache.catalina.Container)
	 */
	@Override
	public void setContainer(Container container)
	{
		this.container = container;
	}
	
	/**
	 * @see org.apache.catalina.Service#getServer()
	 */
	@Override
	public Server getServer()
	{
		return this.server;
	}
	/**
	 * @see org.apache.catalina.Service#setServer(org.apache.catalina.Server)
	 */
	@Override
	public void setServer(Server server)
	{
		this.server = server;
	}

	// ============================================================================
	// Lifecycle Notification and Listener Management
	// ============================================================================
	/**
	 * @see org.apache.catalina.Service#initialize()
	 */
	@Override
	public void initialize() 
	throws LifecycleException
	{
		logger.info("Initializing TomcatServiceAdapter '" + getName() + "'.");
	}

	/**
	 * @see org.apache.catalina.Lifecycle#start()
	 */
	@Override
	public void start() 
	throws LifecycleException
	{
		logger.info("processing start() event notification.");
		
		// notify our container that we are starting
		if(Lifecycle.class.isInstance(getContainer()))
			((Lifecycle)getContainer()).start();
		
		notifyLifecycleListeners(new LifecycleEvent(this, "START"));
	}

	/**
	 * @see org.apache.catalina.Lifecycle#stop()
	 */
	@Override
	public void stop() 
	throws LifecycleException
	{
		logger.info("processing stop() event notification.");
		
		// notify our container that we are stopping
		if(Lifecycle.class.isInstance(getContainer()))
			((Lifecycle)getContainer()).stop();
		
		notifyLifecycleListeners(new LifecycleEvent(this, "STOP"));
	}

	private List<LifecycleListener> lifecycleListeners = new ArrayList<LifecycleListener>();
	/**
	 * @see org.apache.catalina.Lifecycle#addLifecycleListener(org.apache.catalina.LifecycleListener)
	 */
	@Override
	public void addLifecycleListener(LifecycleListener arg0)
	{
		lifecycleListeners.add(arg0);
	}

	/**
	 * @see org.apache.catalina.Lifecycle#findLifecycleListeners()
	 */
	@Override
	public LifecycleListener[] findLifecycleListeners()
	{
		return lifecycleListeners.toArray(new LifecycleListener[lifecycleListeners.size()]);
	}

	/**
	 * @see org.apache.catalina.Lifecycle#removeLifecycleListener(org.apache.catalina.LifecycleListener)
	 */
	@Override
	public void removeLifecycleListener(LifecycleListener arg0)
	{
		this.lifecycleListeners.remove(arg0);
	}

	private void notifyLifecycleListeners(LifecycleEvent event)
	{
		for(LifecycleListener listener : lifecycleListeners)
			listener.lifecycleEvent(event);
	}
	
	// ============================================================================
	// Connector Management
	// ============================================================================
	private List<Connector> connectors = new ArrayList<Connector>();
	
	/**
	 * @see org.apache.catalina.Service#addConnector(org.apache.catalina.connector.Connector)
	 */
	@Override
	public void addConnector(Connector arg0)
	{
		connectors.add(arg0);
	}
	/**
	 * @see org.apache.catalina.Service#findConnectors()
	 */
	@Override
	public Connector[] findConnectors()
	{
		return connectors.toArray(new Connector[connectors.size()]);
	}
	/**
	 * @see org.apache.catalina.Service#removeConnector(org.apache.catalina.connector.Connector)
	 */
	@Override
	public void removeConnector(Connector arg0)
	{
		connectors.remove(arg0);
	}

	// ============================================================================
	// Executor Management
	// ============================================================================
	private List<Executor> executors = new ArrayList<Executor>();
	
	/**
	 * @see org.apache.catalina.Service#addExecutor(org.apache.catalina.Executor)
	 */
	@Override
	public void addExecutor(Executor executor)
	{
		executors.add(executor);
	}
	/**
	 * @see org.apache.catalina.Service#findExecutors()
	 */
	@Override
	public Executor[] findExecutors()
	{
		return executors.toArray(new Executor[executors.size()]);
	}
	/**
	 * @see org.apache.catalina.Service#getExecutor(java.lang.String)
	 */
	@Override
	public Executor getExecutor(String name)
	{
		if(name == null) return null;
		
		for(Executor executor : executors)
			if( name.equals(executor.getName()) )
				return executor;
		
		return null;
	}
	/**
	 * @see org.apache.catalina.Service#removeExecutor(org.apache.catalina.Executor)
	 */
	@Override
	public void removeExecutor(Executor executor)
	{
		executors.remove(executor);
	}
}
