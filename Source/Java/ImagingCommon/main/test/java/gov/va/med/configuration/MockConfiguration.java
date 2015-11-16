/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Jul 28, 2010
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

import java.net.URI;
import java.util.Map;
import javax.naming.NamingException;

/**
 * @author vhaiswbeckec
 *
 */
@ConfigurationOptions(context="MockConfiguration", persistenceUnit=true)
public interface MockConfiguration
extends Configuration
{
	public String getHost() throws NamingException;
	public void setHost(String host) throws NamingException;
	
	public Integer getPort() throws NamingException;
	public void setPort(Integer port) throws NamingException;

	public String[] getProtocolPreferenceFactoryParameters();
	public void setProtocolPreferenceFactoryParameters(String[] parameters);
	
	// this call will return a proxy to a configuration instance
	// A set accessor is not legal for a Configuration, the proxy invocation
	// handler and the ConfigurationFactory do all the sub configuration
	// creation.
	public MockSubConfiguration getSubConfiguration() throws NamingException;
	
	@ConfigurationProperty( "SubConfiguration/SiteServiceUri" )
	public URI getSiteServiceUri() throws NamingException;
	@ConfigurationProperty( "SubConfiguration/SiteServiceUri" )
	public void setSiteServiceUri(URI subHost) throws NamingException;

	// Like a child Configuration, collections can be accessed through
	// get methods but cannot be set.
	// The ConfigurationInvocationHandler deals with all the creating
	// and accessing Map members
	public Map<?,?> getProtocolPreference() throws NamingException;
	
	// If the get includes a reference parameter then the property
	// being accessed is assumed to be a Map and the parameter is
	// a key value.

	// A clear method will clear the contents of a Collection
	public String clearProtocolPreference() throws NamingException;
	
	// A set method with two parameters, the first being a reference, 
	// is assumed to be accessing a Map element
	public String setProtocolPreference(String key, String value) throws NamingException;
	
	// If the get includes a reference parameter then the property
	// being accessed is assumed to be a Map and the parameter is
	// a key value.
	public String getProtocolPreference(String key) throws NamingException;
}
