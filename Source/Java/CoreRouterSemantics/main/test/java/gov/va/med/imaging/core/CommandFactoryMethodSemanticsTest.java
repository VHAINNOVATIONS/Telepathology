/**
 * 
 */
package gov.va.med.imaging.core;

import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class CommandFactoryMethodSemanticsTest 
extends TestCase
{
	private String[] validElements = {
		"createPrefetchPatientStudyListCommand",
		"createPrefetchPatientIdentificationImageCommand"
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
			CommandFactoryMethodSemantics.create(validElement);
		}
	}

	public void testValidCommandClassName() 
	throws CoreRouterSemanticsException
	{
		CommandFactoryMethodSemantics method = null;
		
		method = CommandFactoryMethodSemantics.create("createGetStudyCommand");
		assertEquals(Action.GET, method.getAction());
		assertEquals("Study", method.getObject());
		assertEquals("Study", method.getObjectSimpleName());
		assertEquals(null, method.getObjectCollectionType());
		assertEquals(null, method.getPreposition());
		assertEquals(null, method.getObjectOfPreposition());
		
		method = CommandFactoryMethodSemantics.create("createGetStudyListCommand");
		assertEquals(Action.GET, method.getAction());
		assertEquals("StudyList", method.getObject());
		assertEquals("Study", method.getObjectSimpleName());
		assertEquals("List", method.getObjectCollectionType());
		assertEquals(null, method.getPreposition());
		assertEquals(null, method.getObjectOfPreposition());
		
		method = CommandFactoryMethodSemantics.create("createGetInstanceByInstanceURNCommand");
		assertEquals(Action.GET, method.getAction());
		assertEquals("Instance", method.getObject());
		assertEquals("Instance", method.getObjectSimpleName());
		assertEquals(null, method.getObjectCollectionType());
		assertEquals(Preposition.BY, method.getPreposition());
		assertEquals("InstanceURN", method.getObjectOfPreposition());
		
		method = CommandFactoryMethodSemantics.create("createGetStudyListByPatientICNCommand");
		assertEquals(Action.GET, method.getAction());
		assertEquals("StudyList", method.getObject());
		assertEquals("Study", method.getObjectSimpleName());
		assertEquals("List", method.getObjectCollectionType());
		assertEquals(Preposition.BY, method.getPreposition());
		assertEquals("PatientICN", method.getObjectOfPreposition());
		
		
		method = CommandFactoryMethodSemantics.create("createReadStudyListByPatientICNCommand");
		assertEquals(Action.GET, method.getAction());
		assertEquals("StudyList", method.getObject());
		assertEquals("Study", method.getObjectSimpleName());
		assertEquals("List", method.getObjectCollectionType());
		assertEquals(Preposition.BY, method.getPreposition());
		assertEquals("PatientICN", method.getObjectOfPreposition());
		
		method = CommandFactoryMethodSemantics.create("createPostImageAccessEventCommand");
		assertEquals(Action.POST, method.getAction());
		assertEquals("ImageAccessEvent", method.getObject());
		assertEquals("ImageAccessEvent", method.getObjectSimpleName());
		assertEquals(null, method.getObjectCollectionType());
		assertEquals(null, method.getPreposition());
		assertEquals(null, method.getObjectOfPreposition());
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
