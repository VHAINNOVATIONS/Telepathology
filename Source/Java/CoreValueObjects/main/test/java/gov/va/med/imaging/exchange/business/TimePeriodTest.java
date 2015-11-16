package gov.va.med.imaging.exchange.business;

import java.util.Calendar;

import junit.framework.TestCase;

/**
 * @author vhaiswgraver
 * 
 */
public class TimePeriodTest extends TestCase {

	private TimePeriod hourTimePeriod;
	private TimePeriod hourEqualTimePeriod;
	private TimePeriod hourDayBeforeTimePeriod;
	private TimePeriod hourDayAfterTimePeriod;
	private TimePeriod hourContainedTimePeriod;
	private TimePeriod hourContainingTimePeriod;
	private TimePeriod hourIntersectingStartTimePeriod;
	private TimePeriod hourIntersectingEndTimePeriod;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();

		startCal.set(2010, 1, 1, 1, 0);
		endCal.set(2010, 1, 1, 2, 0);
		hourDayBeforeTimePeriod = new TimePeriod(startCal, endCal);

		startCal.set(2010, 1, 2, 1, 0);
		endCal.set(2010, 1, 2, 2, 0);
		hourTimePeriod = new TimePeriod(startCal, endCal);
		hourEqualTimePeriod = new TimePeriod(startCal, endCal);

		startCal.set(2010, 1, 3, 1, 0);
		endCal.set(2010, 1, 3, 2, 0);
		hourDayAfterTimePeriod = new TimePeriod(startCal, endCal);

		startCal.set(2010, 1, 2, 1, 20);
		endCal.set(2010, 1, 2, 1, 40);
		hourContainedTimePeriod = new TimePeriod(startCal, endCal);

		startCal.set(2010, 1, 2, 0, 40);
		endCal.set(2010, 1, 2, 2, 20);
		hourContainingTimePeriod = new TimePeriod(startCal, endCal);

		startCal.set(2010, 1, 2, 0, 20);
		endCal.set(2010, 1, 2, 1, 20);
		hourIntersectingStartTimePeriod = new TimePeriod(startCal, endCal);

