/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.memento;


import gov.va.med.imaging.storage.cache.memento.RegionMemento;

import java.io.Serializable;

public class PersistentRegionMemento
extends RegionMemento
implements Serializable
{
	private static final long serialVersionUID = 1;
	
	private int secondsReadWaitsForWriteCompletion;
	private boolean setModificationTimeOnRead;
	
	public PersistentRegionMemento()
	{
		super();
	}
	
	public int getSecondsReadWaitsForWriteCompletion()
	{
		return this.secondsReadWaitsForWriteCompletion;
	}
	
	public void setSecondsReadWaitsForWriteCompletion(
			int secondsReadWaitsForWriteCompletion)
	{
		this.secondsReadWaitsForWriteCompletion = secondsReadWaitsForWriteCompletion;
	}

	public boolean isSetModificationTimeOnRead()
	{
		return this.setModificationTimeOnRead;
	}

	public void setSetModificationTimeOnRead(boolean setModificationTimeOnRead)
	{
		this.setModificationTimeOnRead = setModificationTimeOnRead;
	}
	
}