package gov.va.med.imaging;

import gov.va.med.imaging.exchange.business.dicom.Study;
import junit.framework.Assert;
import junit.framework.TestCase;

public class ObjectClonerTest 
extends TestCase
{
	public void testSimpleObjectCopy() throws Exception
	{
		Study originalStudy = new Study("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K");
		Study clonedStudy = (Study)ObjectCloner.deepCopy(originalStudy);
		
		Assert.assertTrue(originalStudy != clonedStudy);
		Assert.assertTrue(originalStudy.equals(clonedStudy));
		
	}
	
}
