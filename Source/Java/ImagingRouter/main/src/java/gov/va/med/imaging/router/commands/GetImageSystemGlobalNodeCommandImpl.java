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
public class GetImageSystemGlobalNodeCommandImpl 
extends AbstractDataSourceCommandImpl<String, ImageDataSourceSpi>
{
	private static final long serialVersionUID = 8164866633578078453L;
	private final AbstractImagingURN imageUrn;
	
	private static final String SPI_METHOD_NAME = "getImageSystemGlobalNode";
	
	/**
	 * @param commandContext - the context available to the command
	 * @param imageUrn - the universal identifier of the image
	 */
	public GetImageSystemGlobalNodeCommandImpl(AbstractImagingURN imageUrn)
	{
		super();
		this.imageUrn = imageUrn;
	}

	@Override
	public RoutingToken getRoutingToken()
	{
		return imageUrn;
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
		return spi.getImageSystemGlobalNode(getImagingUrn());
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
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSiteNumber()
	 */
	@Override
	protected String getSiteNumber() 
	{
		return getImagingUrn().getOriginatingSiteId();
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
		return new Object[]{getImagingUrn()};
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?>[]{AbstractImagingURN.class};
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
		final GetImageSystemGlobalNodeCommandImpl other = (GetImageSystemGlobalNodeCommandImpl) obj;
		if (this.imageUrn == null)
		{
			if (other.imageUrn != null)
				return false;
		} else if (!this.imageUrn.equals(other.imageUrn))
			return false;
		return true;
	}
}
