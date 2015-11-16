package gov.va.med.siteservice.siteprotocol;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages
{
	private static final String BUNDLE_NAME = "gov.va.med.siteservice.siteprotocol.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages()
	{
	}

	public static String getString(String key)
	{
		try
		{
			return RESOURCE_BUNDLE.getString(key);
		} 
		catch (MissingResourceException e)
		{
			return '!' + key + '!';
		}
	}

	public static String getString(String key, String defaultValue)
	{
		try
		{
			return RESOURCE_BUNDLE.getString(key);
		} 
		catch (MissingResourceException e)
		{
			return defaultValue;
		}
	}
	
	public static int getInt(String key, int defaultValue)
    {
		try
		{
			return Integer.parseInt( RESOURCE_BUNDLE.getString(key) );
		} 
		catch (MissingResourceException e)
		{
			return defaultValue;
		}
		catch( NumberFormatException nfX)
		{
			return defaultValue;
		}
    }
	
	public static long getLong(String key, long defaultValue)
    {
		try
		{
			return Long.parseLong( RESOURCE_BUNDLE.getString(key) );
		} 
		catch (MissingResourceException e)
		{
			return defaultValue;
		}
		catch( NumberFormatException nfX)
		{
			return defaultValue;
		}
    }
	
	public static float getFloat(String key, float defaultValue)
    {
		try
		{
			return Float.parseFloat( RESOURCE_BUNDLE.getString(key) );
		} 
		catch (MissingResourceException e)
		{
			return defaultValue;
		}
		catch( NumberFormatException nfX)
		{
			return defaultValue;
		}
    }
	
	public static double getDouble(String key, double defaultValue)
    {
		try
		{
			return Double.parseDouble( RESOURCE_BUNDLE.getString(key) );
		} 
		catch (MissingResourceException e)
		{
			return defaultValue;
		}
		catch( NumberFormatException nfX)
		{
			return defaultValue;
		}
    }
}
