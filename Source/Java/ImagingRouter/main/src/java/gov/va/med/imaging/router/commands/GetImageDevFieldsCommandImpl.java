/**
 * 
 */
package gov.va.med.imaging.router.commands;

import gov.va.med.imaging.AbstractImagingURN;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ApplicationConfigurationException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.datasource.ImageDataSourceSpi;
import gov.va.med.imaging.datasource.VersionableDataSourceSpi;
import gov.va.med.imaging.exchange.InterfaceURLs;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.SortedSet;

/**
 * @author vhaiswbeckec
 *
 */
public class GetImageDevFieldsCommandImpl 
extends AbstractCommandImpl<String>
{
	private static final long serialVersionUID = -1766034076740320465L;
	private final AbstractImagingURN imageUrn;
	private final String flags;
	
	/**
	 * @param commandContext - the context available to the command
	 * @param imageUrn - the universal identifier of the image
	 * @param flags
	 */
	public GetImageDevFieldsCommandImpl(
			AbstractImagingURN imageUrn,
			String flags)
	{
		super();
		this.imageUrn = imageUrn;
		this.flags = flags;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.flags == null) ? 0 : this.flags.hashCode());
		result = prime * result
				+ ((this.imageUrn == null) ? 0 : this.imageUrn.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final GetImageDevFieldsCommandImpl other = (GetImageDevFieldsCommandImpl) obj;
		if (this.flags == null)
		{
			if (other.flags != null)
				return false;
		} else if (!this.flags.equals(other.flags))
			return false;
		if (this.imageUrn == null)
		{
			if (other.imageUrn != null)
				return false;
		} else if (!this.imageUrn.equals(other.imageUrn))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		return getImagingUrn() == null ? "<null imageUrn>" : getImagingUrn().toString() + ","  + getFlags();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.commands.GetImageInformationCommand#getImagingUrn()
	 */
	public AbstractImagingURN getImagingUrn()
	{
		return imageUrn;
	}

	/**
	 * @return the fields
	 */
	public String getFlags()
	{
		return this.flags;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callInTransactionContext()
	 */
	@Override
	public String callSynchronouslyInTransactionContext()
	throws MethodException
	{
		getLogger().info("RouterImpl.getImageDevFields(" + getImagingUrn().toString() + ")");
		URL studyGraphUrl = null;
		Exception lastException = null;
		String siteNumber = getImagingUrn().getOriginatingSiteId();
		TransactionContextFactory.get().setServicedSource(getImagingUrn().toRoutingTokenString());
		try 
		{			
			// 01May2008 CTB
			// Getting the resolvedSite now requires that we use the internal
			// getSite method that passes the method in the SPI to be called
			// and the method parameters.  This is to allow Redirection SPI
			// implementations to inspect the call and redirect it.
			ResolvedArtifactSource resolvedSite;
			//ResolvedSite resolvedSite = getSite(siteNumber);
			try
			{
				Class<? extends VersionableDataSourceSpi> spiClass = ImageDataSourceSpi.class;
				Method spiMethod = spiClass.getDeclaredMethod("getImageDevFields", AbstractImagingURN.class, String.class);
				resolvedSite = getCommandContext().getResolvedArtifactSource(imageUrn, spiClass,  spiMethod, new Object[]{getImagingUrn(), getFlags()} );
			}
			catch(Throwable t)
			{
				getLogger().error("Exception [" + t.getMessage() + "] getting site number with redirect handling, using default site resolution.", t);
				resolvedSite = getCommandContext().getResolvedArtifactSource(imageUrn);
			}
			
			List<URL> metadataUrls = resolvedSite.getMetadataUrls();
			if(metadataUrls == null || metadataUrls.isEmpty())
			{
				throw new ApplicationConfigurationException(
						"The site '" + siteNumber + "' has no available interface URLs.\n" +
						"Please check that the protocol handlers are properly installed and that the \n" +
						"protocol preferences for the site specify valid protocols."
				);
			}

			// try each of the configured protocols in turn
			for(URL artifactUrl : metadataUrls )
			{
				studyGraphUrl = artifactUrl;
				try
				{
					ImageDataSourceSpi imageResolver = getProvider().createImageDataSource(resolvedSite, studyGraphUrl.getProtocol());
					if(imageResolver != null)
					{
						TransactionContextFactory.get().setDatasourceProtocol(studyGraphUrl.getProtocol());
						String result = imageResolver.getImageDevFields(getImagingUrn(), getFlags());
						return result;
					}
				}
				catch(UnsupportedOperationException uoX)
				{
					getLogger().error(
							"Failed to contact site'" + siteNumber + "' using '" + studyGraphUrl.toExternalForm() + "'.\n" +
							"Exception details follow.", 
							uoX);
					lastException = uoX;
				}
				catch(ConnectionException cX)
				{
					getLogger().error(
							"Failed to contact site'" + siteNumber + "' using '" + studyGraphUrl.toExternalForm() + "'.\n" +
							"Exception details follow.", 
							cX);
					lastException = cX;
				}
				catch(MethodException mX)
				{
					getLogger().error(
							"Failed to contact site'" + siteNumber + "' using '" + studyGraphUrl.toExternalForm() + "'.\n" +
							"Exception details follow.", 
							mX);
					lastException = mX;
				} 
			}
		}
		catch (ApplicationConfigurationException acX)
		{
			getLogger().error(acX);
			throw new MethodException(acX);
		}

		getLogger().error(
			"Unsuccessfully tried all configured protocols for site '" + siteNumber + "'.\n" +
			"Please check that the protocol handlers are properly installed and that the \n" +
			"protocol preferences for the site specify valid protocols.");
		// if we had an exception, throw it
		if(lastException != null)
			throw new MethodException(lastException);
				
		return null;
	}

}
