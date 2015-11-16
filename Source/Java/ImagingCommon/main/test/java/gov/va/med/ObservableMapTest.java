/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date May 4, 2010
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

package gov.va.med;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class ObservableMapTest
extends TestCase
{
	public void testPut()
	{
		ObservableMap<String, String> observableMap = new ObservableMap<String, String>();
		LatchingObserver observer = new LatchingObserver();
		observableMap.addObserver(observer);
		
		observableMap.put("A", "First Letter");
		assertTrue(observer.getObserverdEvent() instanceof ObservableMap.Event);
		ObservableMap.Event event = (ObservableMap.Event)(observer.getObserverdEvent());
		assertEquals( ObservableMap.Event.TYPE.PUT, event.getType() );
		assertEquals("A", event.getKey());
		assertEquals("First Letter", event.getNewValue());
		
		observableMap.put("A", "The First Letter");
		assertTrue(observer.getObserverdEvent() instanceof ObservableMap.Event);
		event = (ObservableMap.Event)(observer.getObserverdEvent());
		assertEquals( ObservableMap.Event.TYPE.PUT, event.getType() );
		assertEquals("A", event.getKey());
		assertEquals("First Letter", event.getOldValue());
		assertEquals("The First Letter", event.getNewValue());
		
		observableMap.remove("A");
		assertTrue(observer.getObserverdEvent() instanceof ObservableMap.Event);
		event = (ObservableMap.Event)(observer.getObserverdEvent());
		assertEquals( ObservableMap.Event.TYPE.REMOVE, event.getType() );
	}
	
	public class LatchingObserver
	implements Observer
	{
		private Observable observable; 
		private Object observerdEvent;
		
		@Override
		public void update(Observable o, Object arg)
		{
			this.observable = o;
			this.observerdEvent = arg;
		}

		protected Observable getObservable()
		{
			return this.observable;
		}

		protected Object getObserverdEvent()
		{
			return this.observerdEvent;
		}
	}
}
