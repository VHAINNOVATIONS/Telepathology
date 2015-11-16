/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Aug 2, 2010
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

package gov.va.med.configuration;

import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.CompoundName;
import javax.naming.InvalidNameException;

/**
 * @author vhaiswbeckec
 * 
 * @see http://download.oracle.com/javase/6/docs/api/javax/naming/CompoundName.html
 *
 */
public class ConfigurationPropertyName
extends CompoundName
{
	private static final long serialVersionUID = 1L;
	private static final Properties SYNTAX;
	
	static
	{
		SYNTAX = new Properties();
		SYNTAX.setProperty("jndi.syntax.direction", "left_to_right");
		SYNTAX.setProperty("jndi.syntax.separator", "/");
		SYNTAX.setProperty("jndi.syntax.ignorecase", "true");
		SYNTAX.setProperty("jndi.syntax.trimblanks", "true");
		SYNTAX.setProperty("jndi.syntax.separator.ava", ";");
		SYNTAX.setProperty("jndi.syntax.separator.typeval", "=");
	}

	public static final String NAME_COMPONENT_REGEX = "([A-Za-z0-9_\\\\.-]+)(\\[([A-Za-z0-9_\\\\.-]+)\\])?";
	public static final Pattern NAME_COMPONENT_PATTERN = Pattern.compile(NAME_COMPONENT_REGEX);
	public static final int NAME_COMPONENT_NAME_INDEX = 1;
	public static final int NAME_COMPONENT_KEY_INDEX = 3;
	
	/**
	 * @param comps
	 * @param syntax
	 */
	public ConfigurationPropertyName(Enumeration<String> comps)
	{
		super(comps, SYNTAX);
	}

	/**
	 * @param n
	 * @param syntax
	 * @throws InvalidNameException
	 */
	public ConfigurationPropertyName(String n) 
	throws InvalidNameException
	{
		super(n, SYNTAX);
	}

	
	public static String leafNodePropertyName(String component)
	{
		Matcher matcher = NAME_COMPONENT_PATTERN.matcher(component);
		return matcher.matches() ? matcher.group(NAME_COMPONENT_NAME_INDEX) : null;
	}
	
	public static String leafNodePropertyKey(String component)
	{
		Matcher matcher = NAME_COMPONENT_PATTERN.matcher(component);
		return matcher.matches() ? matcher.group(NAME_COMPONENT_KEY_INDEX) : null;
	}
}
