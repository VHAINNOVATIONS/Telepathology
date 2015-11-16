package gov.va.med.imaging;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class for debugging.  Provides some simple stack trace analysis
 * for use in debugging.
 * 
 * @author VHAISWBECKEC
 *
 */
public class StackTraceAnalyzer
{
	/**
	 * Return the current stack trace. 
	 * @return
	 */
	public static StackTraceElement[] currentStack()
	{
		StackTraceElement[] stackTrace = (new Throwable()).getStackTrace();
		return stackTrace;
	}
	
	/**
	 * Return a StackTraceAnalyzer on the current stack trace.
	 * @return
	 */
	public static StackTraceAnalyzer currentStackAnalyzer()
	{
		StackTraceElement[] stackTrace = (new Throwable()).getStackTrace();
		return new StackTraceAnalyzer(stackTrace);
	}
	
	private StackTraceElement[] stackTraceElements = null;
	
	/**
	 * 
	 * @param stackTraceElements
	 */
	public StackTraceAnalyzer(StackTraceElement[] stackTraceElements)
	{
		this.stackTraceElements = stackTraceElements;
	}

	/**
	 * 
	 * @param subjectClass
	 * @return
	 */
	public boolean isClassInStack(Class<?> subjectClass)
	{
		for(StackTraceElement element:stackTraceElements)
			if(element.getClassName().equals(subjectClass.getName()))
				return true;
		
		return false;
	}

