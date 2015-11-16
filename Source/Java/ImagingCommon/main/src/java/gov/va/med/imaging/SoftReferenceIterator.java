package gov.va.med.imaging;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Iterator;

/**
 * A wrapper around an iterator that deals with SoftReferences
 * and returns the underlying instances.
 * 
 * NOTE: the next() method MAY RETURN NULL, if the weak reference
 * has fallen out of scope.  Also the returned iterator may
 * throw ConcurrentModificationException.
 */
public class SoftReferenceIterator<T>
implements Iterator<T>
{
	Iterator<SoftReference<T>> wrappedIterator = null;
	
	public SoftReferenceIterator(Collection<SoftReference<T>> softReferenceCollection)
	{
		wrappedIterator = softReferenceCollection.iterator();
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