		startCal.set(2010, 1, 2, 1, 20);
		endCal.set(2010, 1, 2, 2, 20);
		hourIntersectingEndTimePeriod = new TimePeriod(startCal, endCal);
	}

	// constructor tests

	public void testCreationFromDates() {
		TimePeriod period = new TimePeriod(hourTimePeriod.getStartCalendar()
				.getTime(), hourTimePeriod.getEndCalendar().getTime());
		assertEquals(hourTimePeriod.getStartCalendar().getTime(), period
				.getStartCalendar().getTime());
		assertEquals(hourTimePeriod.getEndCalendar().getTime(), period
				.getEndCalendar().getTime());
	}

	public void testCreationFromCalendars() {
		TimePeriod period = new TimePeriod(hourTimePeriod.getStartCalendar(),
				hourTimePeriod.getEndCalendar());
		assertEquals(hourTimePeriod.getStartCalendar(), period
				.getStartCalendar());
		assertEquals(hourTimePeriod.getEndCalendar(), period.getEndCalendar());
	}

	// after tests

	public void testAfterCalendarWhenAfter() {
		boolean actual = hourTimePeriod.after(hourDayBeforeTimePeriod
				.getStartCalendar());
		TestCase.assertEquals(true, actual);
	}

	public void testAfterCalendarWhenBefore() {
		boolean actual = hourTimePeriod.after(hourDayAfterTimePeriod
				.getStartCalendar());
		TestCase.assertEquals(false, actual);
	}

	public void testAfterCalendarWhenContains() {
		boolean actual = hourTimePeriod.after(hourContainedTimePeriod
				.getStartCalendar());
		TestCase.assertEquals(false, actual);
	}

	public void testAfterCalendarWhenEqualStart() {
		boolean actual = hourTimePeriod.after(hourEqualTimePeriod
				.getStartCalendar());
		TestCase.assertEquals(false, actual);
	}

	public void testAfterCalendarWhenEqualEnd() {
		boolean actual = hourTimePeriod.after(hourEqualTimePeriod
				.getEndCalendar());
		TestCase.assertEquals(false, actual);
	}

	public void testAfterDateWhenAfter() {
		boolean actual = hourTimePeriod.after(hourDayBeforeTimePeriod
				.getStartCalendar().getTime());
		TestCase.assertEquals(true, actual);
	}

	public void testAfterDateWhenBefore() {
		boolean actual = hourTimePeriod.after(hourDayAfterTimePeriod
				.getStartCalendar().getTime());
		TestCase.assertEquals(false, actual);
	}

	public void testAfterDateWhenContains() {
		boolean actual = hourTimePeriod.after(hourContainedTimePeriod
				.getStartCalendar().getTime());
		TestCase.assertEquals(false, actual);
	}

	public void testAfterDateWhenEqualStart() {
		boolean actual = hourTimePeriod.after(hourEqualTimePeriod
				.getStartCalendar().getTime());
		TestCase.assertEquals(false, actual);
	}

	public void testAfterDateWhenEqualEnd() {
		boolean actual = hourTimePeriod.after(hourEqualTimePeriod
				.getEndCalendar().getTime());
		TestCase.assertEquals(false, actual);
	}

	public void testAfterTimePeriodWhenAfter() {
		boolean actual = hourTimePeriod.after(hourDayBeforeTimePeriod);
		TestCase.assertEquals(true, actual);
	}

	public void testAfterTimePeriodWhenBefore() {
		boolean actual = hourTimePeriod.after(hourDayAfterTimePeriod);
		TestCase.assertEquals(false, actual);
	}

	public void testAfterTimePeriodWhenContains() {
		boolean actual = hourTimePeriod.after(hourContainedTimePeriod);
		TestCase.assertEquals(false, actual);
	}

	public void testAfterTimePeriodWhenEqual() {
		boolean actual = hourTimePeriod.after(hourEqualTimePeriod);
		TestCase.assertEquals(false, actual);
	}

	public void testAfterTimePeriodWhenIntersectingStart() {
		boolean actual = hourTimePeriod.after(hourIntersectingStartTimePeriod);
		TestCase.assertEquals(false, actual);
	}

	public void testAfterTimePeriodWhenIntersectingEnd() {
		boolean actual = hourTimePeriod.after(hourIntersectingEndTimePeriod);
		TestCase.assertEquals(false, actual);
	}

	public void testAfterTimePeriodWhenContained() {
		boolean actual = hourTimePeriod.after(hourContainingTimePeriod);
		TestCase.assertEquals(false, actual);
	}

	// before tests

	public void testBeforeCalendarWhenBefore() {
		boolean actual = hourTimePeriod.before(hourDayAfterTimePeriod
				.getStartCalendar());
		TestCase.assertEquals(true, actual);
	}

	public void testBeforeCalendarWhenAfter() {
		boolean actual = hourTimePeriod.before(hourDayBeforeTimePeriod
				.getStartCalendar());
		TestCase.assertEquals(false, actual);
	}

	public void testBeforeCalendarWhenContains() {
		boolean actual = hourTimePeriod.before(hourContainedTimePeriod
				.getStartCalendar());
		TestCase.assertEquals(false, actual);
	}

	public void testBeforeCalendarWhenEqualStart() {
		boolean actual = hourTimePeriod.before(hourEqualTimePeriod
				.getStartCalendar());
		TestCase.assertEquals(false, actual);
	}

	public void testBeforeCalendarWhenEqualEnd() {
		boolean actual = hourTimePeriod.before(hourEqualTimePeriod
				.getEndCalendar());
		TestCase.assertEquals(false, actual);
	}

	public void testBeforeDateWhenBefore() {
		boolean actual = hourTimePeriod.before(hourDayAfterTimePeriod
				.getStartCalendar().getTime());
		TestCase.assertEquals(true, actual);
	}

	public void testBeforeDateWhenAfter() {
		boolean actual = hourTimePeriod.before(hourDayBeforeTimePeriod
				.getStartCalendar().getTime());
		TestCase.assertEquals(false, actual);
	}

	public void testBeforeDateWhenContains() {
		boolean actual = hourTimePeriod.before(hourContainedTimePeriod
				.getStartCalendar().getTime());
		TestCase.assertEquals(false, actual);
	}

	public void testBeforeDateWhenEqualStart() {
		boolean actual = hourTimePeriod.before(hourEqualTimePeriod
				.getStartCalendar().getTime());
		TestCase.assertEquals(false, actual);
	}

	public void testBeforeDateWhenEqualEnd() {
		boolean actual = hourTimePeriod.before(hourEqualTimePeriod
				.getEndCalendar().getTime());
		TestCase.assertEquals(false, actual);
	}

	public void testBeforeTimePeriodWhenBefore() {
		boolean actual = hourTimePeriod.before(hourDayAfterTimePeriod);
		TestCase.assertEquals(true, actual);
	}

	public void testBeforeTimePeriodWhenAfter() {
		boolean actual = hourTimePeriod.before(hourDayBeforeTimePeriod);
		TestCase.assertEquals(false, actual);
	}

	public void testBeforeTimePeriodWhenContains() {
		boolean actual = hourTimePeriod.before(hourContainedTimePeriod);
		TestCase.assertEquals(false, actual);
	}

	public void testBeforeTimePeriodWhenEqual() {
		boolean actual = hourTimePeriod.before(hourEqualTimePeriod);
		TestCase.assertEquals(false, actual);
	}

	public void testBeforeTimePeriodWhenIntersectingStart() {
		boolean actual = hourTimePeriod.before(hourIntersectingStartTimePeriod);
		TestCase.assertEquals(false, actual);
	}

	public void testBeforeTimePeriodWhenIntersectingEnd() {
		boolean actual = hourTimePeriod.before(hourIntersectingEndTimePeriod);
		TestCase.assertEquals(false, actual);
	}

	public void testBeforeTimePeriodWhenContained() {
		boolean actual = hourTimePeriod.before(hourContainingTimePeriod);
		TestCase.assertEquals(false, actual);
	}

	// contains tests

	public void testContainsCalendarWhenBefore() {
		boolean actual = hourTimePeriod.contains(hourDayAfterTimePeriod
				.getStartCalendar());
		TestCase.assertEquals(false, actual);
	}

	public void testContainsCalendarWhenAfter() {
		boolean actual = hourTimePeriod.contains(hourDayBeforeTimePeriod
				.getStartCalendar());
		TestCase.assertEquals(false, actual);
	}

	public void testContainsCalendarWhenContains() {
		boolean actual = hourTimePeriod.contains(hourContainedTimePeriod
				.getStartCalendar());
		TestCase.assertEquals(true, actual);
	}

	public void testContainsCalendarWhenEqualStart() {
		boolean actual = hourTimePeriod.contains(hourEqualTimePeriod
				.getStartCalendar());
		TestCase.assertEquals(true, actual);
	}

	public void tesContainsCalendarWhenEqualEnd() {
		boolean actual = hourTimePeriod.contains(hourEqualTimePeriod
				.getEndCalendar());
		TestCase.assertEquals(true, actual);
	}

	public void testContainsDateWhenBefore() {
		boolean actual = hourTimePeriod.contains(hourDayAfterTimePeriod
				.getStartCalendar().getTime());
		TestCase.assertEquals(false, actual);
	}

	public void testContainsDateWhenAfter() {
		boolean actual = hourTimePeriod.contains(hourDayBeforeTimePeriod
				.getStartCalendar().getTime());
		TestCase.assertEquals(false, actual);
	}

	public void testContainsDateWhenContains() {
		boolean actual = hourTimePeriod.contains(hourContainedTimePeriod
				.getStartCalendar().getTime());
		TestCase.assertEquals(true, actual);
	}

	public void testContainsDateWhenEqualStart() {
		boolean actual = hourTimePeriod.contains(hourEqualTimePeriod
				.getStartCalendar().getTime());
		TestCase.assertEquals(true, actual);
	}

	public void testContainsDateWhenEqualEnd() {
		boolean actual = hourTimePeriod.contains(hourEqualTimePeriod
				.getEndCalendar().getTime());
		TestCase.assertEquals(true, actual);
	}

	public void testContainsTimePeriodWhenBefore() {
		boolean actual = hourTimePeriod.contains(hourDayAfterTimePeriod);
		TestCase.assertEquals(false, actual);
	}

	public void testContainsTimePeriodWhenAfter() {
		boolean actual = hourTimePeriod.contains(hourDayBeforeTimePeriod);
		TestCase.assertEquals(false, actual);
	}

	public void testContainsTimePeriodWhenContains() {
		boolean actual = hourTimePeriod.contains(hourContainedTimePeriod);
		TestCase.assertEquals(true, actual);
	}

	public void tesContainsTimePeriodWhenEqual() {
		boolean actual = hourTimePeriod.contains(hourEqualTimePeriod);
		TestCase.assertEquals(true, actual);
	}

	public void testContainsTimePeriodWhenIntersectingStart() {
		boolean actual = hourTimePeriod.contains(hourIntersectingStartTimePeriod);
		TestCase.assertEquals(false, actual);
	}

	public void testContainsTimePeriodWhenIntersectingEnd() {
		boolean actual = hourTimePeriod.contains(hourIntersectingEndTimePeriod);
		TestCase.assertEquals(false, actual);
	}

	public void testContainsTimePeriodWhenContained() {
		boolean actual = hourTimePeriod.contains(hourContainingTimePeriod);
		TestCase.assertEquals(false, actual);
	}

	// Intersects tests

	public void testIntersectsTimePeriodWhenBefore() {
		boolean actual = hourTimePeriod.intersects(hourDayAfterTimePeriod);
		TestCase.assertEquals(false, actual);
	}

	public void testIntersectsTimePeriodWhenAfter() {
		boolean actual = hourTimePeriod.intersects(hourDayBeforeTimePeriod);
		TestCase.assertEquals(false, actual);
	}

	public void testIntersectsTimePeriodWhenContains() {
		boolean actual = hourTimePeriod.intersects(hourContainedTimePeriod);
		TestCase.assertEquals(true, actual);
	}

	public void testIntersectsTimePeriodWhenEqual() {
		boolean actual = hourTimePeriod.intersects(hourEqualTimePeriod);
		TestCase.assertEquals(true, actual);
	}

	public void testIntersectsTimePeriodWhenIntersectingStart() {
		boolean actual = hourTimePeriod.intersects(hourIntersectingStartTimePeriod);
		TestCase.assertEquals(true, actual);
	}

	public void testIntersectsTimePeriodWhenIntersectingEnd() {
		boolean actual = hourTimePeriod.intersects(hourIntersectingEndTimePeriod);
		TestCase.assertEquals(true, actual);
	}

	public void testIntersectsTimePeriodWhenContained() {
		boolean actual = hourTimePeriod.intersects(hourContainingTimePeriod);
		TestCase.assertEquals(true, actual);
	}
}
