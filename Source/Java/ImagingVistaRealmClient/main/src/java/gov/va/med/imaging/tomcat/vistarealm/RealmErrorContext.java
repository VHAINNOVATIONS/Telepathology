/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 19, 2011
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.tomcat.vistarealm;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a thread local context to hold error messages when attempting to authenticate with a VistA realm.
 * 
 * @author VHAISWWERFEJ
 *
 */
public class RealmErrorContext
{
	public final static String realmErrorContextExceptionName = "errorContextExceptionName";
	public final static String realmErrorContextExceptionMessage = "errorContextExceptionMessage";
	
	private static ThreadLocal<Map<String, String>> realmErrorContext = 
		new ThreadLocal<Map<String, String>>()
		{
			@Override
			protected Map<String, String> initialValue()
			{
				return new HashMap<String, String>();
			}
		};
		
	private static ThreadLocal<Map<String, String>> getRealmErrorContext()
	{
		return realmErrorContext;
	}
		
	public static Map<String, String> getRealmErrorContextProperties()
	{
		return getRealmErrorContext().get();
	}
	
	public static void unsetRealmErrorContext()
	{
		if(realmErrorContext != null)
			realmErrorContext.remove();
	}
	
	public static void clear()
	{
		getRealmErrorContextProperties().clear();
	}
	
	public static void setProperty(String name, String value)
	{
		getRealmErrorContextProperties().put(name, value);
	}
	
	public static String getProperty(String name)
	{
		return getRealmErrorContextProperties().get(name);
	}
	
	public static void setExceptionMessage(Throwable t)
	{
		if(t != null)
		{
			setProperty(realmErrorContextExceptionName, t.getClass().getSimpleName());
			setProperty(realmErrorContextExceptionMessage, t.getMessage());
		}
	}

}
