/**
 * 
 */
package gov.va.med.imaging.pathology;

import gov.va.med.GeneratedCodeValidationUtility;
import gov.va.med.exceptions.ValidationException;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class ValidatePathologyFacadeRouterFacadeTest
extends TestCase
{
	public void testGeneratedFacade()
	{
		try
		{
			GeneratedCodeValidationUtility.validateImplementation(
				gov.va.med.imaging.pathology.PathologyFacadeRouter.class, 
				gov.va.med.imaging.pathology.PathologyFacadeRouterImpl.class
			);
		}
		catch (ValidationException x)
		{
			fail(x.getMessage());
		}
	}
}
