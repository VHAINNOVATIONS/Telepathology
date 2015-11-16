/**
 * 
 */
package gov.va.med.siteservice.interactive.commands;

import gov.va.med.imaging.datasource.Provider;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandProcessor;
import gov.va.med.siteservice.SiteResolutionProviderConfiguration;

/**
 * @author vhaiswbeckec
 *
 */
public class SaveCommand
extends Command<SiteResolutionProviderConfiguration>
{
	private Provider provider;

	/**
	 * @see gov.va.med.interactive.Command#processCommand(gov.va.med.interactive.CommandProcessor, java.lang.Object)
	 */
	@Override
	public void processCommand(CommandProcessor<SiteResolutionProviderConfiguration> processor, SiteResolutionProviderConfiguration config)
	throws Exception
	{
		getProvider().storeConfiguration();
	}

	/**
	 * @return the provider
	 */
	public Provider getProvider()
	{
		return this.provider;
	}

	/**
	 * @param provider the provider to set
	 */
	public void setProvider(Provider provider)
	{
		this.provider = provider;
	}
}
