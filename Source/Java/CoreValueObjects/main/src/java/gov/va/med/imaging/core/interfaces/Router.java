package gov.va.med.imaging.core.interfaces;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.datasource.DataSourceProvider;
import gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi;

/**
 * This interface is the definition of the VIX core API.  All facade projects operate through 
 * this interface exclusively.
 * 
 * All of the methods within this interface behave as follows:
 * 1.) The return value of a successful request is a populated value object.
 * 2.) If the request did not find the requested object(s) then the return value is an empty
 * Collection or a null if the return type is not a Collection
 * 3.) A method may throw only MethodException or one of its derivatives.
 * 
 * @author VHAISWBECKEC
 *
 */
public interface Router
{
	/**
	 * 
	 * @param command
	 */
    public abstract <T extends Object> void doAsynchronously(Command<T> command);

	/**
	 * 
	 * @param command
	 * @return
	 * @throws Exception 
	 */
    public abstract <T extends Object> T doSynchronously(Command<T> command)
	throws MethodException, ConnectionException;
	
	/**
	 * @return
	 */
	public boolean isFailoverOnMethodException();

	/**
	 * @return
	 */
	public boolean isCachingEnabled();

	/**
	 * Make the application configuration available for commands
	 * @return
	 */
	public IAppConfiguration getAppConfiguration();
	
	/**
	 * 
	 * @return
	 */
	public DataSourceProvider getProvider();

	/**
	 * 
	 * @return
	 */
	public SiteResolutionDataSourceSpi getSiteResolver();

}
