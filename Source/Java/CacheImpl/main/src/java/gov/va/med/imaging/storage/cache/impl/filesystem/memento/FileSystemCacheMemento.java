package gov.va.med.imaging.storage.cache.impl.filesystem.memento;

import gov.va.med.imaging.storage.cache.impl.memento.PersistentRegionMemento;
import gov.va.med.imaging.storage.cache.memento.CacheMemento;
import gov.va.med.imaging.storage.cache.memento.RegionMemento;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains all of the fields required to persist and recreate the Cache instance. 
 * 
 * @author VHAISWBECKEC
 *
 */
public class FileSystemCacheMemento
extends CacheMemento
implements Serializable
{
	private static final long serialVersionUID = 1;
	
	public FileSystemCacheMemento() 
	{
	}

	private List<? extends RegionMemento> regionMementos = new ArrayList<PersistentRegionMemento>();
	public List<? extends RegionMemento> getRegionMementos()
	{
		return this.regionMementos;
	}
	
	public void setRegionMementos(List<? extends RegionMemento> regionMementos)
	{
		this.regionMementos = regionMementos;
	}
}