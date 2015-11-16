package gov.va.med.imaging;

/**
 * @author beckey created: Jan 5, 2005 at 2:44:58 PM
 * 
 * This class does ...
 */
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public final class NetworkInfo
{
	public final static String hexDigitRegex = "[0-9a-fA-F]";
	public final static String windowsMacAddressRegex = 
		hexDigitRegex + hexDigitRegex + "-" + 
		hexDigitRegex + hexDigitRegex + "-" + 
		hexDigitRegex + hexDigitRegex + "-" + 
		hexDigitRegex + hexDigitRegex + "-" + 
		hexDigitRegex + hexDigitRegex + "-" + 
		hexDigitRegex + hexDigitRegex;
	public final static Pattern windowsMacAddressPattern = Pattern.compile(windowsMacAddressRegex); 
	
	public final static String linuxMacAddressRegex = 
		hexDigitRegex + hexDigitRegex + "-" + 
		hexDigitRegex + hexDigitRegex + "-" + 
		hexDigitRegex + hexDigitRegex + "-" + 
		hexDigitRegex + hexDigitRegex + "-" + 
		hexDigitRegex + hexDigitRegex + "-" + 
		hexDigitRegex + hexDigitRegex;
	public final static Pattern linuxMacAddressPattern = Pattern.compile(linuxMacAddressRegex); 
	
	public final static  String getMacAddress()
	throws IOException
	{
		try 
		{
            InetAddress address = InetAddress.getLocalHost();

            NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            if (ni != null) 
            {       
            	StringBuilder sb = new StringBuilder();
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) 
                {                	
                    /*
                     * Extract each array of mac address and convert it to hexa with the
                     * following format 08-00-27-DC-4A-9E.
                     */
                    for (int i = 0; i < mac.length; i++) 
                    {
                    	sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    return sb.toString();
                } 
                else 
                {
                	throw new IOException("Address doesn't exist or is not accessible.");
                }
            } 
            else 
            {
            	throw new IOException("Network Interface for the specified address is not found.");
            }
        } 
		catch (UnknownHostException uhX) 
        {
            throw new IOException(uhX);
        } 
		catch (SocketException sX) 
        {
			throw new IOException(sX);
        }
	}

	public final static boolean linuxIsMacAddress(String macAddressCandidate)
	{
		return 
			macAddressCandidate == null ? 
			false : 
			linuxMacAddressPattern.matcher(macAddressCandidate).matches();
	}

	public final static boolean windowsIsMacAddress(String macAddressCandidate)
	{
		return 
			macAddressCandidate == null ? 
			false : 
			windowsMacAddressPattern.matcher(macAddressCandidate).matches();
	}

	public final static boolean osxIsMacAddress(String macAddressCandidate)
	{
		//	 	 TODO: use a smart regular expression
		if (macAddressCandidate.length() != 17)
			return false;
		return true;
	}

	/*
	 * Main
	 */
	public final static void main(String[] args)
	{
		try
		{
			System.out.println("Network infos");

			System.out.println("  Operating System: "
					+ System.getProperty("os.name"));
			System.out.println("  IP/Localhost: "
					+ InetAddress.getLocalHost().getHostAddress());
			System.out.println("  MAC Address: " + getMacAddress());
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
}