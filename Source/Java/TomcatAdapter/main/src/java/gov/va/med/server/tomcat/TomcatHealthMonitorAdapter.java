/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date May 10, 2010
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

import java.security.Principal;
import java.util.*;
import gov.va.med.server.*;
import gov.va.med.server.ServerLifecycleEvent.EventType;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Realm;
import org.apache.log4j.Logger;

/**
 * This class monitors the health of the host server (Tomcat)
 * on behalf of the VIX Server Health package.
 * 
 * @author vhaiswbeckec
 *
 */
public class TomcatHealthMonitorAdapter
implements LifecycleListener
{
	private int maxEventHistorySize = 20;
	private Deque<ServerLifecycleEvent> eventQueue = new ArrayDeque<ServerLifecycleEvent>(maxEventHistorySize);
	private ServerAdapter serverAdapter;
	private Logger logger = Logger.getLogger(TomcatHealthMonitorAdapter.class);
	
	/**
	 * @see org.apache.catalina.LifecycleListener#lifecycleEvent(org.apache.catalina.LifecycleEvent)
	 */
	@Override
	public void lifecycleEvent(LifecycleEvent event)
	{
		ServerLifecycleEvent applicationEvent = TomcatLifecycleAdapter.translate(event);
		if(applicationEvent != null)
		{
			addEvent(applicationEvent);
			
			validateAtEvent(applicationEvent.getEventType());
		}
	}

	/**
	 * @param eventType
	 */
	private void validateAtEvent(EventType eventType)
	{
		switch(eventType)
		{
		case INIT:
			break;
		case BEFORE_START:
			this.serverAdapter = ServerAdapterImpl.getSingleton();
			break;
		case AFTER_START:
			//validateDevelopmentRealm();		// don't call this except to test the programmatic access to realms 
			break;
		case BEFORE_STOP:
			break;
		case STOP:
			break;
		case AFTER_STOP:
			break;
		}
	}

	/**
	 * A test method to see that we can find a realm
	 */
	private void validateDevelopmentRealm()
	{
		Map<String, Object> realmPropertyMap = new HashMap<String, Object>();
		realmPropertyMap.put("siteNumber", "660");
		ServerAuthentication auth = this.serverAdapter.getServerAuthentication();
		logger.info( (auth == null ? "Did NOT" : "DID") + " acquire ServerAuthentication singleton." );
		SecurityRealmIdentification realmIdentification = new SecurityRealmIdentification((Class<?>)null, realmPropertyMap);
		
		Principal principal = auth.authenticate(realmIdentification, "boating1", "boating1.".getBytes());
		logger.info( (principal == null ? "Did NOT" : "DID") + " acquire and log into VistA realm." );
	}

	/**
	 * 
	 * @return
	 */
	public ServerLifecycleEvent.EventType getCurrentLifecycleStatus()
	{
		ServerLifecycleEvent mostRecent = eventQueue.peekFirst();
		
		return mostRecent == null ? null : mostRecent.getEventType();
	}
	
	/**
	 * @return the maxEventHistorySize
	 */
	protected int getMaxEventHistorySize()
	{
		return this.maxEventHistorySize;
	}
	/**
	 * @param maxEventHistorySize the maxEventHistorySize to set
	 */
	protected void setMaxEventHistorySize(int maxEventHistorySize)
	{
		this.maxEventHistorySize = maxEventHistorySize;
	}

	/**
	 * @param applicationEvent
	 */
	private void addEvent(ServerLifecycleEvent applicationEvent)
	{
		synchronized (eventQueue)
		{
			while(eventQueue.size() > getMaxEventHistorySize())
				eventQueue.removeLast();
			eventQueue.addFirst(applicationEvent);
		}
	}

	/**
	 * 
	 * @return
	 */
	public Iterator<ServerLifecycleEvent> lifecycleEventsIterator()
	{
		return eventQueue.iterator();
	}
}