	/**
	 * @return
	 */
	public boolean isPackageInStack(Package subjectPackage)
	{
		for(StackTraceElement element:stackTraceElements)
		{
			Class<?> stackTraceClass;
			// the ClassNotFoundException should never happen
			try{stackTraceClass = Class.forName(element.getClassName());}
			catch (ClassNotFoundException x){continue;}
			Package stackTracePackage = stackTraceClass == null ? null : stackTraceClass.getPackage();
			if( stackTracePackage.equals(subjectPackage) )
				return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param subjectMethod
	 * @return
	 */
	public boolean isMethodNameInStack(Method subjectMethod)
	{
		Class<?> subjectClass = subjectMethod.getDeclaringClass();
		String subjectClassName = subjectClass.getName();
		
		return isMethodNameInStack(subjectClassName, subjectMethod.getName());
	}
	public boolean isMethodNameInStack(String className, String methodName)
	{
		for(StackTraceElement element:stackTraceElements)
			if(element.getClassName().equals(className) && element.getMethodName().equals(methodName))
				return true;
		
		return false;
	}
	
	public StackTraceElement getFirstElementInPackage(String packageName)
	{
		return findPackage(packageName, true, false, true);
	}
	
	public StackTraceElement getFirstElementNotInPackage(String packageName)
	{
		return findPackage(packageName, false, false, true);
	}
	
	public StackTraceElement getFirstElementInPackageHierarchy(String packageName)
	{
		return findPackage(packageName, true, true, true);
	}
	
	public StackTraceElement getFirstElementNotInPackageHierarchy(String packageName)
	{
		return findPackage(packageName, false, true, true);
	}
	
	public StackTraceElement getFirstElementInClass(String className)
	{
		return findClass(className, false, true, true);
	}
	
	public StackTraceElement getFirstElementNotInClass(String className)
	{
		return findClass(className, false, false, true);
	}
	
	public StackTraceElement getFirstElementInClassPattern(String classNamePattern)
	{
		return findClass(classNamePattern, true, true, true);
	}
	
	public StackTraceElement getFirstElementNotInClassPattern(String classNamePattern)
	{
		return findClass(classNamePattern, true, false, true);
	}
	
	/**
	 * Find the first element referencing (or not referencing) the package (or sub-package) name.
	 * Always ignore our own stack trace elements.
	 * 
	 * @param packageName - a dot-seperated package name
	 * @param positiveFind - if true, return the first occurence of the package (or sub-package) name.
	 *                       if false, return the first occurence NOT of the package (or sub-package) name.
	 * @param includeSubPackages - if true, test for subpackage names (the package name and any name starting with the package name)
	 *                           - if false, test strictly for the package name
	 * @return
	 */
	private StackTraceElement findPackage(String packageName, boolean positiveFind, boolean includeSubPackages, boolean ignoreProxies)
	{
		if(packageName == null)
			return null;
		
		int packageNameLength = packageName.length();		// just an optimization, cause we'll use this as a quick test
		String subPackageName = packageName + ".";			// another little optimization
		
		String myClassName = getClass().getName();
		String myPackageName = getClass().getPackage().getName();
		
		for(int stackTraceIndex=0; stackTraceIndex < stackTraceElements.length; ++stackTraceIndex)
		{
			StackTraceElement element = stackTraceElements[stackTraceIndex];
			String elementClassName = element.getClassName();
			String elementPackageName = elementClassName.lastIndexOf('.') > 0 ? elementClassName.substring(0, elementClassName.lastIndexOf('.')) : "";
			
			if( myClassName.equals(elementClassName) || myPackageName.equals(elementPackageName) )
				continue;
			
			if(ignoreProxies && isProxyName(elementClassName))
				continue;
			
			// if subpackages are included then the element package name just starts with the subject package name
			if(includeSubPackages)
			{
				int elementPackageNameLength = elementPackageName.length();
				
				if(positiveFind)
				{
					if( packageName.equals(elementPackageName) ||
					    (elementPackageNameLength > packageNameLength && elementPackageName.startsWith(subPackageName)) )
							return element;
				}
				else
				{
					if( ! packageName.equals(elementPackageName) &&
					    (elementPackageNameLength > packageNameLength && !elementPackageName.startsWith(subPackageName)) )
							return element;
				}
			}
			else
			{
				if(positiveFind && packageName.equals(elementPackageName))
					return element;
				if(!positiveFind && !packageName.equals(elementPackageName))
					return element;
			}
		}
		
		return null;
	}

	/**
	 * Find the first element referencing (or not referencing) the package (or sub-package) name.
	 * Always ignore our own stack trace elements.
	 * 
	 * @param packageName - a dot-seperated package name
	 * @param positiveFind - if true, return the first occurence of the package (or sub-package) name.
	 *                       if false, return the first occurence NOT of the package (or sub-package) name.
	 * @param ignoreProxies - if true, then ignore Proxy derived classes
	 *                      - if false, then include Proxy derived classes
	 * @return
	 */
	private StackTraceElement findClass(
		String className, 
		boolean treatClassNameAsRegex, 
		boolean positiveFind, 
		boolean ignoreProxies)
	{
		if(className == null)
			return null;
		
		String myClassName = getClass().getName();
		String myPackageName = getClass().getPackage().getName();
		
		for(int stackTraceIndex=0; stackTraceIndex < stackTraceElements.length; ++stackTraceIndex)
		{
			StackTraceElement element = stackTraceElements[stackTraceIndex];
			String elementClassName = element.getClassName();
			String elementPackageName = elementClassName.lastIndexOf('.') > 0 ? elementClassName.substring(0, elementClassName.lastIndexOf('.')) : "";

			// always skip our own stack trace elements
			if( myClassName.equals(elementClassName) || myPackageName.equals(elementPackageName) )
				continue;
			
			if(ignoreProxies && isProxyName(elementClassName))
				continue;
			
			if(treatClassNameAsRegex)
			{
				// find stack trace elements where the class name matches the pattern
				Pattern pattern = Pattern.compile(className);
				Matcher matcher = pattern.matcher(elementClassName);
				
				boolean matches = matcher.matches();
				// find stack trace elements where the class name DOES match
				if( positiveFind && matches )
					return element;
				// find stack trace elements where the class name DOES NOT match
				else if( !positiveFind && !matches )
					return element;
			}
			else
				// find stack trace elements where the class name DOES match
				if(positiveFind && className.equals(elementClassName) )
					return element;
				// find stack trace elements where the class name DOES NOT match
				else if( ! positiveFind && ! className.equals(elementClassName) )
					return element;
		}
		
		return null;
	}

	
	/**
	 * Returns true if the class name is (probably) a dynamic proxy generated class name.
	 * 
	 * @param elementClassName
	 * @return
	 */
	private boolean isProxyName(String elementClassName)
	{
		// TODO Auto-generated method stub
		return elementClassName != null && elementClassName.startsWith("$Proxy");
	}

	/**
	 * 
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		for(StackTraceElement element:stackTraceElements)
		{
			sb.append("[" + element.getClassName() + "].[" + element.getMethodName() + "]" );
			sb.append("[" + element.getFileName() + ":" + element.getLineNumber() + "]" );
			sb.append("\n" );
		}
		
		return sb.toString();
	}
}
