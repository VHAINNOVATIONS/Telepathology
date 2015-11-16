package gov.va.med.imaging;

import java.lang.reflect.Method;

import junit.framework.TestCase;

public class StackTraceAnalyzerTest 
extends TestCase
{
	StackTraceElement[] stackTrace = null;
	StackTraceAnalyzer analyzer = null;
	
	protected void setUp() throws Exception
	{
		super.setUp();
		TraceTestClassOne one = new TraceTestClassOne();
		
		stackTrace = one.makeStackTrace();
		analyzer = new StackTraceAnalyzer(stackTrace);
		System.out.println( analyzer.toString() );
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testIsClassInStack()
	{
		assertTrue( analyzer.isClassInStack(TraceTestClassOne.class) );
		assertTrue( analyzer.isClassInStack(TraceTestClassTwo.class) );
	}

	public void testIsMethodNameInStackMethod() 
	throws SecurityException, NoSuchMethodException
	{
		Method subjectMethodOne = TraceTestClassOne.class.getMethod("makeStackTrace", (Class[])null);
		Method subjectMethodTwo = TraceTestClassOne.class.getMethod("makeStackTrace", (Class[])null);
		
		assertTrue( analyzer.isMethodNameInStack(subjectMethodOne) );
		assertTrue( analyzer.isMethodNameInStack(subjectMethodTwo) );
	}

	public void testIsMethodNameInStackStringString()
	{
		assertTrue( analyzer.isMethodNameInStack(TraceTestClassOne.class.getName(), "makeStackTrace") );
		assertTrue( analyzer.isMethodNameInStack(TraceTestClassTwo.class.getName(), "makeStackTrace") );
	}

	public void testGetFirstElementInPackage()
	{
		StackTraceElement element = analyzer.getFirstElementInPackage("gov.va.med.imaging");
		assertNull(element);
	}

	public void testGetFirstElementNotInPackage()
	{
		StackTraceElement element = analyzer.getFirstElementNotInPackage("gov.va.med.imaging");
		assertNotNull(element);
	}

	public void testGetFirstElementInPackageHierarchy()
	{
		StackTraceElement element = analyzer.getFirstElementInPackageHierarchy("gov.va");
		assertNull(element);
	}

	public void testGetFirstElementNotInPackageHierarchy()
	{
		StackTraceElement element = analyzer.getFirstElementNotInPackageHierarchy("gov.va");
		assertNotNull(element);
	}

	class TraceTestClassOne
	{
		private TraceTestClassTwo another = null;
		TraceTestClassOne()
		{
			another = new TraceTestClassTwo();
		}
		
		public StackTraceElement[] makeStackTrace()
		{
			return another.makeStackTrace();
		}
	}

	class TraceTestClassTwo
	{
		TraceTestClassTwo()
		{
			
		}
		
		public StackTraceElement[] makeStackTrace()
		{
			return Thread.currentThread().getStackTrace();
		}
		
	}
}


