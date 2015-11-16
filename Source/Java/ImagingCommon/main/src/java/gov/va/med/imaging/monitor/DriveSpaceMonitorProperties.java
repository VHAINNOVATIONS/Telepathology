/**
 * 
 */
package gov.va.med.imaging.monitor;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author VHAISWBECKEC
 *
 */
public class DriveSpaceMonitorProperties
{
	private static final String BUNDLE_NAME = "gov.va.med.imaging.monitor.DriveSpaceMonitor"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private DriveSpaceMonitorProperties()
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
			String value = RESOURCE_BUNDLE.getString(key);
			return Integer.parseInt(value);
		} 
		catch (MissingResourceException e)
		{
			return defaultValue;
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}
	
	public static long getLong(String key, long defaultValue)
	{
		try
		{
			String value = RESOURCE_BUNDLE.getString(key);
			return Long.parseLong(value);
		} 
		catch (MissingResourceException e)
		{
			return defaultValue;
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}
	
	public static float getFloat(String key, float defaultValue)
	{
		try
		{
			String value = RESOURCE_BUNDLE.getString(key);
			return Float.parseFloat(value);
		} 
		catch (MissingResourceException e)
		{
			return defaultValue;
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}
}
