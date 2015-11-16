package gov.va.med.imaging.exchange.business;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WorkItemCountsTest 
{
	@Before
	public void setUp() throws Exception 
	{
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMissingOrInvalidStatus()
	{
		WorkItemCounts counts = getWorkItemCountsInstance();
		Assert.assertEquals(0,counts.getCountForSubtypeAndStatus("Subtype1", "InvalidStatus"));
		Assert.assertEquals(0,counts.getCountForSubtypeAndStatus("Subtype2", ""));
		Assert.assertEquals(0,counts.getCountForSubtypeAndStatus("Subtype2", null));
	}

	@Test
	public void testMissingOrInvalidSubtype()
	{
		WorkItemCounts counts = getWorkItemCountsInstance();
		Assert.assertEquals(0,counts.getCountForSubtypeAndStatus("InvalidSubtype", "Status1"));
		Assert.assertEquals(0,counts.getCountForSubtypeAndStatus("", "Status1"));
		Assert.assertEquals(0,counts.getCountForSubtypeAndStatus(null, "Status1"));
	}
	
	@Test
	public void testMissingOrInvalidSubtypeAndStatus()
	{
		WorkItemCounts counts = getWorkItemCountsInstance();
		Assert.assertEquals(0,counts.getCountForSubtypeAndStatus("InvalidSubtype", "InvalidSubtype"));
		Assert.assertEquals(0,counts.getCountForSubtypeAndStatus("", ""));
		Assert.assertEquals(0,counts.getCountForSubtypeAndStatus(null, null));
	}

	@Test
	public void testNullOrEmptySubtypes()
	{
		WorkItemCounts counts = getWorkItemCountsInstance();
		List<String> subtypes = new ArrayList<String>();
		
		Assert.assertEquals(0,counts.getCountForSubtypesAndStatus(subtypes, "Status1"));
		Assert.assertEquals(0,counts.getCountForSubtypesAndStatus(null, "Status1"));
	}

	@Test
	public void testNullOrEmptyStatuses()
	{
		WorkItemCounts counts = getWorkItemCountsInstance();
		List<String> statuses = new ArrayList<String>();
		
		Assert.assertEquals(0,counts.getCountForSubtypeAndStatuses("Subtype1", statuses));
		Assert.assertEquals(0,counts.getCountForSubtypeAndStatuses("Subtype1", null));
	}

	@Test
	public void testNullOrEmptySubtypesAndStatuses()
	{
		WorkItemCounts counts = getWorkItemCountsInstance();
		List<String> subtypes = new ArrayList<String>();
		List<String> statuses = new ArrayList<String>();
		
		Assert.assertEquals(0,counts.getCountForSubtypesAndStatuses(subtypes, statuses));
		Assert.assertEquals(0,counts.getCountForSubtypesAndStatuses(null, null));
	}

	@Test
	public void testGetCountForSubtypeAndStatus()
	{
		WorkItemCounts counts = getWorkItemCountsInstance();
		Assert.assertEquals(1,counts.getCountForSubtypeAndStatus("Subtype1", "Status1"));
		Assert.assertEquals(10,counts.getCountForSubtypeAndStatus("Subtype2", "Status1"));
		Assert.assertEquals(100,counts.getCountForSubtypeAndStatus("Subtype3", "Status1"));
		Assert.assertEquals(2,counts.getCountForSubtypeAndStatus("Subtype1", "Status2"));
		Assert.assertEquals(20,counts.getCountForSubtypeAndStatus("Subtype2", "Status2"));
		Assert.assertEquals(200,counts.getCountForSubtypeAndStatus("Subtype3", "Status2"));
		Assert.assertEquals(3,counts.getCountForSubtypeAndStatus("Subtype1", "Status3"));
		Assert.assertEquals(30,counts.getCountForSubtypeAndStatus("Subtype2", "Status3"));
		Assert.assertEquals(300,counts.getCountForSubtypeAndStatus("Subtype3", "Status3"));
	}

	@Test
	public void testGetCountForSubtypesAndStatus()
	{
		WorkItemCounts counts = getWorkItemCountsInstance();
		List<String> subtypes = new ArrayList<String>();
		subtypes.add("Subtype1");
		
		Assert.assertEquals(1,counts.getCountForSubtypesAndStatus(subtypes, "Status1"));
		Assert.assertEquals(2,counts.getCountForSubtypesAndStatus(subtypes, "Status2"));
		Assert.assertEquals(3,counts.getCountForSubtypesAndStatus(subtypes, "Status3"));
		
		subtypes.add("Subtype2");
		Assert.assertEquals(11,counts.getCountForSubtypesAndStatus(subtypes, "Status1"));
		Assert.assertEquals(22,counts.getCountForSubtypesAndStatus(subtypes, "Status2"));
		Assert.assertEquals(33,counts.getCountForSubtypesAndStatus(subtypes, "Status3"));
		
		subtypes.add("Subtype3");
		Assert.assertEquals(111,counts.getCountForSubtypesAndStatus(subtypes, "Status1"));
		Assert.assertEquals(222,counts.getCountForSubtypesAndStatus(subtypes, "Status2"));
		Assert.assertEquals(333,counts.getCountForSubtypesAndStatus(subtypes, "Status3"));
		
	}

	@Test
	public void testGetCountForSubtypeAndStatuses()
	{
		WorkItemCounts counts = getWorkItemCountsInstance();
		List<String> statuses = new ArrayList<String>();
		statuses.add("Status1");
		
		Assert.assertEquals(1,counts.getCountForSubtypeAndStatuses("Subtype1", statuses));
		Assert.assertEquals(10,counts.getCountForSubtypeAndStatuses("Subtype2", statuses));
		Assert.assertEquals(100,counts.getCountForSubtypeAndStatuses("Subtype3", statuses));
		
		statuses.add("Status2");
		Assert.assertEquals(3,counts.getCountForSubtypeAndStatuses("Subtype1", statuses));
		Assert.assertEquals(30,counts.getCountForSubtypeAndStatuses("Subtype2", statuses));
		Assert.assertEquals(300,counts.getCountForSubtypeAndStatuses("Subtype3", statuses));
		
		statuses.add("Status3");
		Assert.assertEquals(6,counts.getCountForSubtypeAndStatuses("Subtype1", statuses));
		Assert.assertEquals(60,counts.getCountForSubtypeAndStatuses("Subtype2", statuses));
		Assert.assertEquals(600,counts.getCountForSubtypeAndStatuses("Subtype3", statuses));
		
	}

	@Test
	public void testGetCountForSubtypesAndStatuses()
	{
		WorkItemCounts counts = getWorkItemCountsInstance();
		
		List<String> statuses = new ArrayList<String>();
		List<String> subtypes = new ArrayList<String>();

		statuses.add("Status1");
		subtypes.add("Subtype1");
		
		Assert.assertEquals(1,counts.getCountForSubtypesAndStatuses(subtypes, statuses));

		subtypes.add("Subtype2");
		statuses.add("Status2");
		Assert.assertEquals(33,counts.getCountForSubtypesAndStatuses(subtypes, statuses));
		
		subtypes.add("Subtype3");
		statuses.add("Status3");
		Assert.assertEquals(666,counts.getCountForSubtypesAndStatuses(subtypes, statuses));
		
	}

	private WorkItemCounts getWorkItemCountsInstance() 
	{
		WorkItemCounts counts = new WorkItemCounts();
		counts.addCountForSubtypeAndStatus("Subtype1", "Status1", 1);
		counts.addCountForSubtypeAndStatus("Subtype1", "Status2", 2);
		counts.addCountForSubtypeAndStatus("Subtype1", "Status3", 3);
		counts.addCountForSubtypeAndStatus("Subtype2", "Status1", 10);
		counts.addCountForSubtypeAndStatus("Subtype2", "Status2", 20);
		counts.addCountForSubtypeAndStatus("Subtype2", "Status3", 30);
		counts.addCountForSubtypeAndStatus("Subtype3", "Status1", 100);
		counts.addCountForSubtypeAndStatus("Subtype3", "Status2", 200);
		counts.addCountForSubtypeAndStatus("Subtype3", "Status3", 300);
		
		return counts;
	}
}
