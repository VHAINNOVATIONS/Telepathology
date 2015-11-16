/**
 * 
 */
package gov.va.med.vista.siteservice.soap;

import gov.va.med.GeneratedCodeValidationUtility;
import gov.va.med.exceptions.ValidationException;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class ValidateRouterFacadeTest
extends TestCase
{
	public void testGeneratedFacade()
	{
		try
		{
			GeneratedCodeValidationUtility.validateImplementation(
				gov.va.med.vista.siteservice.soap.SiteServiceSOAPFacadeRouter.class, 
				gov.va.med.vista.siteservice.soap.SiteServiceSOAPFacadeRouterImpl.class
			);
		}
		catch (ValidationException x)
		{
			fail(x.getMessage());
		}
	}
}
