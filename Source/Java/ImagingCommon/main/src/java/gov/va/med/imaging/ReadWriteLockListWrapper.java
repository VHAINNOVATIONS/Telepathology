package gov.va.med.imaging;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 
 * @author VHAISWBECKEC
 *
 * @param <E>
 */
public class ReadWriteLockListWrapper<E> 
implements ReadWriteLockList<E>
{
	private List<E> wrappedList = null;
	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	
	ReadWriteLockListWrapper(List<E> wrappedList)
	{
		this.wrappedList = wrappedList;
		this.readWriteLock = new ReentrantReadWriteLock();
	}
	
	/**
	 * A private constructor used for sub lists, where the underlying list
	 * is a subset of our underlying list and we use the same readWriteLock
	 * to assure synchronization.
	 * 
	 * @param wrappedList
	 * @param readWriteLock
	 */
	private ReadWriteLockListWrapper(List<E> wrappedList, ReadWriteLock readWriteLock)
	{
		this.wrappedList = wrappedList;
		this.readWriteLock = readWriteLock;
	}

	/**
	 * @return the readWriteLock
	 */
	private ReadWriteLock getReadWriteLock()
	{
		return this.readWriteLock;
	}

	public boolean add(E element)
	{
		try
		{
			readWriteLock.writeLock().lock();
			return wrappedList.add(element);
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.writeLock().unlock();
		}
	}

	public void add(int index, E element)
	{
		try
		{
			readWriteLock.writeLock().lock();
			wrappedList.add(index, element);
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.writeLock().unlock();
		}
	}

	// unchecked warnings suppressed to allow addAll to implement
	// List interface
	@SuppressWarnings("unchecked")
	public boolean addAll(Collection<? extends E> c)
	{
		try
		{
			readWriteLock.writeLock().lock();
			return wrappedList.addAll(c);
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.writeLock().unlock();
		}
	}

	public boolean addAll(int index, Collection<? extends E> c)
	{
		try
		{
			readWriteLock.writeLock().lock();
			return wrappedList.addAll(index, c);
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.writeLock().unlock();
		}
	}

	public void clear()
	{
		try
		{
			readWriteLock.writeLock().lock();
			wrappedList.clear();
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.writeLock().unlock();
		}
	}

	public void clearAndAddAll(List<E> list)
	{
		try
		{
			readWriteLock.writeLock().lock();
			wrappedList.clear();
			wrappedList.addAll(list);
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.writeLock().unlock();
		}
	}
	
	public boolean contains(Object o)
	{
		try
		{
			readWriteLock.readLock().lock();
			return wrappedList.contains(o);
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.readLock().unlock();
		}
	}

	public boolean containsAll(Collection<?> c)
	{
		try
		{
			readWriteLock.readLock().lock();
			return wrappedList.containsAll(c);
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.readLock().unlock();
		}
	}

	public E get(int index)
	{
		try
		{
			readWriteLock.writeLock().lock();
			return wrappedList.get(index);
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.writeLock().unlock();
		}
	}

	public int indexOf(Object o)
	{
		try
		{
			readWriteLock.readLock().lock();
			return wrappedList.indexOf(o);
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.readLock().unlock();
		}
	}

	public int lastIndexOf(Object o)
	{
		try
		{
			readWriteLock.readLock().lock();
			return wrappedList.lastIndexOf(o);
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.readLock().unlock();
		}
	}

	public boolean isEmpty()
	{
		try
		{
			readWriteLock.readLock().lock();
			return wrappedList.isEmpty();
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.readLock().unlock();
		}
	}

	public boolean remove(Object o)
	{
		try
		{
			readWriteLock.writeLock().lock();
			return wrappedList.remove(o);
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.writeLock().unlock();
		}
	}

	public E remove(int index)
	{
		try
		{
			readWriteLock.writeLock().lock();
			return wrappedList.remove(index);
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.writeLock().unlock();
		}
	}

	public boolean removeAll(Collection<?> c)
	{
		try
		{
			readWriteLock.writeLock().lock();
			return wrappedList.removeAll(c);
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.writeLock().unlock();
		}
	}

	public boolean retainAll(Collection<?> c)
	{
		try
		{
			readWriteLock.writeLock().lock();
			return wrappedList.retainAll(c);
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.writeLock().unlock();
		}
	}

	public E set(int index, E element)
	{
		try
		{
			readWriteLock.writeLock().lock();
			return wrappedList.set(index, element);
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.writeLock().unlock();
		}
	}

	public int size()
	{
		try
		{
			readWriteLock.readLock().lock();
			return wrappedList.size();
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.readLock().unlock();
		}
	}

	/**
	 * The returned sublist uses the same readWriteLock as this instance.
	 */
	public List<E> subList(int fromIndex, int toIndex)
	{
		try
		{
			readWriteLock.readLock().lock();
			return new ReadWriteLockListWrapper<E>( wrappedList.subList(fromIndex, toIndex), this.readWriteLock );
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.readLock().unlock();
		}
	}

	public Object[] toArray()
	{
		try
		{
			readWriteLock.writeLock().lock();
			return wrappedList.toArray();
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.writeLock().unlock();
		}
	}

	@SuppressWarnings("unchecked")
	public Object[] toArray(Object[] a)
	{
		try
		{
			readWriteLock.writeLock().lock();
			return wrappedList.toArray(a);
		}
		catch(RuntimeException rX)
		{
			throw rX;
		}
		finally
		{
			readWriteLock.writeLock().unlock();
		}
	}
	
	public Iterator<E> iterator()
	{
		return new ListReadWriteLockIterator(this);
	}

	public ListIterator<E> listIterator()
	{
		return new ListReadWriteLockIterator(this);
	}

	public ListIterator<E> listIterator(int index)
	{
		return new ListReadWriteLockIterator(this, index);
	}
	
	/**
	 * The iterator class wraps the iterator returned by the wrapped class
	 * using the lists readWriteLock for synchronization.
	 * 
	 * @author VHAISWBECKEC
	 *
	 */
	class ListReadWriteLockIterator
	implements ListIterator<E>
	{
		private ReadWriteLockListWrapper<E> list = null;
		private ListIterator<E> wrappedIterator = null;
		
		ListReadWriteLockIterator(ReadWriteLockListWrapper<E> list)
		{
			this(list, 0);
		}

		ListReadWriteLockIterator(ReadWriteLockListWrapper<E> list, int initialIndex)
		{
			this.list = list;
			this.wrappedIterator = this.list.listIterator(initialIndex);
		}
		
		public void add(E o)
		{
			try
			{
				list.readWriteLock.writeLock().lock();
				wrappedIterator.add(o);
			}
			catch(RuntimeException rX)
			{
				throw rX;
			}
			finally
			{
				list.readWriteLock.writeLock().unlock();
			}
		}

		public boolean hasNext()
		{
			try
			{
				list.readWriteLock.readLock().lock();
				return wrappedIterator.hasNext();
			}
			catch(RuntimeException rX)
			{
				throw rX;
			}
			finally
			{
				list.readWriteLock.readLock().unlock();
			}
		}

		public boolean hasPrevious()
		{
			try
			{
				list.readWriteLock.readLock().lock();
				return wrappedIterator.hasPrevious();
			}
			catch(RuntimeException rX)
			{
				throw rX;
			}
			finally
			{
				list.readWriteLock.readLock().unlock();
			}
		}

		public E next()
		{
			try
			{
				list.readWriteLock.readLock().lock();
				return wrappedIterator.next();
			}
			catch(RuntimeException rX)
			{
				throw rX;
			}
			finally
			{
				list.readWriteLock.readLock().unlock();
			}
		}

		public int nextIndex()
		{
			try
			{
				list.readWriteLock.readLock().lock();
				return wrappedIterator.nextIndex();
			}
			catch(RuntimeException rX)
			{
				throw rX;
			}
			finally
			{
				list.readWriteLock.readLock().unlock();
			}
		}

		public E previous()
		{
			try
			{
				list.readWriteLock.readLock().lock();
				return wrappedIterator.previous();
			}
			catch(RuntimeException rX)
			{
				throw rX;
			}
			finally
			{
				list.readWriteLock.readLock().unlock();
			}
		}

		public int previousIndex()
		{
			try
			{
				list.readWriteLock.readLock().lock();
				return wrappedIterator.previousIndex();
			}
			catch(RuntimeException rX)
			{
				throw rX;
			}
			finally
			{
				list.readWriteLock.readLock().unlock();
			}
		}

		public void remove()
		{
			try
			{
				list.readWriteLock.writeLock().lock();
				wrappedIterator.remove();
			}
			catch(RuntimeException rX)
			{
				throw rX;
			}
			finally
			{
				list.readWriteLock.writeLock().unlock();
			}
		}

		public void set(E o)
		{
			try
			{
				list.readWriteLock.readLock().lock();
				wrappedIterator.set(o);
			}
			catch(RuntimeException rX)
			{
				throw rX;
			}
			finally
			{
				list.readWriteLock.readLock().unlock();
			}
		}
		
	}
}
