package gov.va.med.imaging.storage.cache.timer;

import gov.va.med.imaging.storage.cache.EvictionTimer;
import gov.va.med.imaging.storage.cache.EvictionTimerTask;
import gov.va.med.imaging.storage.cache.Messages;
import gov.va.med.imaging.storage.cache.exceptions.InitializationException;
import gov.va.med.imaging.storage.cache.exceptions.InvalidSweepIntervalSpecification;
import gov.va.med.imaging.storage.cache.exceptions.InvalidSweepSpecification;
import gov.va.med.imaging.storage.cache.memento.EvictionTimerImplMemento;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * A timer that can schedule all of the eviction strategies that are time dependent (i.e. the ones that 
 * say evict everything greater than x hours old).
 * The sweep time, that is when the eviction task will run and evict stuff, is determined by the maximum
 * age in the eviction strategy.  The timer task will schedule stuff so that the actual maximum age of a 
 * cache instance will be the eviction strategies maximum age plus some unit of time, how often the sweep runs.  
 * The actual sweep time is determined by the sweep interval map, which is a configuration parameter to this
 * class. 
 * 
 * @author VHAISWBECKEC
 *
 */
public class EvictionTimerImpl 
implements EvictionTimer
{
	public static final Long defaultAgeSpecification = new Long(Long.MAX_VALUE); //$NON-NLS-1$
	public static final String defaultScheduleSpecification = "0000:00:01:00:00:00@0000:00:00:00:00:00";  //$NON-NLS-1$

	private Timer timer;
	// Use a TreeMap to maintain the ordering by the key value (the max age)
	private Map<Long, SweepInterval> sweepIntervalMap = new TreeMap<Long, SweepInterval>(); 
	
	/**
	 * The constructor arg provided to EvictionTimerImpl maps the maximum age
	 * (in milliseconds) to the next scheduled sweep. The map is sorted
	 * internally so the lowest value greater than the max age is used when
	 * scheduling. There may be only one key value = "default", which indicates
	 * the default scheduling. The value specifies the next sweep time, either
	 * as an interval or as an interval plus a specific Date (if the @ sign is
	 * present). The interval and specific date time format is
	 * yyyy:MM:dd:hh:mm:ss. All fields MUST BE specified and the lengths MUST BE
	 * as specified, i.e. month="1" is illegal Everything is zero based,
	 * including months, i.e. April is 03, January is 00. Hours are zero-based
	 * on a 24 hour schedule, i.e. 00..23.
	 * 
	 * If no @ sign is present then the task will be run in the interval
	 * specified. Examples: 1.) if key="3600000" and value=
	 * "0000:00:00:00:01:00" then for tasks with an aging of less than one hour
	 * (3600000 milliseconds) run the task every minute. 2.) if key="86400000"
	 * and value= "0000:00:00:00:01:00" then for tasks with an aging of less
	 * than one day (86400000 milliseconds) run the task every hour. 3.) if
	 * key="60000" and value= "0000:00:00:00:00:36" then for tasks with an aging
	 * of less than one minute (60000 milliseconds) run the task every 36
	 * seconds.
	 * 
	 * If the @ sign is present then the task will be run in the delta
	 * specified, at the specified time. Examples: 1.) if key="3600000" and
	 * value= "0000:00:00:00:01:00@0000:00:00:00:00:20" then for tasks with an
	 * aging of less than one hour (3600000 milliseconds) run the task every
	 * minute on the 20 second boundary 2.) if key="default" and value=
	 * "0000:00:01:00:00:00@0000:00:00:03:00:00" then for tasks with an aging
	 * greater than any explicitly defined run the task every day at 3AM 3.) if
	 * key="default" and value= "0001:00:00:00:00:00@0000:04:07:03:23:34" then
	 * for tasks with an aging greater than any explicitly defined run the task
	 * every year at 3:23:34 on April 7 If the @ sign is present then the
	 * magnitutude of the boundary (the right of the @ sign) MUST BE less than
	 * the magnitude of the interval (the left of the @ sign). i.e. scheduling
	 * something to run every minute at 3AM will result in an error, scheduling
	 * something to run every 10 minutes on a 2 minute boundary will also fail
	 * (... okay, it should work but geeze ain't this enough!). The difference
	 * must be between components (i.e. hours/minutes or days/hours).
	 * 
	 * For example:
	 * Map sweepIntervalProperties = new HashMap();
	 * sweepIntervalProperties.add("3600000", "0000:00:00:00:01:00");	// if less than an hour then next minute
	 * sweepIntervalProperties.add("86400000", "0000:00:00:01:00:00");	// if less than a day then next hour
	 * sweepIntervalProperties.add("default", "0000:00:01:00:00:00@0000:00:00:03:00:00");	//else, run it at 3AM 
	 * EvictionTimerImpl evictionTimer = new EvictionTimerImpl(sweepIntervalProperties);
	 * 
	 * @param sweepIntervalProperties
	 * @throws InitializationException
	 * @throws InvalidSweepSpecification
	 */
	public static EvictionTimerImpl create(Map<Long, String> sweepIntervalProperties) 
	throws InitializationException, InvalidSweepSpecification
	{
		EvictionTimerImpl evictionTimer = new EvictionTimerImpl();
		boolean defaultSpecified = false;
		for(Iterator<Long> keyIter=sweepIntervalProperties.keySet().iterator(); keyIter.hasNext(); )
		{
			Long maxAge = keyIter.next();
			long max = 0L;
			if(defaultAgeSpecification.equals(maxAge))
				max = Long.MAX_VALUE;
			else
				max = maxAge.longValue();
			
			defaultSpecified = defaultSpecified | (max==Long.MAX_VALUE);
			evictionTimer.addSweepInterval( max, sweepIntervalProperties.get(maxAge) );
		}
		// if no default is specified supply a daily at 3AM, else some stuff may never run
		if(!defaultSpecified)
			evictionTimer.addSweepInterval( Long.MAX_VALUE, defaultScheduleSpecification );
		
		return evictionTimer;
	}
	
	/**
	 * Create an EvictionTimeImpl from a previously generated Memento.
	 * Note that this differs from a property map creation in that the
	 * Memento is assumed to have had its 'default' values turned into
	 * Long values. 
	 * 
	 * @param memento
	 * @return
	 * @throws InitializationException
	 * @throws InvalidSweepSpecification
	 */
	public static EvictionTimerImpl create(EvictionTimerImplMemento memento) 
	throws InitializationException, InvalidSweepSpecification
	{
		EvictionTimerImpl evictionTimer = new EvictionTimerImpl();

		for( Long max:memento.getSweepIntervalMap().keySet() )
			evictionTimer.addSweepInterval( max, memento.getSweepIntervalMap().get(max) );
		
		return evictionTimer;
	}
	
	/**
	 * 
	 * @return
	 * @throws InvalidSweepSpecification 
	 * @throws InitializationException 
	 */
	public static EvictionTimerImpl createDefault() 
	throws InitializationException, InvalidSweepSpecification
	{
		Map<Long, String> sweepIntervalProperties = new HashMap<Long, String>();
		
		sweepIntervalProperties.put(new Long(3600000), "0000:00:00:00:01:00");	// if less than an hour then next minute
		sweepIntervalProperties.put(new Long(86400000), "0000:00:00:01:00:00");	// if less than a day then next hour
		sweepIntervalProperties.put(defaultAgeSpecification, "0000:00:01:00:00:00@0000:00:00:03:00:00");	//else, run it at 3AM
		
		return create(sweepIntervalProperties);
	}
	
	// private constructor prevents creation except through create() methods
	private EvictionTimerImpl()
	{
		// set our name and daemon status
		timer = new Timer("CacheEvictionTimer", true); //$NON-NLS-1$
	}
	
	private void addSweepInterval(Long max, String sweepIntervalSpecification) 
	throws InvalidSweepSpecification
	{
		sweepIntervalMap.put( max, new SweepInterval(sweepIntervalSpecification) );
	}


	/**
	 * Schedule an eviction sweep at the specified interval.  This type of
	 * scheduling is intended for eviction strategies that are not age-based
	 * but simply need to run periodically.  This is a simple pass-through to
	 * the Timer method of the same name.
	 */
	public void scheduleSweep(EvictionTimerTask sweepTask, long delay, long interval)
	{
		timer.schedule((TimerTask)sweepTask, delay, interval);
	}

	/**
	 * Somewhat intelligently schedule the next sweep using the maximum age.
	 * 
	 * @param sweepTask
	 * @param maximumAge
	 */
	public void scheduleSweep(EvictionTimerTask sweepTask, long maximumAge)
	{
		Date nextSweepTime = null;
		SweepInterval scheduleSweepInterval = null;
		long sweepInterval = DateUtil.MILLISECONDS_IN_DAY;
		
		for(Iterator<Long> scheduleIter=sweepIntervalMap.keySet().iterator(); scheduleIter.hasNext(); )
		{
			Long scheduleSweepMax = scheduleIter.next();
			if( maximumAge <= scheduleSweepMax.longValue() )
			{
				scheduleSweepInterval = sweepIntervalMap.get(scheduleSweepMax);
				break;
			}

			// if the max is not found then use the default, which is always the last
			if(! scheduleIter.hasNext())
				scheduleSweepInterval = sweepIntervalMap.get(scheduleSweepMax);
		}
		
		nextSweepTime = scheduleSweepInterval.getNextSweepDate();
		sweepInterval = scheduleSweepInterval.getInterval().getPeriod();
		
		if(nextSweepTime != null)
		{
			try
			{
				schedule(sweepTask, nextSweepTime, sweepInterval);
			} 
			// An IllegalStateException could be because the task has already been scheduled
			// cancel it
			catch (IllegalStateException e)
			{
				sweepTask.cancel();		// cancel the task
				schedule(sweepTask, nextSweepTime, sweepInterval);
			}
		}
	}

	private Collection<EvictionTimerTask> tasks = new ArrayList<EvictionTimerTask>();
	/**
     * @see java.util.Timer#schedule(java.util.TimerTask, long, long)
     */
    public void schedule(EvictionTimerTask task, Date date, long period)
    {
    	tasks.add(task);
	    timer.schedule(task, date, period);
    }

    public void schedule(EvictionTimerTask task, long delay, long period)
    {
    	tasks.add(task);
	    timer.schedule(task, delay, period);
    }

    /**
     * Notify the tasks that they should shut down their worker threads
     * @return
     */
    private void cancelTasks()
    {
    	for(EvictionTimerTask task : tasks)
    		task.cancel();
    }
    
	/**
     * @see java.util.Timer#cancel()
     */
    @Override
    public void cancel()
    {
    	// notify all of our TimerTask that they must cancel,
    	// since many of the timer tasks run threads of their own, they must
    	// be notified to cancel else will hang on shutdown
    	cancelTasks();
    	
	    timer.cancel();
    }

	/**
	 * Get a copy of our state that may be serialized to persistent
	 * storage and used to recreate our state later.
	 * 
	 * @return
	 */
	public EvictionTimerImplMemento createMemento()
	{
		EvictionTimerImplMemento memento = new EvictionTimerImplMemento();
		
		for( Long max:sweepIntervalMap.keySet() )
			memento.addSweepInterval(max, sweepIntervalMap.get(max).toString());
		
		return memento;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof EvictionTimerImpl)
		{
			EvictionTimerImpl that = (EvictionTimerImpl)obj;
			
			if(this.sweepIntervalMap.size() != that.sweepIntervalMap.size())
				return false;
			
			for(Long max:this.sweepIntervalMap.keySet())
			{
				SweepInterval sweepInterval = this.sweepIntervalMap.get(max);
				SweepInterval thatSweepInterval = that.sweepIntervalMap.get(max);
				if(thatSweepInterval == null)
					return false;
				if(! sweepInterval.equals(thatSweepInterval))
					return false;
			}
			return true;
		}
		
		return false;
	}



	class SweepInterval
	{
		private Interval interval = null;
		private IntervalAnchor intervalAnchor = null;
		private String specification = null;			// the original specification that we are created with is saved
														// and is presented as the String representation of this class
		
		/**
		 * The format and interpretation of 'specification' is as follows:
		 * <interval>[@<interval-anchor>]
		 * where
		 * <interval> and <interval-anchor> are in the form (using java DateFormat pattern spec): "yyyy:MM:dd:hh:mm:ss"
		 * All fields of interval and interval-anchor must be specified and zero-filled to specified size.
		 * 
		 * Everything is zero based, including months, i.e. April is 03, January is 00.
		 * Hours are zero-based on a 24 hour schedule, i.e. 00..23.
		 * 
		 * The interval must specify a non-zero value in one and only one field, all others must be zero.
		 *
		 * If no @ sign is present then the task will be run in the interval specified.
		 * Examples: 
		 * 1.) if key="3600000" and value= "0000:00:00:00:01:00"
		 *     then for tasks with an aging of less than one hour (3600000 milliseconds) run the task every minute.
		 * 2.) if key="86400000" and value= "0000:00:00:00:01:00"
		 *     then for tasks with an aging of less than one day (86400000 milliseconds) run the task every hour.
		 * 3.) if key="60000" and value= "0000:00:00:00:00:36" 
		 *     then for tasks with an aging of less than one minute (60000 milliseconds) run the task every 36 seconds.
		 *     
		 * If the @ sign is present then the task will be run in the delta specified, at the specified time.
		 * Examples:
		 * 1.) if key="3600000" and value= "0000:00:00:00:01:00@0000:00:00:00:00:20" 
		 *     then for tasks with an aging of less than one hour (3600000 milliseconds) run the task every minute on the 20 second boundary
		 * 2.) if key="default" and value= "0000:00:01:00:00:00@0000:00:00:03:00:00"
		 *     then for tasks with an aging greater than any explicitly defined run the task every day at 3AM
		 * 3.) if key="default" and value= "0001:00:00:00:00:00@0000:04:07:03:23:34"
		 *     then for tasks with an aging greater than any explicitly defined run the task every year at 3:23:34 on April 7
		 * If the @ sign is present then the magnitutude of the boundary (the right of the @ sign) MUST BE less than the magnitude of
		 * the interval (the left of the @ sign).  i.e. scheduling something to run every minute at 3AM will result in an error, scheduling
		 * something to run every 10 minutes on a 2 minute boundary will also fail (... okay, it should work but geeze ain't this enough!).
		 * The difference must be between components (i.e. hours/minutes or days/hours).
		 * 
		 * @param specification
		 * @throws ParseException
		 * @throws InvalidSweepSpecification 
		 */
		SweepInterval(String specification) 
		throws InvalidSweepSpecification
		{
			if(specification == null)
				throw new InvalidSweepIntervalSpecification(Messages.getString("EvictionTimerImpl.4")); //$NON-NLS-1$
			
			this.specification = specification.trim();		// save this and return it on a toString()
			
			int atIndex = specification.indexOf('@');
			String intervalSpec = null;
			
			if(atIndex > 0)
			{
				intervalSpec = specification.substring(0, atIndex);
				String intervalAnchorSpec = specification.substring(atIndex+1);
				intervalAnchor = new IntervalAnchor(intervalAnchorSpec);
			}
			else
				intervalSpec = specification;
			
			interval = new Interval(intervalSpec);
			
			validate();
		}

		public Interval getInterval()
		{
			return this.interval;
		}

		public IntervalAnchor getIntervalAnchor()
		{
			return this.intervalAnchor;
		}

		/**
		 * Validate the interval and date fields
		 */
		private void validate()
		throws InvalidSweepSpecification
		{
			validateInterval();
			validateIntervalAnchor();
		}

		/**
		 * The interval field must specify one and only one non-zero value.
		 * 
		 * @throws InvalidSweepSpecification
		 */
		private void validateInterval() throws InvalidSweepSpecification
		{
			// one and only one of the Interval fields must be non-zero
			if(interval.getSecond() != 0)
			{
				if(interval.getMinute() != 0 || interval.getHour() != 0 || interval.getDay() != 0 ||
				   interval.getMonth() != 0 || interval.getYear() != 0)
					throw new InvalidSweepSpecification(Messages.getString("EvictionTimerImpl.5")); //$NON-NLS-1$
			}
			
			else if(interval.getMinute() != 0)
			{
				if(interval.getHour() != 0 || interval.getDay() != 0 ||
				   interval.getMonth() != 0 || interval.getYear() != 0)
					throw new InvalidSweepSpecification(Messages.getString("EvictionTimerImpl.6")); //$NON-NLS-1$
			}
			
			else if(interval.getHour() != 0)
			{
				if(interval.getDay() != 0 ||
				   interval.getMonth() != 0 || interval.getYear() != 0)
					throw new InvalidSweepSpecification(Messages.getString("EvictionTimerImpl.7")); //$NON-NLS-1$
			}
			
			else if(interval.getDay() != 0)
			{
				if(interval.getMonth() != 0 || interval.getYear() != 0)
					throw new InvalidSweepSpecification(Messages.getString("EvictionTimerImpl.8")); //$NON-NLS-1$
			}
			
			else if(interval.getMonth() != 0)
			{
				if(interval.getYear() != 0)
					throw new InvalidSweepSpecification(Messages.getString("EvictionTimerImpl.9")); //$NON-NLS-1$
			}
			
			else if(interval.getYear() == 0)
				throw new InvalidSweepSpecification(Messages.getString("EvictionTimerImpl.10")); //$NON-NLS-1$
		}

		/**
		 * 
		 *
		 */
		private void validateIntervalAnchor()
		{
			// hmmm, not much to do but leave it here for symmetry
		}

		/**
		 * Get the next sweep date from the present. 
		 * @return
		 */
		Date getNextSweepDate()
		{
			return getNextSweepDate(new Date());
		}

		/**
		 * Get the next sweep date after the given date.
		 * 
		 * @param after
		 * @return
		 */
		Date getNextSweepDate(Date after)
		{
			int intervalField = interval.mostSignificantCalendarField();
			int intervalFieldValue = interval.getFieldValue(intervalField);
			
			if(intervalAnchor == null)
				return DateUtil.next(after, intervalField, intervalFieldValue);
			else
			{
				Date then = DateUtil.next(after, intervalField, intervalFieldValue);
				int anchorField = intervalAnchor.mostSignificantCalendarField();
				int anchorValue = intervalAnchor.getFieldValue(anchorField);
				
				return DateUtil.nextOccurenceOfCalenderField(then, anchorField, anchorValue, 0);
			}
				
		}
		
		@Override
		public String toString()
		{
			return this.specification;
		}

		@Override
		public boolean equals(Object obj)
		{
			return (obj instanceof SweepInterval) && 
				specification.equals( ((SweepInterval)obj).specification );
		}
		
		
	}
	
	/**
	 * An interval between Date instances.
	 * 
	 * @author VHAISWBECKEC
	 *
	 */
	class Interval
	{
		// get an instance of the local calendar for validation of fields
		private Calendar validationCalendar = Calendar.getInstance();
		private int year = 0;
		private int month = 0;
		private int day = 0;
		private int hour = 0;
		private int minute = 0;
		private int second = 0;
		private int millisecond = 0;
		
		// interval must be in yyyy:MM:dd:hh:mm:ss
		private final Pattern intervalPattern = Pattern.compile(":"); //$NON-NLS-1$
		
		Interval(String intervalSpec) 
		throws InvalidSweepIntervalSpecification
		{
			try
			{
				String[] tokens = intervalPattern.split(intervalSpec);
				if(tokens.length > 0)
					year = Integer.parseInt(tokens[0]);
				if(tokens.length > 1)
					month = Integer.parseInt(tokens[1]);
				if(tokens.length > 2)
					day = Integer.parseInt(tokens[2]);
				if(tokens.length > 3)
					hour = Integer.parseInt(tokens[3]);
				if(tokens.length > 4)
					minute = Integer.parseInt(tokens[4]);
				if(tokens.length > 5)
					second = Integer.parseInt(tokens[5]);
				if(tokens.length > 6)
					millisecond = Integer.parseInt(tokens[6]);
			} 
			catch (NumberFormatException e)
			{
				throw new InvalidSweepIntervalSpecification(
						Messages.getString("EvictionTimerImpl.12") + intervalSpec + 
						Messages.getString("EvictionTimerImpl.13")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			validate();
		}
		
		private void validate()
		throws InvalidSweepIntervalSpecification
		{
			validateField(Calendar.YEAR);
			validateField(Calendar.MONTH);
			validateField(Calendar.DAY_OF_MONTH);
			validateField(Calendar.HOUR_OF_DAY);
			validateField(Calendar.MINUTE);
			validateField(Calendar.SECOND);
			validateField(Calendar.MILLISECOND);
		}
		
		private void validateField(int calendarField) 
		throws InvalidSweepIntervalSpecification
		{
			int fieldValue = getFieldValue(calendarField);
			
			if( fieldValue != 0 && fieldValue < validationCalendar.getMinimum(calendarField) )
				throw new InvalidSweepIntervalSpecification("Interval fields must be 0 or greater than or equal to the default Calendar minimum.");
			if( month != 0 && month > validationCalendar.getMaximum(calendarField) )
				throw new InvalidSweepIntervalSpecification("Interval fields must be 0 or less than or equal to the default Calendar maximum.");
			
		}

		public int getDay()
		{
			return this.day;
		}

		public int getHour()
		{
			return this.hour;
		}

		public int getMillisecond()
		{
			return this.millisecond;
		}

		public int getMinute()
		{
			return this.minute;
		}

		public int getMonth()
		{
			return this.month;
		}

		public int getSecond()
		{
			return this.second;
		}

		public int getYear()
		{
			return this.year;
		}
		
		int mostSignificantCalendarField()
		{
			return 
			year != 0 ? Calendar.YEAR :
			month != 0 ? Calendar.MONTH :
			day != 0 ? Calendar.DAY_OF_MONTH :
			hour != 0 ? Calendar.HOUR_OF_DAY :
			minute != 0 ? Calendar.MINUTE :
			second != 0 ? Calendar.SECOND : Calendar.MILLISECOND;
		}
		
		int getFieldValue(int field)
		{
			switch(field)
			{
			case Calendar.YEAR:
				return year;
			case Calendar.MONTH:
				return month;
			case Calendar.DAY_OF_MONTH:
				return day;
			case Calendar.HOUR_OF_DAY:
				return hour;
			case Calendar.MINUTE:
				return minute;
			case Calendar.SECOND:
				return second;
			case Calendar.MILLISECOND:
				return millisecond;
			default:
				return 0;
			}
		}
		
		/**
		 * For the purpose of this class a month is 30 days and a year is 365 days.
		 * The return value will be a long representing the number of milliseconds
		 * represented by this interval.
		 * 
		 * @return
		 */
		public long getPeriod()
		{
			return millisecond + 
				second * 1000L +
				minute * DateUtil.MILLISECONDS_IN_MINUTE +
				hour * DateUtil.MILLISECONDS_IN_HOUR +
				day * DateUtil.MILLISECONDS_IN_DAY +
				month * 30 * DateUtil.MILLISECONDS_IN_DAY +
				year * 365 * DateUtil.MILLISECONDS_IN_DAY;
		}
	}
	
	/**
	 * Semantically the Interval and IntervalAnchor are very different,
	 * it just so happens that the implementation is very similar.
	 * 
	 * @author VHAISWBECKEC
	 *
	 */
	class IntervalAnchor extends Interval
	{
		IntervalAnchor(String intervalSpec) 
		throws InvalidSweepIntervalSpecification
		{
			super(intervalSpec);
		}
	}
}
