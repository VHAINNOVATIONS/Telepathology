package gov.va.med.imaging;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


public class ClassLoaderUtilities
{
	public static String getClassLoaderHierarchy(Object object)
	{
		StringBuffer sb = new StringBuffer();
		
		for( ClassLoader loader = object.getClass().getClassLoader(); loader != null; loader = loader.getParent() )
		{
			Class loaderClass = loader.getClass();
			
			sb.append("ClassLoader class name '");
			sb.append( loader.getClass().getName() );
			sb.append("' instance '");
			sb.append( loader.hashCode() );
			sb.append("'\n");
			
			Method[] classLoaderMethods = loaderClass.getDeclaredMethods();
			for(Method method: classLoaderMethods)
			{
				sb.append("\t");
				sb.append( Modifier.toString(method.getModifiers()) );
				sb.append(' ');
				sb.append(method.getName());
				sb.append("(");
				for( Class<?> parameterClass: method.getParameterTypes() )
				{
					sb.append(parameterClass.getName());
					sb.append(",");
				}
				sb.append(")");
				sb.append("\n");
			}
			
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public static void main(String[] args)
	{
		System.out.println( getClassLoaderHierarchy(new ClassLoaderUtilities()) );
	}
}
