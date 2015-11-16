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

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.directory.DirContext;
import javax.servlet.ServletException;
import org.apache.catalina.*;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.juli.logging.Log;
import org.apache.log4j.Logger;

/**
 * @author vhaiswbeckec
 *
 */
public class TomcatHostAdapter
implements Host, Lifecycle
{
	private static final String INFO_STRING = null;
	private Logger javaLogger = Logger.getLogger(TomcatEngineAdapter.class);
	private String appBase;
	private String name;
	private List<String> aliases = new ArrayList<String>();
	private boolean autoDeploy;
	private String configClass;
	private boolean deployOnStartup;
	private boolean xmlNamespaceAware;
	private boolean xmlValidation;
	private Map<String, Context> contextMap = new HashMap<String, Context>();
	private List<Container> children = new ArrayList<Container>();
	private List<ContainerListener> containerListeners = new ArrayList<ContainerListener>();
	private List<PropertyChangeListener> propertyChangeListeners = new ArrayList<PropertyChangeListener>();
	private int backgroundProcessorDelay;
	private Cluster cluster;

	/**
	 * 
	 */
	public TomcatHostAdapter()
	{
		javaLogger.info("<ctor> TomcatHostAdapter '" + getName() + "'.");
	}

	@Override
	public void addAlias(String arg0){aliases.add(arg0);}
	@Override
	public String[] findAliases(){return aliases.toArray(new String[aliases.size()]);}
	@Override
	public void removeAlias(String arg0){this.aliases.remove(arg0);}

	@Override
	public String getAppBase(){return this.appBase;}
	@Override
	public void setAppBase(String arg0){this.appBase = arg0;}

	@Override
	public boolean getAutoDeploy(){return this.autoDeploy;}
	@Override
	public void setAutoDeploy(boolean arg0){this.autoDeploy = arg0;}

	@Override
	public String getConfigClass(){return this.configClass;}
	@Override
	public void setConfigClass(String arg0){this.configClass = arg0;}

	@Override
	public boolean getDeployOnStartup(){return this.deployOnStartup;}
	@Override
	public void setDeployOnStartup(boolean arg0){this.deployOnStartup = arg0;}

	@Override
	public String getName(){return this.name;}
	@Override
	public void setName(String arg0){this.name = arg0;}

	@Override
	public boolean getXmlNamespaceAware(){return this.xmlNamespaceAware;}
	@Override
	public void setXmlNamespaceAware(boolean arg0){this.xmlNamespaceAware = arg0;}

	@Override
	public boolean getXmlValidation(){return this.xmlValidation;}
	@Override
	public void setXmlValidation(boolean arg0){this.xmlValidation = arg0;}

	@Override
	public Context map(String arg0){return this.contextMap.get(arg0);}

	@Override
	public void addChild(Container arg0){this.children.add(arg0);}
	@Override
	public Container findChild(String arg0)
	{
		if(arg0 == null) return null;
		for(Container child : this.children)
			if( arg0.equals(child.getName()) )
				return child;
		return null;
	}
	@Override
	public Container[] findChildren(){return this.children.toArray(new Container[this.children.size()]);}

	@Override
	public void addContainerListener(ContainerListener arg0){this.containerListeners.add(arg0);}
	@Override
	public ContainerListener[] findContainerListeners(){return this.containerListeners.toArray(new ContainerListener[this.containerListeners.size()]);}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener arg0){this.propertyChangeListeners.add(arg0);}

	@Override
	public void backgroundProcess(){}
	@Override
	public int getBackgroundProcessorDelay(){return this.backgroundProcessorDelay;}

	@Override
	public Cluster getCluster(){return this.cluster;}

	@Override
	public String getInfo(){return INFO_STRING;}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#getLoader()
	 */
	@Override
	public Loader getLoader()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#getLogger()
	 */
	@Override
	public Log getLogger()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#getManager()
	 */
	@Override
	public Manager getManager()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#getMappingObject()
	 */
	@Override
	public Object getMappingObject()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#getObjectName()
	 */
	@Override
	public String getObjectName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#getParent()
	 */
	@Override
	public Container getParent()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#getParentClassLoader()
	 */
	@Override
	public ClassLoader getParentClassLoader()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#getPipeline()
	 */
	@Override
	public Pipeline getPipeline()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#getRealm()
	 */
	@Override
	public Realm getRealm()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#getResources()
	 */
	@Override
	public DirContext getResources()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#invoke(org.apache.catalina.connector.Request, org.apache.catalina.connector.Response)
	 */
	@Override
	public void invoke(Request arg0, Response arg1) throws IOException, ServletException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#removeChild(org.apache.catalina.Container)
	 */
	@Override
	public void removeChild(Container arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#removeContainerListener(org.apache.catalina.ContainerListener)
	 */
	@Override
	public void removeContainerListener(ContainerListener arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	@Override
	public void removePropertyChangeListener(PropertyChangeListener arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#setBackgroundProcessorDelay(int)
	 */
	@Override
	public void setBackgroundProcessorDelay(int arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#setCluster(org.apache.catalina.Cluster)
	 */
	@Override
	public void setCluster(Cluster arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#setLoader(org.apache.catalina.Loader)
	 */
	@Override
	public void setLoader(Loader arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#setManager(org.apache.catalina.Manager)
	 */
	@Override
	public void setManager(Manager arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#setParent(org.apache.catalina.Container)
	 */
	@Override
	public void setParent(Container arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#setParentClassLoader(java.lang.ClassLoader)
	 */
	@Override
	public void setParentClassLoader(ClassLoader arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#setRealm(org.apache.catalina.Realm)
	 */
	@Override
	public void setRealm(Realm arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Container#setResources(javax.naming.directory.DirContext)
	 */
	@Override
	public void setResources(DirContext arg0)
	{
		// TODO Auto-generated method stub

	}

	// ======================================================================================
	/* (non-Javadoc)
	 * @see org.apache.catalina.Lifecycle#addLifecycleListener(org.apache.catalina.LifecycleListener)
	 */
	@Override
	public void addLifecycleListener(LifecycleListener arg0)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Lifecycle#findLifecycleListeners()
	 */
	@Override
	public LifecycleListener[] findLifecycleListeners()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Lifecycle#removeLifecycleListener(org.apache.catalina.LifecycleListener)
	 */
	@Override
	public void removeLifecycleListener(LifecycleListener arg0)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Lifecycle#start()
	 */
	@Override
	public void start() throws LifecycleException
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Lifecycle#stop()
	 */
	@Override
	public void stop() throws LifecycleException
	{
		// TODO Auto-generated method stub
		
	}

}
