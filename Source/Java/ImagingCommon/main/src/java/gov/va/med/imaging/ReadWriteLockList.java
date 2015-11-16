package gov.va.med.imaging;

import java.util.List;

public interface ReadWriteLockList<T>
extends List<T>
{
	public void clearAndAddAll(List<T> list);
}
