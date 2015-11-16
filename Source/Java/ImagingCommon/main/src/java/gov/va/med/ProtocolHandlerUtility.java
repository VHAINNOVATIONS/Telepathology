/**
 * 
 */
package gov.va.med;

/**
 * A simple static class to initialize the protocol handler packages so
 * that we can stop copying the code everywhere.
 * 
 * @author vhaiswbeckec
 *
 */
public class ProtocolHandlerUtility
{
	public static final String VA_CONNECTION_HANDLER_PACKAGES = "gov.va.med.imaging.url";
	/**
	 * 
	 * @param verbose if true then messages will be sent to System.out
	 */
	public synchronized static void initialize(boolean verbose)
	{
		if(verbose)
			System.out.println("java.protocol.handler.pkgs: " + System.getProperty("java.protocol.handler.pkgs"));
		
		String handlerPackages = System.getProperty("java.protocol.handler.pkgs");
		
		if(handlerPackages == null || handlerPackages.length() == 0)
			handlerPackages = VA_CONNECTION_HANDLER_PACKAGES;
		else
		{
			String[] handlerPackageNames = handlerPackages.split("|");
			boolean vaPackageExists = false;
			for(String handlerPackageName : handlerPackageNames)
				if(VA_CONNECTION_HANDLER_PACKAGES.equals(handlerPackageName))
					vaPackageExists = true;
			if( ! vaPackageExists )
				handlerPackages = handlerPackages + "|" + VA_CONNECTION_HANDLER_PACKAGES;
		}
		
		System.setProperty("java.protocol.handler.pkgs", handlerPackages);
		
		if(verbose)
			System.out.println("java.protocol.handler.pkgs: " + System.getProperty("java.protocol.handler.pkgs"));
	}
	
	/**
	 * 
	 * @param verbose
	 */
	public synchronized static void deInitialize(boolean verbose)
	{
		if(verbose)
			System.out.println("java.protocol.handler.pkgs: " + System.getProperty("java.protocol.handler.pkgs"));
		
		String handlerPackages = System.getProperty("java.protocol.handler.pkgs");
		
		if(handlerPackages == null || handlerPackages.length() < VA_CONNECTION_HANDLER_PACKAGES.length())
			return;
		int vixConnectionsIndex = handlerPackages.indexOf(VA_CONNECTION_HANDLER_PACKAGES);
		if(vixConnectionsIndex < 0)
			return;

		handlerPackages = handlerPackages.replace(VA_CONNECTION_HANDLER_PACKAGES, "");
		handlerPackages = handlerPackages.replace("||", "");
		handlerPackages = handlerPackages.replaceFirst("|$", "");
		
		System.setProperty("java.protocol.handler.pkgs",
	        handlerPackages == null || handlerPackages.length() == 0 ? 
	        null : handlerPackages );
		
		if(verbose)
			System.out.println("java.protocol.handler.pkgs: " + System.getProperty("java.protocol.handler.pkgs"));
	}
}
