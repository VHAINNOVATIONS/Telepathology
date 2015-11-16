package gov.va.med.imaging;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;

/**
 * A wrapper around an iterator that deals with WeakReferences
 * and returns the underlying instances.
 * 
 * NOTE: the next() method MAY RETURN NULL, if the weak reference
 * has fallen out of scope.  Also the returned iterator may
 * throw ConcurrentModificationException.
 */
public class WeakReferenceIterator<T>
implements Iterator<T>
{
	Iterator<WeakReference<T>> wrappedIterator = null;
	
	public WeakReferenceIterator(Collection<WeakReference<T>> weakReferenceCollection)
	{
		wrappedIterator = weakReferenceCollection.iterator();
	}
	
	public boolean hasNext()
	{
		return wrappedIterator.hasNext();
	}

	public T next()
	{
		return wrappedIterator.next().get();
	}

	public void remove()
	{
		wrappedIterator.remove();
	}
}