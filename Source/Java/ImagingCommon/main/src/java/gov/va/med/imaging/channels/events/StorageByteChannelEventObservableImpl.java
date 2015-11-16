/*
 * Copyright (c) 2005, United States Veterans Administration
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list 
 * of conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution.
 * Neither the name of the United States Veterans Administration nor the names of its 
 * contributors may be used to endorse or promote products derived from this software 
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR 
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH 
 * DAMAGE.
 */
 package gov.va.med.imaging.channels.events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Checksum;

/**
 * @author beckey
 * created: Sep 8, 2005 at 4:33:19 PM
 *
 * This class does ...
 */
public abstract class StorageByteChannelEventObservableImpl
implements StorageByteChannelEventObservable
{
	private Object notificationContext = null;
	
	protected StorageByteChannelEventObservableImpl()
	{
	}
	
	protected StorageByteChannelEventObservableImpl(Object notificationContext)
	{
		this.notificationContext = notificationContext;
	}
	
	/* =============================================================================================
	 * StorageByteChannelEventObservable Implementation
	 * ============================================================================================= */
	private Map<StorageByteChannelListener, List<StorageByteChannelEventType>> listeners = 
		new HashMap<StorageByteChannelListener, List<StorageByteChannelEventType>>();
	
	public void addStorageByteChannelListener(StorageByteChannelListener listener, List<StorageByteChannelEventType> eventTypes)
	{
		listeners.put(listener, eventTypes);
	}
	public void addStorageByteChannelListener(StorageByteChannelListener listener)
	{
		listeners.put(listener, null);
	}
	public void removeStorageByteChannelListener(StorageByteChannelListener listener)
	{
		listeners.remove(listener);
	}

	protected void notifyListenersCloseEvent(int bytesRead, int bytesWritten, Checksum crc, String mimeType)
	{
		notifyListeners( 
			StorageByteChannelEvent.createStorageByteChannelCloseEvent(notificationContext, bytesRead, bytesWritten, crc, mimeType)
		);
	}
	
	protected void notifyListenersReadEvent(int bytesRead)
	{
		notifyListeners( 
			StorageByteChannelEvent.createStorageByteChannelReadEvent(notificationContext, bytesRead)
		);
	}
	
	protected void notifyListenersWriteEvent(int bytesWritten)
	{
		notifyListeners( 
			StorageByteChannelEvent.createStorageByteChannelWriteEvent(notificationContext, bytesWritten)
		);
	}
	
	protected void notifyListeners(StorageByteChannelEvent event)
	{
		for(StorageByteChannelListener listener : listeners.keySet() )
		{
			// if the listener is interested in the event
			if( listeners.get(listener) == null || listeners.get(listener).contains(event) )
			{
				StorageByteChannelEventType eventType = event.getEventType(); 
				if( eventType == StorageByteChannelEventType.CLOSE_EVENT)
					listener.storageByteChannelClosed(event);
				else if( eventType == StorageByteChannelEventType.READ_EVENT)
					listener.storageByteChannelRead(event);
				else if( eventType == StorageByteChannelEventType.WRITE_EVENT)
					listener.storageByteChannelWrite(event);
			}
		}
	}
}
