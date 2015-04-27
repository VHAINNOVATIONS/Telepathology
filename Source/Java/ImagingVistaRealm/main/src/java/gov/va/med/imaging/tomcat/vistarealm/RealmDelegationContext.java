/**
 * 
 */
package gov.va.med.imaging.tomcat.vistarealm;

import java.util.HashMap;
import java.util.Map;

/**
 * This class maintains a ThreadLocal that is used by Realm
 * implementations to pass properties when doing delegation.
 * 
 * @author vhaiswbeckec
 *
 */
class RealmDelegationContext
{
	public final static String INHIBIT_PARENT_DELEGATION = "inhibitParentDelegation";
	public final static String INHIBIT_THIS_AUTHENTICATION = "inhibitThisAuthentication";
	
	private static ThreadLocal<Map<String,String>> realmDelegationContext = 
		new ThreadLocal<Map<String,String>>()
		{
			@Override
			protected Map<String, String> initialValue()
			{
				return new HashMap<String, String>();
			}
		};
	
	static ThreadLocal<Map<String, String>> getRealmDelegationContext()
	{
		return realmDelegationContext;
	}
	
	static Map<String,String> getRealmDelegationProperties()
	{
		return getRealmDelegationContext().get();
	}
	
	static void unsetRealmDelegationContext()
	{
		if(realmDelegationContext != null)
			realmDelegationContext.remove();
	}
}
