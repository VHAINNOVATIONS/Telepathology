package gov.va.med.imaging.tomcat.vistarealm;

/**
 * Methods to get/set the security context without invoking
 * the TransactionContext stuff.
 * 
 * @author VHAISWBECKEC
 *
 */
public class VistaRealmSecurityContext
{
	private static ThreadLocal<VistaRealmPrincipal> context = null;
	
	/**
	 * 
	 * @return
	 */
	public static VistaRealmPrincipal get()
	{
		if(context != null)
			return context.get();
		
		return null;
	}

	/**
	 * 
	 * @param principal
	 */
	public static void set(VistaRealmPrincipal principal)
	{
		if(context == null)
			context = new ThreadLocal<VistaRealmPrincipal>();
		
		context.set(principal);
	}
	
	/**
	 * 
	 */
	public static void clear()
	{
		if(context != null)
			context.remove();
	}
}
