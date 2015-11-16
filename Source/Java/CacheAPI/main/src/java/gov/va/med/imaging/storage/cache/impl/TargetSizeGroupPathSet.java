/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl;

import gov.va.med.imaging.storage.cache.Group;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

import java.util.*;

/**
 * A Set of Group instances that are:
 * 1.) maintained in ascending order by last accessed date
 * 2.) restricted such that the total of Group.getSize() for all instances in the group
 *     never exceeds the given value
 *     
 * This class is used in the storage threshold eviction to maintain a collection of
 * groups that are to be evicted.  The collective size of the groups is as close to what
 * is needed such that when they are all evicted the resulting free space meets the target
 * free space.
 * 
 * This class is NOT thread safe.
 * 
 * @author VHAISWBECKEC
 *
 */
public class TargetSizeGroupPathSet 
implements Iterable<GroupPath>
{
	// the maximum total size that all groups in this set should ever attain
	private final long targetSize;
	// keep a running total to avoid iterations to add the sizes
	private long totalSize = 0L;
	// the collective total of all of the groups that were visited
	private long totalVisitedSize = 0L;
	
	// create a set that maintains Groups in ascending order by age
	// i.e. the oldest Groups are earlier in the list
	private SortedSet<GroupPath> wrapped = 
		new TreeSet<GroupPath>( new Comparator<GroupPath>()
		{
			public int compare(GroupPath group1, GroupPath group2)
			{
				try
				{
					return 
						group1.getGroup().getLastAccessed().getTime() < group2.getGroup().getLastAccessed().getTime() ? -1 :
						group1.getGroup().getLastAccessed().getTime() > group2.getGroup().getLastAccessed().getTime() ? 1 : 0;
				} 
				catch (CacheException x)
				{
					x.printStackTrace();
					return 0;
				}
			}
		} );
	
	public TargetSizeGroupPathSet(long targetSize)
	{
		this.targetSize = targetSize;
	}
	
	/**
	 * @return
	 */
	public long getTargetSize()
	{
		return this.targetSize;
	}
	
	/* (non-Javadoc)
	 * @see java.util.List#add(java.lang.Object)
	 */
	public synchronized void add(GroupPath group)
	{
		try
		{
			wrapped.add(group);
			totalSize += group.getGroup().getSize();
			totalVisitedSize += group.getGroup().getSize();
			if(totalSize > getTargetSize())
				trim();
		} 
		catch (CacheException x)
		{
			x.printStackTrace();
		}
	}

	/**
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public synchronized void addAll(Collection<GroupPath> c)
	{
		// this is a bit more efficient than doing multiple add operations
		// add them all then trim back to the target size
		try
		{
			for(GroupPath groupPath : c)
			{
				wrapped.add(groupPath);
				totalSize += groupPath.getGroup().getSize();
				totalVisitedSize += groupPath.getGroup().getSize();
			}
			if(totalSize > getTargetSize())
				trim();
		} 
		catch (CacheException x)
		{
			x.printStackTrace();
		}
	}
	
	/**
	 * Get the collective size of all of the visited regions.
	 * 
	 * @return
	 */
	long getTotalVisitedSize()
	{
		return this.totalVisitedSize;
	}

	public void addAll(TargetSizeGroupPathSet groupSet)
	{
		addAll(groupSet.wrapped);
	}
	
	/**
	 * Trim the instances in the cache such that the total size is less than the
	 * target size.  Trim from the last() of the set (i.e. the newest).
	 * 
	 * @throws CacheException 
	 * 
	 */
	private synchronized void trim() 
	throws CacheException
	{
		if(wrapped.size() < 1)
			return;
		
		for(GroupPath lastGroup = wrapped.last(); lastGroup != null && totalSize > getTargetSize(); )
		{
			// if the Group.getSize returns zero then leave it on the list
			if(lastGroup.getGroup().getSize() > 0)
			{
				totalSize -= lastGroup.getGroup().getSize();
				wrapped.remove(lastGroup);
				try
				{
					lastGroup = wrapped.last();
				} 
				catch (NoSuchElementException x)
				{
					lastGroup = null;
				}
			}
		}
	}


	/* (non-Javadoc)
	 * @see java.util.List#clear()
	 */
	public synchronized void clear()
	{
		wrapped.clear();
		totalSize = 0L;
	}

	/* (non-Javadoc)
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Group group)
	{
		return wrapped.contains(group);
	}

	/* (non-Javadoc)
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<Group> c)
	{
		return wrapped.containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty()
	{
		return wrapped.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.List#iterator()
	 */
	public Iterator<GroupPath> iterator()
	{
		// override the iterator to disallow remove()
		return new Iterator<GroupPath>()
		{
			private Iterator<GroupPath> wrappedIterator = wrapped.iterator();
			public boolean hasNext()
			{
				return wrappedIterator.hasNext();
			}

			public GroupPath next()
			{
				return wrappedIterator.next();
			}

			public void remove()
			{
				throw new UnsupportedOperationException();
			}
			
		};
	}

	/* (non-Javadoc)
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public synchronized boolean remove(GroupPath group)
	{
		if( wrapped.remove(group) )
		{
			try
			{
				totalSize -= group.getGroup().getSize();
				return true;
			} 
			catch (CacheException x)
			{
				x.printStackTrace();
				wrapped.add(group);		// add it back in to stay consistent
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.List#size()
	 */
	public int size()
	{
		return wrapped.size();
	}

	/* (non-Javadoc)
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray()
	{
		return wrapped.toArray();
	}

	/* (non-Javadoc)
	 * @see java.util.List#toArray(T[])
	 */
	public Object[] toArray(Object[] a)
	{
		return wrapped.toArray(a);
	}

}
