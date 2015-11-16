/**
 * 
 */
package gov.va.med.imaging.core;

import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class RouterMethodSemanticsTest 
extends TestCase
{
	public void testValidMethods() 
	throws CoreRouterSemanticsException
	{
		RouterMethodSemantics method = null;
		method = RouterMethodSemantics.create("getStudy");
		assertEquals(Action.GET, method.getAction());
		assertEquals("Study", method.getObject());
		assertEquals("Study", method.getObjectSimpleName());
		assertEquals(null, method.getObjectCollectionType());
		assertEquals(null, method.getPreposition());
		assertEquals(null, method.getObjectOfPreposition());
		
		method = RouterMethodSemantics.create("getStudyList");
		assertEquals(Action.GET, method.getAction());
		assertEquals("StudyList", method.getObject());
		assertEquals("Study", method.getObjectSimpleName());
		assertEquals("List", method.getObjectCollectionType());
		assertEquals(null, method.getPreposition());
		assertEquals(null, method.getObjectOfPreposition());
		
		method = RouterMethodSemantics.create("getInstanceByInstanceURN");
		assertEquals(Action.GET, method.getAction());
		assertEquals("Instance", method.getObject());
		assertEquals("Instance", method.getObjectSimpleName());
		assertEquals(null, method.getObjectCollectionType());
		assertEquals(Preposition.BY, method.getPreposition());
		assertEquals("InstanceURN", method.getObjectOfPreposition());
		
		method = RouterMethodSemantics.create("getStudyListByPatientICN");
		assertEquals(Action.GET, method.getAction());
		assertEquals("StudyList", method.getObject());
		assertEquals("Study", method.getObjectSimpleName());
		assertEquals("List", method.getObjectCollectionType());
		assertEquals(Preposition.BY, method.getPreposition());
		assertEquals("PatientICN", method.getObjectOfPreposition());
		
		
		method = RouterMethodSemantics.create("readStudyListByPatientICN");
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
			RouterMethodSemantics.create("get");
			fail("Method named 'get' should have failed and did not");
		} 
		catch (CoreRouterSemanticsException x){}
	}
}
