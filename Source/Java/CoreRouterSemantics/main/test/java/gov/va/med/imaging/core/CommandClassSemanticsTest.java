/**
 * 
 */
package gov.va.med.imaging.core;

import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class CommandClassSemanticsTest 
extends TestCase
{
	private String[] validElements = {
		"GetSiteConnectivityStatusCommand"
	};
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() 
	throws Exception
	{
		super.setUp();
		
		System.out.println("Command Class Regex = '" + CoreRouterSemantics.getCommandClassNameRegex() + "'.");
	}
	
	public void testKnownValidElements()
	throws CoreRouterSemanticsException
	{
		for(String validElement : validElements)
		{
			CommandClassSemantics.create(validElement, null);
		}
	}
	
	public void testValidCommandClassName() 
	throws CoreRouterSemanticsException
	{
		CommandClassSemantics method = null;
		method = CommandClassSemantics.create("GetStudyCommand", null);
		assertEquals(Action.GET, method.getAction());
		assertEquals("Study", method.getObject());
		assertEquals("Study", method.getObjectSimpleName());
		assertEquals(null, method.getObjectCollectionType());
		assertEquals(null, method.getPreposition());
		assertEquals(null, method.getObjectOfPreposition());
		
		method = CommandClassSemantics.create("GetStudyListCommand", null);
		assertEquals(Action.GET, method.getAction());
		assertEquals("StudyList", method.getObject());
		assertEquals("Study", method.getObjectSimpleName());
		assertEquals("List", method.getObjectCollectionType());
		assertEquals(null, method.getPreposition());
		assertEquals(null, method.getObjectOfPreposition());
		
		method = CommandClassSemantics.create("GetInstanceByInstanceURNCommand", null);
		assertEquals(Action.GET, method.getAction());
		assertEquals("Instance", method.getObject());
		assertEquals("Instance", method.getObjectSimpleName());
		assertEquals(null, method.getObjectCollectionType());
		assertEquals(Preposition.BY, method.getPreposition());
		assertEquals("InstanceURN", method.getObjectOfPreposition());
		
		method = CommandClassSemantics.create("GetStudyListByPatientICNCommand", null);
		assertEquals(Action.GET, method.getAction());
		assertEquals("StudyList", method.getObject());
		assertEquals("Study", method.getObjectSimpleName());
		assertEquals("List", method.getObjectCollectionType());
		assertEquals(Preposition.BY, method.getPreposition());
		assertEquals("PatientICN", method.getObjectOfPreposition());
		
		
		method = CommandClassSemantics.create("ReadStudyListByPatientICNCommand", null);
		assertEquals(Action.GET, method.getAction());
		assertEquals("StudyList", method.getObject());
		assertEquals("Study", method.getObjectSimpleName());
		assertEquals("List", method.getObjectCollectionType());
		assertEquals(Preposition.BY, method.getPreposition());
		assertEquals("PatientICN", method.getObjectOfPreposition());
	}
	
	public void testInvalidMethods()
	{
		try
		{
			CommandClassSemantics.create("get", null);
			fail("Class named 'get' should have failed and did not");
		} 
		catch (CoreRouterSemanticsException x){}
		
	}
}
