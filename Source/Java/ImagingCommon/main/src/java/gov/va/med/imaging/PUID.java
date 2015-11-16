package gov.va.med.imaging;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A globally unique process ID, used in a cluster to identify the process.
 * A PUID (process unique identifier) is unique among processes, each process will
 * have the same PUID.
 * This is a degenerate case of a GUID and uses some of the same code, BUT I 
 * did not elect to make either a single abstract base class or to make this
 * the base class of GUID because the uniqueness of the GUID muist be guaranteed
 * to such a high level, and because it has been tested very strenuously, I
 * did not want to modify it at all.  Hence the derivation through the copy
 * buffer.
 * 
 * @author vhaiswbeckec
 *
 */
public class PUID
{
	private static long uniqueHostIdentifier = 0;
	private static int uniqueProcessIdentifier = 0;
	private static String stringValue = null;

	static
	{
		String hostId = null;
		
		uniqueProcessIdentifier = System.identityHashCode(new Object()) & (int)System.currentTimeMillis();
		try
		{
			hostId = NetworkInfo.getMacAddress();
		}
		catch (IOException e)
		{
			try
			{
				hostId = InetAddress.getLocalHost().getHostAddress();
			}
			catch (UnknownHostException uhX)
			{
				long randomNumber = new Double(Math.random() * (double)0xFFFFFFFF).longValue();
				hostId = Long.toHexString(randomNumber); 
			}
		}
		
		hostId = hostId.replaceAll("[\u0020-\u002F\u003A-\u0040]", "");
		uniqueHostIdentifier = Long.parseLong(hostId, 16);
		
		stringValue = Long.toHexString(uniqueHostIdentifier) + Integer.toHexString(uniqueProcessIdentifier);
		
		System.out.println("PUID Generation - uniqueHostIdentifier = [" + Long.toHexString(uniqueHostIdentifier) + "]");
		System.out.println("PUID Generation - uniqueProcessIdentifier = [" + Integer.toHexString(uniqueProcessIdentifier) + "]");
	}
	
	/**
	 * 
	 */
	@Override
	public String toString()
	{
		return stringValue;
	}
}
