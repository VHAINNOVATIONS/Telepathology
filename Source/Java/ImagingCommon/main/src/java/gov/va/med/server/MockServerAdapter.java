/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Oct 14, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
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
package gov.va.med.server;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple ServerLifecycleAdapter implementation that simply passes events
 * through to the listeners.
 * 
 * @author VHAISWBECKEC
 *
 */
public class MockServerAdapter 
{
	private List<ServerLifecycleListener> listeners = new ArrayList<ServerLifecycleListener>();
	private ServerLifecycleEvent startEvent;
	
	/**
	 * @see gov.va.med.server.ServerLifecycleAdapter#addLifecycleListener(gov.va.med.server.ServerLifecycleListener)
	 */
	public void addLifecycleListener(ServerLifecycleListener listener)
	{
		listeners.add(listener);
		if(startEvent != null)
			listener.serverLifecycleEvent(startEvent);
	}

	/**
	 * @see gov.va.med.server.ServerLifecycleAdapter#removeLifecycleListener(gov.va.med.server.ServerLifecycleListener)
	 */
	public void removeLifecycleListener(ServerLifecycleListener listener)
	{
		listeners.remove(listener);
	}

	public void notifyListenersOfStart()
	{
		ServerLifecycleEvent event = new ServerLifecycleEvent(ServerLifecycleEvent.EventType.START);		
		notifyListeners(event);
	}
	

	public void notifyListenersOfStop()
	{
		ServerLifecycleEvent event = new ServerLifecycleEvent(ServerLifecycleEvent.EventType.STOP);		
		notifyListeners(event);
	}
	
	/**
	 * 
	 * @param event
	 */
	public void notifyListeners(ServerLifecycleEvent event)
	{
		if(ServerLifecycleEvent.EventType.START == event.getEventType())
			this.startEvent = event;
		if(ServerLifecycleEvent.EventType.STOP == event.getEventType())
			this.startEvent = null;
		
		for(ServerLifecycleListener listener : listeners)
			listener.serverLifecycleEvent(event);
	}
}
