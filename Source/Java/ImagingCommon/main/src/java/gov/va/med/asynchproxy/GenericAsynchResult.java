package gov.va.med.asynchproxy;

import java.lang.reflect.Method;

/**
 * 
 * @author VHAISWBECKEC
 * 
 */
public class GenericAsynchResult
{
	private Method method;
	private Object[] args;
	private Object result;

	GenericAsynchResult(Method method, Object[] args, Object result)
	{
		super();
		this.method = method;
		this.args = args;
		this.result = result;
	}

	public Method getMethod()
	{
		return method;
	}

	public Object[] getArgs()
	{
		return args;
	}

	public Object getResult()
	{
		return result;
	}
}