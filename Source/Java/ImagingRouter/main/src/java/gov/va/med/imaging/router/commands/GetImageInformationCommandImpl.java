/**
 * 
 */
package gov.va.med.imaging.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.AbstractImagingURN;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.ImageDataSourceSpi;

/**
 * @author vhaiswbeckec
 *
 */
public class GetImageInformationCommandImpl 
extends AbstractDataSourceCommandImpl<String, ImageDataSourceSpi>
{
	private static final long serialVersionUID = 8187696595182041916L;
	private final AbstractImagingURN imageUrn;
	private final boolean includeDeletedImages;
	
	private static final String SPI_METHOD_NAME = "getImageInformation";
	
	/**
	 * @param commandContext - the context available to the command
	 * @param imageUrn - the universal identifier of the image
	 */
	public GetImageInformationCommandImpl(
			AbstractImagingURN imageUrn)
	{
		this(imageUrn, false);
	}
	
	public GetImageInformationCommandImpl(
			AbstractImagingURN imageUrn, boolean includeDeletedImages)
	{
		super();
		this.imageUrn = imageUrn;
		this.includeDeletedImages = includeDeletedImages;
	}

	@Override
	public RoutingToken getRoutingToken()
	{
		return getImagingUrn();
	}

	public boolean isIncludeDeletedImages()
	{
		return includeDeletedImages;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSiteNumber()
	 */
	@Override
	protected String getSiteNumber() 
	{
		return getImagingUrn().getOriginatingSiteId();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		return getImagingUrn() == null ? "<null imageUrn>" : getImagingUrn().toString();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.commands.GetImageInformationCommand#getImagingUrn()
	 */
	public AbstractImagingURN getImagingUrn()
	{
		return imageUrn;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected String getCommandResult(ImageDataSourceSpi spi)
	throws ConnectionException, MethodException 
	{
		return spi.getImageInformation(getImagingUrn(), isIncludeDeletedImages());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiClass()
	 */
	@Override
	protected Class<ImageDataSourceSpi> getSpiClass() 
	{
		return ImageDataSourceSpi.class;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName() 
	{
		return SPI_METHOD_NAME;
	}
	
	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object[]{getImagingUrn(), isIncludeDeletedImages()};
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?>[]{AbstractImagingURN.class, boolean.class};
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
		final GetImageInformationCommandImpl other = (GetImageInformationCommandImpl) obj;
		if (this.imageUrn == null)
		{
			if (other.imageUrn != null)
				return false;
		} else if (!this.imageUrn.equals(other.imageUrn))
			return false;
		return true;
	}

	
}
