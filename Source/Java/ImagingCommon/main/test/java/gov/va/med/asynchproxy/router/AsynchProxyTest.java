package gov.va.med.asynchproxy.router;

import gov.va.med.asynchproxy.AsynchProxyFactory;
import junit.framework.TestCase;

public class AsynchProxyTest extends TestCase
{

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	public void testAsynchOperation()
	{
		AsynchProxyFactory<Router> asynchFactory = new AsynchProxyFactory<Router>();
		
		try
        {
			AsynchRouterClient client = new AsynchRouterClient();
	        Router asynchRouter = asynchFactory.createAsynchProxy(Router.class, new RouterImpl(), client);
	        String[] result = asynchRouter.getStuff(1);
	        assertNull(result);
	        result = asynchRouter.getStuff(2);
	        assertNull(result);
	        result = asynchRouter.getStuff(3);
	        assertNull(result);
	        result = asynchRouter.getStuff(4);
	        assertNull(result);
	        result = asynchRouter.getStuff(5);
	        assertNull(result);
	        result = asynchRouter.getStuff(6);
	        assertNull(result);
	        result = asynchRouter.getStuff(7);
	        assertNull(result);
	        result = asynchRouter.getStuff(8);
	        assertNull(result);
	        result = asynchRouter.getStuff(9);
	        assertNull(result);
	        result = asynchRouter.getStuff(10);
	        assertNull(result);
	        result = asynchRouter.getStuff(11);
	        assertNull(result);
	        result = asynchRouter.getStuff(12);
	        assertNull(result);
	        
	        Thread.sleep(2000);
	        
	        assertEquals(12, client.getResultCount());
        } 
		catch (Exception e)
        {
			e.printStackTrace();
			fail(e.getMessage());
        } 		
	}
	
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

}
