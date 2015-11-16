/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Mar 11, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
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
package gov.va.med.imaging.core.interfaces.exceptions;

/**
 * @author VHAISWBECKEC
 * A configuration error is thrown when a component within the Vix
 * is either missing, invalid or misconfigured such that it prevents
 * operation.  Component configuration that prevents operation of
 * a component (as opposed to the Vix as a whole) is an acceptable
 * condition to cause this exception to be thrown. 
 * The derived classes should provide more information as to the nature of 
 * the configuration error.
 * 
 * NOTE: this is an unchecked exception, a facade should provide an error
 * barrier and handle errors of this class in a manner consistent with
 * the protocol they support (i.e. HTTP error code, HTML error page, 
 * SOAP fault element, etc).
 */
public class ConfigurationError 
extends Error
{
	private static final long serialVersionUID = 1L;

	private final String componentName;
	private final String additionalInformation;
	
	private static String createMessage(String componentName, String additionalInformation)
	{
		return
			"The component '" + componentName + "' has reported a configuration error.\n" +
			(additionalInformation == null ?
			 "No additional information is available" : additionalInformation);
	}
	
	public ConfigurationError(String componentName)
	{
		super(createMessage(componentName, null));
		this.componentName = componentName;
		this.additionalInformation = null;
	}
	
	public ConfigurationError(String componentName, String additionalInformation)
	{
		super(createMessage(componentName, additionalInformation));
		this.componentName = componentName;
		this.additionalInformation = additionalInformation;
	}
	/**
	 * @param cause
	 */
	public ConfigurationError(String componentName, Throwable cause)
	{
		super(createMessage(componentName, null), cause);
		this.componentName = componentName;
		this.additionalInformation = null;
	}
	
	public ConfigurationError(String componentName, String additionalInformation, Throwable cause)
	{
		super(createMessage(componentName, additionalInformation), cause);
		this.componentName = componentName;
		this.additionalInformation = additionalInformation;
	}

	protected String getComponentName()
    {
    	return componentName;
    }

	protected String getAdditionalInformation()
    {
    	return additionalInformation;
    }
}
