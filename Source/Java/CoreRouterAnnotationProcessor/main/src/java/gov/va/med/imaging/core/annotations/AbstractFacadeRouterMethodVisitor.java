/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 10, 2011
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
package gov.va.med.imaging.core.annotations;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.ElementVisitor;
import javax.lang.model.util.SimpleElementVisitor6;

/**
 * @author VHAISWWERFEJ
 *
 */
public abstract class AbstractFacadeRouterMethodVisitor<R, P> 
extends SimpleElementVisitor6<R, P>
implements ElementVisitor<R, P>
{
	
	// the static mapping of legacy router method names to 
	// semantics compliant method names
	// the legacy name is the key, the semantically correct name is the value
	private static Map<String, String> mappedMethodNameMap = new HashMap<String, String>();
	static
	{
		mappedMethodNameMap = new HashMap<String, String>();
		
		mappedMethodNameMap.put("isSiteAvailable", "getSiteConnectivityStatus");
		mappedMethodNameMap.put("logImageAccessEvent", "postImageAccessEvent");
	}

	public static String findMappedMethodName(String legacyName)
	{
		String mappedName = mappedMethodNameMap.get(legacyName);
		return mappedName == null ? legacyName : mappedName;
	}
}
