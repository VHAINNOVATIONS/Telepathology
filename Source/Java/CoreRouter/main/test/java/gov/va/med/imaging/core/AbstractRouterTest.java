package gov.va.med.imaging.core;

import gov.va.med.imaging.core.interfaces.Router;
import junit.framework.TestCase;

public abstract class AbstractRouterTest 
extends TestCase
{
	private Router router;
	protected abstract Router createRouter() throws Exception;
	
	protected void setUp() 
	throws Exception
	{
		super.setUp();
		router = createRouter();
	}

	protected Router getRouter()
    {
    	return router;
    }

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

}
