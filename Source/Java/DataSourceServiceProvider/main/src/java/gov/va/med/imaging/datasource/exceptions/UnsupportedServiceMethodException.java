package gov.va.med.imaging.datasource.exceptions;

import java.net.URL;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.datasource.DataSourceSpi;

/**
 * A service implementation may not implement all methods defined in an
 * SPI.  If it does not implement a specific method then it must throw
 * an instance of this exception when that method is called.  The abstract
 * base implementations of the SPIs throw this exception for all methods.
 * 
 * @author VHAISWBECKEC
 */
public class UnsupportedServiceMethodException 
extends ConnectionException
{
	private static final long serialVersionUID = 1L;

	public UnsupportedServiceMethodException(Class<? extends DataSourceSpi> spiType, URL url, String methodName)
	{
		super( "Configured '" + (spiType == null ? "unknown service type" : spiType.getSimpleName()) + 
				"'service for URL '" + (url == null ? "unknown URL" : url.toString()) + 
				"' does not implement '" + (methodName == null ? "unknown method name" : methodName) + 
				"'.");
	}
	
	public UnsupportedServiceMethodException(Class<? extends DataSourceSpi> spiType, String methodName)
	{
		super( "Configured '" + (spiType == null ? "unknown service type" : spiType.getSimpleName()) + 
				"'service does not implement '" + (methodName == null ? "unknown method name" : methodName) + 
				"'.");
	}
}
