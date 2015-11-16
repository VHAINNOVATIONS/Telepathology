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

import gov.va.med.server.ServerAgnosticEngine;
import gov.va.med.server.ServerAgnosticEngineAdapter;
import gov.va.med.server.ServerLifecycleEvent;
import gov.va.med.server.ServerLifecycleEvent.EventType;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.naming.directory.DirContext;
import javax.servlet.ServletException;
import org.apache.catalina.*;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardPipeline;
import org.apache.juli.logging.Log;
import org.apache.log4j.Logger;

/**
 * The Engine element represents the entire request processing machinery associated 
 * with a particular Catalina Service. It receives and processes all requests from 
 * one or more Connectors, and returns the completed response to the Connector for 
 * ultimate transmission back to the client.
 * 
 * Exactly one Engine element MUST be nested inside a Service element, following all 
 * of the corresponding Connector elements associated with this Service.
 * 
 * @author vhaiswbeckec
 * @see org.apache.catalina.Engine#getDefaultHost()
 *
 */
public class TomcatEngineAdapter
implements Engine, ServerAgnosticEngineAdapter, Lifecycle
{
	private String defaultHost;
	private String jvmRoute;
	private Service service;
	private List<Container> children = new ArrayList<Container>();
	private List<ContainerListener> containerListeners = new ArrayList<ContainerListener>();
	private List<PropertyChangeListener> propertyChangeListeners = new ArrayList<PropertyChangeListener>();
	private int backgroundProcessorDelay;
	private Cluster cluster;
	private String info = "The VIX Engine adapter";
	private Loader loader;
	private Log logger;
	private Manager manager;
	private Object mappingObject;
	private String name;
	private ClassLoader parentClassLoader;
	private Pipeline pipeline = new StandardPipeline();
	private Realm realm;
	private DirContext resources;
	private List<LifecycleListener> lifecycleListeners = new ArrayList<LifecycleListener>();

	private Logger javaLogger = Logger.getLogger(TomcatEngineAdapter.class);
	
	// ===================================================================================
	// The server agnostic engine is the class that we're wrapping, the one that
	// we'll forward events to execute server operations on behalf of.
	private String serverAgnosticEngineClassName;
	private ServerAgnosticEngine wrappedEngine;
	
	/**
	 * 
	 */
	public TomcatEngineAdapter()
	{
		javaLogger.info("<ctor> TomcatEngineAdapter '" + getName() + "'.");
	}
	
	public String getServerAgnosticEngineClassName()
	{
		return this.serverAgnosticEngineClassName;
	}
	public void setServerAgnosticEngineClassName(String serverAgnosticEngineClassName)
	{
		this.serverAgnosticEngineClassName = serverAgnosticEngineClassName;
		javaLogger.info("TomcatEngineAdapter server agnostic engine class name is '" + serverAgnosticEngineClassName + "'.");
		
		try
		{
			Class<? extends ServerAgnosticEngine> serverAgnosticClass = 
				(Class<? extends ServerAgnosticEngine>) Class.forName(getServerAgnosticEngineClassName());
			javaLogger.info("TomcatEngineAdapter instantiating server agnostic engine.");
			wrappedEngine = serverAgnosticClass.newInstance();
			javaLogger.info("TomcatEngineAdapter instantiated server agnostic engine.");
			wrappedEngine.setServerAgnosticEngineAdapter(this);
			javaLogger.info("TomcatEngineAdapter engine adapter set.");
		}
		catch (ClassNotFoundException x)
		{
			javaLogger.error("Unable to find class '" + getServerAgnosticEngineClassName() + "'.");
			x.printStackTrace();
		}
		catch (InstantiationException x)
		{
			javaLogger.error("Unable to create instance of '" + getServerAgnosticEngineClassName() + "'.");
			x.printStackTrace();
		}
		catch (IllegalAccessException x)
		{
			javaLogger.error("Prevented from creating instance of '" + getServerAgnosticEngineClassName() + "'.");
			x.printStackTrace();
		}
	}

	/**
	 * Gets (and optionally instantiates) the server agnostic class.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ServerAgnosticEngine getServerAgnosticEngine()
	{
		return wrappedEngine;
	}
	
	// ===================================================================================
	
	@Override
	public String getDefaultHost(){return this.defaultHost;}
	@Override
	public void setDefaultHost(String arg0){this.defaultHost = arg0;}

	@Override
	public String getJvmRoute(){return this.jvmRoute;}
	@Override
	public void setJvmRoute(String arg0)
	{
		StringBuilder sb = new StringBuilder();
		for(StackTraceElement e : Thread.currentThread().getStackTrace())
			sb.append(e.toString() + "\n");
		javaLogger.info(sb.toString());
		
		setServerAgnosticEngineClassName(arg0);
		
		this.jvmRoute = arg0;
	}

	@Override
	public Service getService(){return this.service;}
	@Override
	public void setService(Service arg0){this.service = arg0;}

	@Override
	public void addChild(Container arg0){children.add(arg0);}
	@Override
	public Container findChild(String arg0)
	{
		if(arg0 == null) return null;
		
		for(Container container : this.children)
			if( arg0.equals(container.getName()) )
				return container;
		return null;
	}
	@Override
	public Container[] findChildren(){ return this.children.toArray(new Container[children.size()]);	}
	@Override
	public void removeChild(Container arg0){this.children.remove(arg0);}


	@Override
	public void addContainerListener(ContainerListener arg0){this.containerListeners.add(arg0);}
	@Override
	public ContainerListener[] findContainerListeners()
	{return this.containerListeners.toArray(new ContainerListener[this.containerListeners.size()]);}
	@Override
	public void removeContainerListener(ContainerListener arg0){this.containerListeners.remove(arg0);}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener arg0){this.propertyChangeListeners.add(arg0);}
	@Override
	public void removePropertyChangeListener(PropertyChangeListener arg0){this.propertyChangeListeners.remove(arg0);}

	@Override
	public void backgroundProcess()
	{
		javaLogger.info("Running background process within " + this.getClass().getName() + "'.");
	}
	@Override
	public int getBackgroundProcessorDelay(){return this.backgroundProcessorDelay;}
	@Override
	public void setBackgroundProcessorDelay(int arg0){this.backgroundProcessorDelay = arg0;}


	@Override
	public Cluster getCluster(){return this.cluster;}
	@Override
	public void setCluster(Cluster arg0){this.cluster = arg0;}

	@Override
	public String getInfo(){return this.info;}

	
	@Override
	public Loader getLoader(){return this.loader;}
	@Override
	public void setLoader(Loader arg0){this.loader = arg0;}


	@Override
	public Log getLogger(){return this.logger;}

	@Override
	public Manager getManager(){return this.manager;}
	@Override
	public void setManager(Manager arg0){this.manager = arg0;}


	/**
	 * Return an object which may be utilized for mapping to this component. 
	 */
	@Override
	public Object getMappingObject(){return this.mappingObject;}

	@Override
	public String getName(){return this.name;}
	@Override
	public void setName(String arg0){this.name = arg0;}

	/**
	 * Return the JMX name associated with this container. 
	 */
	@Override
	public String getObjectName(){return "gov.va.med.imaging:name=" + getName();}

	/**
	 * If used, an Engine is always the top level Container in a Catalina hierarchy. 
	 * Therefore, the implementation's setParent() method should throw IllegalArgumentException. 
	 */
	@Override
	public Container getParent(){return null;}
	public void setParent(Container arg0){throw new IllegalArgumentException("An Engine is always a top level container, no parent may be set.");}

	@Override
	public ClassLoader getParentClassLoader(){return this.parentClassLoader;}
	@Override
	public void setParentClassLoader(ClassLoader arg0){this.parentClassLoader = arg0;}

	@Override
	public Pipeline getPipeline(){return this.pipeline;}

	@Override
	public Realm getRealm(){return this.realm;}
	@Override
	public void setRealm(Realm arg0){this.realm = arg0;}


	@Override
	public DirContext getResources(){return this.resources;}
	@Override
	public void setResources(DirContext arg0){this.resources = arg0;}

	/**
	 * @see org.apache.catalina.Container#invoke(org.apache.catalina.connector.Request, org.apache.catalina.connector.Response)
	 */
	@Override
	public void invoke(Request request, Response response) 
	throws IOException, ServletException
	{
		this.logger.info("TomcatEngineAdapter.invoke(" + request.toString() + ", " + response.toString() + ")");
	}
	
	
	// =====================================================================================
	// Lifecycle Implementation
	// events from the server will be forwarded on to the server-agnostic engine
	// implementation.
	// It is NOT necessary to register the server-agnostic engine as a lifecycle
	// listener, it will be notified regardless.
	// =====================================================================================
	
	@Override
	public void addLifecycleListener(LifecycleListener arg0){this.lifecycleListeners.add(arg0);}
	@Override
	public LifecycleListener[] findLifecycleListeners(){return this.lifecycleListeners.toArray( new LifecycleListener[this.lifecycleListeners.size()] );}
	@Override
	public void removeLifecycleListener(LifecycleListener arg0){this.lifecycleListeners.remove(arg0);}
	
	@Override
	public void start() 
	throws LifecycleException
	{
		javaLogger.info("processing start() event notification.");
		if(getServerAgnosticEngine() != null)
			getServerAgnosticEngine().serverEvent(new ServerLifecycleEvent(EventType.START));
		startChildContainers();
		notifyLifecycleListeners(new LifecycleEvent(this, "START"));
	}
	
	@Override
	public void stop() 
	throws LifecycleException
	{
		javaLogger.info("processing stop() event notification.");
		if(getServerAgnosticEngine() != null)
			getServerAgnosticEngine().serverEvent(new ServerLifecycleEvent(EventType.STOP));
		stopChildContainers();
		notifyLifecycleListeners(new LifecycleEvent(this, "STOP"));
	}
	
	/**
	 * Notify the Child containers that are also Lifecycle realizations
	 * @param string
	 * @throws LifecycleException 
	 */
	private void startChildContainers() 
	throws LifecycleException
	{
		for(Container container : this.children)
			if(Lifecycle.class.isInstance(container))
				((Lifecycle)container).start();
	}
	private void stopChildContainers() 
	throws LifecycleException
	{
		for(Container container : this.children)
			if(Lifecycle.class.isInstance(container))
				((Lifecycle)container).stop();
	}

	private void notifyLifecycleListeners(LifecycleEvent event)
	{
		for(LifecycleListener listener : lifecycleListeners)
			listener.lifecycleEvent(event);
	}
	
	// =====================================================================================
	// ServerAgnosticEngineAdapter Implementation,
	// These methods implement a proxy for this class to do server-specific operations
	// on behalf of the server-agnostic engine adapter that we're bound to.
	// =====================================================================================
	
	@Override
	public Principal authenticate(String username, byte[] credentials)
	{
		Realm realm = getRealm();
		return realm == null ? null : realm.authenticate(username, credentials);
	}
	
	@Override
	public Principal authenticate(String username, String clientDigest, String nOnce, String nc, String cnonce,
		String qop, String realmName, String md5a2)
	{
		Realm realm = getRealm();
		return realm == null ? null : realm.authenticate(username, clientDigest, nOnce, nc, cnonce, qop, realmName, md5a2);
	}
	
	@Override
	public Principal authenticate(X509Certificate[] certs)
	{
		Realm realm = getRealm();
		return realm == null ? null : realm.authenticate(certs);
	}
	
	@Override
	public boolean hasRole(Principal principal, String role)
	{
		Realm realm = getRealm();
		return realm == null ? null : realm.hasRole(principal, role);
	}
}
