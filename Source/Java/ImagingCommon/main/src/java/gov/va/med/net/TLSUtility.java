/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Jun 29, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */

package gov.va.med.net;

import java.io.PrintStream;
import java.security.Provider;
import java.util.Set;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * @author vhaiswbeckec
 *
 */
public class TLSUtility
{
	private final PrintStream out;
	
	public TLSUtility(PrintStream out)
	{
		this.out = out;
	}
	
	/**
	 * Helper code to output the SSL properties loaded from the keystore/truststore
	 */
	public void dumpSSLProperties()
    {
    	out.println("==================== SSL Properties ======================================");
    	String defaultAlgorithm = dumpDefaultAlgorithm();
    	dumpKeyManagerFactory(defaultAlgorithm);
    	out.println("==================== End SSL Properties ======================================");
    }

	public String dumpDefaultAlgorithm()
	{
		String defaultAlgorithm = null;
		try
    	{
        	out.println("Default Algorithm ======================================");
    		defaultAlgorithm = javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm();
    		out.println("Default Algorithm is '" + defaultAlgorithm + "'");
    	} 
    	catch (Throwable x)
    	{
    		out.println("Error (" + x.getMessage() + ") getting default algorithm");
    	}
    	
    	return defaultAlgorithm;
	}

	public void dumpKeyManagerFactory(String algorithm)
	{
		Provider provider = null;
		
		if(algorithm == null)
    		out.println("No algorithm provided to find KeyManagerFactory.");
		else
		{
	    	try
	    	{
	    		javax.net.ssl.KeyManagerFactory keyMgrFactory = javax.net.ssl.KeyManagerFactory.getInstance(algorithm);
	    		if(keyMgrFactory == null)
		    		out.println("KeyManagerFactory type is '<null>'");
	    		else
	    		{
	    			out.println("KeyManagerFactory type is '" + keyMgrFactory.getClass().getSimpleName() + "'");
	    			try
	    			{
	    				provider = keyMgrFactory.getProvider();
	    				if(provider == null)
	    					out.println("KeyManagerFactory Provider is '<null>'");
	    				else
	    					dumpProvider(provider);
	
	    				dumpKeyManagers(keyMgrFactory);
	    			} 
	    			catch (Throwable x)
	    			{
	    				out.println("Error (" + x.getMessage() + ") getting provider");
	    			}
	    			
	    		}
	    	} 
	    	catch (Throwable x)
	    	{
	    		out.println("Error (" + x.getMessage() + ") getting key manager factory");
	    	}
		}
	}

	public void dumpProvider(Provider provider)
	{
		out.println("KeyManagerFactory Provider type is '" + provider.getClass().getSimpleName() + "'");
		
		dumpProviderTrustManagers(provider);
		
		dumpProviderKeys(provider);
	}

	public void dumpKeyManagers(javax.net.ssl.KeyManagerFactory keyMgrFactory)
	{
		javax.net.ssl.KeyManager[] keyManagers;
		try
		{
			System.out.println("=================== Key Managers ==========================");
			keyManagers = keyMgrFactory.getKeyManagers();
			for (javax.net.ssl.KeyManager keyManager : keyManagers)
				out.println("KeyManager [" + keyManager.getClass().getSimpleName() + "]" );
		} 
		catch (Throwable x)
		{
			out.println("Error (" + x.getMessage() + ") getting key managers");
		}
	}

	public void dumpProviderTrustManagers(Provider provider)
	{
		try
		{
			System.out.println("=================== TrustManagerFactory.PKIX Trust Managers ==========================");
			Object providerValue = provider.get("TrustManagerFactory.PKIX");
			TrustManagerFactory pkixTrustMgrFactory = (TrustManagerFactory)providerValue;
			TrustManager[] pkixTrustmanagers = pkixTrustMgrFactory.getTrustManagers();
			for (TrustManager pkixTrustManager : pkixTrustmanagers)
				out.println("Provider " + pkixTrustManager.toString() );
		}
		catch (Throwable x)
		{
			out.println("Error (" + x.getMessage() + ") getting TrustManagerFactory.PKIX value");
		}
	}

	public void dumpProviderKeys(Provider provider)
	{
		Set<Object> keySet;
		try
		{
			System.out.println("=================== Provider Keys ==========================");
			keySet = provider.keySet();
			for (Object key : keySet)
				out.println("Provider [" + key.toString() + "] [" + provider.get(key).toString() + "]" );
		} 
		catch (Throwable x)
		{
			out.println("Error (" + x.getMessage() + ") getting provider keyset");
		}
	}
}
