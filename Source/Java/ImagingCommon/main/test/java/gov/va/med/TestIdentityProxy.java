/**
 * 
 */
package gov.va.med;

import java.lang.reflect.Proxy;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestIdentityProxy
	extends TestCase
{
	public void testProxy()
	{
		TestImplementation someInstance = new TestImplementation();
		IdentityProxyInvocationHandler<TestInterface> handler = 
			new IdentityProxyInvocationHandler<TestInterface>(someInstance, someInstance.getClass());
		
		TestInterface proxy = 
			(TestInterface)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{TestInterface.class}, handler);
		
		assertEquals(42, proxy.getNumber());
	}
	
	interface TestInterface
	{
		public int getNumber();
	}

	class TestImplementation
	implements TestInterface
	{
		@Override
		public int getNumber()
		{
			return 42;
		}
		
	}
}
